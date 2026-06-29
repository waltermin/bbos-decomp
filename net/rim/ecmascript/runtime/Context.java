package net.rim.ecmascript.runtime;

import net.rim.ecmascript.compiler.CompileError;
import net.rim.ecmascript.compiler.Compiler;
import net.rim.ecmascript.compiler.OpcodeConstants;
import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;

class Context implements OpcodeConstants, DebugContext {
   private Context _caller;
   private ESObject _variables;
   private ScopeChain _scope;
   private long[] _locals;
   private int _localOffset;
   private TryBlock _try;
   private ESObject _this;
   private int _variableProperties;
   private ESFunction _callee;
   private CompiledScript _code;
   private byte[] _byteCode;
   private int _ip;
   private int _stackTop;
   private int _sp;
   private boolean _argsDeleted;
   private boolean _calledAsConstructor;
   private boolean _evalAllowed;
   private long[] _passedParms;
   private int _passedParmOffset;
   private int _nParms;
   private ESObject _getNameThisObject;
   private boolean _supressNativeExecution;
   private boolean _forDebug;
   private ThrownValue _thrownValue;
   private GlobalObject _savedGlobalObject;
   private long[] _savedOpStack;
   private int _savedOpStackSize;
   private GlobalObject _newGlobalObject;

   private Context() {
   }

   Context(
      GlobalObject global,
      Context caller,
      ScopeChain scope,
      ESObject thiz,
      ESObject variables,
      int variableProperties,
      ESFunction callee,
      int stackTop,
      boolean calledAsConstructor
   ) {
      this._caller = caller;
      this._scope = scope;
      this._try = null;
      this._this = thiz;
      this._variables = variables;
      this._variableProperties = variableProperties;
      this._code = callee.getCode();
      if (this._code.forDebug) {
         this._supressNativeExecution = true;
         this._forDebug = true;
         global.stepInfo.newContext();
      }

      if (this._code.maxStack + stackTop >= global.opStackSize) {
         global.growOpStack(stackTop + this._code.maxStack);
      }

      this._callee = callee;
      this._byteCode = this._code.code;
      this._ip = 0;
      this._argsDeleted = false;
      this._stackTop = stackTop;
      this._sp = stackTop + this._code.numTemps;
      this._calledAsConstructor = calledAsConstructor;
   }

   Context makeEvalContext(GlobalObject global, CompiledScript code) {
      ESFunction callee = new ESFunction(code, this._scope);
      Context context = new Context(global, null, this._scope, this._this, this._variables, 0, callee, this._sp, false);
      context.createLocals();
      context.createFunctions();
      return context;
   }

   boolean evalAllowed() {
      return this._evalAllowed;
   }

   boolean calledAsConstructor() {
      return this._calledAsConstructor;
   }

   Context getCaller() {
      return this._caller;
   }

   CompiledScript getCode() {
      return this._code;
   }

   int getIP() {
      return this._ip;
   }

   void createLocals() {
      int length = this._code.lastRealLocal;
      if (this._variables == null) {
         this._locals = Misc.newMixedArray(this._code.lastRealLocal);
         this._localOffset = 0;

         for (int i = length - 1; i >= 0; i--) {
            this._locals[i] = Value.UNDEFINED;
         }
      } else {
         int[] locals = this._code.locals;

         int i;
         for (i = 0; i < length; i++) {
            this._variables.replaceField(this._code.ids[locals[i]], this._variableProperties, Value.UNDEFINED);
         }

         for (int var5 = locals.length; i < var5; i++) {
            this._variables.createPlaceHolder(this._code.ids[locals[i]]);
         }

         this._locals = this._variables.getValueArray();
         this._localOffset = 0;
      }
   }

   void createFunctions() {
      if (this._variables != null) {
         int length = this._code.getNumFunctionDeclarations();

         for (int i = 0; i < length; i++) {
            CompiledScript fd = this._code.functionDeclarations[i];
            ESFunction f = new ESFunction(fd, this._scope);
            this._variables.replaceField(fd.ids[fd.id], this._variableProperties, Value.makeObjectValue(f));
         }
      }
   }

   private static int assemble(byte i1, byte i2, byte i3, byte i4) {
      return (i1 << 24) + ((i2 & 0xFF) << 16) + ((i3 & 0xFF) << 8) + (i4 & 0xFF);
   }

   private static int assemble(byte i1, byte i2) {
      return (i1 << 8) + (i2 & 0xFF);
   }

   private static Context newContext(
      GlobalObject global,
      Context caller,
      ESObject thiz,
      CompiledScript code,
      ESFunction forFunction,
      boolean calledAsConstructor,
      long[] passedParms,
      int passedParmOffset,
      int nParms,
      int stackTop
   ) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   private GlobalObject enterNewGlobal(GlobalObject global, GlobalObject newGlobal) {
      if (newGlobal == null) {
         return global;
      }

      if (global == newGlobal) {
         return global;
      }

      newGlobal.threadEnterGlobal();
      this._savedOpStack = newGlobal.opStack;
      this._savedOpStackSize = newGlobal.opStackSize;
      this._savedGlobalObject = global;
      newGlobal.opStack = global.opStack;
      newGlobal.opStackSize = global.opStackSize;
      return newGlobal;
   }

   private GlobalObject exitNewGlobal(GlobalObject global) {
      GlobalObject oldGlobal = this._savedGlobalObject;
      if (oldGlobal == null) {
         return global;
      }

      global.opStack = this._savedOpStack;
      global.opStackSize = this._savedOpStackSize;
      this._savedOpStack = null;
      this._savedGlobalObject = null;
      global.threadExitGlobal();
      return oldGlobal;
   }

