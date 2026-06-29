package net.rim.device.api.util;

import java.io.DataOutput;
import java.io.UTFDataFormatException;
import java.io.UnsupportedEncodingException;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.internal.i18n.CollatorImpl;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public final class StringUtilities {
   private static CollatorImpl _collator;
   private static WeakReference _scratchWR = new WeakReference(null);

   private StringUtilities() {
   }

   public static final native void convertToOriginal(StringBuffer var0, int var1, int var2);

   public static final native int compareToIgnoreCase(String var0, String var1);

   public static final native int compareToIgnoreCase(String var0, String var1, int var2);

   public static final native String toLowerCase(String var0, int var1);

   public static final native String toUpperCase(String var0, int var1);

   public static final native int computeHashCode(StringBuffer var0);

   public static final int hashCodeIgnoreCase(String str) {
      return hashCode(str, 0, Integer.MAX_VALUE, true);
   }

   public static final native int hashCode(String var0, int var1, int var2, boolean var3);

   public static final int computeReverseLookupHashCodeString(String string) {
      return computeReverseLookupHashCodeString(string, false);
   }

   public static final native int computeReverseLookupHashCodeString(String var0, boolean var1);

   public static final int computeReverseLookupHashCodeBytes(byte[] bytes, int start, int length) {
      return computeReverseLookupHashCodeBytes(bytes, start, length, false);
   }

   public static final native int computeReverseLookupHashCodeBytes(byte[] var0, int var1, int var2, boolean var3);

   public static final native int codeBOM(String var0, int var1, int var2, byte[] var3, int var4);

   public static final String decodeBOM(byte[] data, int offset, int len) {
      return decodeBOM(data, offset, len, false);
   }

   public static final String decodeBOM(byte[] data, int offset, int len, boolean stripNullLatin1) {
      String returnedString;
      if (len >= 2 && data[offset] == -2 && data[offset + 1] == -1) {
         try {
            returnedString = new String(data, offset + 2, len - 2, "utf-16be");
         } catch (UnsupportedEncodingException e) {
            returnedString = null;
         }
      } else if (len >= 3 && data[offset] == -17 && data[offset + 1] == -69 && data[offset + 2] == -65) {
         try {
            returnedString = new String(data, offset + 3, len - 3, "utf-8");
         } catch (UnsupportedEncodingException e) {
            returnedString = null;
         }
      } else {
         int end = offset + len;
         int byteEncoded = strConversionRequired(data, offset, end, '\u0080', ' ');
         if (stripNullLatin1 && len > 0 && data[offset + len - 1] == 0) {
            len--;
         }

         switch (byteEncoded) {
            case -1:
               char[] inputChars = new char[len];
               int out = 0;
               end = offset + len;
               char prevc = 0;
               char thisc = '\u0000';

               for (int lv = offset; lv < end; lv++) {
                  char c = (char)(data[lv] & 0xFF);
                  thisc = c;
                  switch (c) {
                     case '\n':
                        if (prevc == '\r') {
                           char var24 = false;
                           continue;
                        }
                        break;
                     case '\r':
                        c = '\n';
                        break;
                     case '\u0080':
                        c = 8364;
                        break;
                     case '\u0082':
                        c = 8218;
                        break;
                     case '\u0083':
                        c = 402;
                        break;
                     case '\u0084':
                        c = 8222;
                        break;
                     case '\u0085':
                        c = 8230;
                        break;
                     case '\u0086':
                        c = 8224;
                        break;
                     case '\u0087':
                        c = 8225;
                        break;
                     case '\u0088':
                        c = 710;
                        break;
                     case '\u0089':
                        c = 8240;
                        break;
                     case '\u008a':
                        c = 352;
                        break;
                     case '\u008b':
                        c = 8249;
                        break;
                     case '\u008c':
                        c = 338;
                        break;
                     case '\u008e':
                        c = 381;
                        break;
                     case '\u0091':
                        c = 8216;
                        break;
                     case '\u0092':
                        c = 8217;
                        break;
                     case '\u0093':
                        c = 8220;
                        break;
                     case '\u0094':
                        c = 8221;
                        break;
                     case '\u0095':
                        c = 8226;
                        break;
                     case '\u0096':
                        c = 8211;
                        break;
                     case '\u0097':
                        c = 8212;
                        break;
                     case '\u0098':
                        c = 732;
                        break;
                     case '\u0099':
                        c = 8482;
                        break;
                     case '\u009a':
                        c = 353;
                        break;
                     case '\u009b':
                        c = 8250;
                        break;
                     case '\u009c':
                        c = 339;
                        break;
                     case '\u009e':
                        c = 382;
                        break;
                     case '\u009f':
                        c = 376;
                  }

                  prevc = thisc;
                  inputChars[out] = c;
                  out++;
               }

               returnedString = new String(inputChars, 0, out);
               break;
            case 0:
            default:
               returnedString = new String(data, offset, len);
               break;
            case 1:
               byte[] inputChars = new byte[len];
               int out = 0;
               end = offset + len;
               byte prevc = 0;
               byte thisc = 0;

               for (int lv = offset; lv < end; lv++) {
                  byte c = (byte)(data[lv] & 0xFF);
                  thisc = c;
                  switch (c) {
                     case 10:
                        if (prevc == 13) {
                           byte var21 = false;
                           continue;
                        }
                        break;
                     case 13:
                        c = 10;
                  }

                  prevc = thisc;
                  inputChars[out] = c;
                  out++;
               }

               returnedString = new String(inputChars, 0, out);
         }
      }

      return returnedString;
   }

   public static final native int getCharacterSize(String var0);

   public static final native boolean isASCII(String var0);

   public static final String intToString(int code) {
      if (code == -1) {
         return null;
      }

      StringBuffer buffer = new StringBuffer();

      for (int lv = 24; lv >= 0; lv -= 8) {
         char ch = (char)(code >> lv & 0xFF);
         if (ch == 0) {
            if (code != 0) {
               throw new IllegalArgumentException();
            }
            break;
         }

         buffer.append(ch);
      }

      return buffer.toString();
   }

   public static final boolean startsWithIgnoreCase(String string, String prefix) {
      if (string == prefix) {
         return true;
      }

      int prefixLength = prefix.length();
      return string.length() < prefixLength ? false : string.regionMatches(true, 0, prefix, 0, prefixLength);
   }

   public static final boolean startsWithIgnoreCase(String string, String prefix, int locale) {
      if (string == prefix) {
         return true;
      }

      int prefixLength = prefix.length();
      return string.length() < prefixLength ? false : regionMatches(string, true, 0, prefix, 0, prefixLength, locale);
   }

   public static final boolean endsWithIgnoreCase(String string, String suffix, int locale) {
      if (string == suffix) {
         return true;
      }

      int suffixLength = suffix.length();
      return string.length() < suffixLength ? false : regionMatches(string, true, string.length() - suffixLength, suffix, 0, suffixLength, locale);
   }

   public static final boolean startsWithIgnoreCaseAndAccents(String string, String prefix) {
      if (_collator == null) {
         _collator = new CollatorImpl();
      }

      return _collator.compare(string, prefix, prefix.length()) == 0;
   }

   public static final String removeLineBreaksInString(String string) {
      if (string.indexOf(10) < 0) {
         return string;
      }

      int length = string.length();
      StringBuffer result = new StringBuffer();
      result.setLength(length);
      int dest = 0;
      boolean seenCR = true;

      for (int src = 0; src < length; src++) {
         char c = string.charAt(src);
         if (c == '\n') {
            if (!seenCR) {
               result.setCharAt(dest, ' ');
               dest++;
               seenCR = true;
            }
         } else {
            result.setCharAt(dest, c);
            dest++;
            seenCR = false;
         }
      }

      result.setLength(dest);
      return result.toString();
   }

   public static final String[] stringToWords(String string) {
      String[] theWords = new String[10];
      int count = stringToWords(string, theWords, 0);
      Array.resize(theWords, count);
      return theWords;
   }

   public static final String[] stringToKeywords(String string) {
      String[] theKeywords = new String[10];
      int count = stringToKeywords(string, theKeywords, 0);
      Array.resize(theKeywords, count);
      return theKeywords;
   }

   public static final int stringToWords(String stringContainingWords, int[] indexResults, int resultOffset) {
      return stringToWordsOrKeywords(stringContainingWords, indexResults, null, resultOffset, false);
   }

   public static final int stringToKeywords(String stringContainingKeywords, int[] startOffsets, int resultOffset) {
      return stringToWordsOrKeywords(stringContainingKeywords, startOffsets, null, resultOffset, true);
   }

   public static final native int stringToWordsOrKeywords(String var0, int[] var1, int[] var2, int var3, boolean var4);

   public static final int stringToWords(String string, String[] wordArray, int index) {
      return stringToWordsOrKeywords(string, wordArray, index, false);
   }

   public static final int stringToKeywords(String string, String[] wordArray, int index) {
      return stringToWordsOrKeywords(string, wordArray, index, true);
   }

   private static final int stringToWordsOrKeywords(String string, String[] wordArray, int index, boolean keywords) {
      if (string == null) {
         return 0;
      }

      int wordCount = 0;
      int length = string.length();
      int l = index + (length >> 1) + 1;
      int[] startOffsets = new int[(length >> 1) + 1];
      int[] endOffsets = new int[(length >> 1) + 1];
      if (l > wordArray.length) {
         Array.resize(wordArray, l);
      }

      try {
         wordCount = stringToWordsOrKeywords(string, startOffsets, endOffsets, 0, keywords);
      } catch (ArrayIndexOutOfBoundsException exc) {
         System.err.println("Internal error!");
      }

      for (int i = 0; i < wordCount; i++) {
         wordArray[index++] = string.substring(startOffsets[i], endOffsets[i]);
      }

      return wordCount;
   }

   public static final boolean strEqualIgnoreCase(String s1, String s2) {
      if (s1 == s2) {
         return true;
      } else {
         return s1 == null || s2 == null || s1.length() != s2.length() ? false : compareToIgnoreCase(s1, s2) == 0;
      }
   }

   public static final boolean strEqualIgnoreCase(String s1, String s2, int locale) {
      if (s1 == s2) {
         return true;
      } else {
         return s1 == null || s2 == null || s1.length() != s2.length() ? false : compareToIgnoreCase(s1, s2, locale) == 0;
      }
   }

   public static final boolean strEqual(String s1, String s2) {
      if (s1 == s2) {
         return true;
      } else {
         return s1 != null && s2 != null ? s1.equals(s2) : false;
      }
   }

   public static final String cStr2String(byte[] b, int start, int len) {
      int i = len;

      while (i > 0 && b[i + start - 1] == 0) {
         i--;
      }

      return new String(b, start, i);
   }

   public static final int compareObjectToStringIgnoreCase(Object o1, Object o2) {
      if (o1 == o2) {
         return 0;
      }

      if (o1 == null && o2 != null) {
         return -1;
      }

      if (o2 == null) {
         return 1;
      }

      String s1 = o1.toString();
      String s2 = o2.toString();
      return compareToIgnoreCase(s1, s2);
   }

   public static final int compareObjectToStringIgnoreCase(Object o1, Object o2, int locale) {
      if (o1 == o2) {
         return 0;
      }

      if (o1 == null && o2 != null) {
         return -1;
      }

      if (o2 == null) {
         return 1;
      }

      String s1 = o1.toString();
      String s2 = o2.toString();
      return compareToIgnoreCase(s1, s2, locale);
   }

   public static final int indexOf(String string, char ch, int fromIndex, int toIndex) {
      return indexOf(string, ch & 65535, fromIndex, toIndex);
   }

   public static final native int indexOf(String var0, int var1, int var2, int var3);

   public static final String removeChars(String src, String remove) {
      boolean changed = false;
      String result = src;
      int length = src.length();
      StringBuffer buffer = WeakReferenceUtilities.getStringBuffer(_scratchWR);
      synchronized (buffer) {
         for (int lv = 0; lv < length; lv++) {
            char character = src.charAt(lv);
            if (remove.indexOf(character) == -1) {
               buffer.append(character);
            } else {
               changed = true;
            }
         }

         if (changed) {
            result = buffer.toString();
         }

         buffer.setLength(0);
         return result;
      }
   }

   public static final long stringHashToLong(String key) {
      SHA1Digest digest = new SHA1Digest();
      digest.update(key.getBytes());
      byte[] hashValBytes = digest.getDigest();
      long hashValLong = 0;

      for (int i = 0; i < 8; i++) {
         hashValLong |= (hashValBytes[i] & 255) << 8 * i;
      }

      return hashValLong;
   }

   public static final int stringToInt(String text) {
      int result = 0;
      int shift = 24;
      int length = text.length();
      if (length > 4) {
         return -1;
      }

      for (int lv = 0; lv < length; lv++) {
         char ch = text.charAt(lv);
         if (ch > 255) {
            return -1;
         }

         result |= text.charAt(lv) << shift;
         shift -= 8;
      }

      return result;
   }

   public static final StringBuffer append(StringBuffer strBuf, String str, int offset, int length) {
      synchronized (strBuf) {
         if (offset >= 0 && length >= 0 && offset + length <= str.length()) {
            doAppend(strBuf, str, offset, length);
            return strBuf;
         } else {
            throw new StringIndexOutOfBoundsException();
         }
      }
   }

   private static final native void doAppend(StringBuffer var0, String var1, int var2, int var3);

   public static final StringBuffer append(StringBuffer strBuf, byte[] buffer, int offset, int length) {
      synchronized (strBuf) {
         if (offset >= 0 && length >= 0 && offset + length <= buffer.length) {
            doAppend(strBuf, buffer, offset, length);
            return strBuf;
         } else {
            throw new ArrayIndexOutOfBoundsException();
         }
      }
   }

   private static final native void doAppend(StringBuffer var0, byte[] var1, int var2, int var3);

   public static final StringBuffer append(StringBuffer strBuf, StringBuffer other) {
      synchronized (other) {
         return append(strBuf, other, 0, other.length());
      }
   }

   public static final StringBuffer append(StringBuffer strBuf, StringBuffer other, int offset, int length) {
      synchronized (other) {
         synchronized (strBuf) {
            if (offset < 0 || length < 0 || offset + length > other.length()) {
               throw new ArrayIndexOutOfBoundsException();
            }

            doAppend(strBuf, other, offset, length);
         }

         return strBuf;
      }
   }

   private static final native void doAppend(StringBuffer var0, StringBuffer var1, int var2, int var3);

   private static final native int strConversionRequired(byte[] var0, int var1, int var2, char var3, char var4);

   public static final int writeUTF(String str, DataOutput out) {
      int strlen = str.length();
      int utflen = 0;
      char[] charr = new char[strlen];
      int count = 0;
      str.getChars(0, strlen, charr, 0);

      for (int i = 0; i < strlen; i++) {
         int c = charr[i];
         if (c >= 1 && c <= 127) {
            utflen++;
         } else if (c > 2047) {
            utflen += 3;
         } else {
            utflen += 2;
         }
      }

      if (utflen > 65535) {
         throw new UTFDataFormatException();
      }

      byte[] bytearr = new byte[utflen + 2];
      bytearr[count++] = (byte)(utflen >>> 8 & 0xFF);
      bytearr[count++] = (byte)(utflen >>> 0 & 0xFF);

      for (int i = 0; i < strlen; i++) {
         int c = charr[i];
         if (c >= 1 && c <= 127) {
            bytearr[count++] = (byte)c;
         } else if (c > 2047) {
            bytearr[count++] = (byte)(224 | c >> 12 & 15);
            bytearr[count++] = (byte)(128 | c >> 6 & 63);
            bytearr[count++] = (byte)(128 | c >> 0 & 63);
         } else {
            bytearr[count++] = (byte)(192 | c >> 6 & 31);
            bytearr[count++] = (byte)(128 | c >> 0 & 63);
         }
      }

      out.write(bytearr);
      return utflen + 2;
   }

   public static final native boolean regionMatches(String var0, boolean var1, int var2, String var3, int var4, int var5, int var6);

   public static final native boolean isHan(String var0, int var1, int var2);
}
