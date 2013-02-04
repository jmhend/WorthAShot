package com.example.worth_a_shot.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.worth_a_shot.R;
import com.example.worth_a_shot.models.Drink;

public class DrinksAdapter extends ArrayAdapter<Drink> {

	private List<Drink> mDrinks;
	
	public DrinksAdapter(Context context, List<Drink> drinks) {
		super(context, R.layout.listitem_drink, drinks);
		
		mDrinks = drinks;
	}
	
	public void replaceContent(List<Drink> drinks) {
		if (drinks == null) drinks = new ArrayList<Drink>();
		
		mDrinks = drinks;
		
		clear();
		for (Drink drink : drinks) {
			add(drink);
		}
	}
	
	@Override
	public Drink getItem(int position) {
		return mDrinks.get(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_drink, null);
		}
		Drink drink = mDrinks.get(position);
		TextView v = (TextView) convertView.findViewById(R.id.drink_name);
		v.setText(drink.getName());
		
		
		return convertView;
	}

}
