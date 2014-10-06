package com.mzw.jobinformation;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobFairFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_info_list, container, false);
        ListView listview = (ListView)view.findViewById(R.id.listview);

        SimpleAdapter simpleAdapterJobFair= new SimpleAdapter(getActivity(), getFairData(),
                R.layout.job_fair, new String[] {"date", "title", "comp", "time", "place"},
                new int[] {R.id.date, R.id.title, R.id.comp, R.id.time, R.id.place});

        listview.setAdapter(simpleAdapterJobFair);
        listview.setOnItemClickListener(new JobFairListener());

        return view;
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

    class JobFairListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            System.out.println(i);
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailActivity.class);
            intent.setData(Uri.parse("" + i));
            startActivity(intent);
        }
    }
}
