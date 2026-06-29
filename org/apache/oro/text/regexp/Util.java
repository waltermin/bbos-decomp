package org.apache.oro.text.regexp;

import java.util.Vector;

public final class Util {
   public static final int SUBSTITUTE_ALL;
   public static final int SPLIT_ALL;

   private Util() {
   }

   public static final void split(Vector results, PatternMatcher matcher, Pattern pattern, String input, int limit) {
      PatternMatcherInput pinput = new PatternMatcherInput(input);
      int beginOffset = 0;

      while (--limit != 0 && matcher.contains(pinput, pattern)) {
         MatchResult currentResult = matcher.getMatch();
         results.addElement(input.substring(beginOffset, currentResult.beginOffset(0)));
         beginOffset = currentResult.endOffset(0);
      }

      results.addElement(input.substring(beginOffset, input.length()));
   }

   public static final void split(Vector results, PatternMatcher matcher, Pattern pattern, String input) {
      split(results, matcher, pattern, input, 0);
   }

   public static final Vector split(PatternMatcher matcher, Pattern pattern, String input, int limit) {
      Vector results = (Vector)(new Object(20));
      split(results, matcher, pattern, input, limit);
      return results;
   }

   public static final Vector split(PatternMatcher matcher, Pattern pattern, String input) {
      return split(matcher, pattern, input, 0);
   }

   public static final String substitute(PatternMatcher matcher, Pattern pattern, Substitution sub, String input, int numSubs) {
      StringBuffer buffer = (StringBuffer)(new Object(input.length()));
      PatternMatcherInput pinput = new PatternMatcherInput(input);
      return substitute(buffer, matcher, pattern, sub, pinput, numSubs) != 0 ? buffer.toString() : input;
   }

   public static final String substitute(PatternMatcher matcher, Pattern pattern, Substitution sub, String input) {
      return substitute(matcher, pattern, sub, input, 1);
   }

   public static final int substitute(StringBuffer result, PatternMatcher matcher, Pattern pattern, Substitution sub, String input, int numSubs) {
      PatternMatcherInput pinput = new PatternMatcherInput(input);
      return substitute(result, matcher, pattern, sub, pinput, numSubs);
   }

   public static final int substitute(StringBuffer result, PatternMatcher matcher, Pattern pattern, Substitution sub, PatternMatcherInput input, int numSubs) {
      int subCount = 0;
      int beginOffset = input.getBeginOffset();
      char[] inputBuffer = input.getBuffer();

      while (numSubs != 0 && matcher.contains(input, pattern)) {
         numSubs--;
         subCount++;
         result.append(inputBuffer, beginOffset, input.getMatchBeginOffset() - beginOffset);
         sub.appendSubstitution(result, matcher.getMatch(), subCount, input, matcher, pattern);
         beginOffset = input.getMatchEndOffset();
      }

      result.append(inputBuffer, beginOffset, input.length() - beginOffset);
      return subCount;
   }

   static final boolean isLetterOrDigit(char ch) {
      return isLetter(ch) || isDigit(ch);
   }

   static final boolean isWhitespace(char ch) {
      switch (ch) {
         case '\t':
         case '\n':
         case '\u000b':
         case '\f':
         case '\r':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case ' ':
         case '\u200b':
         case '\u2028':
         case '\u2029':
         case ' ':
         case '　':
            return true;
         default:
            return false;
      }
   }

   static final boolean isLetter(char ch) {
      return isLowerCase(ch) || isUpperCase(ch);
   }

   static final boolean isLowerCase(char ch) {
      return ch > 'z' ? false : ch >= 'a';
   }

   static final boolean isUpperCase(char ch) {
      return ch > 'Z' ? false : ch >= 'A';
   }

   static final boolean isDigit(char ch) {
      return ch > '9' ? false : ch >= '0';
   }

   static final boolean isSpaceChar(char ch) {
      switch (ch) {
         case '\t':
         case '\u000b':
         case '\f':
         case ' ':
         case ' ':
            return true;
         default:
            return false;
      }
   }

   static final boolean isISOControl(char ch) {
      return false;
   }
}
