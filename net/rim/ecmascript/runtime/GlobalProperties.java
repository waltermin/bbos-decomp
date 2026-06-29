package net.rim.ecmascript.runtime;

import net.rim.ecmascript.compiler.Tokenizer;
import net.rim.ecmascript.util.Resources;

class GlobalProperties {
   private static final GlobalProperties$PackageTable ptAPIUIcomponent = new GlobalProperties$PackageTable("component", null);
   private static final GlobalProperties$PackageTable ptAPIUIcontainer = new GlobalProperties$PackageTable("container", null);
   private static final GlobalProperties$PackageTable ptAPIUItext = new GlobalProperties$PackageTable("text", null);
   private static final GlobalProperties$PackageTable ptLang = new GlobalProperties$PackageTable("lang", null);
   private static final GlobalProperties$PackageTable ptUtil = new GlobalProperties$PackageTable("util", null);
   private static final GlobalProperties$PackageTable ptAPIcollection = new GlobalProperties$PackageTable("collection", null);
   private static final GlobalProperties$PackageTable ptAPIcompress = new GlobalProperties$PackageTable("compress", null);
   private static final GlobalProperties$PackageTable ptAPIcrypto = new GlobalProperties$PackageTable("crypto", null);
   private static final GlobalProperties$PackageTable ptAPIi18n = new GlobalProperties$PackageTable("i18n", null);
   private static final GlobalProperties$PackageTable ptAPIio = new GlobalProperties$PackageTable("io", null);
   private static final GlobalProperties$PackageTable ptAPIitpolicy = new GlobalProperties$PackageTable("itpolicy", null);
   private static final GlobalProperties$PackageTable ptAPIldap = new GlobalProperties$PackageTable("ldap", null);
   private static final GlobalProperties$PackageTable ptAPIlowmemory = new GlobalProperties$PackageTable("lowmemory", null);
   private static final GlobalProperties$PackageTable ptAPImemorycleaner = new GlobalProperties$PackageTable("memorycleaner", null);
   private static final GlobalProperties$PackageTable ptAPImime = new GlobalProperties$PackageTable("mime", null);
   private static final GlobalProperties$PackageTable ptAPInotification = new GlobalProperties$PackageTable("notification", null);
   private static final GlobalProperties$PackageTable ptAPIsmartcard = new GlobalProperties$PackageTable("smartcard", null);
   private static final GlobalProperties$PackageTable ptAPIsynchronization = new GlobalProperties$PackageTable("synchronization", null);
   private static final GlobalProperties$PackageTable ptAPIsystem = new GlobalProperties$PackageTable("system", null);
   private static final GlobalProperties$PackageTable ptAPIutil = new GlobalProperties$PackageTable("util", null);
   private static final GlobalProperties$PackageTable ptIO = new GlobalProperties$PackageTable("io", null);
   private static final GlobalProperties$PackageTable ptCLDC = new GlobalProperties$PackageTable("cldc", new GlobalProperties$PackageTable[]{ptIO});
   private static final GlobalProperties$PackageTable ptAPIui = new GlobalProperties$PackageTable(
      "ui", new GlobalProperties$PackageTable[]{ptAPIUIcomponent, ptAPIUIcontainer, ptAPIUItext}
   );
   private static final GlobalProperties$PackageTable ptAPI = new GlobalProperties$PackageTable(
      "api",
      new GlobalProperties$PackageTable[]{
         ptAPIcollection,
         ptAPIcompress,
         ptAPIcrypto,
         ptAPIi18n,
         ptAPIio,
         ptAPIitpolicy,
         ptAPIldap,
         ptAPIlowmemory,
         ptAPImemorycleaner,
         ptAPImime,
         ptAPInotification,
         ptAPIsmartcard,
         ptAPIsynchronization,
         ptAPIsystem,
         ptAPIui,
         ptAPIutil
      }
   );
   private static final GlobalProperties$PackageTable ptDevice = new GlobalProperties$PackageTable("device", new GlobalProperties$PackageTable[]{ptAPI, ptCLDC});
   private static final GlobalProperties$PackageTable ptRim = new GlobalProperties$PackageTable("rim", new GlobalProperties$PackageTable[]{ptDevice});
   private static final GlobalProperties$PackageTable ptNet = new GlobalProperties$PackageTable("net", new GlobalProperties$PackageTable[]{ptRim});
   private static final GlobalProperties$PackageTable ptJava = new GlobalProperties$PackageTable(
      "java", new GlobalProperties$PackageTable[]{ptLang, ptUtil, ptIO}
   );
   static final String hexDigits = "0123456789ABCDEF";

   private GlobalProperties() {
   }

   private static JavaPackage addPackages(JavaPackage rootPackage, String prefix, GlobalProperties$PackageTable table) {
      StringBuffer fullName = (StringBuffer)(new Object(prefix));
      fullName.append(table.name);
      JavaPackage pakkage = new JavaPackage(fullName.toString());
      if (table.children != null) {
         String childPrefix = fullName.append('.').toString();

         for (int i = 0; i < table.children.length; i++) {
            addPackages(pakkage, childPrefix, table.children[i]);
         }
      }

      try {
         rootPackage.putField(table.name, Value.makeObjectValue(pakkage));
      } catch (ThrownValue var7) {
      }

      return pakkage;
   }

