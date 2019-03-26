package com.assignment.whatstheweather.interfaces;

import org.json.JSONException;

public interface ServerCallback {
    void onSuccess(String result) throws JSONException;
}
