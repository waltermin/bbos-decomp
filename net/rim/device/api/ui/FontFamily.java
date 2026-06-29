package net.rim.device.api.ui;

import java.io.IOException;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.LongHashtable;

public class FontFamily {
   private String _name;
   private LongHashtable _fontsUsed = new LongHashtable();
   private Font[] _fonts;
   private int[] _heights;
   private int _type;
   private byte[] _hashArray;
   private static int[] FONTSIZES = new int[]{
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20,
      21,
      22,
      23,
      24,
      25,
      26,
      27,
      28,
      29,
      30,
      31,
      32,
      33,
      34,
      35,
      36,
      37,
      38,
      39,
      40,
      41,
      42,
      43,
      44,
      45,
      46,
      47,
      48,
      49,
      50,
      51,
      52,
      53,
      54,
      55,
      56,
      57,
      58,
      59,
      60,
      61,
      62,
      63,
      64,
      65,
      66,
      67,
      68,
      69,
      70,
      71,
      72,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      207814912,
      1277135469,
      16218465,
      1287875073,
      1918312329,
      1394831384,
      1930628195,
      1064304896,
      -1117257139,
      -1934043308,
      1879113763,
      -1435393985,
      16802828,
      16788083,
      -2104615050,
      527827200,
      1948811918,
      2059787,
      -1910540799,
      12861826,
      -1910540799,
      2036419714,
      1979777271,
      1283624479,
      821524833,
      527827200,
      1388282510,
      1979777272,
      1979777066,
      1097165679,
      1864803,
      1802466817,
      1717912677,
      1953264993,
      1232409421,
      100678516,
      1819631974,
      1819879028,
      1952661856,
      1812332572,
      100689154,
      1097138796,
      1812332740,
      -128817918,
      134219776,
      1617721638,
      638058584,
      1499491449,
      2032535552,
      638058622,
      8286439,
      555427848,
      671612935,
      1153502307,
      477417833,
      1982334976,
      134254111,
      1869832769,
      1130708844,
      -2072975508,
      134247557,
      688350017,
      645816937,
      152450,
      123945224,
      2120378665,
      1668506948,
      8939890,
      123945224,
      2120378665,
      -343247291,
      1665206272,
      1651058951,
      274942334,
      1668506948,
      8939890,
      123945224,
      2120378665,
      6630478,
      123945224,
      2120378665,
      1846305104,
      1091043444,
      1764296547,
      1867677282,
      1091043454,
      1764296547,
      -1957462430,
      1950424259,
      1665206272,
      1651058951,
      -1014279298,
      1956816412,
      1665206272,
      1651058951,
      14111870,
      123945224,
      2120378665,
      6649222,
      123945224,
      2120378665,
      1097128888,
      1091043444,
      1764296547,
      -608666014,
      1956816484,
      1665206272,
      15801460,
      1952661768,
      1666409913,
      134222604,
      -1183554751,
      -134327166,
      1816201216,
      1085809524,
      1816201216,
      1186743924,
      7985964,
      1953382664,
      1634279273,
      107957619,
      1883310080,
      12766576,
      124993800,
      134247534,
      1920234561,
      1698652777,
      1830831731,
      134265926,
      -1486258111,
      -1991475614,
      7562527,
      -434552312,
      1866598400,
      7562402,
      1688355336,
      1107820670,
      543648685,
      134243490,
      1735109954,
      1331995168,
      2030284144,
      -1388181504,
      -1574934677,
      426550884,
      -1388181504,
      -1574934677,
      1869391460,
      1107820718,
      543648685,
      1724212386,
      134240286,
      1735109954,
      -983260640,
      5840486,
      1806516744,
      6710895,
      1806516744,
      208610948,
      -1052637184,
      5001813,
      1824604680,
      134243585,
      1894214723,
      7636356,
      212747016,
      1816622190,
      1394565743,
      1248355,
      -675527928,
      1399565606,
      1141375212,
      1735746149,
      1214581844,
      749707,
      1717912584,
      1953264993,
      1698957312,
      1819631974,
      12338804,
      1717912584,
      1953264993,
      1232409421,
      134232948,
      1634100548,
      -378246027,
      7628977
   };
   public static String FAMILY_SYSTEM = FontRegistry.DEFAULT_FAMILY;
   public static int MONO_BITMAP_FONT = 2;
   public static int SCALABLE_FONT = 1;
   public static int UNKNOWN_FONT = 0;

   FontFamily(String name) {
      this._type = UNKNOWN_FONT;
      this._hashArray = new byte[38];
      this._name = name;
   }

   public static FontFamily forName(String name) {
      FontFamily f = FontRegistry.get(name);
      if (f == null) {
         throw new ClassNotFoundException();
      } else {
         return f;
      }
   }

   public Font getFont(int style, int height) {
      return this.getFont(style, height, 0, 1, 0);
   }

   public Font getFont(int style, int height, int units) {
      return this.getFont(style, height, units, 1, 0);
   }

   public Font getFont(int style, int height, int units, int antialiasingMode, int effects) {
      return this.getFont(style, height, units, antialiasingMode, effects, 65536, 0, 0, 65536, 0, 0);
   }

   public synchronized Font getFont(int style, int height, int units, int antialiasingMode, int effects, int A, int B, int C, int D, int Tx, int Ty) {
      return this.getFont(style, height, units, antialiasingMode, effects, A, B, C, D, Tx, Ty, 0, 16777215);
   }

