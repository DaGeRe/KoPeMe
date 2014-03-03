package de.kopeme.datastorage;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DataLoader {
	public Map<String, Map<Date, Long>> getData();
}
