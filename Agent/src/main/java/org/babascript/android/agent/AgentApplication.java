package org.babascript.android.agent;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * Created by takumi on 2014/02/04.
 */
public class AgentApplication extends Application {

    private MainActivity mActivity;
    private Context mContext;
    private JSONObject mTask;
    private static String appName = "babascript";
    public static String API = "http://133.27.246.111:3000/api";

    @Override
    public void onCreate(){
        mContext = getApplicationContext();
        mTask = new JSONObject();
        SharedPreferences pref = getSharedPreferences(appName, Activity.MODE_PRIVATE);
        if(pref.getString("token", "").equals("")){
            this.registerInBackground();
        }
        if(pref.getString("uuid", "").equals("")){
           pref.edit().putString("uuid", UUID.randomUUID().toString()).commit();
        }
    }

    public void addTask(JSONObject tuple){
        mTask = tuple;
        EventBus.getDefault().post(mTask);
    }

    public void setActivity(MainActivity activity){
        this.mActivity = activity;
    }

    public void returnValue(Object value){
        RequestQueue mQueue = Volley.newRequestQueue(mContext);
        JSONObject params = new JSONObject();
        JSONObject tuple = new JSONObject();
        String name = "";
        try {
            name =  mTask.getString("name");
            params.put("baba", "script");
            params.put("value", value);
            params.put("type", "return");
            params.put("cid", mTask.getString("cid"));
            tuple.put("tuple", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mQueue.add(new JsonObjectRequest(Request.Method.POST, API+"/linda/"+name, tuple
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("onResponse", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }));
    }

    public void login(){
        SharedPreferences pref = getSharedPreferences(appName, Activity.MODE_PRIVATE);
        String id = pref.getString("id", "");
        String pass = pref.getString("pass", "");
        this.login(id, pass);
    }

    public void login(String id, String pass){
        SharedPreferences pref = getSharedPreferences(appName, Activity.MODE_PRIVATE);
        String uuid = pref.getString("uuid", "");
        String token = pref.getString("token", "");
        RequestQueue mQueue = Volley.newRequestQueue(mContext);
        JSONObject params = new JSONObject();
        if (id.equals("") || pass.equals("")){
            mActivity.isLogined = false;
        }else{
            mActivity.isLogined = true;
            if(uuid.equals("")){
                uuid = UUID.randomUUID().toString();
                pref.edit().putString("uuid", uuid).commit();
            }else if(token.equals("")){
                this.registerInBackground();
            }else{
                try{
                    params.put("id", id);
                    params.put("pass", pass);
                    params.put("deviceId", uuid);
                    params.put("deviceType", "GCM");
                    params.put("token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mQueue.add(new JsonObjectRequest(Request.Method.POST, API+"/device/login", params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean isSuccess = false;
                        String id = "";
                        String pass = "";
                        try {
                            isSuccess = response.getBoolean("status");
                            id = response.getString("id");
                            pass = response.getString("pass");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(isSuccess == true){
                            getSharedPreferences(appName, Activity.MODE_PRIVATE).edit().putString("id", id).putString("pass", pass).commit();
                            mActivity.setNormalView();
                        }
                        Log.d("login", response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }));
            }
        }
    }

    public void logout(){
        SharedPreferences pref = getSharedPreferences(appName, Activity.MODE_PRIVATE);
        pref.edit().clear().commit();
//        pref.edit().putString("id", "").putString("pass", "").putString("uuid", "").putString("token", "").commit();
    }

    public void registerInBackground(){
        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... params) {
                SharedPreferences pref = getSharedPreferences(appName, Activity.MODE_PRIVATE);
                String registrationId = "";
                try{
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
                    registrationId = gcm.register(String.valueOf(R.string.project_id));
//                    registrationId = gcm.register("170405382963");

                    pref.edit().putString("token", registrationId).commit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                login();
                return registrationId;
            }
        }.execute(null, null, null);
    }
}