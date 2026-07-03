package dev.anye.mc.st.js;

import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import java.io.Reader;
import java.util.Map;

public class _NashornJS extends _JavaScript<_NashornJS> {
    private final ScriptEngineFactory sef;
    private final ScriptEngine engine;
    public _NashornJS(boolean cache){
        super(cache);
        sef = new NashornScriptEngineFactory();
        engine = sef.getScriptEngine();
    }
    public ScriptEngineFactory getSef() {
        return sef;
    }

    public ScriptEngine getEngine() {
        return engine;
    }
    @Override
    public _NashornJS addParameter(String name , Object value){
        this.engine.put(name,value);
        return this;
    }
    @Override
    public _NashornJS setParameter(Map<String,Object> map){
        map.forEach(engine::put);
        return this;
    }
    @Override
    public Object runCode(String code){
        try {
            return engine.eval(code);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Object runFile(Reader file){
        try {
            return engine.eval(file);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }
}