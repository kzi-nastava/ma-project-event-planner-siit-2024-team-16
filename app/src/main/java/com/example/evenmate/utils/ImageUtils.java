package com.example.evenmate.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import com.example.evenmate.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {
    private static final int MAX_IMAGE_SIZE = 800;

    private final Context context;
    private final ActivityResultLauncher<String> permissionLauncher;
    private final ActivityResultLauncher<Intent> imagePickerLauncher;
    private final ImageHandlerCallback callback;

    public interface ImageHandlerCallback {
        void onImageSelected(String base64Image, Bitmap bitmap);
        void onImageError(String error);
    }

    public ImageUtils(Fragment fragment, ImageHandlerCallback callback) {
        this.context = fragment.requireContext();
        this.callback = callback;

        this.permissionLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchImagePicker();
                    } else {
                        callback.onImageError("Permission required to select image");
                    }
                }
        );

        this.imagePickerLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        handleImageResult(result.getData());
                    }
                }
        );
    }

    public void selectImage() {
        checkPermissionAndPickImage();
    }

    private void checkPermissionAndPickImage() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(permission);
        } else {
            launchImagePicker();
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void handleImageResult(Intent data) {
        try {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                Bitmap originalBitmap = ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(context.getContentResolver(), imageUri)
                );

                Bitmap resizedBitmap = resizeImage(originalBitmap);
                String base64Image = convertBitmapToBase64(resizedBitmap);

                callback.onImageSelected(base64Image, resizedBitmap);
            }
        } catch (IOException e) {
            callback.onImageError("Failed to load image");
        }
    }

    private Bitmap resizeImage(Bitmap original) {
        int width = original.getWidth();
        int height = original.getHeight();

        if (width <= MAX_IMAGE_SIZE && height <= MAX_IMAGE_SIZE) {
            return original;
        }

        float ratio = Math.min(
                (float) MAX_IMAGE_SIZE / width,
                (float) MAX_IMAGE_SIZE / height
        );

        int finalWidth = Math.round(width * ratio);
        int finalHeight = Math.round(height * ratio);

        return Bitmap.createScaledBitmap(original, finalWidth, finalHeight, true);
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean hasAlpha = bitmap.hasAlpha();

        if (hasAlpha) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            String base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
            return "data:image/png;base64," + base64String;
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            String base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
            return "data:image/jpeg;base64," + base64String;
        }
    }

    public static void setImageFromBase64(ImageView imageView, String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_upload_image);
            return;
        }

        try {
            String base64Data = base64Image;
            if (base64Image.contains(",")) {
                base64Data = base64Image.split(",")[1];
            }

            byte[] decodedString = Base64.decode(base64Data, Base64.DEFAULT);
            Bitmap decodedBitmap = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            if (decodedBitmap != null) {
                imageView.setImageBitmap(decodedBitmap);
            } else {
                imageView.setImageResource(R.drawable.ic_error);
            }
        } catch (IllegalArgumentException e) {
            imageView.setImageResource(R.drawable.ic_error);
        }
    }
}