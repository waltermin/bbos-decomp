package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.device.api.util.CharacterUtilities;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.util.Utils;

public class EnglishMetaphone implements IMetaphone {
   private SpellCheckVariantsCreator _creator;
   private char[] _tempBuffer = new char[150];
   private char[] _tempBuffer1 = new char[150];
   private char[] _tempBuffer2 = new char[50];
   public static String META_VOWELS = "AEIOU";
   private static String FRONTV = "EIY";
   private static String VARSON = "CSPTG";
   private static String DOUBLE = ".";
   private static String EXCP_PAIR = "AGKPW";
   private static String NEXT_LTR = "ENNNR";
   private static String VOWELS = "aeiouy";
   private static String VOWEL_WILDCARD = "\u0002" + VOWELS + '\u0003' + '\u0001';
   private static final int VOWEL_WILDCARD_LEN = VOWEL_WILDCARD.length();
   private static String VOWELS1 = "aeiou";
   private static String VOWEL1_WILDCARD = "\u0002" + VOWELS1 + '\u0003' + '\u0001';
   private static final int VOWEL1_WILDCARD_LEN = VOWEL1_WILDCARD.length();
   private static String METAPHONE_ALPHABET = "BCFHJKLMNPRSTWXY0\u0001";
   private static String[] METAPHONE_ALPHABET_WILDCARDS = new String[]{
      "b\u0002b\u0003\u0001",
      "cc",
      "\u0005f\u0010v\u0010ph\u0006\u0002vf\u0003\u0001",
      "h",
      "\u0005g\u0010j\u0010dg\u0006\u0002g\u0003\u0001",
      "\u0005k\u0010g\u0010c\u0010q\u0006\u0002cgkqh\u0003\u0001",
      "l\u0002l\u0003\u0001",
      "m\u0002m\u0003\u0001",
      "n\u0002n\u0003\u0001",
      "p\u0002p\u0003\u0001",
      "r\u0002r\u0003\u0001",
      "\u0005s\u0010z\u0010c\u0006\u0002sz\u0003\u0001",
      "\u0005t\u0010d\u0006\u0002td\u0003\u0001",
      "w",
      "\u0005ssia\u0010ssio\u0010sia\u0010sio\u0010sh\u0010cia\u0010ch\u0010tia\u0010tio\u0010tch\u0006",
      "y",
      "th",
      "\u0002bcdfghjklmnpqrstvwxyz\u0003"
   };
   private static String FIRST_METAPHONE_ALPHABET = "AEIOUBCFHJKLMNPRSTWXY0\u0001";
   private static String[][] FIRST_METAPHONE_REPLACEMENTS = new String[][]{
      {"a", "o", "u"},
      {"e", "ae", "i", "a"},
      {"i", "e"},
      {"o"},
      {"u", "a", "o"},
      {"b"},
      new String[0],
      {"f", "v", "ph"},
      {"h"},
      {"g", "j"},
      {"k", "g", "c", "q"},
      {"l"},
      {"m"},
      {"n", "gn", "kn", "pn"},
      {"p"},
      {"r", "wr"},
      {"s", "z", "c"},
      {"t", "d"},
      {"w", "wh"},
      {"sh", "ch", "tch"},
      {"y"},
      {"th"},
      {"\u0001"}
   };
   private static String[][] SECOND_METAPHONE_REPLACEMENTS = new String[][]{
      {"b\u0002b\u0003\u0001"},
      {"cc"},
      {"f\u0002f\u0003\u0001", "v", "ph"},
      {"h"},
      {"g", "j", "dg\u0002eiy\u0003"},
      {"k", "g\u0002g\u0003\u0001", "c\u0002ckq\u0003\u0001", "q"},
      {"l\u0002l\u0003\u0001"},
      {"m\u0002m\u0003\u0001"},
      {"n\u0002n\u0003\u0001"},
      {"p\u0002p\u0003\u0001"},
      {"r\u0002r\u0003\u0001"},
      {"s\u0002s\u0003\u0001", "z\u0002z\u0003\u0001", "c\u0002c\u0003\u0001"},
      {"t\u0002t\u0003\u0001", "d\u0002d\u0003\u0001"},
      {"w"},
      {"sh", "ch", "tch", "sia"},
      {"y"},
      {"th"},
      {"b", "c", "d", "f", "g", "h", "g", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "y", "z"}
   };
   private static String SILENT_WH_WILDCARD = "\u0002w\u0003\u0001";

