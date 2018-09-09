package com.ljmu.andre.snaptools.ModulePack.Utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.common.base.MoreObjects;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.ljmu.andre.snaptools.ModulePack.Notifications.SafeToastAdapter;
import com.ljmu.andre.snaptools.ModulePack.Utils.AesCbcWithIntegrity.CipherTextIvMac;
import com.ljmu.andre.snaptools.ModulePack.Utils.AesCbcWithIntegrity.SecretKeys;
import com.ljmu.andre.snaptools.ModulePack.Utils.Result.BadResult;
import com.ljmu.andre.snaptools.Utils.Callable;
import com.ljmu.andre.snaptools.Utils.CustomObservers.ErrorObserver;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;
import com.ljmu.andre.snaptools.Utils.FileUtils;
import com.ljmu.andre.snaptools.Utils.ShellUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getCreateDir;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.TEMP_PATH;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class AccountManagerUtils {
    public static final String ACCOUNT_MANAGER_FILENAME = "AccountManager";
    private static final String ACC_FOLDER_HEADER = "Acc_";
    private static final String ACC_FILE_NAME = "acc";
    private static final String DB_FILE_NAME = "sc_db";
    private static final String TEMP_DECRYPTED_ACC_FILENAME = "temp";

    private static final byte[] SALT = "hF7NEGtkKu".getBytes();

    private static String getAccountFilename(String identifier) {
        return ACC_FOLDER_HEADER + identifier;
    }

    public static void safeLogout(Activity activity, Callable<Result<Boolean, String>> resultCallable) {
        // ===========================================================================
        Observable<Boolean> deletePrefsObservable = ShellUtils.sendCommand(
                "rm -r \"" + getSnapchatPrefsPath(activity) + "\""
        );
        // ===========================================================================
        Observable<Boolean> deleteDBObservable = ShellUtils.sendCommand(
                "rm -r \"" + getSnapchatTcspahnPath(activity) + "\""
        );
        // ===========================================================================

        StringBuilder resultString = new StringBuilder();

        Observable.concat(
                transformShellWResult(deletePrefsObservable, "Deleted Preferences: "),
                transformShellWResult(deleteDBObservable, "Deleted SC Database: "))
                .map(result -> resultString.append(result).append("\n"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorObserver<StringBuilder>() {
                    @Override
                    public void onComplete() {
                        super.onComplete();
                        Timber.d("Completed");
                        resultCallable.call(new Result<>(true, resultString.toString()));
                    }
                });
    }

    public static String getSnapchatPrefsPath(Activity activity) {
        return getSnapchatDataDir(activity) + "/shared_prefs/com.snapchat.android_preferences.xml";
    }

    private static String getSnapchatTcspahnPath(Activity activity) {
        return getSnapchatDataDir(activity) + "/databases/tcspahn.db";
    }

    private static Observable<String> transformShellWResult(Observable<Boolean> observable, String tag) {
        return observable.map(result -> tag + (result ? "Yes" : "No"));
    }

    private static String getSnapchatDataDir(Activity activity) {
        PackageManager m = activity.getPackageManager();
        String packageName = "com.snapchat.android";
        String dataDir;

        try {
            PackageInfo p = m.getPackageInfo(packageName, 0);
            dataDir = p.applicationInfo.dataDir;
        } catch (NameNotFoundException e) {
            Timber.e(e);
            dataDir = "data/data/com.snapchat.android";
        }

        return dataDir;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void buildAccountAndUpdateManager(File accountsDir, File uniqueAccountDir, String identifier,
                                                     String password, boolean shouldEncrypt,
                                                     Callable<Result<Boolean, ?>> resultCallable) {
        Observable.fromCallable(() -> {
            Result<Boolean, Object> buildAccountDataResult = buildAccountData(uniqueAccountDir, identifier, password, shouldEncrypt);

            if (!buildAccountDataResult.getKey()) {
                FileUtils.deleteRecursive(uniqueAccountDir);
                return buildAccountDataResult;
            }

            if (buildAccountDataResult.getValue() == null) {
                FileUtils.deleteRecursive(uniqueAccountDir);
                return new Result<>(false, (Object) "Account Model not built correctly");
            }

            Result<Boolean, ?> updateManagerResult = updateAccountManagerFile(
                    accountsDir,
                    (SnapchatAccountModel) buildAccountDataResult.getValue()
            );

            if (!updateManagerResult.getKey()) {
                FileUtils.deleteRecursive(uniqueAccountDir);
                return updateManagerResult;
            }

            return buildAccountDataResult;
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Result<Boolean, ?>>("Fatal error building account model") {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        FileUtils.deleteRecursive(uniqueAccountDir);
                        resultCallable.call(new Result<>(false, "Fatal error building account model"));
                    }

                    @Override
                    public void onNext(Result<Boolean, ?> result) {
                        Timber.d("Build account result: " + result);
                        resultCallable.call(result);
                    }
                });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static Result<Boolean, Object> buildAccountData(
            File uniqueAccountDir, String identifier, String password, boolean shouldEncrypt) {
        SecretKeys keys;

        try {
            keys = AesCbcWithIntegrity.generateKeyFromPassword(password, SALT);
        } catch (GeneralSecurityException e) {
            Timber.e(e);
            return new Result<>(false, "Couldn't create encryption keys");
        }

        String encryptedIdentifier = null;

        if (shouldEncrypt) {
            try {
                CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(identifier, keys);
                encryptedIdentifier = cipherTextIvMac.toString();
            } catch (UnsupportedEncodingException | GeneralSecurityException e) {
                Timber.e(e);
                return new Result<>(false, "Failed to encrypt identifier");
            }

            Timber.d("Encrypting Account File");
            File accountFile = new File(uniqueAccountDir, ACC_FILE_NAME);
            Result<Boolean, Object> encryptAccountResult = encryptFile("account", keys, accountFile, accountFile);
            Timber.d("Acc Encryption Result: " + encryptAccountResult.getValue());

            if (!encryptAccountResult.getKey()) {
                FileUtils.deleteRecursive(uniqueAccountDir);
                return encryptAccountResult;
            }

            Timber.d("Account Encrypted");
            File dBFile = new File(uniqueAccountDir, DB_FILE_NAME);

            if (dBFile.exists()) {
                Timber.d("Encrypting DB File");
                Result<Boolean, Object> encryptDBResult = encryptFile("ScDB", keys, dBFile, dBFile);
                Timber.d("DB Encryption Result: " + encryptDBResult.getValue());

                if (!encryptDBResult.getKey()) {
                    FileUtils.deleteRecursive(uniqueAccountDir);
                    return encryptDBResult;
                }

                Timber.d("DB Encrypted");
            }
        }

        SnapchatAccountModel accountModel = new SnapchatAccountModel()
                .setFolderName(uniqueAccountDir.getName())
                .setIdentifier(encryptedIdentifier != null ? encryptedIdentifier : identifier)
                .setDecryptedIdentifier(identifier)
                .setEncrypted(shouldEncrypt);

        Timber.d("AccountModel: " + accountModel);

        return new Result<>(true, accountModel);
    }

    private static Result<Boolean, String> updateAccountManagerFile(File accountsDir, SnapchatAccountModel accountModel) {
        Timber.d("Updating account manager file");
        File accountManagerFile = new File(accountsDir, ACCOUNT_MANAGER_FILENAME);

        List<SnapchatAccountModel> accountModelList;

        try {
            accountModelList = parseAccountModelFile(accountManagerFile);
            Timber.d("Account manager already contained %s accounts", accountModelList.size());
        } catch (FileNotFoundException ignored) {
            Timber.d("Account manager file not found... Creating new");
            try {
                //noinspection ResultOfMethodCallIgnored
                accountManagerFile.createNewFile();
            } catch (IOException e) {
                Timber.w(e);
            }

            accountModelList = new ArrayList<>(1);
        }

        accountModelList.add(accountModel);

        return updateAccountManagerFile(accountManagerFile, accountModelList);
    }

    private static Result<Boolean, Object> encryptFile(String fileTag, SecretKeys keys, File source, File target) {
        Timber.d("Starting encryption for: " + source);

        Closer closer = Closer.create();

        try {
            FileInputStream sourceFileInputStream = new FileInputStream(source);
            byte[] sourceByteArray = ByteStreams.toByteArray(sourceFileInputStream);

            try {
                sourceFileInputStream.close();
            } catch (IOException ignored) {
            }

            Timber.d("Read %s bytes from " + fileTag + " file", sourceByteArray.length);

            CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(sourceByteArray, keys);
            FileWriter writer = closer.register(new FileWriter(target));
            writer.write(cipherTextIvMac.toString());
            writer.flush();

            return new Result<>(true, "Successfully encrypted file");
        } catch (FileNotFoundException e) {
            Timber.e(e);
            return new Result<>(false, "Source " + fileTag + " file not found");
        } catch (IOException e) {
            Timber.e(e);
            return new Result<>(false, "Failed to read/write " + fileTag + " file");
        } catch (GeneralSecurityException e) {
            Timber.e(e);
            return new Result<>(false, "Failed to encrypt " + fileTag + " file");
        } finally {
            try {
                closer.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static File getUniqueAccountDir(File accountsDir) {
        String filename = getAccountFilename(Integer.toHexString((int) (Math.random() * 9999999))) + "/";

        File accFile = new File(accountsDir, filename);

        if (accFile.exists())
            return getUniqueAccountDir(accountsDir);

        accFile.mkdirs();
        return accFile;
    }

    public static File getUniqueAccountFile(File accountDir) {
        String filename = getAccountFilename(String.valueOf((int) (Math.random() * 9999999)));

        File accFile = new File(accountDir, filename);

        if (accFile.exists())
            return getUniqueAccountFile(accountDir);

        return accFile;
    }

    public static File[] getAccountFiles(File accountsDir) {
        return accountsDir.listFiles((dir, name) -> name.startsWith(ACC_FOLDER_HEADER));
    }

    public static void generateTestData(File accountsDir) throws GeneralSecurityException, IOException {
        File accountManagerFile = new File(accountsDir, ACCOUNT_MANAGER_FILENAME);

        if (accountManagerFile.exists())
            return;

        List<SnapchatAccountModel> accountModelList = new ArrayList<>();

        SecretKeys keys = AesCbcWithIntegrity.generateKeyFromPassword("test", SALT);
        for (int i = 0; i < 10; i++) {
            SnapchatAccountModel accountModel = new SnapchatAccountModel();
            accountModel.setFolderName("Folder");
            accountModel.setEncrypted(true);
            String decryptedUsername = "User" + Math.random();

            if (i == 3) {
                accountModel.setEncrypted(false);
                accountModel.setIdentifier(decryptedUsername);
            } else
                accountModel.setIdentifier(AesCbcWithIntegrity.encrypt(decryptedUsername, keys).toString());

            accountModelList.add(accountModel);
        }

        FileWriter writer = new FileWriter(accountManagerFile);
        new Gson().toJson(accountModelList, writer);
        writer.flush();
        writer.close();
    }

    public static Observable<List<SnapchatAccountModel>> loadSnapchatAccountModels(File accountsDir, String password) {
        return Observable.fromCallable(() -> {
            SecretKeys keys = AesCbcWithIntegrity.generateKeyFromPassword(password, SALT);
            File accountManagerFile = new File(accountsDir, ACCOUNT_MANAGER_FILENAME);

            List<SnapchatAccountModel> accountModelList;

            try {
                accountModelList = parseAccountModelFile(accountManagerFile);
            } catch (FileNotFoundException e) {
                try {
                    accountManagerFile.createNewFile();
                } catch (IOException ignored) {
                }

                accountModelList = new ArrayList<>();
            }

            for (SnapchatAccountModel accountModel : accountModelList) {
                if (!accountModel.isEncrypted()) {

                    accountModel.setDecryptedIdentifier(accountModel.getIdentifier());

                } else {

                    // ===========================================================================
                    try {
                        CipherTextIvMac cipherTextIvMac = new CipherTextIvMac(accountModel.getIdentifier());
                        String decryptedUsername = AesCbcWithIntegrity.decryptString(cipherTextIvMac, keys);

                        accountModel.setDecryptedIdentifier(decryptedUsername);
                    } catch (GeneralSecurityException ignored) {
                        accountModel.setHasFailed(true);
                    } catch (Throwable t) {
                        Timber.e(t);
                        accountModel.setHasFailed(true);
                    }
                    // ===========================================================================

                }

                File uniqueAccountDir = new File(accountsDir, accountModel.getFolderName());
                File accountFile = new File(uniqueAccountDir, ACC_FILE_NAME);

                if (!accountFile.exists()) {
                    Timber.w("Account %s file doesn't exist", accountModel.getFolderName());
                    accountModel.setMissingFile();
                }
            }

            return accountModelList;
        });
    }

    private static List<SnapchatAccountModel> parseAccountModelFile(File accountManagerFile) throws FileNotFoundException {
        Type listType = new TypeToken<ArrayList<SnapchatAccountModel>>() {
        }.getType();

        Closer closer = Closer.create();

        try {
            FileReader fileReader = closer.register(new FileReader(accountManagerFile));
            List<SnapchatAccountModel> accountModelList = new Gson().fromJson(fileReader, listType);

            if (accountModelList == null)
                return new ArrayList<>();

            return accountModelList;
        } catch (JsonIOException | JsonSyntaxException e) {
            Timber.w(e);
            return new ArrayList<>();
        } finally {
            try {
                closer.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void backupCurrentAccount(Activity activity, File accountsDir, String identifier, String password,
                                            boolean shouldEncrypt,
                                            Callable<Result<Boolean, ?>> resultCallable) {
        File tempDir = getCreateDir(TEMP_PATH);

        if (tempDir == null) {
            resultCallable.call(new Result<>(false, "Couldn't create Temp Directory"));
            return;
        }

        String scPrefsPath = getSnapchatPrefsPath(activity);
        String scDBPath = getSnapchatTcspahnPath(activity);

        File uniqueAccountDir = getUniqueAccountDir(accountsDir);
        File backupAccountFile = new File(uniqueAccountDir, ACC_FILE_NAME);
        File backupDBFile = new File(uniqueAccountDir, DB_FILE_NAME);

        Observable<Boolean> backupPrefsObservable = ShellUtils.sendCommand("cp " + scPrefsPath + " " + backupAccountFile.getAbsolutePath());
        Observable<Boolean> backupDBObservable = ShellUtils.sendCommand("cp " + scDBPath + " " + backupDBFile.getAbsolutePath());

		/*List<Observable<Result<Boolean, String>>> observables = new ArrayList<>();
		observables.add(transformShellWResult(backupPrefsObservable, "Copied Preferences: "));
		observables.add(transformShellWResult(backupDBObservable, "Copied SC Database: "));*/

        StringBuilder resultString = new StringBuilder();

        Observable.concat(
                transformShellWResult(backupPrefsObservable, "Copied Preferences: "),
                transformShellWResult(backupDBObservable, "Copied SC Database: "))
                .map(result -> resultString.append(result).append("\n"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorObserver<StringBuilder>() {
                    @Override
                    public void onComplete() {
                        super.onComplete();
                        Timber.d("Completed");
                        SafeToastAdapter.showDefaultToast(
                                activity,
                                resultString.toString()
                        );

                        buildAccountAndUpdateManager(
                                accountsDir,
                                uniqueAccountDir,
                                identifier,
                                password,
                                shouldEncrypt,
                                resultCallable
                        );
                    }
                });
    }

    public static void restoreAccount(Activity activity, File accountsDir, String password, SnapchatAccountModel accountModel,
                                      Callable<Result<Boolean, String>> resultCallable) {
        SecretKeys keys;

        try {
            keys = AesCbcWithIntegrity.generateKeyFromPassword(password, SALT);
        } catch (GeneralSecurityException e) {
            Timber.e(e);
            resultCallable.call(new BadResult("Couldn't create encryption keys"));
            return;
        }

        File uniqueAccountDir = new File(accountsDir, accountModel.getFolderName());

        if (!uniqueAccountDir.exists()) {
            resultCallable.call(new BadResult("Account Folder not found!"));
            return;
        }

        File accountFile = new File(uniqueAccountDir, ACC_FILE_NAME);
        File dbFile = new File(uniqueAccountDir, DB_FILE_NAME);

        if (!accountModel.isEncrypted()) {
            Timber.d("Account not encrypted");

            exportDecryptedAccounts(
                    activity,
                    accountModel.isEncrypted,
                    accountFile, dbFile,
                    resultCallable
            );

            return;
        }

        Timber.d("Performing Account Model Decryption");

        File decryptedAccountFile = new File(uniqueAccountDir, "temp_" + ACC_FILE_NAME);
        File decryptedDBFile = new File(uniqueAccountDir, "temp_" + DB_FILE_NAME);

        // ===========================================================================
        Observable<Result<Boolean, String>> decryptAccountObservable = Observable.fromCallable(
                () -> decryptFile("account", keys, accountFile, decryptedAccountFile)
        );
        // ===========================================================================
        Observable<Result<Boolean, String>> decryptDbObservable = Observable.fromCallable(
                () -> dbFile.exists() ? decryptFile("ScDB", keys, dbFile, decryptedDBFile)
                        : new Result<>(true, "ScDB is non existent... Skipping")
        );
        // ===========================================================================

        Observable.concat(
                decryptAccountObservable,
                decryptDbObservable
        ).map((Function<Result<Boolean, String>, Object>) booleanStringResult -> {
            Timber.d("Checking decryption state: " + booleanStringResult.toString());

            if (!booleanStringResult.getKey())
                throw new AccountRestoreException(booleanStringResult.getValue());

            return booleanStringResult;
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorObserver<Object>() {
                    @SuppressWarnings("ResultOfMethodCallIgnored")
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        decryptedAccountFile.delete();
                        decryptedDBFile.delete();

                        String message = "Fatal exception restoring account";

                        if (e instanceof AccountRestoreException)
                            message = e.getMessage();

                        resultCallable.call(new BadResult(message));
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("Completed Decryption");

                        exportDecryptedAccounts(
                                activity,
                                accountModel.isEncrypted(),
                                decryptedAccountFile,
                                decryptedDBFile,
                                resultCallable
                        );
                    }
                });
    }

    private static void exportDecryptedAccounts(Activity activity, boolean wasEncrypted,
                                                File decryptedAccountFile, File decryptedDBFile,
                                                Callable<Result<Boolean, String>> resultCallable) {
        Timber.d("Performing account exporting");

        // ===========================================================================
        Observable<Boolean> restorePrefsObservable =
                ShellUtils.sendCommand("cp " + decryptedAccountFile.getAbsolutePath() + " " + getSnapchatPrefsPath(activity))
                        .map(aBoolean -> {
                            if (!aBoolean)
                                throw new AccountRestoreException("Couldn't export Preference file... Aborting restore");

                            return true;
                        });
        // ===========================================================================
        Observable<Boolean> restoreDBObservable =
                decryptedDBFile.exists() ?
                        ShellUtils.sendCommand("cp " + decryptedDBFile.getAbsolutePath() + " " + getSnapchatTcspahnPath(activity))
                        : ShellUtils.sendCommand("rm -r \"" + getSnapchatTcspahnPath(activity) + "\"");
        // ===========================================================================
        Observable<Boolean> chmodDBObservable =
                decryptedDBFile.exists() ?
                        ShellUtils.sendCommand("chmod 777 " + decryptedDBFile.getAbsolutePath())
                        : Observable.just(false);
        // ===========================================================================

        StringBuilder resultString = new StringBuilder();

        Observable.concat(
                transformShellWResult(restorePrefsObservable, "Copied Preferences: "),
                transformShellWResult(restoreDBObservable, "Copied SC Database: "),
                transformShellWResult(chmodDBObservable, "Chmod SC Database: "))
                .map(result -> resultString.append(result).append("\n"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorObserver<StringBuilder>() {
                    @SuppressWarnings("ResultOfMethodCallIgnored")
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                        if (wasEncrypted) {
                            decryptedAccountFile.delete();
                            decryptedDBFile.delete();
                        }

                        String message = "Fatal exception restoring account";

                        if (e instanceof AccountRestoreException)
                            message = e.getMessage();

                        resultCallable.call(new BadResult(message));
                    }

                    @SuppressWarnings("ResultOfMethodCallIgnored")
                    @Override
                    public void onComplete() {
                        super.onComplete();
                        Timber.d("Completed");

                        if (wasEncrypted) {
                            decryptedAccountFile.delete();
                            decryptedDBFile.delete();
                        }

                        killSnapchat(killedSnapchat -> resultCallable.call(new Result<>(
                                true,
                                resultString.toString()
                        )));
                    }
                });
    }

    private static Result<Boolean, String> decryptFile(String fileTag, SecretKeys keys, File source, File target) {
        Timber.d("Starting decryption for: " + source);

        Closer closer = Closer.create();

        try {
            FileInputStream sourceFileInputStream = new FileInputStream(source);
            byte[] sourceByteArray = ByteStreams.toByteArray(sourceFileInputStream);

            try {
                sourceFileInputStream.close();
            } catch (IOException ignored) {
            }

            Timber.d("Read %s encrypted bytes from " + fileTag + " file", sourceByteArray.length);

            CipherTextIvMac cipherTextIvMac = new CipherTextIvMac(new String(sourceByteArray, "UTF-8"));
            byte[] decryptedByteArray = AesCbcWithIntegrity.decrypt(cipherTextIvMac, keys);

            BufferedOutputStream bos = closer.register(new BufferedOutputStream(new FileOutputStream(target)));
            bos.write(decryptedByteArray);
            bos.flush();

            return new Result<>(true, "Successfully decrypted file");
        } catch (FileNotFoundException e) {
            Timber.e(e);
            return new Result<>(false, "Source " + fileTag + " file not found");
        } catch (IOException e) {
            Timber.e(e);
            return new Result<>(false, "Failed to read/write " + fileTag + " file");
        } catch (GeneralSecurityException e) {
            Timber.e(e);
            return new Result<>(false, "Failed to decrypt " + fileTag + " file");
        } finally {
            try {
                closer.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void killSnapchat(Callable<Boolean> resultCallable) {
        ShellUtils.sendCommand(
                "am force-stop com.snapchat.android\n"
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        resultCallable.call(aBoolean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        resultCallable.call(false);
                    }
                });
    }

    public static Result<Boolean, String> deleteAccount(File accountsDir, SnapchatAccountModel accountModel) {
        File accountManagerFile = new File(accountsDir, ACCOUNT_MANAGER_FILENAME);


        List<SnapchatAccountModel> accountModelList;

        try {
            accountModelList = parseAccountModelFile(accountManagerFile);
            Timber.d("Account manager already contained %s accounts", accountModelList.size());
        } catch (FileNotFoundException ignored) {
            Timber.d("Account manager file not found... Creating new");
            try {
                //noinspection ResultOfMethodCallIgnored
                accountManagerFile.createNewFile();
            } catch (IOException e) {
                Timber.w(e);
            }

            accountModelList = new ArrayList<>(1);
        }

        Iterator<SnapchatAccountModel> accountModelIterator = accountModelList.iterator();
        boolean hasRemovedAccount = false;

        while (accountModelIterator.hasNext()) {
            SnapchatAccountModel checkingAccountModel = accountModelIterator.next();

            if (checkingAccountModel.getIdentifier().equals(accountModel.getIdentifier())) {
                accountModelIterator.remove();
                hasRemovedAccount = true;
                break;
            }
        }

        if (!hasRemovedAccount) {
            return new BadResult("Failed to remove Snapchat Account");
        }

        Result<Boolean, String> updateAccountManagerResult = updateAccountManagerFile(
                accountManagerFile,
                accountModelList
        );

        if (updateAccountManagerResult.getKey()) {
            File uniqueAccountDir = new File(accountsDir, accountModel.getFolderName());
            FileUtils.deleteRecursive(uniqueAccountDir);
        }

        return new Result<>(true, "Successfully deleted Account backup");
    }

    private static Result<Boolean, String> updateAccountManagerFile(File accountManagerFile, List<SnapchatAccountModel> accountModelList) {
        Closer closer = Closer.create();

        try {
            FileWriter writer = closer.register(new FileWriter(accountManagerFile));
            new Gson().toJson(accountModelList, writer);
            writer.flush();

            Timber.d("Updated account manager");
            return new Result<>(true, "Successfully updated account manager file");
        } catch (IOException e) {
            Timber.e(e);
            return new Result<>(false, "Account Manager file couldn't be created or written to");
        } finally {
            try {
                closer.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static class SnapchatAccountModel implements Serializable {
        private static final long serialVersionUID = -9212042169531888865L;
        @SerializedName("identifier")
        private String identifier;
        @SerializedName("folder_name")
        private String folderName;
        @SerializedName("encrypted")
        private boolean isEncrypted;

        private transient String decryptedIdentifier;
        private transient boolean hasFailed;
        private transient boolean missingFile;

        public String getIdentifier() {
            return identifier;
        }

        public SnapchatAccountModel setIdentifier(String identifier) {
            this.identifier = identifier;

            if (!isEncrypted)
                setDecryptedIdentifier(identifier);

            return this;
        }

        String getFolderName() {
            return folderName;
        }

        SnapchatAccountModel setFolderName(String folderName) {
            this.folderName = folderName;
            return this;
        }

        public boolean isEncrypted() {
            return isEncrypted;
        }

        SnapchatAccountModel setEncrypted(boolean encrypted) {
            isEncrypted = encrypted;

            if (identifier != null && !isEncrypted)
                setDecryptedIdentifier(identifier);

            return this;
        }

        public String getDecryptedIdentifier() {
            return decryptedIdentifier;
        }

        SnapchatAccountModel setDecryptedIdentifier(String decryptedIdentifier) {
            this.decryptedIdentifier = decryptedIdentifier;
            return this;
        }

        public boolean isMissingFile() {
            return missingFile;
        }

        void setMissingFile() {
            this.missingFile = true;
        }

        public boolean hasFailed() {
            return hasFailed;
        }

        public SnapchatAccountModel setHasFailed(boolean hasFailed) {
            this.hasFailed = hasFailed;
            return this;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .omitNullValues()
                    .add("identifier", identifier)
                    .add("folderName", folderName)
                    .add("isEncrypted", isEncrypted)
                    .add("decryptedIdentifier", decryptedIdentifier)
                    .add("hasFailed", hasFailed)
                    .toString();
        }
    }

    public static class AccountRestoreException extends Exception {

        private static final long serialVersionUID = -1522429061684058728L;

        AccountRestoreException(String message) {
            super(message);
        }
    }
}
