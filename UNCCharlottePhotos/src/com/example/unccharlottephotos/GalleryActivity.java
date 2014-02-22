package com.example.unccharlottephotos;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridLayout;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class GalleryActivity extends Activity {
	private int thumbID;
	private String[] thumbURLs;
	Handler handler;
	ExecutorService taskPool;
	GridLayout grid;
	Intent intent;
	ProgressDialog load;
	public static final String thumb = "Thumb";
	public static final String image = "Image";
	private int count = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		handler = new Handler();
		taskPool = Executors.newFixedThreadPool(5);
		grid = (GridLayout) findViewById(R.id.GridLayout1);
		intent = new Intent(getBaseContext(), ImageViewerActivity.class);
		load = new ProgressDialog(this);
		load.setMessage("Loading Thumbnails");
		load.setCancelable(false);
		load.show();

		if (getIntent().getExtras() != null) {
			thumbID = getIntent().getExtras().getInt("Thumbs");
		}

		thumbURLs = getResources().getStringArray(thumbID);
		
		for (String thumb : thumbURLs) {
			taskPool.execute(new GetImagesWork(thumb));
		}
	}

	private class GetImagesWork implements Runnable {
		String imgURL;
		int imageID;
		public GetImagesWork(String imgURL) {
			this.imgURL = imgURL;
		}

		@Override
		public void run() {
			for(int i=0; i<thumbURLs.length; i++){
				if(imgURL == thumbURLs[i]){
					imageID = i;
				}
			}
			try {
				URL url = new URL(imgURL);
				Bitmap image = BitmapFactory.decodeStream(url.openStream());
				if (image != null) {
//					Log.d("demo", "Image downloaded");
					handler.post(new AddImagesWork(image, imageID));
				} else {
					image = ((BitmapDrawable) getResources().getDrawable(
							R.drawable.not_found)).getBitmap();
//					Log.d("demo", "Image not found");
					handler.post(new AddImagesWork(image, imageID));
				}
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private class AddImagesWork implements Runnable {
		Bitmap image;
		int imageID;
		public AddImagesWork(Bitmap image, int imageID) {
			this.image = image;
			this.imageID = imageID;
		}

		@Override
		public void run() {
				
				ImageView imageViewToBeAdded = new ImageView(getBaseContext());
				imageViewToBeAdded.setImageBitmap(this.image);
				imageViewToBeAdded.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				imageViewToBeAdded.setPadding(2, 2, 2, 2);
				imageViewToBeAdded.setTag(R.id.images_tag, imageID);
				imageViewToBeAdded.setTag(R.id.thumbs_tag, thumbID);
				imageViewToBeAdded.setOnClickListener(new intentClass());
				grid.addView(imageViewToBeAdded);
				count++;
				if(count == thumbURLs.length){
					load.cancel();
				}
		}

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		taskPool.shutdown();
	}
	
	private class intentClass implements View.OnClickListener {

		@Override
		public void onClick(View v) {
//			int ID = imageListID;
			Log.d("demo", Integer.toString(((Integer)v.getTag(R.id.thumbs_tag)).intValue()));
			intent.putExtra(thumb, ((Integer)v.getTag(R.id.thumbs_tag)).intValue());
			intent.putExtra(image, ((Integer)v.getTag(R.id.images_tag)).intValue());
			startActivity(intent);
			
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_gallery, menu);
		return true;
	}

}
