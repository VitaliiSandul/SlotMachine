package com.example.casinoslotmachine;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class OnlineActivity extends Activity {

    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private String url = "https://www.pinup.casino/ru/?lang=lang&st=j3lkf5wp&s1=&s2=&s3=&s4=&s5=&source=https://pinup-casino.net.ua/&pc=30&options=%7Boptions%7D&form_key=%7B_form_key%7D&trId=c2qedp1ct2h2o4tlktpg&popup=registration";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView wv = new WebView(this);
        wv.setWebViewClient(new WebViewClient());
        wv.setWebChromeClient(new WebChromeClient() {

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                OnlineActivity.this.showAttachmentDialog(uploadMsg);
            }

            // For Android > 3.x
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                OnlineActivity.this.showAttachmentDialog(uploadMsg);
            }

            // For Android > 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                OnlineActivity.this.showAttachmentDialog(uploadMsg);
            }
        });

        this.setContentView(wv);

        wv.loadUrl(url);

    }

    private void showAttachmentDialog(ValueCallback<Uri> uploadMsg) {
        this.mUploadMessage = uploadMsg;

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");

        this.startActivityForResult(Intent.createChooser(i, "Choose type of attachment"), FILECHOOSER_RESULTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == this.mUploadMessage) {
                return;
            }
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            this.mUploadMessage.onReceiveValue(result);
            this.mUploadMessage = null;
        }
    }
}