package org.androidtown.pouchmanager;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;

/**
 * Created by sybaek94 on 2017-04-02.
 */

public class ListViewItem {

    private Drawable iconDrawable;
    private String titleStr;
    private String descStr;
    private boolean mIsChecked;

    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }
    public void setChecked(boolean isChecked) {
        mIsChecked = isChecked;
    }

    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getTitle() {
        return this.titleStr ;
    }
    public String getDesc() {
        return this.descStr ;
    }
    public boolean getChecked() {
        return mIsChecked;
    }

    @Override
    public String toString() {
        return "titleStr: " + titleStr + "\ndescStr: " + descStr + "\nisChecked: " + mIsChecked;
    }
}
