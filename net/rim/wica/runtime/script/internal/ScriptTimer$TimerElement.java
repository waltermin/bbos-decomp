package net.rim.wica.runtime.script.internal;

import net.rim.ecmascript.runtime.CompiledScript;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESFunction;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.ESString;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.script.ScriptCompiler;

final class ScriptTimer$TimerElement implements Runnable {
   private int _timerId;
   private Object _action;
   private long[] _args;
   private boolean _repeat;
   private long _repeatTime;
   private long _timeToExecuteAt;
   private ScriptEngineImpl _engine;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         if (this._action instanceof ESFunction) {
            this._engine.executeFunction((ESFunction)this._action, null, this._args);
            var6 = false;
         } else if (this._action instanceof ESString) {
            CompiledScript script = ScriptCompiler.compileScript((String)this._action);
            this._action = script;
            this._engine.executeScript(script);
            var6 = false;
         } else if (this._action instanceof CompiledScript) {
            this._engine.executeScript((CompiledScript)this._action);
            var6 = false;
         } else {
            ESObject esObj = (ESObject)this._action;
            long fieldHandle = esObj.getField("handleEvent");
            if (fieldHandle == Value.UNDEFINED && fieldHandle == Value.NULL) {
               this._repeat = false;
               return;
            }

            ESObject func = Convert.toObject(fieldHandle);
            this._engine.executeFunction((ESFunction)func, esObj, this._args);
            var6 = false;
         }
      } finally {
         if (var6) {
            this._repeat = false;
            return;
         }
      }
   }
}
