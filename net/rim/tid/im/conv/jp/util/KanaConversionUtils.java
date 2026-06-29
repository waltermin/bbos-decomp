package net.rim.tid.im.conv.jp.util;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CharacterUtilities;

public class KanaConversionUtils implements ISLJpConst {
   protected static final char[] hira2UNICODE = new char[]{
      'ぁ',
      'あ',
      'ぃ',
      'い',
      'ぅ',
      'う',
      'ぇ',
      'え',
      'ぉ',
      'お',
      'か',
      'が',
      'き',
      'ぎ',
      'く',
      'ぐ',
      'け',
      'げ',
      'こ',
      'ご',
      'さ',
      'ざ',
      'し',
      'じ',
      'す',
      'ず',
      'せ',
      'ぜ',
      'そ',
      'ぞ',
      'た',
      'だ',
      'ち',
      'ぢ',
      'っ',
      'つ',
      'づ',
      'て',
      'で',
      'と',
      'ど',
      'な',
      'に',
      'ぬ',
      'ね',
      'の',
      'は',
      'ば',
      'ぱ',
      'ひ',
      'び',
      'ぴ',
      'ふ',
      'ぶ',
      'ぷ',
      'へ',
      'べ',
      'ぺ',
      'ほ',
      'ぼ',
      'ぽ',
      'ま',
      'み',
      'む',
      'め',
      'も',
      'ゃ',
      'や',
      'ゅ',
      'ゆ',
      'ょ',
      'よ',
      'ら',
      'り',
      'る',
      'れ',
      'ろ',
      'ゎ',
      'わ',
      'ゐ',
      'ゑ',
      'を',
      'ん',
      'ヴ',
      'ヵ',
      'ヶ',
      'ふ',
      '\u0000',
      '\u0000',
      '\u0000',
      '\u0000',
      'ー',
      '。',
      '、',
      '＇',
      '＂',
      '・',
      '「',
      '」',
      '！',
      '＃',
      '＄',
      '（',
      '）',
      '＊',
      '＋',
      '：',
      '；',
      '？',
      '＠',
      '＿',
      '￡',
      '￥',
      '　',
      'r',
      '퀆',
      'ｧ',
      'ｱ',
      'ｨ',
      'ｲ',
      'ｩ',
      'ｳ',
      'ｪ',
      'ｴ',
      'ｫ',
      'ｵ',
      'ｶ',
      '\u0000',
      'ｷ',
      '\u0000',
      'ｸ',
      '\u0000',
      'ｹ',
      '\u0000',
      'ｺ',
      '\u0000',
      'ｻ',
      '\u0000',
      'ｼ',
      '\u0000',
      'ｽ',
      '\u0000',
      'ｾ',
      '\u0000',
      'ｿ',
      '\uff00',
      'ﾀ',
      '\u0000',
      'ﾁ',
      '\u0000',
      'ｯ',
      'ﾂ',
      '\u0000',
      'ﾃ',
      '\u0000',
      'ﾄ',
      '\u0000',
      'ﾅ',
      'ﾆ',
      'ﾇ',
      'ﾈ',
      'ﾉ',
      'ﾊ',
      '\u0000',
      '\u0000',
      'ﾋ',
      '\u0000',
      '\u0000',
      'ﾌ',
      '\u0000',
      '\u0000',
      'ﾍ',
      '\u0000',
      '\u0000',
      'ﾎ',
      '\u0000',
      '\u0000',
      'ﾏ',
      'ﾐ',
      'ﾑ',
      'ﾒ',
      'ﾓ',
      'ｬ',
      'ﾔ',
      'ｭ',
      'ﾕ',
      'ｮ',
      'ﾖ',
      'ﾗ',
      'ﾘ',
      'ﾙ',
      'ﾚ',
      'ﾛ',
      'ヮ',
      'ﾜ',
      'ヰ',
      'ヱ',
      'ｦ',
      'ﾝ',
      '\u0000',
      'ヵ',
      'ヶ',
      'ﾌ',
      '\u0000',
      '\u0000',
      '\u0000',
      '\u0000',
      'ｰ',
      '｡',
      '､',
      '\'',
      '"',
      '･',
      '｢',
      '｣',
      '!',
      '#',
      '$',
      '(',
      ')',
      '*',
      '+',
      ':',
      ';',
      '?',
      '@',
      '_',
      '£'
   };
   protected static final char[] hiraToKatakanaHalf = new char[]{
      'ｧ',
      'ｱ',
      'ｨ',
      'ｲ',
      'ｩ',
      'ｳ',
      'ｪ',
      'ｴ',
      'ｫ',
      'ｵ',
      'ｶ',
      '\u0000',
      'ｷ',
      '\u0000',
      'ｸ',
      '\u0000',
      'ｹ',
      '\u0000',
      'ｺ',
      '\u0000',
      'ｻ',
      '\u0000',
      'ｼ',
      '\u0000',
      'ｽ',
      '\u0000',
      'ｾ',
      '\u0000',
      'ｿ',
      '\uff00',
      'ﾀ',
      '\u0000',
      'ﾁ',
      '\u0000',
      'ｯ',
      'ﾂ',
      '\u0000',
      'ﾃ',
      '\u0000',
      'ﾄ',
      '\u0000',
      'ﾅ',
      'ﾆ',
      'ﾇ',
      'ﾈ',
      'ﾉ',
      'ﾊ',
      '\u0000',
      '\u0000',
      'ﾋ',
      '\u0000',
      '\u0000',
      'ﾌ',
      '\u0000',
      '\u0000',
      'ﾍ',
      '\u0000',
      '\u0000',
      'ﾎ',
      '\u0000',
      '\u0000',
      'ﾏ',
      'ﾐ',
      'ﾑ',
      'ﾒ',
      'ﾓ',
      'ｬ',
      'ﾔ',
      'ｭ',
      'ﾕ',
      'ｮ',
      'ﾖ',
      'ﾗ',
      'ﾘ',
      'ﾙ',
      'ﾚ',
      'ﾛ',
      'ヮ',
      'ﾜ',
      'ヰ',
      'ヱ',
      'ｦ',
      'ﾝ',
      '\u0000',
      'ヵ',
      'ヶ',
      'ﾌ',
      '\u0000',
      '\u0000',
      '\u0000',
      '\u0000',
      'ｰ',
      '｡',
      '､',
      '\'',
      '"',
      '･',
      '｢',
      '｣',
      '!',
      '#',
      '$',
      '(',
      ')',
      '*',
      '+',
      ':',
      ';',
      '?',
      '@',
      '_',
      '£',
      '¥',
      ' ',
      '\u0001',
      '퀊',
      '\uffff',
      '\uffff',
      'Ā',
      'ℛ',
      '楴',
      '䶂',
      '䑉',
      'ɬ',
      'Ā',
      '潈',
      '᭬',
      '\u000b',
      '瀁',
      '䴿',
      '栂',
      '咽',
      '룗',
      '⎌',
      'Ā',
      '㽰',
      '潍',
      '렆',
      '⎌',
      'Ā',
      '⩳',
      'Ѐ',
      'Ѐ',
      '敌',
      '湀',
      '垄',
      '氯',
      '\u009e',
      '圄',
      '氯',
      '\u009e',
      '圄',
      '氯',
      'ꂞ',
      '\ue661',
      'Ѐ',
      'Ὡ',
      'ì',
      '昆',
      '攁',
      '\u0600',
      'ɬ',
      'e',
      '氆',
      '攂',
      '쑁',
      '၎',
      '捓',
      '쐠',
      '뾄',
      's',
      '氆',
      '攂',
      '\uf852',
      '\u0600',
      'ɬ',
      '坥',
      '/',
      '氆',
      '攂',
      '⽗',
      '͗',
      '䕨',
      '孮',
      '葤',
      '\u0600',
      '璭',
      '芹',
      '\u0600',
      '璭',
      '芹',
      '灓',
      '沋',
      '⊸',
      '䥫',
      'M',
      '☈',
      '瑀',
      'º',
      '☈',
      '\u0082',
      '⠈',
      'ℛ',
      '\u0007',
      '⠈',
      'ℛ',
      '〇',
      'ࠀ',
      '瀨',
      '䌿',
      'ힼ',
      'ࠀ',
      '瀨',
      '䴿',
      '栂',
      '½',
      '⠈',
      '㽰',
      'ɍ',
      '뵨',
      '뱃',
      '氠',
      '扏',
      '썪',
      'ࠀ',
      '瀨',
      '䴿',
      '栂'
   };
   protected static final int[] hiraganaNigori = new int[]{
      12,
      14,
      16,
      18,
      20,
      22,
      24,
      26,
      28,
      30,
      32,
      34,
      37,
      39,
      41,
      48,
      51,
      54,
      57,
      60,
      84,
      -804651003,
      49,
      52,
      55,
      58,
      61,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      -804913038,
      809644097,
      809775171,
      809906245,
      810037319,
      810168393,
      810299467,
      810430541,
      810561615,
      810692689,
      810823763,
      810954837,
      811085911,
      811216985,
      811348059,
      811479133,
      811610207,
      811741281,
      811872355,
      812003429,
      812134503,
      812265577,
      812396651,
      812527725,
      812658799,
      812789873,
      812920947,
      813052021,
      813183095,
      813314169,
      813445243,
      813576317,
      813707391,
      813838465,
      813969539,
      814100613,
      814231687,
      814362761,
      814493835,
      814624909,
      814755983,
      814887057,
      821309587,
      821440757,
      12405,
      0,
      821821440,
      805384194,
      -16580857,
      806105339,
      -16699379,
      -16449789
   };
   protected static final int[] hiraganaMaru = new int[]{
      49,
      52,
      55,
      58,
      61,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      -804913038,
      809644097,
      809775171,
      809906245,
      810037319,
      810168393,
      810299467,
      810430541,
      810561615,
      810692689
   };

