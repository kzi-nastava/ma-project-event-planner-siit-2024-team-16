package com.example.evenmate.utils;

import android.content.Context;

import com.example.evenmate.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DialogUtils {
    public interface ConfirmationCallback {
        void onResult(boolean confirmed);
    }
    public static void showDeleteConfirmation(Context context, ConfirmationCallback callback) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.delete_dialog_title)
                .setMessage(R.string.delete_dialog_text)
                .setPositiveButton(R.string.delete, (dialog, which) -> callback.onResult(true))
                .setNegativeButton(R.string.cancel, (dialog, which) -> callback.onResult(false))
                .show();
    }
}
