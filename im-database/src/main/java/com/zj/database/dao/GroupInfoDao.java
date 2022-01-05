package com.zj.database.dao;

import androidx.annotation.WorkerThread;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zj.database.entity.GroupInfoEntity;

/**
 * @author: JayQiu
 * @date: 2021/12/17
 * @description:
 */

@Dao
public interface GroupInfoDao {
    @WorkerThread
    @Query("SELECT * FROM groupStatus WHERE groupId = :groupId")
    GroupInfoEntity findGroupInfoById(Long groupId);

    @WorkerThread
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateGroupInfo(GroupInfoEntity groupInfoEntity);
}
