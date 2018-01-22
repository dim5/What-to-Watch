package com.danielmarczin.whattowatch.util;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.view.View;

import java.util.ArrayDeque;
import java.util.Queue;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class ShowCaseQueue {
    private final Queue<MaterialTapTargetPrompt> tutorials;
    private final MaterialTapTargetPrompt.PromptStateChangeListener listener;
    private final Activity activity;

    public ShowCaseQueue(Activity activity) {
        this.activity = activity;
        this.tutorials = new ArrayDeque<>();

        listener = new MaterialTapTargetPrompt.PromptStateChangeListener() {
            @Override
            public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                    if (!tutorials.isEmpty())
                        tutorials.poll().show();
                }
            }
        };

    }

    public void addTutorials(@IdRes int target, @StringRes int primaryText, @StringRes int secondaryText) {
        tutorials.add(
                createBuilder(activity.findViewById(target))
                        .setPrimaryText(activity.getString(primaryText))
                        .setSecondaryText(activity.getString(secondaryText))
                        .create());
    }

    public void addTutorials(View target, @StringRes int primaryText, @StringRes int secondaryText) {
        tutorials.add(
                createBuilder(target)
                        .setPrimaryText(activity.getString(primaryText))
                        .setSecondaryText(activity.getString(secondaryText))
                        .create());
    }

    private MaterialTapTargetPrompt.Builder createBuilder(View focus) {
        return new MaterialTapTargetPrompt.Builder(activity)
                .setTarget(focus)
                .setPromptStateChangeListener(listener)
                .setCaptureTouchEventOutsidePrompt(true)
                .setCaptureTouchEventOnFocal(true)
                ;
    }


    public void start() {
        tutorials.poll().show();
    }

}
