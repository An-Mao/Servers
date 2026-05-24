package dev.anye.mc.st.js;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class _GraalJS extends _JavaScript<_GraalJS> {
    private final Context context;
    private final Value bindings;

    public static _GraalJS NotSafe(boolean cache){
        return new _GraalJS( Context.newBuilder("js")
                .allowAllAccess(true)
                .option("engine.WarnInterpreterOnly", "false")
                .build(),cache);
    }
    public _GraalJS(Context context, Value bindings,boolean cache){
        super(cache);
        this.context = context;
        this.bindings = bindings;
    }
    public _GraalJS(Context context,boolean cache){
        this(context,context.getBindings("js"),cache);
    }
    public _GraalJS(boolean cache){
        super(cache);
        context = Context.create("js");
        bindings = context.getBindings("js");
    }
    public Context getEngine() {
        return context;
    }
    @Override
    public _GraalJS addParameter(String name , Object value){
        this.bindings.putMember(name,value);
        return this;
    }
    @Override
    public _GraalJS setParameter(Map<String,Object> map){
        map.forEach(bindings::putMember);
        return this;
    }
    @Override
    public Object runCode(String code){
        return context.eval("js",code);
    }
    @Override
    public Object runFile(Reader file){
        try {
            return context.eval(Source.newBuilder("js",file,"").build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
