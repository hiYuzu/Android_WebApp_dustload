package cn.hb712.webapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cn.hb712.webapp.Utils.WebViewCookiesUtils;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();

    private Button mLoginButton;
    private TextView mMessageText;
    private TextView mResultText;
    private EditText mUsernameEdit;
    private EditText mPasswordEdit;
    private CheckBox mSaveUsernameCheckBox;
    private CheckBox mSuperFlag;
    public static Boolean superFlag = false;
    private ImageView mLoadingImage;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mActionBar = getSupportActionBar();
        String title = this.getString(R.string.app_name);
        mActionBar.setTitle(Html.fromHtml("<font color='#000000'>" + title + "</font>"));

        mSaveUsernameCheckBox = findViewById(R.id.save_username_passwd);
        mSuperFlag = findViewById(R.id.super_flag);
        mUsernameEdit = findViewById(R.id.username);
        mPasswordEdit = findViewById(R.id.password);
        mMessageText = findViewById(R.id.message);
        mResultText = findViewById(R.id.result_message);
        mLoginButton = findViewById(R.id.login_button);
        mLoadingImage = findViewById(R.id.loading_image);

        mMessageText.setVisibility(View.GONE);
        mResultText.setVisibility(View.GONE);
        mLoadingImage.setVisibility(View.GONE);


        MainApplication application = MainApplication.getInstance();
        application.removePassword();

        String username = application.getUsername();

        mUsernameEdit.setText(username);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromInputMethod(v.getWindowToken(), 0);
                }

                String username = mUsernameEdit.getText().toString();
                String password = mPasswordEdit.getText().toString();

                mLoginButton.setEnabled(false);
                mUsernameEdit.setEnabled(false);
                mPasswordEdit.setEnabled(false);
                mSaveUsernameCheckBox.setEnabled(false);
                mSuperFlag.setEnabled(false);

                if(username.isEmpty() || password.isEmpty())
                {
                    onLoginFailed("请输入用户名密码");
                    return;
                }

                startLoginAction(username, password);
            }
        });

    }

    private void startLoginAction(final String username, final String password) {
        superFlag = mSuperFlag.isChecked();
        MainApplication.getInstance().getWebServiceClient().runLoginTask(new WebServiceClient.TaskHandler() {
            @Override
            public void onStart() {
                mResultText.setVisibility(View.GONE);
                mMessageText.setVisibility(View.VISIBLE);
                mMessageText.setText("正在登录...");
                Animation loadingAnim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.loading);
                loadingAnim.setRepeatCount(Animation.INFINITE);
                mLoadingImage.startAnimation(loadingAnim);
                mLoadingImage.setVisibility(View.VISIBLE);


            }

            @Override
            public void onSuccess(JSONObject obj) {
                Log.d(TAG, "Login onSuccess: " + obj.toString());
                try {
                    String token = obj.getString("token");
                    WebViewCookiesUtils.saveCookie(MainApplication.baseUrl, "token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Boolean saveUsername = mSaveUsernameCheckBox.isChecked();
                if(saveUsername) {
                    MainApplication.getInstance().saveUsername(username);
                    MainApplication.getInstance().savePassword(password);
                }

                gotoMainActivity(username, password);

            }

            @Override
            public void onFailed(String errMsg) {
                Log.d(TAG, "onLoginResult: Failed :" + errMsg);
                onLoginFailed(errMsg);
            }
        }, username, password);

    }

    private void onLoginFailed(String message){
        mLoginButton.setEnabled(true);
        mUsernameEdit.setEnabled(true);
        mPasswordEdit.setEnabled(true);
        mSaveUsernameCheckBox.setEnabled(true);
        mSuperFlag.setEnabled(true);

        mMessageText.setVisibility(View.GONE);
        mResultText.setVisibility(View.VISIBLE);
        mResultText.setText(message);

        mLoadingImage.clearAnimation();
        mLoadingImage.setVisibility(View.GONE);
    }

    private void gotoMainActivity(String username, String password) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_USERNAME, username);
        intent.putExtra(MainActivity.EXTRA_PASSWORD, password);
        startActivity(intent);
        finish();
    }

}
