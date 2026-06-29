package net.rim.ecmascript.runtime;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.ecmascript.compiler.Compiler;
import net.rim.ecmascript.regexp.RegExp$MatchResult;
import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;
import net.rim.vm.ThreadSpecificData;

public class GlobalObject extends RedirectedObject implements Debugger {
   ESObjectPrototype objectPrototype;
   GlobalObject _parent;
   Context contextFreeList;
   Context contextAllocList;
   ESArrayPrototype arrayPrototype;
   HostFunction arrayConstructor;
   PrintStream debugOut;
   PrintStream stdOut;
   int compilerFlags;
   ESBooleanPrototype booleanPrototype;
   ESDatePrototype datePrototype;
   ESEvalErrorPrototype evalErrorPrototype;
   ESFunctionPrototype functionPrototype;
   ESNamespacePrototype namespacePrototype;
   ESMath math;
   ESNumberPrototype numberPrototype;
   ESRangeErrorPrototype rangeErrorPrototype;
   ESReferenceErrorPrototype referenceErrorPrototype;
   ESRegExpPrototype regExpPrototype;
   ESStringPrototype stringPrototype;
   ESSyntaxErrorPrototype syntaxErrorPrototype;
   ESTypeErrorPrototype typeErrorPrototype;
   ESURIErrorPrototype URIErrorPrototype;
   ESErrorPrototype errorPrototype;
   HostFunction booleanConstructor;
   HostFunction dateConstructor;
   HostFunction errorConstructor;
   HostFunction evalErrorConstructor;
   HostFunction functionConstructor;
   HostFunction namespaceConstructor;
   HostFunction numberConstructor;
   HostFunction objectConstructor;
   HostFunction rangeErrorConstructor;
   HostFunction referenceErrorConstructor;
   HostFunction stringConstructor;
   HostFunction syntaxErrorConstructor;
   HostFunction typeErrorConstructor;
   HostFunction URIErrorConstructor;
   HostFunction regExpConstructor;
   JavaPackage rootJavaPackage;
   Hashtable javaClassLookupTable;
   int version = 0;
   TimeZone timeZone;
   HostFunction regExpExec;
   RegExp$MatchResult regExpLastMatch;
   boolean regExpMultiline;
   String regExpInput;
   long[] opStack;
   int opStackSize;
   private RedirectedObject _redirect;
   boolean stopExecution;
   String nameObject;
   String namePrototype;
   DebugCallback debugCallback;
   StepInfo stepInfo = new StepInfo();
   private Hashtable _allDebugScripts = new Hashtable();
   private Vector _importedPackages = null;
   private Object _syncObject = new Object();
   private GlobalObject[] _previousGlobals = new GlobalObject[10];
   private int _previousGlobalIndex = 0;
   private Thread _ownerThread;
   private int _ownCount;
   private int _ownerPriority;
   private Object _synchObject = new Object();
   private boolean _allowDelayedCompilation = false;
   private SimpleDateFormat _dateFormat;
   private SimpleDateFormat _timeFormat;
   private SimpleDateFormat _dateTimeFormat;
   static long _hash = NativeHash.NativeHash;
   static boolean noLiveConnect;
   public static final int V_1_0 = 100;
   public static final int V_1_1 = 110;
   public static final int V_1_2 = 120;
   public static final int V_1_3 = 130;
   public static final int V_1_4 = 140;
   public static final int V_1_5 = 150;
   public static final int V_DEFAULT = 0;
   private static final int InitialOpStackSize = 100;
   private static final int OpStackIncrement = 30;
   private static final int MaxOpStack = 16384;

   String dateFormat(long time) {
      if (this._dateFormat == null) {
         this._dateFormat = new SimpleDateFormat(32);
      }

      return this._dateFormat.formatLocal(time);
   }

   String timeFormat(long time) {
      if (this._timeFormat == null) {
         this._timeFormat = new SimpleDateFormat(4);
      }

      return this._timeFormat.formatLocal(time);
   }

   String dateTimeFormat(long time) {
      if (this._dateTimeFormat == null) {
         this._dateTimeFormat = new SimpleDateFormat(36);
      }

      return this._dateTimeFormat.formatLocal(time);
   }

   @Override
   long[] getValueArray() {
      return this.noRedirectGetValueArray();
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      return this._redirect != null ? this._redirect.notifyFieldChanged(name, value) : super.notifyFieldChanged(name, value);
   }

   void importPackage(JavaPackage pkg) {
      if (this._importedPackages == null) {
         this._importedPackages = new Vector();
      }

      this._importedPackages.addElement(pkg);
   }

