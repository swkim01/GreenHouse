package deu.sw.greenhouse.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import deu.sw.greenhouse.R;

public class GreenHouseMainActivity extends Activity {

	ImageButton viewButton;
	ImageButton searchButton;
	ImageButton settingButton;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupDefaultActivity();
		setContentView(R.layout.activity_greenhouse_main);

		viewButton = (ImageButton) findViewById(R.id.view_button);
		viewButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StartViewActivity();
			}

		});
		//
		searchButton = (ImageButton) findViewById(R.id.search_button);
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StartSearchActivity();
			}
		});
		
		settingButton = (ImageButton) findViewById(R.id.setting_button);
		settingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StartSettingActivity();
			}
		});

	}

	protected void setupDefaultActivity() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	public void StartViewActivity() {
		 Intent intent = new Intent(this, ARPreviewActivity.class);
			//intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}

	public void StartSearchActivity() {

		Intent intent = new Intent(this, IBeaconSearchActivity.class);
		startActivity(intent);
	}
	
	public void StartSettingActivity() {
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
	}

}
