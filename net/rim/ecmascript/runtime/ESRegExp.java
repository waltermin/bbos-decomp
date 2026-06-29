package net.rim.ecmascript.runtime;

import net.rim.ecmascript.regexp.RegExp;
import net.rim.ecmascript.regexp.RegExp$MatchResult;

public class ESRegExp extends HostFunction {
   private RegExp _expr;
   private boolean _global;
   private boolean _ignoreCase;
   private boolean _multiLine;
   private String _pattern;

   public ESRegExp() {
      this("");
   }

   public ESRegExp(String pattern) {
      this(pattern, false, false, false);
   }

   public ESRegExp(String pattern, boolean global, boolean ignoreCase, boolean multiLine) {
      super("RegExp", "exec", 1);
      this.setObjectClass("RegExp");
      this.setPrototype(GlobalObject.getInstance().regExpPrototype);
      this.compile(pattern, global, ignoreCase, multiLine);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void compile(String pattern, boolean global, boolean ignoreCase, boolean multiLine) throws ThrownValue {
      this.replaceField("source", 7, Value.makeStringValue(pattern));
      this.replaceField("global", 7, Value.makeBooleanValue(global));
      this.replaceField("ignoreCase", 7, Value.makeBooleanValue(ignoreCase));
      this.replaceField("multiline", 7, Value.makeBooleanValue(multiLine));
      this.replaceField("lastIndex", 6, Value.ZERO);
      this._global = global;
      this._ignoreCase = ignoreCase;
      this._multiLine = multiLine;
      this._pattern = pattern;

      try {
         this._expr = new RegExp(pattern, ignoreCase, multiLine);
      } catch (Throwable var7) {
         throw ThrownValue.syntaxError(se.getMessage());
      }
   }

   void setLastIndex(long lastIndex) {
      this.putField("lastIndex", lastIndex);
   }

   long getLastIndex() {
      return this.getField("lastIndex");
   }

   @Override
   public long run() {
      GlobalObject global = GlobalObject.getInstance();
      return global.callFunction(global.regExpExec, this, super._parmArray, super._firstParm, super._numParms);
   }

   String getPattern() {
      return this._pattern;
   }

   boolean getGlobal() {
      return this._global;
   }

   boolean getIgnoreCase() {
      return this._ignoreCase;
   }

   boolean getMultiLine() {
      return this._multiLine;
   }

   public RegExp$MatchResult match(String str, int startIndex) {
      return this.match(str, startIndex, true);
   }

   public RegExp$MatchResult matchNoAdvance(String str, int startIndex) {
      return this.match(str, startIndex, false);
   }

   public RegExp$MatchResult match(String str, int startIndex, boolean advance) {
      GlobalObject global = GlobalObject.getInstance();
      boolean oldMultiLine = false;
      if (global.regExpMultiline) {
         oldMultiLine = this._expr.setMultiLine(true);
      }

      RegExp$MatchResult m = this._expr.match(str, startIndex, advance);
      if (global.regExpMultiline) {
         this._expr.setMultiLine(oldMultiLine);
      }

      if (m != null) {
         global.regExpLastMatch = m;
      }

      return m;
   }
}
