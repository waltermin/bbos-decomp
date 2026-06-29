package net.rim.blackberry.api.stringpattern;

import net.rim.device.api.util.StringPattern$Match;
import org.apache.oro.text.regexp.MatchResult;
import org.apache.oro.text.regexp.Pattern;
import org.apache.oro.text.regexp.PatternCompiler;
import org.apache.oro.text.regexp.PatternMatcher;

final class RegularExpressionStringPatternImpl extends ExternalStringPatternImpl {
   private Pattern _pattern;

   RegularExpressionStringPatternImpl(String stringPattern, long id) {
      super(stringPattern, id);
      PatternCompiler compiler = (PatternCompiler)(new Object());

      try {
         this._pattern = compiler.compile(super._stringPattern);
      } finally {
         throw new Object("Malformed regular expression");
      }
   }

   @Override
   protected final boolean doMatch(String searchString, int beginIndex, int maxIndex, StringPattern$Match match) {
      PatternMatcher matcher = (PatternMatcher)(new Object());
      if (matcher.contains(searchString.substring(beginIndex, maxIndex), this._pattern)) {
         MatchResult result = matcher.getMatch();
         match.beginIndex = result.beginOffset(0) + beginIndex;
         match.endIndex = result.endOffset(0) + beginIndex;
         match.id = super._id;
         match.prefixLength = 0;
         return true;
      } else {
         return false;
      }
   }
}