   @Override
   public long requestFieldValue(String name) {
      long value;
      if (this._redirect != null) {
         value = this._redirect.requestFieldValue(name);
      } else {
         value = super.requestFieldValue(name);
      }

      if (this._importedPackages == null) {
         return value;
      }

      if (Value.getType(value) != 2) {
         return value;
      }

      int length = this._importedPackages.size();

      for (int i = 0; i < length; i++) {
         JavaPackage pkg = (JavaPackage)this._importedPackages.elementAt(i);
         value = pkg.getJavaClass(name);
         if (Value.getType(value) != 2) {
            return value;
         }
      }

      return value;
   }

   @Override
   public int notifyFieldDeleted(String name) {
      return this._redirect != null ? this._redirect.notifyFieldDeleted(name) : 2;
   }

   public void setRedirectionObject(RedirectedObject redir) {
      this._redirect = redir;
   }

   public RedirectedObject getRedirectionObject() {
      return this._redirect;
   }

   public void clearInstance() {
      synchronized (this._syncObject) {
         this.freeOpStack();
         Misc.cleanup();
      }
   }

   void newOpStack() {
      this.opStackSize = 100;
      this.opStack = Misc.newMixedArray(this.opStackSize);
   }

   void freeOpStack() {
      Misc.freeMixedArray(this.opStack);
      this.opStack = null;
      this.opStackSize = 0;
   }

   void growOpStack(int size) throws ThrownValue {
      if (size > 16384) {
         throw ThrownValue.internalError(Resources.getString(69));
      }

      int toGrow = size - this.opStackSize;
      if (toGrow < 30) {
         toGrow = 30;
      }

      this.opStackSize += toGrow;
      this.opStack = Misc.longArrayResize(this.opStack, this.opStackSize);
   }

   void shrinkOpStack() {
      this.freeOpStack();
      this.newOpStack();
   }

   public static GlobalObject getInstance() {
      return getInstance(Thread.currentThread());
   }

   public static GlobalObject getInstance(Thread thread) {
      GlobalObject global = getInstanceNoThrow(thread);
      if (global == null) {
         throw new RuntimeException(Resources.getString(68));
      } else {
         return global;
      }
   }

   private static synchronized GlobalObject getInstanceNoThrow(Thread thread) {
      return (GlobalObject)ThreadSpecificData.get(thread, 0);
   }

   private static void linkThreadToGlobal(Thread thread, GlobalObject global) {
      global.informVMAboutGlobalSwitch();
      ThreadSpecificData.set(thread, 0, global);
   }

   private void savePreviousLinkedGlobal(Thread currThread) {
      if (this._previousGlobalIndex >= this._previousGlobals.length) {
         this._previousGlobals = Misc.globalObjectArrayResize(this._previousGlobals, this._previousGlobalIndex + 10);
      }

      GlobalObject oldGlobal = getInstanceNoThrow(currThread);
      if (oldGlobal == null) {
         oldGlobal = this;
      }

      this._previousGlobals[this._previousGlobalIndex] = oldGlobal;
      this._previousGlobalIndex++;
   }

   void threadEnterGlobal() {
      synchronized (this._syncObject) {
         this.stopExecution = false;
         Thread currThread = Thread.currentThread();
         if (this._ownerThread == currThread) {
            this.savePreviousLinkedGlobal(currThread);
            linkThreadToGlobal(currThread, this);
            this._ownCount++;
         } else {
            while (this._ownerThread != null) {
               try {
                  this._syncObject.wait();
               } finally {
                  continue;
               }
            }

            this.savePreviousLinkedGlobal(currThread);
            linkThreadToGlobal(currThread, this);
            this._ownerThread = currThread;
            this._ownerPriority = currThread.getPriority();
            this._ownCount = 1;
            if (this._ownerPriority >= 5) {
               currThread.setPriority(4);
            }
         }
      }
   }

   void threadExitGlobal() {
      synchronized (this._syncObject) {
         linkThreadToGlobal(Thread.currentThread(), this._previousGlobals[--this._previousGlobalIndex]);
         if (--this._ownCount == 0) {
            this._ownerThread.setPriority(this._ownerPriority);
            this._ownerThread = null;
            this._syncObject.notify();
         }
      }
   }

