package com.charlie.stories.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.charlie.stories.FirstActivity;
import com.charlie.stories.R;
import com.charlie.stories.StoriesProgressView;
import com.charlie.stories.StoriesProgressView.StoriesListener;

public class StoryFragment extends Fragment {

    private static final int PROGRESS_COUNT = 6;
    private static final String TAG = StoryFragment.class.getSimpleName();
    private final int[] resources = new int[]{
            R.drawable.sample1,
            R.drawable.sample2,
            R.drawable.sample3,
            R.drawable.sample4,
            R.drawable.sample5,
            R.drawable.sample6,
    };
    private final long[] durations = new long[]{
            500L, 1000L, 1500L, 4000L, 5000L, 1000,
    };
    long pressTime = 0L;
    long limit = 500L;
    private int position = 0;
    private StoriesProgressView storiesProgressView;
    private ImageView image;
    private int counter = 0;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    private StoriesProgressView.StoriesListener storiesListener = new StoriesListener() {
        @Override
        public void onPause() {
            Log.d(TAG, "Test StoriesListener onPause: " + position + " Counter: " + counter);
        }

        @Override
        public void onResume() {
            Log.d(TAG, "Test StoriesListener onResume: " + position + " Counter: " + counter);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public void onNext() {
            showImage(resources[++counter]);
            changeCounterAndPosition(position, counter);
        }

        @Override
        public void onPrev() {
            if ((counter - 1) < 0) return;
            showImage(resources[--counter]);
            changeCounterAndPosition(position, counter);
        }

        @Override
        public void onComplete() {
            Log.d(TAG, "Test onComplete: " + "Position: " + position + " Counter: " + counter);
            ((FirstActivity) getActivity()).nextStory();
        }
    };

    public StoryFragment(int position) {
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(
                R.layout.fragment_story, container, false);

        Log.d(TAG, "Test OnCreate: " + "Position: " + position + " Counter: " + counter);

        return view;
    }

    @Override
    public void onResume() {

        View view = this.getView();
        //((FirstActivity) getActivity()).getViewPagerAdapter().notifyItemChanged(position);

        storiesProgressView = (StoriesProgressView) view.findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(PROGRESS_COUNT);
        storiesProgressView.setStoryDuration(3000L);
        // or
        // storiesProgressView.setStoriesCountWithDurations(durations);
        storiesProgressView.setStoriesListener(storiesListener);
//        storiesProgressView.startStories();

        // bind reverse view
        View reverse = view.findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = view.findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);

        counter = 0;
        storiesProgressView.startStoriesClearStack(0);

        image = (ImageView) view.findViewById(R.id.image);
        showImage(resources[counter]);

        Log.d(TAG, "Test onResume: " + "Position: " + position + " Counter: " + counter);
        changeCounterAndPosition(position, counter);

        //storiesProgressView.startStories(0);`
        super.onResume();
    }

    private void showImage(int resource) {
//        Glide.with(getContext())
//                .load(resource)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                //.placeholder(R.drawable.ic_launcher_background)
//                .into(image);
        image.setImageResource(resource);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "Test onPause: " + "Position: " + position + " Counter: " + counter);
        storiesProgressView.destroy();
        super.onPause();
    }

    private void changeCounterAndPosition(int position, int counter) {
        TextView positionText = this.getView().findViewById(R.id.position);
        TextView counterText = this.getView().findViewById(R.id.counter);
        positionText.setText(position + "");
        counterText.setText(counter + "");
    }

    @Override
    public void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }
}
