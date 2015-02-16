package com.alk.receiptcap_v03;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class GalleryActivity extends MainActivity {

	private static final int PICK_FROM_GALLERY = 2;
	ImageView imageViewer;
	File imageFile = null;
	
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.photo_view);
		getActionBar().show();
		
		imageViewer = (ImageView) findViewById(R.id.imageViewer);

		Intent mIntent = getIntent();

		Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		
		
		galleryIntent.putExtra("crop", "true");
		galleryIntent.putExtra("aspectX", 0);
		galleryIntent.putExtra("aspectY", 0);
		galleryIntent.putExtra("outputX", 300);
		galleryIntent.putExtra("outputY", 400);

		try {
			

		galleryIntent.putExtra("return-data", true);
		startActivityForResult(galleryIntent, PICK_FROM_GALLERY);

		} catch (ActivityNotFoundException e) {
		// Do nothing for now
		}		

	}

	  @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	    }

	    @Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// Handle action bar item clicks here. The action bar will
			// automatically handle clicks on the Home/Up button, so long
			// as you specify a parent activity in AndroidManifest.xml.
	    	int id = item.getItemId();
	    	
			if (id == R.id.action_scan) {
				
			}
			
			if (id == R.id.action_edit) {
				Intent editIntent = new Intent (this, EditActivity.class);
				startActivity(editIntent);
			}
			
			if (id == R.id.action_delete) {
				//delete
			}
			if (id == R.id.action_exit) {
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("Save you edit");
				alert.setMessage("Are you sure you want to exit without saving? ");
				alert.setPositiveButton("No",new DialogInterface.OnClickListener() {

					                public void onClick(DialogInterface dialog,int id) {
					                    // go to a new activity of the app
					                	dialog.cancel();
					                }
					              });
					         // set negative button: No message
				alert.setNegativeButton("Yes",new DialogInterface.OnClickListener() {
					                public void onClick(DialogInterface dialog,int id) {
					                    // cancel the alert box and put a Toast to the user
					                	GalleryActivity.this.finish();
					                	moveTaskToBack (true);
					                    
					                }
					            });
					         
					         AlertDialog alertDialog = alert.create();
					          // show alert
					          alertDialog.show();

					         }
	    
			return super.onOptionsItemSelected(item);
			}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
			{
				Uri selectedImageURI = data.getData();
				imageFile = new File(getRealPathFromURI(selectedImageURI));
				Log.i("Path: ", getRealPathFromURI(selectedImageURI));
				
				Bundle extras = data.getExtras();
				if (extras != null) {
					final Bitmap galleryBitmap = extras.getParcelable("data");
					imageViewer.setImageBitmap(galleryBitmap);

					Intent OCRIntent = new Intent (GalleryActivity.this, OCRActivity.class);
					OCRIntent.putExtra("BitmapImage", galleryBitmap);
					OCRIntent.putExtra("filePath", getRealPathFromURI(selectedImageURI));
					startActivity(OCRIntent);

			}
		}
	}
	}

	private String getRealPathFromURI(Uri contentURI) {
		String result;
	    Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
	    if (cursor == null) {
	        result = contentURI.getPath();
	    } else { 
	        cursor.moveToFirst(); 
	        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	        result = cursor.getString(idx);
	        cursor.close();
	    }
	    return result;
	}
}
