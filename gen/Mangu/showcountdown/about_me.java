package Mangu.showcountdown;

import android.app.Activity;
import android.os.Bundle;

public class about_me extends Activity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(R.layout.about_me);
  }

  protected void onStop()
  {
    setContentView(R.layout.activity_my);
    super.onStop();
  }
}