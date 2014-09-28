package deu.sw.greenhouse.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import deu.sw.greenhouse.R;
import deu.sw.greenhouse.mqtt.PushService;

public class SettingActivity extends Activity {
	private String mDeviceID;
    /** Called when the activity is first created. */
	private TextView targetText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDefaultActivity();
        setContentView(R.layout.activity_setting);
        
        mDeviceID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);         
  	  	targetText = ((TextView) findViewById(R.id.target_text));
  	  	final Button saveButton = ((Button) findViewById(R.id.save_button));
	  	final EditText input1 =((EditText) findViewById(R.id.edit1));
	  	final EditText input2 =((EditText) findViewById(R.id.edit2));

  	  saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = getSharedPreferences(PushService.TAG, MODE_PRIVATE).edit();
		    	editor.putString(PushService.MQTT_HOST, input1.getText().toString());
		    	editor.putString(PushService.MQTT_BROKER_PORT_NUM, input2.getText().toString());
		    	editor.commit();
		    	targetText.setText("서버 IPAddress : "+input1.getText());
			}
		});
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
  	  	SharedPreferences p = getSharedPreferences(PushService.TAG, MODE_PRIVATE);
  	  	boolean started = p.getBoolean(PushService.PREF_STARTED, false);
  
  	  SharedPreferences pref = getSharedPreferences(PushService.TAG, MODE_PRIVATE);
  	   String host = pref.getString(PushService.MQTT_HOST, "127.0.0.1");
  	   String port = pref.getString(PushService.MQTT_BROKER_PORT_NUM, "1883");

  	   targetText.setText("서버 IPAddress : "+host);

 		((EditText) findViewById(R.id.edit1)).setText(host);
 		((EditText) findViewById(R.id.edit2)).setText(port);	
    }
    
	protected void setupDefaultActivity() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
    
   
}