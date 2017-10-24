package com.example.infispace;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.infispace.data.InfiContract;
import com.example.infispace.util.AccountsUtil;
import com.example.infispace.util.LogUtil;
import com.example.infispace.util.VolleySingleton;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class SplashScreen extends AppCompatActivity {

    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private ProgressBar mProgressBar;
    private TextView mSplashInfo;
    private Button mChangeServerBtn;
    private final String TAG = LogUtil.makeLogTag(this.getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_splash_screen);

        mProgressBar = (ProgressBar) findViewById(R.id.login_progress);
        mSplashInfo = (TextView) findViewById(R.id.splash_info);
        mChangeServerBtn = (Button) findViewById(R.id.change_server_btn);
        mChangeServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SplashScreen.this);

                alert.setTitle("Server URL");
                alert.setMessage("Change Server URL");

                final EditText input = new EditText(SplashScreen.this);
                alert.setView(input);
                input.setText(AccountsUtil.getServerUrl(SplashScreen.this));

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        AccountsUtil.setServerUrl(value, SplashScreen.this);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });

        mLoginButton = (LoginButton) findViewById(R.id.login_button);
        mLoginButton.setReadPermissions(AccountsUtil.FACEBOOK_PERMISSIONS);
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        mChangeServerBtn.setVisibility(View.GONE);
                        LogUtil.LOGD(TAG, "permissions = " + AccessToken.getCurrentAccessToken().getPermissions());


                        // Check if all required permissions are provided
                        if (AccountsUtil.isLoggedIn()) {
                            LogUtil.LOGV(TAG, "User provided all the permissions");

                            mLoginButton.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.VISIBLE);

                            mSplashInfo.setText(getString(R.string.fetch_fb_string));

                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            parseAndSend(object);
                                        }
                                    });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,first_name,last_name,picture.type(large)");
                            request.setParameters(parameters);
                            request.executeAsync();

                            // Fetch friends of this user and add them to database
                            fetchUserFriends(loginResult.getAccessToken());

                        } else {
                            LogUtil.LOGV(TAG, "User did not provide some permissions, try again");
                            LoginManager.getInstance().logInWithReadPermissions(SplashScreen.this,
                                    Arrays.asList(AccountsUtil.FACEBOOK_PERMISSIONS));
                        }
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(SplashScreen.this, "Cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(SplashScreen.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });

        if (AccountsUtil.isLoggedIn()) {
            mChangeServerBtn.setVisibility(View.GONE);
            // show startup screen for few second and then move to next screen
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startNext();
                }
            }, 1500);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mLoginButton.setVisibility(View.VISIBLE);
        }
    }

    private void startNext() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void parseAndSend(JSONObject responseObject) {
        try {

            // Get data from facebook response
            String user_id = responseObject.getString("id");
            String first_name = responseObject.getString("first_name");
            String last_name = responseObject.getString("last_name");
            String profile_url = DatabaseUtils.sqlEscapeString(
                    responseObject.getJSONObject("picture").getJSONObject("data").getString("url"));

            // Add user in local database
            ContentValues contentValues = new ContentValues();
            contentValues.put(InfiContract.TABLE_USER.COLUMN_USER_ID, user_id);
            contentValues.put(InfiContract.TABLE_USER.COLUMN_FIRST_NAME, first_name);
            contentValues.put(InfiContract.TABLE_USER.COLUMN_LAST_NAME, last_name);
            contentValues.put(InfiContract.TABLE_USER.COLUMN_PROFILE_URL, DatabaseUtils.sqlEscapeString(profile_url));
            try {
                getContentResolver().insert(InfiContract.TABLE_USER.CONTENT_URI, contentValues);
            } catch (Exception se) {
                se.printStackTrace();
            }

            AccountsUtil.setUserId(user_id, this);

            mSplashInfo.setText(getString(R.string.send_data_string));
            JSONObject dataObj = new JSONObject();
            dataObj.put("type", "user");
            dataObj.put(InfiContract.TABLE_USER.COLUMN_USER_ID, user_id);
            dataObj.put(InfiContract.TABLE_USER.COLUMN_FIRST_NAME, first_name);
            dataObj.put(InfiContract.TABLE_USER.COLUMN_LAST_NAME, last_name);
            dataObj.put(InfiContract.TABLE_USER.COLUMN_PROFILE_URL, profile_url);
            JSONObject requestObject = new JSONObject();
            requestObject.put("data", dataObj);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    AccountsUtil.getServerUrl(SplashScreen.this) + "/users", requestObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    LogUtil.LOGV(TAG, "User added on server " + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    LogUtil.LOGD(TAG, "Something went wrong while adding user on server");
                }
            });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    private void sendFriendsToServer(JSONObject payload) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                AccountsUtil.getServerUrl(SplashScreen.this) + "/friendship", payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.LOGV(TAG, "Friends added on server " + response);
                startNext();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.LOGD(TAG, "Something went wrong while adding friends on server");
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    private void fetchUserFriends(AccessToken accessToken) {
        LogUtil.LOGD(TAG, "Fetching friends started");

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        JSONObject responseObject = response.getJSONObject();
                        try {
                            JSONObject payload = new JSONObject();
                            JSONArray data = responseObject.getJSONObject("friends").getJSONArray("data");
                            payload.put("data", data);
                            payload.put("user_id", AccountsUtil.getUserId(SplashScreen.this));
                            sendFriendsToServer(payload);
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "friends{id,first_name,last_name,picture.type(large)}");
        request.setParameters(parameters);
        request.executeAsync();

    }
}
