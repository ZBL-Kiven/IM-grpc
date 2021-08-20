package com.zj.database.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zj.database.entity.FetchSessionMsgInfo;

@SuppressWarnings("unused")
@Dao
public interface SessionLastMessageDao {

    @Query("SELECT * FROM sessionmsginfo WHERE groupId = :groupId")
    FetchSessionMsgInfo findSessionMsgInfoBySessionId(long groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateSessionMsgInfo(FetchSessionMsgInfo info);

}
