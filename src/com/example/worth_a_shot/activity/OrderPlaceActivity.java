package com.example.worth_a_shot.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.worth_a_shot.LoaderActivity;
import com.example.worth_a_shot.R;
import com.example.worth_a_shot.http.ApiRequest.Actions;
import com.example.worth_a_shot.http.CallbackReceiver;
import com.example.worth_a_shot.http.ServiceHelper;
import com.example.worth_a_shot.http.request.DrinkOrderRequest;
import com.example.worth_a_shot.models.Drink;

public class OrderPlaceActivity extends LoaderActivity {

	private static final String TAG = OrderPlaceActivity.class.getSimpleName();
	
////===========================================================================================
//// Member variables.
////===========================================================================================
	
	private OrderReceiver mReceiver;
	private Button mButton;
	
	private Drink mDrink;
	
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
		setContentView(R.layout.activity_order_place);
		
		mReceiver = new OrderReceiver();
		
		mButton = (Button) findViewById(R.id.order_place_button);
		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onOrderClicked();
			}			
		});
		
		String name = getIntent().getExtras().getString("name");
		long id = getIntent().getExtras().getLong("id");
		
		mDrink = new Drink();
		mDrink.setName(name);
		mDrink.setDrinkId(id);		
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(mReceiver, new IntentFilter(Actions.DRINK_ORDER));
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}
	
////===========================================================================================
//// Event handlers.
////===========================================================================================
	
	private void onOrderClicked() {
		DrinkOrderRequest r = new DrinkOrderRequest(this, 0, mDrink.getDrinkId());
		ServiceHelper.getInstance(getApplicationContext()).startService(r);
	}
	
////===========================================================================================
//// Receiver.
////===========================================================================================
	
	private class OrderReceiver extends CallbackReceiver {

		@Override
		public void onResultOk(Context context, Intent intent) {
			Toast.makeText(getApplicationContext(), "Ordered a " + mDrink.getName(), Toast.LENGTH_SHORT).show();
			finish();
		}
		
	}
	
}
