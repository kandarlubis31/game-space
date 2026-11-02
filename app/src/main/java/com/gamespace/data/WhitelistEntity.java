package com.gamespace.data;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "whitelist")
public class WhitelistEntity {
    @PrimaryKey
    @NonNull
    public String packageName;
    public String appName;

    public WhitelistEntity(@NonNull String packageName, String appName) {
        this.packageName = packageName;
        this.appName = appName;
    }
}
