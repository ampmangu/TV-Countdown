package Mangu.showcountdown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;


public class BackgroundDownload extends AsyncTask<String, Integer, String> {
	
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		/*ProgressDialog progressDialogSearching = new ProgressDialog();
		progressDialogSearching
				.setProgress(ProgressDialog.STYLE_SPINNER);
		progressDialogSearching.setMessage(getText(R.string.wait));
		progressDialogSearching.setMax(100);
		progressDialogSearching.show();*/
	}

	@Override
	protected String doInBackground(String... arg0) {
		String input="";
		try {
			URL url = new URL(arg0[0]);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader buff = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String text;
			while((text = buff.readLine())!=null) {
				input+=text;
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
