package com.alk.receiptcap_v03;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

public class OCRActivity extends Activity {

	public static ImageView imageViewer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_view);
		getActionBar().show();

		imageViewer = (ImageView) findViewById(R.id.imageViewer);
		EditText _field = (EditText) findViewById(R.id.editText);

		final String DATA_PATH = Environment.getExternalStorageDirectory()
				.toString() + "/assets/";
		String lang = "fin";

		final String TAG = "OCRActivity.java";

		Intent OCRIntent = getIntent();

		String filePath = OCRIntent.getStringExtra("filePath");
		Bitmap OCRbitmap = (Bitmap) OCRIntent.getParcelableExtra("BitmapImage");

		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };
		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path
							+ " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}
		}

		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata"))
				.exists()) {
			try {
				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang
						+ ".traineddata");
				// GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/"
						+ lang + ".traineddata");
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				// while ((lenf = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				// gin.close();
				out.close();
				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG,
						"Was unable to copy " + lang + " traineddata "
								+ e.toString());
			}
		}

		/*
		 * DisplayMetrics metrics = getResources().getDisplayMetrics();
		 * System.out.println(metrics);
		 */

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		OCRbitmap = BitmapFactory.decodeFile(filePath, options);
		try {
			ExifInterface exif = new ExifInterface(filePath);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			Log.v(TAG, "Orient: " + exifOrientation);
			int rotate = 0;
			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}
			Log.v(TAG, "Rotation: " + rotate);
			if (rotate != 0) {
				// Getting width & height of the given image.
				int w = OCRbitmap.getWidth();
				int h = OCRbitmap.getHeight();
				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);
				// Rotating Bitmap
				OCRbitmap = Bitmap.createBitmap(OCRbitmap, 0, 0, w, h, mtx,
						false);
			}
			// Convert to ARGB_8888, required by tess
			OCRbitmap = OCRbitmap.copy(Bitmap.Config.ARGB_8888, true);
		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
		}

		imageViewer.setImageBitmap(OCRbitmap);
		Log.v(TAG, "Before baseApi");
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, "eng");
		baseApi.init(DATA_PATH, "fin");
		baseApi.init(DATA_PATH, "swe");
		baseApi.setImage(OCRbitmap);
		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();
		// You now have the text in recognizedText var, you can do anything with
		// it.
		// We will display a stripped out trimmed alpha-numeric version of it
		// (if lang is eng)
		// so that garbage doesn't make it to the display.
		Log.v(TAG, "OCRED TEXT: " + recognizedText);
		if (lang.equalsIgnoreCase("eng")) {
			String whiteList = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0abcdefghijklmnopqrstuvxyz123456789,.%-";
			String blackList = "<>#?ŒŠš;:/";
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, whiteList);
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, blackList);

			recognizedText = recognizedText.replaceAll(blackList, "");// remove
																		// space
		}
		if (lang.equalsIgnoreCase("fin")) {
			String whiteList = "ABCDEFGHIJKLMNOPQRSTUVWXYZ€…0abcdefghijklmnopqrstuvxyzŒŠš123456789,.%-";
			String blackList = "<>#?;:/()";
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, whiteList);
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, blackList);

			recognizedText = recognizedText.replaceAll(blackList, "");// remove
																		// space
			// recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9Šš]+",
			// " ");

		}
		if (lang.equalsIgnoreCase("swe")) {
			String whiteList = "ABCDEFGHIJKLMNOPQRSTUVWXYZ€…0abcdefghijklmnopqrstuvxyzŒŠš123456789,.%-";
			String blackList = "<>#?;:/()";
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, whiteList);
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, blackList);

			recognizedText = recognizedText.replaceAll(blackList, "");// remove
																		// space
		}
		recognizedText = recognizedText.trim();
		if (recognizedText.length() != 0) {
			_field.setText(_field.getText().toString().length() == 0 ? recognizedText
					: _field.getText() + " " + recognizedText);
			_field.setSelection(_field.getText().toString().length());
		}
	}
}
