package co.il.Panhd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.facebook.android.*;
import com.facebook.android.Facebook.*;

public class FacebookConnector{

	private static final String[] PERMISSIONS = new String[] {"publish_stream"};
	private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String KEY = "facebook-credentials";
    private Facebook facebook = new Facebook("131326620295351");
	private Activity activity;
	
	public FacebookConnector(Activity activity, Context context){
		
		this.activity = activity;
		
		facebook.authorize(activity, PERMISSIONS,
			      new DialogListener() {
			           @Override
			           public void onComplete(Bundle values) {}
			           @Override
			           public void onFacebookError(FacebookError error) {}
			           @Override
			           public void onError(DialogError e) {}
			           @Override
			           public void onCancel() {}
			      }
			);
		
		restoreCredentials(facebook);
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
	
	public void login() {
	       if (!facebook.isSessionValid()) {
	           facebook.authorize(this.activity, PERMISSIONS,Facebook.FORCE_DIALOG_AUTH,new LoginDialogListener());
	       } else {
	    	   showToast("You are already Logged in to Facebook.");
	       }
	   }


	public void postMessageOnWall(String message){
		if (facebook.isSessionValid()){
			Bundle parameters = new Bundle();
	        parameters.putString("message", message);
	        try {
	        	String response = facebook.request("me/feed", parameters,"POST");
	        	System.out.println(response);
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
		} else {
			login();
		}
	}
	
	public void logout() {
		if (facebook.isSessionValid()){
	        try {
	        	facebook.logout(activity);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			showToast("You are not logged in to Facebook.");
		}
	}

	class LoginDialogListener implements DialogListener {
	    public void onComplete(Bundle values) {
	    	saveCredentials(facebook);
	    	showToast("Logged in to Facebook.");
	    }
	    public void onFacebookError(FacebookError error) {
	    	showToast("Authentication with Facebook failed.");
	    }
	    public void onError(DialogError error) {
	    	showToast("Authentication with Facebook failed.");
	    }
	    public void onCancel() {
	    	showToast("Authentication with Facebook cancelled.");
	    }
	}

	class WallPostDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
            		final String postId = values.getString("post_id");
            		if (postId != null) {
            		showToast("Message posted to your facebook wall.");
            	} else {
            		showToast("Wall post cancelled.");
            	}
        	}
		public void onFacebookError(FacebookError e) {
			showToast("Failed to post to wall.");
			e.printStackTrace();
		}
		public void onError(DialogError e) {
			showToast("Failed to post to wall.");
			e.printStackTrace();
		}
		public void onCancel() {
			showToast("Wall post cancelled.");
		}
    }
	
	public void authorizeCallback(int requestCode, int resultCode, Intent data) {
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
	
	private void showToast(String message){
		Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
	}
}
