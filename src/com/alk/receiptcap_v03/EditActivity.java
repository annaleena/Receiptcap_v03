package com.alk.receiptcap_v03;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;

public class EditActivity extends Activity {
	
	ImageView imageViewer;
	 private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
     private ColorMatrix mColorMatrix = new ColorMatrix();
     private Bitmap mBitmap;
     private float mSaturation;
     private float mAngle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_view);
		getActionBar().show();
     
		imageViewer = (ImageView) findViewById(R.id.imageViewer);

		Intent mIntent = getIntent();
		Bitmap bitmap = (Bitmap) mIntent.getParcelableExtra("BitmapImage");
		imageViewer.setImageBitmap(bitmap);
	}
	
	private static void setContrastScaleOnly(ColorMatrix cm, float contrast) {
        float scale = contrast + 1.f;
           float translate = (-.5f * scale + .5f) * 255.f;
        cm.set(new float[] {
               scale, 0, 0, 0, 0,
               0, scale, 0, 0, 0,
               0, 0, scale, 0, 0,
               0, 0, 0, 1, 0 });
    }
   private static void setContrast(ColorMatrix cm, float contrast) {
        float scale = contrast + 1.f;
           float translate = (-.5f * scale + .5f) * 255.f;
        cm.set(new float[] {
               scale, 0, 0, 0, translate,
               0, scale, 0, 0, translate,
               0, 0, scale, 0, translate,
               0, 0, 0, 1, 0 });
    }

    private static void setContrastTranslateOnly(ColorMatrix cm, float contrast) {
        float scale = contrast + 1.f;
           float translate = (-.5f * scale + .5f) * 255.f;
        cm.set(new float[] {
               1, 0, 0, 0, translate,
               0, 1, 0, 0, translate,
               0, 0, 1, 0, translate,
               0, 0, 0, 1, 0 });
    }

   protected void onDraw(Canvas canvas) {
        Paint paint = mPaint;
        float x = 20;
        float y = 20;

        canvas.drawColor(Color.WHITE);

        paint.setColorFilter(null);
        canvas.drawBitmap(mBitmap, x, y, paint);

        ColorMatrix cm = new ColorMatrix();

        mAngle += 2;
        if (mAngle > 180) {
            mAngle = 0;
        }

        //convert our animated angle [-180...180] to a contrast value of [-1..1]
        float contrast = mAngle / 180.f;

        setContrast(cm, contrast);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(mBitmap, x + mBitmap.getWidth() + 10, y, paint);

        setContrastScaleOnly(cm, contrast);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(mBitmap, x, y + mBitmap.getHeight() + 10, paint);

        setContrastTranslateOnly(cm, contrast);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(mBitmap, x, y + 2*(mBitmap.getHeight() + 10),
                          paint);

      //  invalidate();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

}