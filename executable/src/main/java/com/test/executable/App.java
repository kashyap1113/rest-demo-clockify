package com.test.executable;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class App {
	private static Retrofit retrofit;
	
	public static final String BASE_URL = "https://api.clockify.me/api/v1/";
	
	public static void main(String[] args) throws Exception {
		App app = new App();
		app.getTaskInformation();
	}
	
	public App() {
		retrofit = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(ScalarsConverterFactory.create())
				.build();
	}
	
	public void getTaskInformation() throws Exception {
		String apiKey = "X50mikTohhZrUuAK";
		String userId = "5f9bda123c3c303b582063cb";
		String workspaceId = "5f4cf74e5a388d1887a29ab9";
		String userTimeZone = "Asia/Kolkata";
		
		try {
			boolean taskRunning;
			String taskName;
			String taskLink;
			JSONArray tasks;
			
			Services services = retrofit.create(Services.class);
			
			Call<String> result = services.getRunningTasks(workspaceId, userId, true);
			String response = result.execute().body();
			
			tasks = new JSONArray(response);
			if (tasks.length() > 0) {
				taskRunning = true;
				JSONObject runningTask = tasks.getJSONObject(0);
				taskName = runningTask.getString("description");
				String taskId = taskName.substring(taskName.indexOf("[") + 1, taskName.indexOf("]"));
				taskLink = "https://uffiziojira.atlassian.net/browse/" + taskId;
			} else {
				taskRunning = false;
				taskName = "Stopped";
				taskLink = "";
			}
			
			SimpleDateFormat sdfSQL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdfSQL.setTimeZone(TimeZone.getTimeZone(userTimeZone));
			
			String userDayStartTime = sdfSQL.format(System.currentTimeMillis()).split(" ")[0] + "T00:00:00.000Z";
			result = services.getTotalWorkDuration(workspaceId, userId, userDayStartTime);
			response = result.execute().body();
			tasks = new JSONArray(response);
			JSONObject task;
			String taskDuration;
			Duration duration;
			long workDurationMillis = 0;
			
			SimpleDateFormat sdfISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			sdfISO.setTimeZone(TimeZone.getTimeZone("UTC"));
			
			long estimatedWorkHourInMinute = 8 * 60;
			for (int i = 0; i < tasks.length(); i++) {
				task = tasks.getJSONObject(i);
				if (task.getJSONObject("timeInterval").isNull("duration")) {
					workDurationMillis += 
							System.currentTimeMillis() 
							- sdfISO.parse(task.getJSONObject("timeInterval").getString("start")).getTime();
				} else {
					taskDuration = task.getJSONObject("timeInterval").getString("duration");
					duration = Duration.parse(taskDuration);
					workDurationMillis += duration.toMillis();
				}
			}
			int workDurationMinute = (int) (workDurationMillis / (1000 * 60));
			int hour = workDurationMinute / 60;
			int minute = workDurationMinute % 60;
			
			int remainingHour = (int) (Math.abs(estimatedWorkHourInMinute - workDurationMinute) / 60);
			int remainingMinute = (int) (Math.abs(estimatedWorkHourInMinute - workDurationMinute) % 60);
			String remainingDurationSign = estimatedWorkHourInMinute - workDurationMinute > 0 ? "-" : "+";
			
			String workDuration = String.format("%02d:%02d (" + remainingDurationSign + "%02d:%02d)", hour, minute, remainingHour, remainingMinute);
			
			printContent(taskName, taskLink, workDuration, taskRunning);
		} catch (Exception e) {
			printContent("", "", "Error", false);
		}
	}
	
	public void printContent(String taskName, String taskLink, String workDuration, boolean taskRunning) {
		String imagePath;
		String label = workDuration;
		String tooltip = taskName;
		
		if (taskRunning) {
			imagePath = "/home/intel/Pictures/led_blinksgreen_16x16.gif";
		} else {
			imagePath = "/home/intel/Pictures/button_red_16x16.png";
		}
		
		String content;
		content = "<img>" + imagePath + "</img>"
				+ "<txt>" + label + "</txt>"
				+ "<tool>" + tooltip + "</tool>";
		
		if (taskRunning) {
			String command = "chromium-browser " + taskLink;
			content += "<click>" + command + "</click>"
					+ "<txtclick>" + command + "</txtclick>";
		}
		
		System.out.println(content);
	}
}