   public EnglishMetaphone(SpellCheckVariantsCreator aCreator) {
      this._creator = aCreator;
   }

   @Override
   public void getMetaphoneKey(SLCurrentVariant aWord, SLCurrentVariant aMetaphone) {
      int len = aWord._length;
      int j = 0;

      for (int i = 0; i < len; i++) {
         char ch = aWord._variants[aWord._offset + i];
         if (CharacterUtilities.isLetter(ch)) {
            this._tempBuffer2[j] = Utils.toUpperCase(ch);
            j++;
         }
      }

      len = j;
      if (len != 0) {
         int id = EXCP_PAIR.indexOf(this._tempBuffer2[0]);
         if (id != -1 && NEXT_LTR.charAt(id) == this._tempBuffer2[1]) {
            System.arraycopy(this._tempBuffer2, 1, this._tempBuffer2, 0, --len);
         }

         if (this._tempBuffer2[0] == 'X') {
            this._tempBuffer2[0] = 'S';
         }

         if (len > 1 && this._tempBuffer2[0] == 'W' && this._tempBuffer2[1] == 'H') {
            len--;
            System.arraycopy(this._tempBuffer2, 2, this._tempBuffer2, 1, len - 1);
         }

         int lastChr = len - 1;
         char[] metaph = aMetaphone._variants;
         int metaphone_len = 0;

         for (int i = 0; i < len; i++) {
            char curLtr = this._tempBuffer2[i];
            char prevLtr = ' ';
            boolean vowelBefore = false;
            if (i > 0) {
               prevLtr = this._tempBuffer2[i - 1];
               if (META_VOWELS.indexOf(prevLtr) != -1) {
                  vowelBefore = true;
               }
            }

            if (i == 0 && META_VOWELS.indexOf(curLtr) != -1) {
               metaph[metaphone_len++] = curLtr;
            } else {
               boolean vowelAfter = false;
               boolean frontvAfter = false;
               char nextLtr = ' ';
               if (i < lastChr) {
                  nextLtr = this._tempBuffer2[i + 1];
                  if (META_VOWELS.indexOf(nextLtr) != -1) {
                     vowelAfter = true;
                  }

                  if (FRONTV.indexOf(nextLtr) != -1) {
                     frontvAfter = true;
                  }
               }

               if (curLtr != nextLtr || DOUBLE.indexOf(nextLtr) != -1) {
                  char nextLtr2 = ' ';
                  if (i < lastChr - 1) {
                     nextLtr2 = this._tempBuffer2[i + 2];
                  }

                  char nextLtr3 = ' ';
                  if (i < lastChr - 2) {
                     nextLtr3 = this._tempBuffer2[i + 3];
                  }

                  switch (curLtr) {
                     case 'A':
                     case 'E':
                     case 'I':
                     case 'O':
                     case 'U':
                        break;
                     case 'B':
                     default:
                        boolean silent = false;
                        if (i == lastChr && prevLtr == 'M') {
                           silent = true;
                        }

                        if (!silent) {
                           metaph[metaphone_len++] = curLtr;
                        }
                        break;
                     case 'C':
                        if (i <= 1 || prevLtr != 'S' || !frontvAfter) {
                           if (i > 0 && nextLtr == 'I' && nextLtr2 == 'A') {
                              metaph[metaphone_len++] = 'X';
                           } else if (frontvAfter) {
                              metaph[metaphone_len++] = 'S';
                           } else if (i > 1 && prevLtr == 'S' && nextLtr == 'H') {
                              metaph[metaphone_len++] = 'K';
                           } else if (nextLtr == 'H') {
                              if (i == 0 && META_VOWELS.indexOf(nextLtr2) == -1) {
                                 metaph[metaphone_len++] = 'K';
                              } else {
                                 metaph[metaphone_len++] = 'X';
                              }
                           } else if (prevLtr == 'C') {
                              metaph[metaphone_len++] = 'C';
                           } else {
                              metaph[metaphone_len++] = 'K';
                           }
                        }
                        break;
                     case 'D':
                        if (nextLtr == 'G' && FRONTV.indexOf(nextLtr2) != -1) {
                           metaph[metaphone_len++] = 'J';
                        } else {
                           metaph[metaphone_len++] = 'T';
                        }
                        break;
                     case 'F':
                     case 'J':
                     case 'L':
                     case 'M':
                     case 'N':
                     case 'R':
                        metaph[metaphone_len++] = curLtr;
                        break;
                     case 'G':
                        boolean silent = false;
                        if (i < lastChr - 1 && nextLtr == 'H' && META_VOWELS.indexOf(nextLtr2) == -1) {
                           silent = true;
                        }

                        if (i == lastChr - 3 && nextLtr == 'N' && nextLtr2 == 'E' && nextLtr3 == 'D') {
                           silent = true;
                        } else if (i == lastChr - 1 && nextLtr == 'N') {
                           silent = true;
                        }

                        if (prevLtr == 'D' && frontvAfter) {
                           silent = true;
                        }

                        boolean hard = prevLtr == 'G';
                        if (!silent) {
                           if (frontvAfter && !hard) {
                              metaph[metaphone_len++] = 'J';
                           } else {
                              metaph[metaphone_len++] = 'K';
                           }
                        }
                        break;
                     case 'H':
                        boolean silent = false;
                        if (VARSON.indexOf(prevLtr) != -1) {
                           silent = true;
                        }

                        if (vowelBefore && !vowelAfter) {
                           silent = true;
                        }

                        if (!silent) {
                           metaph[metaphone_len++] = curLtr;
                        }
                        break;
                     case 'K':
                        if (prevLtr != 'C') {
                           metaph[metaphone_len++] = curLtr;
                        }
                        break;
                     case 'P':
                        if (nextLtr == 'H') {
                           metaph[metaphone_len++] = 'F';
                        } else {
                           metaph[metaphone_len++] = 'P';
                        }
                        break;
                     case 'Q':
                        metaph[metaphone_len++] = 'K';
                        break;
                     case 'S':
                        if (i <= 1 || nextLtr != 'I' || nextLtr2 != 'O' && nextLtr2 != 'A') {
                           if (nextLtr == 'H') {
                              metaph[metaphone_len++] = 'X';
                           } else {
                              metaph[metaphone_len++] = 'S';
                           }
                        } else {
                           metaph[metaphone_len++] = 'X';
                        }
                        break;
                     case 'T':
                        if (i <= 1 || nextLtr != 'I' || nextLtr2 != 'O' && nextLtr2 != 'A') {
                           if (nextLtr == 'H') {
                              if ((nextLtr2 == 'O' || nextLtr2 == 'A') && nextLtr3 == 'M') {
                                 metaph[metaphone_len++] = 'T';
                              } else {
                                 metaph[metaphone_len++] = '0';
                              }
                           } else if (i >= lastChr - 2 || nextLtr != 'C' || nextLtr2 != 'H') {
                              metaph[metaphone_len++] = 'T';
                           }
                        } else {
                           metaph[metaphone_len++] = 'X';
                        }
                        break;
                     case 'V':
                        metaph[metaphone_len++] = 'F';
                        break;
                     case 'W':
                     case 'Y':
                        if (i < lastChr && vowelAfter) {
                           metaph[metaphone_len++] = curLtr;
                        }
                        break;
                     case 'X':
                        metaph[metaphone_len++] = 'K';
                        metaph[metaphone_len++] = 'S';
                        break;
                     case 'Z':
                        metaph[metaphone_len++] = 'S';
                  }
               }
            }
         }

         aMetaphone._length = metaphone_len;
      }
   }

