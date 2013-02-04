package com.example.worth_a_shot.activity;

import java.util.List;

import android.os.Bundle;
import android.widget.ListView;

import com.example.worth_a_shot.LoaderActivity;
import com.example.worth_a_shot.adapters.UsersAdapter;
import com.example.worth_a_shot.models.User;

public class FriendsActivity extends LoaderActivity {

	private static final String TAG = FriendsActivity.class.getSimpleName();
	
////===========================================================================================
//// Static constants.
////===========================================================================================
	
	public static final String SELECT = "SELECT";
	
////===========================================================================================
//// Member variables.
////===========================================================================================
	
	private ListView mListView;
	private UsersAdapter mListAdapter;
	private List<User> mFriends;
	
	private boolean mIsFriendsSelect = false;
	
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
