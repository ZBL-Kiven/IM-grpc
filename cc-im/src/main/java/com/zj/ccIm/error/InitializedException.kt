package com.zj.ccIm.error

import com.zj.im.chat.exceptions.IMException

class InitializedException(set: String) : IMException("initialization error, the required properties should not be null, for : $set")