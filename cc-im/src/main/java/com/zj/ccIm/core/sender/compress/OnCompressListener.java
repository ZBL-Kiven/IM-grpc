package com.zj.ccIm.core.sender.compress;

public interface OnCompressListener {

    /**
     * Fired when the compression is started, override to handle in your own code
     */
    void onStart();

    /**
     * Fired when a compression returns successfully, override to handle in your own code
     */
    void onSuccess(String path);

    /**
     * Fired when a compression fails to complete, override to handle in your own code
     */
    void onError(int code, Throwable e);
}