   public synchronized Font getFont(
      int style,
      int height,
      int units,
      int antialiasingMode,
      int effects,
      int A,
      int B,
      int C,
      int D,
      int Tx,
      int Ty,
      int effectsStrokeColor,
      int effectsFillColor
   ) {
      if (effects < 0) {
         effects = 0;
      }

      if (1 > antialiasingMode || 4 < antialiasingMode) {
         antialiasingMode = 1;
      }

      height = Ui.convertSize(height, units, 0);
      if (height < 6 || height > 128) {
         Font df = FontRegistry._getDefaultFont();
         if (df != null) {
            height = df.getHeight();
         } else {
            height = FontRegistry.DEFAULT_SIZE;
         }
      }

      Font font = null;
      this._hashArray[0] = (byte)height;
      this._hashArray[1] = (byte)antialiasingMode;
      this._hashArray[2] = (byte)effects;
      this._hashArray[3] = (byte)(effects >> 8);
      this._hashArray[4] = (byte)(effects >> 16);
      this._hashArray[5] = (byte)(effects >> 24);
      this._hashArray[6] = (byte)A;
      this._hashArray[7] = (byte)(A >> 8);
      this._hashArray[8] = (byte)(A >> 16);
      this._hashArray[9] = (byte)(A >> 24);
      this._hashArray[10] = (byte)B;
      this._hashArray[11] = (byte)(B >> 8);
      this._hashArray[12] = (byte)(B >> 16);
      this._hashArray[13] = (byte)(B >> 24);
      this._hashArray[14] = (byte)C;
      this._hashArray[15] = (byte)(C >> 8);
      this._hashArray[16] = (byte)(C >> 16);
      this._hashArray[17] = (byte)(C >> 24);
      this._hashArray[18] = (byte)D;
      this._hashArray[19] = (byte)(D >> 8);
      this._hashArray[20] = (byte)(D >> 16);
      this._hashArray[21] = (byte)(D >> 24);
      this._hashArray[22] = (byte)Tx;
      this._hashArray[23] = (byte)(Tx >> 8);
      this._hashArray[24] = (byte)(Tx >> 16);
      this._hashArray[25] = (byte)(Tx >> 24);
      this._hashArray[26] = (byte)Ty;
      this._hashArray[27] = (byte)(Ty >> 8);
      this._hashArray[28] = (byte)(Ty >> 16);
      this._hashArray[29] = (byte)(Ty >> 24);
      this._hashArray[30] = (byte)effectsStrokeColor;
      this._hashArray[31] = (byte)(effectsStrokeColor >> 8);
      this._hashArray[32] = (byte)(effectsStrokeColor >> 16);
      this._hashArray[33] = (byte)(effectsStrokeColor >> 24);
      this._hashArray[34] = (byte)effectsFillColor;
      this._hashArray[35] = (byte)(effectsFillColor >> 8);
      this._hashArray[36] = (byte)(effectsFillColor >> 16);
      this._hashArray[37] = (byte)(effectsFillColor >> 24);
      long key = style;
      key = key << 32 | CRC32.update(-this._hashArray.length, this._hashArray, 0, this._hashArray.length) & 65535;
      font = (Font)this._fontsUsed.get(key);
      if (font != null && font.hasAttributes(style, height, antialiasingMode, effects, A, B, C, D, Tx, Ty, effectsStrokeColor, effectsFillColor)) {
         return font;
      }

      font = new Font(this, style, height, antialiasingMode, effects, A, B, C, D, Tx, Ty, effectsStrokeColor, effectsFillColor);
      this._fontsUsed.put(key, font);
      return font;
   }

   public static FontFamily[] getFontFamilies() {
      String[] f = FontRegistry.getFontFamilies();
      FontFamily[] ff = new FontFamily[f.length];

      for (int i = 0; i < f.length; i++) {
         ff[i] = FontRegistry.get(f[i]);
      }

      return ff;
   }

   public int getTypefaceType() {
      if (this._type == UNKNOWN_FONT) {
         FontRegistry registry = FontRegistry.getInstance();
         if (registry != null) {
            try {
               this._type = registry.getTypefaceType(this._name);
            } catch (IOException var3) {
            }
         }
      }

      return this._type;
   }

   public int[] getHeights() {
      if (this._heights == null) {
         FontRegistry registry = FontRegistry.getInstance();
         if (registry != null) {
            int[] rc = registry.getHeightsForTypeface(this._name);
            if (rc != null) {
               this._heights = rc;
            } else {
               this._heights = FONTSIZES;
            }
         } else {
            this._heights = FONTSIZES;
         }
      }

      return Arrays.copy(this._heights);
   }

   public final String getName() {
      return this._name;
   }

   @Override
   public String toString() {
      return this._name;
   }

   public final boolean isHeightSupported(int height) {
      int[] h = this.getHeights();
      if (h != null && h.length != 0) {
         int i = 0;

         while (i < h.length && height != h[i]) {
            i++;
         }

         return i < h.length;
      } else {
         return false;
      }
   }

   public boolean isStyleSupported(int style) {
      return true;
   }

   public final void publish() {
   }

   public Font[] getFonts() {
      if (this._fonts == null) {
         int[] h = this.getHeights();
         if (h == null || h.length == 0) {
            return null;
         }

         this._fonts = new Font[h.length];

         for (int i = 0; i < h.length; i++) {
            this._fonts[i] = this.getFont(0, h[i]);
         }
      }

      return this._fonts;
   }

   @Override
   public boolean equals(Object obj) {
      return this._name == ((FontFamily)obj).getName();
   }

   void reload() {
      this._fonts = null;
      this._heights = null;
   }
}
