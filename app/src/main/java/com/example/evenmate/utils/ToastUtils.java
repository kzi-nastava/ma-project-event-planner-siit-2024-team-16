package com.example.evenmate.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.evenmate.R;

public class ToastUtils {

    public static void showCustomToast(Context context, String message, boolean isError) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View toastView = inflater.inflate(R.layout.custom_toast_layout, null);

        TextView toastText = toastView.findViewById(R.id.toast_text);
        ImageView iconView = toastView.findViewById(R.id.toast_icon);

        toastText.setText(message);

        if (isError) {
            toastText.setTextColor(ContextCompat.getColor(context, R.color.red));
            iconView.setImageResource(R.drawable.ic_error);
        } else {
            toastText.setTextColor(ContextCompat.getColor(context, R.color.green));
            iconView.setImageResource(R.drawable.ic_success);
        }

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastView);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }
}