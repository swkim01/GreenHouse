package net.bibim.database2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SubActivity extends Activity {
	Intent intent;
	TextView textView1;
	TextView textView2;

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.sub);
	    
	    textView1=(TextView) findViewById(R.id.textView1);
	    textView2=(TextView) findViewById(R.id.textView2);
	    intent=getIntent();
	    
	    textView1.setText(intent.getStringExtra("meeting").toString());
	    textView2.setText(intent.getStringExtra("time").toString());
	}

}
