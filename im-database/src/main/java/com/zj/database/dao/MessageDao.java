package com.zj.database.dao;


import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.room.Dao;
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
    @Query("SELECT * FROM messages WHERE serverMsgId = :msgId")
    MessageInfoEntity findMsgById(String msgId);

    @WorkerThread
    @Query("SELECT * FROM messages WHERE clientMsgId = :clientMsgId")
    MessageInfoEntity findMsgByClientId(String clientMsgId);

    @WorkerThread
    @Query("SELECT * FROM messages WHERE saveInfoId = :sid")
    MessageInfoEntity findMsgBySaveInfoId(String sid);

    /**
     * 查询本地最后一条普通消息
     */
    @Query("SELECT * FROM messages WHERE groupId = :groupId AND sendingState = 0 ORDER BY sendTime DESC LIMIT 1")
    MessageInfoEntity findLastMsg(long groupId);

    /**
     * 查询本地最后一条交互消息
     */
    @Query("SELECT * FROM messages WHERE groupId == :groupId AND (sendingState = 0 OR sendingState =3) AND (ownerId = senderId OR replyId = :userId) ORDER BY sendTime DESC LIMIT 1")
    MessageInfoEntity findLastInteractiveMsg(long groupId, int userId);

    /**
     * 获取所有未发送成功的 Msg
     * */
    @Query("SELECT * FROM messages WHERE sendingState = 1")
    List<MessageInfoEntity> getAllSendingMsg();

    @Query("SELECT * FROM messages WHERE groupId = :groupId")
    List<MessageInfoEntity> getAllMessages(long groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertOrChangeMessage(@Nullable MessageInfoEntity entity);

    @Query("DELETE FROM messages WHERE clientMsgId = :clientMsgId")
    void deleteMsgByClientId(String clientMsgId);

    @Query("DELETE FROM messages WHERE saveInfoId = :sid")
    void deleteMsgBySaveInfoId(String sid);

}
