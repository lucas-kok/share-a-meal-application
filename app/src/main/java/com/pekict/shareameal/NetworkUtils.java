package com.pekict.shareameal;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    private static final String TAG_NAME = NetworkUtils.class.getSimpleName();

    private static final String LOGIN_BASE_URL = "https://shareameal-api.herokuapp.com/api/auth/login";
    private static final String QUERY_PARAM = "q";
    private static final String MAX_RESULTS = "maxResults";
    private static final String PRINT_TYPE = "printType";

    static String getLogin(String queryString) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String loginJSONString = null;

        try {
            Uri builtURI = Uri.parse(LOGIN_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, queryString)
                .appendQueryParameter(MAX_RESULTS, "1")
                .appendQueryParameter(PRINT_TYPE, "login")
                .build();

            URL requestURL = new URL(builtURI.toString());

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            // Stream was empty. No need for parsing
            if (builder.length() == 0) {
                return null;
            }

            loginJSONString = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return loginJSONString;
    }

}
