package com.example.worth_a_shot.adapters;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.worth_a_shot.models.User;

public class UsersAdapter extends ArrayAdapter<User> {

	private static final String TAG = UsersAdapter.class.getSimpleName();
	
////======================================================================================
//// Member variables.
////======================================================================================
	
	private List<User> mUsers;
	
////======================================================================================
//// Constructor.
////======================================================================================
	
	public UsersAdapter(Context context, List<User> users) {
		super(context, 0, users);
		mUsers = users;
	}
	
////======================================================================================
//// ArrayAdapter methods.
////======================================================================================

}
