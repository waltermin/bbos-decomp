package net.rim.tid.im.conv.europe;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.tid.im.conv.ConversionEvent;
import net.rim.tid.im.conv.repository.AlphabetChangeListener;
import net.rim.tid.im.conv.repository.IAlphabetRepository;
import net.rim.vm.Array;

public class AccentGrouping implements AlphabetChangeListener {
   private IAlphabetRepository _repository;
   private Locale _locale;
   private int[] _buttonKeys;
   private String[] _completeAccentedChars;
   private String[] _extendedAccentedChars;
   private int _cachedElementNo;
   private int _cachedLocaleAlphabetIndex;
   private final int[] ACCENTED_ALPHABET_CODES = new int[]{
      1701707776, 1684340736, 1718747136, 1702035456, 1769209856, 1886650368, Locale.get("sv", "").getCode()
   };
   private final int[][][] LOCALE_ACCENTED_ALPHABET = new int[][][]{
      (int[][])(new int[0]),
      (int[][])({223, 228, 246, 252, -804650994, 224, 225, 226, 227, 231, 232, 233, 234, 237, 243, 244}),
      (int[][])({
            224,
            226,
            231,
            232,
            233,
            234,
            238,
            239,
            244,
            249,
            251,
            -804651001,
            225,
            233,
            237,
            241,
            243,
            250,
            252,
            -804651002,
            228,
            229,
            232,
            233,
            246,
            252,
            712179968,
            712179968,
            -1975817147,
            16806977,
            1631988339,
            1929445403,
            1745965098,
            1929445562,
            3102506,
            -1607830783,
            1738589467,
            1178627189,
            1079384929,
            1936990569,
            712179968,
            15098272,
            209023745,
            61675587
      }),
      (int[][])({
            225,
            233,
            237,
            241,
            243,
            250,
            252,
            -804651002,
            228,
            229,
            232,
            233,
            246,
            252,
            712179968,
            712179968,
            -1975817147,
            16806977,
            1631988339,
            1929445403,
            1745965098,
            1929445562,
            3102506,
            -1607830783,
            1738589467,
            1178627189,
            1079384929,
            1936990569
      }),
      (int[][])({
            224,
            225,
            232,
            233,
            236,
            237,
            242,
            243,
            249,
            250,
            253,
            -804650997,
            224,
            226,
            231,
            232,
            233,
            234,
            238,
            239,
            244,
            249,
            251,
            -804651001,
            225,
            233,
            237,
            241,
            243,
            250,
            252,
            -804651002,
            228,
            229,
            232,
            233,
            246,
            252,
            712179968,
            712179968,
            -1975817147,
            16806977,
            1631988339,
            1929445403
      }),
      (int[][])({
            224,
            225,
            226,
            227,
            231,
            232,
            233,
            234,
            237,
            243,
            244,
            245,
            250,
            252,
            -804650997,
            224,
            225,
            232,
            233,
            236,
            237,
            242,
            243,
            249,
            250,
            253,
            -804650997,
            224,
            226,
            231,
            232,
            233,
            234,
            238,
            239,
            244,
            249,
            251,
            -804651001,
            225,
            233,
            237,
            241,
            243,
            250,
            252,
            -804651002,
            228,
            229,
            232,
            233,
            246,
            252,
            712179968,
            712179968,
            -1975817147
      }),
      (int[][])({
            228,
            229,
            232,
            233,
            246,
            252,
            712179968,
            712179968,
            -1975817147,
            16806977,
            1631988339,
            1929445403,
            1745965098,
            1929445562,
            3102506,
            -1607830783,
            1738589467,
            1178627189,
            1079384929,
            1936990569,
            712179968,
            15098272,
            209023745,
            61675587
      })
   };
   private StringBuffer _tempCompleteAccBuffer = (StringBuffer)(new Object());
   private StringBuffer _tempExtendedAccBuffer = (StringBuffer)(new Object());
   private StringBuffer _getAccentsTempBuffer;
   private String[] _getAccentsReturnContainer = new Object[2];
   private static final int GROWTH_STEP = 10;
   static int[] BASE_CHARS = new int[]{
      97,
      99,
      100,
      101,
      103,
      105,
      108,
      110,
      111,
      114,
      115,
      116,
      117,
      121,
      122,
      305,
      945,
      949,
      951,
      953,
      959,
      963,
      965,
      969,
      1077,
      1575,
      1608,
      1609,
      1610,
      -804651004,
      223,
      228,
      246,
      252,
      -804650994,
      224,
      225,
      226,
      227,
      231,
      232,
      233,
      234,
      237,
      243,
      244,
      245,
      250,
      252,
      -804650997,
      224,
      225,
      232,
      233,
      236,
      237,
      242,
      243,
      249,
      250,
      253,
      -804650997,
      224,
      226,
      231,
      232,
      233,
      234,
      238,
      239,
      244,
      249,
      251,
      -804651001,
      225,
      233,
      237,
      241,
      243,
      250,
      252,
      -804651002,
      228,
      229,
      232,
      233,
      246,
      252,
      712179968,
      712179968,
      -1975817147,
      16806977,
      1631988339,
      1929445403,
      1745965098,
      1929445562,
      3102506,
      -1607830783,
      1738589467,
      1178627189,
      1079384929,
      1936990569,
      712179968,
      15098272,
      209023745,
      61675587,
      -1258225543,
      -977993472,
      285934336,
      67138329,
      1699480576,
      8678976,
      1650545668,
      192170366,
      67115281,
      1299492244
   };
   static String[] ACCENTED_CANDIDATES = new String[]{
      "à",
      "ç",
      "\u000f\u0001",
      "è",
      "\u001f\u0001",
      "ì",
      ":\u0001>\u0001",
      "ñ",
      "ò",
      "U\u0001Y\u0001",
      "ß",
      "c\u0001e\u0001",
      "ù",
      "ýÿ",
      "z\u0001~\u0001|\u0001",
      "i",
      "¬\u0003",
      "\u00ad\u0003",
      "®\u0003",
      "¯\u0003Ê\u0003\u0090\u0003",
      "Ì\u0003",
      "Â\u0003",
      "Í\u0003Ë\u0003°\u0003",
      "Î\u0003",
      "Q\u0004",
      "#\u0006%\u0006",
      "$\u0006",
      "&\u0006",
      "&\u0006"
   };
   static IntIntHashtable _accentedUnaccentedMap = (IntIntHashtable)(new Object());
   static String FINAL_FORMS;

