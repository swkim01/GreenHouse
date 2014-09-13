package net.bibim.database2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class listViewActivity extends Activity {
	 private DBAdapter mDb;
	 private ArrayList<Info> mInfo;
	 private ArrayAdapter<Info> mAdapter;
	    
	 ListView listView;
	 Intent intent;
	 
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.listview);

        listView=(ListView) findViewById(R.id.listView);
        intent=getIntent();
        
        mDb = new DBAdapter(this);
        mInfo = mDb.getAllInfo();
        mAdapter = new ArrayAdapter<Info>(this, android.R.layout.simple_list_item_1, mInfo);
        listView.setAdapter(mAdapter);
        
        mDb.insertInfo(intent.getStringExtra("edit1").toString(), intent.getStringExtra("edit2").toString());
        refreshList();	 
        
        /* 리스트뷰 클릭시 삭제 */
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				 Info i = mInfo.get(position);
			        Toast.makeText(listViewActivity.this, "delete " + i.getMeeting(), Toast.LENGTH_SHORT).show();
			        mDb.deleteInfo(i.getId());
			        refreshList();
				return false;
			}
		});
	}
		
	   /*옵션키로 리스트뷰 전체 삭제 */
	   public boolean onCreateOptionsMenu(Menu menu) {
	        menu.add(0, 1, 0, "All delete!");
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
