@file:Suppress("unused")

package com.zj.im.utils.log.logger

import com.zj.im.utils.now
import com.zj.im.utils.today

/**
 * Created by ZJJ
 *
 * IM status log collection utils
 *
 * collectionAble = auto
 * */
internal val errorCollector = object : LogCollectionUtils.Config() {

    override fun overriddenFolderName(folderName: String): String {
        return "$folderName/errorLogs"
    }

    override val subPath: () -> String
        get() = { today() }
    override val fileName: () -> String
        get() = { now() }
}