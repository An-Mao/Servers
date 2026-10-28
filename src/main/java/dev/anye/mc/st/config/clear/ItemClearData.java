package dev.anye.mc.st.config.clear;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ItemClearData (
	boolean enable,
	int autoClearTime,
	int safeDistance,
	boolean clearName,
	boolean trash,
	boolean clearTrash,
	Map<Integer, String> msg,
	List<String> blackList,
	List<String> whiteList) {
	public static final ItemClearData DEFAULT = new ItemClearData(
			true,
			300,
			90,
			false,
			true,
			false,
			MsgDefault(),
			List.of(),
			List.of()
	);
	private static Map<Integer, String> MsgDefault() {
		Map<Integer, String> map = new HashMap<>();
		map.put(0, "clear.entity.done");
		map.put(1, "clear.normal.1");
		map.put(2, "clear.normal.2");
		map.put(3, "clear.normal.3");
		map.put(4, "clear.normal.4");
		map.put(5, "clear.normal.5");
		map.put(6, "clear.normal.6");
		map.put(7, "clear.normal.7");
		map.put(8, "clear.normal.8");
		map.put(9, "clear.normal.9");
		map.put(10, "clear.normal.10");
		map.put(30, "clear.normal.30");
		map.put(60, "clear.normal.60");
		return map;
	}
}
