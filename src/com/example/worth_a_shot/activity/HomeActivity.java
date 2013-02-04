package com.example.worth_a_shot.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.example.worth_a_shot.LoaderActivity;
import com.example.worth_a_shot.R;
import com.example.worth_a_shot.adapters.OrdersAdapter;
import com.example.worth_a_shot.fragments.MeFragment;
import com.example.worth_a_shot.fragments.OrderFeedFragment;
import com.example.worth_a_shot.http.ApiRequest.Actions;
import com.example.worth_a_shot.http.CallbackReceiver;
import com.example.worth_a_shot.models.Order;
import com.example.worth_a_shot.utils.FragmentTabHost;
import com.example.worth_a_shot.utils.FragmentTabHost.TabListener;

/**
 * Home screen when the user opens the app and is authenticated.
 * @author jmhend
 *
 */
public class HomeActivity extends LoaderActivity implements TabHost.OnTabChangeListener {
	
	private static final String TAG = HomeActivity.class.getSimpleName();
	
////=================================================================================================
//// Member variables.
////=================================================================================================
	
	private FragmentTabHost mTabHost;
	private ListView mListView;
	private OrdersAdapter mListAdapter;
	private List<Order> mOrders;
	
	private OrdersReceiver mReceiver;

////=================================================================================================
//// Activity lifecycle.
////=================================================================================================
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
			
		mListView = (ListView) findViewById(R.id.home_list);
		mReceiver = new OrdersReceiver();
		
		setUpTabHost();

		View view = LayoutInflater.from(this).inflate(R.layout.abc_home, null);
		Button b = (Button) view.findViewById(R.id.abc_home_button);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, OrderDrinkActivity.class);
				startActivity(intent);
			}
			
		});
		getSupportActionBar().setCustomView(view);
	
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mReceiver, new IntentFilter(Actions.ORDER_FEED));
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}
	
	/*
	 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	@Override
	public void onTabChanged(String tabId) {
		Log.e(TAG, "Changed");
	}
	
	/**
	 * Sets up the TabHost that holds each Fragment for HomeActivity.
	 */
	private void setUpTabHost() {
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		for (int i = 0; i < 2; i++) {
			addTab(mTabHost, i);
		}

		mTabHost.setOnTabChangedListener(this);
	}
	
	/**
	 * Adds a new tab to the given TabHost, along with a TabListener.
	 */
	private void addTab(FragmentTabHost tabHost, int index) {
		View tabLayoutView = LayoutInflater.from(this).inflate(R.layout.tab_layout, null);
		TextView tabLabelView = (TextView) tabLayoutView.findViewById(R.id.tab_label);
		
		String text = "";
		if (index == 0) text = "Recent";
		if (index == 1) text = "Me";
		tabLabelView.setText(text);

		TabSpec tabSpec = tabHost.newTabSpec(text).setIndicator(tabLayoutView).setContent(new TabFactory(this));

		// Create the appropriate TabListener for each Fragment type.
		TabListener<?> listener = null;
		if (index == 0) {
			listener = new FragmentTabHost.TabListener<OrderFeedFragment>(this, "Recent", OrderFeedFragment.class);
		} else if (index == 1) {
			listener = new FragmentTabHost.TabListener<MeFragment>(this, "Me", MeFragment.class);
		} 
		tabHost.addTab(tabSpec, listener);
	}
	

	/**
	 * TabFactory
	 * Necessary for instantiating Views of Tabs. The Views created in createTabContent
	 * aren't actually the Views that will be shown.
	 */
	private class TabFactory implements TabContentFactory {
		private final Context mContext;

		public TabFactory(Context context) {
			mContext = context;   		
		}

		/*
		 * (non-Javadoc)
		 * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
		 */
		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumHeight(0);
			v.setMinimumWidth(0);
			return v;
		}    	
    }

	
////=================================================================================================
//// Event handlers.
////=================================================================================================
	
////=================================================================================================
//// Content.
////=================================================================================================
	
	/**
	 * Safe way to get a reference to the EventListFragment in this HomeActivity.
	 * @return
	 */
	public OrderFeedFragment getFeedFragment() {
		if (mTabHost == null) {
			return null;
		}
		return (OrderFeedFragment) mTabHost.getFragmentByTag("Recent");
	}

	/**
	 * Safe way to get a reference to the EventListFragment in this HomeActivity.
	 * @return
	 */
	public MeFragment getMeFragment() {
		if (mTabHost == null) {
			return null;
		}
		return (MeFragment) mTabHost.getFragmentByTag("Me");
	}
	
////=================================================================================================
//// Receivers.
////=================================================================================================
	
	/**
	 * Listens for responses from SignUpRequest.
	 * @author jmhend
	 */
	private class OrdersReceiver extends CallbackReceiver {
		/*
		 * (non-Javadoc)
		 * @see com.example.worth_a_shot.http.CallbackReceiver#onResultOk(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onResultOk(Context context, Intent intent) {
			Log.e(TAG, "refresh that shit");
			getFeedFragment().refresh();
		}
	}

}
