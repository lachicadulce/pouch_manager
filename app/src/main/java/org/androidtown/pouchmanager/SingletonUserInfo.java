package org.androidtown.pouchmanager;

/**
 * Created by sybaek94 on 2017-06-03.
 */

class SingletonUserInfo {
    private static SingletonUserInfo ourInstance = new SingletonUserInfo();

    private String mUserId;

    static SingletonUserInfo getInstance() {
        return ourInstance;
    }

    public SingletonUserInfo() {

    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }
}
