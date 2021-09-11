package com.zj.ccIm.core.sender.compress;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class ImgCompress implements Handler.Callback {

    private static final String DEFAULT_DISK_CACHE_DIR = "disk_cache";

    private static final int MSG_COMPRESS_START = 1;
    private static final int MSG_COMPRESS_ERROR = 2;
    private static final int MSG_COMPRESS_MULTIPLE_SUCCESS = 3;
    private final String mTargetPath;
    private final String mPath;
    private final int mLeastCompressSize;
    private final OnCompressListener mCompressListener;
    private final Handler mHandler;

    private ImgCompress(Builder builder) {
        this.mPath = builder.mPath;
        this.mTargetPath = builder.mTargetPath;
        this.mCompressListener = builder.mCompressListener;
        this.mLeastCompressSize = builder.mLeastCompressSize;
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    /**
     * Returns a mFile with a cache audio name in the private cache directory.
     *
     * @param context A context.
     */
    private File getImageCacheFile(Context context, String suffix) {
        File cache = getImageCacheDir(context);
        if (cache == null) {
            mCompressListener.onError(new FileNotFoundException("no target cached file found"));
            return null;
        }
        if (!cache.exists() && !cache.mkdirs()) {
            return null;
        }
        String targetPath = mTargetPath + (TextUtils.isEmpty(suffix) ? ".JPEG" : suffix);
        return checkOrBuildFile(cache, targetPath);
    }

    private File checkOrBuildFile(File cache, String targetPath) {
        try {
            File target = new File(cache, targetPath);
            if (!target.exists()) {
                File parent = target.getParentFile();
                if (parent == null) {
                    String s = target.getParent();
                    if (s == null || s.isEmpty()) {
                        s = cache.getPath();
                    }
                    parent = new File(s);
                }
                if ((parent.exists() || parent.mkdirs()) && ((target.exists() && target.isFile()) || target.createNewFile())) {
                    return target;
                }
            }
        } catch (IOException ignored) {
        }
        return null;
    }


    /**
     * Returns a directory with a default name in the private cache directory of the application to
     * use to store retrieved audio.
     *
     * @param context A context.
     * @see #getImageCacheDir(Context, String)
     */
    @Nullable
    private File getImageCacheDir(Context context) {
        return getImageCacheDir(context, DEFAULT_DISK_CACHE_DIR);
    }

    /**
     * Returns a directory with the given name in the private cache directory of the application to
     * use to store retrieved media and thumbnails.
     *
     * @param context   A context.
     * @param cacheName The name of the subdirectory in which to store the cache.
     * @see #getImageCacheDir(Context)
     */
    @Nullable
    private File getImageCacheDir(Context context, String cacheName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        File cacheDir = new File(cachePath);
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            return null;
        }
        File result = new File(cacheDir, cacheName);
        if (result.exists() && !result.isDirectory()) {
            if (!result.delete()) return null;
            result = new File(cacheDir, cacheName);
        }
        if (!result.exists() && !result.mkdirs()) {
            return null;
        }
        return result;
    }

    /**
     * start asynchronous compress thread
     */
    @UiThread
    private void launch(final Context context) {
        if (context == null) {
            mCompressListener.onError(new NullPointerException("context cannot be null"));
            return;
        }
        final String path = mPath;
        if (path == null || path.isEmpty() && mCompressListener != null) {
            mCompressListener.onError(new NullPointerException("image file cannot be null"));
            return;
        }
        if (!TextUtils.isEmpty(path)) {
            AsyncTask.SERIAL_EXECUTOR.execute(() -> {
                try {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START));
                    File result;
                    if (Checker.isNeedCompress(mLeastCompressSize, path)) {
                        File f = getImageCacheFile(context, Checker.checkSuffix(path));
                        if (f == null || !f.exists()) {
                            if (mCompressListener != null) mCompressListener.onError(new NullPointerException("the compress temp file is invalid!"));
                            return;
                        }
                        result = new Engine(path, f).compress();
                    } else {
                        result = new File(path);
                    }
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_MULTIPLE_SUCCESS, result.getAbsolutePath()));
                } catch (IOException e) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, e));
                }
            });
        } else {
            if (mCompressListener != null) mCompressListener.onError(new IllegalArgumentException("can not read the path : " + path));
        }
    }

    /**
     * start compress and return the mFile
     */
    @WorkerThread
    @Nullable
    private File get(String path, Context context) throws IOException {
        File target = getImageCacheFile(context, Checker.checkSuffix(path));
        if (target == null || !target.exists()) {
            mCompressListener.onError(new NullPointerException("the file with path " + path + " is invalid!"));
            return null;
        }
        return Checker.isNeedCompress(mLeastCompressSize, path) ? new Engine(path, target).compress() : new File(path);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (mCompressListener == null) return false;

        switch (msg.what) {
            case MSG_COMPRESS_START:
                mCompressListener.onStart();
                break;
            case MSG_COMPRESS_MULTIPLE_SUCCESS:
                mCompressListener.onSuccess(msg.obj.toString());
                break;
            case MSG_COMPRESS_ERROR:
                mCompressListener.onError((Throwable) msg.obj);
                break;
        }
        return false;
    }

    @SuppressWarnings("unused")
    public static class Builder {
        private final Context context;
        private String mTargetPath;
        private String mPath;
        private int mLeastCompressSize = 100;
        private OnCompressListener mCompressListener;

        Builder(Context context) {
            this.context = context;
        }

        private ImgCompress build() {
            return new ImgCompress(this);
        }

        public Builder load(File file) {
            this.mPath = file.getAbsolutePath();
            return this;
        }

        public Builder load(String string) {
            this.mPath = string;
            return this;
        }

        public Builder setCompressListener(OnCompressListener listener) {
            this.mCompressListener = listener;
            return this;
        }

        public Builder setTargetPath(String targetPath) {
            if (!TextUtils.isEmpty(targetPath)) {
                String[] paths = targetPath.split("\\.");
                this.mTargetPath = paths[0];
            }
            return this;
        }

        /**
         * do not compress when the origin image file size less than one value
         *
         * @param size the value of file size, unit KB, default 100K
         */
        public Builder ignoreBy(int size) {
            this.mLeastCompressSize = size;
            return this;
        }

        /**
         * begin compress image with asynchronous
         */
        public void start() {
            build().launch(context);
        }

        public File get(String path) throws IOException {
            return build().get(path, context);
        }
    }
}
