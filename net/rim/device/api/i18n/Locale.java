package net.rim.device.api.i18n;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardException;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.i18n.DateTimeFormatOptions;
import net.rim.device.internal.i18n.LocaleInternal;
import net.rim.device.internal.i18n.ResourceBundleFetcher;
import net.rim.device.internal.util.StringUtilitiesInternal;
import net.rim.tid.awt.im.InputContext;
import net.rim.vm.Array;
import net.rim.vm.TraceBack;

public final class Locale {
   private int _code;
   private String _variant;
   private int _keyboardID;
   private static final int A = 1;
   private static final int B = 2;
   private static final int C = 4;
   private static final int D = 8;
   private static final int E = 16;
   private static final int F = 32;
   private static final int G = 64;
   private static final int H = 128;
   private static final int I = 256;
   private static final int J = 512;
   private static final int K = 1024;
   private static final int L = 2048;
   private static final int M = 4096;
   private static final int N = 8192;
   private static final int O = 16384;
   private static final int P = 32768;
   private static final int Q = 65536;
   private static final int R = 131072;
   private static final int S = 262144;
   private static final int T = 524288;
   private static final int U = 1048576;
   private static final int V = 2097152;
   private static final int W = 4194304;
   private static final int X = 8388608;
   private static final int Y = 16777216;
   private static final int Z = 33554432;
   private static final int[] _languagesISO639 = new int[]{
      50728995,
      156113,
      17055745,
      33554449,
      1861632,
      16925441,
      1058825,
      17957137,
      1836057,
      4194305,
      18119681,
      2646017,
      17722688,
      18449,
      135172,
      788481,
      1048576,
      5271552,
      8355273,
      5139921,
      33686592,
      16640,
      16384,
      128,
      16640,
      1048705,
      -804651004,
      40,
      38,
      36,
      249,
      -805044208,
      858927408,
      926299444,
      1650538808,
      1717920867,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      -804782058,
      1651663206,
      1684104033,
      1701733477,
      1702192499,
      1718576745,
      1734436466,
      1735157604,
      1769236851,
      1852796524,
      1919774836,
      1937142641,
      -804650996,
      1231964268,
      1231311981,
      1400460148,
      1400460144,
      1299009650,
      1416446824,
      1148603499,
      1349665644,
      1349993582,
      1330921812,
      1346596718,
      1230262339,
      -804650982,
      39811448,
      57570299,
      62029285,
      33576464,
      917716,
      8541952,
      23050683,
      1717248,
      1009688,
      53248,
      54702544,
      20841735,
      67108045,
      34785653,
      4096,
      21642481,
      1,
      5259280,
      53116895,
      40564460,
      50597953,
      1057109,
      262176,
      0,
      1572880,
      4329473,
      -804651004,
      1000,
      100,
      10,
      1,
      1866989824,
      727916,
      1094676481,
      1655138688,
      1979777150,
      846737962
   };
   private static final int _languagesISO639Count = 139;
   private static final int[] _countriesISO3166 = new int[]{
      39811448,
      57570299,
      62029285,
      33576464,
      917716,
      8541952,
      23050683,
      1717248,
      1009688,
      53248,
      54702544,
      20841735,
      67108045,
      34785653,
      4096,
      21642481,
      1,
      5259280,
      53116895,
      40564460,
      50597953,
      1057109,
      262176,
      0,
      1572880,
      4329473,
      -804651004,
      1000,
      100,
      10,
      1,
      1866989824,
      727916,
      1094676481,
      1655138688,
      1979777150,
      846737962,
      16781645,
      1701539702,
      -977993472,
      -1958422706,
      262159,
      -333485820,
      1728841216,
      100666270,
      194930444,
      1904283715,
      463733,
      -1637413882,
      -1368435957,
      201719815,
      -401891737,
      1566992756,
      1850045378,
      -345702041,
      106628608,
      191235584,
      1984259084,
      1935759242,
      -2129932461,
      100688902,
      1383027815,
      1812332581,
      117466370,
      524507,
      555427848,
      671612935,
      671613044,
      747480,
      1953251592,
      423171,
      1852850440,
      1883310080,
      694243438,
      134229879,
      1433301057,
      745409395,
      1091043429,
      11103856,
      1852850440,
      1992389,
      1953775880,
      1063414130,
      134247269,
      -1922796223,
      6820176,
      1770012936,
      679371431,
      -1991491728,
      7562527,
      1770012936,
      1283351207,
      1936007049,
      21104640,
      1697402468,
      1698826240,
      12190055,
      1869562376,
      134226302,
      1717990722,
      1107820555,
      191260277,
      1107820713,
      191260277
   };
   private static final int _countriesISO3166Count = 239;
   private static final short[] _latin1CharacterSetsLocales = new short[]{
      24934,
      25202,
      25441,
      25697,
      25701,
      25966,
      25971,
      25973,
      26217,
      26223,
      26226,
      26465,
      26468,
      26476,
      26995,
      26996,
      28268,
      28271,
      28788,
      29293,
      29553,
      29558,
      12,
      -12278,
      18540,
      18798,
      21613,
      18788,
      21364,
      21369,
      21360,
      21369,
      20594,
      19821,
      17256,
      21613,
      19563,
      17526,
      17260,
      20594,
      17518,
      20599,
      16724,
      20308
   };
   private static final int[] US_VENDORS = new int[]{
      1,
      100,
      101,
      102,
      103,
      104,
      105,
      106,
      107,
      108,
      109,
      126,
      135,
      156,
      157,
      160,
      163,
      164,
      172,
      177,
      189,
      203,
      213,
      225,
      226,
      -804650992,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      -804650989,
      3,
      4,
      5,
      6,
      7,
      8,
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
      -804650991,
      3,
      4,
      5,
      6,
      7,
      8,
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
      -804650982,
      50728995,
      156113,
      17055745,
      33554449,
      1861632,
      16925441,
      1058825,
      17957137,
      1836057,
      4194305,
      18119681,
      2646017,
      17722688,
      18449,
      135172,
      788481,
      1048576,
      5271552,
      8355273
   };
   private static final long LOCALE = 2450419769951257358L;
   private static String EMPTY = "";
   public static final long GUID_LOCALE_CHANGED = -7464003439710973532L;
   public static final long GUID_INPUT_LOCALE_CHANGED = -8040378802380461050L;
   public static final long GUID_NAME_ORDER_CHANGED = -1438311245835636745L;
   public static final int APPLICATION = 1;
   public static final int NO_KEYBOARD_ID = -1;
   public static final int LOCALE_SET_DECLINED = 0;
   public static final int LOCALE_SET_OK = 1;
   private static PersistentObject _persist = RIMPersistentStore.getPersistentObject(3895678782769729248L);
   private static Locale$LocalePersist _localePersist;
   private static final long REGISTRY_NAME = 3895678782769729248L;
   private static Locale$Locales _locales;
   private static Locale _defaultLocale;
   private static Locale _defaultInputLocale;
   public static final int LOCALE_ROOT = 0;
   public static final int LOCALE_ar = 1634861056;
   public static final int LOCALE_ca = 1667301376;
   public static final int LOCALE_cs = 1668481024;
   public static final int LOCALE_de = 1684340736;
   public static final int LOCALE_en = 1701707776;
   public static final int LOCALE_en_GB = 1701726018;
   public static final int LOCALE_en_US = 1701729619;
   public static final int LOCALE_es = 1702035456;
   public static final int LOCALE_es_MX = 1702055256;
   public static final int LOCALE_fr = 1718747136;
   public static final int LOCALE_fr_CA = 1718764353;
   public static final int LOCALE_he = 1751449600;
   public static final int LOCALE_hu = 1752498176;
   public static final int LOCALE_it = 1769209856;
   public static final int LOCALE_ko = 1802436608;
   public static final int LOCALE_nl = 1852571648;
   public static final int LOCALE_pt = 1886650368;
   public static final int LOCALE_pt_BR = 1886667346;
   public static final int LOCALE_zh = 2053636096;
   public static final int LOCALE_zh_CN = 2053653326;
   public static final int LOCALE_zh_HK = 2053654603;
   public static final int LOCALE_el = 1701576704;
   public static final int LOCALE_tr = 1953628160;
   public static final int LOCALE_ru = 1920270336;
   public static final int LOCALE_pl = 1886126080;
   public static final int LOCALE_ja = 1784741888;
   public static final int KEYBOARD_ID_QWERTY = 0;
   public static final int KEYBOARD_ID_QWERTY_PHONE = 1179602501;
   public static final int KEYBOARD_ID_QWERTY_REDUCED = 1364346180;
   public static final int KEYBOARD_ID_QWERTY_LEGACY = 1295594807;
   public static final int FIRST_NAME_ORDER = 0;
   public static final int LAST_NAME_ORDER = 1;

