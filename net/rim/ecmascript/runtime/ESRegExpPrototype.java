package net.rim.ecmascript.runtime;

import net.rim.ecmascript.regexp.RegExp$MatchResult;
import net.rim.ecmascript.util.Resources;

class ESRegExpPrototype extends ESRegExp {
   ESRegExpPrototype() {
      this.setPrototype(GlobalObject.getInstance().objectPrototype);
      this.setObjectClass("Object");
   }

   static RegExp$MatchResult doMatch(ESObject regExp, String s) {
      return doMatch("", regExp, s);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static RegExp$MatchResult doMatch(String name, ESObject regExp, String s) {
      boolean var8 = false /* VF: Semaphore variable */;

      ESRegExp thiz;
      try {
         var8 = true;
         thiz = (ESRegExp)regExp;
         var8 = false;
      } finally {
         if (var8) {
            throw ThrownValue.typeError(Resources.getString(50), name);
         }
      }

      int length = s.length();
      int index = Convert.toInteger(thiz.getField("lastIndex"));
      if (!thiz.getGlobal()) {
         index = 0;
      }

      if (index >= 0 && index <= length) {
         RegExp$MatchResult m = thiz.match(s, index);
         if (m == null) {
            thiz.putField("lastIndex", Value.ZERO);
            return null;
         }

         if (thiz.getGlobal()) {
            thiz.putField("lastIndex", Value.makeIntegerValue(m.endIndex));
         }

         return m;
      } else {
         thiz.putField("lastIndex", Value.ZERO);
         return null;
      }
   }

   static long makeMatchArray(RegExp$MatchResult m, String s) {
      if (m == null) {
         return Value.NULL;
      }

      ESArray a = new ESArray(m.captures.length);
      a.putField("index", Value.makeIntegerValue(m.startIndex));
      a.putField("input", Value.makeStringValue(s));

      for (int i = 0; i < m.captures.length; i++) {
         String cap = m.captures[i];
         a.putIndex(i, cap == null ? Value.UNDEFINED : Value.makeStringValue(cap));
      }

      return Value.makeObjectValue(a);
   }

   void populate() {
      GlobalObject global = GlobalObject.getInstance();
      this.addField("constructor", 2, Value.makeObjectValue(global.regExpConstructor));
      this.addHostFunction(global.regExpExec = new ESRegExpPrototype$1(this, "RegExp", "exec", 1));
      this.addHostFunction(new ESRegExpPrototype$2(this, "RegExp", "test", 1));
      this.addHostFunction(new ESRegExpPrototype$3(this, "RegExp", "toString"));
      this.addHostFunction(new ESRegExpPrototype$4(this, "RegExp", "compile"));
   }
}
