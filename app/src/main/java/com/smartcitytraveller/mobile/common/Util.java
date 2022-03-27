package com.smartcitytraveller.mobile.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.database.DbHandler;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Response;

public class Util {
    private static final String TAG = Util.class.getSimpleName();

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {

        }
    }

    public static void subscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, String.format("failed to subscribe to %s topic", topic));
                        }
                        Log.d(TAG, String.format("successfully subscribed to %s topic", topic));
                    }
                });
    }

    public static void unsubscribeTopic(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

    public static Bitmap generateQRCode(String contents) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(contents, BarcodeFormat.QR_CODE, 400, 400);
        } catch (Exception e) {
            return null;
        }
    }

    public static void loadAvatar(UserDto userDto, ImageView imageView) {
        if (userDto.getAvatar() != null) {
            byte[] imageAsBytes = Base64.decode(userDto.getAvatar(), Base64.DEFAULT);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
    }

    public static void showCameraPermissionRationale(Activity activity, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Camera Permission Explanation")
                .setMessage("eMalyami requires the camera permission to allow you to capture Profile Pictures, KYC Documents and scan QR Codes.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.CAMERA}, requestCode
                        );
                    }
                })
                .show();
    }

    public static JWT tokenToJWT(String token) {
        return new JWT(token.replace("Bearer ", ""));
    }

    public static boolean isSessionValid(SharedPreferencesManager sharedPreferencesManager) {
        String authenticationToken = sharedPreferencesManager.getAuthenticationToken();
        JWT jwt = null;
        if (authenticationToken != null) {
            Log.d(TAG, authenticationToken);
            jwt = tokenToJWT(authenticationToken);
            return jwt != null && !jwt.isExpired(0);
        }
        return false;
    }


    public static void clearSessionData(SharedPreferencesManager sharedPreferencesManager, Context context) {
        try {
            sharedPreferencesManager.clearAll();
            DbHandler dbHandler = new DbHandler(context);
            dbHandler.deleteAllProducts();
        } catch (Exception ignore) {
        }
    }

    public static File saveBitmap(Context context, Bitmap bitmap, String filename) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getFilesDir();
        File file = new File(directory, filename + ".png");

        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getFormattedTime(String pattern, Long timestamp) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }

    public static String[] splitCountryCodeFromPhone(String phoneNumber) throws NumberParseException {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phone = phoneNumberUtil.parse(phoneNumber, Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.name());
        return new String[]{"+" + phone.getCountryCode(), String.valueOf(phone.getNationalNumber())};
    }


    public static String handleHttpException(Response response) {
        try {
            String json = response.errorBody().string();
            JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
            return jsonObject.get("message").getAsString();
        } catch (IOException e) {
            Log.d(TAG, "Exception: ", e);
            return "Something went wrong!";
        }
    }

}
