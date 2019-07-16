package com.dreamteam.httprequest.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.dreamteam.httprequest.database.Data.UserDB;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM userDB")
    List<UserDB> getAll();

    @Query("SELECT * FROM userDB WHERE id = :id")
    UserDB getById(String id);

    @Insert
    void insert (UserDB user);

    @Update
    void update (UserDB user);

    @Delete
    void delete (UserDB user);
}
