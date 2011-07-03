package co.il.Panhd;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
 
public class splash extends Activity {
	
	int TAKE_PICTURE = 235;
	private Uri outputFileUri;
	
   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main);
      
		try {
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
			          dialog.show(); 
				}
				catch (WindowManager.BadTokenException e){
					e.getCause();
				}
				catch (ClassCastException e){
					e.getCause();
				}
			}});
	// ** End of Camera Button
		
		
	// ** Start the camera Button
		Button start = (Button) this.findViewById(R.id.mainStartButton);
		start.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
		        String fileName = "PanhdPicture.jpg";
		        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		        File file = new File(Environment.getExternalStorageDirectory(), fileName);
		        outputFileUri = Uri.fromFile(file);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
				startActivityForResult(intent, TAKE_PICTURE);
			}});
		
	// ** End of Camera Button
		
		
	// ** URL link Button
		Button urlbutton = (Button) this.findViewById(R.id.mainAlbumLink);
		urlbutton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Uri uri = Uri.parse("http://www.facebook.com/media/set/?set=a.477553187029.255929.703267029&l=3a2b9f863b"); 
				Intent intent = new Intent(Intent.ACTION_VIEW, uri); 
				startActivity(intent); 
				
			}});
		
	// ** End of URL link
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
}