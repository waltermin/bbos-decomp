package net.rim.device.cldc.io.utility;

import com.sun.cldc.i18n.Helper;
import java.io.UnsupportedEncodingException;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Memory;

public final class URIEncoder {
   private static final String UTF_8 = "utf-8";
   private static final String ISO_8859_1 = "iso-8859-1";

   public static final String encodeBlanks(String str) {
      int length = str.length();
      int startIndex;
      if (length != 0 && (startIndex = str.indexOf(32)) != -1) {
         StringBuffer buff = new StringBuffer(length * 2);
         StringUtilities.append(buff, str, 0, startIndex);

         for (int i = startIndex; i < length; i++) {
            char ch = str.charAt(i);
            if (ch == ' ') {
               buff.append('%');
               buff.append('2');
               buff.append('0');
            } else {
               buff.append(ch);
            }
         }

         return buff.toString();
      } else {
         return str;
      }
   }

   private static final boolean fixEntityBeforeChar(char ch) {
      return (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') && (ch < '0' || ch > '9');
   }

   public static final String encodeNonUSASCII(String str, boolean useHTMLBrowserCompatibility) {
      int length = str.length();
      if (length != 0 && (!Memory.isStringAllBytes(str) || str.indexOf(32) != -1)) {
         StringBuffer buff = new StringBuffer(length * 2);

         for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            if (ch <= 127) {
               if (ch == ' ') {
                  buff.append('%');
                  buff.append('2');
                  buff.append('0');
               } else {
                  buff.append(ch);
               }
            } else if (useHTMLBrowserCompatibility && i + 1 < length && fixEntityBeforeChar(str.charAt(i + 1))) {
               switch (ch) {
                  case 'Œ':
                     buff.append("&OElig");
                     break;
                  case 'œ':
                     buff.append("&oelig");
                     break;
                  case 'Š':
                     buff.append("&Scaron");
                     break;
                  case 'š':
                     buff.append("&scaron");
                     break;
                  case 'Ÿ':
                     buff.append("&Yuml");
                     break;
                  case 'ƒ':
                     buff.append("&fnof");
                     break;
                  case 'ˆ':
                     buff.append("&circ");
                     break;
                  case '˜':
                     buff.append("&tilde");
                     break;
                  case 'Α':
                     buff.append("&Alpha");
                     break;
                  case 'Β':
                     buff.append("&Beta");
                     break;
                  case 'Γ':
                     buff.append("&Gamma");
                     break;
                  case 'Δ':
                     buff.append("&Delta");
                     break;
                  case 'Ε':
                     buff.append("&Epsilon");
                     break;
                  case 'Ζ':
                     buff.append("&Zeta");
                     break;
                  case 'Η':
                     buff.append("&Eta");
                     break;
                  case 'Θ':
                     buff.append("&Theta");
                     break;
                  case 'Ι':
                     buff.append("&Iota");
                     break;
                  case 'Κ':
                     buff.append("&Kappa");
                     break;
                  case 'Λ':
                     buff.append("&Lambda");
                     break;
                  case 'Μ':
                     buff.append("&Mu");
                     break;
                  case 'Ν':
                     buff.append("&Nu");
                     break;
                  case 'Ξ':
                     buff.append("&Xi");
                     break;
                  case 'Ο':
                     buff.append("&Omicron");
                     break;
                  case 'Π':
                     buff.append("&Pi");
                     break;
                  case 'Ρ':
                     buff.append("&Rho");
                     break;
                  case 'Σ':
                     buff.append("&Sigma");
                     break;
                  case 'Τ':
                     buff.append("&Tau");
                     break;
                  case 'Υ':
                     buff.append("&Upsilon");
                     break;
                  case 'Φ':
                     buff.append("&Phi");
                     break;
                  case 'Χ':
                     buff.append("&Chi");
                     break;
                  case 'Ψ':
                     buff.append("&Psi");
                     break;
                  case 'Ω':
                     buff.append("&Omega");
                     break;
                  case 'α':
                     buff.append("&alpha");
                     break;
                  case 'β':
                     buff.append("&beta");
                     break;
                  case 'γ':
                     buff.append("&gamma");
                     break;
                  case 'δ':
                     buff.append("&delta");
                     break;
                  case 'ε':
                     buff.append("&epsilon");
                     break;
                  case 'ζ':
                     buff.append("&zeta");
                     break;
                  case 'η':
                     buff.append("&eta");
                     break;
                  case 'θ':
                     buff.append("&theta");
                     break;
                  case 'ι':
                     buff.append("&iota");
                     break;
                  case 'κ':
                     buff.append("&kappa");
                     break;
                  case 'λ':
                     buff.append("&lambda");
                     break;
                  case 'μ':
                     buff.append("&mu");
                     break;
                  case 'ν':
                     buff.append("&nu");
                     break;
                  case 'ξ':
                     buff.append("&xi");
                     break;
                  case 'ο':
                     buff.append("&omicron");
                     break;
                  case 'π':
                     buff.append("&pi");
                     break;
                  case 'ρ':
                     buff.append("&rho");
                     break;
                  case 'ς':
                     buff.append("&sigmaf");
                     break;
                  case 'σ':
                     buff.append("&sigma");
                     break;
                  case 'τ':
                     buff.append("&tau");
                     break;
                  case 'υ':
                     buff.append("&upsilon");
                     break;
                  case 'φ':
                     buff.append("&phi");
                     break;
                  case 'χ':
                     buff.append("&chi");
                     break;
                  case 'ψ':
                     buff.append("&psi");
                     break;
                  case 'ω':
                     buff.append("&omega");
                     break;
                  case 'ϑ':
                     buff.append("&thetasym");
                     break;
                  case 'ϒ':
                     buff.append("&upsih");
                     break;
                  case 'ϖ':
                     buff.append("&piv");
                     break;
                  case ' ':
                     buff.append("&ensp");
                     break;
                  case ' ':
                     buff.append("&emsp");
                     break;
                  case ' ':
                     buff.append("&thinsp");
                     break;
                  case '\u200c':
                     buff.append("&zwnj");
                     break;
                  case '\u200d':
                     buff.append("&zwj");
                     break;
                  case '\u200e':
                     buff.append("&lrm");
                     break;
                  case '\u200f':
                     buff.append("&rlm");
                     break;
                  case '–':
                     buff.append("&ndash");
                     break;
                  case '—':
                     buff.append("&mdash");
                     break;
                  case '‘':
                     buff.append("&lsquo");
                     break;
                  case '’':
                     buff.append("&rsquo");
                     break;
                  case '‚':
                     buff.append("&sbquo");
                     break;
                  case '“':
                     buff.append("&ldquo");
                     break;
                  case '”':
                     buff.append("&rdquo");
                     break;
                  case '„':
                     buff.append("&bdquo");
                     break;
                  case '†':
                     buff.append("&dagger");
                     break;
                  case '‡':
                     buff.append("&Dagger");
                     break;
                  case '•':
                     buff.append("&bull");
                     break;
                  case '…':
                     buff.append("&hellip");
                     break;
                  case '‰':
                     buff.append("&permil");
                     break;
                  case '′':
                     buff.append("&prime");
                     break;
                  case '″':
                     buff.append("&Prime");
                     break;
                  case '‹':
                     buff.append("&lsaquo");
                     break;
                  case '›':
                     buff.append("&rsaquo");
                     break;
                  case '‾':
                     buff.append("&oline");
                     break;
                  case '⁄':
                     buff.append("&frasl");
                     break;
                  case '€':
                     buff.append("&euro");
                     break;
                  case 'ℑ':
                     buff.append("&image");
                     break;
                  case '℘':
                     buff.append("&weierp");
                     break;
                  case 'ℜ':
                     buff.append("&real");
                     break;
                  case '™':
                     buff.append("&trade");
                     break;
                  case 'ℵ':
                     buff.append("&alefsym");
                     break;
                  case '←':
                     buff.append("&larr");
                     break;
                  case '↑':
                     buff.append("&uarr");
                     break;
                  case '→':
                     buff.append("&rarr");
                     break;
                  case '↓':
                     buff.append("&darr");
                     break;
                  case '↔':
                     buff.append("&harr");
                     break;
                  case '↵':
                     buff.append("&crarr");
                     break;
                  case '⇐':
                     buff.append("&lArr");
                     break;
                  case '⇑':
                     buff.append("&uArr");
                     break;
                  case '⇒':
                     buff.append("&rArr");
                     break;
                  case '⇓':
                     buff.append("&dArr");
                     break;
                  case '⇔':
                     buff.append("&hArr");
                     break;
                  case '∀':
                     buff.append("&forall");
                     break;
                  case '∂':
                     buff.append("&part");
                     break;
                  case '∃':
                     buff.append("&exist");
                     break;
                  case '∅':
                     buff.append("&empty");
                     break;
                  case '∇':
                     buff.append("&nabla");
                     break;
                  case '∈':
                     buff.append("&isin");
                     break;
                  case '∉':
                     buff.append("&notin");
                     break;
                  case '∋':
                     buff.append("&ni");
                     break;
                  case '∏':
                     buff.append("&prod");
                     break;
                  case '∑':
                     buff.append("&sum");
                     break;
                  case '−':
                     buff.append("&minus");
                     break;
                  case '∗':
                     buff.append("&lowast");
                     break;
                  case '√':
                     buff.append("&radic");
                     break;
                  case '∝':
                     buff.append("&prop");
                     break;
                  case '∞':
                     buff.append("&infin");
                     break;
                  case '∠':
                     buff.append("&ang");
                     break;
                  case '∧':
                     buff.append("&and");
                     break;
                  case '∨':
                     buff.append("&or");
                     break;
                  case '∩':
                     buff.append("&cap");
                     break;
                  case '∪':
                     buff.append("&cup");
                     break;
                  case '∫':
                     buff.append("&int");
                     break;
                  case '∴':
                     buff.append("&there4");
                     break;
                  case '∼':
                     buff.append("&sim");
                     break;
                  case '≅':
                     buff.append("&cong");
                     break;
                  case '≈':
                     buff.append("&asymp");
                     break;
                  case '≠':
                     buff.append("&ne");
                     break;
                  case '≡':
                     buff.append("&equiv");
                     break;
                  case '≤':
                     buff.append("&le");
                     break;
                  case '≥':
                     buff.append("&ge");
                     break;
                  case '⊂':
                     buff.append("&sub");
                     break;
                  case '⊃':
                     buff.append("&sup");
                     break;
                  case '⊄':
                     buff.append("&nsub");
                     break;
                  case '⊆':
                     buff.append("&sube");
                     break;
                  case '⊇':
                     buff.append("&supe");
                     break;
                  case '⊕':
                     buff.append("&oplus");
                     break;
                  case '⊗':
                     buff.append("&otimes");
                     break;
                  case '⊥':
                     buff.append("&perp");
                     break;
                  case '⋅':
                     buff.append("&sdot");
                     break;
                  case '⌈':
                     buff.append("&lceil");
                     break;
                  case '⌉':
                     buff.append("&rceil");
                     break;
                  case '⌊':
                     buff.append("&lfloor");
                     break;
                  case '⌋':
                     buff.append("&rfloor");
                     break;
                  case '〈':
                     buff.append("&lang");
                     break;
                  case '〉':
                     buff.append("&rang");
                     break;
                  case '◊':
                     buff.append("&loz");
                     break;
                  case '♠':
                     buff.append("&spades");
                     break;
                  case '♣':
                     buff.append("&clubs");
                     break;
                  case '♥':
                     buff.append("&hearts");
                     break;
                  case '♦':
                     buff.append("&diams");
                     break;
                  default:
                     writeUTF8Char(buff, ch);
               }
            } else {
               writeUTF8Char(buff, ch);
            }
         }

         return buff.toString();
      } else {
         return str;
      }
   }

