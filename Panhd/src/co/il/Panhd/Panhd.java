package co.il.Panhd;

import java.io.File;
//import java.net.URLEncoder;

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
//import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
//import net.londatiga.android.*;


public class Panhd extends Activity {

	int TAKE_PICTURE = 235;
	int EMAIL_SENT = 216;
	private Uri outputFileUri;
	private static FacebookConnector fbConnector;
	protected static Handler mFacebookHandler;
	private boolean facebookConnectorStarted = false;
	private static SharedPreferences preferences;
	private static Activity activity;
	private SubMenu fileMenu;
//	private WebView likewebview;
//	private String likeURL;
	
	private static final int FacebookConst = 123;

	private static final int LOG_IN = 666;
	private static final int LOG_OUT = LOG_IN + 1;
	private static final int POST = LOG_IN + 2;
//	private static final int LIKE = LOG_IN + 3;
	
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
		final Thread camerathread = new Thread(){
	           @Override
	           public void run() {
	        	   String fileName = "PanhdPicture.jpg";
	        	   Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	        	   File file = new File(Environment.getExternalStorageDirectory(), fileName);
	        	   outputFileUri = Uri.fromFile(file);
	        	   cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	        	   try {
					startActivityForResult(cameraintent, TAKE_PICTURE);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
							try {
								Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
								emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.myemail)});
								emailIntent.setType("*/*");
								startActivity(emailIntent);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
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
		Button start = (Button) findViewById(R.id.mainStartButton);
		start.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				camerathread.start();
			}});
		start.setMinHeight(Hpixels/4);
		start.setMinWidth(Wpixels/2);
		// ** End of Camera Button
		
		if (FacebookBoolean){
			fbConnector = new FacebookConnector(this, getBaseContext());
			facebookConnectorStarted = true;
		}
		
//		likewebview = (WebView) findViewById(R.id.likewebview);
//		likewebview.getSettings().setJavaScriptEnabled(true);
//		likewebview.setWebViewClient(new HelloWebViewClient());
//        
//        likeURL ="http://www.facebook.com/plugins/like.php?" +
//        "href=" + URLEncoder.encode( "http://developers.facebook.com/docs/opengraph/" )
//        + "&" +
//        "layout=button_count&" +
//        "show_faces=0&" +
//        "width=90&" +
//        "height=24&" +
//        "locale=en_IN" +
//        "colorscheme=light" ;

		
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
		fileMenu = menu.addSubMenu("Facebook");
	    fileMenu.add(FacebookConst, LOG_IN, 0, "Log in");
	    fileMenu.add(FacebookConst, LOG_OUT, 1, "Log out");
	    fileMenu.add(FacebookConst, POST, 2, "Post something");
//	    fileMenu.add(FacebookConst, LIKE, 3, "Like my album");
	    fileMenu.setIcon(R.drawable.facebookicon72);
	    
	    modifySubMenu();
		
	    MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		modifySubMenu();
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
				
			case LOG_IN:
				if (facebookConnectorStarted){
					fbConnector.login(isFirstLaunch(), true);
					return true;
				} else {
					startFacebook();
					return true;
				}
				
		    case LOG_OUT:
		    	if (facebookConnectorStarted){
					if (fbConnector.isLoggedIn()){
						logOutInThread();
						return true;
					} else {
						showToast("You are not logged in to Facebook.");
						return true;
					}
				}
		        return true;
		        
		    case POST:
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
				
//		    case LIKE:
//		    	likewebview.loadUrl( likeURL );
//		    	return true;
				
			default:
				return super.onOptionsItemSelected(item);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void modifySubMenu() {
		try {
			// Logged out
			fileMenu.findItem(LOG_IN).setEnabled(true);
			fileMenu.findItem(LOG_OUT).setEnabled(false);
			fileMenu.findItem(POST).setEnabled(false);
			
			fileMenu.setHeaderIcon(getResources().getDrawable(R.drawable.loggedout64));
			fileMenu.setHeaderTitle("Logged out");
			if (facebookConnectorStarted){
				if (fbConnector.isLoggedIn()){
					// Logged in
					fileMenu.findItem(LOG_IN).setEnabled(false);
					fileMenu.findItem(LOG_OUT).setEnabled(true);
					final boolean FacebookPost = preferences.getBoolean("wallpost", false);
					fileMenu.findItem(POST).setEnabled(FacebookPost);
						
					fileMenu.setHeaderTitle("Logged in");
					fileMenu.setHeaderIcon(getResources().getDrawable(R.drawable.loggedin64));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
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
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	// Like WebView
//	private class HelloWebViewClient extends WebViewClient {
//	    @Override
//	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//	        view.loadUrl(url);
//	        return true;
//	    }
//	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//	    if ((keyCode == KeyEvent.KEYCODE_BACK) && likewebview.canGoBack()) {
//	    	likewebview.goBack();
//	        return true;
//	    }
//	    return super.onKeyDown(keyCode, event);
//	}
	   
	// ** End of Facebook
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		
		super.onActivityResult(requestCode, resultCode, data);
		
		final boolean SendBoolean = preferences.getBoolean("sendpicture", true);
		final boolean FacebookPost = preferences.getBoolean("wallpost", false);
		
		try {
			if (requestCode == TAKE_PICTURE && SendBoolean){
				sendPicture();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		try {
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			emailIntent.setType("image/jpeg");
			emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email)});
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "New Panh'd!");
			emailIntent.putExtra(Intent.EXTRA_TEXT, "Hey Aviad, check out my new Panh'd!");
			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(outputFileUri.toString()));
			startActivityForResult(emailIntent, EMAIL_SENT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
