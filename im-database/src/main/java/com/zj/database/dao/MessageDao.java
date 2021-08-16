package com.zj.database.dao;


import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zj.database.entity.MessageInfoEntity;

import java.util.List;

@Dao
public interface MessageDao {

    /**
     * Query by session id
     */
    @WorkerThread
    @Query("SELECT * FROM messages WHERE msgId = :msgId")
    MessageInfoEntity findMsgById(long msgId);


    /**
     * 查询本地最后一条普通消息
     */
    @Query("SELECT * FROM messages WHERE groupId = :groupId AND sendingState = 0 ORDER BY sendTime DESC LIMIT 1")
    MessageInfoEntity findLastMsg(long groupId);

    /**
     * 查询本地最后一条交互消息
     */
    @Query("SELECT * FROM messages WHERE groupId == :groupId AND sendingState < 0 AND (ownerId = senderId OR replyId = :userId) ORDER BY sendTime DESC LIMIT 1")
    MessageInfoEntity findLastInteractiveMsg(long groupId, int userId);

    /**
     * Query by session id
     */
    @WorkerThread
    @Query("SELECT * FROM messages WHERE clientMsgId = :clientMsgId")
    MessageInfoEntity findMsgByClientId(String clientMsgId);

    @Query("SELECT * FROM messages WHERE groupId = :groupId")
    List<MessageInfoEntity> getAllMessages(long groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertOrChangeMessage(@Nullable MessageInfoEntity entity);

    @Query("DELETE FROM messages WHERE clientMsgId = :clientMsgId")
    void deleteMsgByClientId(String clientMsgId);

}
