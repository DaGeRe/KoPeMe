package de.kopeme.datacollection;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;

/**
 * Diese Klasse speichert Daten über die Arbeitsspeicherbenutzung. Allerdings wird hier nicht der durchschnittliche Arbeitsspeicherverbrauch
 * oder ein Verlauf gespeichert, sondern nur der Arbeitsspeicherverbrauch, der während des Methodenaufrufs hinzugekommen ist. Ein Messen
 * des gesamten Verlaufs würde keine vergleichbaren Zahlen erzeugen, sondern Verläufe, die dann schwerer vergleichbar wären. Weiterhin ist
 * das Vergleichen des Arbeitsspeicherverbrauches, wenn zwischendurch der GarbageCollector aufgerufen wurde, wenig sinnvoll, da der eigentliche
 * Arbeitsspeicherverbrauch einer Methode nicht mehr ermittelt werden kann. 
 * @author dagere
 *
 */
public class RAMUsageCollector extends DataCollector {
	MemoryMXBean mxb;
	long usedStart, value;
	
	@Override
	public void startCollection() {
		System.gc();
//		mxb = ManagementFactory.getMemoryMXBean();
//		long used = mxb.getHeapMemoryUsage().getUsed();
//		long used2 = mxb.getNonHeapMemoryUsage().getUsed();
		
		usedStart = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

	}

	@Override
	public void stopCollection() {
//		long used = mxb.getHeapMemoryUsage().getUsed();
//		long used2 = mxb.getNonHeapMemoryUsage().getUsed();
		
		long nowVal = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		
		value = nowVal - usedStart ;
	}

	@Override
	public long getValue() {
		return value;
	}

}
