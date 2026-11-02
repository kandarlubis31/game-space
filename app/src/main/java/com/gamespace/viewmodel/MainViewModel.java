package com.gamespace.viewmodel;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.gamespace.data.WhitelistEntity;
import com.gamespace.repository.AppRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private AppRepository repository;
    private LiveData<List<WhitelistEntity>> whitelist;
    private MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isGameModeOn = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        whitelist = repository.getWhitelist();
        isGameModeOn.setValue(repository.isGameModeOn());
    }

    public LiveData<List<WhitelistEntity>> getWhitelist() { return whitelist; }
    public LiveData<String> getStatusMessage() { return statusMessage; }
    public LiveData<Boolean> getGameModeStatus() { return isGameModeOn; }

    public void toggleWhitelist(String pkg, String name, boolean isWhitelisted) {
        repository.toggleWhitelist(pkg, name, isWhitelisted);
    }

    public void enableGameMode() {
        repository.enableGameMode((count, error) -> {
            if (error != null) statusMessage.postValue("Error: " + error);
            else {
                statusMessage.postValue("Game Mode ON! " + count + " apps disabled.");
                isGameModeOn.postValue(true);
            }
        });
    }

    public void disableGameMode() {
        repository.disableGameMode((count, error) -> {
            if (error != null) statusMessage.postValue("Error: " + error);
            else {
                statusMessage.postValue("Game Mode OFF! " + count + " apps enabled.");
                isGameModeOn.postValue(false);
            }
        });
    }

    public List<AppInfo> getInstalledApps() {
        List<AppInfo> list = new ArrayList<>();
        PackageManager pm = repository.getPackageManager();
        for(ApplicationInfo info : repository.getInstalledApps()) {
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                list.add(new AppInfo(info.loadLabel(pm).toString(), info.packageName));
            }
        }
        Collections.sort(list, (a, b) -> a.name.compareToIgnoreCase(b.name));
        return list;
    }

    public static class AppInfo {
        public String name;
        public String pkg;
        public AppInfo(String name, String pkg) { this.name = name; this.pkg = pkg; }
    }
}
