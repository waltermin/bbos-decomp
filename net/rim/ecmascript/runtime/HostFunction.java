package net.rim.ecmascript.runtime;

public class HostFunction extends ESFunction {
   ESObject _thisObj;
   boolean _isConstructor;
   boolean _calledAsConstructor;
   long[] _parmArray;
   int _firstParm;
   int _numParms;
   String _name;
   Context _context;
   private int _expectedNumParms;
   private String _class;
   boolean _okToRecurse;
   static final byte Properties = 2;

   Object startRecurse() {
      HostFunction$State s = new HostFunction$State();
      s._thisObj = this._thisObj;
      s._isConstructor = this._isConstructor;
      s._calledAsConstructor = this._calledAsConstructor;
      s._parmArray = this._parmArray;
      s._firstParm = this._firstParm;
      s._numParms = this._numParms;
      s._context = this._context;
      s._expectedNumParms = this._expectedNumParms;
      s._okToRecurse = this._okToRecurse;
      this._okToRecurse = true;
      return s;
   }

   void endRecurse(Object o) {
      HostFunction$State s = (HostFunction$State)o;
      this._thisObj = s._thisObj;
      this._isConstructor = s._isConstructor;
      this._calledAsConstructor = s._calledAsConstructor;
      this._parmArray = s._parmArray;
      this._firstParm = s._firstParm;
      this._numParms = s._numParms;
      this._context = s._context;
      this._expectedNumParms = s._expectedNumParms;
      this._okToRecurse = s._okToRecurse;
   }

   public HostFunction(String clazz, String name) {
      this(clazz, name, 0);
   }

   public HostFunction(String clazz, String name, int expectedNumParms) {
      this(clazz, name, expectedNumParms, false);
   }

   HostFunction(int type, String clazz, String name, int expectedNumParms) {
      this(clazz, name, expectedNumParms, false, false, type);
   }

   HostFunction(String clazz, String name, int expectedNumParms, boolean isConstructor) {
      this(clazz, name, expectedNumParms, isConstructor, false);
   }

   HostFunction(String clazz, String name, int expectedNumParms, boolean isConstructor, boolean nullPrototypeOK) {
      this(clazz, name, expectedNumParms, isConstructor, nullPrototypeOK, 2);
   }

   HostFunction(String clazz, String name, int expectedNumParms, boolean isConstructor, boolean nullPrototypeOK, int type) {
      super(null, null, nullPrototypeOK);
      this.setFunctionType(type);
      this._isConstructor = isConstructor;
      this._expectedNumParms = expectedNumParms;
      this._class = clazz;
      this._name = name;
   }

   @Override
   public int notifyFieldDeleted(String name) {
      return name != "length" && name != "arity" && name != "name" ? 2 : 1;
   }

   @Override
   public long requestFieldValue(String name) {
      if (name == "length" || name == "arity") {
         return Value.makeIntegerValue(this._expectedNumParms);
      } else {
         return name == "name" ? Value.makeStringValue(this._name) : super.requestFieldValue(name);
      }
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      return name != "length" && name != "arity" && name != "name" ? super.notifyFieldChanged(name, value) : false;
   }

   public long run() {
      throw null;
   }

   public int getNumParms() {
      return this._numParms;
   }

   public ESObject getThis() {
      return this._thisObj;
   }

   public boolean getIsConstructor() {
      return this._isConstructor;
   }

   public boolean calledAsConstructor() {
      return this._calledAsConstructor;
   }

   public int getVersion() {
      return this.getGlobalInstance().version;
   }

   public GlobalObject getGlobalInstance() {
      return GlobalObject.getInstance();
   }

   public long getParm(int i) {
      return this.getParm(i, Value.UNDEFINED);
   }

   public long getParm(int i, long defaultValue) {
      return i >= this._numParms ? defaultValue : this._parmArray[i + this._firstParm];
   }

   public String getName() {
      return this._name;
   }

   public Context getContext() {
      return this._context;
   }

   @Override
   public String getSource() {
      return ((StringBuffer)(new Object("function ")))
         .append(this._name)
         .append("() { [native code for ")
         .append(this._class)
         .append(".")
         .append(this._name)
         .append(", arity=")
         .append(this._expectedNumParms)
         .append("] }\n")
         .toString();
   }
}
