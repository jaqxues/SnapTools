package com.ljmu.andre.snaptools.ModulePack.Utils;

import android.os.Build;

import com.ljmu.andre.snaptools.Utils.ShellUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;

//import com.crashlytics.android.answers.Answers;
//import com.crashlytics.android.answers.CustomEvent;
//import io.fabric.sdk.android.Fabric;

/**
 * Created by ethan on 10/5/2017.
 */


public class NotificationLEDUtil {
    private static ArrayList<NotificationColor> queue = new ArrayList<>();
    private static Boolean currentlyLit = false;

    public synchronized static void flashLED(NotificationColor color) {
        if (currentlyLit) {
            queue.add(color);
            return;
        }
        currentlyLit = true;
        Boolean red = false;
        Boolean green = false;
        Boolean blue = false;
        ManufacturerLEDPath ledPath = getStringFromManufacturer();

        if (ledPath.hasInitialCommand) {
            ShellUtils.sendCommand(ledPath.initialCommand).subscribe();
        }

        int a = 0;
        for (int i : color.getBrightness()) {
            switch (a) {
                case 0:
                    ShellUtils.sendCommand("echo " + i + " > " + ledPath.redPath).subscribe();
                    red = true;
                    break;
                case 1:
                    ShellUtils.sendCommand("echo " + i + " > " + ledPath.greenPath).subscribe();
                    green = true;
                    break;
                case 2:
                    ShellUtils.sendCommand("echo " + i + " > " + ledPath.bluePath).subscribe();
                    blue = true;
                    break;
            }
            a++;
        }

        Timer t = new Timer();
        Boolean finalBlue = blue;
        Boolean finalGreen = green;
        Boolean finalRed = red;
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if (finalRed) {
                    ShellUtils.sendCommand("echo 0 > " + ledPath.redPath).subscribe();
                }
                if (finalGreen) {
                    ShellUtils.sendCommand("echo 0 > " + ledPath.greenPath).subscribe();
                }
                if (finalBlue) {
                    ShellUtils.sendCommand("echo 0 > " + ledPath.bluePath).subscribe();
                }
                currentlyLit = false;
                if (queue.isEmpty()) {
                    if (ledPath.hasExitCommand) {
                        ShellUtils.sendCommand(ledPath.exitCommand).subscribe();
                    }
                } else {
                    NotificationColor n = queue.get(0);
                    queue.remove(0);
                    flashLED(n);
                }
                t.cancel();
            }
        }, 1000);
    }

    private static ManufacturerLEDPath getStringFromManufacturer() {
        String m = Build.MANUFACTURER;
        Timber.d("LED_MANUFACTURER:" + m);

        if (m.equalsIgnoreCase("samsung")) {
            return ManufacturerLEDPath.SAMSUNG;
        } else if (m.equalsIgnoreCase("OnePlus")) {
            return ManufacturerLEDPath.GENERIC;
        } else {
//			if (!(Boolean) getPref(LED_INFO_ALREADY_SENT)) {
//				try {
//					if (Fabric.isInitialized()) {
//						CustomEvent unknownLedEvent = new CustomEvent("UnknownLEDManufacturer");
//						unknownLedEvent.putCustomAttribute("Manufacturer", m);
//						unknownLedEvent.putCustomAttribute("GenericLEDPathInfo", getLEDDirectoryContents("/sys/class/leds"));
//						unknownLedEvent.putCustomAttribute("SamsungLEDPathInfo", getLEDDirectoryContents("/sys/devices/virtual/sec/led"));
//						Answers.safeLogEvent(unknownLedEvent);
//						putPref(LED_INFO_ALREADY_SENT, true);
//					}
//				} catch (Throwable t) {
//					Timber.w(t, "Issue sending Answers Event");
//				}
//			}

            return ManufacturerLEDPath.GENERIC;
        }
    }

    private static String getLEDDirectoryContents(String path) {

        try {
            Process process = Runtime.getRuntime().exec("ls " + path);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuilder output = new StringBuilder();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            process.waitFor();

            return output.toString();
        } catch (Throwable t) {
            Timber.w(t, "Unable to get LED Directory Contents");
        }

        return "ERROR: UNABLE TO DETERMINE LEDS LIST`";
    }

    public enum NotificationColor {
        RED(new int[]{255, 0, 0}), GREEN(new int[]{0, 255, 0}), YELLOW(new int[]{150, 50, 0}), BLUE(new int[]{0, 0, 255});

        int[] brightness;

        NotificationColor(int[] brightness_rgb) {
            this.brightness = brightness_rgb;
        }

        int[] getBrightness() {
            return brightness;
        }
    }

    public enum ManufacturerLEDPath {
        SAMSUNG("samsung", "/sys/devices/virtual/sec/led/led_r", "/sys/devices/virtual/sec/led/led_g", "/sys/devices/virtual/sec/led/led_b", false, "", false, ""),
        GENERIC("generic", "/sys/class/leds/red/brightness", "/sys/class/leds/green/brightness", "/sys/class/leds/blue/brightness", false, "", false, "");

        String name;
        String redPath;
        String greenPath;
        String bluePath;
        Boolean hasInitialCommand;
        String initialCommand;
        Boolean hasExitCommand;
        String exitCommand;

        ManufacturerLEDPath(String name, String pathToRed, String pathToGreen, String pathToBlue, Boolean hasInitialCommand, String initialCommand, Boolean hasExitCommand, String exitCommand) {
            this.name = name;
            this.redPath = pathToRed;
            this.bluePath = pathToBlue;
            this.greenPath = pathToGreen;
            this.hasInitialCommand = hasInitialCommand;
            this.initialCommand = initialCommand;
            this.hasExitCommand = hasExitCommand;
            this.exitCommand = exitCommand;
        }
    }

}
