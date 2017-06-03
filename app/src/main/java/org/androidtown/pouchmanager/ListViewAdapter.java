package org.androidtown.pouchmanager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * Created by sybaek94 on 2017-04-02.
 */

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<ListViewItem> mListViewItemList;

    private boolean mShowCheckbox;
    private int mCurrentFragment;

    // ListViewAdapter의 생성자
    public ListViewAdapter(int option, ArrayList<ListViewItem> listViewItemList) {
        // 어댑터 클래스는 하나니까, 어떤 프래그먼트에 대한 어댑터인지 mCurrentFragment에 저장
        mCurrentFragment = option;
        // 메인 액티비티의 리스트 참조를 가져 옴
        mListViewItemList = listViewItemList;

        // 현재 프래그먼트가 전체 목록이라면 체크 박스를 보이게 함
        switch (mCurrentFragment) {
            case Constants.FRAGMENTS.POUCH_LIST:
                mShowCheckbox = true;
                break;
            default:
                mShowCheckbox = false;
                break;
        }
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return mListViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        final ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1) ;
        final TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1) ;
        final TextView descTextView = (TextView) convertView.findViewById(R.id.textView2) ;
        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.cb_charge);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ListViewItem listViewItem = mListViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.getIcon());
        titleTextView.setText(listViewItem.getTitle());
        descTextView.setText(listViewItem.getDesc());
        checkBox.setChecked(listViewItem.getChecked());

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBox.setChecked(isChecked);
                listViewItem.setChecked(isChecked);
            }
        });

        if (!mShowCheckbox) {
            checkBox.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return mListViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(Drawable icon, String title, String desc) {
        ListViewItem item = new ListViewItem();

        item.setIcon(icon);
        item.setTitle(title);
        item.setDesc(desc);
        item.setChecked(false);

        mListViewItemList.add(item);
    }

    public void clearItems() {
        mListViewItemList.clear();
    }
}
