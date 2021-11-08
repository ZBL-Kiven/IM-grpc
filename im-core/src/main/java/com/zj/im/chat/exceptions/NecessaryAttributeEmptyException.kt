package com.zj.im.chat.exceptions

/**
 * created by ZJJ
 *
 * it called when the necessary params not exists
 * */

internal class NecessaryAttributeEmptyException(case: String) : IMException(case, null, ERROR_LEVEL_DEADLY)
