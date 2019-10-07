package com.dreamteam.httprequest.database.User;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dreamteam.httprequest.database.Data.UserDB;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM userDB")
    List<UserDB> getAll();

    @Query("SELECT * FROM userDB WHERE id = :id")
    UserDB getById(String id);

    @Insert
    void insert(UserDB user);

    @Update
    void update(UserDB user);

    @Delete
    void delete(UserDB user);
}
