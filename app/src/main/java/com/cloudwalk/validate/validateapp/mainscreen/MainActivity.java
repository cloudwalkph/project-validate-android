package com.cloudwalk.validate.validateapp.mainscreen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cloudwalk.validate.validateapp.App;
import com.cloudwalk.validate.validateapp.R;
import com.cloudwalk.validate.validateapp.data.AppRepository;
import com.cloudwalk.validate.validateapp.data.local.models.Assignment;
import com.cloudwalk.validate.validateapp.data.local.models.Event;
import com.cloudwalk.validate.validateapp.eventproperscreen.EventProperActivity;
import com.cloudwalk.validate.validateapp.loginscreen.LoginActivity;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements MainScreenContract.View {

    public List<Event> eventList;
    public static List<Assignment> mAssignments;
    private RecyclerView.LayoutManager mLayoutManager;
    private MainScreenContract.Presenter mPresenter;

    public ProgressDialog mProgres;
    public RecyclerView mRvEvents;
    EventsAdapter adapter;

    @Inject
    AppRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_event);
        setTitle("Choose an Event");
        Stetho.initializeWithDefaults(this);

        //Inject dependency
        App.getAppComponent().inject(this);
        new MainScreenPresenter(repository, this);

        mRvEvents = (RecyclerView) findViewById(R.id.rv_events);

        // Initialize events
        eventList = new ArrayList<Event>();

        mProgres = new ProgressDialog(MainActivity.this);
        mProgres.setCancelable(false);

        adapter = new EventsAdapter(this, eventList);
        mRvEvents.setAdapter(adapter);
        mRvEvents.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                EventProperActivity.mCurrentEvent = null;
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.sync_btn:
                mProgres.show();

                mPresenter.loadEmployeeFromRemoteDataStore();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void setPresenter(MainScreenContract.Presenter presenter) {
        this.mPresenter = presenter;
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

    @Override
    public void getAssignments(List<Assignment> assignments) {
        mAssignments = assignments;
        for (Assignment assignment : assignments) {
            Log.i("MAINSCREEN ASSIGNMENT", assignment.getQevent().toString());

            // Get the event
            mPresenter.getEventById(Integer.parseInt(assignment.getQevent()));
        }
    }

    @Override
    public void getEvents(Event event) {
        Log.i("MAINSCREEN EVENT", event.getName());

        boolean found = false;
        for (Event ev : eventList) {
            if (ev.getId() == event.getId()) {
                found = true;
                break;
            }
        }

        // Not found, add the event
        if (! found) {
            eventList.add(event);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setProgressMessage(String message) {
        if (mProgres.isShowing() && mProgres != null) {
            mProgres.setMessage(message);
        }
    }

    @Override
    public void syncFinish() {
        if (mProgres.isShowing() && mProgres != null) {
            mProgres.dismiss();
            adapter.notifyDataSetChanged();
//            mRvEvents.invalidate();
        }
    }
}
