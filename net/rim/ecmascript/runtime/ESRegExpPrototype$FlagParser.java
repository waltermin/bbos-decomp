package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESRegExpPrototype$FlagParser {
   boolean global;
   boolean multiLine;
   boolean ignoreCase;

   void set(boolean g, boolean m, boolean i) {
      this.global = g;
      this.multiLine = m;
      this.ignoreCase = i;
   }

   void parse(String flags) {
      for (int i = flags.length() - 1; i >= 0; i--) {
         switch (flags.charAt(i)) {
            case 'g':
               if (this.global) {
                  throw ThrownValue.syntaxError(Resources.getString(52), flags);
               }

               this.global = true;
               break;
            case 'i':
               if (this.ignoreCase) {
                  throw ThrownValue.syntaxError(Resources.getString(52), flags);
               }

               this.ignoreCase = true;
               break;
            case 'm':
               if (!this.multiLine) {
                  this.multiLine = true;
                  break;
               }

               throw ThrownValue.syntaxError(Resources.getString(52), flags);
            default:
               throw ThrownValue.syntaxError(Resources.getString(52), flags);
         }
      }
   }
}
