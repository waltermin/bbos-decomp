package net.rim.device.apps.internal.browser.util;

import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.StringPatternEnumerator;
import net.rim.device.api.util.StringPatternRepository$Internal;

public final class LinkType {
   private static Object _syncObject = new Object();
   private static StringPatternEnumerator _stringEnum;

   private LinkType() {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final long getLinkType(String href) {
      long linkType = 5019899335844518230L;
      boolean doMatch = true;
      int length = href.length();
      if (length > 0) {
         char char0 = CharacterUtilities.toLowerCase(href.charAt(0), 1701707776);
         if (char0 == '/') {
            doMatch = false;
         } else if (length >= 6) {
            char char1 = CharacterUtilities.toLowerCase(href.charAt(1), 1701707776);
            char char2 = CharacterUtilities.toLowerCase(href.charAt(2), 1701707776);
            char char3 = CharacterUtilities.toLowerCase(href.charAt(3), 1701707776);
            char char4 = CharacterUtilities.toLowerCase(href.charAt(4), 1701707776);
            char char5 = CharacterUtilities.toLowerCase(href.charAt(5), 1701707776);
            if ((char0 != 'h' || char1 != 't' || char2 != 't' || char3 != 'p' || char4 != ':')
               && (char0 != 'h' || char1 != 't' || char2 != 't' || char3 != 'p' || char4 != 's' || char5 != ':')
               && (
                  length < 11
                     || char0 != 'j'
                     || char1 != 'a'
                     || char2 != 'v'
                     || char3 != 'a'
                     || char4 != 's'
                     || char5 != 'c'
                     || CharacterUtilities.toLowerCase(href.charAt(6), 1701707776) != 'r'
                     || CharacterUtilities.toLowerCase(href.charAt(7), 1701707776) != 'i'
                     || CharacterUtilities.toLowerCase(href.charAt(8), 1701707776) != 'p'
                     || CharacterUtilities.toLowerCase(href.charAt(9), 1701707776) != 't'
                     || href.charAt(10) != ':'
               )) {
               if (length >= 7 && char0 == 'm' && char1 == 'a' && char2 == 'i' && char3 == 'l' && char4 == 't' && char5 == 'o' && href.charAt(6) == ':') {
                  if (href.indexOf(63) == -1 && href.indexOf(44) == -1) {
                     linkType = -2985347935260258684L;
                  } else {
                     linkType = 8245590029279666536L;
                  }

                  doMatch = false;
               }
            } else {
               doMatch = false;
            }
         }

         if (doMatch && href.indexOf(58) == -1) {
            doMatch = false;
         }

         if (doMatch) {
            synchronized (_syncObject) {
               boolean var15 = false /* VF: Semaphore variable */;

               try {
                  var15 = true;
                  if (_stringEnum == null) {
                     _stringEnum = (StringPatternEnumerator)(new Object(href, StringPatternRepository$Internal.getStringPatterns()));
                  } else {
                     _stringEnum.reset(href, 0, href.length());
                  }

                  if (_stringEnum.hasMoreMatches()) {
                     StringPattern$Match stringMatch = (StringPattern$Match)(new Object());
                     _stringEnum.nextMatch(stringMatch);
                     if (stringMatch.beginIndex == 0) {
                        linkType = stringMatch.id;
                        var15 = false;
                     } else {
                        var15 = false;
                     }
                  } else {
                     var15 = false;
                  }
               } finally {
                  if (var15) {
                     if (_stringEnum != null) {
                        _stringEnum.reset(null, 0, 0);
                     }
                  }
               }

               if (_stringEnum != null) {
                  _stringEnum.reset(null, 0, 0);
               }

               return linkType;
            }
         }
      }

      return linkType;
   }
}
