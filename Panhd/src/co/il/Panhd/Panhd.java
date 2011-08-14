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
import android.os.Handler;
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
import android.widget.Toast;
import net.londatiga.android.*;


public class Panhd extends Activity {

	int TAKE_PICTURE = 235;
	int EMAIL_SENT = 216;
	private Uri outputFileUri;
	private static FacebookConnector fbConnector;
	protected static Handler mFacebookHandler;
	private boolean facebookConnectorStarted = false;
	private static SharedPreferences preferences;
	private static Activity activity;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		
		activity = this;
		Display display = getWindowManager().getDefaultDisplay(); 
		int Wpixels = display.getWidth();
		int Hpixels = display.getHeight();
		mFacebookHandler = new Handler();	
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		final boolean FacebookBoolean = preferences.getBoolean("facebooklogin", false);
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
							emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.myemail)});
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


		// ** "Start the camera" Button
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
		
		if (FacebookBoolean){
			fbConnector = new FacebookConnector(this, getBaseContext());
			facebookConnectorStarted = true;
		}
	}

	// ** End of OnCreate //////////////////////////////////////////////////////////////////////////////
	
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
		return super.onCreateOptionsMenu(menu);
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
				
			case R.id.menufacebook:
				
//				// Creating the popups
//				// Add Status item
//				ActionItem statusAction = new ActionItem();
//				statusAction.setTitle("Logged out");
//				statusAction.setIcon(getResources().getDrawable(R.drawable.loggedout64));
//				if (facebookConnectorStarted){
//					if (fbConnector.isLoggedIn()){
//						statusAction.setTitle("Logged in");
//						statusAction.setIcon(getResources().getDrawable(R.drawable.loggedin64));
//					}
//				} 
//				
//				//Add action item
//				ActionItem addAction = new ActionItem();
//				addAction.setTitle("Log in");
//				addAction.setIcon(getResources().getDrawable(R.drawable.loginicon64));
//				 
//				//Accept action item
//				ActionItem accAction = new ActionItem();
//				accAction.setTitle("Log out");
//				accAction.setIcon(getResources().getDrawable(R.drawable.logouticon64));
//				 
//				//Upload action item
//				ActionItem upAction = new ActionItem();
//				upAction.setTitle("Post");
//				upAction.setIcon(getResources().getDrawable(R.drawable.posticon64));
//				
//				final QuickAction mQuickAction  = new QuickAction(this);
//				 
//				mQuickAction.addActionItem(statusAction);
//				mQuickAction.addActionItem(addAction);
//				mQuickAction.addActionItem(accAction);
//				mQuickAction.addActionItem(upAction);
//				mQuickAction.animateTrack(true);
//				
//				//setup the action item click listener
//				mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
//				    @Override
//				        public void onItemClick(int pos) {
//				        if (pos == 1) { //Login item selected
//				        	if (facebookConnectorStarted){
//								fbConnector.login(isFirstLaunch(), true);
//							} else {
//								startFacebook();
//							}
//				        } else if (pos == 2) { //Logout item selected
//				        	if (facebookConnectorStarted){
//								if (fbConnector.isLoggedIn()){
//									logOutInThread();
//								} else {
//									showToast("You are not logged in to Facebook.");
//								}
//							}
//				        } else if (pos == 3) { //Post item selected
//				        	if (!facebookConnectorStarted) startFacebook();
//							if (!fbConnector.isLoggedIn()) fbConnector.login(isFirstLaunch(), true);
//							postMessageInThread("Yeah, it's coming...");
//				        }
//				    }
//				});
//				mQuickAction.show(activity.findViewById(R.id.mainAboutButton));
				
				
				
				return true;
			
			case R.id.menualbum:
				Uri uri = Uri.parse("http://www.facebook.com/media/set/?set=a.477553187029.255929.703267029&l=3a2b9f863b"); 
				Intent intent = new Intent(Intent.ACTION_VIEW, uri); 
				startActivity(intent); 
				return true;
				
			case R.id.login:
				if (facebookConnectorStarted){
					fbConnector.login(isFirstLaunch(), true);
				} else {
					startFacebook();
				}
				return true;
				
			case R.id.logout:
				if (facebookConnectorStarted){
					if (fbConnector.isLoggedIn()){
						logOutInThread();
					} else {
						showToast("You are not logged in to Facebook.");
					}
				}
				return true;
				
			case R.id.post:
				if (facebookConnectorStarted){
					if (fbConnector.isLoggedIn()){
						fbConnector.postDialog();
//						postMessageInThread("Yeah, it's coming...");
						return true;
					} else {
						showToast("Log in first...");
						return true;
					}
				} else {
					showToast("Log in first...");
					return true;
				}
				
			default:
				return super.onOptionsItemSelected(item);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return false;
	}

	// ** End of Menu
	
	// ** Facebook

	public static void postMessageInThread(final String msg) {
		Thread t = new Thread() {
			public void run() {
		    	try {
		    		fbConnector.postMessageOnWall(msg);
				} catch (Exception ex) {
				}
		    }
		};
		t.start();
	}

	private void logOutInThread() {
		Thread t = new Thread() {
			public void run() {
		    	try {
		    		fbConnector.logout();
					mFacebookHandler.post(mUpdateFacebookLogOutNotification);
				} catch (Exception ex) {
				}
		    }
		};
		t.start();
	}
	
	   final Runnable mUpdateFacebookLogOutNotification = new Runnable() {
	       public void run() {
	       	Toast.makeText(getBaseContext(), getString(R.string.FbLogOut), Toast.LENGTH_LONG).show();
	       }
	   };
	   
	// ** End of Facebook
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		
		super.onActivityResult(requestCode, resultCode, data);
		
		final boolean SendBoolean = preferences.getBoolean("sendpicture", true);
		final boolean FacebookPost = preferences.getBoolean("wallpost", false);
		
		if (requestCode == TAKE_PICTURE && SendBoolean){
			sendPicture();
		}
		
		if(requestCode==EMAIL_SENT)
	    {
	        if(resultCode==Activity.RESULT_OK){
	            Toast.makeText(this, "Mail sent", Toast.LENGTH_SHORT).show();
	            if (FacebookPost){
	            	postMessageInThread(getString(R.string.FbMailPost));
	            }
	        } else if (resultCode==Activity.RESULT_CANCELED){
	            Toast.makeText(this, "Mail canceled", Toast.LENGTH_SHORT).show();
	        }
	    }
		
		if (facebookConnectorStarted) fbConnector.authorizeCallback(requestCode, resultCode, data);
	}
	
	private void sendPicture() {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("image/jpeg");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email)});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "New Panh'd!");
		emailIntent.putExtra(Intent.EXTRA_TEXT, "Hey Aviad, check out my new Panh'd!");
		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(outputFileUri.toString()));
		startActivityForResult(emailIntent, EMAIL_SENT);
	}

	protected static void showToast(String message){
		Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
	}
	
	protected void startFacebook() {
		fbConnector = new FacebookConnector(this, getBaseContext());
		facebookConnectorStarted = true;
		
	}
	
	protected static boolean isFirstLaunch() {
        boolean isFirstLaunch = preferences.getBoolean("isFirstLaunch", true);
        return isFirstLaunch;
    }
	
}