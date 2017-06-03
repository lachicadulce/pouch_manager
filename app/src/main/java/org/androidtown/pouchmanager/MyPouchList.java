package org.androidtown.pouchmanager;


import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyPouchList extends Fragment {

    private View mRootView;
    private ArrayList<ListViewItem> mListViewItems;
    private ListView lvPouchList;
    private ListViewAdapter mAdapter;

    //private SharedPreferences loginPreferences;
    //private String user_id;

    public static MyPouchList newInstance() {

        Bundle args = new Bundle();

        MyPouchList fragment = new MyPouchList();
        fragment.setArguments(args);
        return fragment;
    }

    public MyPouchList() {
        // Required empty public constructor
        //loginPreferences = this.getActivity().getSharedPreferences("loginPrefs", MODE_PRIVATE);
        //user_id = loginPreferences.getString("id", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = (View) inflater.inflate(R.layout.fragment_my_pouch_list,null);

        // 리스트뷰 참조 및 Adapter달기
        lvPouchList = (ListView) mRootView.findViewById(R.id.listview_item);


        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ListViewAdapter(Constants.FRAGMENTS.MY_POUCH_LIST_FRAG, ((MainActivity) getActivity()).mMyPouchListItems);

        lvPouchList.setAdapter(mAdapter);
        lvPouchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position) ;

                String titleStr = item.getTitle() ;
                String descStr = item.getDesc() ;
                Drawable iconDrawable = item.getIcon() ;

                // TODO : use item data.
                Toast.makeText(getContext(), titleStr, Toast.LENGTH_SHORT).show();
            }
        });

        getPouchListFunc();
    }

    public void getPouchListFunc() {
        NetworkTask networkTask = new NetworkTask();

        Map params = new HashMap();
        params.put("code", "mylist");
        params.put("id", "14050032");
        networkTask.execute(params);

        //Log.d("sybaek_id", user_id);
    }

    public class NetworkTask extends AsyncTask<Map, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("ERROR")) {
            } else {
                try {
                    if (mAdapter.getCount() > 0) {
                        mAdapter.clearItems();
                        mAdapter.notifyDataSetChanged();
                    }

                    JSONArray array = new JSONArray(s);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);

                        mAdapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_bag2), row.getString("pouch_name"), "담당: "+row.getString("in_charge")+" 수신: "+row.getString("receiver"));
                    }

                    mAdapter.notifyDataSetChanged();

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }


        @Override
        protected String doInBackground(Map... maps) {

            HttpClient.Builder http = new HttpClient.Builder("POST", "http://211.253.8.132:80/list");

            http.addAllParameters(maps[0]);

            HttpClient post = http.create();
            post.request();

            if (post.getHttpStatusCode() == 200) {
                String body = post.getBody();

                Log.d("sybaek", body);
                return body;
            } else {
                Log.d("statuscodemylist", String.valueOf(post.getHttpStatusCode()));

                return "ERROR";
            }
        }


    }
}
