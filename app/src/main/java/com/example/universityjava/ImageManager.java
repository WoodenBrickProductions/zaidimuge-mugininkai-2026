package com.example.universityjava;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageManager {

    private static final String DIR = "game_icons";
    private static final String[] EXTS = {".jpg", ".jpeg", ".png"};

    private static final Map<String, List<Runnable>> sPending = new HashMap<>();
    private static final Handler sMain = new Handler(Looper.getMainLooper());
    private static final long DEBOUNCE_MS = 50;
    private static Runnable sFlushTask;

    public static void requestImage(Context ctx, String name, Runnable onCached) {
        if (name == null || name.isEmpty()) return;

        if (cacheFile(ctx, name) != null) {
            if (onCached != null) sMain.post(onCached);
            return;
        }

        if (persistentFile(ctx, name) == null) return;

        List<Runnable> cbs = sPending.get(name);
        if (cbs == null) { cbs = new ArrayList<>(); sPending.put(name, cbs); }
        if (onCached != null) cbs.add(onCached);

        if (sFlushTask != null) sMain.removeCallbacks(sFlushTask);
        sFlushTask = () -> flushPending(ctx.getApplicationContext());
        sMain.postDelayed(sFlushTask, DEBOUNCE_MS);
    }

    private static void flushPending(Context ctx) {
        if (sPending.isEmpty()) return;
        Map<String, List<Runnable>> batch = new HashMap<>(sPending);
        sPending.clear();
        sFlushTask = null;

        downloadBatch(ctx, new ArrayList<>(batch.keySet()), () -> {
            for (Map.Entry<String, List<Runnable>> entry : batch.entrySet()) {
                for (Runnable cb : entry.getValue()) {
                    if (cb != null) cb.run();
                }
            }
        });
    }

    public static File persistentFile(Context ctx, String name) {
        if (name == null || name.isEmpty()) return null;
        for (String ext : EXTS) {
            File f = new File(ctx.getFilesDir(), DIR + "/" + name + ext);
            if (f.exists()) return f;
        }
        return null;
    }

    public static File cacheFile(Context ctx, String name) {
        return AppActivity.getCachedImageFile(ctx, name);
    }

    public static File saveImage(Context ctx, Uri uri, String name) {
        File persistDir = new File(ctx.getFilesDir(), DIR);
        persistDir.mkdirs();
        File cacheDir = new File(ctx.getCacheDir(), DIR);
        cacheDir.mkdirs();

        File persistDest = new File(persistDir, name + ".png");
        File cacheDest   = new File(cacheDir,   name + ".png");

        try (InputStream in = ctx.getContentResolver().openInputStream(uri)) {
            if (in == null) return null;
            byte[] bytes = in.readAllBytes();
            writeBytes(bytes, persistDest);
            writeBytes(bytes, cacheDest);
            return cacheDest;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void downloadBatch(Context ctx, List<String> names, Runnable onComplete) {
        Set<String> toDownload = new LinkedHashSet<>();
        for (String name : names) {
            if (name == null || name.isEmpty()) continue;
            if (cacheFile(ctx, name) != null) continue;
            if (persistentFile(ctx, name) == null) continue;
            toDownload.add(name);
            if (toDownload.size() >= 100) break;
        }

        Handler mainHandler = new Handler(Looper.getMainLooper());

        if (toDownload.isEmpty()) {
            mainHandler.post(onComplete);
            return;
        }

        AtomicInteger remaining = new AtomicInteger(toDownload.size());
        ExecutorService executor = Executors.newCachedThreadPool();

        for (String name : toDownload) {
            executor.submit(() -> {
                try {
                    long delay = 100L + (long) (Math.random() * 900L);
                    Thread.sleep(delay);

                    File src = persistentFile(ctx, name);
                    if (src != null) {
                        File cacheDir = new File(ctx.getCacheDir(), DIR);
                        cacheDir.mkdirs();
                        String ext = src.getName().substring(src.getName().lastIndexOf('.'));
                        copyFile(src, new File(cacheDir, name + ext));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (remaining.decrementAndGet() == 0) {
                        mainHandler.post(onComplete);
                    }
                }
            });
        }
        executor.shutdown();
    }

    public static void loadFromUrl(String url, ImageView view) {
        view.setTag(url);
        view.setImageResource(R.drawable.ic_launcher_background);
        if (url == null || url.isEmpty()) return;
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(10000);
                Bitmap bm = BitmapFactory.decodeStream(conn.getInputStream());
                conn.disconnect();
                sMain.post(() -> { if (url.equals(view.getTag())) view.setImageBitmap(bm); });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void downloadUrlToFile(String url, File dest, Runnable onComplete) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(15000);
                File parent = dest.getParentFile();
                if (parent != null) parent.mkdirs();
                try (InputStream in = conn.getInputStream();
                     FileOutputStream out = new FileOutputStream(dest)) {
                    byte[] buf = new byte[4096];
                    int n;
                    while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
                }
                conn.disconnect();
                if (onComplete != null) sMain.post(onComplete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void copyFile(File src, File dst) throws IOException {
        try (FileInputStream in  = new FileInputStream(src);
             FileOutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
        }
    }

    private static void writeBytes(byte[] bytes, File dest) throws IOException {
        try (FileOutputStream out = new FileOutputStream(dest)) {
            out.write(bytes);
        }
    }
}
