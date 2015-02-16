package com.alk.receiptcap_v03;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;


public class MainActivity extends Activity implements OnClickListener {

	  
	private Button cameraButton, galleryButton;
	//private int selection;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
		setContentView(R.layout.activity_main);

		 cameraButton = (Button) this.findViewById(R.id.cameraButton);
		 galleryButton = (Button) this.findViewById(R.id.galleryButton);
		 
		 //Here we choose to take a new picture
		 cameraButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					launchCamera();
				}});
		 
		// Here we choose a photo from the gallery
		galleryButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openGallery();
				}
			}); 

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
			//ORC
		}
		
		if (id == R.id.action_edit) {
		// edit image
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
				                	MainActivity.this.finish();
				                	moveTaskToBack (true);
				                    
				                }
				            });
				         
				         AlertDialog alertDialog = alert.create();
				          // show alert
				          alertDialog.show();

				         }
    
		return super.onOptionsItemSelected(item);
		}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	//method to open camera
	public void launchCamera() {
		Intent cameraIntent = new Intent(MainActivity.this, ImageActivity.class);
		cameraIntent.putExtra("selection", 1);
		startActivity(cameraIntent);
	}
	
	public void openGallery() {
		Intent galleryIntent = new Intent(MainActivity.this, ImageActivity.class);
		galleryIntent.putExtra("selection", 2);
		startActivity(galleryIntent);
	}
}