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
import android.widget.TextView;

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

public class JobInfoFragment extends Fragment{

    private ListView listview;
    private int page = 0;
    private JobInfoHandler jobInfoHandler = new JobInfoHandler();
    private ArrayList<JobInfo> jobInfoList = new ArrayList<JobInfo>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_info_list, container, false);
        listview = (ListView) view.findViewById(R.id.listview);

        JobInfoThread thread = new JobInfoThread("" + page);
        thread.start();

        listview.setOnItemClickListener(new JobInfoListener());

        View footer = LayoutInflater.from(getActivity()).inflate(R.layout.list_footer, null);
        listview.addFooterView(footer);

        return view;
    }

    class JobInfoListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (i < jobInfoList.size()) {
                System.out.println(i);
                JobInfo jobInfo = jobInfoList.get(i);
                Intent intent = new Intent();

                Bundle bundle = new Bundle();
                bundle.putString("id", jobInfo.getId());
                bundle.putString("title", jobInfo.getTitle());
                bundle.putString("date", jobInfo.getDate());
                bundle.putString("comp", jobInfo.getCorporation());
                bundle.putString("type", "job");
                intent.putExtras(bundle);

                intent.setClass(getActivity(), DetailActivity.class);
                startActivity(intent);
            } else {
                System.out.println("more");
                page ++;
                JobInfoThread thread = new JobInfoThread("" + page);
                thread.start();
            }
        }
    }

    private List<Map<String, Object>> getInfoData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        for (JobInfo jobInfo:jobInfoList) {
            map = new HashMap<String, Object>();
            try {
                map.put("date", jobInfo.getDate());
                map.put("title", jobInfo.getTitle());
                map.put("comp", jobInfo.getCorporation());
            } catch (Exception e) {
                e.printStackTrace();
            }
            list.add(map);
        }

        return list;
    }

    class JobInfoHandler extends Handler {

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
                    jobInfoList.add(new JobInfo(id, date, title, corporation));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            SimpleAdapter simpleAdapterJobInfo= new SimpleAdapter(getActivity(), getInfoData(),
                R.layout.job_info, new String[] {"date", "title", "comp"},
                new int[] {R.id.date, R.id.title, R.id.comp});

            listview.setAdapter(simpleAdapterJobInfo);
        }
    }

    class JobInfoThread extends Thread {

        private String page;

        public JobInfoThread(String page) {
            this.page = page;
        }

        @Override
        public void run() {
            HttpClient httpClient = new DefaultHttpClient();
            StringBuilder builder = new StringBuilder();
            JSONArray jsonArray;
            String url = "http://push-mobile.twtapps.net/content/list";
            HttpPost httpPost = new HttpPost(url);
            NameValuePair pair1 = new BasicNameValuePair("ctype", "job");
            NameValuePair pair2 = new BasicNameValuePair("page", page);
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
                    Message msg = jobInfoHandler.obtainMessage();
                    msg.obj = jsonArray;
                    jobInfoHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
