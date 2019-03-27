package com.assignment.whatstheweather.utils;

import com.android.volley.Request;

public class Constants {
    public static final String BASE_URL = "https://api.openweathermap.org/";

    public static final String TOKEN = "7435a163dd59017aa5478f7379e0a7d9";

    public static final int MAX_RETRY_POLICY_VOLLEY = 60000;

    public static final class ServerUrl {
        public static final String LOGIN_USER= "";
    }
    public static final int REQUEST_TYPE_POST = Request.Method.POST;
    public static final int REQUEST_TYPE_GET = Request.Method.GET;
}