   public void setRepository(IAlphabetRepository aRepository, Locale aLocale) {
      this._repository = aRepository;
      this._repository.addAlphabetChangeListener(this);
      if (this._locale != aLocale) {
         if (this._buttonKeys == null) {
            this._buttonKeys = new int[10];
            this._completeAccentedChars = new Object[10];
            this._extendedAccentedChars = new Object[10];
         }

         this.reset();
         this._locale = aLocale;
         this._cachedLocaleAlphabetIndex = Arrays.getIndex(this.ACCENTED_ALPHABET_CODES, aLocale.getCode() & -65536);
      }
   }

   public void appendAccentedChars(ConversionEvent event) {
      if (this._repository != null || this._locale != null) {
         StringBuffer keyChars = event.getKeyChars();
         String[] accents = this.getAccents(keyChars);
         if (accents[0] != null) {
            keyChars.append(accents[0]);
         }

         if (accents[1] != null) {
            event.addOptionalKeyChars(accents[1]);
         }
      }
   }

   public void appendAccentedChars(StringBuffer aGroup) {
      this.appendAccentedChars(aGroup, false);
   }

   public void appendAccentedChars(StringBuffer aGroup, boolean ignoreExtendedAccentedChars) {
      if (this._repository != null || this._locale != null) {
         String[] accents = this.getAccents(aGroup);
         int origLen = aGroup.length();
         if (accents[0] != null) {
            if (accents[1] != null && accents[1].length() != 0) {
               for (int i = 0; i < accents[0].length(); i++) {
                  char ch = accents[0].charAt(i);
                  if (accents[1].indexOf(ch) == -1) {
                     boolean toAppend = true;

                     for (int j = 0; j < origLen; j++) {
                        if (ch == aGroup.charAt(j)) {
                           toAppend = false;
                           break;
                        }
                     }

                     if (toAppend) {
                        aGroup.append(ch);
                     }
                  }
               }
            } else {
               for (int i = 0; i < accents[0].length(); i++) {
                  char ch = accents[0].charAt(i);
                  boolean toAppend = true;

                  for (int j = 0; j < origLen; j++) {
                     if (ch == aGroup.charAt(j)) {
                        toAppend = false;
                        break;
                     }
                  }

                  if (toAppend) {
                     aGroup.append(ch);
                  }
               }
            }
         }
      }
   }

   public String getAccents(char aChar) {
      if (this._getAccentsTempBuffer == null) {
         this._getAccentsTempBuffer = (StringBuffer)(new Object(1));
      } else {
         this._getAccentsTempBuffer.setLength(0);
      }

      this._getAccentsTempBuffer.append(aChar);
      return this.getAccents(this._getAccentsTempBuffer)[0];
   }