   private Locale(int code, String variant) {
      if (!verifyCode(code)) {
         throw new IllegalArgumentException("Invalid locale code: " + code);
      }

      if (variant == null || variant.length() == 0) {
         variant = EMPTY;
      }

      if (variant != EMPTY && code == 0) {
         throw new IllegalArgumentException("Invalid locale code: " + code);
      }

      this._code = code;
      this._variant = variant;
      this._keyboardID = -1;
   }

   public static final boolean isLatinOneCharacterSetLocale(Locale locale) {
      if (_latin1CharacterSetsLocales != null && locale != null) {
         short code = (short)(locale.getCode() >> 16);

         for (int i = 0; i < _latin1CharacterSetsLocales.length; i++) {
            if (code == _latin1CharacterSetsLocales[i]) {
               return true;
            }
         }
      }

      return false;
   }

   public static final void addLocaleInternal(Locale locale) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      synchronized (_locales.available) {
         Array.resize(_locales.available, _locales.available.length + 1);
         _locales.available[_locales.available.length - 1] = locale;
         Arrays.sort(_locales.available, _locales.comparator);
      }
   }

   public static final void addInputLocaleInternal(Locale locale) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      synchronized (_locales.inputAvailable) {
         if (Arrays.getIndex(_locales.inputAvailable, locale) == -1) {
            Array.resize(_locales.inputAvailable, _locales.inputAvailable.length + 1);
            _locales.inputAvailable[_locales.inputAvailable.length - 1] = locale;
            Arrays.sort(_locales.inputAvailable, _locales.comparator);
         }
      }
   }

   public static final String convertCodeToVariant(int code) {
      return convertKeyboardIDToString(code);
   }

   public static final int convertVariantToCcode(String variant) {
      return convertStringToKeyboardID(variant);
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof Locale)) {
         return false;
      }

      Locale l = (Locale)obj;
      return this._code == l._code && this._variant.equals(l._variant);
   }

   public static final Locale get(int code) {
      return get(code, null);
   }

   public static final Locale get(int code, String variant) {
      if (variant == null) {
         variant = EMPTY;
      }

      synchronized (_locales.used) {
         Locale locale = null;
         if (variant == EMPTY) {
            locale = (Locale)_locales.used.get(code);
         }

         if (locale == null) {
            locale = new Locale(code, variant);
            if (variant == EMPTY) {
               _locales.used.put(code, locale);
            }
         }

         return locale;
      }
   }

   public static final Locale get(String language) {
      return get(language, null, null);
   }

   public static final Locale get(String language, String country) {
      return get(language, country, null);
   }

   public static final Locale get(String language, String country, String variant) {
      int code = pack(language, country);
      return get(code, variant);
   }

   public static final Locale get(int code, String variant, int keyboardID) {
      if (keyboardID == -1) {
         return get(code, variant);
      }

      Locale result = new Locale(code, variant);
      result._keyboardID = keyboardID;
      return result;
   }

   public static final Locale[] getAvailableLocales() {
      synchronized (_locales.available) {
         Locale[] result = new Locale[_locales.available.length];
         System.arraycopy(_locales.available, 0, result, 0, result.length);
         return result;
      }
   }

   public static final Locale[] getAvailableInputLocales() {
      synchronized (_locales.inputAvailable) {
         Locale[] result = new Locale[_locales.inputAvailable.length];
         System.arraycopy(_locales.inputAvailable, 0, result, 0, result.length);
         return result;
      }
   }

   public final int getCode() {
      return this._code;
   }

   public final String getCountry() {
      String country = EMPTY;
      if ((this._code & 65535) != 0) {
         country = EMPTY + (char)(this._code >> 8 & 0xFF) + (char)(this._code >> 0 & 0xFF);
      }

      return country;
   }

   public static final String getDefaultCountry() {
      String country = EMPTY;
      int code = getDefaultLanguageLocale();
      if ((code & 65535) != 0) {
         country = EMPTY + (char)(code >> 8 & 0xFF) + (char)(code >> 0 & 0xFF);
      }

      return country;
   }

   public static final Locale getDefault() {
      return _defaultLocale != null ? _defaultLocale : _locales.current;
   }

   public static final Locale getDefaultInput() {
      if (_defaultInputLocale != null) {
         return _defaultInputLocale;
      } else {
         return _locales.currentInput != null ? _locales.currentInput : getDefault();
      }
   }

   public static final Locale getDefaultForSystem() {
      return _locales.current;
   }

   public static final Locale getDefaultInputForSystem() {
      return _locales.currentInput;
   }

   public static final Locale getDefaultForKeyboard() {
      int code = checkDefaultCode(Keypad.getHardwareMap());
      return get(code);
   }

   private static final Locale getDefaultFromOS() {
      int code = checkDefaultCode(getDefaultLanguageLocale());
      return get(code, null);
   }

   private static final int checkDefaultCode(int code) {
      if (verifyCode(code) && code != 0) {
         return code;
      }

      int vendorId = Branding.getVendorId();

      for (int i = US_VENDORS.length - 1; i >= 0; i--) {
         if (US_VENDORS[i] == vendorId) {
            return 1701729619;
         }
      }

      return 1701726018;
   }

   public static final int getDefaultNameOrder(int _localeCode) {
      String str = get(_localeCode).getLanguage();
      return !str.equals("zh") && !str.equals("ja") ? 0 : 1;
   }

   public final String getDisplayCountry() {
      int country = this._code & 65535;
      return country == 0 ? EMPTY : LocaleInternal.getString(65536 + country);
   }

   public final String getDisplayLanguage() {
      int language = this._code >> 16 & 65535;
      if (language == 0) {
         return EMPTY;
      }

      String result = null;
      ResourceBundle bundle = LocaleInternal.getBundle().getBundle(this);
      if (bundle.getLocale().getCode() == this._code) {
         result = (String)bundle.getObject(2, false);
      }

      if (result == null) {
         result = LocaleInternal.getString(65536 + language);
      }

      return result;
   }

   public final String getDisplayName() {
      String language = this.getDisplayLanguage();
      String country = this.getDisplayCountry();
      String variant = this.getDisplayVariant();
      if (country.length() == 0 && variant.length() != 0) {
         country = variant;
         variant = EMPTY;
      }

      String result = null;
      if (country.length() != 0) {
         result = MessageFormat.format(LocaleInternal.getString(100), new Object[]{language, country, variant != EMPTY ? "," : EMPTY, variant});
      } else {
         result = language;
      }

      return result;
   }

   public final String getDisplayVariant() {
      return this._variant;
   }

   public static final int getSystemKeyboardID() {
      return Keypad.getHardwareLayout();
   }

   public final boolean isKeyboardIDSet() {
      return this._keyboardID != -1;
   }

   public final int getKeyboardID() {
      return this._keyboardID;
   }

   public static final String convertKeyboardIDToString(int aID) {
      if (aID != 0 && aID != -1) {
         StringBuffer buffer = _locales.buffer;
         synchronized (buffer) {
            buffer.setLength(0);

            for (int lv = 24; lv >= 0; lv -= 8) {
               char ch = (char)(aID >> lv & 0xFF);
               if (ch == 0) {
                  break;
               }

               buffer.append(ch);
            }

            return buffer.toString();
         }
      } else {
         return "";
      }
   }

   public static final String getPersonalNamesSeparator() {
      return getPersonalNamesSeparator(getSystemNameOrder());
   }

   public static final String getPersonalNamesSeparator(int order) {
      String nameSeparator;
      switch (order) {
         case -1:
            throw new IllegalArgumentException("Illegal order ID " + order);
         case 0:
         default:
            nameSeparator = LocaleInternal.getBundle().getString(83539);
            break;
         case 1:
            nameSeparator = LocaleInternal.getBundle().getString(83540);
      }

      return nameSeparator != null && nameSeparator.length() >= 1 ? nameSeparator : "";
   }

   public static final int convertStringToKeyboardID(String variant) {
      if (variant == null) {
         return -1;
      }

      if (variant.length() == 0) {
         return 0;
      }

      int result = 0;
      int shift = 24;
      int length = variant.length();

      for (int lv = 0; lv < length; lv++) {
         result |= variant.charAt(lv) << shift;
         shift -= 8;
      }

      return result;
   }

   public static final String[] getISOCountries() {
      String[] countries = new String[239];
      StringBuffer buffer = _locales.buffer;
      synchronized (buffer) {
         buffer.setLength(0);
         int count = 0;

         for (int first = 0; first < 26; first++) {
            for (int second = 0; second < 26; second++) {
               if ((1 << second & _countriesISO3166[first]) != 0) {
                  buffer.setLength(0);
                  buffer.append((char)(65 + first));
                  buffer.append((char)(65 + second));
                  countries[count++] = buffer.toString();
               }
            }
         }

         return countries;
      }
   }

   public static final String[] getISOLanguages() {
      String[] languages = new String[139];
      StringBuffer buffer = _locales.buffer;
      synchronized (buffer) {
         buffer.setLength(0);
         int count = 0;

         for (int first = 0; first < 26; first++) {
            for (int second = 0; second < 26; second++) {
               if ((1 << second & _languagesISO639[first]) != 0) {
                  buffer.setLength(0);
                  buffer.append((char)(97 + first));
                  buffer.append((char)(97 + second));
                  languages[count++] = buffer.toString();
               }
            }
         }

         return languages;
      }
   }

   public final String getLanguage() {
      String language = EMPTY;
      if ((this._code & -65536) != 0) {
         language = EMPTY + (char)(this._code >> 24 & 0xFF) + (char)(this._code >> 16 & 0xFF);
      }

      return language;
   }

   final Locale getParent() {
      int code = this._code;
      if (this._variant == EMPTY) {
         if ((this._code & 65535) != 0) {
            code &= -65536;
         } else {
            if ((this._code & -65536) == 0) {
               return null;
            }

            code = 0;
         }
      }

      return get(code);
   }

   public static final int getSystemNameOrder() {
      return _localePersist.nameOrder;
   }

   public final String getVariant() {
      return this._variant;
   }

   @Override
   public final int hashCode() {
      return this._variant == EMPTY ? this._code : this._code ^ this._variant.hashCode();
   }

   public static final boolean isDefaultForKeyboardSet() {
      int code = Keypad.getHardwareMap();
      return verifyCode(code) && code != 0;
   }

   private static final int pack(String language, String country) {
      if (!verifyLanguage(language)) {
         throw new IllegalArgumentException("Invalid language: " + language);
      }

      if (!verifyCountry(country)) {
         throw new IllegalArgumentException("Invalid country: " + country);
      }

      int code = 0;
      if (language != null && !language.equals(EMPTY)) {
         code |= language.charAt(0) << 24 | language.charAt(1) << 16;
      }

      if (country != null && !country.equals(EMPTY)) {
         code |= country.charAt(0) << '\b' | country.charAt(1) << 0;
      }

      return code;
   }

   public static final Locale parse(String id) {
      if (id == null) {
         return null;
      }

      int code = 0;
      String variant = null;

      for (int lv = id.length() - 1; lv >= 0; lv--) {
         char ch = id.charAt(lv);
         if (ch > 127) {
            throw new IllegalArgumentException();
         }
      }

      int length = id.length();
      int pos = 0;

      try {
         if (pos < length) {
            if (id.charAt(pos) != '_') {
               char l0 = id.charAt(pos + 0);
               char l1 = id.charAt(pos + 1);
               code |= l0 << 24 | l1 << 16;
               pos += 2;
            }

            if (pos < length) {
               if (id.charAt(pos) != '_') {
                  throw new IllegalArgumentException();
               }

               if (id.charAt(++pos) != '_') {
                  char c0 = id.charAt(pos + 0);
                  char c1 = id.charAt(pos + 1);
                  code |= c0 << '\b' | c1 << 0;
                  pos += 2;
               }

               if (pos < length) {
                  if (id.charAt(pos) != '_') {
                     throw new IllegalArgumentException();
                  }

                  if (++pos == length) {
                     throw new IllegalArgumentException();
                  }

                  variant = id.substring(pos);
               }
            }
         }
      } catch (IndexOutOfBoundsException e) {
         throw new IllegalArgumentException();
      }

      return get(code, variant);
   }

   public static final boolean isLocaleEligbleForRemovable(Locale locale, boolean inputLocale) {
      if (locale == null) {
         return false;
      }

      int localeCode = locale.getCode();
      return localeCode != 0 && localeCode != 1701707776 && localeCode != getDefaultLanguageLocale()
         ? !inputLocale || checkDefaultCode(0) != localeCode
         : false;
   }

   public static final void removeLocaleInternal(Locale locale) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      int indexToRemove = Arrays.binarySearch(_locales.available, locale, _locales.comparator, 0, _locales.available.length);
      if (indexToRemove > 0 && locale.getCode() == _locales.available[indexToRemove].getCode() && isLocaleEligbleForRemovable(locale, false)) {
         String paramVariant = locale.getVariant();
         String localeVariant = _locales.available[indexToRemove].getVariant();
         if (paramVariant != null && localeVariant != null && paramVariant.equals(localeVariant)) {
            Arrays.remove(_locales.available, _locales.available[indexToRemove]);
         }
      }

      indexToRemove = Arrays.binarySearch(_locales.inputAvailable, locale, _locales.comparator, 0, _locales.inputAvailable.length);
      if (indexToRemove >= 0 && locale.getCode() == _locales.inputAvailable[indexToRemove].getCode() && isLocaleEligbleForRemovable(locale, true)) {
         String paramVariant = locale.getVariant();
         String localeVariant = _locales.inputAvailable[indexToRemove].getVariant();
         if (paramVariant != null && localeVariant != null && paramVariant.equals(localeVariant)) {
            Arrays.remove(_locales.inputAvailable, _locales.inputAvailable[indexToRemove]);
         }
      }
   }

   public static final void setDefault(Locale defaultLocale) {
      _defaultLocale = defaultLocale;
      int pid = Application.getApplication().getProcessId();
      RIMGlobalMessagePoster.postGlobalEvent(pid, -7464003439710973532L, 1, 0, null, null);
      RIMGlobalMessagePoster.postGlobalEvent(pid, -8040378802380461050L, 1, 0, null, null);
      RIMGlobalMessagePoster.postGlobalEvent(pid, -1438311245835636745L, 1, 0, null, null);
      DateTimeFormatOptions.onAppLocaleChange();
      RIMGlobalMessagePoster.postGlobalEvent(pid, 7207871974803693937L, 1, 0, null, null);
   }

   public static final void setDefaultInput(Locale defaultLocale) {
      if (InputContext.getInstance().selectInputMethod(defaultLocale)) {
         _defaultInputLocale = defaultLocale;
         int pid = Application.getApplication().getProcessId();
         RIMGlobalMessagePoster.postGlobalEvent(pid, -8040378802380461050L, 1, 0, null, null);
      }
   }

   public static final void setDefaultForSystem(Locale locale) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      synchronized (_locales.available) {
         int index = Arrays.binarySearch(_locales.available, locale, _locales.comparator, 0, _locales.available.length);
         if (index < 0 || locale.getCode() == 0) {
            Locale searchLocale = get(locale.getCode() & -65536);
            index = Arrays.binarySearch(_locales.available, searchLocale, _locales.comparator, 0, _locales.available.length);
         }

         if (index < 0 || locale.getCode() == 0) {
            throw new IllegalArgumentException("Unsupported locale: " + locale.toString());
         }
      }

      synchronized (_persist) {
         _localePersist.code = locale.getCode();
         _localePersist.variant = locale.getVariant();
         _persist.commit();
         _locales.current = locale;
      }

      RIMGlobalMessagePoster.postGlobalEvent(-7464003439710973532L);
      RIMGlobalMessagePoster.postGlobalEvent(-8040378802380461050L);
      RIMGlobalMessagePoster.postGlobalEvent(-1438311245835636745L);
      DateTimeFormatOptions.onSystemLocaleChange();
      RIMGlobalMessagePoster.postGlobalEvent(7207871974803693937L);
      InputContext.getInstance().selectInputMethod(getDefaultInputForSystem());
      locale.setDisplayLocale(locale.getCode());
      if (SIMCard.isSupported()) {
         try {
            SIMCard.atSetLocale(locale.getCode());
            return;
         } catch (SIMCardException var7) {
         }
      }
   }

   private final native void setDisplayLocale(int var1);

   private final native void setInputLocale(int var1);

   public static final int setDefaultInputForSystem(Locale locale) {
      int callingModule = TraceBack.getCallingModule(0);
      return setDefaultInputForSystemImpl(locale, true, callingModule);
   }

   public static final int setDefaultInputForSystem(Locale locale, boolean updateInput) {
      int callingModule = TraceBack.getCallingModule(0);
      return setDefaultInputForSystemImpl(locale, updateInput, callingModule);
   }

   private static final int setDefaultInputForSystemImpl(Locale locale, boolean updateInput, int callingModule) {
      if (!ControlledAccess.verifyRRISignature(callingModule)) {
         ApplicationControl.assertChangeDeviceSettingsPermitted(true, CommonResource.getBundle(), 10133);
      }

      if (updateInput && !InputContext.getInstance().selectInputMethod(locale)) {
         return 0;
      }

      synchronized (_persist) {
         if (locale == null) {
            _localePersist.inputCode = -1;
            _localePersist.inputVariant = null;
         } else {
            _localePersist.inputCode = locale.getCode();
            _localePersist.inputVariant = locale.getVariant();
         }

         _persist.commit();
         _locales.currentInput = locale;
      }

      if (locale != null) {
         locale.setInputLocale(locale.getCode());
      }

      RIMGlobalMessagePoster.postGlobalEvent(-8040378802380461050L);
      return 1;
   }

   public static final String getCLDCLocaleString() {
      return _locales.current.buildToString(false).replace('_', '-');
   }

   public static final void setNameOrder(int order) {
      synchronized (_persist) {
         _localePersist.nameOrder = order;
         _persist.commit();
      }

      RIMGlobalMessagePoster.postGlobalEvent(-1438311245835636745L);
   }

   @Override
   public final String toString() {
      return this._code == 0 ? EMPTY : this.buildToString(true);
   }

   private final String buildToString(boolean withVariant) {
      StringBuffer buffer = _locales.buffer;
      synchronized (buffer) {
         buffer.setLength(0);
         if ((this._code & -65536) != 0) {
            buffer.append((char)(this._code >> 24 & 0xFF));
            buffer.append((char)(this._code >> 16 & 0xFF));
         }

         if ((this._code & 65535) != 0) {
            buffer.append('_');
            buffer.append((char)(this._code >> 8 & 0xFF));
            buffer.append((char)(this._code >> 0 & 0xFF));
         }

         if (withVariant && this._variant != EMPTY) {
            if ((this._code & 65535) == 0) {
               buffer.append('_');
            }

            buffer.append('_');
            buffer.append(this._variant);
         }

         return buffer.toString();
      }
   }

   public static final boolean verifyCode(int code) {
      try {
         return ((code & -65536) == 0 || (_languagesISO639[(code >>> 24) - 97] & 1 << (code >> 16 & 0xFF) - 97) != 0)
            && ((code & 65535) == 0 || (_countriesISO3166[(code >> 8 & 0xFF) - 65] & 1 << (code & 0xFF) - 65) != 0);
      } catch (Exception e) {
         return false;
      }
   }

   private static final boolean verifyCountry(String country) {
      try {
         return country == null || country.equals(EMPTY) || (_countriesISO3166[country.charAt(0) - 'A'] & 1 << country.charAt(1) - 'A') != 0;
      } catch (Exception e) {
         return false;
      }
   }

   private static final boolean verifyLanguage(String language) {
      try {
         return language == null || language.equals(EMPTY) || (_languagesISO639[language.charAt(0) - 'a'] & 1 << language.charAt(1) - 'a') != 0;
      } catch (Exception e) {
         return false;
      }
   }

   private static final Locale verifySystemModulePresent(Locale locale) {
      boolean giveUp = false;

      while (true) {
         try {
            StringBuffer scratch = StringUtilitiesInternal.getScratchBuffer();
            String name;
            synchronized (scratch) {
               scratch.append("net.rim.device.internal.resource.Locale");
               scratch.append('£');
               scratch.append(locale);
               name = scratch.toString();
               scratch.setLength(0);
            }

            boolean exists = ResourceBundleFetcher.verifyCompressedResourcePresent(name + ".crb");
            if (exists) {
               return locale;
            }

            Class.forName(name);
            return locale;
         } catch (ClassNotFoundException e) {
            if (giveUp) {
               throw new IllegalArgumentException();
            }

            int code = locale.getCode();
            if ((code & 65535) != 0) {
               locale = get(code & -65536);
            } else {
               locale = get(1701707776);
               giveUp = true;
            }
         }
      }
   }

   private static final native int getDefaultLanguageLocale();

   static {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      synchronized (_persist) {
         _localePersist = (Locale$LocalePersist)_persist.getContents();
         _locales = (Locale$Locales)applicationRegistry.get(2450419769951257358L);
         if (_locales == null) {
            _locales = new Locale$Locales();
            if (_localePersist == null) {
               _localePersist = new Locale$LocalePersist();
               int oscode = getDefaultFromOS().getCode();
               _localePersist.code = oscode;
               _localePersist.oscode = oscode;
               _localePersist.variant = null;
               _localePersist.inputCode = oscode;
               _localePersist.inputVariant = null;
               _localePersist.nameOrder = getDefaultNameOrder(oscode);
               _persist.setContents(_localePersist, 51, false);
               _persist.commit();
            } else {
               int oscode = getDefaultFromOS().getCode();
               if (_localePersist.oscode != oscode) {
                  _localePersist.code = oscode;
                  _localePersist.oscode = oscode;
                  _localePersist.variant = null;
                  _persist.commit();
               }
            }

            _locales.current = verifySystemModulePresent(get(_localePersist.code, _localePersist.variant));
            if (_locales.current != null) {
               _locales.current.setDisplayLocale(_locales.current.getCode());
            }

            _locales.currentInput = get(_localePersist.inputCode, _localePersist.inputVariant);
            if (_locales.currentInput != null) {
               _locales.currentInput.setInputLocale(_locales.currentInput.getCode());
            }

            applicationRegistry.put(2450419769951257358L, _locales);
         }
      }
   }
}
