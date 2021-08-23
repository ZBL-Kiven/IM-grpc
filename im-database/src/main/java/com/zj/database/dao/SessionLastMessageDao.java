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

    @Query("SELECT * FROM sessionmsginfo WHERE groupId = :groupId")
    SessionLastMsgInfo findSessionMsgInfoBySessionId(long groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateSessionMsgInfo(SessionLastMsgInfo info);

}
