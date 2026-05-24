package dev.anye.mc.st.config.player$group;

import com.google.gson.reflect.TypeToken;
import dev.anye.core.json._JsonConfig;
import dev.anye.core.system._File;
import dev.anye.mc.st.ST;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerGroupConfig {
    public static final HashMap<String,Group> GROUPS = getGroups();

    public static HashMap<String,Group> getGroups() {
        HashMap<String,Group> abc = new HashMap<>();
        List<Path> jsonFiles = _File.getFiles(ST.ConfigDir_PlayerGroup,".json");
        for (Path path : jsonFiles) {
            String fileName = path.getFileName().toString();
            abc.put(fileName,new Group(path.toString()));
        }
        return abc;
    }

    public static class Group extends _JsonConfig<List<String>> {
        public Group(String filePath) {
            super(filePath, "",new TypeToken<>(){});
        }

        @Override
        public List<String> getDatas() {
            if (datas == null) datas = new ArrayList<>();
            return datas;
        }

        @Override
        public String toString() {
            return datas.toString();
        }
    }
}
