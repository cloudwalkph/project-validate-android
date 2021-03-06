package com.cloudwalk.validate.validateapp.splashscreen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cloudwalk.validate.validateapp.App;
import com.cloudwalk.validate.validateapp.R;
import com.cloudwalk.validate.validateapp.data.AppRepository;
import com.cloudwalk.validate.validateapp.data.local.models.Employee;
import com.cloudwalk.validate.validateapp.data.remote.AppRemoteDataStore;
import com.cloudwalk.validate.validateapp.loginscreen.LoginActivity;
import com.facebook.stetho.Stetho;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity implements SplashScreenContract.View {

    private Subscription mSubscription;
    private SplashScreenContract.Presenter mPresenter;

    public ProgressDialog mProgress;

    @Inject
    AppRepository repository;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Inject dependency
        App.getAppComponent().inject(this);
        new SplashScreenPresenter(repository, this);

        mProgress = new ProgressDialog(this);
        mProgress.setCancelable(false);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
            }
        }, 1000);

        if (isNetworkAvailable()) {
            mProgress.show();
            mPresenter.loadEmployeeFromRemoteDataStore();
        } else {
            Toast.makeText(this, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
            syncFinish();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void setPresenter(SplashScreenContract.Presenter presenter) {
        mPresenter = presenter;
    }

//    @Override
//    public void showEmployeeCompleteSync() {
//        Toast.makeText(this, "Employee Sync Completed", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void showEventCompleteSync() {
//        Toast.makeText(this, "Event Sync Completed", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void showQuestionCompleteSync() {
//        Toast.makeText(this, "Question Sync Completed", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void showAssignmentCompleteSync() {
//        Toast.makeText(this, "Assignment Sync Completed", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void showTeamLeaderCompleteSync() {
//        Toast.makeText(this, "Team Leader Sync Completed", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void showNegotiatorCompleteSync() {
//        Toast.makeText(this, "Negotiator Sync Completed", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void showAnswerCompleteSync() {
//        Toast.makeText(this, "Answer Sync Completed", Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void setProgressMessage(String message) {
        mProgress.setMessage(message);
    }

    @Override
    public void syncFinish() {
        mProgress.dismiss();

        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void moveToLoginScreen() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }
}