   public static final String encode(StringBuffer outputStringBuffer, String str, String encoding, boolean encodeBlanks) {
      int length = str.length();
      if (length == 0) {
         return str;
      }

      boolean needsChange = false;
      boolean postData = true;
      if (outputStringBuffer == null) {
         outputStringBuffer = new StringBuffer();
         postData = false;
      }

      for (int i = 0; i < length; i++) {
         char character = str.charAt(i);
         switch (character) {
            case '\n':
               outputStringBuffer.append("%0D%0A");
               needsChange = true;
               break;
            case ' ':
               if (encodeBlanks) {
                  outputStringBuffer.append("%20");
               } else {
                  outputStringBuffer.append('+');
               }

               needsChange = true;
               break;
            case '*':
            case '-':
            case '.':
            case '@':
            case '_':
               outputStringBuffer.append(character);
               needsChange = true;
               break;
            default:
               if ((character < '0' || character > '9') && (character < 'A' || character > 'Z') && (character < 'a' || character > 'z')) {
                  needsChange = true;
                  handleSpecialCharacter(outputStringBuffer, character, encoding);
               } else {
                  outputStringBuffer.append(character);
               }
         }
      }

      if (!postData) {
         return outputStringBuffer.toString();
      } else {
         return needsChange ? outputStringBuffer.toString() : str;
      }
   }

