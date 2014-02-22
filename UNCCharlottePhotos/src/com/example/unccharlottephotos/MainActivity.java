
package com.example.unccharlottephotos;

import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity implements View.OnClickListener{
	private ImageButton[] allImages;
	private int[] imgUrls = { R.string.uncc_main_thumb,
			R.string.football_main_thumb, R.string.ifest_main_thumb,
			R.string.commencement_main_thumb };
	private int[] thumbs = {R.array.uncc_thumbs, R.array.football_thumbs, R.array.ifest_thumbs, R.array.commencement_thumbs};
	Intent intent;
	ProgressDialog load;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		intent = new Intent(getBaseContext(), GalleryActivity.class);
		
		load = new ProgressDialog(this);
		load.setMessage("Loading...");
		load.setCancelable(false);
		load.show();
		allImages = new ImageButton[4];
		for (int i = 0; i < 4; i++) {
			String imageButtonID = "imageButton" + Integer.toString(i + 1);
			int id = getResources().getIdentifier(imageButtonID, "id",
					"com.example.unccharlottephotos");
			allImages[i] = (ImageButton) findViewById(id);
			allImages[i].setOnClickListener(this);
			allImages[i].setTag(R.id.thumbs_tag, thumbs[i]);
		}

		for (int i = 0; i < imgUrls.length; i++) {
			// Log.d("demo", Integer.toString(i));
			new GetImagesWorkAsyncTask().execute(i);
		}
		
		Button btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				System.exit(0);
				
			}
		});

	}

	private class GetImagesWorkAsyncTask extends
			AsyncTask<Integer, Void, Bitmap> {
		private int index;

		@Override
		protected Bitmap doInBackground(Integer... params) {
			this.index = params[0];
			Bitmap image = null;
			String imgUrl = getResources().getString(imgUrls[params[0]]);
			//Log.d("demo", imgUrl);
			try {
				URL url = new URL(imgUrl);
				image = BitmapFactory.decodeStream(url.openStream());

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return image;
		}

		protected void onPostExecute(Bitmap image) {
			if (image == null) {
				image = ((BitmapDrawable) getResources().getDrawable(
						R.drawable.not_found)).getBitmap();
				//Log.d("demo", "Error");
			}
			allImages[index].setImageBitmap(image);
			allImages[index].setPadding(10, 10, 10, 10);
			//Log.d("demo", "Proper image downloaded");
			if(index == allImages.length-1){
				load.cancel();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		intent.putExtra("Thumbs", ((Integer)v.getTag(R.id.thumbs_tag)).intValue());
		startActivity(intent);
		
	}

}
