package com.example.technologyden.User;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.technologyden.Models.Request;
import com.example.technologyden.Models.User;
import com.example.technologyden.Remote.IGeographicalCoordinates;
import com.example.technologyden.Remote.RetrofitClient;

public class UserLocalStored {

    public static User currentUser;
    public static Request currentRequest;
    public static final String baseUrl = "https://maps.googleapis.com";
    public static final String user_key = "User";
    public static final String user_password = "Password";

    public static IGeographicalCoordinates getGeographicalCoordinatesService() {
      return RetrofitClient.getClient(baseUrl).create(IGeographicalCoordinates.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int width, int height) {
        Bitmap scaledBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);

        float scaleX = width/(float)bitmap.getWidth();
        float scaleY = height/(float)bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if(networkInfos != null) {
                for (int i = 0; i < networkInfos.length; i++) {
                    if(networkInfos[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
