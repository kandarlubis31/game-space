package com.gamespace.data;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface WhitelistDao {
    @Query("SELECT * FROM whitelist ORDER BY appName ASC")
    LiveData<List<WhitelistEntity>> getAll();

    @Query("SELECT packageName FROM whitelist")
    List<String> getPackagesSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WhitelistEntity entity);

    @Delete
    void delete(WhitelistEntity entity);
}