   private static final void handleSpecialCharacter(StringBuffer outputStringBuffer, char character, String encoding) {
      if (encoding != null && StringUtilities.strEqualIgnoreCase(encoding, "utf-8", 1701707776)) {
         writeUTF8Char(outputStringBuffer, character);
      } else if (encoding != null && !StringUtilities.strEqualIgnoreCase(encoding, "iso-8859-1", 1701707776)) {
         char[] charArray = new char[]{character};
         byte[] bytes = null;

         try {
            bytes = Helper.charToByteArray(charArray, 0, 1, encoding);
         } catch (UnsupportedEncodingException e) {
            handleSpecialCharacter(outputStringBuffer, character, null);
         }

         if (bytes != null) {
            for (int i = 0; i < bytes.length; i++) {
               writeByte(outputStringBuffer, bytes[i]);
            }
         }
      } else {
         writeByte(outputStringBuffer, unicodeToWin1252(character));
      }
   }

   private static final int unicodeToWin1252(char character) {
      if (character <= 255) {
         return character;
      }

      switch (character) {
         case 'Œ':
            return 140;
         case 'œ':
            return 156;
         case 'Š':
            return 138;
         case 'š':
            return 154;
         case 'Ÿ':
            return 159;
         case 'Ž':
            return 142;
         case 'ž':
            return 158;
         case 'ƒ':
            return 131;
         case 'ˆ':
            return 136;
         case '˜':
            return 152;
         case '–':
            return 150;
         case '—':
            return 151;
         case '‘':
            return 145;
         case '’':
            return 146;
         case '‚':
            return 130;
         case '“':
            return 147;
         case '”':
            return 148;
         case '„':
            return 132;
         case '†':
            return 134;
         case '‡':
            return 135;
         case '•':
            return 149;
         case '…':
            return 133;
         case '‰':
            return 137;
         case '‹':
            return 139;
         case '›':
            return 155;
         case '€':
            return 128;
         case '™':
            return 153;
         default:
            return character;
      }
   }

