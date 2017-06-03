package org.androidtown.pouchmanager;


import android.content.Intent;
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
import android.widget.CheckBox;
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
public class MyPage extends Fragment {

    private SingletonUserInfo mSingletonUserInfo = SingletonUserInfo.getInstance();
    private View mRootView;
    private ArrayList<ListViewItem> mListViewItems;
    private ListView lvPouchList;
    private ListViewAdapter mAdapter;

    public static MyPage newInstance() {

        Bundle args = new Bundle();

        MyPage fragment = new MyPage();
        fragment.setArguments(args);
        return fragment;
    }

    public MyPage() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = (View) inflater.inflate(R.layout.fragment_my_page,null);

        // 리스트뷰 참조 및 Adapter달기
        lvPouchList = (ListView) mRootView.findViewById(R.id.listview_item);


        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Adapter 생성
        mAdapter = new ListViewAdapter(Constants.FRAGMENTS.MY_PAGE, ((MainActivity) getActivity()).mMyPageItems);

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
                Intent intent1 = new Intent(getActivity(), PouchDetail.class);
                Log.d("sybaek", "파우치이름: "+titleStr);
                intent1.putExtra("pouch_name", titleStr);
                startActivity(intent1);
            }
        });


        getPouchListFunc();
    }

    public void getPouchListFunc() {
        NetworkTask networkTask = new NetworkTask();

        Map params = new HashMap();
        params.put("code", "receivelist");
        params.put("id", mSingletonUserInfo.getUserId());
        networkTask.execute(params);

        Log.d("sybaek", "listener");
    }

    public class NetworkTask extends AsyncTask<Map, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("ERROR")) {
                Log.d("sybaek","여기 에러래");
            } else {
                try {
                    if (mAdapter.getCount() > 0) {
                        mAdapter.clearItems();
                        mAdapter.notifyDataSetChanged();
                    }

                    JSONArray array = new JSONArray(s);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);

                        mAdapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_bag2), row.getString("pouch_name"), "발신: "+row.getString("sender")+" 상태: "+row.getString("status"));
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
                return "ERROR";
            }
        }


    }
}
