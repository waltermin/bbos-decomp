package net.rim.ecmascript.regexp;

import org.apache.oro.text.regexp.MalformedPatternException;
import org.apache.oro.text.regexp.MatchResult;
import org.apache.oro.text.regexp.Pattern;
import org.apache.oro.text.regexp.PatternMatcherInput;
import org.apache.oro.text.regexp.Perl5Compiler;
import org.apache.oro.text.regexp.Perl5Matcher;

public class RegExp {
   private Pattern _pattern;
   private boolean _multiLine;
   private String _cachedStr;
   private PatternMatcherInput _cachedInput;
   private static Perl5Compiler _compiler = new Perl5Compiler();
   private static Perl5Matcher _matcher = new Perl5Matcher();

   public RegExp(String str, boolean ignoreCase, boolean multiLine) throws RegExp$SyntaxError {
      this._multiLine = multiLine;
      int options = 0;
      if (ignoreCase) {
         options = 1;
      }

      try {
         this._pattern = _compiler.compile(str, options);
      } catch (MalformedPatternException mfe) {
         throw new RegExp$SyntaxError(mfe.getMessage());
      }
   }

   public RegExp(String str, boolean ignoreCase) {
      this(str, ignoreCase, false);
   }

   public boolean setMultiLine(boolean multiLine) {
      boolean rc = this._multiLine;
      this._multiLine = multiLine;
      return rc;
   }

   public RegExp(String str) {
      this(str, false);
   }

   public RegExp$MatchResult match(String str, int startIndex) {
      return this.match(str, startIndex, true);
   }

   public RegExp$MatchResult matchNoAdvance(String str, int startIndex) {
      return this.match(str, startIndex, false);
   }

   public RegExp$MatchResult match(String str, int startIndex, boolean advance) {
      _matcher.setMultiline(this._multiLine);
      int length = str.length();
      if (str != this._cachedStr) {
         this._cachedStr = str;
         this._cachedInput = new PatternMatcherInput(str);
      }

      PatternMatcherInput input = this._cachedInput;
      input.setCurrentOffset(startIndex);
      boolean matches;
      if (advance) {
         matches = _matcher.contains(input, this._pattern);
      } else {
         matches = _matcher.matchesPrefix(input, this._pattern);
      }

      if (!matches) {
         return null;
      }

      MatchResult match = _matcher.getMatch();
      int numGroups = match.groups();
      String[] groups = new Object[numGroups];

      for (int i = 0; i < numGroups; i++) {
         int start = match.beginOffset(i);
         int end = match.endOffset(i);
         if (start < 0 || end < 0 || start > end || start > length || end > length) {
            groups[i] = null;
         } else if (start != end && start >= 0 && start < length && end >= 0 && end <= length) {
            groups[i] = str.substring(start, end);
         } else {
            groups[i] = "";
         }
      }

      return new RegExp$MatchResult(str, match.beginOffset(0), match.endOffset(0), groups);
   }
}
