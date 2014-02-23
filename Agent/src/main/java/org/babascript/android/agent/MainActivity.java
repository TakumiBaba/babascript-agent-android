package org.babascript.android.agent;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.babascript.android.agent.views.BooleanFragment;
import org.babascript.android.agent.views.IntFragment;
import org.babascript.android.agent.views.ListFragment;
import org.babascript.android.agent.views.LoginView;
import org.babascript.android.agent.views.StringFragment;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import de.greenrobot.event.EventBus;

public class MainActivity extends ActionBarActivity implements OnFragmentInteractionListener {

    private GoogleCloudMessaging mGcm;
    private Context mContext;
    public Boolean isLogined = false;
    private AgentApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        mContext = getApplicationContext();
        application = (AgentApplication)getApplication();
        application.setActivity(this);
        application.login();
        EventBus.getDefault().register(this);

        if(isLogined == false){
            setLoginView();
        }
    }

    public void onEvent(JSONObject object){
//        ここでsetViewする
        Log.d("onEvent", object.toString());
        String format = null;
        if(object == null){
            format = "index";
        }else{
            try {
                format = object.getString("format").toLowerCase();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Fragment fragment = null;
        if(format.equals("index") || format.equals("")){
            fragment = (Fragment) new PlaceholderFragment();
        }else if(format.equals("bool") || format.equals("boolean")){
            fragment = (Fragment) BooleanFragment.newInstance(object);
        }else if(format.equals("string")){
            fragment = (Fragment) StringFragment.newInstance(object);
        }else if(format.equals("int") || format.equals("number")){
            fragment = (Fragment) IntFragment.newInstance(object);
        }else if(format.equals("list")){
            fragment = (Fragment) ListFragment.newInstance(object);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

    }

    public void setLoginView(){
        LoginView fragment = LoginView.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void setNormalView(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new PlaceholderFragment())
                .commit();
    }

    private void registerInBackground(){
        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... params) {
                SharedPreferences pref = getSharedPreferences("BABASCRIPT", Activity.MODE_PRIVATE);
                String uuid = pref.getString("uuid", "");
                String registrationId = "";
                if(uuid.equals("")){
                    pref.edit().putString("uuid", UUID.randomUUID().toString()).commit();
                }
                try{
                    if(mGcm == null){
                        mGcm = GoogleCloudMessaging.getInstance(mContext);
                    }
                    registrationId = mGcm.register("170405382963");
                    RequestQueue mQueue = Volley.newRequestQueue(mContext);
                    JSONObject user = new JSONObject();
                    try {
                        user.put("uuid", uuid);
                        user.put("registrationId", registrationId);
                        user.put("deviceType", "GCM");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Boolean isInitialized = pref.getBoolean("isInitialized", false);
                    if(!isInitialized){
                        mQueue.add(new JsonObjectRequest(Request.Method.POST, "http://192.168.2.107:3000/user/new", user
                                , new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                getSharedPreferences("BABASCRIPT", Activity.MODE_PRIVATE).edit().putBoolean("isInitialized", true).commit();
                                Log.d("onResponse", response.toString());
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        }));
                    }
                    Log.d("TAGTAG", registrationId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return registrationId;
            }
        }.execute(null, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.logout_button){
            application.logout();
//            application.login();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Object value) {
        Log.d("fragment interaction", value.toString());

        application.returnValue(value);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new PlaceholderFragment())
                .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    public void onLoginFragmentInteraction(String id, String pass){
        Log.d("babascript", id+pass);
        application.login(id, pass);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
