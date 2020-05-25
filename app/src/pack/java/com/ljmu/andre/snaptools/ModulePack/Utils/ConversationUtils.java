package com.ljmu.andre.snaptools.ModulePack.Utils;

import android.app.Activity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.CBIDatabase.Utils.QueryBuilder;
import com.ljmu.andre.snaptools.Dialogs.Content.TextInputBasic;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.ModulePack.Databases.ChatDatabase;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.ChatObject;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.ConversationObject;
import com.ljmu.andre.snaptools.ModulePack.Notifications.SafeToastAdapter;
import com.ljmu.andre.snaptools.Utils.Callable;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.FileUtils.getCreateDir;
import static com.ljmu.andre.snaptools.Utils.StringUtils.yyyyMMddHHmmss;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ConversationUtils {
    public static void requestFilename(Activity activity, String defaultFilename, Callable<String> filenameCallback) {
        DialogFactory.createBasicTextInputDialog(
                activity,
                "Export Filename",
                "Please enter the filename for the exported conversation",
                defaultFilename,
                defaultFilename,
                null,
                new ThemedClickListener() {
                    @Override
                    public void clicked(ThemedDialog themedDialog) {
                        TextInputBasic input = themedDialog.getExtension();
                        String filename = input.getInputMessage();
                        filenameCallback.call(filename);
                        themedDialog.dismiss();
                    }
                }
        ).show();
    }

    public static void exportConversationAsJson(Activity activity, String filename, ConversationObject conversationObject) {
        Observable.fromCallable(() -> {
            File exportDir = getCreateDir(PackPathProvider.getChatExportPath());

            if (exportDir == null || !exportDir.exists()) {
                Timber.w("Couldn't create ChatExportPath");
                return false;
            }

            File exportFile = new File(exportDir, filename + ".json");
            if (!exportFile.createNewFile())
                Timber.w("Couldn't create exported conversation file... Attempting to continue anyway");

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            CBITable<ChatObject> chatObjectTable = ChatDatabase.getTable(ChatObject.class);

            Collection<ChatObject> chatObjectCollection = chatObjectTable.getAll(
                    new QueryBuilder()
                            .addSelection("conversation_id", conversationObject.conv_id)
                            .addSort("timestamp", "DESC")
            );

            FileWriter writer = new FileWriter(exportFile);
            gson.toJson(chatObjectCollection, writer);
            writer.flush();
            writer.close();
            return true;
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (!aBoolean) {
                            SafeToastAdapter.showErrorToast(
                                    activity,
                                    "Couldn't export Json Conversation"
                            );
                            return;
                        }

                        SafeToastAdapter.showDefaultToast(
                                activity,
                                "Exported Json Conversation"
                        );
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                        SafeToastAdapter.showErrorToast(
                                activity,
                                "Fatal error while exporting conversation"
                        );
                    }
                });
    }

    public static void exportConversationAsTxt(Activity activity, String filename, ConversationObject conversationObject) {
        Observable.fromCallable(() -> {
            File exportDir = getCreateDir(PackPathProvider.getChatExportPath());

            if (exportDir == null || !exportDir.exists()) {
                Timber.w("Couldn't create ChatExportPath");
                return false;
            }

            File exportFile = new File(exportDir, filename + ".txt");
            if (!exportFile.createNewFile())
                Timber.w("Couldn't create exported conversation file... Attempting to continue anyway");

            CBITable<ChatObject> chatObjectTable = ChatDatabase.getTable(ChatObject.class);

            Collection<ChatObject> chatObjectCollection = chatObjectTable.getAll(
                    new QueryBuilder()
                            .addSelection("conversation_id", conversationObject.conv_id)
                            .addSort("timestamp", "DESC")
            );

            FileWriter writer = new FileWriter(exportFile);

            writer.write(
                    "Device Timezone: "
                            + TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)
                            + "\n"
            );

            for (ChatObject chatObject : chatObjectCollection) {
                String timestamp = yyyyMMddHHmmss.format(new Date(chatObject.timestamp));

                writer.write(String.format("[%s] %s: %s\n\n", timestamp, chatObject.from, chatObject.text));
            }

            writer.flush();
            writer.close();
            return true;
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (!aBoolean) {
                            SafeToastAdapter.showErrorToast(
                                    activity,
                                    "Couldn't export Txt Conversation"
                            );
                            return;
                        }

                        SafeToastAdapter.showDefaultToast(
                                activity,
                                "Exported Txt Conversation"
                        );
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                        SafeToastAdapter.showErrorToast(
                                activity,
                                "Fatal error while exporting conversation"
                        );
                    }
                });
    }
}
