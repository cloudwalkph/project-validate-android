package com.cloudwalk.validate.validateapp.data.remote;

import android.util.Log;

import com.cloudwalk.validate.validateapp.App;
import com.cloudwalk.validate.validateapp.data.AppDataStore;
import com.cloudwalk.validate.validateapp.data.local.AppLocalDataStore;
import com.cloudwalk.validate.validateapp.data.local.models.Assignment;
import com.cloudwalk.validate.validateapp.data.local.models.Employee;
import com.cloudwalk.validate.validateapp.data.local.models.Event;
import com.cloudwalk.validate.validateapp.data.local.models.Question;
import com.cloudwalk.validate.validateapp.data.local.models.TeamLeader;

import java.util.List;

import javax.inject.Inject;

import dagger.Component;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by nkmcheng on 03/01/2017.
 */

public class AppRemoteDataStore implements AppDataStore {
    @Inject
    Retrofit retrofit;

    @Inject
    AppLocalDataStore appLocalDataStore;

    public AppRemoteDataStore() {
        App.getAppComponent().inject(this);
    }

    @Override
    public Observable<List<Employee>> getEmployees() {
        Log.d("REMOTE EMPLOYEES","Loaded from remote");

        return retrofit.create(EmployeeService.class).getEmployeeList().doOnNext(new Action1<List<Employee>>() {
            @Override
            public void call(List<Employee> employees) {
                appLocalDataStore.saveEmployeeToDatabase(employees);
            }
        });
    }

    private interface EmployeeService {
        @GET("/admin/getAllEmployee")
        Observable<List<Employee>> getEmployeeList();
    }

    @Override
    public Observable<List<Event>> getEvents() {
        Log.d("REMOTE EVENTS","Loaded from remote");

        return retrofit.create(EventService.class).getEventList().doOnNext(new Action1<List<Event>>() {
            @Override
            public void call(List<Event> events) {
                appLocalDataStore.saveEventToDatabase(events);
            }
        });
    }

    private interface EventService {
        @GET("/admin/getAllEvents")
        Observable<List<Event>> getEventList();
    }

    @Override
    public Observable<List<Question>> getQuestions() {
        Log.d("REMOTE QUESTIONS","Loaded from remote");

        return retrofit.create(QuestionService.class).getQuestionList().doOnNext(new Action1<List<Question>>() {
            @Override
            public void call(List<Question> questions) {
                appLocalDataStore.saveQuestionToDatabase(questions);
            }
        });
    }

    private interface QuestionService {
        @GET("/admin/getAllQuestion")
        Observable<List<Question>> getQuestionList();
    }

    @Override
    public Observable<List<Assignment>> getAssignments() {
        Log.d("REMOTE ASSIGNMENT","Loaded from remote");

        return retrofit.create(AssignmentService.class).getAssignmentList().doOnNext(new Action1<List<Assignment>>() {
            @Override
            public void call(List<Assignment> assignments) {
                appLocalDataStore.saveAssignmentToDatabase(assignments);
            }
        });
    }

    private interface AssignmentService {
        @GET("/admin/getAllAsignQuestion")
        Observable<List<Assignment>> getAssignmentList();
    }

    @Override
    public Observable<List<TeamLeader>> getTeamLeaders() {
        Log.d("REMOTE TEAM LEADER","Loaded from remote");

        return retrofit.create(TeamLeaderService.class).getTeamLeaderList().doOnNext(new Action1<List<TeamLeader>>() {
            @Override
            public void call(List<TeamLeader> teamLeaders) {
                appLocalDataStore.saveTeamLeaderToDatabase(teamLeaders);
            }
        });
    }

    private interface TeamLeaderService {
        @GET("/admin/getAllTls")
        Observable<List<TeamLeader>> getTeamLeaderList();
    }

}
