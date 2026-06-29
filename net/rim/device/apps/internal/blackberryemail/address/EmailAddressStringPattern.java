package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPattern$Match;

public final class EmailAddressStringPattern extends StringPattern {
   private boolean _hasParameters = false;
   private char[] MAILTO_PREFIX_LOWERCASE = new char[]{'m', 'a', 'i', 'l', 't', 'o', ':', 'Ā', '潈', '᭬', '\u000b', '戁', 'ꉯ', 'd'};
   private char[] MAILTO_PREFIX_UPPERCASE = new char[]{'M', 'A', 'I', 'L', 'T', 'O', ':', '\u0000', '\u0007', '퀆', 'm', 'a', 'i', 'l'};
   private final int MAILTO_PREFIX_LEN = this.MAILTO_PREFIX_LOWERCASE.length;
   private int _beginIndex;
   private int _endIndex;
   private int _nextIndex;
   private boolean _hasMailtoPrefix;

   @Override
   public final long getPatternTypeIdentifier() {
      return this._hasParameters ? 8245590029279666536L : -2985347935260258684L;
   }

   @Override
   public final synchronized boolean findMatch(AbstractString str, int minIndex, int maxIndex, StringPattern$Match match) {
      if (str == null) {
         return false;
      }

      int index = minIndex;

      while (index < maxIndex) {
         int atSymbolIndex = str.indexOf('@', index, maxIndex);
         if (atSymbolIndex >= index && atSymbolIndex < maxIndex) {
            this.delimitEmailAddress(str, atSymbolIndex, minIndex, maxIndex);
            if ((this._beginIndex >= atSymbolIndex || this._beginIndex < minIndex || this._endIndex - atSymbolIndex <= 2) && !this._hasMailtoPrefix) {
               index = this._nextIndex;
               continue;
            }

            match.beginIndex = this._beginIndex;
            if (this._hasMailtoPrefix) {
               match.prefixLength = 7;
               this._beginIndex += 7;
            } else {
               match.prefixLength = 0;
            }

            int questionMarkIndex = str.indexOf('?', index, maxIndex);
            if ((this._beginIndex < questionMarkIndex && questionMarkIndex == this._endIndex || this._beginIndex == questionMarkIndex)
               && questionMarkIndex + 1 < maxIndex) {
               this._hasParameters = true;
               match.endIndex = this.getEndIndex(str, questionMarkIndex + 1, maxIndex);
            } else {
               this._hasParameters = false;
               match.endIndex = this._endIndex;
            }

            if (this._hasParameters) {
               match.id = 8245590029279666536L;
               return true;
            }

            match.id = -2985347935260258684L;
            return true;
         }

         return false;
      }

      return false;
   }

   private final int getEndIndex(AbstractString str, int startIndex, int endIndex) {
      if (!CharacterUtilities.isLetter(str.charAt(startIndex))) {
         return startIndex - 1;
      }

      for (int i = startIndex; i < endIndex; i++) {
         switch (str.charAt(i)) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
            case '"':
               return i;
         }
      }

