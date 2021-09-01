package com.zj.ccIm.error

class FetchSessionResult(val success: Boolean, val isFirstFetch: Boolean, val isNullData: Boolean) : IMError()