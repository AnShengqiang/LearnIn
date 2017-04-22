package com.example.anshengqiang.learnin.libcore;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by anshengqiang on 2017/3/5.
 */

public class MyDiskLruCache {

    private static final String TAG = "MyDiskLruCache";

    public static DiskLruCache mDiskLruCache;

    public MyDiskLruCache(Context context){
        mDiskLruCache = newDiskLruCache(context);
    }

    public static void deleteCachedBitmap(String imageUrl){
        try {
            String key = hashKeyForDisk(imageUrl);
            mDiskLruCache.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 我也是重点标志
     * */
    public static Bitmap getCachedBitmap(String imageUrl) {
        try {
//            imageUrl = "http://img.my.csdn.net/uploads/201309/01/1378037235_7476.jpg";
            String key = hashKeyForDisk(imageUrl);

            DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
            if (snapShot != null) {
                InputStream is = snapShot.getInputStream(0);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeImage(final String imageUrl, Bitmap bitmap){
        String key = hashKeyForDisk(imageUrl);

        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if (editor != null){
                OutputStream outputStream = editor.newOutputStream(0);
                /**
                 * bitmap.compress()
                 * */
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                editor.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 我是重点标志
     * 此方法会创造一个线程，大量使用会出错，造成多个线程读写同一个文件的情况
     */
    public static void writeImageThread(final String imageUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    String imageUrl = "http://img.my.csdn.net/uploads/201309/01/1378037235_7476.jpg";

                    String key = hashKeyForDisk(imageUrl);

                    DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                    if (editor != null) {
                        OutputStream outputStream = editor.newOutputStream(0);
                        if (downloadUrlToStream(imageUrl, outputStream)) {
                            editor.commit();
                            Log.i(TAG, "写入了缓存，图片链接为：" + imageUrl);
                        } else {
                            editor.abort();
                        }
                    }
                    mDiskLruCache.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static DiskLruCache.Editor getDiskLruCacheEditor() {
        String imageUrl = "http://img.my.csdn.net/uploads/201309/01/1378037235_7476.jpg";
        String key = hashKeyForDisk(imageUrl);
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskLruCache.edit(key);
            return editor;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private static boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static DiskLruCache newDiskLruCache(Context context) {

        try {
            File cacheDir = getDiskCacheDir(context, "bitmap");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, 200 * 1024 * 1024);
            return mDiskLruCache;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

}
