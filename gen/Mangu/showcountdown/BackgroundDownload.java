package Mangu.showcountdown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;

public class BackgroundDownload extends AsyncTask<String, Integer, String> {
	@Override
	protected String doInBackground(String... arg0) {
		String input = "";
		try {
			URL url = new URL(arg0[0]);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			BufferedReader buff = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			String text;
			while ((text = buff.readLine()) != null) {
				input += text;
			}
			buff.close();
			

		} catch (MalformedURLException localMalformedURLException) {
			localMalformedURLException.printStackTrace();
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
		return input;
	}

	

}
