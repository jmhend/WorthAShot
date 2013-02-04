package com.example.worth_a_shot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.worth_a_shot.LoaderActivity;
import com.example.worth_a_shot.WorthAShot;

public class MultiplexActivity extends LoaderActivity {
	
	private static final String TAG = MultiplexActivity.class.getSimpleName();
	
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
		
		Class<? extends Activity> clazz;
		clazz = (WorthAShot.get(getApplicationContext()).isLoggedIn())? HomeActivity.class : SignInActivity.class;
		
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
		finish();
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaded() {
		// TODO Auto-generated method stub
		
	}

}
