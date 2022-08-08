package com.charlie.stories;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class StoriesProgressView extends LinearLayout {

    private static final String TAG = StoriesProgressView.class.getSimpleName();

    private final LayoutParams PROGRESS_BAR_LAYOUT_PARAM = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
    private final LayoutParams SPACE_LAYOUT_PARAM = new LayoutParams(5, LayoutParams.WRAP_CONTENT);

    private final List<PausableProgressBar> progressBars = new ArrayList<>();
    boolean isComplete;
    private int storiesCount = -1;
    /**
     * pointer of running animation
     */
    private int current = -1;
    private StoriesListener storiesListener;
    private boolean isSkipStart;
    private boolean isReverseStart;

    public StoriesProgressView(Context context) {
        this(context, null);
    }

    public StoriesProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StoriesProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StoriesProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StoriesProgressView);
        storiesCount = typedArray.getInt(R.styleable.StoriesProgressView_progressCount, 0);
        typedArray.recycle();
        bindViews();
    }

    private void bindViews() {
        progressBars.clear();
        removeAllViews();

        for (int i = 0; i < storiesCount; i++) {
            final PausableProgressBar p = createProgressBar();
            progressBars.add(p);
            addView(p);
            if ((i + 1) < storiesCount) {
                addView(createSpace());
            }
        }
    }

    private PausableProgressBar createProgressBar() {
        PausableProgressBar p = new PausableProgressBar(getContext());
        p.setLayoutParams(PROGRESS_BAR_LAYOUT_PARAM);
        return p;
    }

    private View createSpace() {
        View v = new View(getContext());
        v.setLayoutParams(SPACE_LAYOUT_PARAM);
        return v;
    }

    /**
     * Set story count and create views
     *
     * @param storiesCount story count
     */
    public void setStoriesCount(int storiesCount) {
        this.storiesCount = storiesCount;
        bindViews();
    }

    public void resetCurrent(){
        current = -1;
    }

    /**
     * Set storiesListener
     *
     * @param storiesListener StoriesListener
     */
    public void setStoriesListener(StoriesListener storiesListener) {
        this.storiesListener = storiesListener;
    }

    /**
     * Skip current story
     */
    public void skip() {
        if (isSkipStart || isReverseStart) return;
        if (isComplete) return;
        if (current < 0) return;
        PausableProgressBar p = progressBars.get(current);
        isSkipStart = true;
        p.setMax();
    }

    /**
     * Reverse current story
     */
    public void reverse() {
        if (isSkipStart || isReverseStart) return;
        if (isComplete) return;
        if (current < 0) return;
        PausableProgressBar p = progressBars.get(current);
        isReverseStart = true;
        p.setMin();
    }

    /**
     * Set a story's duration
     *
     * @param duration millisecond
     */
    public void setStoryDuration(long duration) {
        for (int i = 0; i < progressBars.size(); i++) {
            progressBars.get(i).setDuration(duration);
            progressBars.get(i).setCallback(callback(i));
        }
    }

    /**
     * Set stories count and each story duration
     *
     * @param durations milli
     */
    public void setStoriesCountWithDurations(@NonNull long[] durations) {
        storiesCount = durations.length;
        bindViews();
        for (int i = 0; i < progressBars.size(); i++) {
            progressBars.get(i).setDuration(durations[i]);
            progressBars.get(i).setCallback(callback(i));
        }
    }

    private PausableProgressBar.Callback callback(final int index) {
        return new PausableProgressBar.Callback() {
            @Override
            public void onStartProgress() {
                current = index;
            }

            @Override
            public void onFinishProgress() {
                if (isReverseStart) {
                    if (storiesListener != null) storiesListener.onPrev();
                    if (0 <= (current - 1)) {
                        PausableProgressBar p = progressBars.get(current - 1);
                        p.setMinWithoutCallback();
                        progressBars.get(--current).startProgress();
                    } else {
                        progressBars.get(current).startProgress();
                    }
                    isReverseStart = false;
                    return;
                }
                int next = current + 1;
                if (next <= (progressBars.size() - 1)) {
                    if (storiesListener != null) storiesListener.onNext();
                    progressBars.get(next).startProgress();
                } else {
                    isComplete = true;
                    if (storiesListener != null) storiesListener.onComplete();
                }
                isSkipStart = false;
            }
        };
    }

    /**
     * Start progress animation
     */
    public void startStories() {
        progressBars.get(0).startProgress();
    }

    /**
     * Start progress animation from specific progress
     */
    public void startStories(int from) {
        for (int i = 0; i < from; i++) {
            progressBars.get(i).setMaxWithoutCallback();
        }
        progressBars.get(from).startProgress();
    }

    /**
     * Start progress animation from specific progress and clear others
     */
    public void startStoriesClearStack(int from) {
        if (from == 0) {
            int i =0;
            for (PausableProgressBar p : progressBars) {
                Log.d(TAG, "Test clear: " + i);
                p.reset();
                i++;
            }
        } else if (from > 0) {
            for (int i = 0; i < from; i++) {
                progressBars.get(i).reset();
            }
        }
        progressBars.get(from).startProgress();
    }

    /**
     * Need to call when Activity or Fragment destroy
     */
    public void destroy() {
        Log.d(TAG, "Test destroy: " + current);
        for (PausableProgressBar p : progressBars) {
            p.clear();
        }
        if (storiesListener != null) storiesListener.onDestroy();
    }

    /**
     * Pause story
     */
    public void pause() {
        Log.d(TAG, "Test pause: " + current);
        if (current < 0) return;
        progressBars.get(current).pauseProgress();
        if (storiesListener != null) storiesListener.onPause();
    }

    /**
     * Resume story
     */
    public void resume() {
        Log.d(TAG, "Test resume: " + current);
        if (current < 0) return;
        progressBars.get(current).resumeProgress();
        if (storiesListener != null) storiesListener.onResume();
    }

    public interface StoriesListener {
        void onPause();

        void onResume();

        void onDestroy();

        void onNext();

        void onPrev();

        void onComplete();
    }
}