   public static synchronized GlobalObject newInstance() {
      return new GlobalObject();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private GlobalObject() {
      super("global", new ESObjectPrototype());
      this.nameObject = "Object";
      this.namePrototype = "prototype";
      this.threadEnterGlobal();
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         this.setGrowthIncrement(100);
         this.objectPrototype = (ESObjectPrototype)this.getPrototype();
         this.arrayPrototype = new ESArrayPrototype();
         this.stdOut = System.out;
         this.booleanPrototype = new ESBooleanPrototype();
         this.datePrototype = new ESDatePrototype();
         this.functionPrototype = new ESFunctionPrototype();
         this.namespacePrototype = new ESNamespacePrototype();
         this.math = new ESMath();
         this.numberPrototype = new ESNumberPrototype();
         this.errorPrototype = new ESErrorPrototype();
         this.evalErrorPrototype = new ESEvalErrorPrototype();
         this.rangeErrorPrototype = new ESRangeErrorPrototype();
         this.referenceErrorPrototype = new ESReferenceErrorPrototype();

         try {
            this.regExpPrototype = new ESRegExpPrototype();
            this.stringPrototype = new ESStringPrototype();
         } catch (ThrownValue var11) {
         }

         this.syntaxErrorPrototype = new ESSyntaxErrorPrototype();
         this.typeErrorPrototype = new ESTypeErrorPrototype();
         this.URIErrorPrototype = new ESURIErrorPrototype();
         this.booleanConstructor = new ESBooleanPrototype$Constructor();
         this.arrayConstructor = new ESArrayPrototype$Constructor();
         this.dateConstructor = new ESDatePrototype$Constructor();
         this.errorConstructor = new ESErrorPrototype$Constructor("Error", this.errorPrototype);
         this.evalErrorConstructor = new ESEvalErrorPrototype$Constructor();
         this.functionConstructor = new ESFunctionPrototype$Constructor();
         this.namespaceConstructor = new ESNamespacePrototype$Constructor();
         this.numberConstructor = new ESNumberPrototype$Constructor();
         this.objectConstructor = new ESObjectPrototype$Constructor();
         this.rangeErrorConstructor = new ESRangeErrorPrototype$Constructor();
         this.referenceErrorConstructor = new ESReferenceErrorPrototype$Constructor();
         this.stringConstructor = new ESStringPrototype$Constructor();
         this.syntaxErrorConstructor = new ESSyntaxErrorPrototype$Constructor();
         this.typeErrorConstructor = new ESTypeErrorPrototype$Constructor();
         this.URIErrorConstructor = new ESURIErrorPrototype$Constructor();
         this.regExpConstructor = new ESRegExpPrototype$Constructor();
         this.javaClassLookupTable = new Hashtable();
         boolean var10 = false /* VF: Semaphore variable */;

         label55:
         try {
            var10 = true;
            this.timeZone = TimeZone.getDefault();
            var10 = false;
         } finally {
            if (var10) {
               this.timeZone = null;
               break label55;
            }
         }

         GlobalProperties.populate(this, noLiveConnect);
         this.newOpStack();
         var6 = false;
      } finally {
         if (var6) {
            this.threadExitGlobal();
         }
      }

      this.threadExitGlobal();
   }

   public void stop() {
      this.stopExecution = true;
   }

   public static void disableLiveConnect() {
      noLiveConnect = true;
   }

   public long run(PrintStream pDebugOut, int pCompilerFlags, PrintStream pStdOut, CompiledScript script) {
      this.stdOut = pStdOut;
      this.debugOut = pDebugOut;
      this.compilerFlags = pCompilerFlags;
      if (script.forDebug) {
         this.compilerFlags |= 16777216;
      }

      return this.run(script);
   }

   public long run(PrintStream pStdOut, CompiledScript script) {
      this.stdOut = pStdOut;
      return this.run(script);
   }

   public static String stringIntern(String s) {
      return Misc.stringIntern(s);
   }

