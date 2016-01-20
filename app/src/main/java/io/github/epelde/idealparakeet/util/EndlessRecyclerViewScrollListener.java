package io.github.epelde.idealparakeet.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by epelde on 20/01/2016.
 */
public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager layoutManager;

    // The minimum amount of items to have below
    // your current scroll position before loading more.
    private int visibleThreshold = 6;

    // The current offset index of data you have loaded
    private int currentPage = 1;

    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;

    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        int totalItemCount = layoutManager.getItemCount();

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            currentPage = 1;
            previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        if (!loading && (lastVisiblePosition + visibleThreshold) >= totalItemCount) {
            onLoadMore(++currentPage);
            loading = true;
        }
    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int page);
}
