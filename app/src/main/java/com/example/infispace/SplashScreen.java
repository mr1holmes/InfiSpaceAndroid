package com.example.infispace;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.infispace.service.FetchUserFriends;
import com.example.infispace.util.AccountsUtil;
import com.example.infispace.util.LogUtil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

public class SplashScreen extends AppCompatActivity {

    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private ProgressBar mProgressBar;
    private final String TAG = LogUtil.makeLogTag(this.getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_splash_screen);

        mProgressBar = (ProgressBar) findViewById(R.id.login_progress);

        mLoginButton = (LoginButton) findViewById(R.id.login_button);
        mLoginButton.setReadPermissions(AccountsUtil.FACEBOOK_PERMISSIONS);
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        LogUtil.LOGD(TAG, "permissions = " + AccessToken.getCurrentAccessToken().getPermissions());


                        // Check if all required permissions are provided
                        if (AccountsUtil.isLoggedIn()) {
                            LogUtil.LOGV(TAG, "User provided all the permissions");

                            mLoginButton.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.VISIBLE);

                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
//                                            parseAndSend(object);
                                        }
                                    });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,first_name,last_name,picture{url}");
                            request.setParameters(parameters);
                            request.executeAsync();

                            // Fetch friends of this user and add them to database
                            Intent fetchUserService = new Intent(SplashScreen.this, FetchUserFriends.class);
                            startService(fetchUserService);

                        } else {
                            LogUtil.LOGV(TAG, "User did not provide some permissions, try again");
                            LoginManager.getInstance().logInWithReadPermissions(SplashScreen.this,
                                    Arrays.asList(AccountsUtil.FACEBOOK_PERMISSIONS));
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Toast.makeText(SplashScreen.this, "Cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(SplashScreen.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });

        if (AccountsUtil.isLoggedIn()) {
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
}
