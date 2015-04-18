package com.alk.receiptcap_v03;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

public class OCR extends OCRActivity {	

	final String recognizedText = null;
	private Bitmap bitmap;
	private String filePath;
	final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/assets/";
	final String TAG = "OCRActivity.java";
	private String lang = "fin";

	//OCR constructor
	public OCR(Bitmap bitmap, String filePath) {
		this.bitmap = bitmap;
		this.filePath = filePath;
	}
	
	//Run OCR
	public String run() {
		String ocrText = doOCR(bitmap);
		return ocrText;	
	}

	private String doOCR(Bitmap bm) {
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };
		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path
							+ " on sdcard failed");
					
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}
		}

		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {
				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
				OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/"
						+ lang + ".traineddata");
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG,
						"Was unable to copy " + lang + " traineddata "
								+ e.toString());
			}
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		
		bm = BitmapFactory.decodeFile(filePath, options);
		
		Bitmap newOCRbitmap = bm.copy(Bitmap.Config.ARGB_8888 ,true);
		Canvas canvas = new Canvas(newOCRbitmap);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		float contrast = 3.0f;
		float brightness = -173f;
		
		//Manipulate image to improve OCR result
		cm.set(new float[] {   
				contrast, 0, 0, 0, brightness,
			    0, contrast, 0, 0, brightness,
			    0, 0, contrast, 0, brightness,
			    0, 0, 0, 1, 0});

		paint.setColorFilter(new ColorMatrixColorFilter(cm));
		Matrix matrix = new Matrix();
		canvas.drawBitmap(bm, matrix, paint);

		//Orientation if needed
		try {
			ExifInterface exif = new ExifInterface(filePath);
			int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
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
				// Getting width & height of the OCRbitmap.
				int w = newOCRbitmap.getWidth();
				int h = newOCRbitmap.getHeight();
				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);
				// Rotating Bitmap
				newOCRbitmap = Bitmap.createBitmap(newOCRbitmap, 0, 0, w, h, mtx,
						false);
			}
			// Convert to ARGB_8888, required by tesseract
			newOCRbitmap = newOCRbitmap.copy(Bitmap.Config.ARGB_8888, true);
		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
		}
		
		imageViewer.setImageBitmap(bm);
		Log.v(TAG, "Before baseApi");
		
		//Set languages and approved characters
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, "eng");
		baseApi.init(DATA_PATH, "fin");
		baseApi.init(DATA_PATH, "swe");
		baseApi.setImage(newOCRbitmap);
		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();
		
		if (lang.equalsIgnoreCase("eng")) {
			String whiteList = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz1234567890,.%-";
			String blackList = "üûÜÛíÌìÍßøáÀàÁïÏéèÈØø<>#?åäö;:/()";
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, whiteList);
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, blackList);

			recognizedText = recognizedText.replaceAll(blackList, ""); // remove space											
		}
		
		if (lang.equalsIgnoreCase("fin") || lang.equalsIgnoreCase("swe")) {
			String whiteList = "ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖabcdefghijklmnopqrstuvxyzåäö1234567890,.%-";
			String blackList = "üûÜÛíÌìÍßøáÁàÀïÏéèÈØø<>#?;:/()";
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, whiteList);
			baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, blackList);

			recognizedText = recognizedText.replaceAll(blackList, "");// remove space
		}
		
		recognizedText = recognizedText.trim();
		return recognizedText;

	}

}
