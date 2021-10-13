package com.zj.ccIm.error

class FetchSessionResult(val success: Boolean, val isFirstFetch: Boolean, val isNullData: Boolean) : IMError()

class FetchPrivateChatSessionResult(val success: Boolean, val isFirstFetch: Boolean, val isNullData: Boolean) : IMError()

class ConnectionError(errorMsg: String) : IMError(errorMsg)