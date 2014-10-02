package Mangu.showcountdown;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.CalendarContract.Events;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyActivity extends Activity {
	// String necesarios
	private static TextView result_show;
	private static String result_show_string;
	private static final String show_init = "http://api.trakt.tv/show/seasons.json/a5cddd67244d70d9fb782579fa64bc87/";
	private static final String episode_init = "http://api.trakt.tv/show/season.json/a5cddd67244d70d9fb782579fa64bc87/";
	private static final String summary_init = "http://api.trakt.tv/show/episode/summary.json/a5cddd67244d70d9fb782579fa64bc87/";
	// Objetos en la UI
	private Button about;
	private ImageView banner_show;
	private EditText input_text;
	private Button search_sender;
	private TextView text_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);
		MyActivity.result_show = (TextView) findViewById(R.id.result_show);
		result_show_string = result_show.getText().toString();
		this.search_sender = ((Button) findViewById(R.id.search_sender));
		this.banner_show = ((ImageView) findViewById(R.id.banner_show));
		this.text_result = ((TextView) findViewById(R.id.text_result));
		this.input_text = ((EditText) findViewById(R.id.input_text));
		this.about = ((Button) findViewById(R.id.about_button));

		this.search_sender.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					StrictMode
							.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
									.permitAll().build());
					BackgroundDownload localBackgroundDownload = new BackgroundDownload();
					String show_to_search = input_text.getText().toString();
					show_to_search = show_to_search.toLowerCase().replace(" ",
							"-");
					show_to_search = changeShow(show_to_search);
					String str2 = show_init + show_to_search;

					Toast.makeText(getApplicationContext(),
							getText(R.string.wait), Toast.LENGTH_LONG);
					String season_number = String.valueOf(new JSONObject(
							(makeJSON(localBackgroundDownload
									.doInBackground(str2)))).get("season"));
					String str5 = episode_init + show_to_search + "/"
							+ season_number;
					JSONObject localJSONObject = new JSONObject(
							(makeJSON(localBackgroundDownload
									.doInBackground(str5))));
					String episode_number = String.valueOf(localJSONObject
							.get("episode"));
					String episode_title = String.valueOf(localJSONObject
							.getString("title"));
					String iso_date = String.valueOf(localJSONObject
							.get("first_aired_iso"));
					final String final_date = getFinalDate(iso_date);
					String str10 = summary_init + show_to_search + "/"
							+ season_number + "/" + episode_number;
					JSONObject banner = new JSONObject(localBackgroundDownload
							.doInBackground(str10));
					String banner_url = banner.getJSONObject("show")
							.getJSONObject("images").getString("banner");
					if (checkDate(iso_date)) {
						if (episode_title.equalsIgnoreCase("TBA")) {
							episode_title = getApplicationContext().getString(
									R.string.tba);
						}
						result_show.setText((getText(R.string.result_en)) + " "
								+ episode_title);
						text_result.setVisibility(View.VISIBLE);
						text_result.setText((getText(R.string.date_next)) + " "
								+ final_date);
						InputStream localIS = getImage(banner_url);
						Bitmap localBitmap = BitmapFactory
								.decodeStream(localIS);
						banner_show.setImageBitmap(localBitmap);
						banner_show.setVisibility(View.VISIBLE);
						new AlertDialog.Builder(MyActivity.this)
								.setTitle(
										getText(R.string.title_reminder) + " "
												+ episode_title)
								.setMessage(R.string.setCalendar)
								.setPositiveButton(R.string.yes,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												Calendar calendar = Calendar
														.getInstance();
												calendar.set(
														Calendar.DAY_OF_MONTH,
														getDay(final_date));
												calendar.set(Calendar.MONTH,
														getMonth(final_date));
												calendar.set(Calendar.YEAR,
														getYear(final_date));
												calendar.set(Calendar.AM_PM,
														Calendar.PM);
												calendar.set(
														Calendar.HOUR_OF_DAY,
														15);
												calendar.set(Calendar.MINUTE, 0);
												calendar.set(Calendar.SECOND, 0);
												AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
												Intent myIntent = new Intent(
														MyActivity.this,
														AlarmReceiver.class);
												PendingIntent pendingIntent = PendingIntent
														.getBroadcast(
																MyActivity.this,
																0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
												
												alarmManager
														.set(AlarmManager.RTC,
																calendar.getTimeInMillis(),
																pendingIntent);
												dialog.cancel();
											}
										})
								.setNegativeButton(R.string.no,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// Do nothing.
												dialog.cancel();
											}
										}).setIcon(R.drawable.ic_launcher)
								.show();
						localIS.close();
					} else {
						result_show.setText(R.string.no_episodes);
						banner_show.setVisibility(View.INVISIBLE);
						text_result.setVisibility(View.INVISIBLE);
					}
				} catch (FileNotFoundException e) {
					Toast.makeText(getApplicationContext(), R.string.no_exists,
							Toast.LENGTH_LONG).show();
				} catch (IOException ex) {
					Toast.makeText(getApplicationContext(), ex.getMessage(),
							Toast.LENGTH_LONG).show();
				} catch (Exception exx) {
					Toast.makeText(getApplicationContext(), exx.getMessage(),
							Toast.LENGTH_LONG).show();
				}

			}
		});
		this.about.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent localIntent = new Intent(getApplicationContext(),
						about_me.class);
				startActivity(localIntent);
			}
		});
	}

	private static String makeJSON(String paramString) {
		StringBuilder localStringBuilder = new StringBuilder(paramString);
		if (paramString.length() > 1) {
			localStringBuilder.deleteCharAt(0);
			localStringBuilder.deleteCharAt(-1 + localStringBuilder.length());
		}
		String str = localStringBuilder.toString();
		return str;
	}

	private static String getFinalDate(String paramString) {
		String str1 = paramString.substring(0, 4);
		String str2 = paramString.substring(5, 7);
		return paramString.substring(8, 10) + "-" + str2 + "-" + str1;
	}

	private int getDay(String paramString) {
		return Integer.getInteger(paramString.substring(0, 2),
				new Date().getDate());
	}

	private static int getMonth(String paramString) {
		return Integer.getInteger(paramString.substring(3, 5),
				new Date().getMonth());
	}

	private static int getYear(String paramString) {
		return Integer.getInteger(paramString.substring(6, 10),
				new Date().getYear());
	}

	private static boolean checkDate(String paramString) throws ParseException {
		int i = 0;
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");
		Date localDate1 = localSimpleDateFormat.parse(paramString);
		Date localDate2 = localSimpleDateFormat.parse(localSimpleDateFormat
				.format(new Date()));
		if ((localDate1.after(localDate2)) || (localDate1.equals(localDate2)))
			i = 1;
		return i == 0 ? false : true;
	}

	private static InputStream getImage(String paramString) throws IOException {
		return ((HttpURLConnection) new URL(paramString).openConnection())
				.getInputStream();
	}

	/**
	 * @author Adrian Marin This method is bullshit but is the only thing I can
	 *         do, I can't use the switch statement with a String
	 * @param paramString
	 * @return str
	 */
	private String changeShow(String paramString) {
		String str = paramString;
		if (paramString.equalsIgnoreCase("juego de tronos")) {
			str = "game-of-thrones";
		}
		if (paramString.equalsIgnoreCase("la cupula")) {
			str = "under-the-dome";
		}
		if (paramString.equalsIgnoreCase("big bang theory")) {
			str = "the-big-bang-theory";
		}
		if (paramString.equalsIgnoreCase("doctor who")) {
			str = "doctor-who-2005";
		}
		if (paramString.equalsIgnoreCase("the bridge")) {
			str = "the-bridge-2013";
		}
		if ((paramString.equalsIgnoreCase("2 chicas sin blanca"))
				|| (paramString.equalsIgnoreCase("dos chicas sin blanca"))) {
			str = "2-broke-girls";
		}
		if (paramString.equalsIgnoreCase("sobrenatural")) {
			str = "supernatural";
		}
		if (paramString.equalsIgnoreCase("once upon a time")) {
			str = "once-upon-a-time-2011";
		}
		if (paramString.equalsIgnoreCase("wilfred")) {
			str = "wilfred-us";
		}
		if (paramString.equalsIgnoreCase("Walking dead")) {
			str = "the-walking-dead";
		}
		if (paramString.equalsIgnoreCase("cronicas vampiricas")) {
			str = "the-vampire-diaries";
		}
		if ((paramString.equalsIgnoreCase("agents of shield"))
				|| (paramString.equalsIgnoreCase("agentes de shield"))
				|| (paramString.equalsIgnoreCase("Marvel Agents of Shield"))) {
			str = "marvels-agents-of-shield";
		}
		if (paramString.equalsIgnoreCase("masterchef")) {
			str = "masterchef-us";
		}

		return str;
	}

}
