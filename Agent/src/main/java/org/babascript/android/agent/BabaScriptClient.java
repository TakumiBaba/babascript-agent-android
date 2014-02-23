package org.babascript.android.agent;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by takumi on 2014/02/04.
 */
public class BabaScriptClient {

    private Context mContext;

    public BabaScriptClient(Context c){
        this.mContext = c;
    }

    public void returnValue(Object value){
        RequestQueue mQueue = Volley.newRequestQueue(mContext);
        JSONObject params = new JSONObject();
        JSONObject tuple = new JSONObject();
        try {
            params.put("baba", "script");
            params.put("value", value);
            tuple.put("tuple", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mQueue.add(new JsonObjectRequest(Request.Method.POST, "http://linda.babascript.org/masuilab/", tuple
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
}
