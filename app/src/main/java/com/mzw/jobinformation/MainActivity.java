package com.mzw.jobinformation;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private PagerSlidingTabStrip tabs;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<ListView> viewList = new ArrayList<ListView>();
        LayoutInflater inflater = getLayoutInflater();

        View view1 = inflater.inflate(R.layout.job_info_list, null);
        View view2 = inflater.inflate(R.layout.job_info_list, null);
        ListView listview1 = (ListView) view1.findViewById(R.id.listview);
        ListView listview2 = (ListView) view2.findViewById(R.id.listview);

        SimpleAdapter simpleAdapterJobInfo= new SimpleAdapter(this, getInfoData(),
                R.layout.job_info, new String[] {"date", "title", "comp"},
                new int[] {R.id.date, R.id.title, R.id.comp});

        SimpleAdapter simpleAdapterJobFair= new SimpleAdapter(this, getFairData(),
                R.layout.job_fair, new String[] {"date", "title", "comp", "time", "place"},
                new int[] {R.id.date, R.id.title, R.id.comp, R.id.time, R.id.place});

        listview1.setAdapter(simpleAdapterJobInfo);
        listview2.setAdapter(simpleAdapterJobFair);

        listview1.setOnItemClickListener(new JobInfoListener());

        viewList.add(listview1);
        viewList.add(listview2);

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new ViewAdapter(viewList));

        // Bind the tabs to the ViewPager
        tabs = (PagerSlidingTabStrip)findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        tabs.setIndicatorColor(0xFF228b22);
        tabs.setTextColor(0xFF228b22);
        getActionBar().setBackgroundDrawable(new ColorDrawable(0xFF228b22));
    }

    class ViewAdapter extends PagerAdapter {

        private final String[] TITLES = { "招聘信息", "招聘会" };
        private final ArrayList<ListView> viewList;

        ViewAdapter(ArrayList<ListView> vl) {
            viewList = vl;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ListView v = viewList.get(position);
            container.addView(v, position);

            return v;
        }
    }

    private List<Map<String, Object>> getInfoData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        for (int i = 1; i < 15; i++) {
            map = new HashMap<String, Object>();
            map.put("date", "2014-10-" + i);
            map.put("title", "job" + i);
            map.put("comp", "Comp" + i);
            list.add(map);
        }

        return list;
    }

    private List<Map<String, Object>> getFairData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        for (int i = 0; i < 15; i++) {
            map = new HashMap<String, Object>();
            map.put("date", "2014-10-" + i);
            map.put("title", "job" + i);
            map.put("comp", "Comp" + i);
            map.put("time", i);
            map.put("place", "Room" + i);
            list.add(map);
        }

        return list;
    }

    class JobInfoListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            System.out.println(i);
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, JobInfoActivity.class);
            intent.setData(Uri.parse("" + i));
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
