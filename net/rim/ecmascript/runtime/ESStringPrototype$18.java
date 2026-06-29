package net.rim.ecmascript.runtime;

import java.util.Vector;
import net.rim.device.api.util.StringUtilities;
import net.rim.ecmascript.regexp.RegExp$MatchResult;
import net.rim.ecmascript.util.Misc;

class ESStringPrototype$18 extends HostFunction {
   private final ESStringPrototype this$0;

   ESStringPrototype$18(ESStringPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      Object savedState = this.startRecurse();
      boolean var20 = false /* VF: Semaphore variable */;

      long var38;
      try {
         var20 = true;
         String s = ESStringPrototype.toString(this.getThis());
         Vector matches = new Vector();
         long searchValue = this.getParm(0);
         ESObject obj = Value.checkIfObjectValue(searchValue);
         int capLength = 0;
         if (obj != null && obj instanceof ESRegExp) {
            ESRegExp regExp = (ESRegExp)obj;
            if (regExp.getGlobal()) {
               long lastIndex = Value.ZERO;
               int i = 0;

               while (true) {
                  regExp.setLastIndex(lastIndex);
                  RegExp$MatchResult m = ESRegExpPrototype.doMatch(regExp, s);
                  if (m == null) {
                     break;
                  }

                  capLength = m.captures.length;
                  matches.addElement(m);
                  long newLastIndex = regExp.getLastIndex();
                  if (Expr.eq(lastIndex, newLastIndex)) {
                     lastIndex = Expr.inc(lastIndex, 1);
                  } else {
                     lastIndex = newLastIndex;
                  }

                  i++;
               }
            } else {
               RegExp$MatchResult m = ESRegExpPrototype.doMatch(regExp, s);
               if (m != null) {
                  matches.addElement(m);
                  capLength = m.captures.length;
               }
            }
         } else {
            String searchStr = Convert.toString(searchValue);
            int searchLen = searchStr.length();
            int startIndex = s.indexOf(searchStr);
            if (startIndex != -1) {
               RegExp$MatchResult m = new RegExp$MatchResult(s, startIndex, startIndex + searchLen, new String[]{searchStr});
               matches.addElement(m);
               capLength = m.captures.length;
            }
         }

         long replaceValue = this.getParm(1);
         Vector replacements = new Vector();
         ESFunction replaceFn = Value.checkIfFunctionValue(replaceValue);
         if (replaceFn == null) {
            String replaceString = Convert.toString(replaceValue);

            for (int i = 0; i < matches.size(); i++) {
               replacements.addElement(ESStringPrototype.replaceDollar(replaceString, (RegExp$MatchResult)matches.elementAt(i)));
            }
         } else {
            GlobalObject global = this.getGlobalInstance();
            long[] args = Misc.newMixedArray(capLength + 2);

            for (int i = 0; i < matches.size(); i++) {
               RegExp$MatchResult m = (RegExp$MatchResult)matches.elementAt(i);

               int j;
               for (j = 0; j < m.captures.length; j++) {
                  String str = m.captures[j];
                  if (str == null) {
                     args[j] = Value.UNDEFINED;
                  } else {
                     args[j] = Value.makeStringValue(str);
                  }
               }

               args[j++] = Value.makeIntegerValue(m.startIndex);
               args[j++] = Value.makeStringValue(s);
               replacements.addElement(Convert.toString(global.callFunction(replaceFn, args)));
            }

            Misc.freeMixedArray(args);
         }

         StringBuffer sb = new StringBuffer();
         int lastIndex = 0;

         for (int i = 0; i < matches.size(); i++) {
            RegExp$MatchResult m = (RegExp$MatchResult)matches.elementAt(i);
            sb = StringUtilities.append(sb, s, lastIndex, m.startIndex - lastIndex);
            sb.append((String)replacements.elementAt(i));
            lastIndex = m.endIndex;
         }

         sb = StringUtilities.append(sb, s, lastIndex, s.length() - lastIndex);
         s = sb.toString();
         var38 = Value.makeStringValue(s);
         var20 = false;
      } finally {
         if (var20) {
            this.endRecurse(savedState);
         }
      }

      this.endRecurse(savedState);
      return var38;
   }
}