   public static int kanaToHalfWidth(StringBuffer input, int inputBegin, int inputLength, StringBuffer output) {
      return kanaToHalfWidth(input, inputBegin, inputLength, output, false);
   }

   public static int kanaToHalfWidth(StringBuffer input, int inputBegin, int inputLength, StringBuffer output, boolean skipUnconverted) {
      return kanaToHalfWidth(input, inputBegin, inputLength, output, null, null, skipUnconverted);
   }

   public static int kanaToHalfWidth(
      StringBuffer input,
      int inputBegin,
      int inputLength,
      StringBuffer output,
      KanaConversionUtils$ConversionFilter preConversionFltr,
      KanaConversionUtils$ConversionFilter postConversionFltr,
      boolean skipUnconverted
   ) {
      if (input != null && input.length() > inputBegin && output != null) {
         int len = input.length();
         if (inputBegin + inputLength < len) {
            len = inputBegin + inputLength;
         }

         int totalConverted = 0;

         for (int i = inputBegin; i < len; i++) {
            char ch = input.charAt(i);
            if (preConversionFltr == null || preConversionFltr.accept(ch)) {
               int index;
               if (12353 <= ch && ch <= 12435) {
                  index = ch - 12352;
               } else if (12449 <= ch && ch <= 12531) {
                  index = ch - 12448;
               } else if (ch == 12532) {
                  index = 84;
               } else if (ch == 12539) {
                  index = 97;
               } else {
                  if (ch != 12540) {
                     if ((postConversionFltr == null || postConversionFltr.accept(ch)) && !skipUnconverted) {
                        output.append(ch);
                     }
                     continue;
                  }

                  index = 92;
               }

               char accent = 0;
               if (Arrays.getIndex(hiraganaNigori, index) >= 0) {
                  if (index == 84) {
                     index = 6;
                  } else {
                     index--;
                  }

                  accent = 'ﾞ';
               } else if (Arrays.getIndex(hiraganaMaru, index) >= 0) {
                  index -= 2;
                  accent = 'ﾟ';
               }

               char result = hiraToKatakanaHalf[index - 1];
               if (result < '｡') {
                  if ((postConversionFltr == null || postConversionFltr.accept(ch)) && !skipUnconverted) {
                     output.append(ch);
                  }
               } else if (postConversionFltr == null || postConversionFltr.accept(result)) {
                  totalConverted++;
                  output.append(result);
                  if (accent != 0) {
                     output.append(accent);
                  }
               }
            }
         }

         return totalConverted;
      } else {
         return 0;
      }
   }

