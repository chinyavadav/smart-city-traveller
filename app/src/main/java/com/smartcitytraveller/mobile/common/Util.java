package com.smartcitytraveller.mobile.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.database.DbHandler;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;

import java.io.IOException;

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

    public static void loadAvatar(UserDto userDto, ImageView imageView) {
        if (userDto.getAvatar() != null) {
            byte[] imageAsBytes = Base64.decode(userDto.getAvatar(), Base64.DEFAULT);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
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

}
