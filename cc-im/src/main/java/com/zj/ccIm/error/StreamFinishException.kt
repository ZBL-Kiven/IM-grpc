package com.zj.ccIm.error

import com.zj.im.chat.exceptions.IMException

class StreamFinishException(errorMsg: String) : IMException(errorMsg)