package dev.anye.mc.st.config.login$reward;

import com.google.gson.reflect.TypeToken;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import dev.anye.core.json._JsonConfig;
import dev.anye.mc.st.ST;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginReward extends _JsonConfig<LoginRewardData> {
    private static final String file = ST.ConfigDir_Login + "LoginReward.json";
    public static final LoginReward I = new LoginReward();
    public static final LoginData loginData = new LoginData(ST.ConfigDir_Login + "LoginData.json");
    public static final PlayerLoginDataConfig playerLoginData = new PlayerLoginDataConfig(ST.ConfigDir_Login + "PlayerLoginData.json");
    public static String day = null;


    public LoginReward() {
        super(file, """
                {
                    "enable": true,
                    "recurrent":true,
                    "dayRewardList": [
                        "minecraft:gold_ingot"
                    ],
                    "appointedDayRewardList": {
                        "20241107": "minecraft:diamond"
                    }
                }
                """, new TypeToken<>() {});

    }
    public List<String> getDayRewardList() {
        if (getDatas().dayRewardList == null) getDatas().dayRewardList = new ArrayList<>();
        return getDatas().dayRewardList;
    }
    public List<ItemStack> getDayReward(String uuid) {
        List<String> list = getDayRewardList();
        List<ItemStack> itemStacks = new ArrayList<>();
        if (list.isEmpty()) return itemStacks;
        int Index = playerLoginData.getLastLoginIndex(uuid) + 1;

        if (Index >= list.size()) {
            if (getDatas().recurrent) {
                Index = 0;
            }else {
                return itemStacks;
            }
        }
        playerLoginData.setLastLoginIndex(uuid, Index);
        itemStacks.add(new ItemStack(getItem(list.get(Index))));
        if (getAppointedDayRewardList().containsKey(getDayCheck())){
            itemStacks.add(new ItemStack(getItem(getAppointedDayRewardList().get(getDayCheck()))));
        }
        return itemStacks;
    }
    public HashMap<String, String> getAppointedDayRewardList() {
        if (getDatas().appointedDayRewardList == null) getDatas().appointedDayRewardList = new HashMap<>();
        return getDatas().appointedDayRewardList;
    }

    public static Item getItem(String name) {
        return BuiltInRegistries.ITEM.get(Identifier.tryParse(name)).get().value();
    }
    public static class LoginData extends _JsonConfig<HashMap<String, List<String>>> {
        public LoginData(String file) {
            super(file, "", new TypeToken<>() {});
        }

        public HashMap<String, List<String>> getDatas() {
            if (datas == null) datas = new HashMap<>();
            return datas;
        }
        public void addData(String key, String value) {
            if (!getDatas().containsKey(key)) getDatas().put(key, List.of(value));
            else {
                List<String> list = getDatas().getOrDefault(key, new ArrayList<>());
                list.add(value);
                getDatas().put(key, list);
            }
            save();
        }
    }
    public static String getDay(){

        return DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd");
    }
    public static String getDayCheck(){
        if (day == null) {
            day = getDay();
        }else if (!day.equals(getDay())) {
            day = getDay();
            LoginReward.loginData.getDatas().clear();
            LoginReward.loginData.save();
        }
        return day;
    }

    public static class PlayerLoginDataConfig extends _JsonConfig<HashMap<String, PlayerLoginData>> {
        public PlayerLoginDataConfig(String filePath) {
            super(filePath, """
                    """, new TypeToken<>() {});
        }

        public HashMap<String, PlayerLoginData> getDatas() {
            if (datas == null) datas = new HashMap<>();
            return datas;
        }
        public int getLoginCount(String uuid) {
            checkData(uuid);
            return getDatas().get(uuid).LoginCount;
        }
        public void setLoginCount(String uuid) {
            checkData(uuid);
            getDatas().get(uuid).LoginCount ++;
            save();
        }
        public int getLastLoginIndex(String uuid) {
            checkData(uuid);
            return getDatas().get(uuid).lastLoginIndex;
        }
        public void setLastLoginIndex(String uuid, int index) {
            checkData(uuid);
            getDatas().get(uuid).lastLoginIndex = index;
            save();
        }

        public void checkData(String uuid){
            if (!getDatas().containsKey(uuid)) getDatas().put(uuid, new PlayerLoginData());
        }

    }
    public static class PlayerLoginData{
        public int lastLoginIndex;
        public int LoginCount;
        public PlayerLoginData() {
            lastLoginIndex = 0;
            LoginCount = 0;
        }
    }
}