   private static final void writeByte(StringBuffer outputStringBuffer, int aByte) {
      outputStringBuffer.append('%');
      outputStringBuffer.append(NumberUtilities.intToUpperHexDigit(aByte >> 4));
      outputStringBuffer.append(NumberUtilities.intToUpperHexDigit(aByte));
   }

   public static final void writeUTF8Char(StringBuffer outputStringBuffer, int character) {
      if (character <= 127) {
         writeByte(outputStringBuffer, character);
      } else if (character <= 2047) {
         int intToConvert = 192 | character >> 6;
         writeByte(outputStringBuffer, intToConvert);
         intToConvert = 128 | character & 63;
         writeByte(outputStringBuffer, intToConvert);
      } else if (character <= 65535) {
         int intToConvert = 224 | character >> 12;
         writeByte(outputStringBuffer, intToConvert);
         intToConvert = 128 | character >> 6 & 63;
         writeByte(outputStringBuffer, intToConvert);
         intToConvert = 128 | character & 63;
         writeByte(outputStringBuffer, intToConvert);
      } else {
         int u = (character >> 16) + 1;
         int intToConvert = 240 | u >> 2;
         writeByte(outputStringBuffer, intToConvert);
         intToConvert = 128 | (u & 3) << 4 | character >> 12 & 15;
         writeByte(outputStringBuffer, intToConvert);
         intToConvert = 128 | character >> 6 & 63;
         writeByte(outputStringBuffer, intToConvert);
         intToConvert = 128 | character & 63;
         writeByte(outputStringBuffer, intToConvert);
      }
   }
}