   @Override
   public void getVariants(char[] aMetaphone, int aMetaLen, SpellCheckResultContainer aRes) {
      int search_len = 0;
      if (aMetaLen >= 1 && aMetaLen <= 10) {
         int len = 30 * aMetaLen;
         if (len > this._tempBuffer.length) {
            this._tempBuffer = new char[len];
            this._tempBuffer1 = new char[len];
         }

         int id = FIRST_METAPHONE_ALPHABET.indexOf(aMetaphone[0]);
         if (id == -1) {
            throw new IllegalStateException("");
         }

         String[] replacements = FIRST_METAPHONE_REPLACEMENTS[id];
         MetaphoneRegExprIterator iterator = this._creator.getMetaphoneRegExprIterator();

         for (int i = 0; i < replacements.length; i++) {
            search_len = replacements[i].length();
            replacements[i].getChars(0, search_len, this._tempBuffer, 0);
            if (search_len != 1) {
               search_len = this.constructRegExpr(aMetaphone, 1, aMetaLen, this._tempBuffer, search_len);
               iterator.init(this._tempBuffer, 0, search_len);
               this._creator.getVariants(iterator, aRes);
            } else {
               String vowels;
               if (aMetaphone[1] != 'Y' && aMetaphone[1] != 1) {
                  vowels = VOWELS;
               } else {
                  vowels = VOWELS1;
               }

               search_len = this.constructRegExpr(aMetaphone, 1, aMetaLen, this._tempBuffer, search_len + 1);

               for (int j = 0; j < vowels.length(); j++) {
                  this._tempBuffer[1] = VOWELS.charAt(j);
                  iterator.init(this._tempBuffer, 0, search_len);
                  this._creator.getVariants(iterator, aRes);
               }

               if (aMetaLen > 1) {
                  id = METAPHONE_ALPHABET.indexOf(aMetaphone[1]);
                  if (id == -1) {
                     throw new IllegalStateException("");
                  }

                  String[] replacements2 = SECOND_METAPHONE_REPLACEMENTS[id];
                  int regexp_len = this.constructRegExpr(aMetaphone, 2, aMetaLen, this._tempBuffer1, 0);

                  for (int j = 0; j < replacements2.length; j++) {
                     search_len = replacements2[j].length();
                     replacements2[j].getChars(0, search_len, this._tempBuffer, 1);
                     System.arraycopy(this._tempBuffer1, 0, this._tempBuffer, ++search_len, regexp_len);
                     search_len += regexp_len;
                     iterator.init(this._tempBuffer, 0, search_len);
                     this._creator.getVariants(iterator, aRes);
                  }

                  if (aMetaLen > 2 && aMetaphone[1] == 'K' && aMetaphone[2] == 'S') {
                     this._tempBuffer[1] = 'x';
                     regexp_len = this.constructRegExpr(aMetaphone, 3, aMetaLen, this._tempBuffer, 2);
                     iterator.init(this._tempBuffer, 0, regexp_len);
                     this._creator.getVariants(iterator, aRes);
                  }
               }
            }
         }
      }
   }

