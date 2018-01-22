package com.danielmarczin.whattowatch.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danielmarczin.whattowatch.R;
import com.danielmarczin.whattowatch.db.AppDatabase;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class StatsFragment extends Fragment {

    private static final String TAG = "StatsFragment";
    private static List<Integer> colorTemplate;
    private CompositeDisposable disposables;
    private AppDatabase db;
    private Context appContext;
    private PieChart chart;
    private View rootView;


    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getAppDatabase(appContext);

        if (colorTemplate == null)
            colorTemplate = ColorTemplate.createColors(getResources(), new int[]{R.color.colorDeepGreen, R.color.colorDarkRed});

        if (disposables == null || disposables.isDisposed())
            disposables = new CompositeDisposable();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_stats, container, false);

        chart = rootView.findViewById(R.id.chartStats);
        chart.setNoDataTextColor(getResources().getColor(R.color.textColorPrimary));

        loadDataToPie();

        return rootView;
    }

    private void loadDataToPie() {
        disposables.add(db.getMovieDao().countMoviesAsync(false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Integer>() {
                    @Override
                    public void onSuccess(final Integer accepted) {
                        disposables.add(db.getMovieDao().countMoviesAsync(true)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableSingleObserver<Integer>() {
                                    @Override
                                    public void onSuccess(Integer rejected) {
                                        initChart(accepted, rejected);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e(TAG, "DB error", e);
                                        displayError();
                                    }
                                }));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "DB error", e);
                        displayError();
                    }
                }));
    }


    private void displayError() {
        Snackbar.make(rootView, R.string.database_error, Snackbar.LENGTH_LONG)
                .show();
    }

    private void initChart(int accepted, int rejected) {
        if (accepted == 0 && rejected == 0)
            return;

        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(accepted, getString(R.string.accepted)));
        entries.add(new PieEntry(rejected, getString(R.string.rejected)));

        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.decisions));
        dataSet.setColors(colorTemplate);

        PieData data = new PieData(dataSet);
        chart.setData(data);
        chart.setDescription(null);
        chart.invalidate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appContext = context.getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
        disposables.dispose();
    }
}
