package com.example.videocaching;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.net.URI;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "7";

    public static final String CHANNEL_NAME = "VideoCaching";
    public static final String CHANNEL_DESCRIPTION = "Channel for app";
    private StyledPlayerView playerView;
    private DefaultDataSource.Factory dataSourceFactory;
    private ExoPlayer player;
    ArrayList<String> videos;
    private String vidUri = "http://techslides.com/demos/sample-videos/small.mp4";    private static NotificationChannel channel;

    private void createChannel(){
        CharSequence name = CHANNEL_NAME;
        String description = CHANNEL_DESCRIPTION;
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.player_view);

        videos = new ArrayList<String>();

        videos.add("http://techslides.com/demos/sample-videos/small.mp4");
        videos.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        videos.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");
        videos.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
    }

    @Override
    protected void onStart() {
        super.onStart();
        createChannel();

        player = new ExoPlayer.Builder(getApplicationContext()).build();

        DownloadRequest downloadRequest = new DownloadRequest.Builder(vidUri, Uri.parse(vidUri)).build();

        DownloadService.sendAddDownload(
                this,
                MediaDownloadService.class,
                downloadRequest,
                /* foreground= */ false);

        DataSource.Factory cacheDataSourceFactory =
                new CacheDataSource.Factory()
                        .setCache(DownloadUtil.getDownloadCache(this))
                        .setUpstreamDataSourceFactory(DownloadUtil.getDataSourceFactory(this))
                        .setCacheWriteDataSinkFactory(null); // Disable writing.

        ExoPlayer player = new ExoPlayer.Builder(getApplicationContext())
                .setMediaSourceFactory(
                        new DefaultMediaSourceFactory(getApplicationContext())
                                .setDataSourceFactory(cacheDataSourceFactory))
                .build();


        playerView.setPlayer(player);

        ProgressiveMediaSource mediaSource =
                new ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(vidUri));
        player.setMediaSource(mediaSource);
        player.prepare();

    }

    @Override
    protected void onStop() {
        playerView.setPlayer(null);
        player.release();
        super.onStop();
    }
}