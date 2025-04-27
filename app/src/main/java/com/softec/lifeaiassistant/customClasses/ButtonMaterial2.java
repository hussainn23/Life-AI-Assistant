package com.softec.lifeaiassistant.customClasses;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.softec.lifeaiassistant.R;



public class ButtonMaterial2 extends LinearLayout {

    private TextView combinedTextView;

    public ButtonMaterial2(Context context) {
        super(context);
        init(context, null);
    }

    public ButtonMaterial2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ButtonMaterial2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setPadding(dpToPx(context, 8), dpToPx(context, 13), dpToPx(context, 8), dpToPx(context, 13));


        combinedTextView = new TextView(context);
        combinedTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        combinedTextView.setGravity(Gravity.CENTER_VERTICAL);
        combinedTextView.setLines(2);
        addView(combinedTextView);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ButtonMaterial, 0, 0);
            String titleText = a.getString(R.styleable.ButtonMaterial_title);
            String subtitleText = a.getString(R.styleable.ButtonMaterial_subtitle);
            int startIconResId = a.getResourceId(R.styleable.ButtonMaterial_startIcon, -1);
            int endIconResId = a.getResourceId(R.styleable.ButtonMaterial_endIcon, -1);
            int textColor = a.getColor(R.styleable.ButtonMaterial_textColor, -1);
            a.recycle();

            setText(titleText, subtitleText);
            setIcons(startIconResId, endIconResId);
            if (textColor != -1) {
                setTextColor(textColor);
            }
        }
    }

    public void setText(String title, String subtitle) {

        String formattedText = "<small>" + title + "</small><br/>" + subtitle;
        combinedTextView.setText(Html.fromHtml(formattedText, Html.FROM_HTML_MODE_LEGACY));
        combinedTextView.setTextSize(14);

    }


    public void setBackgroundDrawable(@DrawableRes int drawableResId) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), drawableResId);
        if (drawable != null) {
            super.setBackground(drawable);
        }
    }


    public void setTextColor(int color) {
        combinedTextView.setTextColor(color);
    }

    public void setIcons(int startIconResId, int endIconResId) {
        Drawable startDrawable = null;
        Drawable endDrawable = null;

        if (startIconResId != -1) {
            startDrawable = ContextCompat.getDrawable(getContext(), startIconResId);
            if (startDrawable != null) {
                int padding = dpToPx(getContext(), 20); // Padding value
                startDrawable = applyPaddingToDrawable(startDrawable, padding);
            }
        }

        if (endIconResId != -1) {
            endDrawable = ContextCompat.getDrawable(getContext(), endIconResId);
            if (endDrawable != null) {
                int padding = dpToPx(getContext(), 15); // Padding value
                endDrawable = applyPaddingToDrawable(endDrawable, padding);
            }
        }

        combinedTextView.setCompoundDrawablesWithIntrinsicBounds(startDrawable, null, endDrawable, null);
    }

    public void setStartIcon(int resId) {
        if (resId != -1) {
            Drawable startDrawable = ContextCompat.getDrawable(getContext(), resId);
            if (startDrawable != null) {
                int padding = dpToPx(getContext(), 10); // Padding value
                startDrawable = applyPaddingToDrawable(startDrawable, padding);
            }
            combinedTextView.setCompoundDrawablesWithIntrinsicBounds(startDrawable, null, null, null);
        } else {
            combinedTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    public void setEndIcon(int resId) {
        if (resId != -1) {
            Drawable endDrawable = ContextCompat.getDrawable(getContext(), resId);
            if (endDrawable != null) {
                int padding = dpToPx(getContext(), 10); // Padding value
                endDrawable = applyPaddingToDrawable(endDrawable, padding);
            }
            combinedTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, endDrawable, null);
        } else {
            combinedTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    private Drawable applyPaddingToDrawable(Drawable drawable, int padding) {
        return new InsetDrawable(drawable, padding, 0, padding, 0);
    }

    private int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
