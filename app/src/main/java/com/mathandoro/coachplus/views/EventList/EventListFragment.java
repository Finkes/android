package com.mathandoro.coachplus.views.EventList;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mathandoro.coachplus.api.Response.EventsResponse;
import com.mathandoro.coachplus.views.EventDetail.EventDetailActivity;
import com.mathandoro.coachplus.R;
import com.mathandoro.coachplus.persistence.DataLayer;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.functions.Consumer;

import static com.mathandoro.coachplus.views.EventDetail.EventDetailActivity.EXTRA_BUNDLE;
import static com.mathandoro.coachplus.views.EventDetail.EventDetailActivity.EXTRA_EVENT;
import static com.mathandoro.coachplus.views.EventDetail.EventDetailActivity.EXTRA_TEAM;


public class EventListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String PARAM_TEAM = "TEAM";
    private Team team;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private EventListAdapter eventListAdapter;
    protected DataLayer dataLayer;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected boolean showUpcomingEvents = true;


    public EventListFragment() {
        // Required empty public constructor
    }

    public void setShowUpcomingEvents(boolean showUpcomingEvents){
        this.showUpcomingEvents = showUpcomingEvents;
    }

    public static EventListFragment newInstance(Team team, boolean showUpcomingEvents) {
        EventListFragment fragment = new EventListFragment();
        fragment.setShowUpcomingEvents(showUpcomingEvents);
        Bundle args = new Bundle();
        args.putParcelable(PARAM_TEAM,  team);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataLayer = DataLayer.getInstance(this.getActivity());
        if (getArguments() != null) {
            team = getArguments().getParcelable(PARAM_TEAM);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.eventListSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = view.findViewById(R.id.eventListRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        eventListAdapter = new EventListAdapter((EventListActivity)getActivity(), this);
        mRecyclerView.setAdapter(eventListAdapter);

        this.loadEvents(true);
    }

    private Consumer<EventsResponse> eventsLoaded = eventsResponse -> {
        List<Event> events = eventsResponse.getEvents();
        List<Event> visibleEvents = this.filterVisibleEvents(events);
        eventListAdapter.setEvents(visibleEvents);
        swipeRefreshLayout.setRefreshing(false);
    };

    private Consumer<Throwable> eventsLoadedError = error -> {
        swipeRefreshLayout.setRefreshing(false);
    };

    private List<Event> filterVisibleEvents(List<Event> events){
        if(showUpcomingEvents){
            return this.filterUpcomingEvents(events);
        }
        return this.filterPastEvents(events);
    }

    private List<Event> filterUpcomingEvents(List<Event> events){
        List<Event> upcomingEvents = new ArrayList<Event>();
        for(Event event: events){
            if(event.getEnd().after(new Date())){
                upcomingEvents.add(event);
            }
        }
        Collections.sort(upcomingEvents, (eventA, eventB) -> eventA.getStart().compareTo(eventB.getStart()));
        return upcomingEvents;
    }

    private List<Event> filterPastEvents(List<Event> events){
        List<Event> pastEvents = new ArrayList<Event>();
        for(Event event: events){
            if(event.getEnd().before(new Date())){
                pastEvents.add(event);
            }
        }
        Collections.sort(pastEvents, (eventA, eventB) -> eventA.getEnd().compareTo(eventB.getEnd()) * -1);
        return pastEvents;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        this.loadEvents(false);
    }

    private void loadEvents(boolean useCache){
        dataLayer.getEvents(team, useCache).subscribe(eventsLoaded, eventsLoadedError);
    }

    void navigateToEventDetail(Event event){
        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_TEAM, team);
        bundle.putParcelable(EXTRA_EVENT, event);
        intent.putExtra(EXTRA_BUNDLE, bundle);
        startActivity(intent);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
