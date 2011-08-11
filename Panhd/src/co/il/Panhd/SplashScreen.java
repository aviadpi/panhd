package co.il.Panhd;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SplashScreen extends Activity {
    /** Called when the activity is first created. */
	int SPLASH_TIME = 2000;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	// Get the preferences
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		final boolean SplashBoolean = preferences.getBoolean("splashscreen", true);
    	
    // ** Screen Splash
    	setContentView(R.layout.splash);
    	
    	if (SplashBoolean == false){
    		Thread splashThread = new Thread() {
 	           @Override
 	           public void run() {
 	        	   finish();
 	        	   Intent i = new Intent();
 	        	   i.setClassName("co.il.Panhd",
 	        	   "co.il.Panhd.Panhd");
 	        	   startActivity(i);
 	           }
 	        };
 	        splashThread.start();
    	} else {
    		Thread splashThread = new Thread() {
    	           @Override
    	           public void run() {
    	              try {
    	                 int waited = 0; 
    	                 while (waited < SPLASH_TIME) {
    	                    sleep(100);
    	                    waited += 100;
    	                 }
    	              } catch (InterruptedException e) {
    	                 // do nothing
    	              } finally {
    	                 finish();
    	                 Intent i = new Intent();
    	                 i.setClassName("co.il.Panhd",
    	                                "co.il.Panhd.Panhd");
    	                 startActivity(i);
    	              }
    	           }
    	        };
    	        splashThread.start();
   	}
    // ** End of Splash Screen
    }
}