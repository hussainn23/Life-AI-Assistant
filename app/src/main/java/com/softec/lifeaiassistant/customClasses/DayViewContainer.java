package com.softec.lifeaiassistant.customClasses;

import android.view.View;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.kizitonwose.calendar.view.ViewContainer;
import com.softec.lifeaiassistant.R;

import java.time.LocalDate;

public class DayViewContainer extends ViewContainer {
    public TextView dayNameText;
    public TextView textView;
    //    public View tabIndicator;
    public MaterialCardView base;
    //public TextView monthYearText;
    public LocalDate date;

    public DayViewContainer(View view) {
        super(view);
        dayNameText = view.findViewById(R.id.dayNameText);
        textView = view.findViewById(R.id.calendarDayText);
        base = view.findViewById(R.id.base);
//        tabIndicator = view.findViewById(R.id.tabIndicator);
        // monthYearText = view.findViewById(R.id.monthYearText);
    }
}

