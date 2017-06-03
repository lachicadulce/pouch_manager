package org.androidtown.pouchmanager;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;

import java.io.PrintWriter;

/**
 * Created by 백소영 on 2017-03-23.
 */

public class PouchPagerAdapter extends FragmentPagerAdapter {

    private PouchList mPouchListFrag;
    private MyPouchList mMyPouchListFrag;
    private MyPouchChargedList mMyPouchChargedListFrag;
    private MyPage mMyPageFrag;

    private static int PAGE_NUMBER = 4;

    public PouchPagerAdapter(FragmentManager fm) {
        super(fm);

        mPouchListFrag = PouchList.newInstance();
        mMyPouchListFrag = MyPouchList.newInstance();
        mMyPouchChargedListFrag = MyPouchChargedList.newInstance();
        mMyPageFrag = MyPage.newInstance();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mPouchListFrag;
            case 1:
                return mMyPouchListFrag;
            case 2:
                return mMyPouchChargedListFrag;
            case 3:
                return mMyPageFrag;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "대기 목록";
            case 1:
                return "등록 목록";
            case 2:
                return "담당 목록";
            case 3:
                return "받을 목록";
            default:
                return null;
        }
    }

    public Object getFragment(int fragment) {
        switch (fragment) {
            case Constants.FRAGMENTS.POUCH_LIST:
                return mPouchListFrag;
            case Constants.FRAGMENTS.MY_POUCH_LIST_FRAG:
                return mMyPouchListFrag;
            case Constants.FRAGMENTS.MY_POUTCH_CHARGED_LIST:
                return mMyPouchChargedListFrag;
            case Constants.FRAGMENTS.MY_PAGE:
                return mMyPageFrag;

            default:
                return null;
        }
    }
}
