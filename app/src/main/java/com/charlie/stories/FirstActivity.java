package com.charlie.stories;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.charlie.stories.fragments.StoryFragment;

import java.util.ArrayList;

public class FirstActivity extends FragmentActivity  {

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 5;
    private static final String TAG = FirstActivity.class.getSimpleName();

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager2 viewPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private FragmentStateAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first);

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.viewpager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(new RotateUpPageTransformer());
        viewPager.setOffscreenPageLimit(1);
    }

    public FragmentStateAdapter getViewPagerAdapter(){
        return pagerAdapter;
    }

    /**
     * Move to next story on story complete
     */
    public void nextStory() {
        if (viewPager.getCurrentItem() != (NUM_PAGES - 1)) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            viewPager.requestTransform();
        }

    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            viewPager.requestTransform();
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            Log.d(TAG, "Test position " + position);
            StoryFragment storyFragment = new StoryFragment(position);
//            mFragments.add(position, storyFragment);
            return storyFragment;
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}