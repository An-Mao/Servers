package dev.anye.mc.st.config.login_reward;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.json._JsonConfigS;
import dev.anye.core.system._File;
import dev.anye.mc.st.ST;
import dev.anye.mc.st.config.ConfigDir;
import dev.anye.mc.st.helper.ItemHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.*;

public class LoginReward extends _JsonConfigS<LoginRewardData> {
	private static final String FILE = _File.getFilePath(ConfigDir.LOGIN, "LoginReward.json");
	public static final LoginReward I = new LoginReward();
	public static final LoginData LOGIN_DATA = new LoginData(_File.getFilePath(ConfigDir.LOGIN, "LoginData.json"));
	public static final PlayerLoginDataConfig PLAYER_LOGIN_DATA = new PlayerLoginDataConfig(_File.getFilePath(ConfigDir.LOGIN, "PlayerLoginData.json"));
	public static String day = null;

	public LoginReward() {
		super(FILE, LoginRewardData.DEFAULT, new TypeToken<>() {});
	}




	public List<String> getDayRewardList(LoginRewardData loginRewardData) {
		return loginRewardData.dayRewardList();
	}

	public List<ItemStack> getDayReward(String uuid) {
		return read(loginRewardData -> {
			if (loginRewardData.enable()) {
				List<String> list = getDayRewardList(loginRewardData);
				if (list.isEmpty()) return new ArrayList<>();

				List<ItemStack> itemStacks = new ArrayList<>();
				int index = PLAYER_LOGIN_DATA.getLastLoginIndex(uuid) + 1;
				if (index >= list.size()) {
					if (loginRewardData.recurrent()) {
						index = 0;
					} else {
						return itemStacks;
					}
				}
				PLAYER_LOGIN_DATA.setLastLoginIndex(uuid, index);
				itemStacks.add(new ItemStack(ItemHelper.getItem(list.get(index))));
				getAppointedDayRewardList(loginRewardData,itemStacks);
				/*
				if (getAppointedDayRewardList().containsKey(getDayCheck())) {
					itemStacks.add(new ItemStack(getItem(getAppointedDayRewardList().get(getDayCheck()))));
				}*/
				return itemStacks;
			}
			return new ArrayList<>();
		},new ArrayList<>());
	}

	public void getAppointedDayRewardList(LoginRewardData loginRewardData,final List<ItemStack> itemStacks) {
		if (loginRewardData.appointedDayRewardList().containsKey(getDayCheck())) {
			itemStacks.add(new ItemStack(ItemHelper.getItem(loginRewardData.appointedDayRewardList().get(getDayCheck()))));
		}
	}

	public static class LoginData extends _JsonConfigS<Map<String, List<String>>> {
		public LoginData(String file) {
			super(file, new HashMap<>(), new TypeToken<>() {});
		}


		public void clear(){
			update(Map::clear);
		}

		public void addData(String key, String... value) {
			read(stringListMap -> {
				if (!stringListMap.containsKey(key)) {
					stringListMap.put(key,List.of(value));
				} else {
					List<String> list = stringListMap.getOrDefault(key, new ArrayList<>());
					list.addAll(Arrays.asList(value));
					stringListMap.put(key, list);
				}
			});
			saveIfDirtyAsync();
		}
	}

	public static String getDay() {
		return ST.FAST_DATE_TIME.update().toDateString("");
		// DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd");
	}

	public static String getDayCheck() {
		if (day == null) {
			day = getDay();
		} else if (!day.equals(getDay())) {
			day = getDay();
			LoginReward.LOGIN_DATA.clear();
			LoginReward.LOGIN_DATA.save();
		}
		return day;
	}

	public static class PlayerLoginDataConfig extends _JsonConfigS<Map<String, PlayerLoginData>> {
		public PlayerLoginDataConfig(String filePath) {
			super(filePath, new HashMap<>(), new TypeToken<>() {});
		}

		public int getLoginCount(String uuid) {
			checkData(uuid);
			return read(stringPlayerLoginDataMap -> stringPlayerLoginDataMap.get(uuid).loginCount,-1);
		}

		public void setLoginCount(String uuid) {
			checkData(uuid);
			update(stringPlayerLoginDataMap -> stringPlayerLoginDataMap.get(uuid).loginCount++);
			save();
		}

		public int getLastLoginIndex(String uuid) {
			checkData(uuid);
			return read(stringPlayerLoginDataMap -> stringPlayerLoginDataMap.get(uuid).lastLoginIndex,-1);
		}

		public void setLastLoginIndex(String uuid, int index) {
			checkData(uuid);
			update(stringPlayerLoginDataMap -> stringPlayerLoginDataMap.get(uuid).lastLoginIndex = index);
			save();
		}

		public void checkData(String uuid) {
			read(stringPlayerLoginDataMap -> {
				if (!stringPlayerLoginDataMap.containsKey(uuid)) stringPlayerLoginDataMap.put(uuid,new PlayerLoginData());
			});
		}

	}

	public static class PlayerLoginData {
		public int lastLoginIndex;
		public int loginCount;

		public PlayerLoginData() {
			lastLoginIndex = 0;
			loginCount = 0;
		}
	}
}
