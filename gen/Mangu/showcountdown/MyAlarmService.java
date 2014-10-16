package Mangu.showcountdown;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class MyAlarmService extends IntentService {
	private NotificationManager mManager;
	private static final int NOTIF_ID = 1;

	public MyAlarmService() {
		super("MyAlarmService");
	}

	@Override
	public void onHandleIntent(Intent intent) {

		mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent1 = new Intent(this.getApplicationContext(),
				MyActivity.class);
		Notification notification = new Notification(R.drawable.ic_launcher,
				this.getText(R.string.notification), System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		PendingIntent pendingNotificationIntent = PendingIntent.getActivity(
				this.getApplicationContext(), 0, intent1, 0);
		notification.setLatestEventInfo(this.getApplicationContext(),
				"TV Countdown", this.getText(R.string.reminder), pendingNotificationIntent);

		mManager.notify(NOTIF_ID, notification);
	}

}
