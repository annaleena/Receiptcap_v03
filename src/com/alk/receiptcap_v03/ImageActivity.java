package com.alk.receiptcap_v03;
 
 
import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
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


public class ImageActivity extends Activity {
	
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_GALLERY = 2;
	public static ImageView imageViewer;
	Uri mCapturedImageURI = null;
	File imageFile = null;
	
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_view);
		getActionBar().show();
		
		imageViewer = (ImageView) findViewById(R.id.imageViewer);

		Intent imageIntent = getIntent();
		int choice = imageIntent.getIntExtra("selection", 0);
		
		if (choice == 1) {
			
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			cameraIntent.putExtra("crop", "true");
			cameraIntent.putExtra("aspectX", 0);
			cameraIntent.putExtra("aspectY", 0);
			cameraIntent.putExtra("outputX", 300);
			cameraIntent.putExtra("outputY", 400);
			
			String fileName = "temp.jpg";  
	        ContentValues values = new ContentValues();  
	        values.put(MediaStore.Images.Media.TITLE, fileName);  
	        mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  
	         
			
			try {

				cameraIntent.putExtra("return-data", true);
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI); 
				startActivityForResult(cameraIntent, PICK_FROM_CAMERA);

			} catch (ActivityNotFoundException e) {
				// Do nothing for now
			}
		}
		
		if (choice == 2) {
			
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
				//edit image
				
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
					                	ImageActivity.this.finish();
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

		if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
			{
				String[] projection = { MediaStore.Images.Media.DATA}; 
	            Cursor cursor = getContentResolver().query(mCapturedImageURI, projection, null, null, null); 
	            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
	            cursor.moveToFirst(); 
	            String capturedImageFilePath = cursor.getString(column_index_data);
	            Log.i("Path: ", capturedImageFilePath);
				
	            Bundle extras = data.getExtras();
				if (extras != null) {
					final Bitmap cameraBitmap = extras.getParcelable("data");
					imageViewer.setImageBitmap(cameraBitmap);
						
					Intent OCRIntent = new Intent (ImageActivity.this, OCRActivity.class);
					OCRIntent.putExtra("BitmapImage", cameraBitmap);
					OCRIntent.putExtra("filePath", capturedImageFilePath);
					startActivity(OCRIntent);
				}
			}
		}
		
		if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
			{
				Uri selectedImageURI = data.getData();
				imageFile = new File(getRealPathFromURI(selectedImageURI));
				Log.i("Path: ", getRealPathFromURI(selectedImageURI));
				
				Bundle extras = data.getExtras();
				if (extras != null) {
					final Bitmap galleryBitmap = extras.getParcelable("data");
					imageViewer.setImageBitmap(galleryBitmap);
					
					Intent OCRIntent = new Intent (ImageActivity.this, OCRActivity.class);
					OCRIntent.putExtra("BitmapImage", galleryBitmap);
					OCRIntent.putExtra("filePath", getRealPathFromURI(selectedImageURI));
					startActivity(OCRIntent);
			}
		}	
	}
}

	private void startOCR() {
		// TODO Auto-generated method stub	
		
	}

	private String getRealPathFromURI(Uri contentURI) {
		// TODO Auto-generated method stub
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
