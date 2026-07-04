package dev.anye.mc.st.js;

public class CJS {
	public static final _JavaScript<?> E = GetNewInstance();


	public static _JavaScript<?> GetNewInstance() {
		if (Js.GraalJs) return _GraalJS.NotSafe(false);
		else return new _NashornJS(false);
	}
}
