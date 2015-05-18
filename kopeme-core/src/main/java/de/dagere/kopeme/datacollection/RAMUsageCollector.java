package de.dagere.kopeme.datacollection;

/**
 * Diese Klasse speichert Daten über die Arbeitsspeicherbenutzung. Allerdings wird hier nicht der durchschnittliche Arbeitsspeicherverbrauch oder ein Verlauf
 * gespeichert, sondern nur der Arbeitsspeicherverbrauch, der während des Methodenaufrufs hinzugekommen ist. Ein Messen des gesamten Verlaufs würde keine
 * vergleichbaren Zahlen erzeugen, sondern Verläufe, die dann schwerer vergleichbar wären. Weiterhin ist das Vergleichen des Arbeitsspeicherverbrauches, wenn
 * zwischendurch der GarbageCollector aufgerufen wurde, wenig sinnvoll, da der eigentliche Arbeitsspeicherverbrauch einer Methode nicht mehr ermittelt werden
 * kann.
 * 
 * @author dagere
 *
 */
public final class RAMUsageCollector extends DataCollector {

	private long usedStart, value;

	@Override
	public int getPriority() {
		return MIDDLE_COLLECTOR_PRIORITY; // Middle-High Priority, as the RAMUsageCollector should not
		// measure the things other DataCollectors create
	}

	@Override
	public void startCollection() {
		System.gc();
		usedStart = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

	}

	@Override
	public void stopCollection() {
		long nowVal = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		value = nowVal - usedStart;
	}

	@Override
	public long getValue() {
		return value;
	}
}
