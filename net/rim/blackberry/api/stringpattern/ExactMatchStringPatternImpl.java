package net.rim.blackberry.api.stringpattern;

import net.rim.device.api.util.StringPattern$Match;

final class ExactMatchStringPatternImpl extends ExternalStringPatternImpl {
   ExactMatchStringPatternImpl(String stringPattern, long id) {
      super(stringPattern, id);
   }

   @Override
   protected final boolean doMatch(String searchString, int beginIndex, int maxIndex, StringPattern$Match match) {
      int matchIndex = -1;
      if ((matchIndex = searchString.indexOf(super._stringPattern, beginIndex)) != -1) {
         int endIndex = matchIndex + super._stringPattern.length();
         if (endIndex > maxIndex) {
            return false;
         }

         match.beginIndex = matchIndex;
         match.endIndex = endIndex;
         match.id = super._id;
         match.prefixLength = 0;
         return true;
      } else {
         return false;
      }
   }
}
