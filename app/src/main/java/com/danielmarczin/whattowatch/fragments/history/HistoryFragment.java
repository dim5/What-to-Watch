package com.danielmarczin.whattowatch.fragments.history;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danielmarczin.whattowatch.R;

public class HistoryFragment extends Fragment {

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycleHistory);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        HistoryAdapter adapter = new HistoryAdapter(getContext());
        recyclerView.setAdapter(adapter);

        HistoryItemTouchHelperCallback helperCallback = new HistoryItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(helperCallback);
        touchHelper.attachToRecyclerView(recyclerView);

        return rootView;
    }

}
