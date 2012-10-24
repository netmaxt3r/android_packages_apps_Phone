package com.android.phone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Set;

public class BlackListUtil {
    public static final String SPNAME="black_list";
    public static boolean isBlackListed(PhoneApp mApplication, String address) {
        SharedPreferences blackListPref = mApplication.getSharedPreferences(SPNAME, 0);
        if(blackListPref.contains(address)) return true;
        address = strip(address);
        if(blackListPref.contains(address)) return true;
        Set<String> keys = blackListPref.getAll().keySet();
        for(String key:keys){
            key = strip(key);
            if(key.equalsIgnoreCase(address)) return true;
            address= address.substring(2);
            if(key.equalsIgnoreCase(address)) return true;
        }
        return false;
    }
    public static String strip(String input){
        while(input.startsWith("+")||input.startsWith("0")){
            input = input.substring(1);
        } 
        return input;
    }
    public static int getCount(Context ctx) {
        return ctx.getSharedPreferences(SPNAME, 0).getAll().size();
    }

    public static Object get(Context ctx, int position) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SPNAME, 0);
        Object key = sharedPreferences.getAll().keySet().toArray()[position];
        return sharedPreferences.getString(key.toString(), null);
    }

    public static void addBlackList(Context ctx, String selectedNumber) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SPNAME, 0);
       Editor editor = sharedPreferences.edit();
       editor.putString(selectedNumber, selectedNumber);
       editor.commit();
    }

    public static void removeFromBlackList(Context ctx,String selectedNumber) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SPNAME, 0);
        if(sharedPreferences.contains(selectedNumber)){
            Editor editor = sharedPreferences.edit();
            editor.remove(selectedNumber);
            editor.commit();
        }
    }

}
