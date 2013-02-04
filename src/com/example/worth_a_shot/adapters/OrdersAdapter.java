package com.example.worth_a_shot.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.worth_a_shot.R;
import com.example.worth_a_shot.models.Order;
import com.example.worth_a_shot.utils.uil.core.ImageLoader;

public class OrdersAdapter extends ArrayAdapter<Order> {
	
	private static final String TAG = OrdersAdapter.class.getSimpleName();
	
////============================================================================================
//// Member variables
////============================================================================================
	
	private List<Order> mOrders;
	
////============================================================================================
//// Constructor.
////============================================================================================

	public OrdersAdapter(Context context, List<Order> orders) {
		super(context, R.layout.listitem_order, orders);
		
		mOrders = orders;
		
	}
	
////============================================================================================
//// ArrayAdapter methods.
////============================================================================================
	
	/**
	 * Replace the ListView content model.
	 * @param orders
	 */
	public void replaceContent(List<Order> orders) {
		if (orders == null) orders = new ArrayList<Order>();
		
		synchronized(mOrders) {
			mOrders = orders;
			clear();
			for (Order order : mOrders) {
				add(order);
			}
		}	
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return (mOrders != null)? mOrders.size() : 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.BaseAdapter#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getCount() == 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_order, null);
			
			holder = new ViewHolder();
			holder.avatarView = (ImageView) convertView.findViewById(R.id.order_user_avatar);
			holder.recAvatarView = (ImageView) convertView.findViewById(R.id.order_recipient_avatar);
			holder.nameView = (TextView) convertView.findViewById(R.id.order_name);
			holder.drinkView = (TextView) convertView.findViewById(R.id.order_drink);
			holder.recNameView = (TextView) convertView.findViewById(R.id.order_recipient_name);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Order order = mOrders.get(position);
		holder.nameView.setText(order.getUser().getName());
		holder.drinkView.setText("bought a " + order.getDrink().getName());
		ImageLoader.getInstance().displayImage(order.getUser().getAvatarUrl(), holder.avatarView);
		
		if (order.getUser().getFacebookId() != order.getRecipient().getFacebookId()) {
			holder.recAvatarView.setVisibility(View.VISIBLE);
			holder.recNameView.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(order.getRecipient().getAvatarUrl(), holder.recAvatarView);
			holder.recNameView.setText("for " + order.getRecipient().getName());
		} else {
			holder.recAvatarView.setVisibility(View.GONE);
			holder.recNameView.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	private static class ViewHolder {
		public ImageView avatarView;
		public ImageView recAvatarView;
		public TextView nameView;
		public TextView drinkView;
		public TextView recNameView;
	}

}
