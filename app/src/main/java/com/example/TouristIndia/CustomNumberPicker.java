package com.example.TouristIndia;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class CustomNumberPicker extends android.widget.NumberPicker {

    Typeface type;

    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        type = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/b_yekan.ttf");
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);

        type = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/poppins.ttf");
        updateView(child);
    }

    private void updateView(View view) {

        if (view instanceof EditText) {
            ((EditText) view).setTypeface(type);
            ((EditText) view).setTextSize(25);
            ((EditText) view).setTextColor(getResources().getColor(
                    R.color.bluesdark));
        }

    }

}