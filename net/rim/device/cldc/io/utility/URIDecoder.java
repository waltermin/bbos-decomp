package net.rim.device.cldc.io.utility;

import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringMatch;
import net.rim.device.api.util.StringUtilities;

public final class URIDecoder {
   private static final String UTF_8 = "utf-8";
   private static final String ISO_8859_1 = "iso-8859-1";
   private static StringMatch DECODER_MATCH_PLUS = new StringMatch(new String[]{"%", "+"}, false, false);
   private static StringMatch DECODER_MATCH = new StringMatch(new String[]{"%"}, false, false);

   public static final String decode(String str, String encoding) {
      return decode(str, encoding, true);
   }

   public static final String decode(String str, String encoding, boolean decodePlusAsSpace) {
      return decode(str, encoding, decodePlusAsSpace, false);
   }

   public static final String decode(String str, String encoding, boolean decodePlusAsSpace, boolean returnNullOnError) {
      int length = str.length();
      if (length == 0) {
         return str;
      }

      StringMatch match;
      if (decodePlusAsSpace) {
         match = DECODER_MATCH_PLUS;
      } else {
         match = DECODER_MATCH;
      }

      synchronized (match) {
         int oldPos = 0;
         int patternIndex = match.indexOf(str);
         if (patternIndex == -1) {
            return str;
         }

         try {
            StringBuffer newStr = new StringBuffer(length);

            while (patternIndex >= 0) {
               StringUtilities.append(newStr, str, oldPos, patternIndex - oldPos);
               switch (match.getLastMatchedPattern()) {
                  case -1:
                     break;
                  case 0:
                     if (!StringUtilities.strEqualIgnoreCase(encoding, "utf-8")) {
                        int h4 = NumberUtilities.hexDigitToInt(str.charAt(++patternIndex));
                        int h0 = NumberUtilities.hexDigitToInt(str.charAt(++patternIndex));
                        newStr.append((char)(h4 << 4 | h0));
                     } else {
                        int h4 = NumberUtilities.hexDigitToInt(str.charAt(++patternIndex));
                        int h0 = NumberUtilities.hexDigitToInt(str.charAt(++patternIndex));
                        int c0 = h4 << 4 | h0;
                        if (c0 == -1) {
                           if (returnNullOnError) {
                              return null;
                           }

                           newStr.append("-1");
                        } else {
                           char newCharacter = '\u0000';
                           c0 &= 255;
                           switch (c0 >> 4) {
                              case -1:
                              case 8:
                              case 9:
                              case 10:
                              case 11:
                                 if (returnNullOnError) {
                                    return null;
                                 }

                                 newCharacter = '?';
                                 break;
                              case 0:
                              case 1:
                              case 2:
                              case 3:
                              case 4:
                              case 5:
                              case 6:
                              case 7:
                              default:
                                 newCharacter = (char)c0;
                                 break;
                              case 12:
                              case 13:
                                 if (str.charAt(patternIndex + 1) != '%') {
                                    if (returnNullOnError) {
                                       return null;
                                    }

                                    newCharacter = '?';
                                 } else {
                                    patternIndex++;
                                    h4 = NumberUtilities.hexDigitToInt(str.charAt(++patternIndex));
                                    h0 = NumberUtilities.hexDigitToInt(str.charAt(++patternIndex));
                                    int c1 = h4 << 4 | h0;
                                    if ((c1 & 192) != 128) {
                                       if (returnNullOnError) {
                                          return null;
                                       }

                                       newCharacter = '?';
                                    } else {
                                       newCharacter = (char)((c0 & 31) << 6 | c1 & 63);
                                    }
                                 }
                                 break;
                              case 14:
                                 if (str.charAt(patternIndex + 1) != '%') {
                                    if (returnNullOnError) {
                                       return null;
                                    }

                                    newCharacter = '?';
                                 } else {
                                    patternIndex++;
                                    h4 = NumberUtilities.hexDigitToInt(str.charAt(++patternIndex));
                                    h0 = NumberUtilities.hexDigitToInt(str.charAt(++patternIndex));
                                    int c1 = h4 << 4 | h0;
                                    if ((c1 & 192) != 128) {
                                       if (returnNullOnError) {
                                          return null;
                                       }

                                       newCharacter = '?';
                                    } else if (str.charAt(patternIndex + 1) != '%') {
                                       if (returnNullOnError) {
                                          return null;
                                       }

                                       newCharacter = '?';
                                    } else {
                                       patternIndex++;
                                       h4 = NumberUtilities.hexDigitToInt(str.charAt(++patternIndex));
                                       h0 = NumberUtilities.hexDigitToInt(str.charAt(++patternIndex));
                                       int c2 = h4 << 4 | h0;
                                       if ((c2 & 192) != 128) {
                                          if (returnNullOnError) {
                                             return null;
                                          }

                                          newCharacter = '?';
                                       } else {
                                          newCharacter = (char)((c0 & 15) << 12 | (c1 & 63) << 6 | (c2 & 63) << 0);
                                       }
                                    }
                                 }
                           }

                           newStr.append(newCharacter);
                        }
                     }
                     break;
                  case 1:
                  default:
                     newStr.append(' ');
               }

               oldPos = ++patternIndex;
               if (patternIndex >= length) {
                  break;
               }

               patternIndex = match.indexOf(str, patternIndex);
            }

            StringUtilities.append(newStr, str, oldPos, length - oldPos);
            return newStr.toString();
         } catch (NumberFormatException e) {
            if (returnNullOnError) {
               return null;
            }
         } catch (StringIndexOutOfBoundsException sioobe) {
            if (returnNullOnError) {
               return null;
            }
         }

         return str;
      }
   }
}
