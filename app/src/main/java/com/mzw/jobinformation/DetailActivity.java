package com.mzw.jobinformation;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class DetailActivity extends Activity {

    private WebView webView;
    private MenuItem likedItem;
    private String id;
    private String title;
    private String date;
    private String comp;
    private String type;
    private DetailHandler detailHandler = new DetailHandler();
    private static final String DATABASE_TABLE = "job_collection";
    private SQLiteDatabase db;
    private JobDBHelper dbHelper;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFF228b22));
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView titleView = (TextView)findViewById(R.id.title);
        TextView dateView = (TextView)findViewById(R.id.date);
        webView = (WebView)findViewById(R.id.webview);
        dbHelper = new JobDBHelper(this);
        db = dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        id = bundle.getString("id");
        title = bundle.getString("title");
        date = bundle.getString("date");
        comp = bundle.getString("comp");
        type = bundle.getString("type");


        DetailThread thread = new DetailThread(type, id);
        thread.start();

        titleView.setText(title);
        dateView.setText(date);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.like:
                onLiked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        likedItem = menu.findItem(R.id.like);
        Cursor c = db.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE id=" + id, null);
        if (c.getCount() == 0) {
            likedItem.setIcon(R.drawable.like);
        } else {
            likedItem.setIcon(R.drawable.liked);
        }
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

    private void onLiked() {
        Cursor c = db.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE id=" + id, null);
        ContentValues cv = new ContentValues();
        if (c.getCount() == 0) {
            cv.put("id", id);
            cv.put("date", date);
            cv.put("title", title);
            cv.put("comp", comp);
            cv.put("type", type);
            db.insert(DATABASE_TABLE, null, cv);
            likedItem.setIcon(R.drawable.liked);
        } else {
            db.delete(DATABASE_TABLE, "id=" + id, null);
            likedItem.setIcon(R.drawable.like);
        }
    }
}
