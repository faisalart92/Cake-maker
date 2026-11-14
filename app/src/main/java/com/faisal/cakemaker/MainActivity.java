package com.faisal.cakemaker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
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

public class MainActivity extends Activity {
    private WebView webView;

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
        settings.setAllowContentAccess(true);
        webView.addJavascriptInterface(new CakeBridge(), "AndroidCake");
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }

    private class CakeBridge {
        @JavascriptInterface
        public void saveCakeImage(String dataUrl) {
            runOnUiThread(() -> {
                try {
                    int comma = dataUrl.indexOf(',');
                    if (comma < 0) throw new IllegalArgumentException("Invalid image data");
                    byte[] imageBytes = Base64.decode(dataUrl.substring(comma + 1), Base64.DEFAULT);
                    String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                    String fileName = "Faisal_Cake_" + stamp + ".png";
                    OutputStream output;

                    if (Build.VERSION.SDK_INT >= 29) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Faisal Cake Maker");
                        values.put(MediaStore.Images.Media.IS_PENDING, 1);
                        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        if (imageUri == null) throw new IllegalStateException("Gallery could not create image");
                        output = getContentResolver().openOutputStream(imageUri);
                        if (output == null) throw new IllegalStateException("Gallery output unavailable");
                        output.write(imageBytes);
                        output.close();
                        ContentValues done = new ContentValues();
                        done.put(MediaStore.Images.Media.IS_PENDING, 0);
                        getContentResolver().update(imageUri, done, null, null);
                    } else {
                        File folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Faisal Cake Maker");
                        if (!folder.exists() && !folder.mkdirs()) throw new IllegalStateException("Cannot create picture folder");
                        File file = new File(folder, fileName);
                        output = new FileOutputStream(file);
                        output.write(imageBytes);
                        output.close();
                    }
                    Toast.makeText(MainActivity.this, "Cake picture saved successfully!", Toast.LENGTH_LONG).show();
                } catch (Exception error) {
                    Toast.makeText(MainActivity.this, "Could not save cake picture. Please try again.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
