package com.zj.ccIm.error

import com.zj.im.chat.exceptions.IMException

class DBFileException : IMException("the database file was injured , please reinstall the app to fix it", null, ERROR_LEVEL_REINSTALL)