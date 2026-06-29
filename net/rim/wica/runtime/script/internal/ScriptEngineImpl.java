package net.rim.wica.runtime.script.internal;

import java.util.Hashtable;
import net.rim.device.api.system.ObjectGroup;
import net.rim.ecmascript.runtime.CompiledScript;
import net.rim.ecmascript.runtime.ESFunction;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.GlobalObject;
import net.rim.ecmascript.runtime.Value;
import net.rim.wica.runtime.access.invoker.AccessInvokeService;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.metadata.component.Msg;
import net.rim.wica.runtime.metadata.component.REError;
import net.rim.wica.runtime.metadata.component.ScriptCollection;
import net.rim.wica.runtime.metadata.component.ui.ScreenManager;
import net.rim.wica.runtime.script.CompilerException;
import net.rim.wica.runtime.script.ScriptException;
import net.rim.wica.runtime.script.ScriptMonitor;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.ui.UiService;

public final class ScriptEngineImpl implements ScriptEngineInternal, Serviceable {
   private GlobalObject _globalObject;
   private WicaAppContext _context = new WicaAppContext(this);
   private WicletRuntime _runtime;
   private ScriptCollection _collection;
   private Hashtable _lookupTable = new Hashtable(32);
   private ScriptTimer _timer = new ScriptTimer(this);
   private ScriptEngineImpl$ScriptWatchdog _watchdog;
   private ScriptMonitor _monitor;
   static Class class$net$rim$wica$runtime$metadata$WicletRuntime;
   static Class class$net$rim$wica$runtime$metadata$component$ui$ScreenManager;
   static Class class$net$rim$wica$runtime$access$invoker$AccessInvokeService;
   static Class class$net$rim$wica$runtime$ui$UiService;

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean call(String name, long[] args) {
      boolean result = true;

      try {
         return this.executeFunction(name, args);
      } catch (Throwable var6) {
         this.unhandledError(name, t);
         return result;
      }
   }

   private final boolean executeFunction(String name, long[] parms) {
      name = GlobalObject.stringIntern(ObjectGroup.isInGroup(name) ? new String(name) : name);
      ESFunction function = Value.checkIfFunctionValue(this._globalObject.getField(name));
      return this.executeFunction(function, null, parms);
   }

   private final boolean executeFunctionNoEvents(ESFunction function, ESObject thiz, long[] parms) {
      try {
         return this.toReturnValue(this._globalObject.callFunction(function, thiz, parms));
      } catch (ExitThrownValue e) {
         this._runtime.stop();
         return true;
      }
   }

   private final boolean executeScriptNoEvents(CompiledScript script) {
      try {
         return this.toReturnValue(this._globalObject.run(script));
      } catch (ExitThrownValue e) {
         this._runtime.stop();
         return true;
      }
   }

   private final void preExecute() {
      if (this._monitor != null) {
         this._monitor.scriptPreExecute();
      }

      this._watchdog.kick(true);
   }

   private final void postExecute() {
      this._watchdog.kick(false);
      if (this._monitor != null) {
         this._monitor.scriptPostExecute();
      }
   }

   private final void unhandledError(String function, Exception e) {
      if (this._monitor != null) {
         this._monitor.scriptUnhandledError(function, e);
      }
   }

   private final boolean toReturnValue(long result) {
      switch (Value.getType(result)) {
         case 4:
            return Value.getBooleanValue(result);
         default:
            return true;
      }
   }

   public final WicletRuntime getRuntime() {
      return this._runtime;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final synchronized boolean executeFunction(ESFunction function, ESObject thiz, long[] parms) {
      this.preExecute();
      boolean result = true;
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         result = this.executeFunctionNoEvents(function, thiz, parms);
         var7 = false;
      } finally {
         if (var7) {
            this.postExecute();
         }
      }

      this.postExecute();
      return result;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final synchronized boolean executeScript(CompiledScript script) {
      this.preExecute();
      boolean result = true;
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         result = this.executeScriptNoEvents(script);
         var5 = false;
      } finally {
         if (var5) {
            this.postExecute();
         }
      }

      this.postExecute();
      return result;
   }

