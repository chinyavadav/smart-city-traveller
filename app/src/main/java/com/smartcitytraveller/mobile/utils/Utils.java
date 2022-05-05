package com.smartcitytraveller.mobile.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;

import java.io.IOException;

import retrofit2.Response;

public class Utils {
  private static final String TAG = Utils.class.getSimpleName();

  public static void hideSoftKeyboard(Activity activity) {
    try {
      InputMethodManager inputMethodManager =
          (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    } catch (Exception ignored) {

    }
  }

  public static void loadAvatar(UserDto userDto, ImageView imageView) {
    if (userDto.getAvatar() != null) {
      byte[] imageAsBytes = Base64.decode(userDto.getAvatar(), Base64.DEFAULT);
      imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
    }
  }

  public static void clearSessionData(
      SharedPreferencesManager sharedPreferencesManager, Context context) {
    try {
      sharedPreferencesManager.clearAll();
    } catch (Exception ignore) {
    }
  }

  public static boolean isSessionValid(SharedPreferencesManager sharedPreferencesManager) {
    return sharedPreferencesManager.getUser() != null;
  }

  public static String handleHttpException(Response response) {
    try {
      String json = response.errorBody().string();
      JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
      if (jsonObject.get("error") != null) {
        return jsonObject.get("error").getAsString();
      }
    } catch (IOException e) {
      Log.d(TAG, "Exception: ", e);
    }
    return "Something went wrong!";
  }

  public static boolean hasLocationPermission(Context context) {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED;
  }

  public static void requestLocationPermission(Activity activity) {
    if (!hasLocationPermission(activity.getApplicationContext())) {
      ActivityCompat.requestPermissions(
          activity,
          new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
          },
          999);
    }
  }
}
