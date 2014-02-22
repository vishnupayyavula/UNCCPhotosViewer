package com.example.unccharlottephotos;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.LruCache;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

@SuppressLint({ "ValidFragment", "NewApi" })
public class ImageViewerActivity extends Activity {
	private int[] thumbs = { R.array.uncc_thumbs, R.array.football_thumbs,
			R.array.ifest_thumbs, R.array.commencement_thumbs };
	private int[] images = { R.array.uncc_photos, R.array.football_photos,
			R.array.ifest_photos, R.array.commencement_photos };
	ImageView imageView;
	private int thumbID;
	private String[] imageURLs;
	ProgressDialog load;
	private int imageListID;
	private int imageID;
	private String imgURL;
	Intent intent;
	GestureDetector gesture;
	int swipe_Min = 50;
	int swipe_Vel = 150;
	LruCache<String, Bitmap> cache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_viewer);
		load = new ProgressDialog(this);
		intent = new Intent(getBaseContext(), GalleryActivity.class);
		imageView = (ImageView) findViewById(R.id.imageView1);
		gesture = new GestureDetector(getBaseContext(), new GestureListener());
		int cacheSize = 4 * 1024 * 1024;
		
		cache = new LruCache<String, Bitmap>(cacheSize){
			protected int sizeOf(String key, Bitmap bitmap){
				return bitmap.getByteCount();
			}
			
		};

		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().containsKey(GalleryActivity.thumb)) {
				thumbID = getIntent().getExtras().getInt(GalleryActivity.thumb);
			}
			if (getIntent().getExtras().containsKey(GalleryActivity.image)) {
				imageID = getIntent().getExtras().getInt(GalleryActivity.image);
			}
		}

		for (int i = 0; i < 4; i++) {
			if (thumbID == thumbs[i]) {
				imageListID = images[i];
			}
		}

		imageURLs = getResources().getStringArray(imageListID);
		imgURL = imageURLs[imageID];
		imageWork(imgURL);

		imageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gdt.onTouchEvent(event);
				return true;
			}
		});

		Button btn1 = (Button) findViewById(R.id.button2);
		btn1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (imageID == 0) {
					imageID = imageURLs.length - 1;
				} else {
					imageID = imageID - 1;
				}
				imgURL = imageURLs[imageID];
				imageWork(imgURL);

			}
		});

		Button btn2 = (Button) findViewById(R.id.button3);
		btn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				intent.putExtra("Thumbs", thumbID);
				startActivity(intent);

			}
		});

		Button btn3 = (Button) findViewById(R.id.button4);
		btn3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (imageID == imageURLs.length - 1) {
					imageID = 0;
				} else {
					imageID = imageID + 1;
				}
				imgURL = imageURLs[imageID];
				imageWork(imgURL);

			}
		});

	}
	
	public void addBitmapToMemory(String key, Bitmap bitmap){
		if(getBitmapFromMemCache(key)==null){
			cache.put(key, bitmap);
		}
	}
	
	public Bitmap getBitmapFromMemCache(String key){
		return cache.get(key);
	}

	public void imageWork(String imageURL) {
		load.setMessage("Loading Photo");
		load.setCancelable(false);
		load.show();
		Bitmap bitmap = getBitmapFromMemCache(imageURL);
		if(bitmap==null){
			new GetImageWorkAsyncTask().execute(imageURL);
		} else {
			imageView.setImageBitmap(bitmap);
			load.cancel();
		}
		
	}

	private final GestureDetector gdt = new GestureDetector(getBaseContext(),
			new GestureListener());

	private static final int SWIPE_MIN_DISTANCE = 60;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;

	private class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent me1, MotionEvent me2, float velX,
				float velY) {
			if (me1.getX() - me2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velX) > SWIPE_THRESHOLD_VELOCITY) {
				if (imageID == imageURLs.length - 1) {
					imageID = 0;
				} else {
					imageID = imageID + 1;
				}
				imgURL = imageURLs[imageID];
				imageWork(imgURL);
				return true;
			} else if (me2.getX() - me1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velX) > SWIPE_THRESHOLD_VELOCITY) {
				if (imageID == 0) {
					imageID = imageURLs.length - 1;
				} else {
					imageID = imageID - 1;
				}
				imgURL = imageURLs[imageID];
				imageWork(imgURL);
				return true;
			}
			return false;
		}
	}

	private class GetImageWorkAsyncTask extends AsyncTask<String, Void, Bitmap> {
		String imgURL;

		@Override
		protected Bitmap doInBackground(String... params) {
			this.imgURL = params[0];
			Bitmap image = null;
			try {
				URL url = new URL(this.imgURL);
				image = BitmapFactory.decodeStream(url.openStream());

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return image;
		}

		protected void onPostExecute(Bitmap image) {
			if (image == null) {
				image = ((BitmapDrawable) getResources().getDrawable(
						R.drawable.not_found)).getBitmap();
			}
			imageView.setImageBitmap(image);
			addBitmapToMemory(this.imgURL, image);
			load.cancel();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_image_viewer, menu);
		return true;
	}

}
