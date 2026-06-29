package net.rim.tid.im.layout;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.ui.UiSettings;
import net.rim.tid.util.Utils;
import net.rim.vm.Array;

public class SLKeyLayout {
   private short[][][] _ranges;
   private short[] _special;
   private int _headerLength;
   private int _indexesLength;
   private byte[] _data;
   private StringBuffer _defResult = new StringBuffer("");
   private Locale _locale;
   private byte[] _cache;
   private int _cacheSize = -1;
   private boolean _isReduced;
   private byte _mask;
   private int _KeyCodeCache = -1;
   private int[] _iAltedKeys;
   private StringBuffer _bytes2StringCache;
   private String _nameID;
   private static final int STATUS_CHARACTER = 12288;
   private static final int STATUS_UNALT = 16384;
   private static String MAP_COMMON_PREFIX = "Keypad_";
   private static String MAP_COMMON_SUFFIX = ".map";
   private static int[] _modifiers = new int[]{
      0, 1, 2, 8, 3, 9, 10, 11, -804650996, 0, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1, 0, -804650998, 3, 5, 7, 10, 15, 3, 5, 7, 10, 15
   };
   private static String[] _maps = new String[]{
      "azerty_ca___QR24",
      "azerty_ca___QRED",
      "azerty_da___QR24",
      "azerty_da___QRED",
      "azerty_de___QR24",
      "azerty_de___QRED",
      "azerty_default___QR24",
      "azerty_default___QRED",
      "azerty_it___QR24",
      "azerty_it___QRED",
      "azerty_pt___QR24",
      "azerty_pt___QRED",
      "azerty_ru_US__QW32",
      "azerty_sv___QR24",
      "azerty_sv___QRED",
      "azerty_tr___QR24",
      "azerty_tr___QRED",
      "qwerty_ar___FONE",
      "qwerty_ar___QR24",
      "qwerty_ar___QRED",
      "qwerty_ar___QW32",
      "qwerty_ca___QR24",
      "qwerty_ca___QRED",
      "qwerty_da___QR24",
      "qwerty_da___QRED",
      "qwerty_de___QR24",
      "qwerty_de___QRED",
      "qwerty_default___QR24",
      "qwerty_default___QRED",
      "qwerty_default___QRED_phonetest",
      "qwerty_el___QR24",
      "qwerty_el___QRED",
      "qwerty_he_US__QR24",
      "qwerty_it___QR24",
      "qwerty_it___QRED",
      "qwerty_ja___QW32",
      "qwerty_ko___QW32",
      "qwerty_ko___FONE",
      "qwerty_ko___QW32",
      "qwerty_no___QR24",
      "qwerty_no___QW32",
      "qwerty_pt___QRED",
      "qwerty_ru_US__FONE",
      "qwerty_ru_US__QR24",
      "qwerty_ru_US__QRED",
      "qwerty_ru_US__QW32",
      "qwerty_ru___FONE",
      "qwerty_ru___QR24",
      "qwerty_ru___QRED",
      "qwerty_ru___QW32",
      "qwerty_sv___QR24",
      "qwerty_sv___QRED",
      "qwerty_tr___QR24",
      "qwerty_tr___QRED",
      "qwerty_zh_CN_Pinyin_QR24",
      "qwertz_ca___QR24",
      "qwertz_ca___QRED",
      "qwertz_da___QR24",
      "qwertz_da___QRED",
      "qwertz_de___QR24",
      "qwertz_de___QRED",
      "qwertz_default___QR24",
      "qwertz_default___QRED",
      "qwertz_el___QR24",
      "qwertz_el___QRED",
      "qwertz_it___QR24",
      "qwertz_it___QRED",
      "qwertz_pt___QR24",
      "qwertz_pt___QRED",
      "qwertz_ru_US__QW32",
      "qwertz_sv___QR24",
      "qwertz_sv___QRED",
      "qwertz_tr___QR24",
      "qwertz_tr___QRED",
      "azerty_ca___FONE",
      "azerty_ca___QW32",
      "azerty_cs___FONE",
      "azerty_cs___QW32",
      "azerty_da___FONE",
      "azerty_da___QW32",
      "azerty_de___FONE",
      "azerty_de___QW32",
      "azerty_default",
      "azerty_default___FNEO",
      "azerty_default___FONE",
      "azerty_default___QW32",
      "azerty_el___FONE",
      "azerty_el___QW32",
      "azerty_fr",
      "azerty_hu___FONE",
      "azerty_hu___QW32",
      "azerty_it___FONE",
      "azerty_it___QW32",
      "azerty_nl___FONE",
      "azerty_nl___QW32",
      "azerty_pl___FONE",
      "azerty_pl___QW32",
      "azerty_sv___FONE",
      "azerty_sv___QW32",
      "azerty_tr___FONE",
      "azerty_tr___QW32",
      "qwerty_ca___FONE",
      "qwerty_ca___QW32",
      "qwerty_cs___FONE",
      "qwerty_cs___QW32",
      "qwerty_da___FONE",
      "qwerty_da___QW32",
      "qwerty_de___FONE",
      "qwerty_de___QW32",
      "qwerty_default",
      "qwerty_default___FNEO",
      "qwerty_default___FONE",
      "qwerty_default___QW32",
      "qwerty_el___FONE",
      "qwerty_el___QW32",
      "qwerty_en",
      "qwerty_en_GB",
      "qwerty_en_US",
      "qwerty_en_US__DVRK",
      "qwerty_en_US__FONE",
      "qwerty_en_US__M957",
      "qwerty_he_US__QW32",
      "qwerty_he___QW32",
      "qwerty_hu___FONE",
      "qwerty_hu___QW32",
      "qwerty_it___FONE",
      "qwerty_it___QW32",
      "qwerty_nl___FONE",
      "qwerty_nl___QW32",
      "qwerty_pl___FONE",
      "qwerty_pl___QW32",
      "qwerty_ru__Translit_FONE",
      "qwerty_sv___FONE",
      "qwerty_sv___QW32",
      "qwerty_tr___FONE",
      "qwerty_tr___QW32",
      "qwerty_zh_TW__FONE",
      "qwerty_zh_TW__QW32",
      "qwertz_ca___FONE",
      "qwertz_ca___QW32",
      "qwertz_cs___FONE",
      "qwertz_cs___QW32",
      "qwertz_da___FONE",
      "qwertz_da___QW32",
      "qwertz_de",
      "qwertz_de___FONE",
      "qwertz_de___QW32",
      "qwertz_default",
      "qwertz_default___FNEO",
      "qwertz_default___FONE",
      "qwertz_default___QW32",
      "qwertz_el___FONE",
      "qwertz_el___QW32",
      "qwertz_hu___FONE",
      "qwertz_hu___QW32",
      "qwertz_it___FONE",
      "qwertz_it___QW32",
      "qwertz_nl___FONE",
      "qwertz_nl___QW32",
      "qwertz_pl___FONE",
      "qwertz_pl___QW32",
      "qwertz_sv___FONE",
      "qwertz_sv___QW32",
      "qwertz_tr___FONE",
      "qwertz_tr___QW32",
      "azerty_ru__Translit_FONE",
      "qwertz_ru__Translit_FONE"
   };

