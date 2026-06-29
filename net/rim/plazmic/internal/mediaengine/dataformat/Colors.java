package net.rim.plazmic.internal.mediaengine.dataformat;

import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.ToIntHashtable;

public class Colors {
   public static final int TRANSPARENT = -1;
   private static final int NUM_COLORS = 147;
   private static final String HEXVALUE_ID = "#";
   private static final String RGB_COLOR_ID = "rgb(";
   private static ToIntHashtable colors;

   public static int getColor(String name) {
      int color = -1;
      if (name != null) {
         if (colors == null) {
            init();
         }

         color = mapColor(StringUtilities.toLowerCase(name, 1701707776));
      }

      return color;
   }

   private static int mapColor(String name) {
      int color = -1;
      if (name.startsWith("rgb(")) {
         return parseRGBValue(name);
      } else {
         return name.startsWith("#") ? parseHexValue(name) : colors.get(name);
      }
   }

   private static int parseRGBValue(String param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: bipush -1
      // 02: istore 1
      // 03: new java/lang/Object
      // 06: dup
      // 07: aload 0
      // 08: bipush 4
      // 0a: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 0d: ldc_w ",)"
      // 10: invokespecial net/rim/device/api/util/StringTokenizer.<init> (Ljava/lang/String;Ljava/lang/String;)V
      // 13: astore 2
      // 14: aload 2
      // 15: invokevirtual net/rim/device/api/util/StringTokenizer.nextElement ()Ljava/lang/Object;
      // 18: checkcast java/lang/Object
      // 1b: invokevirtual java/lang/String.trim ()Ljava/lang/String;
      // 1e: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 21: istore 3
      // 22: aload 2
      // 23: invokevirtual net/rim/device/api/util/StringTokenizer.nextElement ()Ljava/lang/Object;
      // 26: checkcast java/lang/Object
      // 29: invokevirtual java/lang/String.trim ()Ljava/lang/String;
      // 2c: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 2f: istore 4
      // 31: aload 2
      // 32: invokevirtual net/rim/device/api/util/StringTokenizer.nextElement ()Ljava/lang/Object;
      // 35: checkcast java/lang/Object
      // 38: invokevirtual java/lang/String.trim ()Ljava/lang/String;
      // 3b: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;)I
      // 3e: istore 5
      // 40: iload 3
      // 41: iflt 80
      // 44: iload 3
      // 45: sipush 255
      // 48: if_icmpgt 80
      // 4b: iload 4
      // 4d: iflt 80
      // 50: iload 4
      // 52: sipush 255
      // 55: if_icmpgt 80
      // 58: iload 5
      // 5a: iflt 80
      // 5d: iload 5
      // 5f: sipush 255
      // 62: if_icmpgt 80
      // 65: aload 2
      // 66: invokevirtual net/rim/device/api/util/StringTokenizer.hasMoreTokens ()Z
      // 69: ifne 80
      // 6c: iload 3
      // 6d: bipush 16
      // 6f: ishl
      // 70: iload 4
      // 72: bipush 8
      // 74: ishl
      // 75: ior
      // 76: iload 5
      // 78: ior
      // 79: istore 1
      // 7a: iload 1
      // 7b: ireturn
      // 7c: astore 3
      // 7d: iload 1
      // 7e: ireturn
      // 7f: astore 3
      // 80: iload 1
      // 81: ireturn
      // try (10 -> 56): 58 null
      // try (10 -> 56): 61 null
   }

   private static int parseHexValue(String name) {
      int color = -1;

      try {
         name = name.substring(1);
         int length = name.length();
         if (length == 3) {
            char[] c = new char[6];

            for (int i = 0; i < 3; i++) {
               int index = i << 1;
               c[index] = name.charAt(i);
               c[index + 1] = c[index];
            }

            name = (String)(new Object(c));
            color = Integer.parseInt(name, 16);
         } else if (length == 6) {
            return Integer.parseInt(name, 16);
         }
      } finally {
         return color;
      }

      return color;
   }

