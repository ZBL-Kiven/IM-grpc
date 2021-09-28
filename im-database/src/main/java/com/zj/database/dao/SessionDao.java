package com.zj.database.dao;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zj.database.entity.SessionInfoEntity;

import java.util.List;

@SuppressWarnings("unused")
@Dao
public interface SessionDao {

    @Query("SELECT * FROM sessions")
    List<SessionInfoEntity> findAll();

    /**
     * Query by session id
     */
    @WorkerThread
    @Query("SELECT * FROM sessions WHERE groupId = :sessionId")
    SessionInfoEntity findSessionById(long sessionId);

    /**
     * Query by owner id
     */
    @WorkerThread
    @Query("SELECT * FROM sessions WHERE ownerId = :ownerId")
    SessionInfoEntity findSessionByOwnerId(long ownerId);


    @Query("SELECT * FROM sessions")
    List<SessionInfoEntity> getAllSessions();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertOrChangeSession(@Nullable SessionInfoEntity entity);

    @Query("DELETE FROM sessions WHERE groupId = :sessionId")
    void deleteSessionById(long sessionId);

    @Delete
    int deleteSession(SessionInfoEntity info);
}
