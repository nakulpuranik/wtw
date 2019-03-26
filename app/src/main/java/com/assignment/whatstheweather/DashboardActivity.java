package com.assignment.whatstheweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.assignment.whatstheweather.utils.Constants;
import com.assignment.whatstheweather.utils.ServerComm;
import com.assignment.whatstheweather.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Api which will check the user is already present or not
     */
    private void authenticateUser(final JSONObject requestParams) {
        try {
            new ServerComm(new ServerComm.ServerCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject loginResponseJson = new JSONObject(result);
                        String userName = loginResponseJson.getString("username");
                        Constants.AuthConstants.ACCESS_TOKEN = loginResponseJson.getString("access_token");

                        startActivity(new Intent(LoginActivity.this, ContainerActivity.class));
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(String result, int errorCode) throws JSONException {
                    Utils.showApiErrorMessage(LoginActivity.this, getString(R.string.error), getString(R.string.invalid_login_crediential));
                }
            }, LoginActivity.this).communicateWithServerJsonParameters(Constants.ServerUrl.REQUEST_TYPE_POST, Constants.ServerUrl.LOGIN_USER, requestParams, true, true, false, getResources().getString(R.string.please_wait));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
