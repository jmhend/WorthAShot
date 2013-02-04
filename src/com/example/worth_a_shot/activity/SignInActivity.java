package com.example.worth_a_shot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.example.worth_a_shot.LoaderActivity;
import com.example.worth_a_shot.R;

public class SignInActivity extends LoaderActivity {
	
	private static final String TAG = SignInActivity.class.getSimpleName();

////========================================================================================
//// Activity lifecycle.
////========================================================================================
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		getSupportActionBar().hide();
		
		LinearLayout button = (LinearLayout) findViewById(R.id.signin_button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), FacebookAuthActivity.class);
				startActivity(intent);
				finish();
			}			
		});
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
