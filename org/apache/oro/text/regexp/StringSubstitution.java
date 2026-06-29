package org.apache.oro.text.regexp;

public class StringSubstitution implements Substitution {
   int _subLength;
   String _substitution;

   public StringSubstitution() {
      this("");
   }

   public StringSubstitution(String substitution) {
      this.setSubstitution(substitution);
   }

   public void setSubstitution(String substitution) {
      this._substitution = substitution;
      this._subLength = substitution.length();
   }

   public String getSubstitution() {
      return this._substitution;
   }

   @Override
   public String toString() {
      return this.getSubstitution();
   }

   @Override
   public void appendSubstitution(
      StringBuffer appendBuffer, MatchResult match, int substitutionCount, PatternMatcherInput originalInput, PatternMatcher matcher, Pattern pattern
   ) {
      if (this._subLength != 0) {
         appendBuffer.append(this._substitution);
      }
   }
}