   public static boolean versionCheck(CompiledScript script) {
      if (script._isRedirected != Compiler.getMajorVersion()) {
         return false;
      } else {
         return script._isRedirected ? true : script._functionType == Compiler.getMinorVersion();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public long runRecursive(CompiledScript script) {
      long[] savedOpStack = this.opStack;
      boolean var7 = false /* VF: Semaphore variable */;

      long var3;
      try {
         var7 = true;
         var3 = this.run(script);
         var7 = false;
      } finally {
         if (var7) {
            this.opStack = savedOpStack;
         }
      }

      this.opStack = savedOpStack;
      return var3;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public long run(CompiledScript script) throws ThrownValue {
      if (!versionCheck(script)) {
         throw ThrownValue.syntaxError(
            Resources.getString(49),
            script.compilerVersionMajor + "." + script.compilerVersionMinor,
            Compiler.getMajorVersion() + "." + Compiler.getMinorVersion()
         );
      }

      this.threadEnterGlobal();
      boolean var11 = false /* VF: Semaphore variable */;

      long var7;
      try {
         var11 = true;
         ScopeChain globalScope = null;
         if (this.getParent() != null) {
            ScopeChain sc = new ScopeChain(null, this, this);
            globalScope = new ScopeChain(sc, this.getParent().getRedirectionObject(), this.getParent().getRedirectionObject());
         } else {
            globalScope = new ScopeChain(null, this, this);
         }

         this.version = script.version;
         ESFunction func = new ESFunction(script, globalScope);
         Context context = new Context(this, null, globalScope, this, this, 4, func, 0, false);
         context.createLocals();
         context.createFunctions();
         this.linkGlobals(script);
         long rc = Context.run(context);
         this.shrinkOpStack();
         var7 = rc;
         var11 = false;
      } finally {
         if (var11) {
            this.threadExitGlobal();
         }
      }

      this.threadExitGlobal();
      return var7;
   }

   private void setGlobals(CompiledScript script, int[] globals) {
      script.globals = globals;

      for (int i = script.getNumFunctionExpressions() - 1; i >= 0; i--) {
         this.setGlobals(script.functionExpressions[i], globals);
      }

      for (int i = script.getNumFunctionDeclarations() - 1; i >= 0; i--) {
         this.setGlobals(script.functionDeclarations[i], globals);
      }
   }

   void linkGlobals(CompiledScript script) {
      int[] locals = script.locals;
      int length = locals.length;
      int[] globals = new int[length];

      for (int i = 0; i < length; i++) {
         globals[i] = this.createPlaceHolder(script.ids[locals[i]]);
      }

      this.setGlobals(script, globals);
   }

   public long callFunction(ESFunction func) {
      return this.callFunction(func, null, null);
   }

   public long callFunction(ESFunction func, long[] parms) {
      return this.callFunction(func, null, parms);
   }

   public long callFunction(ESFunction func, ESObject thiz, long[] parms) {
      return this.callFunction(func, thiz, parms, 0, parms != null ? parms.length : 0);
   }

   public void setParent(GlobalObject parent) {
      this._parent = parent;
   }

   public GlobalObject getParent() {
      return this._parent;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public long callFunction(ESFunction func, ESObject thiz, long[] parms, int firstParm, int numParms) {
      this.threadEnterGlobal();
      boolean var10 = false /* VF: Semaphore variable */;

      long var6;
      try {
         var10 = true;
         if (thiz == null) {
            thiz = this;
         }

         if (parms == null) {
            parms = Names.NoParms;
         }

         var6 = Context.callFunction(func, thiz, parms, firstParm, numParms, false);
         var10 = false;
      } finally {
         if (var10) {
            this.threadExitGlobal();
         }
      }

      this.threadExitGlobal();
      return var6;
   }

   public long callConstructor(ESFunction func) {
      return this.callConstructor(func, null, null);
   }

   public long callConstructor(ESFunction func, long[] parms) {
      return this.callConstructor(func, null, parms);
   }

   public long callConstructor(ESFunction func, ESObject thiz, long[] parms) {
      return this.callConstructor(func, thiz, parms, 0, parms != null ? parms.length : 0);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public long callConstructor(ESFunction func, ESObject thiz, long[] parms, int firstParm, int numParms) {
      this.threadEnterGlobal();
      boolean var12 = false /* VF: Semaphore variable */;

      long var14;
      label53: {
         try {
            var12 = true;
            if (thiz == null) {
               thiz = new ESObject();
            }

            if (parms == null) {
               parms = Names.NoParms;
            }

            long rc = Context.callFunction(func, thiz, parms, firstParm, numParms, true);
            if (Value.getType(rc) == 6) {
               var14 = rc;
               var12 = false;
               break label53;
            }

            var14 = Value.makeObjectValue(thiz);
            var12 = false;
         } finally {
            if (var12) {
               this.threadExitGlobal();
            }
         }

         this.threadExitGlobal();
         return var14;
      }

      this.threadExitGlobal();
      return var14;
   }

   public void setFakeTimeZone() {
      this.timeZone = null;
   }

   public ESObjectPrototype getObjectPrototype() {
      return this.objectPrototype;
   }

   native boolean initVM();

   native void informVMAboutGlobalSwitch();

   public void setAllowDelayedCompilation(boolean allow) {
      this._allowDelayedCompilation = allow;
   }

   public boolean getAllowDelayedCompilation() {
      return this._allowDelayedCompilation;
   }

   @Override
   public void registerDebugCallback(DebugCallback d) {
      this.debugCallback = d;
   }

   @Override
   public boolean setBreakpoint(DebugScript script, int line) {
      this._allDebugScripts.put(script, script);
      return ((CompiledScript)script).setBreakpoint(line);
   }

   @Override
   public boolean clearBreakpoint(DebugScript script, int line) {
      return ((CompiledScript)script).clearBreakpoint(line);
   }

   @Override
   public void clearAllBreakpoints() {
      Enumeration e = this._allDebugScripts.keys();

      while (e.hasMoreElements()) {
         ((CompiledScript)e.nextElement()).clearAllBreakpoints();
      }

      this._allDebugScripts = new Hashtable();
   }

   @Override
   public void setStepMode(int mode) {
      this.stepInfo.stepMode = mode;
   }

   @Override
   public void catchExceptions(boolean all) {
      this.stepInfo.allExceptions = all;
   }
}
