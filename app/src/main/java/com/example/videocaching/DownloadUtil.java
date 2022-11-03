package com.example.videocaching;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import com.google.android.exoplayer2.database.StandaloneDatabaseProvider;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.concurrent.Executor;

public class DownloadUtil {
    private static Cache downloadCache;
    private static DownloadManager downloadManager;
    private static StandaloneDatabaseProvider databaseProvider;
    private static DataSource.Factory dataSourceFactory;


    public static synchronized Cache getDownloadCache(Context context){
        if(downloadCache == null){
            File cacheDirectory = new File(context.getCacheDir(), "downloads");
            downloadCache = new SimpleCache(
                    cacheDirectory,
                    new NoOpCacheEvictor(),
                    getDatabaseProvider(context));
        }
        return downloadCache;
    }

    public static StandaloneDatabaseProvider getDatabaseProvider(Context context){
        if(databaseProvider == null){
            databaseProvider = new StandaloneDatabaseProvider(context);
        }
        return databaseProvider;
    }

    public static DataSource.Factory getDataSourceFactory(Context context){
        if(dataSourceFactory == null){
            dataSourceFactory = new DefaultHttpDataSource.Factory();
        }
        return dataSourceFactory;
    }

    public static synchronized DownloadManager getDownloadManager(Context context){
        if(downloadManager == null){
            Executor downloadExecutor = Runnable::run;
            downloadManager = new DownloadManager(
                    context,
                    getDatabaseProvider(context),
                    getDownloadCache(context),
                    getDataSourceFactory(context),
                    downloadExecutor);
        }
        return downloadManager;
    }
}