   public SLKeyLayout(Locale locale, boolean reduced, byte modifierMask, InputStream is) {
      this._isReduced = reduced;
      this._mask = modifierMask;
      this._locale = locale;

      try {
         this.openMapFile(is);
      } catch (Exception e) {
         System.err.println("Error while opening map file: " + locale.getLanguage() + " | " + e);
      }
   }

   public SLKeyLayout(Locale locale, boolean reduced, byte modifierMask, byte[] data) {
      this._isReduced = reduced;
      this._mask = modifierMask;
      this._locale = locale;

      try {
         this.openMapFile(data);
      } catch (Exception e) {
         System.err.println("Error while opening map file: " + locale.getLanguage() + " | " + e);
      }
   }

   private boolean openMapFile(InputStream is) {
      byte[] data = new byte[is.available()];
      is.read(data);
      return this.openMapFile(data);
   }

   public String getNameID() {
      return this._nameID;
   }

   public void setNameID(String nameID) {
      this._nameID = nameID;
   }

   private boolean openMapFile(byte[] data) {
      this._data = data;
      int ptr = 0;
      byte nRanges = (byte)(data[ptr++] & 0xFF);
      byte nSpecial = (byte)(data[ptr++] & 0xFF);
      this._headerLength = nRanges * 4 + nSpecial * 2;
      this._ranges = new short[nRanges][][];
      this._special = new short[nSpecial];

      for (int i = 0; i < nRanges; i++) {
         short[] range = new short[2];
         int start = (short)this.bytesToInt(data[ptr++], data[ptr++]);
         int finish = (short)this.bytesToInt(data[ptr++], data[ptr++]);
         this._indexesLength += (finish - start) * 2;
         range[0] = (short)start;
         range[1] = (short)finish;
         this._ranges[i] = (short[][])range;
      }

      this._indexesLength += nSpecial * 2;

      for (int i = 0; i < nSpecial; i++) {
         this._special[i] = (short)this.bytesToInt(data[ptr++], data[ptr++]);
      }

      this.createAltKeys();
      return true;
   }

