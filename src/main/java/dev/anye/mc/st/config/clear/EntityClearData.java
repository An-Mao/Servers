package dev.anye.mc.st.config.clear;

import java.util.HashMap;
import java.util.List;

public class EntityClearData {
    public boolean enable;
    public int autoClearTime;
    public int allEntityLimit;
    public int defaultEntityLimit;
    public boolean stopSpawn;
    public HashMap<String,Integer> entityLimit;
    public boolean clearMob;
    public boolean clearAnimal;
    public boolean clearPet;
    public boolean clearNpc;
    public boolean clearXp;
    public boolean safeEntityItem;
    public int safeDistance;
    public boolean clearName;
    public String LimitClearMsg;
    public HashMap<Integer,String> msg;
    public List<String> blackList;
    public List<String> whiteList;
}
