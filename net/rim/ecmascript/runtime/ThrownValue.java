package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;

public class ThrownValue extends Exception {
   private long[] _value = Misc.newMixedArray(1);
   private ThrownValue _errorInEval;
   private Context _context;

   public ThrownValue(long value) {
      this._value[0] = value;
   }

   void setContext(Context c) {
      if (this._context == null) {
         this._context = c;
      }
   }

   public long getValue() {
      return this._value[0];
   }

   public boolean isFromEval() {
      return this._errorInEval != null;
   }

   public boolean isErrorObject(String clazz) {
      ESObject obj = Value.checkIfObjectValue(this._value[0]);
      return obj == null ? false : clazz.equals(obj.getObjectClass());
   }

   static ThrownValue errorInEval(ThrownValue th) {
      ThrownValue newth = new ThrownValue(th._value[0]);
      newth._errorInEval = th;
      return newth;
   }

   static ThrownValue evalError(String message) {
      return new ThrownValue(Value.makeObjectValue(new ESEvalError(message)));
   }

   public static ThrownValue uriError(String message) {
      return new ThrownValue(Value.makeObjectValue(new ESURIError(message)));
   }

   public static ThrownValue typeError(String message) {
      return new ThrownValue(Value.makeObjectValue(new ESTypeError(message)));
   }

   static ThrownValue typeError(String message, String parm) {
      return typeError(Misc.replace(message, parm));
   }

   static ThrownValue typeError(String message, String parm1, String parm2) {
      return typeError(Misc.replace(message, parm1, parm2));
   }

   static ThrownValue syntaxError(String message) {
      return new ThrownValue(Value.makeObjectValue(new ESSyntaxError(message)));
   }

   static ThrownValue syntaxError(String message, String parm) {
      return syntaxError(Misc.replace(message, parm));
   }

   static ThrownValue syntaxError(String message, String parm1, String parm2) {
      return syntaxError(Misc.replace(message, parm1, parm2));
   }

   public static ThrownValue referenceError(String name) {
      return new ThrownValue(Value.makeObjectValue(new ESReferenceError(Misc.replace(Resources.getString(56), name))));
   }

   static ThrownValue referenceError(String message, String parm1) {
      return new ThrownValue(Value.makeObjectValue(new ESReferenceError(Misc.replace(message, parm1))));
   }

   static ThrownValue referenceError(String message, String parm1, String parm2) {
      return new ThrownValue(Value.makeObjectValue(new ESReferenceError(Misc.replace(message, parm1, parm2))));
   }

   public static ThrownValue rangeError(String name) {
      return new ThrownValue(Value.makeObjectValue(new ESRangeError(Misc.replace(Resources.getString(56), name))));
   }

   static ThrownValue rangeError(String message, String parm1) {
      return new ThrownValue(Value.makeObjectValue(new ESRangeError(Misc.replace(message, parm1))));
   }

   static ThrownValue rangeError(String message, String parm1, String parm2) {
      return new ThrownValue(Value.makeObjectValue(new ESRangeError(Misc.replace(message, parm1, parm2))));
   }

   static ThrownValue badArrayLength(String length) {
      return rangeError(Resources.getString(48), length);
   }

   static ThrownValue internalError(String message) {
      return new ThrownValue(Value.makeObjectValue(new ESInternalError(message)));
   }

   @Override
   public String toString() {
      String str;
      try {
         str = Convert.toString(this._value[0]);
      } catch (ThrownValue th) {
         str = Resources.getString(57);
      }

      return str + "\n" + this.traceBack();
   }

   private static String fourDigits(int line) {
      String str = Integer.toString(line);

      for (int i = str.length(); i < 4; i++) {
         str = " " + str;
      }

      return str;
   }

   public String traceBack() {
      String str = "";
      if (this._errorInEval != null) {
         str = this._errorInEval.traceBack();
      }

      String msg = Resources.getString(45);
      int maxDepth = 500;
      LineAndOffsets lineAndOffsets = new LineAndOffsets();

      for (Context c = this._context; c != null; c = c.getCaller()) {
         if (--maxDepth <= 0) {
            return str;
         }

         CompiledScript code = c.getCode();
         if (code.getLineAndOffsets(c.getIP() - 1, lineAndOffsets)) {
            str = str + Misc.replace(msg, fourDigits(lineAndOffsets.line), code.getSourceCodeFromOffset(lineAndOffsets.offset)) + "\n";
         }

         msg = Resources.getString(37);
      }

      return str;
   }
}
