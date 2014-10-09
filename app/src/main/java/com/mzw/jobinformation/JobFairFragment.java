package com.mzw.jobinformation;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobFairFragment extends Fragment{

    private ListView listview;
    private JobFairHandler jobFairHandler = new JobFairHandler();
    private ArrayList<JobFair> jobFairList = new ArrayList<JobFair>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_info_list, container, false);
        listview = (ListView)view.findViewById(R.id.listview);

        JobFairThread jobFairThread = new JobFairThread();
        jobFairThread.start();

        listview.setOnItemClickListener(new JobFairListener());

        return view;
    }

    class JobFairListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            JobFair jobFair = jobFairList.get(i);
            Intent intent = new Intent();

            Bundle bundle = new Bundle();
            bundle.putString("id", jobFair.getId());
            bundle.putString("title", jobFair.getTitle());
            bundle.putString("date", jobFair.getDate());
            bundle.putString("type", "fair");
            intent.putExtras(bundle);

            intent.setClass(getActivity(), DetailActivity.class);
            startActivity(intent);
        }
    }

    private List<Map<String, Object>> getFairData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        for (JobFair jobFair: jobFairList) {
            map = new HashMap<String, Object>();
            try {
                map.put("date", jobFair.getDate());
                map.put("title", jobFair.getTitle());
                map.put("comp", jobFair.getCorporation());
                map.put("time", jobFair.getTime());
                map.put("place", jobFair.getPlace());
            } catch (Exception e) {
                e.printStackTrace();
            }
            list.add(map);
        }

        return list;
    }

    class JobFairHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            JSONArray jsonArray = (JSONArray)msg.obj;

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String date = jsonObject.getString("date");
                    String title = jsonObject.getString("title");
                    String corporation = jsonObject.getString("corporation");
                    String time = jsonObject.getString("held_date") + " " + jsonObject.getString("held_time");
                    String place = jsonObject.getString("place");
                    jobFairList.add(new JobFair(id, date, title, corporation, time, place));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            SimpleAdapter simpleAdapterJobFair= new SimpleAdapter(getActivity(), getFairData(),
                    R.layout.job_fair, new String[] {"date", "title", "comp", "time", "place"},
                    new int[] {R.id.date, R.id.title, R.id.comp, R.id.time, R.id.place});

            listview.setAdapter(simpleAdapterJobFair);
        }
    }

    class JobFairThread extends Thread {

        @Override
        public void run() {
            HttpClient httpClient = new DefaultHttpClient();
            StringBuilder builder = new StringBuilder();
            JSONArray jsonArray;
            String url = "http://push-mobile.twtapps.net/content/list";
            HttpPost httpPost = new HttpPost(url);
            NameValuePair pair1 = new BasicNameValuePair("ctype", "fair");
            NameValuePair pair2 = new BasicNameValuePair("page", "0");
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
                    for (String s = reader.readLine(); s != null; s = reader.readLine()) {
                        builder.append(s);
                    }
                    jsonArray = new JSONArray(builder.toString());
                    Message msg = jobFairHandler.obtainMessage();
                    msg.obj = jsonArray;
                    jobFairHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
