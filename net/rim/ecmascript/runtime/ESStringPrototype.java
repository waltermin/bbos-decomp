package net.rim.ecmascript.runtime;

import net.rim.ecmascript.regexp.RegExp$MatchResult;

class ESStringPrototype extends ESString {
   ESStringPrototype() {
      super("", true);
      this.setPrototype(GlobalObject.getInstance().objectPrototype);
      this.setGrowthIncrement(25);
   }

   private static String toString(ESObject thisObj) {
      return !(thisObj instanceof ESString) ? Convert.toString(thisObj.defaultStringValue()) : ((ESString)thisObj).getValue();
   }

   private static String replaceDollar(String s, RegExp$MatchResult m) {
      StringBuffer b = new StringBuffer();
      int len = s.length();
      int i = 0;

      while (i < len) {
         char ch = s.charAt(i++);
         if (ch != '$') {
            b.append(ch);
         } else {
            if (i >= len) {
               b.append(ch);
               break;
            }

            ch = s.charAt(i++);
            switch (ch) {
               case '$':
                  b.append(ch);
                  break;
               case '&':
                  b.append(m.captures[0]);
                  break;
               case '\'':
                  b.append(m.input.substring(m.endIndex));
                  break;
               case '0':
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
                  int index = ch - '0';
                  boolean two = false;
                  if (i < len && m.captures.length > 9) {
                     char ch2 = s.charAt(i);
                     switch (ch2) {
                        case '/':
                           break;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                        default:
                           two = true;
                           index = index * 10 + ch2 - 48;
                           i++;
                     }
                  }

                  if (index == 0) {
                     b.append(two ? "$00" : "$0");
                  } else if (index >= m.captures.length) {
                     if (index <= 9) {
                        b.append('$');
                        if (two) {
                           b.append("0");
                        }

                        b.append((char)(index + 48));
                     } else {
                        b.append('$');
                        b.append(Integer.toString(index));
                     }
                  } else {
                     b.append(m.captures[index]);
                  }
                  break;
               case '`':
                  b.append(m.input.substring(0, m.startIndex));
                  break;
               default:
                  b.append(ch);
            }
         }
      }

      return b.toString();
   }

   static RegExp$MatchResult splitMatch(ESRegExp regExp, String regExpString, String s, int q) {
      if (regExp != null) {
         return regExp.matchNoAdvance(s, q);
      }

      int startIndex = s.indexOf(regExpString, q);
      return startIndex != q ? null : new RegExp$MatchResult(s, startIndex, startIndex + regExpString.length(), new String[1]);
   }

   void populate() {
      this.addField("constructor", 2, Value.makeObjectValue(GlobalObject.getInstance().stringConstructor));
      this.addHostFunction(new ESStringPrototype$1(this, "String", "toString", 0));
      this.addHostFunction(new ESStringPrototype$2(this, "String", "toLowerCase", 0));
      this.addHostFunction(new ESStringPrototype$3(this, "String", "toLocaleLowerCase", 0));
      this.addHostFunction(new ESStringPrototype$4(this, "String", "toUpperCase", 0));
      this.addHostFunction(new ESStringPrototype$5(this, "String", "toLocaleUpperCase", 0));
      this.addHostFunction(new ESStringPrototype$6(this, "String", "valueOf", 0));
      this.addHostFunction(new ESStringPrototype$7(this, 7, "String", "charAt", 1));
      this.addHostFunction(new ESStringPrototype$8(this, 8, "String", "charCodeAt", 1));
      this.addHostFunction(new ESStringPrototype$9(this, "String", "concat", 1));
      this.addHostFunction(new ESStringPrototype$10(this, 3, "String", "indexOf", 1));
      this.addHostFunction(new ESStringPrototype$11(this, 6, "String", "lastIndexOf", 1));
      this.addHostFunction(new ESStringPrototype$12(this, 4, "String", "substring", 2));
      this.addHostFunction(new ESStringPrototype$13(this, 5, "String", "substr", 2));
      this.addHostFunction(new ESStringPrototype$14(this, 9, "String", "slice", 2));
      this.addHostFunction(new ESStringPrototype$15(this, "String", "split", 2));
      this.addHostFunction(new ESStringPrototype$16(this, "String", "search", 1));
      this.addHostFunction(new ESStringPrototype$17(this, "String", "match", 1));
      this.addHostFunction(new ESStringPrototype$18(this, "String", "replace", 2));
      this.addHostFunction(new ESStringPrototype$19(this, "String", "localeCompare", 1));
   }
}
