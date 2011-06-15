package co.il.Panhd;

import java.io.File;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Panhd extends Activity {
    /** Called when the activity is first created. */
	int TAKE_PICTURE = 235;
	private Uri outputFileUri;
	
	
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //define the file-name to save photo taken by Camera activity
        String fileName = "PanhdPicture.jpg";
        //create new Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        outputFileUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, TAKE_PICTURE);
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
 
		if (requestCode == TAKE_PICTURE){
//			Toast.makeText(getApplicationContext(), outputFileUri.toString(), 1);
//			Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
		//	TextView header = (TextView) get
			
			// Email the picture
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"aviadpi@gmail.com"});
			intent.putExtra(Intent.EXTRA_SUBJECT, "New Panh'd!");
			intent.putExtra(Intent.EXTRA_TEXT, "Hey Aviad, check out my new Panh'd!");
			//intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(outputFileUri.toString()));
			intent.setType("vnd.android.cursor.dir/email"); 
			startActivity(Intent.createChooser(intent, "Send Email"));
			
		}
	}
    
    
}