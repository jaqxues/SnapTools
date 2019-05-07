package com.ljmu.andre.snaptools.ModulePack.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.ljmu.andre.snaptools.Dialogs.Content.TextInputBasic;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.AccountManagerViewProvider;
import com.ljmu.andre.snaptools.ModulePack.Fragments.Tutorials.AccountManagerTutorial;
import com.ljmu.andre.snaptools.ModulePack.Notifications.SafeToastAdapter;
import com.ljmu.andre.snaptools.ModulePack.Utils.AccountManagerUtils;
import com.ljmu.andre.snaptools.ModulePack.Utils.AccountManagerUtils.SnapchatAccountModel;
import com.ljmu.andre.snaptools.ModulePack.Utils.Result;
import com.ljmu.andre.snaptools.ModulePack.Utils.Result.BadResult;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.Callable;
import com.ljmu.andre.snaptools.Utils.Constants;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;
import com.ljmu.andre.snaptools.Utils.FileUtils;
import com.ljmu.andre.snaptools.Utils.MiscUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getCreateDir;
import static com.ljmu.andre.snaptools.ModulePack.Utils.AccountManagerUtils.loadSnapchatAccountModels;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.ACCOUNTS_PATH;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDSLView;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;
import static com.ljmu.andre.snaptools.Utils.StringUtils.htmlHighlight;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class AccountManagerFragment extends FragmentHelper {
    private static final int TIMEOUT_MINUTES = 5;

    @Nullable
    private static String masterPassword;
    private static long inactiveTimestamp = -1;

    private final List<SnapchatAccountModel> accountModelList = new ArrayList<>();

    private AccountManagerViewProvider viewProvider;
    private File accountsDir;
    // ===========================================================================
    private ViewGroup mainContainer;
    private ViewGroup accountsContainer;
    private ViewGroup settingsContainer;
    private ViewGroup errorContainer;
    private ViewPager pager;
    private ListView accountsListView;
    // ===========================================================================
    private BaseAdapter accountListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewProvider = new AccountManagerViewProvider(
                getActivity(),
                this::handleEvent,
                this::handleAccountEvent,
                accountModelList,
                viewGroup -> accountsContainer = viewGroup,
                viewGroup -> settingsContainer = viewGroup
        );

        mainContainer = viewProvider.getMainContainer();
        pager = getDSLView(mainContainer, "view_pager");

        errorContainer = getDSLView(accountsContainer, "error_container");
        accountsListView = getDSLView(accountsContainer, "accounts_list_view");
        accountListAdapter = (BaseAdapter) accountsListView.getAdapter();

        tutorialDetails = AccountManagerTutorial.getTutorials();

        accountsDir = getCreateDir(ACCOUNTS_PATH);

        if (accountsDir == null) {
            Timber.e("Couldn't find or create Accounts Directory");
            DialogFactory.createErrorDialog(
                    getActivity(),
                    "Directory Creation Failure",
                    "Couldn't find or create the Accounts Directory"
            ).show();
        } else {
            FileUtils.createReadme(
                    accountsDir,
                    "AccountManager",
                    "This folder is home to all of the Snapchat Accounts you've backed up"
                            + "\n[IMPORTANT] Do not alter ANY file names or their content as you will cause irreparable damage to their contents which could potentially affect your Snapchat"
            );
        }

        //displayCredentialsDialog(this::generateAccountData);

        return mainContainer;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (masterPassword != null) {
            long timeDifference = MiscUtils.calcTimeDiff(inactiveTimestamp);

            if (inactiveTimestamp > 0 &&
                    timeDifference > TimeUnit.MINUTES.toMillis(TIMEOUT_MINUTES)) {
                inactiveTimestamp = -1;
                masterPassword = null;

                DialogFactory.createErrorDialog(
                        getActivity(),
                        "Account Manager Timeout",
                        "The account manager has been inactive for " + TimeUnit.MILLISECONDS.toMinutes(timeDifference) + " minutes"
                                + "\n\nAs a result, the account manager has been locked and requires unlocking"
                ).show();
            } else {
                inactiveTimestamp = -1;
                generateAccountData(masterPassword, true);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        inactiveTimestamp = System.currentTimeMillis();
    }

    private void generateAccountData(String password, boolean bypassConfirmPassword) {
        if (password == null || password.isEmpty()) {
            displayPasswordError();
            return;
        }

        ThemedDialog progressDialog = DialogFactory.createProgressDialog(
                getActivity(),
                "Decrypting Account Information",
                "Account information decryption in progress... This should only take a moment",
                false
        );

        progressDialog.show();

        Assert.notNull("Null AccountsDir", accountsDir);

        loadSnapchatAccountModels(accountsDir, password)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<List<SnapchatAccountModel>>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        progressDialog.dismiss();
                        displayGeneralError(
                                "Account Loading Error",
                                "A fatal error occurred while trying to load accounts"
                        );
                    }

                    @Override
                    public void onNext(List<SnapchatAccountModel> snapchatAccountModels) {
                        progressDialog.dismiss();

                        /**
                         * ===========================================================================
                         * When no accounts found check if credentials are acceptable
                         * ===========================================================================
                         */
                        if (snapchatAccountModels.isEmpty() && !bypassConfirmPassword) {
                            DialogFactory.createBasicTextInputDialog(
                                    getActivity(),
                                    "Confirm Password",
                                    "Please confirm the password you would like to use for the Account Manager."
                                            + "\nIf you forget or lose this password your backups will not be able to be recovered!",
                                    "Manager Password",
                                    null,
                                    null,
                                    new ThemedClickListener() {
                                        @Override
                                        public void clicked(ThemedDialog themedDialog) {
                                            themedDialog.dismiss();
                                            String passwordConfirmation = themedDialog.<TextInputBasic>getExtension().getInputMessage();

                                            if (!passwordConfirmation.equals(password))
                                                displayPasswordError();
                                            else
                                                setAccountModelList(snapchatAccountModels, password);
                                        }
                                    }
                            ).show();

                            return;
                        }
                        // ===========================================================================

                        int failedDecryptions = 0;

                        Timber.d("Models: " + snapchatAccountModels);

                        List<SnapchatAccountModel> validAccountModelList = new ArrayList<>();

                        for (SnapchatAccountModel accountModel : snapchatAccountModels) {
                            if (accountModel.hasFailed())
                                failedDecryptions++;
                            else
                                validAccountModelList.add(accountModel);
                        }

                        if (failedDecryptions > 0 && failedDecryptions == snapchatAccountModels.size()) {
                            displayPasswordError();
                            return;
                        } else if (failedDecryptions > 0) {
                            SafeToastAdapter.showErrorToast(
                                    getActivity(),
                                    "Failed to decrypt " + failedDecryptions + " accounts"
                            );
                        }

                        setAccountModelList(validAccountModelList, password);
                    }
                });
    }

    private void displayPasswordError() {
        accountsListView.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
        viewProvider.errorViewPasswordMode(errorContainer);

        animateMainContainer();
    }

    private void displayGeneralError(String header, String message) {
        accountsListView.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
        viewProvider.errorViewGeneralMode(errorContainer, header, message);

        animateMainContainer();
    }

    private void setAccountModelList(List<SnapchatAccountModel> accountModelList, String password) {
        masterPassword = password;

        this.accountModelList.clear();
        this.accountModelList.addAll(accountModelList);
        accountListAdapter.notifyDataSetChanged();

        accountsListView.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);

        accountsListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //At this point the layout is complete and the
                //dimensions of recyclerView and any child views are known.

                if (Constants.getApkVersionCode() >= 66)
                    AnimationUtils.sequentGroup(accountsListView);

                accountsListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void animateMainContainer() {
        if (Constants.getApkVersionCode() >= 66)
            AnimationUtils.sequentGroup(mainContainer);
    }

    private void handleEvent(AccountManagerEvent event) {
        Timber.d("AccountManagerEvent: " + event);

        switch (event) {
            case RETRY_CREDENTIALS:
                displayCredentialsDialog(this::generateAccountData);
                break;
            case BACKUP_ACCOUNT:
                if (masterPassword == null) {
                    SafeToastAdapter.showErrorToast(
                            getActivity(),
                            "Account Unlocking required before backing up an account"
                    );

                    return;
                }

                backupSnapchatAccount();
                break;
            default:
                Timber.e("Unknown AccountManagerEvent: " + event);
        }
    }

    private void displayCredentialsDialog(Callable<String> resultCallable) {
        displayCredentialsDialog(
                "Account Manager password required to decrypt account information."
                        + "\nIf you've never backed up an account before, please provide a new password to use.",
                resultCallable
        );
    }

    private void backupSnapchatAccount() {
        DialogFactory.createBasicTextInputDialog(
                getActivity(),
                "Identifier Required",
                "Please enter an identifier for this account"
                        + "\n<i>Identifiers are encrypted so usernames are hidden until decrypted</i>",
                "Identifier",
                null,
                null,
                new ThemedClickListener() {
                    @Override
                    public void clicked(ThemedDialog themedDialog) {
                        String identifier = themedDialog.<TextInputBasic>getExtension().getInputMessage().trim();

                        Result<Boolean, String> isIdentifierValidResult = isIdentifierValid(identifier);

                        if (!isIdentifierValidResult.getKey()) {
                            SafeToastAdapter.showErrorToast(
                                    getActivity(),
                                    isIdentifierValidResult.getValue() + "... Please try again"
                            );

                            return;
                        }

                        themedDialog.dismiss();
                        AccountManagerUtils.backupCurrentAccount(
                                getActivity(),
                                accountsDir,
                                identifier,
                                masterPassword,
                                true,
                                result -> {
                                    Timber.d("Result: " + result.toString());

                                    if (!result.getKey()) {
                                        SafeToastAdapter.showErrorToast(
                                                getActivity(),
                                                (String) result.getValue()
                                        );
                                        return;
                                    }

                                    SafeToastAdapter.showDefaultToast(
                                            getActivity(),
                                            "Successfully backed up Snapchat Account"
                                    );

                                    SnapchatAccountModel accountModel = (SnapchatAccountModel) result.getValue();
                                    accountModelList.add(accountModel);
                                    accountListAdapter.notifyDataSetChanged();
                                }
                        );
                    }
                }
        ).show();
    }

    private void displayCredentialsDialog(String message, Callable<String> resultCallable) {
        boolean oldApk = Constants.getApkVersionCode() < 66;
        int drawableId = oldApk ?
                getDrawable(getActivity(), "error_header") :
                getDrawable(getActivity(), "lock_header");

        new ThemedDialog(getActivity())
                .setTitle("Credentials Required")
                .setHeaderDrawable(drawableId)
                .setExtension(
                        new TextInputBasic()
                                .setHint("Manager Password")
                                .setMessage(message)
                                .setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                .setOkayClickListener(
                                        new ThemedClickListener() {
                                            @Override
                                            public void clicked(ThemedDialog themedDialog) {
                                                themedDialog.dismiss();
                                                String password = themedDialog.<TextInputBasic>getExtension().getInputMessage().trim();
                                                resultCallable.call(password);
                                            }
                                        }
                                )
                ).show();
    }

    private Result<Boolean, String> isIdentifierValid(String identifier) {
        if (identifier.isEmpty())
            return new BadResult("Entered empty identifier");

        for (SnapchatAccountModel accountModel : accountModelList) {
            if (accountModel.getDecryptedIdentifier().equals(identifier))
                return new BadResult("Identifier already used");
        }

        return new Result<>(true, null);
    }

    private void handleAccountEvent(Result<AccountEvent, Integer> accountEventResult) {
        Timber.d("AccountEvent: " + accountEventResult);

        int position = accountEventResult.getValue();

        if (position >= accountModelList.size()) {
            SafeToastAdapter.showErrorToast(
                    getActivity(),
                    "Clicked account outside account list bounds"
            );

            Timber.e(new Throwable("Clicked account outside account list bounds"));
            return;
        }

        SnapchatAccountModel accountModel = accountModelList.get(position);

        switch (accountEventResult.getKey()) {
            case DELETE:
                showAccountDeletionPrompt(accountModel);
                break;
            case RESTORE:
                showAccountRestorePrompt(accountModel);
                break;

        }
    }

    private void showAccountDeletionPrompt(SnapchatAccountModel accountModel) {
        DialogFactory.createConfirmation(
                getActivity(),
                "Delete Snapchat Account Backup?",
                "Are you sure you want to delete the backup for the account " + htmlHighlight(accountModel.getDecryptedIdentifier()) + "?"
                        + "\nThis is an " + htmlHighlight("IRREVERSIBLE") + " action!",
                new ThemedClickListener() {
                    @Override
                    public void clicked(ThemedDialog themedDialog) {
                        themedDialog.dismiss();
                        displayCredentialsDialog("Confirm Account Manager Password",
                                s -> {
                                    if (!s.equals(masterPassword)) {
                                        SafeToastAdapter.showErrorToast(
                                                getActivity(),
                                                "Supplied password incorrect"
                                        );

                                        return;
                                    }

                                    Result<Boolean, String> deleteAccountResult =
                                            AccountManagerUtils.deleteAccount(accountsDir, accountModel);

                                    if (!deleteAccountResult.getKey()) {
                                        SafeToastAdapter.showErrorToast(
                                                getActivity(),
                                                deleteAccountResult.getValue()
                                        );

                                        return;
                                    }

                                    SafeToastAdapter.showDefaultToast(
                                            getActivity(),
                                            deleteAccountResult.getValue()
                                    );

                                    generateAccountData(masterPassword, true);
                                });
                    }
                }
        ).show();
    }

    private void showAccountRestorePrompt(SnapchatAccountModel accountModel) {
        DialogFactory.createConfirmation(
                getActivity(),
                "Restore Snapchat Account?",
                "Are you sure you want to restore the backup for the account " + htmlHighlight(accountModel.getDecryptedIdentifier()) + "?",
                new ThemedClickListener() {
                    @Override
                    public void clicked(ThemedDialog themedDialog) {
                        themedDialog.dismiss();
                        AccountManagerUtils.restoreAccount(
                                getActivity(),
                                accountsDir,
                                masterPassword,
                                accountModel,
                                restoreAccountResult -> {
                                    if (!restoreAccountResult.getKey()) {
                                        SafeToastAdapter.showErrorToast(
                                                getActivity(),
                                                restoreAccountResult.getValue()
                                        );

                                        return;
                                    }

                                    SafeToastAdapter.showDefaultToast(
                                            getActivity(),
                                            restoreAccountResult.getValue()
                                    );
                                }
                        );
                    }
                }
        ).show();
    }

    private void generateAccountData(String password) {
        generateAccountData(password, false);
    }

    public enum AccountManagerEvent {
        RETRY_CREDENTIALS, BACKUP_ACCOUNT, DECRYPT_ALL, ENCRYPT_ALL
    }

    public enum AccountEvent {
        DELETE, RESTORE
    }

    @Override
    public boolean hasTutorial() {
        return true;
    }

    @Override
    public String getName() {
        return "Account Manager";
    }


    @Override
    public Integer getMenuId() {
        return getIdFromString(getName());
    }
}