   @Override
   public final ESObject getCachedObject(Object key) {
      return (ESObject)this._lookupTable.get(key);
   }

   @Override
   public final GlobalObject getGlobalObject() {
      return this._globalObject;
   }

   @Override
   public final ScriptTimer getTimer() {
      return this._timer;
   }

   @Override
   public final void putCachedObject(Object key, ESObject value) {
      this._lookupTable.put(key, value);
   }

   @Override
   public final void synchronousTaskStarted() {
      this._watchdog.kick(false);
   }

   @Override
   public final void synchronousTaskCompleted() {
      this._watchdog.kick(true);
   }

   @Override
   public final synchronized boolean call(int functionId, long[] args) {
      long[] params = args != null ? this._context.getFunctionParams(args) : null;
      return this.call(this._collection.getFunctionName(functionId), params);
   }

   @Override
   public final synchronized boolean call(int functionId, Msg msg) {
      return this.call(this._collection.getFunctionName(functionId), null);
   }

   @Override
   public final synchronized boolean call(int functionId, REError error) {
      this._context.setMDSError(error);
      return this.call(this._collection.getFunctionName(functionId), null);
   }

   @Override
   public final synchronized void startEngine(ScriptMonitor param1) throws CompilerException, ScriptException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/ecmascript/runtime/GlobalObject.disableLiveConnect ()V
      // 03: aload 0
      // 04: invokestatic net/rim/ecmascript/runtime/GlobalObject.newInstance ()Lnet/rim/ecmascript/runtime/GlobalObject;
      // 07: putfield net/rim/wica/runtime/script/internal/ScriptEngineImpl._globalObject Lnet/rim/ecmascript/runtime/GlobalObject;
      // 0a: aload 1
      // 0b: ifnull 1f
      // 0e: aload 0
      // 0f: aload 1
      // 10: putfield net/rim/wica/runtime/script/internal/ScriptEngineImpl._monitor Lnet/rim/wica/runtime/script/ScriptMonitor;
      // 13: aload 0
      // 14: new net/rim/wica/runtime/script/internal/ScriptEngineImpl$ScriptWatchdog
      // 17: dup
      // 18: aload 0
      // 19: invokespecial net/rim/wica/runtime/script/internal/ScriptEngineImpl$ScriptWatchdog.<init> (Lnet/rim/wica/runtime/script/internal/ScriptEngineImpl;)V
      // 1c: putfield net/rim/wica/runtime/script/internal/ScriptEngineImpl._watchdog Lnet/rim/wica/runtime/script/internal/ScriptEngineImpl$ScriptWatchdog;
      // 1f: aload 0
      // 20: getfield net/rim/wica/runtime/script/internal/ScriptEngineImpl._runtime Lnet/rim/wica/runtime/metadata/WicletRuntime;
      // 23: invokeinterface net/rim/wica/runtime/metadata/WicletRuntime.getWiclet ()Lnet/rim/wica/runtime/metadata/Wiclet; 1
      // 28: astore 2
      // 29: aload 0
      // 2a: aload 2
      // 2b: invokeinterface net/rim/wica/runtime/metadata/Wiclet.getScripts ()Lnet/rim/wica/runtime/metadata/component/ScriptCollection; 1
      // 30: putfield net/rim/wica/runtime/script/internal/ScriptEngineImpl._collection Lnet/rim/wica/runtime/metadata/component/ScriptCollection;
      // 33: aload 0
      // 34: getfield net/rim/wica/runtime/script/internal/ScriptEngineImpl._context Lnet/rim/wica/runtime/script/internal/WicaAppContext;
      // 37: aload 2
      // 38: invokevirtual net/rim/wica/runtime/script/internal/WicaAppContext.load (Lnet/rim/wica/runtime/metadata/Wiclet;)V
      // 3b: aload 2
      // 3c: invokeinterface net/rim/wica/runtime/metadata/Wiclet.getContext ()Lnet/rim/wica/runtime/metadata/WicletContext; 1
      // 41: invokeinterface net/rim/wica/runtime/metadata/WicletContext.getWicletStore ()Lnet/rim/wica/runtime/persistence/WicletStore; 1
      // 46: astore 3
      // 47: aload 3
      // 48: ldc_w "wiclet.code"
      // 4b: invokeinterface net/rim/wica/runtime/persistence/WicletStore.getResource (Ljava/lang/String;)Lnet/rim/wica/runtime/persistence/Resource; 2
      // 50: astore 4
      // 52: aload 4
      // 54: ifnull 89
      // 57: aload 4
      // 59: invokevirtual net/rim/wica/runtime/persistence/Resource.getData ()[B
      // 5c: invokestatic net/rim/wica/runtime/script/ScriptCompiler.deserialize ([B)Lnet/rim/ecmascript/runtime/CompiledScript;
      // 5f: astore 5
      // 61: aload 0
      // 62: aload 5
      // 64: invokespecial net/rim/wica/runtime/script/internal/ScriptEngineImpl.executeScriptNoEvents (Lnet/rim/ecmascript/runtime/CompiledScript;)Z
      // 67: pop
      // 68: return
      // 69: astore 4
      // 6b: new net/rim/wica/runtime/script/CompilerException
      // 6e: dup
      // 6f: sipush 142
      // 72: invokestatic net/rim/wica/runtime/resources/RuntimeResources.getString (I)Ljava/lang/String;
      // 75: invokespecial net/rim/wica/runtime/script/CompilerException.<init> (Ljava/lang/String;)V
      // 78: athrow
      // 79: astore 4
      // 7b: new net/rim/wica/runtime/script/ScriptException
      // 7e: dup
      // 7f: sipush 143
      // 82: invokestatic net/rim/wica/runtime/resources/RuntimeResources.getString (I)Ljava/lang/String;
      // 85: invokespecial net/rim/wica/runtime/script/ScriptException.<init> (Ljava/lang/String;)V
      // 88: athrow
      // 89: return
      // try (31 -> 45): 46 null
      // try (31 -> 45): 53 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void stopEngine() {
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         try {
            var5 = true;
            this._timer.dispose();
            this._context.dispose();
            this._watchdog.kill();
            var5 = false;
         } catch (Throwable var8) {
            throw new ScriptException(e.getMessage());
         }
      } finally {
         if (var5) {
            this._globalObject.stop();
            this._globalObject.clearInstance();
            this._globalObject = null;
            this._watchdog = null;
         }
      }

      this._globalObject.stop();
      this._globalObject.clearInstance();
      this._globalObject = null;
      this._watchdog = null;
   }

   @Override
   public final void stopExecution() {
      this._globalObject.stop();
   }

   @Override
   public final void setServices(ServiceProvider provider) {
      this._runtime = (WicletRuntime)provider.getService(
         class$net$rim$wica$runtime$metadata$WicletRuntime == null
            ? (class$net$rim$wica$runtime$metadata$WicletRuntime = class$("net.rim.wica.runtime.metadata.WicletRuntime"))
            : class$net$rim$wica$runtime$metadata$WicletRuntime
      );
      this._context.setRuntime(this._runtime);
      this._context
         .setScreenManager(
            (ScreenManager)this._runtime
               .getService(
                  class$net$rim$wica$runtime$metadata$component$ui$ScreenManager == null
                     ? (class$net$rim$wica$runtime$metadata$component$ui$ScreenManager = class$("net.rim.wica.runtime.metadata.component.ui.ScreenManager"))
                     : class$net$rim$wica$runtime$metadata$component$ui$ScreenManager
               )
         );
      this._context
         .setAccessServ(
            (AccessInvokeService)provider.getService(
               class$net$rim$wica$runtime$access$invoker$AccessInvokeService == null
                  ? (class$net$rim$wica$runtime$access$invoker$AccessInvokeService = class$("net.rim.wica.runtime.access.invoker.AccessInvokeService"))
                  : class$net$rim$wica$runtime$access$invoker$AccessInvokeService
            )
         );
      this._context
         .setUiServ(
            (UiService)provider.getService(
               class$net$rim$wica$runtime$ui$UiService == null
                  ? (class$net$rim$wica$runtime$ui$UiService = class$("net.rim.wica.runtime.ui.UiService"))
                  : class$net$rim$wica$runtime$ui$UiService
            )
         );
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}
