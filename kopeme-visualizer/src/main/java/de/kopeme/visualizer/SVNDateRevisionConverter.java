package de.kopeme.visualizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.kopeme.visualizer.DateConverter;



public class SVNDateRevisionConverter implements DateConverter{

	public StringDatePair getDisplayStringOfDate(Date d) {
		Date d2 = new Date(d.getTime() + 60*1000);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String command = "svn log -r {'"+ df.format(d) + "'}:{'"+df.format(d2)+"'}";
		System.out.println("Command: " + command);
		
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader is = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line = "";
			String output = "";
			while ( (line = is.readLine()) != null)
			{
				System.out.println("Line: " + line);
				output += line;
			}
			
			is = new BufferedReader(new InputStreamReader(p.getInputStream()));
			line = "";
			while ( (line = is.readLine()) != null)
			{
				System.out.println("LineInput: " + line);
				output += line;
			}
			
			p.waitFor();
//			System.out.println("Output: " + output);
			String [] parts = output.split("|");
			System.out.println("Ouput: " + output);
			System.out.println("Output 1: " + parts[0]);
			
			String numberString = parts[0].substring(1);
			return new StringDatePair(numberString, d);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String args[])
	{
		System.out.println("Date: " + args[0]);
		Date d;
		try {
			d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(args[0]);
			SVNDateRevisionConverter converter = new SVNDateRevisionConverter();
			converter.getDisplayStringOfDate(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