   private void createAltKeys() {
      int size = 0;
      int len = this._ranges.length;

      for (int i = 0; i < len; i++) {
         size += this._ranges[i][1] - this._ranges[i][0];
      }

      size += this._special.length;
      this._iAltedKeys = new int[size];
      int index = 0;

      for (int i = 0; i < this._ranges.length; i++) {
         int rLen = (int)this._ranges[i][1];

         for (int j = (int)this._ranges[i][0]; j < rLen; j++) {
            this._iAltedKeys[index++] = CharacterUtilities.toUpperCase(this.getKeyChars(j, 0, false).charAt(0), this._locale.getCode());
         }
      }

      len = this._special.length;

      for (int i = 0; i < len; i++) {
         this._iAltedKeys[index++] = CharacterUtilities.toUpperCase(this.getKeyChars(this._special[i], 0, false).charAt(0), this._locale.getCode());
      }
   }

   private int findAltedKeyCode(int ch) {
      int len = this._iAltedKeys.length;

      for (int i = 0; i < len; i++) {
         if (ch == this._iAltedKeys[i]) {
            int index = 0;

            for (int j = 0; j < this._ranges.length; j++) {
               if (index + (this._ranges[j][1] - this._ranges[j][0]) > i) {
                  return this._ranges[j][0] + (i - index);
               }

               index += this._ranges[j][1] - this._ranges[j][0];
            }

            return this._special[i - index];
         }
      }

      return -1;
   }

   public char getAltedChar(char ch) {
      int keyCode = -1;
      switch (ch) {
         case 'Σ':
            keyCode = 83;
            break;
         case 'ς':
            keyCode = 87;
            break;
         default:
            keyCode = this.findAltedKeyCode(CharacterUtilities.toUpperCase(ch, this._locale.getCode()));
      }

      return keyCode != -1 ? this.getKeyChars(keyCode, 8, false).charAt(0) : '\u0000';
   }

   public char getUnaltedChar(char ch) {
      ch = CharacterUtilities.toUpperCase(ch, this._locale.getCode());

      for (int i = 0; i < this._ranges.length; i++) {
         int rLen = (int)this._ranges[i][1];

         for (int j = (int)this._ranges[i][0]; j < rLen; j++) {
            char normal = this.getKeyChars(j, 8, false).charAt(0);
            if (CharacterUtilities.toUpperCase(normal, this._locale.getCode()) == ch) {
               return this.getKeyChars(j, 0, false).charAt(0);
            }
         }
      }

      int len = this._special.length;

      for (int i = 0; i < len; i++) {
         char normal = this.getKeyChars(this._special[i], 8, false).charAt(0);
         if (CharacterUtilities.toUpperCase(normal, this._locale.getCode()) == ch) {
            return this.getKeyChars(this._special[i], 0, false).charAt(0);
         }
      }

      return '\u0000';
   }