   private static Context call(GlobalObject global, Context c, int nParms, boolean calledAsConstructor) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   static long callProperty(ESObject thiz, String funcName, long[] parms) {
      try {
         long funcValue = thiz.getField(funcName);
         ESFunction func = Value.checkIfFunctionValue(funcValue);
         return func == null ? Value.DEFAULT : callFunction(func, thiz, parms, 0, parms.length, false);
      } catch (ThrownValue tv) {
         return Value.DEFAULT;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static long callFunction(ESFunction func, ESObject thiz, long[] parms, int firstParm, int numParms, boolean calledAsConstructor) {
      CompiledScript code = func.getCode();
      GlobalObject global = GlobalObject.getInstance();
      if (code != null) {
         long[] stack = global.opStack;
         int stackSize = global.opStackSize;
         boolean var22 = false /* VF: Semaphore variable */;

         long var13;
         try {
            var22 = true;
            global.newOpStack();
            Context calledContext = newContext(global, null, thiz, code, func, calledAsConstructor, parms, firstParm, numParms, 0);
            global = calledContext.enterNewGlobal(global, func.getGlobalObject());
            long var27 = run(calledContext);
            global = calledContext.exitNewGlobal(global);
            var13 = var27;
            var22 = false;
         } finally {
            if (var22) {
               global.freeOpStack();
               global.opStack = stack;
               global.opStackSize = stackSize;
            }
         }

         global.freeOpStack();
         global.opStack = stack;
         global.opStackSize = stackSize;
         return var13;
      } else {
         func.getGlobalObject().threadEnterGlobal();
         boolean var19 = false /* VF: Semaphore variable */;

         long rc;
         try {
            var19 = true;
            HostFunction hostFunc = (HostFunction)func;
            hostFunc._parmArray = parms;
            hostFunc._numParms = numParms;
            hostFunc._firstParm = firstParm;
            hostFunc._thisObj = thiz;
            hostFunc._calledAsConstructor = calledAsConstructor;
            hostFunc._context = null;
            long rcx = hostFunc.run();
            rc = rcx;
            var19 = false;
         } finally {
            if (var19) {
               func.getGlobalObject().threadExitGlobal();
            }
         }

         func.getGlobalObject().threadExitGlobal();
         return rc;
      }
   }

   private static long deleteName(ScopeChain scope, String name) {
      while (scope != null) {
         ESObject o = scope._variables;
         if (o.hasField(name)) {
            return Value.makeBooleanValue(o.deleteField(name));
         }

         scope = scope._caller;
      }

      return Value.FALSE;
   }

   private static long typeOfName(ScopeChain scope, String name) {
      while (scope != null) {
         ESObject o = scope._variables;
         long v = o.getRedirectedField(name);
         if (v != Value.DEFAULT) {
            return Value.typeOf(v);
         }

         if (o.hasField(name)) {
            return Value.typeOf(o.getField(name));
         }

         scope = scope._caller;
      }

      return Value.typeOf(Value.UNDEFINED);
   }

   private long getName(ScopeChain scope, String name) throws ThrownValue {
      while (scope != null) {
         ESObject o = scope.object;
         long v = o.getRedirectedField(name);
         if (v != Value.DEFAULT) {
            this._getNameThisObject = scope.thiz;
            return v;
         }

         if (o.hasField(name)) {
            this._getNameThisObject = scope.thiz;
            return o.getFieldAllowExceptions(name);
         }

         scope = scope.next;
      }

      throw ThrownValue.referenceError(name);
   }

   private static void putName(ScopeChain scope, String name, long value) {
      while (true) {
         ESObject o = scope._variables;
         ScopeChain next = scope._caller;
         if (o.putRedirectedField(name, value)) {
            return;
         }

         if (next == null || o.hasField(name)) {
            o.putField(name, value);
            return;
         }

         scope = next;
      }
   }

   private static void constName(ScopeChain scope, String name) {
      while (true) {
         ESObject o = scope._variables;
         ScopeChain next = scope._caller;
         if (next == null || o.hasField(name)) {
            o.setAttributesField(name, 1);
            return;
         }

         scope = next;
      }
   }

   private static Context findExceptionHandler(GlobalObject global, ThrownValue th, Context c, StepInfo stepInfo) {
      Context search = c;

      while (true) {
         TryBlock tryBlock = search._try;
         if (tryBlock != null) {
            search._try = tryBlock.next;
            search._ip = tryBlock.catchIp;
            search._scope = tryBlock.scope;
            search._sp = search._stackTop + search._code.numTemps;
            search._newGlobalObject = global;
            GlobalObject.getInstance().opStack[search._sp++] = th.getValue();
            return search;
         }

         Context caller = search._caller;
         if (caller == null) {
            return null;
         }

         global = caller.exitNewGlobal(global);
         search = caller;
      }
   }

   private long createArgumentsArray() {
      if (this._argsDeleted) {
         return Value.UNDEFINED;
      }

      if (this._variables != null && this._variables.hasOwnField("arguments")) {
         return this._variables.getField("arguments");
      }

      ESObject args = new ESObject("Arguments", GlobalObject.getInstance().objectPrototype);

      for (int i = 0; i < this._nParms; i++) {
         args.putIndex(i, this._passedParms[this._passedParmOffset + i]);
      }

      args.setAllAttributes(2);
      args.addField("length", 2, Value.makeIntegerValue(this._nParms));
      args.addField("callee", 2, Value.makeObjectValue(this._callee));
      long rc = Value.makeObjectValue(args);
      if (this._variables != null) {
         this._variables.addField("arguments", 2, rc);
      }

      return rc;
   }

   private long getFunctionLength(ESFunction func) {
      for (Context search = this; search != null; search = search._caller) {
         if (search._callee == func) {
            return Value.makeIntegerValue(search._nParms);
         }
      }

      return Value.makeIntegerValue(func.getCode().numParms);
   }

   private long buildFunctionArguments(ESFunction func) {
      for (Context search = this; search != null; search = search._caller) {
         if (search._callee == func) {
            return search.createArgumentsArray();
         }
      }

      return Value.NULL;
   }

   static native Context runNative(Context var0);

   static long run(Context c) throws ThrownValue {
      GlobalObject global = GlobalObject.getInstance();
      StepInfo stepInfo = global.stepInfo;
      if (!global.initVM()) {
         throw ThrownValue.internalError(Resources.getString(33));
      }

      label610:
      while (!global.stopExecution) {
         if (!c._supressNativeExecution) {
            c = runNative(c);
         }

         label608:
         while (true) {
            int sp = c._sp;
            int ip = c._ip;
            byte[] code = c._byteCode;
            long[] locals = c._locals;
            int localOffset = c._localOffset;
            int[] globalIndexes = c._code.globals;
            long[] globalValues = global.getValueArray();
            long[] stack = global.opStack;
            DebugCallback debugCallback = global.debugCallback;

            try {
               while (!global.stopExecution) {
                  byte op = code[ip++];
                  switch (op) {
                     case -129:
                     case -115:
                     case -114:
                     case -112:
                     case -111:
                     case -110:
                     case -109:
                     case -108:
                     case -107:
                     case -106:
                     case -105:
                     case -104:
                     case -103:
                     case -102:
                     case -101:
                     case -100:
                     case -99:
                     case -98:
                     case -97:
                     case -96:
                     case -95:
                     case -94:
                     case -93:
                     case -92:
                     case -91:
                     case -90:
                     case -89:
                     case -88:
                     case -87:
                     case -86:
                     case -85:
                     case -84:
                     case -83:
                     case -82:
                     case -81:
                     case -80:
                     case -79:
                     case -78:
                     case -77:
                     case -76:
                     case -75:
                     case -74:
                     case -73:
                     case -72:
                     case -71:
                     case -70:
                     case -69:
                     case -68:
                     case -67:
                     case -66:
                     case -65:
                     case -64:
                     case -63:
                     case -62:
                     case -61:
                     case -60:
                     case -59:
                     case -58:
                     case -57:
                     case -56:
                     case -55:
                     case -54:
                     case -53:
                     case -52:
                     case -51:
                     case -50:
                     case -49:
                     case -48:
                     case -47:
                     case -46:
                     case -45:
                     case -44:
                     case -43:
                     case -42:
                     case -41:
                     case -40:
                     case -39:
                     case -38:
                     case -37:
                     case -36:
                     case -35:
                     case -34:
                     case -33:
                     case -32:
                     case -31:
                     case -30:
                     case -29:
                     case -28:
                     case -27:
                     case -26:
                     case -25:
                     case -24:
                     case -23:
                     case -22:
                     case -21:
                     case -20:
                     case -19:
                     case -18:
                     case -17:
                     case -16:
                     case -15:
                     case -14:
                     case -13:
                     case -12:
                     case -11:
                     case -10:
                     case -9:
                     case -8:
                     case -7:
                     case -6:
                     case -5:
                     case -4:
                     case -3:
                     case -2:
                     case -1:
                     case 10:
                        return Value.UNDEFINED;
                     case -128:
                        stack[code[ip++] + c._stackTop] = stack[sp - 1];
                        break;
                     case -127:
                        ESObject var120 = Value.checkIfObjectValue(stack[sp - 2]);
                        if (var120 != null) {
                           ESFunction var129 = Value.checkIfFunctionValue(stack[sp - 1]);
                           if (var129 != null && var129.getCode() != null) {
                              ESObject prototype = Value.checkIfObjectValue(var129.getField("prototype"));
                              if (prototype != null) {
                                 var120.setPrototype(prototype);
                              }
                           }
                        }
                        break;
                     case -126:
                        sp--;
                        stack[sp - 1] = Expr.sub(stack[sp - 1], stack[sp]);
                        break;
                     case -125:
                        long var88 = stack[sp - 1];
                        stack[sp - 1] = stack[sp - 2];
                        stack[sp - 2] = var88;
                        break;
                     case -124:
                        stack[sp - 1] = Value.makeBooleanValue(!Convert.toBoolean(stack[sp - 1]));
                        break;
                     case -123:
                        stack[sp - 1] = Value.makeBooleanValue(Convert.toBoolean(stack[sp - 1]));
                        break;
                     case -122:
                        stack[sp - 1] = Convert.toNumber(stack[sp - 1]);
                        break;
                     case -121:
                        throw new ThrownValue(stack[--sp]);
                     case -120:
                        stack[sp] = typeOfName(c._scope, c._code.ids[assemble(code[ip++], code[ip++])]);
                        sp++;
                        break;
                     case -119:
                        stack[sp - 1] = Value.typeOf(stack[sp - 1]);
                        break;
                     case -118:
                        sp--;
                        stack[sp - 1] = Expr.ursh(stack[sp - 1], stack[sp]);
                        break;
                     case -117:
                     default:
                        int lineNum = assemble(code[ip++], code[ip++]);
                        if (debugCallback != null && c._forDebug) {
                           stepInfo.offset = ip - 5;
                           stepInfo.line = lineNum;
                           int event = 0;
                           c._sp = sp;
                           c._ip = ip;
                           if (c._code.haveBreakpoint(lineNum)) {
                              stepInfo.reset();
                              debugCallback.event(c, 1);
                              if (code[ip] == -116) {
                                 ip++;
                              }
                           } else {
                              switch (stepInfo.stepMode) {
                                 case 0:
                                    break;
                                 case 1:
                                    stepInfo.reset();
                                    debugCallback.event(c, 2);
                                    if (code[ip] == -116) {
                                       ip++;
                                    }
                                    break;
                                 case 2:
                                 default:
                                    if (stepInfo.numCalls <= 0) {
                                       stepInfo.reset();
                                       debugCallback.event(c, 2);
                                       if (code[ip] == -116) {
                                          ip++;
                                       }
                                    }
                                    break;
                                 case 3:
                                    if (stepInfo.numCalls < 0) {
                                       stepInfo.reset();
                                       debugCallback.event(c, 2);
                                       if (code[ip] == -116) {
                                          ip++;
                                       }
                                    }
                              }
                           }
                        }
                        break;
                     case -116:
                        if (debugCallback != null && c._forDebug) {
                           c._sp = sp;
                           c._ip = ip;
                           stepInfo.reset();
                           debugCallback.event(c, 1);
                        }
                        break;
                     case -113:
                        long overrides = stack[--sp];
                        int var127 = code[ip++];
                        HostFunction hf = ((JavaClass)Value.checkIfObjectValue(stack[sp - var127 - 1])).getConstructor();
                        if (hf instanceof JavaInterface) {
                           if (var127 != 0) {
                              throw ThrownValue.typeError(Resources.getString(65));
                           }

                           JavaInterface ji = (JavaInterface)hf;
                           ji.setOverrides(Value.checkIfObjectValue(overrides));
                           sp--;
                           stack[sp - 1] = ji.run();
                        } else {
                           if (!(hf instanceof JavaConstructor) && !(hf instanceof JavaOverloadedConstructor)) {
                              throw ThrownValue.typeError(Resources.getString(66));
                           }

                           c._sp = sp;
                           c._ip = ip;
                           c = call(global, c, var127, true);
                           global = c._newGlobalObject;
                           globalValues = global.getValueArray();
                           globalIndexes = c._code.globals;
                           sp = c._sp;
                           JavaClassInstance jc = (JavaClassInstance)Value.checkIfObjectValue(stack[sp - 1]);
                           jc.setOverrides(Convert.toObject(overrides));
                        }
                        break;
                     case 0:
                        sp--;
                        stack[sp - 1] = Expr.add(stack[sp - 1], stack[sp]);
                        break;
                     case 1:
                        sp--;
                        stack[sp - 1] = Expr.bitand(stack[sp - 1], stack[sp]);
                        break;
                     case 2:
                        stack[sp - 1] = Expr.bitnot(stack[sp - 1]);
                        break;
                     case 3:
                        sp--;
                        stack[sp - 1] = Expr.bitor(stack[sp - 1], stack[sp]);
                        break;
                     case 4:
                        sp--;
                        stack[sp - 1] = Expr.bitxor(stack[sp - 1], stack[sp]);
                        break;
                     case 5:
                        c._evalAllowed = true;
                        int var126 = assemble(code[ip++], code[ip++]);
                        c._sp = sp;
                        c._ip = ip;
                        c = call(global, c, var126, false);
                        global = c._newGlobalObject;
                        c._evalAllowed = false;
                        continue label610;
                     case 6:
                        int var125 = code[ip++];
                        c._sp = sp;
                        c._ip = ip;
                        c = call(global, c, var125, false);
                        global = c._newGlobalObject;
                        continue label610;
                     case 7:
                        int nParms = assemble(code[ip++], code[ip++]);
                        c._sp = sp;
                        c._ip = ip;
                        c = call(global, c, nParms, false);
                        global = c._newGlobalObject;
                        continue label610;
                     case 8:
                        int var123 = code[ip++];
                        c._sp = sp;
                        c._ip = ip;
                        c = call(global, c, var123, true);
                        global = c._newGlobalObject;
                        Misc.GC();
                        continue label610;
                     case 9:
                        int var122 = assemble(code[ip++], code[ip++]);
                        c._sp = sp;
                        c._ip = ip;
                        c = call(global, c, var122, true);
                        global = c._newGlobalObject;
                        Misc.GC();
                        continue label610;
                     case 11:
                        sp--;
                        stack[sp - 1] = Value.makeBooleanValue(Expr.eq(stack[sp - 1], stack[sp]));
                        break;
                     case 12:
                        sp--;
                        stack[sp - 1] = Value.makeBooleanValue(Expr.ge(stack[sp - 1], stack[sp]));
                        break;
                     case 13:
                        sp--;
                        stack[sp - 1] = Value.makeBooleanValue(Expr.gt(stack[sp - 1], stack[sp]));
                        break;
                     case 14:
                        sp--;
                        stack[sp - 1] = Value.makeBooleanValue(Expr.le(stack[sp - 1], stack[sp]));
                        break;
                     case 15:
                        sp--;
                        stack[sp - 1] = Value.makeBooleanValue(Expr.lt(stack[sp - 1], stack[sp]));
                        break;
                     case 16:
                        sp--;
                        stack[sp - 1] = Value.makeBooleanValue(!Expr.eq(stack[sp - 1], stack[sp]));
                        break;
                     case 17:
                        sp--;
                        stack[sp - 1] = Value.makeBooleanValue(Expr.stricteq(stack[sp - 1], stack[sp]));
                        break;
                     case 18:
                        sp--;
                        stack[sp - 1] = Value.makeBooleanValue(!Expr.stricteq(stack[sp - 1], stack[sp]));
                        break;
                     case 19:
                        stack[sp - 1] = Expr.sub(stack[sp - 1], Value.ONE);
                        break;
                     case 20:
                        stack[sp++] = deleteName(c._scope, "arguments");
                        c._argsDeleted = true;
                        break;
                     case 21:
                        stack[sp++] = deleteName(c._scope, c._code.ids[assemble(code[ip++], code[ip++])]);
                        break;
                     case 22: {
                        ESObject obj = Convert.toObject(stack[sp - 1]);
                        String var102 = c._code.ids[assemble(code[ip++], code[ip++])];
                        stack[sp - 1] = Value.makeBooleanValue(obj.deleteField(var102));
                        break;
                     }
                     case 23:
                        sp--;
                        ESObject var118 = Convert.toObject(stack[sp - 1]);
                        stack[sp - 1] = Value.makeBooleanValue(var118.deleteElement(stack[sp]));
                        break;
                     case 24:
                        sp--;
                        stack[sp - 1] = Expr.div(stack[sp - 1], stack[sp]);
                        break;
                     case 25:
                        stack[sp] = stack[sp - 1];
                        sp++;
                        break;
                     case 26:
                        stack[sp] = stack[sp - 1];
                        stack[sp - 1] = stack[sp - 2];
                        stack[sp - 2] = stack[sp];
                        sp++;
                        break;
                     case 27:
                        stack[sp] = stack[sp - 1];
                        stack[sp - 1] = stack[sp - 2];
                        stack[sp - 2] = stack[sp - 3];
                        stack[sp - 3] = stack[sp];
                        sp++;
                        break;
                     case 28:
                        stack[sp] = stack[sp - 2];
                        stack[sp + 1] = stack[sp - 1];
                        sp += 2;
                        break;
                     case 29:
                        ESObject var117 = Convert.toObject(stack[sp - 1]);
                        stack[sp - 1] = Value.makeObjectValue(new ObjectEnumerator(var117));
                        break;
                     case 30: {
                        ObjectEnumerator oe = (ObjectEnumerator)Value.checkIfObjectValue(stack[sp - 1]);
                        stack[sp - 1] = Value.makeBooleanValue(oe.hasNextElement());
                        break;
                     }
                     case 31: {
                        ObjectEnumerator oe = (ObjectEnumerator)Value.checkIfObjectValue(stack[sp - 1]);
                        Object o = oe.nextElement();
                        long tmp;
                        if (!(o instanceof String)) {
                           if (!(o instanceof ESObject)) {
                              tmp = Value.makeObjectValue(JavaObject.createInstance(o));
                           } else {
                              tmp = Value.makeObjectValue((ESObject)o);
                           }
                        } else {
                           tmp = Value.makeStringValue((String)o);
                        }

                        stack[sp - 1] = tmp;
                        break;
                     }
                     case 32:
                        c.createArgumentsArray();
                        break;
                     case 33:
                        sp -= 2;
                        ESObject var116 = Convert.toObject(stack[sp]);

                        long var86;
                        try {
                           var86 = var116.getElementAllowExceptions(stack[sp + 1]);
                        } catch (GetFunctionLengthException gle) {
                           var86 = c.getFunctionLength((ESFunction)gle.getFunction());
                        } catch (BuildArgumentsException bae) {
                           var86 = c.buildFunctionArguments((ESFunction)bae.getFunction());
                        }

                        stack[sp++] = var86;
                        break;
                     case 34:
                     case 35:
                        String var101;
                        switch (op) {
                           case 34:
                              var101 = c._code.ids[code[ip++]];
                              break;
                           default:
                              var101 = c._code.ids[assemble(code[ip++], code[ip++])];
                        }

                        long var84;
                        try {
                           var84 = stack[sp - 1];
                           switch (Value.getType(var84)) {
                              case -1:
                                 var84 = global.numberPrototype.getField(var101);
                                 break;
                              case 0:
                              default:
                                 var84 = global.numberPrototype.getField(var101);
                                 break;
                              case 1:
                              case 2:
                                 throw ThrownValue.typeError(Resources.getString(41), "undefined");
                              case 3:
                                 throw ThrownValue.typeError(Resources.getString(41), "null");
                              case 4:
                                 var84 = global.booleanPrototype.getField(var101);
                                 break;
                              case 5:
                                 if (var101 == "length") {
                                    var84 = Value.makeIntegerValue(Value.getStringValue(var84).length());
                                 } else {
                                    var84 = global.stringPrototype.getField(var101);
                                 }
                                 break;
                              case 6:
                                 var84 = Value.getObjectValue(var84).getFieldAllowExceptions(var101);
                           }
                        } catch (GetFunctionLengthException gle) {
                           var84 = c.getFunctionLength((ESFunction)gle.getFunction());
                        } catch (BuildArgumentsException bae) {
                           var84 = c.buildFunctionArguments((ESFunction)bae.getFunction());
                        }

                        stack[sp - 1] = var84;
                        break;
                     case 36:
                        String var100 = c._code.ids[assemble(code[ip++], code[ip++])];

                        long var83;
                        try {
                           var83 = c.getName(c._scope, var100);
                        } catch (GetFunctionLengthException gle) {
                           var83 = c.getFunctionLength((ESFunction)gle.getFunction());
                        } catch (BuildArgumentsException bae) {
                           var83 = c.buildFunctionArguments((ESFunction)bae.getFunction());
                        }

                        stack[sp++] = var83;
                        break;
                     case 41: {
                        String name = c._code.ids[assemble(code[ip++], code[ip++])];

                        long var82;
                        try {
                           var82 = c.getName(c._scope, name);
                        } catch (GetFunctionLengthException gle) {
                           var82 = c.getFunctionLength((ESFunction)gle.getFunction());
                        } catch (BuildArgumentsException bae) {
                           var82 = c.buildFunctionArguments((ESFunction)bae.getFunction());
                        }

                        stack[sp++] = Value.makeObjectValue(c._getNameThisObject);
                        stack[sp++] = var82;
                        break;
                     }
                     case 42:
                        stack[sp++] = Value.makeObjectValue(global);
                     case 37:
                        stack[sp++] = locals[code[ip++] + localOffset];
                        break;
                     case 43:
                        stack[sp++] = Value.makeObjectValue(global);
                     case 38:
                        stack[sp++] = locals[assemble(code[ip++], code[ip++]) + localOffset];
                        break;
                     case 44:
                        stack[sp++] = Value.makeObjectValue(global);
                     case 39:
                        int var109 = globalIndexes[code[ip++]];
                        long var81 = globalValues[var109];
                        if (var81 == Value.PLACEHOLDER_GLOBAL) {
                           String var98 = global.getName(var109);

                           try {
                              var81 = global.requestFieldValue(var98);
                           } catch (GetFunctionLengthException var45) {
                           } catch (BuildArgumentsException var46) {
                           }

                           if (Value.getType(var81) == 2) {
                              throw ThrownValue.referenceError(var98);
                           }
                        }

                        stack[sp++] = var81;
                        break;
                     case 45:
                        stack[sp++] = Value.makeObjectValue(global);
                     case 40:
                        int var108 = globalIndexes[assemble(code[ip++], code[ip++])];
                        long var80 = globalValues[var108];
                        if (var80 == Value.PLACEHOLDER_GLOBAL) {
                           String namex = global.getName(var108);

                           try {
                              var80 = global.requestFieldValue(namex);
                           } catch (GetFunctionLengthException var43) {
                           } catch (BuildArgumentsException var44) {
                           }

                           if (Value.getType(var80) == 2) {
                              throw ThrownValue.referenceError(namex);
                           }
                        }

                        stack[sp++] = var80;
                        break;
                     case 46:
                        stack[sp++] = stack[code[ip++] + c._stackTop];
                        break;
                     case 47:
                        stack[sp++] = stack[assemble(code[ip++], code[ip++]) + c._stackTop];
                        break;
                     case 48:
                        int var68 = assemble(code[ip++], code[ip++]);
                        if ((int)stack[--sp] == 0) {
                           ip = var68;
                        }
                        break;
                     case 49:
                        if ((int)stack[--sp] == 0) {
                           ip += code[ip];
                        } else {
                           ip++;
                        }
                        break;
                     case 50:
                        int var67 = assemble(code[ip++], code[ip++]);
                        if ((int)stack[--sp] != 0) {
                           ip = var67;
                        }
                        break;
                     case 51:
                        if ((int)stack[--sp] != 0) {
                           ip += code[ip];
                        } else {
                           ip++;
                        }
                        break;
                     case 52: {
                        sp -= 2;
                        int offset = assemble(code[ip++], code[ip++]);
                        if (Expr.eq(stack[sp + 0], stack[sp + 1])) {
                           ip = offset;
                        }
                        break;
                     }
                     case 53:
                        sp -= 2;
                        if (Expr.eq(stack[sp + 0], stack[sp + 1])) {
                           ip += code[ip];
                        } else {
                           ip++;
                        }
                        break;
                     case 54:
                     case 56:
                        sp -= 2;
                        int var65 = assemble(code[ip++], code[ip++]);
                        if (Expr.ge(op == 56, stack[sp + 0], stack[sp + 1])) {
                           ip = var65;
                        }
                        break;
                     case 55:
                     case 57:
                        sp -= 2;
                        if (Expr.ge(op == 57, stack[sp + 0], stack[sp + 1])) {
                           ip += code[ip];
                        } else {
                           ip++;
                        }
                        break;
                     case 58:
                     case 60:
                        sp -= 2;
                        int var64 = assemble(code[ip++], code[ip++]);
                        if (Expr.gt(op == 60, stack[sp + 0], stack[sp + 1])) {
                           ip = var64;
                        }
                        break;
                     case 59:
                     case 61:
                        sp -= 2;
                        if (Expr.gt(op == 61, stack[sp + 0], stack[sp + 1])) {
                           ip += code[ip];
                        } else {
                           ip++;
                        }
                        break;
                     case 62:
                     case 64:
                        sp -= 2;
                        int var63 = assemble(code[ip++], code[ip++]);
                        if (Expr.le(op == 64, stack[sp + 0], stack[sp + 1])) {
                           ip = var63;
                        }
                        break;
                     case 63:
                     case 65:
                        sp -= 2;
                        if (Expr.le(op == 65, stack[sp + 0], stack[sp + 1])) {
                           ip += code[ip];
                        } else {
                           ip++;
                        }
                        break;
                     case 66:
                     case 68:
                        sp -= 2;
                        int var62 = assemble(code[ip++], code[ip++]);
                        if (Expr.lt(op == 68, stack[sp + 0], stack[sp + 1])) {
                           ip = var62;
                        }
                        break;
                     case 67:
                     case 69:
                        sp -= 2;
                        if (Expr.lt(op == 69, stack[sp + 0], stack[sp + 1])) {
                           ip += code[ip];
                        } else {
                           ip++;
                        }
                        break;
                     case 70:
                        sp -= 2;
                        int var61 = assemble(code[ip++], code[ip++]);
                        if (!Expr.eq(stack[sp + 0], stack[sp + 1])) {
                           ip = var61;
                        }
                        break;
                     case 71:
                        sp -= 2;
                        if (!Expr.eq(stack[sp + 0], stack[sp + 1])) {
                           ip += code[ip];
                        } else {
                           ip++;
                        }
                        break;
                     case 72:
                        sp -= 2;
                        int var60 = assemble(code[ip++], code[ip++]);
                        if (Expr.stricteq(stack[sp + 0], stack[sp + 1])) {
                           ip = var60;
                        }
                        break;
                     case 73:
                        sp -= 2;
                        if (Expr.stricteq(stack[sp + 0], stack[sp + 1])) {
                           ip += code[ip];
                        } else {
                           ip++;
                        }
                        break;
                     case 74:
                        sp -= 2;
                        int var59 = assemble(code[ip++], code[ip++]);
                        if (!Expr.stricteq(stack[sp + 0], stack[sp + 1])) {
                           ip = var59;
                        }
                        break;
                     case 75:
                        sp -= 2;
                        if (!Expr.stricteq(stack[sp + 0], stack[sp + 1])) {
                           ip += code[ip];
                        } else {
                           ip++;
                        }
                        break;
                     case 76:
                        long objValue = stack[--sp];
                        ESObject var115 = Value.checkIfObjectValue(objValue);
                        if (var115 == null) {
                           throw ThrownValue.typeError(Resources.getString(54), Convert.toString(objValue));
                        }

                        stack[sp - 1] = Value.makeBooleanValue(var115.hasElement(stack[sp - 1]));
                        break;
                     case 77:
                        stack[sp - 1] = Expr.inc(stack[sp - 1], 1);
                        break;
                     case 78: {
                        String name = c._code.ids[assemble(code[ip++], code[ip++])];

                        long var78;
                        try {
                           var78 = c.getName(c._scope, name);
                        } catch (GetFunctionLengthException gle) {
                           var78 = c.getFunctionLength((ESFunction)gle.getFunction());
                        } catch (BuildArgumentsException bae) {
                           var78 = c.buildFunctionArguments((ESFunction)bae.getFunction());
                        }

                        var78 = Expr.inc(var78, code[ip++]);
                        putName(c._scope, name, var78);
                        break;
                     }
                     case 79: {
                        int index = code[ip++] + localOffset;
                        locals[index] = Expr.inc(locals[index], code[ip++]);
                        break;
                     }
                     case 80:
                        int var106 = assemble(code[ip++], code[ip++]) + localOffset;
                        locals[var106] = Expr.inc(locals[var106], code[ip++]);
                        break;
                     case 81:
                        int var105 = globalIndexes[code[ip++]];
                        long tmp = globalValues[var105];
                        if (tmp == Value.PLACEHOLDER_GLOBAL) {
                           String namex = global.getName(var105);

                           try {
                              tmp = global.requestFieldValue(namex);
                           } catch (GetFunctionLengthException var55) {
                           } catch (BuildArgumentsException var56) {
                           }

                           if (Value.getType(tmp) == 2) {
                              throw ThrownValue.referenceError(namex);
                           }

                           tmp = Expr.inc(tmp, code[ip++]);
                           if (!global.notifyFieldChanged(namex, tmp)) {
                              break;
                           }
                        } else {
                           tmp = Expr.inc(tmp, code[ip++]);
                        }

                        globalValues[var105] = tmp;
                        break;
                     case 82:
                        int var104 = globalIndexes[assemble(code[ip++], code[ip++])];
                        long var74 = globalValues[var104];
                        if (var74 == Value.PLACEHOLDER_GLOBAL) {
                           String namex = global.getName(var104);

                           try {
                              var74 = global.requestFieldValue(namex);
                           } catch (GetFunctionLengthException var53) {
                           } catch (BuildArgumentsException var54) {
                           }

                           if (Value.getType(var74) == 2) {
                              throw ThrownValue.referenceError(namex);
                           }

                           var74 = Expr.inc(var74, code[ip++]);
                           if (!global.notifyFieldChanged(namex, var74)) {
                              break;
                           }
                        } else {
                           var74 = Expr.inc(var74, code[ip++]);
                        }

                        globalValues[var104] = var74;
                        break;
                     case 83:
                        long funcValue = stack[--sp];
                        ESFunction func = Value.checkIfFunctionValue(funcValue);
                        if (func == null) {
                           stack[sp - 1] = Value.FALSE;
                        } else {
                           stack[sp - 1] = Value.makeBooleanValue(func.hasInstance(stack[sp - 1]));
                        }
                        break;
                     case 84:
                        ip = assemble(code[ip++], code[ip++]);
                        break;
                     case 85:
                        ip += code[ip];
                        break;
                     case 86: {
                        int offset = assemble(code[ip++], code[ip++], code[ip++], code[ip++]);
                        int local = (char)(offset >> 16);
                        int var58 = (char)offset;
                        stack[local + c._stackTop] = Value.makeReturnAddressValue(ip);
                        ip = var58;
                        break;
                     }
                     case 87:
                        sp--;
                        stack[sp - 1] = Expr.lsh(stack[sp - 1], stack[sp]);
                        break;
                     case 88:
                        sp--;
                        stack[sp - 1] = Expr.mod(stack[sp - 1], stack[sp]);
                        break;
                     case 89:
                        sp--;
                        stack[sp - 1] = Expr.mul(stack[sp - 1], stack[sp]);
                        break;
                     case 90:
                        stack[sp - 1] = Expr.neg(stack[sp - 1]);
                        break;
                     case 91:
                        CompiledScript script = c._code.functionExpressions[assemble(code[ip++], code[ip++])];
                        ESFunction newFunc = new ESFunction(script, c._scope);
                        stack[sp++] = Value.makeObjectValue(newFunc);
                        break;
                     case 92:
                        sp--;
                        break;
                     case 93:
                        c._try = c._try.next;
                        break;
                     case 94:
                        c._scope = c._scope.next;
                        break;
                     case 95:
                        stack[sp++] = Value.FALSE;
                        break;
                     case 96:
                        stack[sp++] = Value.makeDoubleValue(c._code.doubles[assemble(code[ip++], code[ip++])]);
                        break;
                     case 97:
                        stack[sp++] = c._code.integers[code[ip++]];
                        break;
                     case 98:
                        stack[sp++] = assemble(code[ip++], code[ip++], code[ip++], code[ip++]) & 4294967295L;
                        break;
                     case 99:
                        stack[sp++] = code[ip++] & 4294967295L;
                        break;
                     case 100:
                        stack[sp++] = Value.ZERO;
                        break;
                     case 101:
                        stack[sp++] = Value.ONE;
                        break;
                     case 102:
                        stack[sp++] = Value.NULL;
                        break;
                     case 103:
                        stack[sp++] = Value.makeObjectValue(global);
                        break;
                     case 104:
                        stack[sp++] = Value.makeStringValue(c._code.strings[code[ip++]]);
                        break;
                     case 105:
                        stack[sp++] = Value.makeStringValue(c._code.strings[assemble(code[ip++], code[ip++])]);
                        break;
                     case 106:
                        stack[sp++] = Value.makeObjectValue(c._this);
                        break;
                     case 107:
                        stack[sp++] = Value.makeObjectValue(new ESObject());
                        break;
                     case 108:
                        long var73 = stack[sp - 1];
                        ESArray var114;
                        switch (Value.getType(var73)) {
                           case 0:
                              long size = Value.getIntegerValue(var73);
                              if (size < 0) {
                                 throw ThrownValue.badArrayLength(Convert.toString(var73));
                              }

                              var114 = new ESArray(size);
                              break;
                           case 7:
                              long var132 = ESObject.toArrayIndex(var73);
                              if (var132 == -1) {
                                 throw ThrownValue.badArrayLength(Convert.toString(var73));
                              }

                              var114 = new ESArray(var132);
                              break;
                           default:
                              var114 = new ESArray(1);
                              var114.putIndex(0, var73);
                        }

                        stack[sp - 1] = Value.makeObjectValue(var114);
                        break;
                     case 109:
                        stack[sp++] = Value.TRUE;
                        break;
                     case 110:
                        c._try = new TryBlock(c._try, c._scope, assemble(code[ip++], code[ip++]));
                        break;
                     case 111:
                        stack[sp++] = Value.UNDEFINED;
                        break;
                     case 112:
                        ESObject var113 = Convert.toObject(stack[--sp]);
                        c._scope = new ScopeChain(c._scope, var113, var113);
                        break;
                     case 113:
                        sp -= 3;
                        ESObject var112 = Convert.toObject(stack[sp]);
                        var112.putElement(stack[sp + 1], stack[sp + 2]);
                        break;
                     case 114:
                        long var72 = stack[--sp];
                        ESObject var111 = Convert.toObject(stack[--sp]);
                        String var93 = c._code.ids[code[ip++]];
                        var111.putField(var93, var72);
                        break;
                     case 115: {
                        long var71 = stack[--sp];
                        ESObject obj = Convert.toObject(stack[--sp]);
                        String var92 = c._code.ids[assemble(code[ip++], code[ip++])];
                        obj.putField(var92, var71);
                        break;
                     }
                     case 116:
                        String var91 = c._code.ids[assemble(code[ip++], code[ip++])];
                        constName(c._scope, var91);
                        break;
                     case 117: {
                        String name = c._code.ids[assemble(code[ip++], code[ip++])];
                        putName(c._scope, name, stack[--sp]);
                        break;
                     }
                     case 118:
                        locals[code[ip++] + localOffset] = stack[--sp];
                        break;
                     case 119:
                        locals[assemble(code[ip++], code[ip++]) + localOffset] = stack[--sp];
                        break;
                     case 120:
                        int var103 = globalIndexes[code[ip++]];
                        long var70 = stack[--sp];
                        if (globalValues[var103] == Value.PLACEHOLDER_GLOBAL) {
                           String var89 = global.getName(var103);
                           if (!global.notifyFieldChanged(var89, var70)) {
                              break;
                           }
                        }

                        globalValues[var103] = var70;
                        break;
                     case 121: {
                        int index = globalIndexes[assemble(code[ip++], code[ip++])];
                        long var69 = stack[--sp];
                        if (globalValues[index] == Value.PLACEHOLDER_GLOBAL) {
                           String namex = global.getName(index);
                           if (!global.notifyFieldChanged(namex, var69)) {
                              break;
                           }
                        }

                        globalValues[index] = var69;
                        break;
                     }
                     case 122:
                        stack[code[ip++] + c._stackTop] = stack[--sp];
                        break;
                     case 123:
                        stack[assemble(code[ip++], code[ip++]) + c._stackTop] = stack[--sp];
                        break;
                     case 124:
                        long ipValue = stack[assemble(code[ip++], code[ip++]) + c._stackTop];
                        if (Value.getType(ipValue) != 1) {
                           throw ThrownValue.internalError("not a valid return address");
                        }

                        ip = Value.getReturnAddressValue(ipValue);
                        break;
                     case 125:
                        Context caller = c._caller;
                        c._caller = global.contextFreeList;
                        global.contextFreeList = c;
                        long tmp = stack[--sp];
                        if (caller == null) {
                           return tmp;
                        }

                        if (!c._calledAsConstructor || Value.getType(tmp) == 6) {
                           stack[caller._sp - 1] = tmp;
                        }

                        if (debugCallback != null && c._forDebug) {
                           stepInfo.exitCall();
                        }

                        c = caller;
                        global = c.exitNewGlobal(global);
                        continue label610;
                     case 126:
                        Context caller = c._caller;
                        c._caller = global.contextFreeList;
                        global.contextFreeList = c;
                        if (caller == null) {
                           return Value.UNDEFINED;
                        }

                        if (!c._calledAsConstructor) {
                           stack[caller._sp - 1] = Value.UNDEFINED;
                        }

                        if (debugCallback != null && c._forDebug) {
                           stepInfo.exitCall();
                        }

                        c = caller;
                        global = c.exitNewGlobal(global);
                        continue label610;
                     case 127:
                        sp--;
                        stack[sp - 1] = Expr.rsh(stack[sp - 1], stack[sp]);
                  }

                  if (!c._supressNativeExecution) {
                     c._sp = sp;
                     c._ip = ip;
                     c = runNative(c);
                     continue label608;
                  }
               }

               return Value.UNDEFINED;
            } catch (ThrownValue th) {
               th.setContext(c);
               c._sp = sp;
               c._ip = ip;
               Context newContext = findExceptionHandler(global, th, c, stepInfo);
               if (debugCallback != null && c._forDebug && (newContext == null || stepInfo.allExceptions)) {
                  c._thrownValue = th;
                  c._code.getLineAndOffsets(ip, stepInfo);
                  debugCallback.event(c, 3);
                  c._thrownValue = null;
               }

               if (newContext == null) {
                  throw th;
               }

               c = newContext;
               global = c._newGlobalObject;
               break;
            }
         }
      }

      return Value.UNDEFINED;
   }

   @Override
   public DebugScope getScope() {
      return this._scope;
   }

   @Override
   public ThrownValue getThrownValue() {
      return this._thrownValue;
   }

   @Override
   public ESObject getGlobalObject() {
      return GlobalObject.getInstance();
   }

   @Override
   public DebugScript getScript() {
      return this._code.getTopLevelScript();
   }

   @Override
   public int getLine() {
      LineAndOffsets lineAndOffsets = new LineAndOffsets();
      CompiledScript code = this.getCode();
      int rc = 0;
      if (code.getLineAndOffsets(this.getIP() - 1, lineAndOffsets) && lineAndOffsets.line != -1) {
         rc = lineAndOffsets.line;
      }

      return rc;
   }

   @Override
   public DebugContext getCallingContext() {
      return this._caller;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long eval(String script) throws ThrownValue {
      GlobalObject global = GlobalObject.getInstance();

      CompiledScript code;
      try {
         code = new Compiler(script, global.version, global.debugOut, global.compilerFlags + 67108864).compile();
      } catch (CompileError ce) {
         throw ThrownValue.syntaxError(ce.toString());
      }

      DebugCallback debugCallback = global.debugCallback;
      StepInfo stepInfo = global.stepInfo;
      boolean var12 = false /* VF: Semaphore variable */;

      long th;
      try {
         var12 = true;
         global.debugCallback = null;
         global.stepInfo = new StepInfo();
         th = run(this.makeEvalContext(global, code));
         var12 = false;
      } catch (ThrownValue thx) {
         throw ThrownValue.errorInEval(thx);
      } finally {
         if (var12) {
            global.debugCallback = debugCallback;
            global.stepInfo = stepInfo;
         }
      }

      global.debugCallback = debugCallback;
      global.stepInfo = stepInfo;
      return th;
   }

   @Override
   public String getFunctionName() {
      if (this._callee == null) {
         return "";
      }

      try {
         return this._callee.getCode().getId();
      } catch (ThrownValue tv) {
         return "";
      }
   }
}
