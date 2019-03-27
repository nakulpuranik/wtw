package com.assignment.whatstheweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.assignment.whatstheweather.utils.Constants;
import com.assignment.whatstheweather.utils.ServerComm;
import com.assignment.whatstheweather.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat", 35);
            jsonObject.put("lon", 139);
            jsonObject.put("appid", Constants.TOKEN);
            jsonObject.put("units", "metric");
            String queryString = getQueryString(jsonObject);
            authenticateUser(queryString);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public String getQueryString(JSONObject unparsedJson) throws JSONException {
        StringBuilder sb = new StringBuilder();
        JSONObject json = unparsedJson;
        Iterator<String> keys = json.keys();
        sb.append("?"); //start of query args
        while (keys.hasNext()) {
            String key = keys.next();
            sb.append(key);
            sb.append("=");
            sb.append(json.get(key));
            sb.append("&"); //To allow for another argument.
        }

        return sb.toString();
    }

    /**
     * Api which will check the user is already present or not
     */
    private void authenticateUser(final String requestParams) {
        try {
            new ServerComm(new ServerComm.ServerCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                       JSONObject loginResponseJson = new JSONObject(result);
                        Log.e("Resp Weather :: ",""+result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(String result, int errorCode) throws JSONException {
                    Utils.showApiErrorMessage(DashboardActivity.this, getString(R.string.error), "");
                }
            }, DashboardActivity.this).communicateWithServerJsonParameters(Constants.REQUEST_TYPE_GET, "data/2.5/weather"+ requestParams, null, true, true, false, getResources().getString(R.string.please_wait));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
