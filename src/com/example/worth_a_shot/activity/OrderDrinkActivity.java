package com.example.worth_a_shot.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.worth_a_shot.LoaderActivity;
import com.example.worth_a_shot.R;
import com.example.worth_a_shot.adapters.DrinksAdapter;
import com.example.worth_a_shot.http.ApiRequest.Actions;
import com.example.worth_a_shot.http.CallbackReceiver;
import com.example.worth_a_shot.http.ServiceHelper;
import com.example.worth_a_shot.http.request.DrinksSearchRequest;
import com.example.worth_a_shot.models.Drink;

public class OrderDrinkActivity extends LoaderActivity {

	private static final String TAG = OrderDrinkActivity.class.getSimpleName();
	
////===========================================================================================
//// Member variables.
////===========================================================================================
	
	private ListView mListView;
	private DrinksAdapter mListAdapter;
	private List<Drink> mDrinks;
	private DrinksReceiver mReceiver;
	
////===========================================================================================
//// Activity lifecycle.
////===========================================================================================
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_drinks);
		
		mListView = (ListView) findViewById(R.id.order_drinks_list);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Drink drink = mListAdapter.getItem(position);
				
				Intent intent = new Intent(OrderDrinkActivity.this, OrderPlaceActivity.class);
				intent.putExtra("name", drink.getName());
				intent.putExtra("id", drink.getDrinkId());
				
				startActivity(intent);
			}
			
		});
		mReceiver = new DrinksReceiver();
		
		DrinksSearchRequest r = new DrinksSearchRequest(this, null);
		ServiceHelper.getInstance(getApplicationContext()).startService(r);
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(mReceiver, new IntentFilter(Actions.DRINKS_SEARCH));
	}
	
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.LoaderActivity#load()
	 */
	@Override
	public void load() {
		mDrinks = Drink.getAllDrinks(getApplicationContext());
	}

	/*
	 * (non-Javadoc)
	 * @see com.example.worth_a_shot.LoaderActivity#onLoaded()
	 */
	@Override
	public void onLoaded() {
		refreshList();
	}
	
////===========================================================================================
//// Content
////===========================================================================================
	
	private void refreshList() {
		if (mDrinks == null) mDrinks = new ArrayList<Drink>();
		if (mListAdapter == null) {
			mListAdapter = new DrinksAdapter(this, mDrinks);
			mListView.setAdapter(mListAdapter);
		} else {
			mListAdapter.replaceContent(mDrinks);
		}
	}	
	
////===========================================================================================
//// Receiver.
////===========================================================================================
	
	/**
	 * 
	 * @author jmhend
	 *
	 */
	private class DrinksReceiver extends CallbackReceiver {
		/*
		 * (non-Javadoc)
		 * @see com.example.worth_a_shot.http.CallbackReceiver#onResultOk(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onResultOk(Context context, Intent intent) {
			requestLoad();
		}		
	}
}