   private String[] getAccents(StringBuffer aGroup) {
      if (aGroup.length() == 0) {
         this._getAccentsReturnContainer[0] = null;
         this._getAccentsReturnContainer[1] = null;
         return this._getAccentsReturnContainer;
      }

      int key = aGroup.charAt(0);
      int idx = Arrays.binarySearch(this._buttonKeys, key, 0, this._cachedElementNo);
      if (idx < 0) {
         idx = -(idx + 1);
         if (this._cachedElementNo == this._buttonKeys.length) {
            Array.resize(this._buttonKeys, this._buttonKeys.length + 10);
            Array.resize(this._completeAccentedChars, this._completeAccentedChars.length + 10);
            Array.resize(this._extendedAccentedChars, this._extendedAccentedChars.length + 10);
         }

         this._tempCompleteAccBuffer.setLength(0);
         this._tempExtendedAccBuffer.setLength(0);

         for (int i = 0; i < aGroup.length(); i++) {
            char ch = aGroup.charAt(i);
            boolean is_uc = CharacterUtilities.isUpperCase(ch);
            if (is_uc) {
               ch = CaseCorrector.toLowerCase(ch, this._locale);
            }

            int candidate_index = Arrays.binarySearch(BASE_CHARS, ch);
            if (candidate_index >= 0) {
               String candidates = ACCENTED_CANDIDATES[candidate_index];

               for (int j = 0; j < candidates.length(); j++) {
                  ch = candidates.charAt(j);
                  if (!is_uc || ch != 305 || this._locale.getCode() == Locale.get("tr").getCode()) {
                     boolean inGenericAlph = this.checkAlphabet(ch, true);
                     boolean inExtendedAlph = this.checkAlphabet(ch, false);
                     if ((inGenericAlph || inExtendedAlph) && (!is_uc || FINAL_FORMS.indexOf(ch) == -1)) {
                        if (!inGenericAlph && inExtendedAlph) {
                           this._tempExtendedAccBuffer.append(ch);
                        }

                        if (is_uc) {
                           ch = CaseCorrector.toUpperCase(ch, this._locale);
                        }

                        this._tempCompleteAccBuffer.append(ch);
                     }
                  }
               }
            }
         }

         if (idx < this._cachedElementNo) {
            int copyLen = this._cachedElementNo - idx;
            System.arraycopy(this._buttonKeys, idx, this._buttonKeys, idx + 1, copyLen);
            System.arraycopy(this._completeAccentedChars, idx, this._completeAccentedChars, idx + 1, copyLen);
            System.arraycopy(this._extendedAccentedChars, idx, this._extendedAccentedChars, idx + 1, copyLen);
         }

         this._buttonKeys[idx] = key;
         this._completeAccentedChars[idx] = this._tempCompleteAccBuffer.length() == 0 ? null : this._tempCompleteAccBuffer.toString();
         this._extendedAccentedChars[idx] = this._tempExtendedAccBuffer.length() == 0 ? null : this._tempExtendedAccBuffer.toString();
         this._cachedElementNo++;
      }

      this._getAccentsReturnContainer[0] = this._completeAccentedChars[idx];
      this._getAccentsReturnContainer[1] = this._extendedAccentedChars[idx];
      return this._getAccentsReturnContainer;
   }

   private boolean checkAlphabet(char ch, boolean genericAlphabet) {
      if (genericAlphabet && this._cachedLocaleAlphabetIndex != -1) {
         return this.LOCALE_ACCENTED_ALPHABET[this._cachedLocaleAlphabetIndex] != null
            ? Arrays.binarySearch((int[])this.LOCALE_ACCENTED_ALPHABET[this._cachedLocaleAlphabetIndex], ch) > -1
            : false;
      } else if (this._repository != null) {
         return genericAlphabet ? this._repository.isInBasicAlphabet(ch) : this._repository.isInExtendedAlphabet(ch);
      } else {
         return false;
      }
   }

   public static char lowerCaseToAccentless(char ch, Locale locale) {
      if (ch == 'i' && locale.getCode() != Locale.get("tr").getCode()) {
         return ch;
      }

      int less = _accentedUnaccentedMap.get(ch);
      return less == -1 ? ch : (char)less;
   }

   public static boolean isAccented(char ch, Locale locale) {
      return CharacterUtilities.isUpperCase(ch)
         ? lowerCaseToAccentless(CaseCorrector.toLowerCase(ch, locale), locale) != ch
         : lowerCaseToAccentless(ch, locale) != ch;
   }

   public static char toAccentless(char ch, Locale locale) {
      if (CharacterUtilities.isUpperCase(ch)) {
         ch = lowerCaseToAccentless(CaseCorrector.toLowerCase(ch, locale), locale);
         return CaseCorrector.toUpperCase(ch, locale);
      } else {
         return lowerCaseToAccentless(ch, locale);
      }
   }

   public char toAccentless(char ch) {
      return toAccentless(ch, this._locale);
   }

   public boolean areFromOneGroup(char aChar, char aAccentedChar, boolean aIgnoreCase) {
      if (aIgnoreCase) {
         aChar = CaseCorrector.toLowerCase(aChar, this._locale);
         aAccentedChar = CaseCorrector.toLowerCase(aAccentedChar, this._locale);
      }

      String accents = this.getAccents(aChar);
      return accents == null ? false : accents.indexOf(aAccentedChar) != -1;
   }

   private void reset() {
      this._cachedElementNo = 0;
   }

   @Override
   public void alphabetChanged() {
      this.reset();
   }

   static {
      int accentedCnt = 0;

      for (int i = 0; i < ACCENTED_CANDIDATES.length; i++) {
         accentedCnt += ACCENTED_CANDIDATES[i].length();
      }

      int k = 0;

      for (int i = 0; i < ACCENTED_CANDIDATES.length; i++) {
         String accents = ACCENTED_CANDIDATES[i];

         for (int j = 0; j < accents.length(); j++) {
            _accentedUnaccentedMap.put(accents.charAt(j), BASE_CHARS[i]);
         }
      }

      FINAL_FORMS = "ßς";
   }
}
