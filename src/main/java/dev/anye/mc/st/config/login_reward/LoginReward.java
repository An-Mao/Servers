package dev.anye.mc.st.config.login_reward;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.system._File;
import dev.anye.mc.st.config.ConfigDir;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.*;

public class LoginReward extends _JsonConfig<LoginRewardData> {
	private static final String FILE = _File.getFilePath(ConfigDir.LOGIN, "LoginReward.json");
	public static final LoginReward I = new LoginReward();
	public static final LoginData LOGIN_DATA = new LoginData(_File.getFilePath(ConfigDir.LOGIN, "LoginData.json"));
	public static final PlayerLoginDataConfig PLAYER_LOGIN_DATA = new PlayerLoginDataConfig(_File.getFilePath(ConfigDir.LOGIN, "PlayerLoginData.json"));
	public static String day = null;

	public LoginReward() {
		super(FILE, LoginRewardData.DEFAULT, new TypeToken<>() {});
	}




	public List<String> getDayRewardList() {
		if (this.data.isPresent()){
			return this.data.get().dayRewardList();
		}
		return new ArrayList<>();
	}

	public List<ItemStack> getDayReward(String uuid) {
		if (this.data.isPresent()) {
			List<String> list = getDayRewardList();


			List<ItemStack> itemStacks = new ArrayList<>();
			if (list.isEmpty()) return itemStacks;
			int index = PLAYER_LOGIN_DATA.getLastLoginIndex(uuid) + 1;

			if (index >= list.size()) {
				if (this.data.get().recurrent()) {
					index = 0;
				} else {
					return itemStacks;
				}
			}
			PLAYER_LOGIN_DATA.setLastLoginIndex(uuid, index);
			itemStacks.add(new ItemStack(getItem(list.get(index))));
			if (getAppointedDayRewardList().containsKey(getDayCheck())) {
				itemStacks.add(new ItemStack(getItem(getAppointedDayRewardList().get(getDayCheck()))));
			}
			return itemStacks;
		}
		return new ArrayList<>();
	}

	public Map<String, String> getAppointedDayRewardList() {
		if (data.isPresent()){
			return data.get().appointedDayRewardList();
		}
		return new HashMap<>();
	}

	public static Item getItem(String name) {
		return BuiltInRegistries.ITEM.get(Identifier.tryParse(name)).get().value();
	}

	public static class LoginData extends _JsonConfig<Map<String, List<String>>> {
		public LoginData(String file) {
			super(file, new HashMap<>(), new TypeToken<>() {});
		}


		public void clear(){
			ifPresent(Map::clear);
		}

		public void addData(String key, String... value) {
			ifPresent(stringListMap -> {
				if (!stringListMap.containsKey(key)) {
					stringListMap.put(key,List.of(value));
				} else {
					List<String> list = stringListMap.getOrDefault(key, new ArrayList<>());
					list.addAll(Arrays.asList(value));
					stringListMap.put(key, list);
				}
				save();
			});
		}
	}

	public static String getDay() {

		return DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd");
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

	public static class PlayerLoginDataConfig extends _JsonConfig<Map<String, PlayerLoginData>> {
		public PlayerLoginDataConfig(String filePath) {
			super(filePath, new HashMap<>(), new TypeToken<>() {});
		}

		public int getLoginCount(String uuid) {
			checkData(uuid);
			return data.map(stringPlayerLoginDataMap -> stringPlayerLoginDataMap.get(uuid).loginCount).orElse(-1);
		}

		public void setLoginCount(String uuid) {
			checkData(uuid);
			data.ifPresent(stringPlayerLoginDataMap -> stringPlayerLoginDataMap.get(uuid).loginCount++);
			save();
		}

		public int getLastLoginIndex(String uuid) {
			checkData(uuid);
			return data.map(stringPlayerLoginDataMap -> stringPlayerLoginDataMap.get(uuid).lastLoginIndex).orElse(-1);
		}

		public void setLastLoginIndex(String uuid, int index) {
			checkData(uuid);
			data.ifPresent(stringPlayerLoginDataMap -> stringPlayerLoginDataMap.get(uuid).lastLoginIndex = index);
			save();
		}

		public void checkData(String uuid) {
			ifPresent(stringPlayerLoginDataMap -> {
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
