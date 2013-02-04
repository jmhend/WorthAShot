package com.example.worth_a_shot.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class MeFragment extends Fragment {
	
	private View mFragmentView;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mFragmentView == null) {
			
		} else {
			ViewGroup parent = (ViewGroup) mFragmentView.getParent();
			if (parent != null) parent.removeView(mFragmentView);
		}
		
		Button button = new Button(getActivity());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		button.setText("Me");
		button.setLayoutParams(params);
		
		return button;
	}
}
