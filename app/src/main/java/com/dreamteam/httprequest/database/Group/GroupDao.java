package com.dreamteam.httprequest.database.Group;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dreamteam.httprequest.database.Data.GroupDB;
import com.dreamteam.httprequest.database.Data.InvitationDB;

import java.util.List;

@Dao
public interface GroupDao {
    @Query("SELECT * FROM groupDB")
    List<InvitationDB> getAll();

    @Query("SELECT * FROM groupDB WHERE id = :id")
    GroupDB getById(String id);

    @Insert
    void insert(GroupDB invitation);

    @Update
    void update(GroupDB invitation);

    @Delete
    void delete(GroupDB invitation);
}
