package com.example.videocaching;

import static com.example.videocaching.DemoUtil.DOWNLOAD_NOTIFICATION_CHANNEL_ID;

import android.app.Notification;
import android.content.Context;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.PlatformScheduler;
import com.google.android.exoplayer2.scheduler.Requirements;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.ui.DownloadNotificationHelper;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;
import java.util.List;

/** A service for downloading media. */
public class MediaDownloadService extends DownloadService {

    public MediaDownloadService() {
        super(1,DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL);
    }

    /**
     * Returns a {@link DownloadManager} to be used to downloaded content. Called only once in the
     * life cycle of the process.
     */
    @Override
    protected DownloadManager getDownloadManager() {
        return DownloadUtil.getDownloadManager(this);
    }

    /**
     * Returns a {@link Scheduler} to restart the service when requirements for downloads to continue
     * are met.
     *
     * <p>This method is not called on all devices or for all service configurations. When it is
     * called, it's called only once in the life cycle of the process. If a service has unfinished
     * downloads that cannot make progress due to unmet requirements, it will behave according to the
     * first matching case below:
     *
     * <ul>
     *   <li>If the service has {@code foregroundNotificationId} set to {@link
     *       #FOREGROUND_NOTIFICATION_ID_NONE}, then this method will not be called. The service will
     *       remain in the background until the downloads are able to continue to completion or the
     *       service is killed by the platform.
     *   <li>If the device API level is less than 31, a {@link Scheduler} is returned from this
     *       method, and the returned {@link Scheduler} {@link Scheduler#getSupportedRequirements
     *       supports} all of the requirements that have been specified for downloads to continue,
     *       then the service will stop itself and the {@link Scheduler} will be used to restart it in
     *       the foreground when the requirements are met.
     *   <li>If the device API level is less than 31 and either {@code null} or a {@link Scheduler}
     *       that does not {@link Scheduler#getSupportedRequirements support} all of the requirements
     *       is returned from this method, then the service will remain in the foreground until the
     *       downloads are able to continue to completion.
     *   <li>If the device API level is 31 or above, then this method will not be called and the
     *       service will remain in the foreground until the downloads are able to continue to
     *       completion. A {@link Scheduler} cannot be used for this case due to <a
     *       href="https://developer.android.com/about/versions/12/foreground-services">Android 12
     *       foreground service launch restrictions</a>.
     *   <li>
     * </ul>
     */
    @Nullable
    @Override
    protected Scheduler getScheduler() {
        return null;
    }

    /**
     * Returns a notification to be displayed when this service running in the foreground.
     *
     * <p>Download services that do not wish to run in the foreground should be created by setting the
     * {@code foregroundNotificationId} constructor argument to {@link
     * #FOREGROUND_NOTIFICATION_ID_NONE}. This method is not called for such services, meaning it can
     * be implemented to throw {@link UnsupportedOperationException}.
     *
     * @param downloads          The current downloads.
     * @param notMetRequirements Any requirements for downloads that are not currently met.
     * @return The foreground notification to display.
     */
    @Override
    protected Notification getForegroundNotification(List<Download> downloads, int notMetRequirements) {
        return new DownloadNotificationHelper(this, MainActivity.CHANNEL_ID).buildProgressNotification(this, R.drawable.ic_download, null,null,downloads,notMetRequirements);
    }
}