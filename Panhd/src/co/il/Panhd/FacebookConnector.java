package co.il.Panhd;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.facebook.android.*;
import com.facebook.android.Facebook.*;

public class FacebookConnector{

	private static final String[] PERMISSIONS = new String[] {"publish_stream"};
	private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String KEY = "facebook-credentials";
    private boolean FacebookPost;
    private Facebook facebook = new Facebook("131326620295351");
    private AsyncFacebookRunner mAsyncRunner;
	private Activity activity;
	private boolean isLoggedIn;
	private SharedPreferences preferences;
	private String messageToPost;
	
	public FacebookConnector(Activity activity, Context context){
		
		this.activity = activity;
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		restoreCredentials(facebook);
		
//		facebook.authorize(this.activity, PERMISSIONS ,new LoginDialogListener());
		facebook.authorize(this.activity, PERMISSIONS,Facebook.FORCE_DIALOG_AUTH,new LoginDialogListener());
		
		preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		FacebookPost = preferences.getBoolean("wallpost", false);
				
	}
	
	public boolean saveCredentials(Facebook facebook) {
    	Editor editor = activity.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
    	editor.putString(TOKEN, facebook.getAccessToken());
    	editor.putLong(EXPIRES, facebook.getAccessExpires());
    	return editor.commit();
	}

	public boolean restoreCredentials(Facebook facebook) {
    	SharedPreferences sharedPreferences = activity.getSharedPreferences(KEY, Context.MODE_PRIVATE);
    	facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
    	facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
    	return facebook.isSessionValid();
	}

	public void postMessageOnWall(String message){
		messageToPost = message;
		if (facebook.isSessionValid()){
			if (isLoggedIn){
				Bundle parameters = new Bundle();
		        parameters.putString("message", messageToPost);
		        parameters.putString("link", "http://www.facebook.com/media/set/?set=a.477553187029.255929.703267029&l=3a2b9f863b");
		        try {
		        	String response = facebook.request("me/feed", parameters,"POST");
		        	System.out.println(response);
//		        	Panhd.showToast(activity.getString(R.string.FbPostMsg));
		        } catch (IOException e) {
		        	e.printStackTrace();
		        }
			} else {
				loginAndPost(Panhd.isFirstLaunch(), false);
			}
		}
	}
	
	public void login(boolean isFirstLaunch, boolean force) {
	    	if (isLoggedIn){
	    		showToast("You are already logged in to Facebook");
	    	}else{
	    		if (force){
		    		   facebook.authorize(this.activity, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH ,new LoginDialogListener());
		    	   }else{
		    		   facebook.authorize(this.activity, PERMISSIONS , new LoginDialogListener());
		    	   }
	    	}
	}
	
	public void loginAndPost(boolean isFirstLaunch, boolean force) {
    	if (isLoggedIn){
    		showToast("You are already logged in to Facebook");
    	}else{
    		if (force){
	    		   facebook.authorize(this.activity, PERMISSIONS,Facebook.FORCE_DIALOG_AUTH,new LoginDialogAndPostListener());
	    	   }else{
	    		   facebook.authorize(this.activity, PERMISSIONS ,new LoginDialogAndPostListener());
	    	   }
    	}
}

	public void logout() {
		if (facebook.isSessionValid()){
	        if (isLoggedIn){
	        	try {
		        	facebook.logout(activity);
			    	setLoggedIn(false);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        } else {
	        	showToast("You are not logged in to Facebook");
	        }
	        
		} else {
			showToast("Facebook session is invalid");
		}
	}

	class LoginDialogListener implements DialogListener {
	    public void onComplete(Bundle values) {
	    	saveCredentials(facebook);
	    	showToast("Logged in to Facebook");
	    	setLoggedIn(true);
	    	if (Panhd.isFirstLaunch() && FacebookPost){
	    		postFirstTime();
	    	}
	    }
	    public void onFacebookError(FacebookError error) {
	    	showToast("Facebook Authentication failed");
	    }
	    public void onError(DialogError error) {
	    	showToast("Facebook Authentication failed");
	    }
	    public void onCancel() {
	    	showToast("Facebook Authentication cancelled");
	    }
	}

	class LoginDialogAndPostListener implements DialogListener {
	    public void onComplete(Bundle values) {
	    	saveCredentials(facebook);
	    	showToast("Logged in to Facebook");
	    	setLoggedIn(true);
	    	if (Panhd.isFirstLaunch() && FacebookPost){
	    		postFirstTime();
	    	}
	    	postMessageOnWall(messageToPost);
	    }
	    public void onFacebookError(FacebookError error) {
	    	showToast("Facebook Authentication failed");
	    }
	    public void onError(DialogError error) {
	    	showToast("Facebook Authentication failed");
	    }
	    public void onCancel() {
	    	showToast("Facebook Authentication cancelled");
	    }
	}
	
//	class WallPostDialogListener implements DialogListener {
//		public void onComplete(Bundle values) {
//            		final String postId = values.getString("post_id");
//            		if (postId != null) {
//            		showToast(activity.getString(R.string.FbPostMsg));
//            	} else {
//            		showToast("Wall post cancelled.");
//            	}
//        	}
//		public void onFacebookError(FacebookError e) {
//			showToast("Failed to post to wall.");
//			e.printStackTrace();
//		}
//		public void onError(DialogError e) {
//			showToast("Failed to post to wall.");
//			e.printStackTrace();
//		}
//		public void onCancel() {
//			showToast("Wall post cancelled.");
//		}
//    }
	
	public void authorizeCallback(int requestCode, int resultCode, Intent data) {
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
	
	public void postFirstTime() {
		Panhd.postMessageInThread(activity.getString(R.string.FbfirstPost));
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("isFirstLaunch", false);
		editor.commit();
	}
	
	private void showToast(String message){
		Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
	}

	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

}
