package net.bibim.database2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ExDatabase2Activity extends Activity {
	EditText editText1;
	EditText editText2;
	Button btnSave;
	Intent intent;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        editText1=(EditText) findViewById(R.id.editText1);
        editText2=(EditText) findViewById(R.id.editText2);
        btnSave=(Button) findViewById(R.id.btnSave);
        
        btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				intent=new Intent(ExDatabase2Activity.this, listViewActivity.class);
				intent.putExtra("edit1", editText1.getText().toString());
				intent.putExtra("edit2", editText2.getText().toString());
				startActivityForResult(intent, 0);	
				editText1.setText("");
			    editText2.setText("");
			}
		});
    }
}