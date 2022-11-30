package com.example.qq.Collector;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
/*
用于管理所有活动
 */
public class ActivityCollector {
    public static List<Activity> activities=new ArrayList<>();

    public static void addActivity(Activity activity)
    {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity)
    {
        activities.add(activity);
    }

    public static void finishAll()
    {
        for(Activity activity:activities)
        {
            if(!activity.isFinishing())
            {
                activity.finish();
            }
        }
    }
    public static Activity getCurrentActivity(){
        return activities.size()==0 ? null : activities.get(activities.size()-1);
    }
}
