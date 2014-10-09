package com.mzw.jobinformation;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class DetailActivity extends Activity {

    private WebView webView;
    private DetailHandler detailHandler = new DetailHandler();

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFF228b22));
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView title = (TextView)findViewById(R.id.title);
        TextView date = (TextView)findViewById(R.id.date);
        webView = (WebView)findViewById(R.id.webview);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        DetailThread thread = new DetailThread(bundle.getString("type"), bundle.getString("id"));
        thread.start();

        title.setText(bundle.getString("title"));
        date.setText(bundle.getString("date"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            case R.id.like:
                System.out.println("I like it");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DetailHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            JSONObject jsonObject = (JSONObject)msg.obj;
            try {
                String html = jsonObject.getString("content");
                webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class DetailThread extends Thread {

        private String type;
        private String id;

        protected DetailThread(String type, String id) {
            this.type = type;
            this.id = id;
        }

        @Override
        public void run() {
            HttpClient httpClient = new DefaultHttpClient();
            JSONObject jsonObject;
            String url = "http://push-mobile.twtapps.net/content/detail";
            HttpPost httpPost = new HttpPost(url);
            NameValuePair pair1 = new BasicNameValuePair("ctype", type);
            NameValuePair pair2 = new BasicNameValuePair("index", id);
            ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(pair1);
            pairs.add(pair2);
            try {
                HttpEntity requestEntity = new UrlEncodedFormEntity(pairs);
                httpPost.setEntity(requestEntity);
                HttpResponse response = httpClient.execute(httpPost);
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String result = reader.readLine();
                    jsonObject = new JSONObject(result);
                    Message msg = detailHandler.obtainMessage();
                    msg.obj = jsonObject;
                    detailHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
