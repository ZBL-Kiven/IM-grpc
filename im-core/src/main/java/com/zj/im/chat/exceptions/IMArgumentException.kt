package com.zj.im.chat.exceptions

class IMArgumentException(case: String) : IMException(case, IMException.ERROR_LEVEL_ALERT) {}