   public static void halfWidthToHiragana(StringBuffer input, StringBuffer output) {
      int len = input.length();
      int lenTable = hiraToKatakanaHalf.length;

      for (int i = 0; i < len; i++) {
         char ch = input.charAt(i);
         int index = 0;

         while (index < lenTable && ch != hiraToKatakanaHalf[index]) {
            index++;
         }

         if (index == lenTable) {
            if (ch == 'ﾞ') {
               ch = '゛';
            }

            if (ch == 'ﾟ') {
               ch = '゜';
            }

            output.append(ch);
         } else {
            ch = hira2UNICODE[index];
            index++;
            char next = 0;
            if (i + 1 < len) {
               next = input.charAt(i + 1);
            }

            if (next == 'ﾞ') {
               if (index == 6) {
                  output.append('ヴ');
                  i++;
                  continue;
               }

               if (Arrays.getIndex(hiraganaNigori, ++index) >= 0) {
                  output.append((char)(ch + 1));
                  i++;
                  continue;
               }
            } else if (next == 'ﾟ') {
               index += 2;
               if (Arrays.getIndex(hiraganaMaru, index) >= 0) {
                  output.append((char)(ch + 2));
                  i++;
                  continue;
               }
            }

            output.append(ch);
         }
      }
   }

   public static boolean isHiragana(char ch) {
      return ch >= 12353 && ch <= 12446 || ch == 12540;
   }

   public static String composeAdjustedSearchPatternForJapanese(String pattern) {
      StringBuffer compositeSearchPattern = new StringBuffer();
      boolean hasHanSymbols = false;

      for (int i = 0; i < pattern.length(); i++) {
         char ch = pattern.charAt(i);
         hasHanSymbols |= CharacterUtilities.isHan(ch);
         compositeSearchPattern.append(ch);
      }

      if (hasHanSymbols) {
         int lengthBefore = compositeSearchPattern.length();
         int converted = kanaToHalfWidth(compositeSearchPattern, 0, compositeSearchPattern.length(), compositeSearchPattern);
         if (converted != lengthBefore) {
            compositeSearchPattern.setLength(lengthBefore);
         } else {
            compositeSearchPattern.delete(0, lengthBefore);
         }
      }

      return compositeSearchPattern.toString();
   }
}
