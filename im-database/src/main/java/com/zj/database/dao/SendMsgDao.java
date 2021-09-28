package com.zj.database.dao;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zj.database.entity.SendMessageReqEn;

import java.util.List;

@SuppressWarnings("unused")
@Dao
public interface SendMsgDao {

    @Query("SELECT * FROM sendingMsg")
    List<SendMessageReqEn> findAll();

    /**
     * Query by session id
     */
    @WorkerThread
    @Query("SELECT * FROM sendingMsg WHERE groupId = :gid")
    List<SendMessageReqEn> findAllBySessionId(long gid);

    @WorkerThread
    @Query("SELECT * FROM sendingMsg WHERE clientMsgId = :callId")
    SendMessageReqEn findByCallId(String callId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertOrChange(@Nullable SendMessageReqEn entity);

    @Query("DELETE FROM sendingMsg WHERE groupId = :sessionId")
    void deleteAllBySessionId(long sessionId);

    @Query("DELETE FROM sendingMsg WHERE clientMsgId = :callId")
    void deleteByCallId(String callId);

    @Delete
    int delete(SendMessageReqEn info);
}