   public int getOriginalKeyCode(char ch, int modifier) {
      for (int i = 0; i < this._ranges.length; i++) {
         int rLen = (int)this._ranges[i][1];

         for (int j = (int)this._ranges[i][0]; j < rLen; j++) {
            StringBuffer chars = this.getKeyChars(j, modifier, false);
            if (this.indexOf(chars, ch) != -1) {
               return j;
            }
         }
      }

      int len = this._special.length;

      for (int i = 0; i < len; i++) {
         StringBuffer chars = this.getKeyChars(this._special[i], modifier, false);
         if (this.indexOf(chars, ch) != -1) {
            return this._special[i];
         }
      }

      return 0;
   }

   private int bytesToInt(byte byte1, byte byte2) {
      int result = 0;
      result |= byte1 & 255;
      result <<= 8;
      return result | byte2 & 0xFF;
   }

   public boolean isReduced() {
      return this._isReduced;
   }

   public byte getModifierMask() {
      return this._mask;
   }

   public boolean contains(char ch) {
      ch = CharacterUtilities.toUpperCase(ch, this._locale.getCode());

      for (int i = 0; i < this._ranges.length; i++) {
         int rLen = (int)this._ranges[i][1];

         for (int j = (int)this._ranges[i][0]; j < rLen; j++) {
            for (int g = 0; g < 7; g++) {
               if (g != 4 && g != 5) {
                  char normal = this.getKeyChars(j, _modifiers[g], false).charAt(0);
                  if (CharacterUtilities.toUpperCase(normal, this._locale.getCode()) == ch) {
                     return true;
                  }
               }
            }
         }
      }

      int len = this._special.length;

      for (int i = 0; i < len; i++) {
         for (int g = 0; g < 7; g++) {
            if (g != 4 && g != 5) {
               char normal = this.getKeyChars(this._special[i], _modifiers[g], false).charAt(0);
               if (CharacterUtilities.toUpperCase(normal, this._locale.getCode()) == ch) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean getDataFor(int keyCode) {
      try {
         int ptr = 0;
         boolean found = false;
         int skipInIndexes = 0;
         int offsetStart = 0;

         for (int i = 0; i < this._ranges.length; i++) {
            int start = this._ranges[i][0] & '\uffff';
            int finish = this._ranges[i][1] & '\uffff';
            if (keyCode >= start && keyCode < finish) {
               skipInIndexes += (keyCode - start) * 2;
               found = true;
               break;
            }

            skipInIndexes += (finish - start) * 2;
         }

         if (!found) {
            for (int i = 0; i < this._special.length; i++) {
               if (keyCode == this._special[i]) {
                  skipInIndexes += 2 * i;
                  found = true;
                  break;
               }
            }
         }

         int toRead = 0;
         if (!found) {
            return false;
         }

         ptr = 2 + this._headerLength + skipInIndexes;
         byte readedBytes = 2;
         offsetStart = this.bytesToInt(this._data[ptr++], this._data[ptr++]);
         if (skipInIndexes + 2 == this._indexesLength) {
            toRead = -1;
         } else {
            readedBytes = 4;
            toRead = this.bytesToInt(this._data[ptr++], this._data[ptr++]) - offsetStart;
         }

         if (toRead == 0) {
            return false;
         }

         ptr += this._indexesLength - (skipInIndexes + readedBytes) + offsetStart;
         if (toRead == -1) {
            toRead = this._data.length - ptr;
         }

         if (this._cache == null) {
            this._cache = new byte[toRead];
         } else if (this._cache.length < toRead) {
            Array.resize(this._cache, toRead);
         }

         System.arraycopy(this._data, ptr, this._cache, 0, toRead);
         this._cacheSize = toRead;
         return true;
      } catch (Exception e) {
         System.err.println("Error while requesting data from map file: " + this._locale.getLanguage());
         e.printStackTrace();
         return false;
      }
   }

   public synchronized StringBuffer getComplementaryChars(char ch, int modifier) {
      for (int i = 0; i < this._ranges.length; i++) {
         int rLen = (int)this._ranges[i][1];

         for (int j = (int)this._ranges[i][0]; j < rLen; j++) {
            StringBuffer temp = this.getKeyChars(j, modifier, false);
            if (this.indexOf(temp, ch) != -1) {
               return temp;
            }
         }
      }

      int len = this._special.length;

      for (int i = 0; i < len; i++) {
         StringBuffer temp = this.getKeyChars(this._special[i], modifier, false);
         if (this.indexOf(temp, ch) != -1) {
            return temp;
         }
      }

      return null;
   }

   private int indexOf(StringBuffer sb, char element) {
      for (int i = 0; i < sb.length(); i++) {
         if (sb.charAt(i) == element) {
            return i;
         }
      }

      return -1;
   }

   private int getModifierIndex(int modifier) {
      for (int i = 0; i < _modifiers.length; i++) {
         if (_modifiers[i] == modifier) {
            return i;
         }
      }

      return -1;
   }

   public synchronized StringBuffer getKeyChars(int keyCode, int modifier) {
      return this.getKeyChars(keyCode, modifier, false);
   }

   public synchronized StringBuffer getKeyChars(int keyCode, int modifier, boolean isCapsOn) {
      if (keyCode == 36 && !this.getDataFor(keyCode)) {
         if (this._bytes2StringCache == null) {
            this._bytes2StringCache = new StringBuffer();
         }

         this._bytes2StringCache.setLength(1);
         this._bytes2StringCache.setCharAt(0, UiSettings.getCurrencyKey() != 0 ? UiSettings.getCurrencyKey() : '$');
         this._KeyCodeCache = keyCode;
         return this._bytes2StringCache;
      } else if (this._data != null && (this._KeyCodeCache == keyCode || this.getDataFor(keyCode))) {
         this._KeyCodeCache = keyCode;
         modifier &= 11;
         return this.getKeyChars0(this._cache, this._cacheSize, modifier, isCapsOn);
      } else {
         return this.getDefBuffer();
      }
   }

   private synchronized StringBuffer getKeyChars0(byte[] data, int length, int modifier, boolean isCapsOn) {
      byte needCaps = (byte)(data[0] & 1);
      byte byteStructure = (byte)((data[0] & 2) != 0 ? 1 : 2);
      if (isCapsOn && needCaps == 1) {
         switch (modifier) {
            case -1:
               break;
            case 0:
            default:
               modifier = 1;
               break;
            case 1:
               modifier = 0;
         }
      }

      int mIndex = this.getModifierIndex(modifier);
      int charsLength = (length - 1) / byteStructure;
      if (charsLength <= _modifiers.length) {
         return this.bytesToString(data, (mIndex < charsLength ? mIndex : charsLength - 1) * byteStructure + 1, 1, byteStructure);
      }

      int bIdx = 0;
      int eIdx = byteStructure == 1 ? data[bIdx + 1] : this.bytesToInt(data[bIdx * 2 + 1], data[bIdx * 2 + 2]);

      for (int i = 0; i < _modifiers.length; i++) {
         if (bIdx > charsLength || eIdx > charsLength) {
            System.err.println("Error in map file for key code ");
            break;
         }

         try {
            if (i == mIndex) {
               return this.bytesToString(data, (bIdx + 1) * byteStructure + 1, eIdx - bIdx, byteStructure);
            }
         } catch (Exception e) {
            e.printStackTrace();
            return null;
         }

         bIdx = eIdx + 1;
         if (bIdx >= charsLength) {
            break;
         }

         eIdx = bIdx + (byteStructure == 1 ? data[bIdx + 1] : this.bytesToInt(data[bIdx * 2 + 1], data[bIdx * 2 + 2]));
      }

      return this.getDefBuffer();
   }

   private StringBuffer bytesToString(byte[] data, int start, int len, byte byteStructure) {
      if (len == 0) {
         return this.getDefBuffer();
      }

      if (this._bytes2StringCache == null) {
         this._bytes2StringCache = new StringBuffer();
      }

      this._bytes2StringCache.setLength(len);
      if (byteStructure == 1) {
         for (int i = 0; i < len; i++) {
            this._bytes2StringCache.setCharAt(i, (char)(data[start + i] & 0xFF));
         }
      } else {
         for (int i = 0; i < len; i++) {
            int pos = start + i * 2;
            this._bytes2StringCache.setCharAt(i, (char)this.bytesToInt(data[pos], data[pos + 1]));
         }
      }

      return this._bytes2StringCache;
   }

   public static int convertStatusToModifiers(int status) {
      if ((status & 32768) != 0) {
         return 32768;
      }

      status &= 28695;
      if (status == 0) {
         return 0;
      }

      if ((status & 4) != 0 && (status & 16) != 0) {
         status &= -7;
      }

      if ((status & 4) != 0) {
         status &= -3;
         if ((status & 1) != 0) {
            status &= -5;
         }
      }

      if ((status & 16) != 0) {
         status &= -2;
         if ((status & 2) != 0) {
            status &= -17;
         }
      }

      int modifier = 0;
      if ((status & 2) != 0) {
         modifier |= 1;
      }

      if ((status & 1) != 0) {
         modifier |= 8;
      }

      if ((status & 4) != 0) {
         modifier |= 2;
      }

      if ((status & 16) != 0) {
         modifier |= 10;
      }

      return modifier;
   }

   public static int convertModifiersToStatus(int modifier) {
      int status = 0;
      if ((modifier & 1) != 0) {
         status |= 2;
      }

      if ((modifier & 8) != 0) {
         status |= 1;
      }

      return status;
   }

   private StringBuffer getDefBuffer() {
      if (this._defResult.length() != 1) {
         this._defResult = new StringBuffer("");
      }

      return this._defResult;
   }

   public Locale getLocale() {
      return this._locale;
   }

   private static boolean isExists(String mapName) {
      if (mapName != null && mapName.length() != 0) {
         for (int i = 0; i < _maps.length; i++) {
            if (mapName.equals(_maps[i])) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static String getMapID(int aKeyboardId, String aKeyboardType, Locale anInputLocale, String mapLocation, boolean useDefault) {
      if (mapLocation != null && mapLocation.length() != 0) {
         if (anInputLocale.getVariant().equals("Multitap")) {
            anInputLocale = Locale.get(anInputLocale.getLanguage());
         }

         if ((anInputLocale.getCode() & -65536) == 1701707776) {
            useDefault = true;
         }

         if ((aKeyboardId & -65536) == 1364656128) {
            aKeyboardId = 1364669234;
         }

         String mapID = Utils.composeResourceID(aKeyboardId, aKeyboardType, anInputLocale, useDefault);
         if (!isExists(mapID)) {
            mapID = null;
         }

         return mapID;
      } else {
         return null;
      }
   }

   public static SLKeyLayout getLayout(
      Locale originatingLocale, boolean reduced, int aKeyboardId, String aKeyboardType, Locale anInputLocale, String mapLocation, boolean useDefault
   ) {
      String mapID = getMapID(aKeyboardId, aKeyboardType, anInputLocale, mapLocation, useDefault);
      byte[] data = null;
      SLKeyLayout result = null;
      if (mapID != null) {
         data = Utils.loadRimRes(mapLocation, MAP_COMMON_PREFIX + mapID + MAP_COMMON_SUFFIX);
         if (data != null) {
            result = new SLKeyLayout(originatingLocale, reduced, (byte)0, data);
            result.setNameID(mapID);
         }
      }

      return result;
   }

   public static InputStream getLayoutData(int aKeyboardId, String aKeyboardType, Locale anInputLocale, String mapLocation, boolean useDefault) {
      String mapID = getMapID(aKeyboardId, aKeyboardType, anInputLocale, mapLocation, useDefault);
      byte[] data = null;
      if (mapID != null) {
         data = Utils.loadRimRes(mapLocation, MAP_COMMON_PREFIX + mapID + MAP_COMMON_SUFFIX);
      }

      return data == null ? null : new ByteArrayInputStream(data);
   }

   public static String getKeyboardType(int aLocaleCode) {
      String ret = "qwerty";
      switch (aLocaleCode & -65536) {
         case 1684340736:
            ret = "qwertz";
         default:
            return ret;
         case 1718747136:
            return "azerty";
      }
   }
}
