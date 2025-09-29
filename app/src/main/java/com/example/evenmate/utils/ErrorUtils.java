package com.example.evenmate.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Response;

public class ErrorUtils {
    public static void showErrorToast(Response<?> response, Context context) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();

                JsonObject obj = JsonParser.parseString(errorJson).getAsJsonObject();

                String message = obj.has("message")
                        ? obj.get("message").getAsString()
                        : "Something went wrong";

                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Unexpected error", Toast.LENGTH_LONG).show();
        }
    }
}
