package com.dreamteam.httprequest.database.Invitation;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dreamteam.httprequest.database.Data.InvitationDB;

import java.util.List;

@Dao
public interface InvitationDao {
    @Query("SELECT * FROM invitationDB")
    List<InvitationDB> getAll();

    @Query("SELECT * FROM invitationDB WHERE id = :id")
    InvitationDB getById(String id);

    @Insert
    void insert(InvitationDB invitation);

    @Update
    void update(InvitationDB invitation);

    @Delete
    void delete(InvitationDB invitation);
}
