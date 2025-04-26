package com.softec.lifeaiassistant.customClasses;



import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;


public class AppDialogBuilder {
    final private BottomSheetDialog dialog;
    final private LinearLayout layout;
    private boolean isCancelable = true;
    private OnActionListener backPressedAction, dialogShownListener;
    public AppDialogBuilder(@NonNull Context cx) {
        dialog = new BottomSheetDialog(cx);
        dialog.getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                if (backPressedAction != null) backPressedAction.onAction();
            }
        });
        layout = new LinearLayout(cx);
        ViewCompat.setOnApplyWindowInsetsListener(layout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        layout.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
        layout.setBackgroundResource(android.R.color.transparent);
        layout.setGravity(BOTTOM);
        //layout.setOnClickListener(view -> {
//            if (isCancelable) dismiss();
        //      });
        if (dialog.getWindow() != null) dialog.getWindow().setContentView(layout);
        setDismissListener(null);
        dismiss();
    }
    public AppDialogBuilder(@NonNull Context cx, @NonNull View v) {
        this(cx);
        setContentView(v);
    }
    public AppDialogBuilder(@NonNull Context cx, @LayoutRes int id) {
        this(cx);
        setContentView(id);
    }
    public void setDismissListener (OnActionListener action) {
        dialog.setOnDismissListener(dialogInterface -> {
            if (action!=null) action.onAction();
        });
    }

    public void setCancelable (boolean cancelable) {
        isCancelable = cancelable;
        dialog.setCancelable(cancelable);
    }
    public void setDimBackground (float dim) {
        if (dialog.getWindow()!=null)
            dialog.getWindow().setDimAmount(dim);
    }
    public final boolean isCancelable () {
        return isCancelable;
    }

    public void show () {
        if (dialogShownListener != null) dialogShownListener.onAction();
        dialog.show();
    }
    public void dismiss () {
        dialog.dismiss();
    }

    public void setContentView (View v) {
        layout.removeAllViews();
        if (v == null) return;
        if (v.getParent() != null) ((ViewGroup)v.getParent()).removeView(v);
        layout.addView(v);
    }
    public void setContentView (@LayoutRes int id) {
        layout.removeAllViews();
        LayoutInflater.from(layout.getContext()).inflate(id, layout);
    }
    public final <T extends View> T findViewById (@IdRes int id) {
        return layout.findViewById(id);
    }
    public void setContentGravity (int gravity) {
        layout.setGravity(gravity);
    }
    public void switchToWindowContentView (boolean windowMode) {
        if (windowMode) {
            layout.setGravity(CENTER);

        }
        else layout.setGravity(BOTTOM);
    }

    public void setDialogShownListener(OnActionListener dialogShownListener) {
        this.dialogShownListener = dialogShownListener;
    }
    public void setBackPressedAction(OnActionListener action) {
        backPressedAction = action;
    }
    public interface OnActionListener {
        void onAction();
    }
    public View getRootView() {
        return layout; // Returns the root LinearLayout
    }

}