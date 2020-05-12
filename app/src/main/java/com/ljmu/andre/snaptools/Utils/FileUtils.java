package com.ljmu.andre.snaptools.Utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.io.Closer;
import com.google.common.io.Files;
import com.google.common.io.Flushables;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.MODULES_PATH;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FileUtils {

    /**
     * Converts the content:// scheme to the file:// scheme
     *
     * @param context    Context to get the Content Resolver from
     * @param contentUri The URI to be converted using content:// scheme
     * @return The converted File
     */
    public static File getFileFromContentUri(Context context, Uri contentUri) {
        String filePath;

        try {
            filePath = getPathFromUri(context, contentUri);
        } catch (URISyntaxException e) {
            Timber.e(e);
            return null;
        }

        if (filePath == null) {
            return null;
        }

        return new File(filePath);
    }

    public static String getPathFromUri(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);

                if (cursor == null)
                    return null;

                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Converts the content:// scheme to the file path
     *
     * @param contentResolver Provides access to the content model
     * @param contentUri      The URI to be converted using content:// scheme
     * @return The converted file path
     */
    public static String getPathFromContentUri(ContentResolver contentResolver, Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(contentUri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        } else {
            return null;
        }
    }

    @Nullable
    public static File getLastModifiedFile(String dir) {
        File fl = new File(dir);
        System.out.println("F1: " + fl);

        File[] files = fl.listFiles(pathname -> true);
        System.out.println("Files: " + Arrays.toString(files));

        if (files == null || files.length <= 0)
            return null;

        long lastMod = Long.MIN_VALUE;
        File choice = null;

        for (File file : files) {
            if (file.lastModified() > lastMod) {
                choice = file;
                lastMod = file.lastModified();
            }
        }
        return choice;
    }

    public static Bitmap loadBitmap(File bitmapFile) {
        return loadBitmap(bitmapFile.getAbsolutePath());
    }

    public static Bitmap loadBitmap(String bitmapFilePath) {
        Timber.d("Loading bitmap: " + bitmapFilePath);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(bitmapFilePath, options);
    }

    public static Bitmap loadBitmap(Uri uri, Context context) {
        InputStream in = null;
        try {
            in = context.getContentResolver().openInputStream(uri);
            // You could do anything with the InputStream.
            // Here we'll just get the Bitmap at full size
            return BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            Timber.e(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }

        return null;
    }

    public static boolean createReadme(File directory, String tag, String content) {
        try {
            if (!directory.exists() && !directory.mkdirs())
                return false;

            File readme = new File(directory, getReadmeFilename(tag));

            if (!readme.exists()) {
                if (!readme.createNewFile())
                    return false;

                Files.write(
                        content,
                        readme,
                        Charset.defaultCharset()
                );
            }

            return true;
        } catch (IOException ignored) {
        }

        return false;
    }

    public static String getReadmeFilename(String tag) {
        return "ReadMe-" + tag + ".txt";
    }

    public static File getCodeCacheDir(Context context) {
        File codeCacheDir;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            codeCacheDir = context.getCodeCacheDir();
        else
            codeCacheDir = new File(context.getApplicationInfo().dataDir + "/code_cache");

        return codeCacheDir;
    }

    public static void deletePack(@Nullable Activity activity, String name) {
        File packFile = new File((String) getPref(MODULES_PATH), name + ".jar");
        if (packFile.exists())
            Timber.d("Deleted ModulePack JAR: " + packFile.delete());

        if (activity == null)
            return;

        File codeCacheDir;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            codeCacheDir = activity.getCodeCacheDir();
        else
            codeCacheDir = new File(activity.getApplicationInfo().dataDir + "/code_cache");

        File dexFile = new File(codeCacheDir, packFile.getName());
        if (dexFile.exists())
            Timber.d("Deleted ModulePack DEX: " + dexFile.delete());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File createFile(String directory, String fileName) {
        File file = new File(directory, fileName);

        return createFile(file);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Nullable
    public static File createFile(File file) {
        if (!file.exists()) {
            try {
                Files.createParentDirs(file);
                file.createNewFile();
            } catch (IOException e) {
                Timber.e(e, "Error creating file: " + file.getAbsolutePath());
                return null;
            }
        }

        return file;
    }

    public static long getDirSize(@NonNull File dir) {
        long size = 0;

        if (!dir.exists())
            return -1;

        Iterable<File> filesIterable = Files.fileTraverser().depthFirstPostOrder(dir);

        for (File file : filesIterable) {
            if (file.isDirectory())
                continue;

            size += file.length();
            Timber.d("Scanned file: " + file + " Size: " + size);
        }

        return size;
    }

    public static void deleteRecursive(File fileOrDirectory) {
        deleteRecursive(fileOrDirectory, true);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteRecursive(File fileOrDirectory, boolean deleteRoot) {
        deleteRecursive(fileOrDirectory, deleteRoot, (Set<String>) null);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteRecursive(File fileOrDirectory, boolean deleteRoot, @Nullable String fileNameBlacklist) {
        Set<String> fileNameBlacklistSet = new HashSet<>(1);
        fileNameBlacklistSet.add(fileNameBlacklist);
        deleteRecursive(fileOrDirectory, deleteRoot, fileNameBlacklistSet);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteRecursive(File fileOrDirectory, boolean deleteRoot, @Nullable Set<String> fileNameBlacklistSet) {

        if (fileOrDirectory.isDirectory()) {
            File[] fileList = fileOrDirectory.listFiles();

            if (fileList == null)
                return;

            for (File child : fileList)
                deleteRecursive(child, true);
        }

        if (deleteRoot &&
                (fileNameBlacklistSet == null || !fileNameBlacklistSet.contains(fileOrDirectory.getName())))
            fileOrDirectory.delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteEmptyFolders(File aStartingDir) {
        List<File> emptyFolders = new ArrayList<>();
        findEmptyFoldersInDir(aStartingDir, emptyFolders);

        for (File f : emptyFolders)
            f.delete();
    }

    private static boolean findEmptyFoldersInDir(File folder, List<File> emptyFolders) {
        boolean isEmpty = false;
        File[] filesAndDirs = folder.listFiles();
        if (filesAndDirs == null)
            return true;

        List<File> filesDirs = Arrays.asList(filesAndDirs);
        if (filesDirs.size() == 0) {
            isEmpty = true;
        }
        if (filesDirs.size() > 0) {
            boolean allDirsEmpty = true;
            boolean noFiles = true;
            for (File file : filesDirs) {
                if (!file.isFile()) {
                    boolean isEmptyChild = findEmptyFoldersInDir(file, emptyFolders);
                    if (!isEmptyChild) {
                        allDirsEmpty = false;
                    }
                }
                if (file.isFile()) {
                    noFiles = false;
                }
            }
            if (noFiles && allDirsEmpty) {
                isEmpty = true;
            }
        }
        if (isEmpty) {
            emptyFolders.add(folder);
        }
        return isEmpty;
    }

    public static boolean tryWrite(byte[] bytes, File outputFile) {
        try {
            Files.write(bytes, outputFile);
            return true;
        } catch (IOException e) {
            Timber.e(e);
        }

        return false;
    }

    public static boolean tryCreate(File newFile) {
        try {
            if (!newFile.exists() && !newFile.createNewFile()) {
                throw new IOException("Couldn't create file: " + newFile);
            }

            return true;
        } catch (IOException e) {
            Timber.e(e);
        }

        return false;
    }

    public static boolean streamCopy(ByteArrayOutputStream byteOutput, OutputStream targetStream) {
        Timber.d("Copying stream");

        Closer closer = Closer.create();

        try {
            closer.register(byteOutput);
            closer.register(targetStream);
            byteOutput.writeTo(targetStream);

            Flushables.flushQuietly(byteOutput);
            Flushables.flushQuietly(targetStream);

            return true;
        } catch (IOException e) {
            Timber.e(e);
        } finally {
            try {
                closer.close();
            } catch (IOException e) {
                Timber.e(e);
            }
        }

        return false;
    }

    public static boolean isDirEmpty(File root) {
        boolean isEmpty = true;

        int iterations = 0;
        for (File testingEmptiness : Files.fileTraverser().depthFirstPreOrder(root)) {
            Timber.d("Testing emptiness: " + (++iterations));

            if (!testingEmptiness.equals(root)) {
                isEmpty = false;
                break;
            }
        }

        return isEmpty;
    }
}
