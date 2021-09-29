package com.zj.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zj.database.entity.PrivateOwnerEntity;

import java.util.List;

@Dao
public interface PrivateChatOwnerDao {


    @Query("SELECT * FROM PRIVATE_OWNER WHERE groupId = :groupId")
    PrivateOwnerEntity findByGroupId(Long groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(PrivateOwnerEntity entity);

    @Delete()
    int delete(PrivateOwnerEntity entity);

    @Query("DELETE FROM private_owner WHERE groupId = :groupId")
    int deleteByGroupId(Long groupId);

    @Query("SELECT * FROM private_owner")
    List<PrivateOwnerEntity> findAll();

}
