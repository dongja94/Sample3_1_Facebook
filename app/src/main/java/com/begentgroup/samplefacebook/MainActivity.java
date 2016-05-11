package com.begentgroup.samplefacebook;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    LoginManager mLoginManager;
    AccessTokenTracker mTracker;

    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);
        callbackManager = CallbackManager.Factory.create();
        mLoginManager = LoginManager.getInstance();
//        LoginButton btn = (LoginButton)findViewById(R.id.login_button);
//        btn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });

        loginButton = (Button)findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin()) {
                    mLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Toast.makeText(MainActivity.this, "login success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(MainActivity.this, "login cancel", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Toast.makeText(MainActivity.this, "login fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                    mLoginManager.logInWithReadPermissions(MainActivity.this, Arrays.asList("email"));
                } else {
                    mLoginManager.logOut();
                }
            }
        });

        Button btn = (Button)findViewById(R.id.btn_my_info);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyInfo();
            }
        });
    }


    @OnClick(R.id.btn_post)
    public void onPost(){

    }

    @OnClick(R.id.btn_my_info)
    public void onMyInfo() {
        if (isLogin()) {
            new MyFacebookInfoTask().execute();
        }
    }


    private static final String SERVER = "https://graph.facebook.com";
    private static final String MY_INFO = SERVER+"/v2.6/me?fields=id,name,email&access_token=%s";

    class MyFacebookInfoTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            AccessToken token = AccessToken.getCurrentAccessToken();
            try {

                URL url = new URL(String.format(MY_INFO,token.getToken()));
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                int code = conn.getResponseCode();
                if (code >= 200 && code < 300) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while((line=br.readLine()) != null) {
                        sb.append(line).append("\n\r");
                    }
                    return sb.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Toast.makeText(MainActivity.this, "info : " + s , Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mTracker == null) {
            mTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    changeButtonText();
                }
            };
        } else {
            mTracker.startTracking();
        }
        changeButtonText();
    }

    private void changeButtonText() {
        if (isLogin()) {
            loginButton.setText("logout");
        } else {
            loginButton.setText("login");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTracker.stopTracking();
    }

    private boolean isLogin() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        return token!=null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
