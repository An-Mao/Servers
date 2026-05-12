package dev.anye.mc.st.config.clear;

import java.util.HashMap;
import java.util.List;

public class ItemClearData {

    public boolean enable;
    public int autoClearTime;
    public int safeDistance;
    public boolean clearName;
    public boolean trash;
    public boolean clearTrash;
    public HashMap<Integer,String> msg;
    public List<String> blackList;
    public List<String> whiteList;
}
