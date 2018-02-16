package com.company.zicure.campusconnect.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.company.zicure.campusconnect.R;
import com.company.zicure.campusconnect.activity.MainMenuActivity;
import com.company.zicure.campusconnect.utility.ContextCart;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Pakgon on 5/16/2017 AD.
 */

public class MessagingService extends FirebaseMessagingService {

    private String TAG = "tag_messaging";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //Handle FCM messages here.
        //Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        //Check if messages contains a data payload.
        if (remoteMessage.getData().size() > 0){
            String remote = remoteMessage.getData().get("badge");
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            Log.i(TAG, "Message data payload: " + remote + ", " + title +", "+ body);

            if (remote != null) {
                int badge = Integer.parseInt(remote);
                countBadgeOnForeground(badge);
            }

            if (!title.isEmpty() && !body.isEmpty()) {
                sendNotification(title, body);
            }
        }

        //Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody() + ", " + "Title: " + remoteMessage.getNotification().getTitle());
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

        //Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(String title, String body){
        if (!title.equalsIgnoreCase("check notification")) {
            Intent intent = new Intent(this, MainMenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            PackageManager pm = getPackageManager();
            Intent launcherIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(launcherIntent.getComponent());
            stackBuilder.addNextIntent(launcherIntent);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this,"fcm_default_channel")
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.drawable.logo_connect)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setDefaults(NotificationCompat.VISIBILITY_PUBLIC)
                            .setAutoCancel(true);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());
        }
    }

    private void countBadgeOnForeground(int badge){
        try {
            if (badge > 0){
                BadgeController.getInstance(this).setCountBadge(badge);
                ((MainMenuActivity) ContextCart.getInstance().getContext()).showBadge();
            }else{
                BadgeController.getInstance(this).setCountBadge(badge);
                ((MainMenuActivity) ContextCart.getInstance().getContext()).hideBadge();
            }
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
