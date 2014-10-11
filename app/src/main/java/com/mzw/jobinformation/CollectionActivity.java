package com.mzw.jobinformation;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class CollectionActivity extends Activity{

    private static final String DATABASE_TABLE = "job_collection";
    private SQLiteDatabase db;
    private JobDBHelper dbHelper;
    private ListView listView;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFF228b22));
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        dbHelper = new JobDBHelper(this);
        db = dbHelper.getReadableDatabase();

        listView = (ListView)findViewById(R.id.listview);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, getInfoData(), R.layout.job_info,
                new String[] {"date", "title", "comp"},
                new int[] {R.id.date, R.id.title, R.id.comp});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new JobInfoListener());
    }

    private List<Map<String, Object>> getInfoData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        Cursor c = db.rawQuery("SELECT * FROM " + DATABASE_TABLE, null);
        c.moveToLast();

        for (int i = 0; i < c.getCount(); i++) {
            map = new HashMap<String, Object>();

            map.put("date", c.getString(c.getColumnIndex("date")));
            map.put("title", c.getString(c.getColumnIndex("title")));
            map.put("comp", c.getString(c.getColumnIndex("comp")));

            list.add(map);
            c.moveToPrevious();
        }

        return list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class JobInfoListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Cursor c = db.rawQuery("SELECT * FROM " + DATABASE_TABLE, null);
            c.moveToLast();
            for (int j = 0; j < i; j++) {
                c.moveToPrevious();
            }
            String id = c.getString(c.getColumnIndex("id"));
            String title = c.getString(c.getColumnIndex("title"));
            String date = c.getString(c.getColumnIndex("date"));
            String comp = c.getString(c.getColumnIndex("comp"));
            String type = c.getString(c.getColumnIndex("type"));
            Intent intent = new Intent();

            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bundle.putString("title", title);
            bundle.putString("date", date);
            bundle.putString("comp", comp);
            bundle.putString("type", type);
            intent.putExtras(bundle);

            intent.setClass(CollectionActivity.this, DetailActivity.class);
            startActivity(intent);
        }
    }
}
