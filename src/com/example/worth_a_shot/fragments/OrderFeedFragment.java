package com.example.worth_a_shot.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.worth_a_shot.R;
import com.example.worth_a_shot.activity.OrderDrinkActivity;
import com.example.worth_a_shot.adapters.OrdersAdapter;
import com.example.worth_a_shot.http.ServiceHelper;
import com.example.worth_a_shot.http.request.OrderFeedRequest;
import com.example.worth_a_shot.models.Order;

public class OrderFeedFragment extends Fragment {
	
	private static final String TAG = OrderFeedFragment.class.getSimpleName();
	
////=============================================================================================
//// Member variables.
////=============================================================================================
	
	private View mFragmentView;
	
	private ListView mListView;
	private OrdersAdapter mListAdapter;
	
////=============================================================================================
//// Fragment lifecycle.
////=============================================================================================

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		OrderFeedRequest r = new OrderFeedRequest(getActivity());
		ServiceHelper.getInstance(getActivity()).startService(r);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mFragmentView == null) {
			mFragmentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_orders_feed, null);
			mListView = (ListView) mFragmentView.findViewById(R.id.feed_list);
			Button b = (Button) mFragmentView.findViewById(R.id.feed_button);
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), OrderDrinkActivity.class);
					getActivity().startActivity(intent);
				}				
			});
		} else {
			ViewGroup parent = (ViewGroup) mFragmentView.getParent();
			if (parent != null) parent.removeView(mFragmentView);
		}
		
		return mFragmentView;
	}
	
////=============================================================================================
//// Content.
////=============================================================================================
	
	public void refresh() {
		new FetchOrdersTask().execute();
	}
	
	private void refreshList(List<Order> orders) {
		if (orders == null) orders = new ArrayList<Order>();
		if (mListAdapter == null) {
			mListAdapter = new OrdersAdapter(getActivity(), orders);
			mListView.setAdapter(mListAdapter);
		} else {
			mListAdapter.replaceContent(orders);
		}
	}
	
	private class FetchOrdersTask extends AsyncTask<Void, Void, List<Order>> {

		@Override
		protected List<Order> doInBackground(Void... params) {
			return Order.getAllFriendOrders(getActivity());
		}
		
		@Override
		public void onPostExecute(List<Order> result) {
			refreshList(result);
		}
		
	}
}
