package dev.anye.mc.st.config.login_reward;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record LoginRewardData(
		boolean enable,
		List<String> dayRewardList,
		Map<String, String> appointedDayRewardList,
		boolean recurrent) {
	//public List<String> cumulativeRewardList;

	public static final LoginRewardData DEFAULT = new LoginRewardData(
			true,
			List.of("minecraft:gold_ingot"),
			AppointedDayRewardListDefault(),
			true);
	public static Map<String, String> AppointedDayRewardListDefault (){
		Map<String, String> map = new HashMap<>();
		map.put("20241107","minecraft:diamond");
		return map;
	}
}
