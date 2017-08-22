package de.qa.synchronizer;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OfflineDataManager {

    private static final String TAG = OfflineDataManager.class.getSimpleName();

    private Context mContext;

    private Callback mCallback;

    private Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                Request request = new Request.Builder()
                        .url(mContext.getString(R.string.config_jsonUri))
                        .build();
                Response response = client.newCall(request).execute();
                JSONArray rootArray = new JSONArray(response.body().string());
                if (rootArray.length() >= 1 &&
                        !rootArray.getJSONObject(0).optString("revision", "")
                                .equals(getInstalledRevision())) {
                    deleteOfflineData();
                    String uri = rootArray.getJSONObject(0).optString("url");
                    IntentFilter filter
                            = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                    mContext.registerReceiver(mDownloadCompleteReceiver, filter);
                    DownloadManager downloadManager = (DownloadManager) mContext
                            .getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request dlRequest = new DownloadManager.Request(Uri.parse(uri));
                    dlRequest
                            .setTitle(mContext.getString(R.string.notification_download_title))
                            .setDestinationInExternalFilesDir(mContext, null,
                                    "offline_data.tar.gz")
                            .setNotificationVisibility(
                                    DownloadManager.Request.VISIBILITY_VISIBLE)
                            .setVisibleInDownloadsUi(false);
                    downloadManager.enqueue(dlRequest);
                } else {
                    updateCompleted();
                }
            } catch (IOException | JSONException | IllegalArgumentException e) {
                Log.e(TAG, Log.getStackTraceString(e));
                updateFailed();
            }
        }

        private void deleteOfflineData() {
            try {
                FileUtils.deleteDirectory(new File(mContext.getExternalFilesDir(null), "offline_data"));
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), Log.getStackTraceString(e));
            }
        }
    };

    /**
     * Check for updated offline data and download them if necessary.
     * DO NOT CALL if database is locked - it might corrupt your database.
     *
     * @param context a Context
     */
    public void update(Context context, Callback callback) {
        Log.i(TAG, "Updating offline data...");
        mCallback = callback;
        mContext = context;
        new Thread(mUpdateRunnable).start();
    }

    private BroadcastReceiver mDownloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager nm = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentTitle(context.getString(R.string.notification_extract_title));
            builder.setProgress(0, 0, true);
            builder.setSmallIcon(android.R.drawable.stat_sys_download);
            nm.notify(0, builder.build());
            context.unregisterReceiver(this);
            try {
                extractUpdate(context);
                updateCompleted();
            } catch (IOException e) {
                Log.e(TAG, Log.getStackTraceString(e));
                updateFailed();
            }
        }
    };

    private String getInstalledRevision() {
        File revisionFile = new File(mContext.getExternalFilesDir(null), "offline_data/revision");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(revisionFile));
            String revision = reader.readLine();
            reader.close();
            return revision;
        } catch (IOException e) {
            return null;
        }
    }

    private void extractUpdate(Context context) throws IOException {
        File gzFile = new File(context.getExternalFilesDir(null), "offline_data.tar.gz");
        File targetDir = context.getExternalFilesDir(null);
        GZIPInputStream gzIn = new GZIPInputStream(new FileInputStream(gzFile));
        TarArchiveInputStream tarIn = new TarArchiveInputStream(gzIn);
        TarArchiveEntry entry;
        while ((entry = tarIn.getNextTarEntry()) != null) {
            if (entry.isDirectory()) {
                new File(targetDir, entry.getName()).mkdirs();
            } else {
                File file = new File(targetDir, entry.getName());
                IOUtils.copy(tarIn, new FileOutputStream(file));
                Log.d(TAG, "Extracting " + file.getAbsolutePath());
            }
        }
        tarIn.close();
        gzIn.close();
        gzFile.delete();
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(0);
    }

    private void updateCompleted() {
        Log.i(TAG, "Completed.");
        if (mCallback != null) {
            mCallback.onUpdateCompleted(getInstalledRevision());
        }
    }

    private void updateFailed() {
        Log.w(TAG, "Failed!");
        if (mCallback != null) {
            mCallback.onUpdateFailed();
        }
    }

    public interface Callback {
        void onUpdateFailed();

        void onUpdateCompleted(String newRevision);
    }

}
