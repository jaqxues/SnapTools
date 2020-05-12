package com.ljmu.andre.snaptools;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ljmu.andre.GsonPreferences.Preferences;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.PackUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.WATCHDOG_HANG_TIMEOUT;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class HangErrorActivity extends AppCompatActivity {
    public static final String EXTRA_REASON = "hang_reason";
    @BindView(R.id.txt_message)
    TextView txtMessage;
    @BindView(R.id.seek_timeout)
    SeekBar seekTimeout;
    @BindView(R.id.label_timeout)
    TextView labelTimeout;
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hang_error);
        unbinder = ButterKnife.bind(this);


        try {
            Preferences.init(
                    Preferences.getExternalPath() + "/" + STApplication.MODULE_TAG + "/"
            );
        } catch (Exception e) {
            Timber.e(e);

            DialogFactory.createErrorDialog(
                    this,
                    "Error Initialising Preferences",
                    "Preference system not loaded. The reason is likely to be permission issues. The application will terminate to preserve data integrity"
                            + "\n\n"
                            + "Reason: " + e.getMessage(),
                    new ThemedClickListener() {
                        @Override
                        public void clicked(ThemedDialog themedDialog) {
                            themedDialog.dismiss();
                            finish();
                        }
                    }
            ).setDismissable(false).show();
        }

        initHangTimer();

        Intent intent = getIntent();
        if (intent == null)
            return;

        Bundle extras = intent.getExtras();
        if (extras == null)
            return;

        String hangReason = extras.getString(EXTRA_REASON);
        if (hangReason == null || hangReason.isEmpty())
            return;

        txtMessage.setText(hangReason);
    }

    private void initHangTimer() {
        int timeoutMin = 5;

        int prefTimeout = getPref(WATCHDOG_HANG_TIMEOUT);
        int displayTimeout = prefTimeout / 1000;

        labelTimeout.setText(displayTimeout + " Seconds");

        seekTimeout.setMax(30 - timeoutMin);
        seekTimeout.setProgress(displayTimeout - timeoutMin);
        seekTimeout.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += timeoutMin;
                labelTimeout.setText(progress + " Seconds");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                progress += timeoutMin;
                putPref(WATCHDOG_HANG_TIMEOUT, progress * 1000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.hang_force_kill)
    public void onViewClicked() {
        PackUtils.killSCService(this);
    }

    public static void start(String reason) {
        start(ContextHelper.getModuleContext(null), reason);
    }

    public static boolean start(Context context, String reason) {
        if (context == null)
            context = ContextHelper.getModuleContext(null);

        if (context == null) {
            Timber.e(new Exception("Couldn't get a valid context to load Hang Activity with"));
            return false;
        }

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setComponent(ComponentName.unflattenFromString("com.ljmu.andre.snaptools/.HangErrorActivity"));
        intent.putExtra(HangErrorActivity.EXTRA_REASON, reason);
        context.startActivity(intent);
        return true;
    }


}
