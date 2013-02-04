/**
 * File:    FragmentTabHost.java
 * Created: October 1, 2012
 * Author:	Jesse Hendrickson
 */
package com.example.worth_a_shot.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TabHost;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.worth_a_shot.R;

/**
 * FragmentTabHost
 * Allows the usage of Fragments in a TabHost.
 */
public class FragmentTabHost extends TabHost {
	
	private static final String TAG = FragmentTabHost.class.getSimpleName();
	
////================================================================================================================
//// Member variables
////================================================================================================================
		
	private final ArrayList<TabListener<?>> mTabListeners = new ArrayList<TabListener<?>>();
	
////================================================================================================================
//// Constructors.
////================================================================================================================

	/**
	 * Required for subclasses.
	 * @param context
	 */
	public FragmentTabHost(Context context) {
		super(context);
	}
	
	/**
	 * Required for subclasses.
	 * @param context
	 * @param attrs
	 */
	public FragmentTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
////================================================================================================================
//// TabHost methods.
////================================================================================================================
	
	/**
	 * Adds a TabSpec to this TabHost, and registers it's listener that responds when tabs are clicked.
	 * @param tabSpec
	 * @param listener
	 */
	public void addTab(TabSpec tabSpec, TabListener<?> listener) {
		if (listener == null) {
			throw new IllegalArgumentException("Must provide a TabListener for " + TAG);
		}
		
		mTabListeners.add(listener);		
		super.addTab(tabSpec);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.TabHost#setCurrentTab(int)
	 */
	@Override
	public void setCurrentTab(int index) {
		if (index < 0) return;		
		int currentIndex = getCurrentTab();		
	
		if (mTabListeners.size() > 0) {
			FragmentTransaction ft = ((SherlockFragmentActivity) this.getContext()).getSupportFragmentManager()
					.beginTransaction().disallowAddToBackStack();
			
			// Check if the current tab is the same as the clicked tab.
			if (currentIndex == index) {
				mTabListeners.get(index).onTabReselected(ft);
				
			// Call the TabListener for both the clicked tab and the current tab.
			} else {
				if (currentIndex >= 0) mTabListeners.get(currentIndex).onTabUnselected(ft);
				mTabListeners.get(index).onTabSelected(ft);
			}
			
			// Commit the FragmentTransaction.
			if (ft != null && !ft.isEmpty()) {
	            ft.commit();
	        }
		}
		
		// Make sure to call super so the tabs actually switch.
		super.setCurrentTab(index);
	}
	
////================================================================================================================
//// Getting Fragment references.
////================================================================================================================	
	
	/**
	 * Return a Fragment included in this FragmentTabHost.
	 * @param tag The Tag of the Fragment to search for.
	 * @return The Fragment given by 'tag' if it exists, null otherwise.
	 */
	public Fragment getFragmentByTag(String tag) {
		if (!U.strValid(tag)) return null;
		
		synchronized(mTabListeners) {
			if (mTabListeners == null) return null;
			for (TabListener<?> listener : mTabListeners) {
				if (listener.getTag().equals(tag)) {
					return listener.getFragment();
				}
			}
		}
		
		return null;
	}

	
////================================================================================================================
//// TabListener
////================================================================================================================	
	
	/**
	 * TabListener
	 * Listeners for tab clicks, and calls appropriate callback. Handles FragmentTransactions.
	 * @param <T> The specific Fragment type.
	 */
    public static class TabListener<T extends Fragment> {
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final Bundle mArgs;
        private Fragment mFragment;

        /**
         * Constructor.
         * @param activity
         * @param tag
         * @param clz
         */
        public TabListener(Activity activity, String tag, Class<T> clz) {
            this(activity, tag, clz, null);
        }
        
        /**
         * Constructor. Makes sure a previous instantiation of the given Fragment type doesn't leak over.
         * @param activity
         * @param tag
         * @param clz
         * @param args
         */
        public TabListener(Activity activity, String tag, Class<T> clz, Bundle args) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mArgs = args;
            
            // Make sure no previous references to the Tab Fragments are kept, to avoid 
            // mismatching references.
            // TODO: This may need to be changed when we all configuration changes, 
            // as HomeActivity's onCreate will be called again.
            mFragment = ((SherlockFragmentActivity) mActivity).getSupportFragmentManager().findFragmentByTag(mTag);
            if (mFragment != null) {
                FragmentTransaction ft = ((SherlockFragmentActivity) mActivity).getSupportFragmentManager().beginTransaction();
                ft.remove(mFragment);
                ft.commit();
                mFragment = null;
            }
        }
        
        /**
         * Get this Listener's tag descriptor.
         * @return
         */
        public String getTag() {
        	return mTag;
        }
        
        /**
         * Get a reference to this Listener's Fragment.
         * @return
         */
        public Fragment getFragment() {
        	return mFragment;
        }

        /**
         * Called when this listener's Tab is goes from unselected to selected.
         * Sets the HomeActivity's content to frame to this Fragment.
         * @param ft
         */
        public void onTabSelected(FragmentTransaction ft) {
        	Log.e(TAG, "onTabSelected");
            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
                ft.add(R.id.home_tab_fragment_containter, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }
        }

        /**
         * Called when this listener's Tab goes from selected to unselected.
         * Removes this Fragment from the HomeActivity's content frame.
         * @param ft
         */
        public void onTabUnselected(FragmentTransaction ft) {
        	Log.e(TAG, "onTabUNSelected");
            if (mFragment != null) {
                ft.detach(mFragment);
            }
        }

        /**
         * Called when this listener's Tab is clicked while already selected.
         * @param ft
         */
        public void onTabReselected(FragmentTransaction ft) {
        	Log.e(TAG, "onTabRESelected");
        }
    }
}
