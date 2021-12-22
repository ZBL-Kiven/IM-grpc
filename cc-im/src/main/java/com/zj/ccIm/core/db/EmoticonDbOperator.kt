package com.zj.ccIm.core.db

import com.zj.ccIm.core.IMHelper
import com.zj.database.entity.EmoticonInfo

/**
 * @author: JayQiu
 * @date: 2021/12/17
 * @description:
 */
internal object EmoticonDbOperator {
    fun findEmoticonListByPackId(packId: Long): List<EmoticonInfo>? {
        return IMHelper.withDb { imDb ->
            imDb.emoticonInfoDao().findEmoticonListByPackId(packId)
        }
    }

    fun findAll(): List<EmoticonInfo>? {
        return IMHelper.withDb { imDb ->
            imDb.emoticonInfoDao().findAll()
        }
    }

    fun insertOrUpdateEmoticonList(emoticonList: List<EmoticonInfo>) {
        IMHelper.withDb { imDb ->
            imDb.emoticonInfoDao().insertOrUpdateEmoticonList(emoticonList)
        }
    }
}