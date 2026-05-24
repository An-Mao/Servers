package dev.anye.mc.st.js;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public abstract class  _JavaScript<T extends _JavaScript<T>> implements _IJS {
    private final boolean cache;
    private final HashMap<String, Reader> fileTemp = new HashMap<>();
    public _JavaScript(boolean cache){
        this.cache = cache;
    }
    public void clearFileTemp(){
        fileTemp.clear();
    }

    public abstract T addParameter(String name , Object value);
    public abstract T setParameter(Map<String,Object> map);



    @Override
    public Object runFile(String file) {
        if (cache){
            if (!fileTemp.containsKey(file)) {
                try {
                    fileTemp.put(file,new FileReader(file));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            return this.runFile(fileTemp.get(file));
        }else {
            try {
                return this.runFile(new FileReader(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
