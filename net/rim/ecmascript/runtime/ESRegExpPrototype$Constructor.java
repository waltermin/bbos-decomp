package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESRegExpPrototype$Constructor extends Constructor {
   private static final int LASTMATCH = 0;
   private static final int DOLLAR_1 = 1;
   private static final int DOLLAR_2 = 2;
   private static final int DOLLAR_3 = 3;
   private static final int DOLLAR_4 = 4;
   private static final int DOLLAR_5 = 5;
   private static final int DOLLAR_6 = 6;
   private static final int DOLLAR_7 = 7;
   private static final int DOLLAR_8 = 8;
   private static final int DOLLAR_9 = 9;
   private static final int INPUT = 10;
   private static final int MULTILINE = 11;
   private static final int LASTPAREN = 12;
   private static final int LEFTCONTEXT = 13;
   private static final int RIGHTCONTEXT = 14;
   private static final String[] staticProperties = new String[]{
      "$1",
      "$2",
      "$3",
      "$4",
      "$5",
      "$6",
      "$7",
      "$8",
      "$9",
      "$_",
      "input",
      "$*",
      "multiline",
      "$&",
      "lastMatch",
      "$+",
      "lastParen",
      "$`",
      "leftContext",
      "$'",
      "rightContext"
   };
   private static final int[] staticIndexes = new int[]{
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      10,
      11,
      11,
      0,
      0,
      12,
      12,
      13,
      13,
      14,
      14,
      51,
      1064304896,
      -977993472,
      1634076160,
      1316252789,
      1971719133,
      1711669349,
      1953264993,
      6649222,
      1969317382,
      -2034207636,
      100689269,
      1164247660,
      9059180,
      1694657542,
      1812332730,
      12543234,
      134273799,
      -1339357184,
      1701869940,
      1916864512,
      134248910,
      2043572801,
      1091043497,
      1769108596,
      1936015202,
      -1975817147,
      1950418944,
      1651077748,
      -1166842561,
      1950418944,
      1651077748,
      -1082956481,
      707346920,
      1698957312,
      1819631974,
      199052916,
      1816463360,
      134253115,
      -1975817147,
      -1730821055,
      1191706739,
      526544748,
      1816594432,
      1327456879,
      12806754,
      1852262664,
      -1052570513,
      1632372736,
      1688359707,
      1328052577,
      191017059,
      1632372736,
      134265371,
      6630478,
      199052808,
      1649346560,
      -1488731286,
      1325924393,
      12545655,
      1763922440,
      -1088195375,
      1632765952
   };

   private static int nameLookup(String name) {
      for (int i = staticProperties.length - 1; i >= 0; i--) {
         if (name == staticProperties[i]) {
            return staticIndexes[i];
         }
      }

      return -1;
   }

   ESRegExpPrototype$Constructor() {
      super("RegExp", GlobalObject.getInstance().regExpPrototype, 2);
   }

   @Override
   public long requestFieldValue(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/ecmascript/runtime/GlobalObject.getInstance ()Lnet/rim/ecmascript/runtime/GlobalObject;
      // 03: astore 2
      // 04: aload 2
      // 05: getfield net/rim/ecmascript/runtime/GlobalObject.regExpLastMatch Lnet/rim/ecmascript/regexp/RegExp$MatchResult;
      // 08: astore 3
      // 09: aload 1
      // 0a: invokestatic net/rim/ecmascript/runtime/ESRegExpPrototype$Constructor.nameLookup (Ljava/lang/String;)I
      // 0d: istore 4
      // 0f: iload 4
      // 11: tableswitch 79 -1 14 166 79 79 79 79 79 79 79 79 79 79 98 90 106 135 151
      // 60: aload 3
      // 61: getfield net/rim/ecmascript/regexp/RegExp$MatchResult.captures [Ljava/lang/String;
      // 64: iload 4
      // 66: aaload
      // 67: invokestatic net/rim/ecmascript/runtime/Value.makeStringValue (Ljava/lang/String;)J
      // 6a: lreturn
      // 6b: aload 2
      // 6c: getfield net/rim/ecmascript/runtime/GlobalObject.regExpMultiline Z
      // 6f: invokestatic net/rim/ecmascript/runtime/Value.makeBooleanValue (Z)J
      // 72: lreturn
      // 73: aload 2
      // 74: getfield net/rim/ecmascript/runtime/GlobalObject.regExpInput Ljava/lang/String;
      // 77: invokestatic net/rim/ecmascript/runtime/Value.makeStringValue (Ljava/lang/String;)J
      // 7a: lreturn
      // 7b: aload 3
      // 7c: getfield net/rim/ecmascript/regexp/RegExp$MatchResult.captures [Ljava/lang/String;
      // 7f: arraylength
      // 80: istore 5
      // 82: iload 5
      // 84: bipush 1
      // 85: if_icmpgt 8b
      // 88: goto c4
      // 8b: aload 3
      // 8c: getfield net/rim/ecmascript/regexp/RegExp$MatchResult.captures [Ljava/lang/String;
      // 8f: iload 5
      // 91: bipush 1
      // 92: isub
      // 93: aaload
      // 94: invokestatic net/rim/ecmascript/runtime/Value.makeStringValue (Ljava/lang/String;)J
      // 97: lreturn
      // 98: aload 3
      // 99: getfield net/rim/ecmascript/regexp/RegExp$MatchResult.input Ljava/lang/String;
      // 9c: bipush 0
      // 9d: aload 3
      // 9e: getfield net/rim/ecmascript/regexp/RegExp$MatchResult.startIndex I
      // a1: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // a4: invokestatic net/rim/ecmascript/runtime/Value.makeStringValue (Ljava/lang/String;)J
      // a7: lreturn
      // a8: aload 3
      // a9: getfield net/rim/ecmascript/regexp/RegExp$MatchResult.input Ljava/lang/String;
      // ac: aload 3
      // ad: getfield net/rim/ecmascript/regexp/RegExp$MatchResult.endIndex I
      // b0: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // b3: invokestatic net/rim/ecmascript/runtime/Value.makeStringValue (Ljava/lang/String;)J
      // b6: lreturn
      // b7: aload 0
      // b8: aload 1
      // b9: invokespecial net/rim/ecmascript/runtime/Constructor.requestFieldValue (Ljava/lang/String;)J
      // bc: lreturn
      // bd: astore 5
      // bf: goto c4
      // c2: astore 5
      // c4: ldc_w ""
      // c7: invokestatic net/rim/ecmascript/runtime/Value.makeStringValue (Ljava/lang/String;)J
      // ca: lreturn
      // try (8 -> 15): 59 null
      // try (16 -> 19): 59 null
      // try (20 -> 23): 59 null
      // try (24 -> 39): 59 null
      // try (40 -> 47): 59 null
      // try (48 -> 54): 59 null
      // try (55 -> 58): 59 null
      // try (8 -> 15): 61 null
      // try (16 -> 19): 61 null
      // try (20 -> 23): 61 null
      // try (24 -> 39): 61 null
      // try (40 -> 47): 61 null
      // try (48 -> 54): 61 null
      // try (55 -> 58): 61 null
   }

   @Override
   public int notifyFieldDeleted(String name) {
      return super.notifyFieldDeleted(name);
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      GlobalObject global = GlobalObject.getInstance();
      int index = nameLookup(name);
      switch (index) {
         case 9:
            return super.notifyFieldChanged(name, value);
         case 10:
            global.regExpInput = Convert.toString(value);
            return true;
         case 11:
         default:
            global.regExpMultiline = Convert.toBoolean(value);
            return true;
      }
   }

   @Override
   public long run() {
      long parmPattern = this.getParm(0);
      long parmFlags = this.getParm(1);
      ESRegExp regExp = null;

      label56:
      try {
         regExp = (ESRegExp)Value.checkIfObjectValue(parmPattern);
      } finally {
         break label56;
      }

      if (!this.calledAsConstructor() && regExp != null && Value.getType(parmFlags) == 2) {
         return parmPattern;
      }

      String pattern = "";
      String flags = "";
      ESRegExpPrototype$FlagParser fp = new ESRegExpPrototype$FlagParser();
      if (regExp != null) {
         if (Value.getType(parmFlags) != 2) {
            throw ThrownValue.typeError(Resources.getString(35));
         }

         pattern = regExp.getPattern();
         fp.set(regExp.getGlobal(), regExp.getMultiLine(), regExp.getIgnoreCase());
      }

      if (Value.getType(parmPattern) != 2) {
         pattern = Convert.toString(parmPattern);
      }

      if (Value.getType(parmFlags) != 2) {
         flags = Convert.toString(parmFlags);
      }

      fp.parse(flags);
      return Value.makeObjectValue(new ESRegExp(pattern, fp.global, fp.ignoreCase, fp.multiLine));
   }
}
