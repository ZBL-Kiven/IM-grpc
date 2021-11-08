package com.zj.ccIm.error

import com.zj.im.chat.exceptions.IMException

class ConnectionError(errorMsg: String) : IMException(errorMsg)