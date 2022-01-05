package com.zj.ccIm.core.db

import com.zj.ccIm.core.IMHelper
import com.zj.database.entity.GroupInfoEntity

/**
 * @author: JayQiu
 * @date: 2021/12/17
 * @description:
 */
internal object GroupInfoDbOperator {
    fun getGroupInfoById(groupId: Long): GroupInfoEntity? {
        return IMHelper.withDb { imDb ->
            imDb.groupInfoDao().findGroupInfoById(groupId)
        }
    }

    fun insertOrUpdateGroupInfo(groupInfoEntity: GroupInfoEntity) {
        IMHelper.withDb { imDb ->
            imDb.groupInfoDao().insertOrUpdateGroupInfo(groupInfoEntity)
        }
    }
}