   private static void init() {
      colors = (ToIntHashtable)(new Object(147));
      colors.put("aliceblue", 15792383);
      colors.put("antiquewhite", 16444375);
      colors.put("aqua", 65535);
      colors.put("aquamarine", 8388564);
      colors.put("azure", 15794175);
      colors.put("beige", 16119260);
      colors.put("bisque", 16770244);
      colors.put("black", 0);
      colors.put("blanchedalmond", 16772045);
      colors.put("blue", 255);
      colors.put("blueviolet", 9055202);
      colors.put("brown", 10824234);
      colors.put("burlywood", 14596231);
      colors.put("cadetblue", 6266528);
      colors.put("chartreuse", 8388352);
      colors.put("chocolate", 13789470);
      colors.put("coral", 16744272);
      colors.put("cornflowerblue", 6591981);
      colors.put("cornsilk", 16775388);
      colors.put("crimson", 14423100);
      colors.put("cyan", 65535);
      colors.put("darkblue", 139);
      colors.put("darkcyan", 35723);
      colors.put("darkgoldenrod", 12092939);
      colors.put("darkgray", 11119017);
      colors.put("darkgreen", 25600);
      colors.put("darkgrey", 11119017);
      colors.put("darkkhaki", 12433259);
      colors.put("darkmagenta", 9109643);
      colors.put("darkolivegreen", 5597999);
      colors.put("darkorange", 16747520);
      colors.put("darkorchid", 10040012);
      colors.put("darkred", 9109504);
      colors.put("darksalmon", 15308410);
      colors.put("darkseagreen", 9419919);
      colors.put("darkslateblue", 4734347);
      colors.put("darkslategray", 3100495);
      colors.put("darkslategrey", 3100495);
      colors.put("darkturquoise", 52945);
      colors.put("darkviolet", 9699539);
      colors.put("deeppink", 16716947);
      colors.put("deepskyblue", 49151);
      colors.put("dimgray", 6908265);
      colors.put("dimgrey", 6908265);
      colors.put("dodgerblue", 2003199);
      colors.put("firebrick", 11674146);
      colors.put("floralwhite", 16775920);
      colors.put("forestgreen", 2263842);
      colors.put("fuchsia", 16711935);
      colors.put("gainsboro", 14474460);
      colors.put("ghostwhite", 16316671);
      colors.put("gold", 16766720);
      colors.put("goldenrod", 14329120);
      colors.put("gray", 8421504);
      colors.put("green", 32768);
      colors.put("greenyellow", 11403055);
      colors.put("grey", 8421504);
      colors.put("honeydew", 15794160);
      colors.put("hotpink", 16738740);
      colors.put("indianred", 13458524);
      colors.put("indigo", 4915330);
      colors.put("ivory", 16777200);
      colors.put("khaki", 15787660);
      colors.put("lavender", 15132410);
      colors.put("lavenderblush", 16773365);
      colors.put("lawngreen", 8190976);
      colors.put("lemonchiffon", 16775885);
      colors.put("lightblue", 11393254);
      colors.put("lightcoral", 15761536);
      colors.put("lightcyan", 14745599);
      colors.put("lightgoldenrodyellow", 16448210);
      colors.put("lightgray", 13882323);
      colors.put("lightgreen", 9498256);
      colors.put("lightgrey", 13882323);
      colors.put("lightpink", 16758465);
      colors.put("lightsalmon", 16752762);
      colors.put("lightseagreen", 2142890);
      colors.put("lightskyblue", 8900346);
      colors.put("lightslategray", 7833753);
      colors.put("lightslategrey", 7833753);
      colors.put("lightsteelblue", 11584734);
      colors.put("lightyellow", 16777184);
      colors.put("lime", 65280);
      colors.put("limegreen", 3329330);
      colors.put("linen", 16445670);
      colors.put("magenta", 16711935);
      colors.put("maroon", 8388608);
      colors.put("mediumaquamarine", 6737322);
      colors.put("mediumblue", 205);
      colors.put("mediumorchid", 12211667);
      colors.put("mediumpurple", 9662683);
      colors.put("mediumseagreen", 3978097);
      colors.put("mediumslateblue", 8087790);
      colors.put("mediumspringgreen", 64154);
      colors.put("mediumturquoise", 4772300);
      colors.put("mediumvioletred", 13047173);
      colors.put("midnightblue", 1644912);
      colors.put("mintcream", 16121850);
      colors.put("mistyrose", 16770273);
      colors.put("moccasin", 16770229);
      colors.put("navajowhite", 16768685);
      colors.put("navy", 128);
      colors.put("oldlace", 16643558);
      colors.put("olive", 8421376);
      colors.put("olivedrab", 7048739);
      colors.put("orange", 16753920);
      colors.put("orangered", 16729344);
      colors.put("orchid", 14315734);
      colors.put("palegoldenrod", 15657130);
      colors.put("palegreen", 10025880);
      colors.put("paleturquoise", 11529966);
      colors.put("palevioletred", 14381203);
      colors.put("papayawhip", 16773077);
      colors.put("peachpuff", 16767673);
      colors.put("peru", 13468991);
      colors.put("pink", 16761035);
      colors.put("plum", 14524637);
      colors.put("powderblue", 11591910);
      colors.put("purple", 8388736);
      colors.put("red", 16711680);
      colors.put("rosybrown", 12357519);
      colors.put("royalblue", 4286945);
      colors.put("saddlebrown", 9127187);
      colors.put("salmon", 16416882);
      colors.put("sandybrown", 16032864);
      colors.put("seagreen", 3050327);
      colors.put("seashell", 16774638);
      colors.put("sienna", 10506797);
      colors.put("silver", 12632256);
      colors.put("skyblue", 8900331);
      colors.put("slateblue", 6970061);
      colors.put("slategray", 7372944);
      colors.put("slategrey", 7372944);
      colors.put("snow", 16775930);
      colors.put("springgreen", 65407);
      colors.put("steelblue", 4620980);
      colors.put("tan", 13808780);
      colors.put("teal", 32896);
      colors.put("thistle", 14204888);
      colors.put("tomato", 16737095);
      colors.put("turquoise", 4251856);
      colors.put("violet", 15631086);
      colors.put("wheat", 16113331);
      colors.put("white", 16777215);
      colors.put("whitesmoke", 16119285);
      colors.put("yellow", 16776960);
      colors.put("yellowgreen", 10145074);
   }
}
