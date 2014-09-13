package net.bibim.database1;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ExDatabase1Activity extends Activity {
	    private DBAdapter mDb;
	    private ArrayList<Info> mInfo;
	    private ArrayAdapter<Info> mAdapter;
	    
	    ListView listView;
	    EditText editText1;
	    EditText editText2;
	    Button btnSave;
	    
	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);

	        listView=(ListView) findViewById(R.id.listView);
	        editText1=(EditText) findViewById(R.id.editText1);
	        editText2=(EditText) findViewById(R.id.editText2);
	        btnSave=(Button) findViewById(R.id.btnSave);
	        
	        mDb = new DBAdapter(this);
	        mInfo = mDb.getAllInfo();
	        mAdapter = new ArrayAdapter<Info>(this, android.R.layout.simple_list_item_1, mInfo);
	        listView.setAdapter(mAdapter);
	        
	        /* ���� ��ư */
	        btnSave.setOnClickListener(new OnClickListener() {	
				public void onClick(View v) {
					mDb.insertInfo(editText1.getText().toString(), editText2.getText().toString());
					editText1.setText("");
				    editText2.setText("");
					refreshList();	
				}
			});
	        
	        /* ����Ʈ�� Ŭ���� ���� */
	        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> parent, View view,
						int position, long id) {
					 Info i = mInfo.get(position);
				        Toast.makeText(ExDatabase1Activity.this, "delete " + i.getMeeting(), Toast.LENGTH_SHORT).show();
				        mDb.deleteInfo(i.getId());
				        refreshList();
					return false;
				}
			});
	    }
	    
	    public boolean onCreateOptionsMenu(Menu menu) {
	        menu.add(0, 1, 0, "All delete!");
	        // TODO Auto-generated method stub
	        return super.onCreateOptionsMenu(menu);
	       }
	       
	       public boolean onOptionsItemSelected(MenuItem item) {
	        mDb.deleteAll();
	        refreshList();  
	        return super.onOptionsItemSelected(item);
	       }

	    protected void onDestroy() {
	        mDb.close();
	        super.onDestroy();
	    }
	  
	    private void refreshList() {
	        mInfo.clear();
	        mInfo.addAll(mDb.getAllInfo());
	        mAdapter.notifyDataSetInvalidated();
	    }
	}
