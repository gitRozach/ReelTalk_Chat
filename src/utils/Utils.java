package utils;

import java.util.Calendar;

import javafx.util.Duration;

public class Utils {
	public static String getFileSizeAsString(long fileSize) {
		int digitCounter = 0;
		long currentDividor = 1L;
		
		for(int i = 1; i < 13; ++i) {
			if((fileSize / (currentDividor*1024L)) <= 0) {
				if(Utils.isIntBetween(digitCounter, 0, 2))
					return (int)(Math.pow(10d, digitCounter) * ((double)fileSize / (double)(currentDividor))) + " Bytes";
				else if(Utils.isIntBetween(digitCounter, 3, 5))
					return (int)(Math.pow(10d, digitCounter-3) * ((double)fileSize / (double)(currentDividor))) + " kB";
				else if(Utils.isIntBetween(digitCounter, 6, 8))
					return (int)(Math.pow(10d, digitCounter-6) * ((double)fileSize / (double)(currentDividor))) + " MB";
				else if(Utils.isIntBetween(digitCounter, 9, 11))
					return (int)(Math.pow(10d, digitCounter-9) * ((double)fileSize / (double)(currentDividor))) + " GB";
				else if(Utils.isIntBetween(digitCounter, 12, 14))
					return (int)(Math.pow(10d, digitCounter-12) * ((double)fileSize / (double)(currentDividor))) + " TB";
			}
			else {
				currentDividor *= 1024L;
				digitCounter = (""+currentDividor).length() - 1;
			}
		}
		return "";
	}
	
	public static String getFileName(String path) {
		return getFileName(path, true);
	}
	
	public static String getFileName(String path, boolean withTypeEnding) {
		if(path == null)
			return null;
		String[] allParts = path.split("[\\\\|/]");
		if(allParts != null && allParts.length > 0)
			return withTypeEnding ? allParts[allParts.length-1] : allParts[allParts.length-1].substring(0, allParts[allParts.length-1].lastIndexOf("."));
		else
			return path;
	}
	
	public static boolean isIntBetween(int value, int min, int max)	{
		return value >= min && value <= max;
	}

	public static String durationToMMSS(Duration duration) {
		int minutes = (int) duration.toSeconds() / 60;
		int seconds = (int) duration.toSeconds() % 60;
		return (minutes < 10 ? "0"+minutes : ""+minutes) + ":" + (seconds < 10 ? "0"+seconds : ""+seconds);
	}

	public static String durationToHHMMSS(Duration duration) {
		int hours = (int) duration.toMinutes() / 60;
		int minutes = (int) (duration.toSeconds() / 60) - (hours * 60);
		int seconds = (int) duration.toSeconds() % 60;
		return (hours < 10 ? "0"+hours : ""+hours) + ":" + (minutes < 10 ? "0"+minutes : ""+minutes) + ":" + (seconds < 10 ? "0"+seconds : ""+seconds);
	}

	public static String durationToHHMM(Duration duration) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis((long)duration.toMillis());

		String timeHours = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		timeHours = timeHours.length() < 2 ? ("0"+timeHours) : timeHours;

		String timeMinutes = Integer.toString(calendar.get(Calendar.MINUTE));
		timeMinutes = timeMinutes.length() < 2 ? ("0"+timeMinutes) : timeMinutes;

		return timeHours + ":" + timeMinutes;
	}

	public static String getCurrentTimeHMS() {
		Calendar calendar = Calendar.getInstance();

		String timeHours = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		timeHours = timeHours.length() < 2 ? ("0"+timeHours) : timeHours;

		String timeMinutes = Integer.toString(calendar.get(Calendar.MINUTE));
		timeMinutes = timeMinutes.length() < 2 ? ("0"+timeMinutes) : timeMinutes;

		String timeSeconds = Integer.toString(calendar.get(Calendar.SECOND));
		timeSeconds = timeSeconds.length() < 2 ? ("0"+timeSeconds) : timeSeconds;

		return timeHours + ":" + timeMinutes + ":" + timeSeconds;
	}
}
