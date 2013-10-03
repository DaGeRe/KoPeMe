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
		mxb = ManagementFactory.getMemoryMXBean();
		long init = mxb.getHeapMemoryUsage().getInit();
		long max = mxb.getHeapMemoryUsage().getMax();
		long used = mxb.getHeapMemoryUsage().getUsed();
//		System.out.println("Init: " + init + " Max: " + max + " Used: " + used);
		
		long init2 = mxb.getNonHeapMemoryUsage().getInit();
		long max2 = mxb.getNonHeapMemoryUsage().getMax();
		long used2 = mxb.getNonHeapMemoryUsage().getUsed();
//		System.out.println(" Init: " + init2 + " Max: " + max2 + " Used: " + used2);
		
		usedStart = used + used2;

	}

	@Override
	public void stopCollection() {
		long init = mxb.getHeapMemoryUsage().getInit();
		long max = mxb.getHeapMemoryUsage().getMax();
		long used = mxb.getHeapMemoryUsage().getUsed();
//		System.out.println("Init: " + init + " Max: " + max + " Used: " + used);
		
		long init2 = mxb.getNonHeapMemoryUsage().getInit();
		long max2 = mxb.getNonHeapMemoryUsage().getMax();
		long used2 = mxb.getNonHeapMemoryUsage().getUsed();
//		System.out.println(" Init: " + init2 + " Max: " + max2 + " Used: " + used2);
		
		long nowVal = used + used2;
		
		value = nowVal - usedStart ;
//		System.out.println("RAM: " + value);
	}

	@Override
	public long getValue() {
		// TODO Automatisch generierter Methodenstub
		return value;
	}

}
