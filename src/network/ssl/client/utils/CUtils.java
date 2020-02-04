package network.ssl.client.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

import javafx.scene.control.Slider;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class CUtils
{
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		}
		catch(InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	public static Thread createNewThreadAndStart(Runnable runnable) {
		return CUtils.createNewThread(runnable, true);
	}
	
	public static Thread createNewThread(Runnable runnable, boolean start) {
		if(runnable == null)
			return null;
		Thread newThread = new Thread(runnable);
		if(start)
			newThread.start();
		return newThread;
	}
	
	public static String trimFront(String value) {
		if(value == null || value.isEmpty() || !value.startsWith(" "))
			return value;
		else
			return trimFront(value.substring(1));
	}
	
	public static String getFileSizeString(long fileSize)
	{
		int digitCounter = 0;
		long currentDividor = 1L;
		
		for(int i = 1; i < 13; ++i)
		{
			if((fileSize / (currentDividor*1024L)) <= 0)
			{
				if(CUtils.isIntBetween(digitCounter, 0, 2))
					return (int)(Math.pow(10d, digitCounter) * ((double)fileSize / (double)(currentDividor))) + " Bytes";
				else if(CUtils.isIntBetween(digitCounter, 3, 5))
					return (int)(Math.pow(10d, digitCounter-3) * ((double)fileSize / (double)(currentDividor))) + " kB";
				else if(CUtils.isIntBetween(digitCounter, 6, 8))
					return (int)(Math.pow(10d, digitCounter-6) * ((double)fileSize / (double)(currentDividor))) + " MB";
				else if(CUtils.isIntBetween(digitCounter, 9, 11))
					return (int)(Math.pow(10d, digitCounter-9) * ((double)fileSize / (double)(currentDividor))) + " GB";
				else if(CUtils.isIntBetween(digitCounter, 12, 14))
					return (int)(Math.pow(10d, digitCounter-12) * ((double)fileSize / (double)(currentDividor))) + " TB";
			}
			else
			{
				currentDividor *= 1024L;
				digitCounter = (""+currentDividor).length() - 1;
			}
		}
		
		return "";
	}
	
	public static String getFileName(String path, boolean withTypeEnding)
	{
		String[] allParts = path.split("[\\\\|/]");
		if(allParts != null && allParts.length > 0)
			return withTypeEnding ? allParts[allParts.length-1] : allParts[allParts.length-1].substring(0, allParts[allParts.length-1].lastIndexOf("."));
		else
			return null;
	}
	
	public static byte[] convert(Byte[] array)
	{
		byte[] result = new byte[array.length];
		int index = 0;
		
		for(Byte b : array)
			result[index++] = b;
		
		return result;
	}
	
	public static Byte[] convert(byte[] array)
	{
		Byte[] result = new Byte[array.length];
		int index = 0;
		
		for(byte b : array)
			result[index++] = b;
		
		return result;
	}
	
	public static int[] convert(Integer[] array)
	{
		int[] result = new int[array.length];
		int index = 0;
		
		for(Integer b : array)
			result[index++] = b;
		
		return result;
	}
	
	public static Integer[] convert(int[] array)
	{
		Integer[] result = new Integer[array.length];
		int index = 0;
		
		for(int b : array)
			result[index++] = b;
		
		return result;
	}
	
	public static byte[] trimNulls(byte[] arrayToTrim)
	{
		int trimmedIndex = arrayToTrim.length-1;

		while(trimmedIndex > 0 && arrayToTrim[trimmedIndex] == 0)
			--trimmedIndex;

		byte[] resultArray = new byte[trimmedIndex+1];
		System.arraycopy(arrayToTrim, 0, resultArray, 0, trimmedIndex+1);

		return resultArray;
	}

	public static boolean isIntBetween(int value, int min, int max)
	{
		return value >= min && value <= max;
	}

	public static boolean checkStringLength(String s, int minLength, int maxLength)
	{
		return s.length() >= minLength && s.length() <= maxLength;
	}

	public static boolean anyEmptyString(String ... strings)
	{
		for(String s : strings)
			if(s == null || s.isEmpty())
				return true;
		return false;
	}

	//
	public static byte[] serialize(Object obj)
	{
		try
		{
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
			return bo.toByteArray();
		}
		catch(IOException io)
		{
			io.printStackTrace();
			return null;
		}
	}

	public static Object deserialize(byte[] objBytes)
	{
		try
		{
			ByteArrayInputStream bi = new ByteArrayInputStream(objBytes);
			ObjectInputStream oi = new ObjectInputStream(bi);
			return oi.readObject();
		}
		catch(IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	//
	public static String durationToMMSS(Duration duration)
	{
		int minutes = (int) duration.toSeconds() / 60;
		int seconds = (int) duration.toSeconds() % 60;
		return (minutes < 10 ? "0"+minutes : ""+minutes) + ":" + (seconds < 10 ? "0"+seconds : ""+seconds);
	}

	//
	public static String durationToHHMMSS(Duration duration)
	{
		int hours = (int) duration.toMinutes() / 60;
		int minutes = (int) (duration.toSeconds() / 60) - (hours * 60);
		int seconds = (int) duration.toSeconds() % 60;
		return (hours < 10 ? "0"+hours : ""+hours) + ":" + (minutes < 10 ? "0"+minutes : ""+minutes) + ":" + (seconds < 10 ? "0"+seconds : ""+seconds);
	}

	//
	public static Duration sliderValueToDuration(Slider slider, Duration totalDuration)
	{
		if(slider != null)
		{
			return totalDuration.multiply((slider.getValue() / slider.getMax()));
		}
		else
			return Duration.ZERO;
	}

	//
	public static String getCurrentTimeHM()
	{
		Calendar calendar = Calendar.getInstance();

		String timeHours = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		timeHours = timeHours.length() < 2 ? ("0"+timeHours) : timeHours;

		String timeMinutes = Integer.toString(calendar.get(Calendar.MINUTE));
		timeMinutes = timeMinutes.length() < 2 ? ("0"+timeMinutes) : timeMinutes;

		return timeHours + ":" + timeMinutes;
	}

	//
	public static String getCurrentTimeHMS()
	{
		Calendar calendar = Calendar.getInstance();

		String timeHours = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		timeHours = timeHours.length() < 2 ? ("0"+timeHours) : timeHours;

		String timeMinutes = Integer.toString(calendar.get(Calendar.MINUTE));
		timeMinutes = timeMinutes.length() < 2 ? ("0"+timeMinutes) : timeMinutes;

		String timeSeconds = Integer.toString(calendar.get(Calendar.SECOND));
		timeSeconds = timeSeconds.length() < 2 ? ("0"+timeSeconds) : timeSeconds;

		return timeHours + ":" + timeMinutes + ":" + timeSeconds;
	}

	//
	public static boolean addFolderToPath(String path, String folderName)
	{
		File newFolder = new File(path + "/" + folderName);
		if(!newFolder.exists())
		{
			newFolder.mkdir();
			return true;
		}
		else
		{
			return false;
		}
	}

	//
	public static boolean addFileToPath(String path, String fileName)
	{
		File newFile = new File(path + "/" + fileName);
		if(!newFile.exists())
		{
			try
			{
				newFile.createNewFile();
				return true;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	//
	public static Font CFont(double fontSize)
	{
		return CUtils.CFont(fontSize, null, FontPosture.REGULAR);
	}

	//
	public static Font CFont(double fontSize, FontWeight fontWeight)
	{
		return CUtils.CFont(fontSize, fontWeight, FontPosture.REGULAR);
	}

	//
	private static Font CFont(double fontSize, FontWeight fontWeight, FontPosture fontPosture)//TODO italic
	{
		return Font.font("Segoe UI", fontWeight, fontPosture, fontSize);
//		String fontWeightName = "Lt";
//		if(fontWeight != null)
//		{
//			switch(fontWeight)
//			{
//			case LIGHT:
//				fontWeightName = "";
//				break;
//			case NORMAL:
//				fontWeightName = "Lt";
//				break;
//			case SEMI_BOLD:
//				fontWeightName = "Med";
//				break;
//			case BOLD:
//				fontWeightName = "Bd";
//				break;
//			case EXTRA_BOLD:
//				fontWeightName = "Hv";
//				break;
//			default:
//				System.err.println("FontWeight " + fontWeight.name() + " is not supported.");
//				break;
//			}
//		}
//		return Font.loadFont(CUtils.class.getResourceAsStream("/fonts/HelveticaNeue" + fontWeightName + ".ttf"), fontSize);
	}
}
