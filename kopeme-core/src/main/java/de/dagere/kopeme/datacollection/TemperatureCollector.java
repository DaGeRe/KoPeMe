package de.dagere.kopeme.datacollection;

/**
 * DataCollector for collecting Temperature-Information
 * 
 * @author reichelt
 *
 */
public class TemperatureCollector {
	public static int getTemperature() {

		// TODO: Implement getTemperature

		// try {
		// Process p = Runtime.getRuntime().exec("acpi -t");
		// try {
		// p.waitFor();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// BufferedReader br = new BufferedReader(new
		// InputStreamReader(p.getInputStream()));
		// String line;
		// while ((line = br.readLine()) != null){
		// String s = line.substring(15, 19);
		// System.out.println("Line: " + s);
		// }
		//
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		return 0;
	}

	public static void main(String args[]) {
		getTemperature();
	}
}
