package com.zj.database.dao;

import androidx.annotation.WorkerThread;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zj.database.entity.EmoticonInfo;

import java.util.List;

/**
 * @author: JayQiu
 * @date: 2021/12/17
 * @description:
 */

@Dao
public interface EmoticonInfoDao {
    @WorkerThread
    @Query("SELECT * FROM emoticonInfo WHERE packId = :packId")
    List<EmoticonInfo> findEmoticonListByPackId(Long packId);

    @WorkerThread
    @Query("SELECT * FROM emoticonInfo")
    List<EmoticonInfo> findAll();

    @WorkerThread
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateEmoticonList(List<EmoticonInfo> emoticonList);
}
