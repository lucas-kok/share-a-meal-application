package com.pekict.shareameal.datastorage;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pekict.shareameal.domain.LoginData;
import com.pekict.shareameal.domain.LoginResponse;
import com.pekict.shareameal.domain.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRepository {

    private MutableLiveData<User> mUser;
    private MutableLiveData<User> mUserProfile;
    private static volatile UserRepository instance;
    private final static String TAG = UserRepository.class.getSimpleName();

    // Private - Singleton pattern!
    private UserRepository(Application application) {
        // Niet vergeten, nullpointer als je deze niet initialiseert zoals hieronder.
        mUser = new MutableLiveData<>();
        mUserProfile = new MutableLiveData<>();
    }

    // Get instance of Singleton WordRepository
    public static UserRepository getInstance(Application application) {
        if (instance == null) {
            instance = new UserRepository(application);
        }
        return instance;
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<User> getUser() {
        return mUser;
    }

    public void login(String emailAdress, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://shareameal-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ShareAMealApiService service = retrofit.create(ShareAMealApiService.class);

        LoginData body = new LoginData(emailAdress, password);
        Log.d(TAG, "Calling login on service");

        Call<LoginResponse> call = service.login(body);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                User loggedInUser = response.body().getResult();
                mUser.setValue(loggedInUser);
                Log.d(TAG, "onPostExecute user logged in: " + loggedInUser.getDisplayName());
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(TAG, "Error logging in: " + t.getMessage());
            }
        });
    }

    /**
     * Om het UserProfile op te halen moet de user eerst ingelogd zijn. We sturen een
     * JWT token mee om de user te identificeren/authenticeren. Als dat succesvol is
     * krijgen we het profile van de ingelogde user terug.
     */
    public void getUserProfile(String jwtToken) {
        // Gebruik Retrofit om op de API in te loggen
        Log.d(TAG, "getProfile - User moet ingelogd zijn!");

        new UserProfileAsyncTask().execute(jwtToken);
    }

    public LiveData<User> userProfile() {
        return mUserProfile;
    }


    public void logout() {
        // TODO: revoke authentication
    }


    /**
     * Toegevoegd om login asynchroon te maken
     * <p>
     * Let op: in dit geval is het profile dat we terug krijgen toevallig precies hetzelfde
     * als de LoginResponse die we al eerder gemaakt hebben. We hoeven dus geen nieuw Response
     * class te maken. Als je een ander API endpoint aanspreekt moet je dat wel doen!
     */
    private class UserProfileAsyncTask extends AsyncTask<String, Void, LoginResponse> {

        @Override
        protected LoginResponse doInBackground(String... strings) {
            // Todo: check of we een token hebben, anders null of errormelding
            // De server authenticatie vereist een authorizatie header in de vorm
            // "Bearer <jwt token>". Dat is vastgelegd in de JWT specificatie.
            //
            String token = "Bearer " + strings[0];

            try {
                // handle loggedInUser authentication
                Log.d(TAG, "doInBackground - stuur token mee");

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://shareameal-api.herokuapp.com/")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                ShareAMealApiService service = retrofit.create(ShareAMealApiService.class);

                Log.d(TAG, "Calling getProfile on service - sending JWT Token!");
                Call<LoginResponse> call = service.getUserProfile(token);
                Response<LoginResponse> response = call.execute();

                Log.d(TAG, "Executed call, response.code = " + response.code());

                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Got result " + loginResponse.getResult().firstName);

                    return loginResponse;
                } else {
                    Log.d(TAG, "Error logging in: " + response.message());
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(LoginResponse result) {
            if (result != null) {
                User userProfile = result.getResult();
                mUserProfile.setValue(userProfile);
                Log.d(TAG, "onPostExecute user profile found : " + userProfile.getDisplayName());
            } else {
                // User niet ingelogd, doe hier iets beters.
            }
        }
    }
}
