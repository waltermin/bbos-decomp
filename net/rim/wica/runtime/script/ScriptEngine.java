package net.rim.wica.runtime.script;

import net.rim.wica.runtime.metadata.component.Msg;
import net.rim.wica.runtime.metadata.component.REError;

public interface ScriptEngine {
   boolean call(int var1, long[] var2);

   boolean call(int var1, Msg var2);

   boolean call(int var1, REError var2);

   void startEngine(ScriptMonitor var1);

   void stopEngine();

   void stopExecution();
}
