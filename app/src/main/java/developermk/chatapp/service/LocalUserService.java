package developermk.chatapp.service;

import android.content.Context;
import android.content.SharedPreferences;

import developermk.chatapp.model.user.user;


public class LocalUserService {
    public static user getLocalUserFromPreferences(Context context){
        SharedPreferences pref = context.getSharedPreferences("LocalUser",0);
        user user = new user();
        user.userID = pref.getString("userID",null);
        user.userName = pref.getString("userName",null);
        user.userPhone = pref.getString("userPhone",null);
        return user;
    }

    public static boolean deleteLocalUserFromPreferences(Context context){
        try {
            SharedPreferences pref = context.getSharedPreferences("LocalUser",0);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }



}
