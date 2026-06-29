package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.CompiledScript;
import net.rim.ecmascript.runtime.ESFunction;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.GlobalObject;
import net.rim.wica.runtime.script.ScriptEngine;

public interface ScriptEngineInternal extends ScriptEngine {
   boolean executeFunction(ESFunction var1, ESObject var2, long[] var3);

   boolean executeScript(CompiledScript var1);

   ESObject getCachedObject(Object var1);

   GlobalObject getGlobalObject();

   ScriptTimer getTimer();

   void putCachedObject(Object var1, ESObject var2);

   void synchronousTaskStarted();

   void synchronousTaskCompleted();
}
