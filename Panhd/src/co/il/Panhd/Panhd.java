package co.il.Panhd;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Panhd extends Activity {
    /** Called when the activity is first created. */
	int TAKE_PICTURE = 235;
	int SPLASH_TIME = 2000;
	private Uri outputFileUri;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    // ** Screen Splash
    	setContentView(R.layout.splash);
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
                                "co.il.Panhd.splash");
                 startActivity(i);
              }
           }
        };
        splashThread.start();
    // ** End of Splash Screen
        
    // ** Starting the Camera, and calling the result with TAKE_PICTURE
        
        String fileName = "PanhdPicture.jpg";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        outputFileUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		
		// ENABLE THIS BEFORE RELEASE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		startActivityForResult(intent, TAKE_PICTURE);
		
	// ** End of Camera
		
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
 
		if (requestCode == TAKE_PICTURE){
			// Email the picture
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			emailIntent.setType("image/jpeg");
			emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"aviadpi@gmail.com"});
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "New Panh'd!");
			emailIntent.putExtra(Intent.EXTRA_TEXT, "Hey Aviad, check out my new Panh'd!");
			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(outputFileUri.toString()));
			startActivity(Intent.createChooser(emailIntent, "Send Email"));
		}
	}
    
    static void createAlertDialog(String msg, Context context, String buttonText) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(msg);
		builder.setCancelable(true);
		builder.setNegativeButton(buttonText,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
    
}