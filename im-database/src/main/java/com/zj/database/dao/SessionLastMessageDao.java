package com.zj.database.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zj.database.entity.SessionLastMsgInfo;

import java.util.List;

@SuppressWarnings("unused")
@Dao
public interface SessionLastMessageDao {

    @Query("SELECT * FROM sessionmsginfo")
    List<SessionLastMsgInfo> findAll();

    @Query("SELECT * FROM sessionmsginfo WHERE `key` = :key")
    SessionLastMsgInfo findSessionMsgInfoByKey(String key);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateSessionMsgInfo(SessionLastMsgInfo info);

    @Query("DELETE FROM sessionmsginfo WHERE `key` = :key")
    void deleteByKey(String key);

}
