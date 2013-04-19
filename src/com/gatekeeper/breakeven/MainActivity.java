package com.gatekeeper.breakeven;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MainActivity extends Activity {
	public static final int PAID = 1;
	public static final int PAY = 2;
	private static int myBalance=0;
	private Cursor myCursor;
	private TransactionDbAdapter dbHelper;
	
	private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//context = getApplicationContext();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ListView list = (ListView)findViewById(R.id.mainList);
		Log.i("main","created");
		
		dbHelper = new TransactionDbAdapter(this);
		dbHelper.open();
		
		fillData();
		updateBalance();
		registerForContextMenu(list);
		
	}
	@Override
	protected void onStart(){
		super.onStart();
		Log.i("state","onStart");
		
	}
	@Override
	protected void onStop(){
		super.onStop();
		Log.i("state","onStop");
	}
	@Override
	protected void onPause(){
		super.onPause();
		Log.i("state","onPause");
	}
	@Override
	protected void onRestart(){
		super.onRestart();
		Log.i("state","onRestart");
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (resultCode == RESULT_OK){
			switch (requestCode){
			case 1:
				Log.i("switch","case 1 - PAID");
				Log.i("balance", String.valueOf(myBalance));
				Log.i("balance", String.valueOf(myBalance));
				dbHelper.createTransaction(data.getIntExtra("VALUE",0), data.getStringExtra("CAT"));
				fillData();
				updateBalance();
				break;
			case 2:
				Log.i("switch", "case 2 - PAY");
				dbHelper.createTransaction(data.getIntExtra("VALUE",0)*-1, data.getStringExtra("CAT"));
				fillData();
				updateBalance();
				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo ){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID,0,R.string.menu_delete);
	}
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                dbHelper.deleteTransaction(info.id);
                fillData();
                updateBalance();
                return true;
        }
        return super.onContextItemSelected(item);
    }
	
	public void fillData(){
		myCursor = dbHelper.fetchAllTransactions();
		final ListView list = (ListView)findViewById(R.id.mainList);
		list.setAdapter(new RecentCursorAdapter(this,myCursor));
		
	}
	

	
	public void updateBalance(){
		Log.i("update", String.valueOf(myBalance));
		final TextView textView = (TextView) findViewById(R.id.balance);
		textView.setText(String.valueOf(dbHelper.getBalance()));
		
	}

	
	
	public void paid(View view){
		Intent intent = new Intent(this, PaidActivity.class);
		intent.putExtra("CALL",PAID);
		startActivityForResult(intent, PAID);
		
	}
	
	public void pay(View v){
		Intent intent = new Intent(this, PaidActivity.class);
		intent.putExtra("CALL",PAY);
		startActivityForResult(intent, PAY);
	}
	
	
}
