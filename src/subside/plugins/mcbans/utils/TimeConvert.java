package subside.plugins.mcbans.utils;

import subside.plugins.mcbans.exceptions.InvalidTimeException;

public class TimeConvert {

	public static int convert(String time) throws InvalidTimeException {
		try {
			int amount = 0;
			String amm = "";
			int amm2 = 0;
			for (String c : time.split("")) {
				if ("0123456789".contains(c)) {
					amm = amm + c;
				} else {
					amm2 = Integer.parseInt(amm);
					switch (c) {
					case "w":
						amm2 *= 7;
					case "d":
						amm2 *= 24;
					case "h":
						amm2 *= 60;
					case "m":
						amm2 *= 60;
					case "s":
						break;
					}
					amount += amm2;
					amm = "";
					amm2 = 0;
				}
	
			}
			if(amount == 0){
				throw new InvalidTimeException();
			}
			return amount;
		} catch(NumberFormatException e){
			throw new InvalidTimeException();
		}
	}
	
	public static String timeToString(int time){
		int week = 0;
		int day = 0;
		int hour = 0;
		int minute = 0;
		int second = 0;

		week = time / (60*60*24*7);
		time -= week * (60*60*24*7);
		
		day = time / (60*60*24);
		time -= day * (60*60*24);
		
		hour = time / (60*60);
		time -= hour * (60*60);
		
		minute = time / (60);
		time -= minute * (60);
		
		second = time;
		
		String build = "";
		if(week > 0) build += week+"w";
		if(day > 0) build += day+"d";
		if(hour > 0) build += hour+"h";
		if(minute > 0) build += minute+"m";
		if(second > 0) build += second+"s";
		
		return build;
	}
	
	public static String TextTimeToString(long time){
		if(time < 0){
			return null;
		}
		long week = 0;
		long day = 0;
		long hour = 0;
		long minute = 0;
		long second = 0;

		week = time / (60*60*24*7);
		time -= week * (60*60*24*7);
		
		day = time / (60*60*24);
		time -= day * (60*60*24);
		
		hour = time / (60*60);
		time -= hour * (60*60);
		
		minute = time / (60);
		time -= minute * (60);
		
		second = time;
		
		String build = "";
		if(week > 0) build += week+" week"+(week==1?" ":"s ");
		if(day > 0) build += day+" day"+(day==1?" ":"s ");
		if(hour > 0) build += hour+" hour"+(hour==1?" ":"s ");
		if(minute > 0) build += minute+" minute"+(minute==1?" ":"s ");
		if(second > 0) build += second+" second"+(second==1?" ":"s ");
		
		return build.trim();
	}
	
	public static String timeTillExpiringMills(int time){
		return TextTimeToString(time - Utils.getTimestamp());
	}
}