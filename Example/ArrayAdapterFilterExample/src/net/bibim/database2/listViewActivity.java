package net.bibim.database2;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
        
        mDb.insertInfo(intent.getStringExtra("edit1").toString(), intent.getStringExtra("text").toString());
        refreshList();	 
        
        /* 리스트뷰 클릭시 인텐트호출 */
        listView.setOnItemClickListener(new OnItemClickListener() {	 
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				intent=new Intent(listViewActivity.this, SubActivity.class);
				intent.putExtra("meeting", mInfo.get(position).getMeeting());
				intent.putExtra("time", mInfo.get(position).getTime());
				startActivity(intent);
			}
		});
        
        
        /* 리스트뷰 롱클릭시 삭제 다이얼로그 */
        listView.setOnItemLongClickListener(new OnItemLongClickListener() { 
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				
				AlertDialog diaBox=new AlertDialog.Builder(listViewActivity.this)
		    	.setTitle("Delete")
		    	.setMessage("Really Delete?")
		    	.setIcon(R.drawable.ic_launcher)
		    	.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						 Info i = mInfo.get(position);
						 mDb.deleteInfo(i.getId());
					     refreshList();
					}
				})
				.setNegativeButton("NO", null)
				.create();
				diaBox.show();
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
