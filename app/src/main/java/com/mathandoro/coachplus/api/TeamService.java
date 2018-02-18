package com.mathandoro.coachplus.api;

import com.mathandoro.coachplus.models.RegisterTeam;
import com.mathandoro.coachplus.api.Response.ApiResponse;
import com.mathandoro.coachplus.api.Response.CreateEventResponse;
import com.mathandoro.coachplus.models.Event;
import com.mathandoro.coachplus.api.Response.EventsResponse;
import com.mathandoro.coachplus.api.Response.InvitationResponse;
import com.mathandoro.coachplus.api.Response.TeamMembersResponse;
import com.mathandoro.coachplus.models.Team;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by d059458 on 03.04.17.
 */

public interface TeamService {

    @POST("teams/register")
    Call<ApiResponse<Team>> registerTeam(@Header("X-ACCESS-TOKEN") String accessToken, @Body RegisterTeam team);

    @GET("teams/{teamId}/members")
    Call<ApiResponse<TeamMembersResponse>> getTeamMembers(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId);

    @GET("teams/{teamId}/events")
    Call<ApiResponse<EventsResponse>> getEventsOfTeam(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId);

    @POST("teams/{teamId}/events")
    Call<ApiResponse<CreateEventResponse>> createEvent(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId, @Body Event event);

    @POST("teams/private/join/{token}")
    Call<ApiResponse<Object>> joinPrivateTeam(@Header("X-ACCESS-TOKEN") String accessToken, @Path("token") String token);

    @POST("teams/public/join/{teamId}")
    Call<ApiResponse<Object>> joinPublicTeam(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId);

    @POST("teams/{teamId}/invite")
    Call<ApiResponse<InvitationResponse>> createInvitationUrl(@Header("X-ACCESS-TOKEN") String accessToken, @Path("teamId") String teamId);
}