   private int constructRegExpr(char[] aMetaphone, int aMetaStart, int aMetaLen, char[] aRegExpBuffer, int aRegExpStart) {
      int search_len = aRegExpStart;
      String vowel_wildcard;
      int vowel_len;
      if (aMetaphone[aMetaStart] != 'Y' && aMetaphone[aMetaStart] != 1) {
         vowel_wildcard = VOWEL_WILDCARD;
         vowel_len = VOWEL_WILDCARD_LEN;
      } else {
         vowel_wildcard = VOWEL1_WILDCARD;
         vowel_len = VOWEL1_WILDCARD_LEN;
      }

      vowel_wildcard.getChars(0, vowel_len, aRegExpBuffer, search_len);
      search_len += vowel_len;
      int silent_wh_len = SILENT_WH_WILDCARD.length();
      if (aMetaphone[aMetaStart] != 'W' && aMetaphone[aMetaStart] != 1) {
         SILENT_WH_WILDCARD.getChars(0, silent_wh_len, aRegExpBuffer, search_len);
         search_len += silent_wh_len;
      }

      for (int i = aMetaStart; i < aMetaLen; i++) {
         int id = METAPHONE_ALPHABET.indexOf(aMetaphone[i]);
         if (id == -1) {
            throw new IllegalStateException("");
         }

         String cons_wildcard = METAPHONE_ALPHABET_WILDCARDS[id];
         int cons_len = cons_wildcard.length();
         cons_wildcard.getChars(0, cons_len, aRegExpBuffer, search_len);
         search_len += cons_len;
         if (i >= aMetaLen - 1 || aMetaphone[i + 1] != 'Y' && aMetaphone[i + 1] != 1) {
            vowel_wildcard = VOWEL_WILDCARD;
            vowel_len = VOWEL_WILDCARD_LEN;
         } else {
            vowel_wildcard = VOWEL1_WILDCARD;
            vowel_len = VOWEL1_WILDCARD_LEN;
         }

         vowel_wildcard.getChars(0, vowel_len, aRegExpBuffer, search_len);
         search_len += vowel_len;
         if (i >= aMetaLen - 1 || aMetaphone[i + 1] != 'W' && aMetaphone[i + 1] != 1) {
            SILENT_WH_WILDCARD.getChars(0, silent_wh_len, aRegExpBuffer, search_len);
            search_len += silent_wh_len;
         }
      }

      return search_len;
   }

   @Override
   public String getMetaVowels() {
      return META_VOWELS;
   }
}
