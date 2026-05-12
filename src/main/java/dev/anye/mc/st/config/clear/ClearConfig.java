package dev.anye.mc.st.config.clear;

import com.google.gson.reflect.TypeToken;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import dev.anye.core.json._JsonConfig;
import dev.anye.mc.st.ST;
import dev.anye.mc.st.helper.EntityHelper;

public class ClearConfig {
    public static final EntityClear ENTITY_CLEAR = new EntityClear();
    public static final ItemClear ITEM_CLEAR = new ItemClear();

    public static class EntityClear extends _JsonConfig<EntityClearData>{
        private static final String file = ST.ConfigDir_Clear + "entity.json";
        public EntityClear() {
            super(file, """
                    {
                        "enable": true,
                        "clearMob": true,
                        "clearAnimal": true,
                        "clearPet": false,
                        "clearNpc": false,
                        "clearXp": false,
                        "safeEntityItem":true,
                        "allEntityLimit": 350,
                        "defaultEntityLimit": 50,
                        "stopSpawn":true,
                        "entityLimit": {
                            "minecraft:bee": 50
                            },
                        "autoClearTime": 300,
                        "safeDistance": 70,
                        "clearName":false,
                        "LimitClearMsg": "clear.entity.limit",
                        "msg": {
                            "0": "clear.entity.done",
                            "1": "clear.normal.1",
                            "2": "clear.normal.2",
                            "3": "clear.normal.3",
                            "4": "clear.normal.4",
                            "5": "clear.normal.5",
                            "6": "clear.normal.6",
                            "7": "clear.normal.7",
                            "8": "clear.normal.8",
                            "9": "clear.normal.9",
                            "10": "clear.normal.10",
                            "30": "clear.normal.30",
                            "60": "clear.normal.60"
                            },
                        "blackList": [
                            "minecraft:slime"
                            ],
                        "whiteList": [
                            "minecraft:chicken"
                            ]
                    }
                    """, new TypeToken<>(){});
        }
        public String getMsg(int time){return getDatas().msg.getOrDefault(time,"");}

        public int getEntityLimit(Entity entity){
            return getDatas().entityLimit.getOrDefault(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString(), getDatas().defaultEntityLimit);
        }
        public boolean isInWhiteList(Entity entity){
            return getDatas().whiteList.contains(EntityHelper.getEntityRegID(entity));
        }
        public boolean isInBlackList(Entity entity){
            return getDatas().blackList.contains(EntityHelper.getEntityRegID(entity));
        }

    }
    public static class ItemClear extends _JsonConfig<ItemClearData>{
        private static final String file = ST.ConfigDir_Clear + "item.json";
        public ItemClear() {
            super(file, """
                    {
                        "enable": true,
                        "autoClearTime": 300,
                        "safeDistance": 90,
                        "clearName":false,
                        "trash":true,
                        "clearTrash":false,
                        "msg": {
                            "0": "clear.item.done",
                            "1": "clear.normal.1",
                            "2": "clear.normal.2",
                            "3": "clear.normal.3",
                            "4": "clear.normal.4",
                            "5": "clear.normal.5",
                            "6": "clear.normal.6",
                            "7": "clear.normal.7",
                            "8": "clear.normal.8",
                            "9": "clear.normal.9",
                            "10": "clear.normal.10",
                            "30": "clear.normal.30",
                            "60": "clear.normal.60"
                            },
                        "blackList": [],
                        "whiteList": []
                    }""",
                    new TypeToken<>(){});
        }
    }
}
