package ashley.dewald.com.dailyaffirmations.BackGroundService;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;
import java.util.Random;

import ashley.dewald.com.dailyaffirmations.Affrimation.Affirmation;
import ashley.dewald.com.dailyaffirmations.DataCollections.AffirmationCollection;
import ashley.dewald.com.dailyaffirmations.R;

public class AffirmationService extends IntentService {
    public static final String TAG = AffirmationService.class.getCanonicalName();

    private static final long TIME_ELAPSED = 1000;
    private static final String AFFIRMATION_SERVICE_FAILED_MESSAGE = "Affirmation service failed to start";

    private List<Affirmation> data;
    private String lastSelectedAffirmationMsg;

    public AffirmationService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            data = new AffirmationCollection(this).getSelectedData();
            lastSelectedAffirmationMsg = "HELLO, You should not be seeing this :)";
            if (data == null) {
                throw new Exception();
            }
        } catch (Exception e) {
            // We send the user a notification that there app has failed for some reason so they
            // can at least understand it is not just 'failing' to show up.
            // NOTE: This should in all reality be handled by also making a log and giving the user
            // the ability to: email a bug report, or attempt to rebuild the affirmation collection.
            lastSelectedAffirmationMsg = AFFIRMATION_SERVICE_FAILED_MESSAGE;
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        lastSelectedAffirmationMsg = selectAffirmation();
        displayNotification(intent, lastSelectedAffirmationMsg);

        if(lastSelectedAffirmationMsg.equalsIgnoreCase(AFFIRMATION_SERVICE_FAILED_MESSAGE))
            stopSelf();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, AffirmationService.class);
    }

    public static void setServiceAlarm(final Context context, boolean isOn) {
        Intent i = AffirmationService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), TIME_ELAPSED, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = AffirmationService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);

        return pendingIntent != null;
    }

    private String selectAffirmation(){
        // We are going to want to make sure that the last message that was sent was not an error,
        // and there is at least more than one item available for selection
        if(lastSelectedAffirmationMsg == AFFIRMATION_SERVICE_FAILED_MESSAGE || data.size() == 0){
           return AFFIRMATION_SERVICE_FAILED_MESSAGE;
        }

        // If the user only has one item selected we are just going to select that item and exit the
        // selection process.
        if(data.size() == 1){
            return data.get(0).getAffirmation();
        }

        Random r = new Random();
        int randomValue = -1;
        int possibleAffirmations = data.size() - 1;

        // Since Java also picks negative values we need to clip the most significant bit to make
        // sure the randomValue is positive. We create an empty string variable, assign the value of
        // the index of the AffirmationString we want to send to make sure it is not the same as the
        // last one sent, if it unique we return the new string to be displayed as the notification.
        String newAffirmationString = "";
        do {
            randomValue = r.nextInt() & possibleAffirmations;
            randomValue &= 0x7FFFFFFF;
            newAffirmationString = data.get(randomValue).getAffirmation();
        }while(newAffirmationString.equalsIgnoreCase(lastSelectedAffirmationMsg));
        return newAffirmationString;
    }

    private void displayNotification(Intent intent, String notificationText){
        Resources resources = getResources();
        Intent i = intent; // PhotoGalleryActivity.newIntent(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(resources.getString(R.string.notificationTicker))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(resources.getString(R.string.notificationContentTitle))
                .setContentText(notificationText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notification);
    }
}