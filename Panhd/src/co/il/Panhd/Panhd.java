package co.il.Panhd;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class Panhd extends Activity {

	int TAKE_PICTURE = 235;
	private Uri outputFileUri;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Display display = getWindowManager().getDefaultDisplay(); 
		int Wpixels = display.getWidth();
		int Hpixels = display.getHeight();
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		final boolean CameraBoolean = preferences.getBoolean("startcamera", false);
		 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		 // ** Starting the Camera, and calling the result with TAKE_PICTURE
		Thread camerathread = new Thread(){
	           @Override
	           public void run() {
	        	   String fileName = "PanhdPicture.jpg";
	        	   Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	        	   File file = new File(Environment.getExternalStorageDirectory(), fileName);
	        	   outputFileUri = Uri.fromFile(file);
	        	   cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	        	   startActivityForResult(cameraintent, TAKE_PICTURE);
	           }
 		};
		
		if (CameraBoolean){
    		camerathread.start();
    	}
	// ** End of Camera
		
		// ** About Button
		ImageButton about = (ImageButton) this.findViewById(R.id.mainAboutButton);
		about.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				try{
					final Dialog dialog = new Dialog(view.getContext()); 
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.about);

					Button close = (Button) dialog.findViewById(R.id.aboutCloseButton1);
					close.setOnClickListener(new OnClickListener()
					{                
						public void onClick(View v)
						{
							dialog.dismiss(); 
						} 
					});
					// Email Button
					ImageButton email = (ImageButton) dialog.findViewById(R.id.emailbutton);
					email.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
							emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"Aviadpi@gmail.com"});
							emailIntent.setType("*/*");
							startActivity(emailIntent);
						}
					});
					//Facebook Button
					ImageButton facebook = (ImageButton) dialog.findViewById(R.id.facebookbutton);
					facebook.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Uri uri = Uri.parse("http://www.facebook.com/aviad.panhi"); 
							Intent intent = new Intent(Intent.ACTION_VIEW, uri); 
							startActivity(intent); 
						}
					});

					dialog.show(); 
				}
				catch (WindowManager.BadTokenException e){
					e.getCause();
				}
				catch (ClassCastException e){
					e.getCause();
				}
				catch (NullPointerException e){
					e.getCause();
				}
				catch (ActivityNotFoundException e){
					e.getCause();
				}
			}});
		// ** End of About Button


		// ** Start the camera Button
		Button start = (Button) this.findViewById(R.id.mainStartButton);
		start.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				String fileName = "PanhdPicture.jpg";
				Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File file = new File(Environment.getExternalStorageDirectory(), fileName);
				outputFileUri = Uri.fromFile(file);
				cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
				startActivityForResult(cameraintent, TAKE_PICTURE);
			}});
		start.setMinHeight(Hpixels/4);
		start.setMinWidth(Wpixels/2);
		// ** End of Camera Button
	}

	// ** Settings
	@Override
	protected void onStop(){
		super.onStop();

		// Change preferences
		SharedPreferences settings = getSharedPreferences("Prefs", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("booleanParam", true);

		// Save changes
		editor.commit();       

	}
	// ** End of Settings
	
	// ** Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		try {
			switch (item.getItemId()) {
			
			case R.id.menupreferences:
				Intent settingsActivity = new Intent(this.getBaseContext() ,Preferences.class);
				startActivity(settingsActivity);
				return true;
				
			case R.id.menualbum:
				Uri uri = Uri.parse("http://www.facebook.com/media/set/?set=a.477553187029.255929.703267029&l=3a2b9f863b"); 
				Intent intent = new Intent(Intent.ACTION_VIEW, uri); 
				startActivity(intent); 
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	// ** End of Menu

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		final boolean SendBoolean = preferences.getBoolean("sendpicture", false);
		
		if (requestCode == TAKE_PICTURE && SendBoolean){
			// Email the picture
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			emailIntent.setType("image/jpeg");
			emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"aviadpi@gmail.com"});
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "New Panh'd!");
			emailIntent.putExtra(Intent.EXTRA_TEXT, "Hey Aviad, check out my new Panh'd!");
			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(outputFileUri.toString()));
			startActivity(emailIntent);
		}
	}
}