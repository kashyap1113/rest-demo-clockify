package com.test.executable;

public class App {
	public static void main(String[] args) throws Exception {
		String imagePath;
		String label = "00:00 (-08:00)";
		String tooltip = "TRAK-4014 #WEB Trip wise fuel report";
		String taskLink = "https://www.google.com/";
		boolean taskRunning = false;
		
		if (taskRunning) {
			if ((System.currentTimeMillis() / 1000) % 2 == 0)
				imagePath = "/home/intel/Pictures/button_green_16x16.png";
			else
				imagePath = "/home/intel/Pictures/button_white_16x16.png";
		} else {
			imagePath = "/home/intel/Pictures/button_red_16x16.png";
		}
		
		if (taskRunning) {
			tooltip = "TRAK-4014 #WEB Trip wise fuel report";
		} else {
			tooltip = "Stopped";
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