      return endIndex;
   }

   private final void delimitEmailAddress(AbstractString str, int atSymbolIndex, int minIndex, int maxIndex) {
      this._beginIndex = atSymbolIndex;
      this._endIndex = atSymbolIndex + 1;
      this._nextIndex = atSymbolIndex + 1;
      this.findBeginAddressIndex(str, atSymbolIndex);
      if (this._beginIndex >= minIndex && this._beginIndex < atSymbolIndex) {
         this.findEndAddressIndex(str, atSymbolIndex, maxIndex);
      }
   }

   private final void findBeginAddressIndex(AbstractString str, int atSymbolIndex) {
      int tmpIndex = atSymbolIndex - 1;

      for (this._hasMailtoPrefix = false; tmpIndex >= 0; tmpIndex--) {
         char c = str.charAt(tmpIndex);
         if (StringPattern.isWhitespace(c)) {
            return;
         }

         switch (c) {
            case ' ':
            case '"':
            case '(':
            case ')':
            case ',':
            case ';':
            case '<':
            case '>':
            case '[':
            case ']':
            case '|':
               if (tmpIndex != 0 && str.charAt(tmpIndex - 1) != '\\') {
                  return;
               }
            case '!':
            case '\'':
            case '.':
            case '?':
            case '{':
            case '}':
               break;
            case '/':
               this._beginIndex = atSymbolIndex;
               break;
            case ':':
               if (tmpIndex != 0 && str.charAt(tmpIndex - 1) != '\\') {
                  if (this.hasMailtoPrefix(str, tmpIndex)) {
                     this._beginIndex = tmpIndex + 1 - this.MAILTO_PREFIX_LEN;
                     this._hasMailtoPrefix = true;
                  }

                  return;
               }
               break;
            case '@':
               if (tmpIndex != 0 && str.charAt(tmpIndex - 1) != '\\') {
                  this._beginIndex = atSymbolIndex;
                  return;
               }
               break;
            default:
               this._beginIndex = tmpIndex;
         }
      }
   }

   private final void findEndAddressIndex(AbstractString str, int atSymbolIndex, int maxIndex) {
      int tmpIndex = atSymbolIndex + 1;
      int tmpHashval = 0;
      int domainHashval = 0;

      while (true) {
         label35: {
            if (tmpIndex < maxIndex) {
               char c = str.charAt(tmpIndex);
               switch (c) {
                  case '\t':
                  case '\n':
                  case '\r':
                  case ' ':
                  case '"':
                  case '(':
                  case ')':
                  case ',':
                  case ';':
                  case '<':
                  case '>':
                  case '?':
                  case '[':
                  case ']':
                  case '{':
                  case '|':
                  case '}':
                     break;
                  case '!':
                  case '\'':
                     tmpHashval = tmpHashval * 31 + c;
                     break label35;
                  case '.':
                     tmpHashval = 0;
                     break label35;
                  case '@':
                     this._endIndex = atSymbolIndex + 1;
                     this._nextIndex = tmpIndex;
                     return;
                  case 'A':
                  case 'B':
                  case 'C':
                  case 'D':
                  case 'E':
                  case 'F':
                  case 'G':
                  case 'H':
                  case 'I':
                  case 'J':
                  case 'K':
                  case 'L':
                  case 'M':
                  case 'N':
                  case 'O':
                  case 'P':
                  case 'Q':
                  case 'R':
                  case 'S':
                  case 'T':
                  case 'U':
                  case 'V':
                  case 'W':
                  case 'X':
                  case 'Y':
                  case 'Z':
                     c = (char)(c + ' ');
                  default:
                     tmpHashval = tmpHashval * 31 + c;
                     domainHashval = tmpHashval;
                     this._endIndex = tmpIndex + 1;
                     break label35;
                  case '\\':
                     if (++tmpIndex < maxIndex) {
                        c = str.charAt(tmpIndex);
                        tmpHashval = tmpHashval * 31 + c;
                        domainHashval = tmpHashval;
                        this._endIndex = tmpIndex + 1;
                     }
                     break label35;
               }
            }

            if (!isValidDomainHash(domainHashval)) {
               this._endIndex = atSymbolIndex + 1;
            }

            this._nextIndex = tmpIndex;
            return;
         }

         tmpIndex++;
      }
   }

   private static final boolean isValidDomainHash(int hashVal) {
      switch (hashVal) {
         case -1062811118:
         case -865698022:
         case 97555:
         case 98262:
         case 98689:
         case 100278:
         case 102542:
         case 104431:
         case 108112:
         case 108957:
         case 110308:
         case 111277:
         case 114715:
         case 119160:
         case 2990433:
         case 3002850:
         case 3003594:
         case 3059533:
         case 3237038:
         case 3267670:
         case 3343799:
         case 3357033:
         case 3373707:
         case 3373934:
         case 3446944:
            return true;
         default:
            switch (hashVal) {
               case 3106:
               case 3107:
               case 3108:
               case 3109:
               case 3110:
               case 3112:
               case 3115:
               case 3116:
               case 3117:
               case 3118:
               case 3120:
               case 3121:
               case 3122:
               case 3123:
               case 3124:
               case 3126:
               case 3127:
               case 3129:
               case 3135:
               case 3136:
               case 3138:
               case 3139:
               case 3140:
               case 3141:
               case 3142:
               case 3143:
               case 3144:
               case 3147:
               case 3148:
               case 3149:
               case 3152:
               case 3153:
               case 3154:
               case 3156:
               case 3157:
               case 3159:
               case 3160:
               case 3166:
               case 3168:
               case 3169:
               case 3171:
               case 3172:
               case 3173:
               case 3174:
               case 3176:
               case 3177:
               case 3178:
               case 3179:
               case 3180:
               case 3183:
               case 3184:
               case 3186:
               case 3187:
               case 3189:
               case 3190:
               case 3191:
               case 3201:
               case 3206:
               case 3207:
               case 3209:
               case 3211:
               case 3222:
               case 3230:
               case 3232:
               case 3234:
               case 3235:
               case 3245:
               case 3246:
               case 3247:
               case 3248:
               case 3267:
               case 3268:
               case 3269:
               case 3271:
               case 3273:
               case 3276:
               case 3282:
               case 3290:
               case 3291:
               case 3293:
               case 3294:
               case 3295:
               case 3296:
               case 3297:
               case 3298:
               case 3301:
               case 3302:
               case 3303:
               case 3305:
               case 3306:
               case 3307:
               case 3308:
               case 3309:
               case 3310:
               case 3312:
               case 3314:
               case 3331:
               case 3333:
               case 3334:
               case 3338:
               case 3340:
               case 3341:
               case 3355:
               case 3356:
               case 3363:
               case 3364:
               case 3365:
               case 3366:
               case 3368:
               case 3369:
               case 3370:
               case 3371:
               case 3387:
               case 3395:
               case 3397:
               case 3398:
               case 3418:
               case 3420:
               case 3421:
               case 3422:
               case 3426:
               case 3427:
               case 3429:
               case 3431:
               case 3436:
               case 3438:
               case 3439:
               case 3445:
               case 3446:
               case 3447:
               case 3453:
               case 3455:
               case 3462:
               case 3463:
               case 3464:
               case 3465:
               case 3466:
               case 3469:
               case 3476:
               case 3478:
               case 3479:
               case 3482:
               case 3483:
               case 3486:
               case 3487:
               case 3488:
               case 3489:
               case 3490:
               case 3491:
               case 3492:
               case 3493:
               case 3494:
               case 3495:
               case 3497:
               case 3498:
               case 3499:
               case 3500:
               case 3501:
               case 3507:
               case 3509:
               case 3511:
               case 3512:
               case 3513:
               case 3515:
               case 3518:
               case 3521:
               case 3522:
               case 3524:
               case 3526:
               case 3527:
               case 3532:
               case 3550:
               case 3569:
               case 3573:
               case 3574:
               case 3575:
               case 3576:
               case 3579:
               case 3580:
               case 3581:
               case 3582:
               case 3586:
               case 3587:
               case 3588:
               case 3591:
               case 3593:
               case 3600:
               case 3635:
               case 3645:
               case 3651:
               case 3653:
               case 3662:
               case 3663:
               case 3664:
               case 3665:
               case 3666:
               case 3668:
               case 3669:
               case 3670:
               case 3671:
               case 3672:
               case 3673:
               case 3674:
               case 3675:
               case 3676:
               case 3679:
               case 3681:
               case 3682:
               case 3683:
               case 3686:
               case 3687:
               case 3695:
               case 3696:
               case 3698:
               case 3699:
               case 3700:
               case 3702:
               case 3703:
               case 3705:
               case 3706:
               case 3707:
               case 3708:
               case 3710:
               case 3712:
               case 3714:
               case 3715:
               case 3718:
               case 3724:
               case 3730:
               case 3734:
               case 3736:
               case 3742:
               case 3748:
               case 3749:
               case 3755:
               case 3757:
               case 3759:
               case 3761:
               case 3763:
               case 3768:
               case 3775:
               case 3791:
               case 3804:
               case 3852:
               case 3867:
               case 3868:
               case 3879:
               case 3891:
               case 3896:
               case 3901:
                  return true;
               default:
                  return false;
            }
      }
   }

   private final boolean hasMailtoPrefix(AbstractString str, int colonIndex) {
      int beginIndex = colonIndex + 1 - this.MAILTO_PREFIX_LEN;
      if (beginIndex < 0) {
         return false;
      }

      for (int testIndex = this.MAILTO_PREFIX_LEN - 1; testIndex >= 0; testIndex--) {
         char c = str.charAt(beginIndex + testIndex);
         if (c != this.MAILTO_PREFIX_UPPERCASE[testIndex] && c != this.MAILTO_PREFIX_LOWERCASE[testIndex]) {
            return false;
         }
      }

      if (beginIndex > 0) {
         char c = str.charAt(beginIndex - 1);
         switch (c) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
            case '!':
            case '"':
            case '#':
            case '$':
            case '&':
            case '\'':
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '{':
            case '|':
            case '}':
            case '~':
               break;
            default:
               return false;
         }
      }

      return true;
   }
}
