package com.faisal.cakemaker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.JavascriptInterface;
import android.graphics.Color;
import android.widget.Toast;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private WebView webView;
    private final ExecutorService saveExecutor = Executors.newSingleThreadExecutor();

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(239,120,167));
        getWindow().setNavigationBarColor(Color.rgb(185,54,112));

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(255,240,245));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(false);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        webView.addJavascriptInterface(new CakeBridge(), "AndroidCake");
        webView.setWebViewClient(new WebViewClient() {
            private boolean isAppPage(Uri uri) {
                return uri != null && "file".equals(uri.getScheme())
                    && "/android_asset/index.html".equals(uri.getPath());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return !isAppPage(request.getUrl());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return !isAppPage(Uri.parse(url));
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }

    private class CakeBridge {
        @JavascriptInterface
        public void saveCakeImage(String dataUrl) {
            saveExecutor.execute(() -> {
                boolean success = false;
                Uri pendingImage = null;
                try {
                    if (dataUrl == null || !dataUrl.startsWith("data:image/png;base64,")) {
                        throw new IllegalArgumentException("Invalid PNG image");
                    }
                    byte[] imageBytes = Base64.decode(dataUrl.substring(dataUrl.indexOf(',') + 1), Base64.DEFAULT);
                    if (imageBytes.length == 0 || imageBytes.length > 15 * 1024 * 1024) {
                        throw new IllegalArgumentException("Image size is invalid");
                    }

                    String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(new Date());
                    String fileName = "Faisal_Cake_" + stamp + ".png";

                    if (Build.VERSION.SDK_INT >= 29) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                        values.put(MediaStore.Images.Media.RELATIVE_PATH,
                            Environment.DIRECTORY_PICTURES + "/Faisal Cake Maker");
                        values.put(MediaStore.Images.Media.IS_PENDING, 1);

                        pendingImage = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        if (pendingImage == null) throw new IllegalStateException("Gallery insert failed");

                        try (OutputStream output = getContentResolver().openOutputStream(pendingImage, "w")) {
                            if (output == null) throw new IllegalStateException("Gallery output unavailable");
                            output.write(imageBytes);
                            output.flush();
                        }

                        ContentValues ready = new ContentValues();
                        ready.put(MediaStore.Images.Media.IS_PENDING, 0);
                        getContentResolver().update(pendingImage, ready, null, null);
                    } else {
                        File base = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        if (base == null) throw new IllegalStateException("Picture folder unavailable");
                        File folder = new File(base, "Faisal Cake Maker");
                        if (!folder.exists() && !folder.mkdirs()) {
                            throw new IllegalStateException("Cannot create picture folder");
                        }
                        File file = new File(folder, fileName);
                        try (FileOutputStream output = new FileOutputStream(file)) {
                            output.write(imageBytes);
                            output.flush();
                        }
                    }
                    success = true;
                } catch (Exception error) {
                    if (pendingImage != null) {
                        try { getContentResolver().delete(pendingImage, null, null); }
                        catch (Exception ignored) { }
                    }
                }

                final boolean saved = success;
                runOnUiThread(() -> {
                    if (webView != null) {
                        webView.evaluateJavascript("window.onCakeImageSaved && window.onCakeImageSaved(" + saved + ")", null);
                    }
                    Toast.makeText(MainActivity.this,
                        saved ? "Cake picture saved successfully!" : "Could not save cake picture.",
                        Toast.LENGTH_LONG).show();
                });
            });
        }
    }

    @Override protected void onDestroy() {
        if (webView != null) {
            webView.removeJavascriptInterface("AndroidCake");
            webView.destroy();
            webView = null;
        }
        saveExecutor.shutdown();
        super.onDestroy();
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
