package app.eth.mytestdialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	MainDialog mMainDialog;
	Toast mToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://makerj.tistory.com")));
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public void ONCLICK_MAIN(View v) {
		switch (v.getId()) {
		case R.id.button1:
			mMainDialog = new MainDialog();
			mMainDialog.show(getFragmentManager(), "dialog");
			break;
		}
	}
		
	public static class MainDialog extends DialogFragment {
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(
					getActivity());
			LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
			mBuilder.setView(mLayoutInflater
					.inflate(R.layout.dialog_main, null));
			mBuilder.setTitle("단어 추가");
			mBuilder.setMessage("추가할 단어를 입력해주세요.");
			return mBuilder.create();
		}
		
		@Override
		public void onStop() {
			super.onStop();
		}
		
	}

	public void ONCLICK_DIALOG(View v) {
		switch (v.getId()) {
		case R.id.add_button:
			makeToast("BUTTON1 CLICK!");
			break;
		case R.id.cancel_button:

			break;

		}

	}
	
	public void makeToast(String msg){
		mToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
		mToast.show();
	}

}
