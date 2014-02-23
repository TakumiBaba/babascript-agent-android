package org.babascript.android.agent;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by takumi on 2014/02/04.
 */
public class AgentIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    private AgentApplication mApp;

    public AgentIntentService() {
        super("AgentIntentService");
        mApp = (AgentApplication) this.getApplication();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String type = gcm.getMessageType(intent);

        if(!extras.isEmpty()){
            if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(type)){
                JSONObject tuple = null;
                try {
                    tuple = new JSONObject(extras.getString("default"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AgentApplication agentApp = (AgentApplication)this.getApplication();
                agentApp.addTask(tuple);
            }
        }
    }

    private void sendNotification(String message){
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentText(message);

        mBuilder.setContentIntent(contentIntent);
        manager.notify(0, mBuilder.build());

    }
}