   static void addHex(StringBuffer b, int h) {
      b.append('%');
      b.append("0123456789ABCDEF".charAt(h >> 4 & 15));
      b.append("0123456789ABCDEF".charAt(h & 15));
   }

   static String uriEncode(String s, boolean component) throws ThrownValue {
      int len = s.length();
      StringBuffer b = (StringBuffer)(new Object());

      for (int k = 0; k < len; k++) {
         char ch = s.charAt(k);
         switch (ch) {
            case '!':
            case '\'':
            case '(':
            case ')':
            case '*':
            case '-':
            case '.':
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
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
            case '~':
            default:
               b.append(ch);
               break;
            case '#':
            case '$':
            case '&':
            case '+':
            case ',':
            case '/':
            case ':':
            case ';':
            case '=':
            case '?':
            case '@':
               if (!component) {
                  b.append(ch);
                  break;
               }
            case ' ':
            case '"':
            case '%':
            case '<':
            case '>':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '`':
            case '{':
            case '|':
            case '}':
               if (ch >= '\udc00' && ch <= '\udfff') {
                  throw ThrownValue.uriError(Resources.getString(36));
               }

               if (ch <= 127) {
                  addHex(b, ch);
               } else if (ch <= 2047) {
                  addHex(b, 192 + (ch >> 6));
                  addHex(b, 128 + (ch & '?'));
               } else if (ch <= '\ud7ff' || ch > '\udfff') {
                  addHex(b, 224 + (ch >> '\f' & 15));
                  addHex(b, 128 + (ch >> 6 & 63));
                  addHex(b, 128 + (ch & '?'));
               } else if (ch > '\udbff') {
                  if (ch <= '\udfff') {
                     throw ThrownValue.uriError(Resources.getString(36));
                  }

                  addHex(b, 224 + (ch >> '\f' & 15));
                  addHex(b, 128 + (ch >> 6 & 63));
                  addHex(b, 128 + (ch & '?'));
               } else {
                  if (++k == len) {
                     throw ThrownValue.uriError(Resources.getString(36));
                  }

                  char d = s.charAt(k);
                  if (d < '\udc00' || d > '\udfff') {
                     throw ThrownValue.uriError(Resources.getString(36));
                  }

                  int u = ch >> 6 & 15;
                  addHex(b, 240 + (++u >> 2 & 7));
                  addHex(b, 128 + ((u & 3) << 4) + (ch >> 2 & 15));
                  addHex(b, 128 + ((ch & 3) << 4) + (d >> 6 & 15));
                  addHex(b, 128 + (d & '?'));
               }
         }
      }

      return b.toString();
   }

   static int getEscape(String s, int k, boolean mask) throws ThrownValue {
      try {
         int dig1 = Tokenizer.hexValue(s.charAt(k + 1));
         int dig2 = Tokenizer.hexValue(s.charAt(k + 2));
         if (dig1 != -1 && dig2 != -1) {
            int hex = (dig1 << 4) + dig2;
            if (!mask) {
               return hex;
            }

            if ((hex & 192) == 128) {
               return hex & 63;
            }
         }
      } finally {
         throw ThrownValue.uriError(Resources.getString(36));
      }

      throw ThrownValue.uriError(Resources.getString(36));
   }

   static String uriDecode(String s, boolean component) throws ThrownValue {
      int len = s.length();
      StringBuffer b = (StringBuffer)(new Object());
      int k = 0;

      while (k < len) {
         int start = k;
         char ch = s.charAt(k);
         if (ch != '%') {
            b.append(ch);
            k++;
         } else {
            int hex1 = getEscape(s, k, false);
            k += 3;
            if ((hex1 & 128) == 0) {
               ch = (char)hex1;
            } else if ((hex1 & 224) == 192) {
               hex1 &= 31;
               int hex2 = getEscape(s, k, true);
               k += 3;
               ch = (char)((hex1 << 6) + hex2);
            } else {
               if ((hex1 & 240) != 224) {
                  if ((hex1 & 248) != 240) {
                     throw ThrownValue.uriError(Resources.getString(36));
                  }

                  hex1 &= 7;
                  int hex2 = getEscape(s, k, true);
                  k += 3;
                  int hex3 = getEscape(s, k, true);
                  k += 3;
                  int z = getEscape(s, k, true);
                  k += 3;
                  int v = (hex1 << 2) + (hex2 >> 4) - 1;
                  int w = hex2 & 15;
                  int x = hex3 >> 4;
                  int y = hex3 & 15;
                  b.append((char)(55296 + (v << 6) + (w << 2) + x));
                  b.append((char)(56320 + (y << 6) + z));
                  continue;
               }

               hex1 &= 15;
               int hex2 = getEscape(s, k, true);
               k += 3;
               int hex3 = getEscape(s, k, true);
               k += 3;
               ch = (char)((hex1 << 12) + (hex2 << 6) + hex3);
            }

            if (!component) {
               switch (ch) {
                  case '#':
                  case '$':
                  case '&':
                  case '+':
                  case ',':
                  case '/':
                  case ':':
                  case ';':
                  case '=':
                  case '?':
                  case '@':
                     b.append(s.substring(start, k));
                     continue;
               }
            }

            b.append(ch);
         }
      }

      return b.toString();
   }

