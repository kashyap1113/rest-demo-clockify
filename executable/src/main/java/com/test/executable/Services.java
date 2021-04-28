package com.test.executable;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Services {
	@Headers({"X-Api-Key: X50mikTohhZrUuAK"})
	@GET("workspaces/{workspaceId}/user/{userId}/time-entries")
	Call<String> getRunningTasks(@Path("workspaceId") String workspaceId, @Path("userId") String userId, @Query("in-progress") boolean showRunningTask);

	@Headers({"X-Api-Key: X50mikTohhZrUuAK"})
	@GET("workspaces/{workspaceId}/user/{userId}/time-entries")
	Call<String> getTotalWorkDuration(@Path("workspaceId") String workspaceId, @Path("userId") String userId, @Query("start") String dayStartTime);
}
