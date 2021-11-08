package com.zj.im.chat.exceptions

internal class NoServiceException(case: String) : IMException(case, null, ERROR_LEVEL_DEADLY)