   static void populate(ESObject o, boolean noLiveConnect) {
      GlobalObject global = GlobalObject.getInstance();
      o.addHostFunction(global.objectConstructor);
      o.addHostFunction(global.stringConstructor);
      o.addHostFunction(global.booleanConstructor);
      o.addHostFunction(global.errorConstructor);
      o.addHostFunction(global.evalErrorConstructor);
      o.addHostFunction(global.rangeErrorConstructor);
      o.addHostFunction(global.referenceErrorConstructor);
      o.addHostFunction(global.syntaxErrorConstructor);
      o.addHostFunction(global.typeErrorConstructor);
      o.addHostFunction(global.URIErrorConstructor);
      o.addHostFunction(global.arrayConstructor);
      o.addHostFunction(global.dateConstructor);
      o.addHostFunction(global.functionConstructor);
      o.addHostFunction(global.namespaceConstructor);
      o.addHostFunction(global.numberConstructor);
      o.addHostFunction(global.regExpConstructor);
      global.objectPrototype.populate();
      global.stringPrototype.populate();
      global.booleanPrototype.populate();
      global.errorPrototype.populate();
      global.evalErrorPrototype.populate();
      global.rangeErrorPrototype.populate();
      global.referenceErrorPrototype.populate();
      global.syntaxErrorPrototype.populate();
      global.typeErrorPrototype.populate();
      global.URIErrorPrototype.populate();
      global.arrayPrototype.populate();
      global.datePrototype.populate();
      global.functionPrototype.populate();
      global.namespacePrototype.populate();
      global.numberPrototype.populate();
      global.regExpPrototype.populate();
      o.addHostFunction(new GlobalProperties$1("global", "print", 1));
      o.addHostFunction(new GlobalProperties$2("global", "eval", 1));
      o.addHostFunction(new GlobalProperties$3("global", "version", 1));
      o.addHostFunction(new GlobalProperties$4("global", "quit", 0));
      o.addHostFunction(new GlobalProperties$5("global", "unescape", 1));
      o.addHostFunction(new GlobalProperties$6("global", "escape", 1));
      o.addHostFunction(new GlobalProperties$7("global", "isNaN", 1));
      o.addHostFunction(new GlobalProperties$8("global", "isFinite", 1));
      o.addHostFunction(new GlobalProperties$9("global", "parseFloat", 1));
      o.addHostFunction(new GlobalProperties$10("global", "parseInt", 2));
      o.addHostFunction(new GlobalProperties$11("global", "importPackage", 1));
      o.addHostFunction(new GlobalProperties$12("global", "importClass", 1));
      o.addHostFunction(new GlobalProperties$13("global", "decodeURI", 1));
      o.addHostFunction(new GlobalProperties$14("global", "decodeURIComponent", 1));
      o.addHostFunction(new GlobalProperties$15("global", "encodeURI", 1));
      o.addHostFunction(new GlobalProperties$16("global", "encodeURIComponent", 1));
      o.addHostFunction(new GlobalProperties$17("global", "JavaArray", 2));
      o.addHostFunction(new GlobalProperties$18("global", "JavaBooleanArray", 1));
      o.addHostFunction(new GlobalProperties$19("global", "JavaByteArray", 1));
      o.addHostFunction(new GlobalProperties$20("global", "JavaCharArray", 1));
      o.addHostFunction(new GlobalProperties$21("global", "JavaShortArray", 1));
      o.addHostFunction(new GlobalProperties$22("global", "JavaIntArray", 1));
      o.addHostFunction(new GlobalProperties$23("global", "JavaFloatArray", 1));
      o.addHostFunction(new GlobalProperties$24("global", "JavaLongArray", 1));
      o.addHostFunction(new GlobalProperties$25("global", "JavaDoubleArray", 1));
      o.addField("undefined", 6, Value.UNDEFINED);
      o.addField("Infinity", 6, Value.POSITIVE_INFINITY);
      o.addField("NaN", 6, Value.NaN);
      o.addField("Math", 6, Value.makeObjectValue(global.math));
      if (!noLiveConnect) {
         JavaPackage packages = new JavaPackage(null);
         JavaPackage packageJava = addPackages(packages, "", ptJava);
         JavaPackage packageNet = addPackages(packages, "", ptNet);
         o.addField(ptJava.name, 6, Value.makeObjectValue(packageJava));
         o.addField(ptNet.name, 6, Value.makeObjectValue(packageNet));
         o.addField("Packages", 6, Value.makeObjectValue(packages));
      }
   }
}
