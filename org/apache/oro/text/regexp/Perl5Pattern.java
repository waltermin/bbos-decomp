package org.apache.oro.text.regexp;

public final class Perl5Pattern implements Pattern {
   String _expression;
   char[] _program;
   int _mustUtility;
   int _back;
   int _minLength;
   int _numParentheses;
   boolean _isCaseInsensitive;
   boolean _isExpensive;
   int _startClassOffset;
   int _anchor;
   int _options;
   char[] _mustString;
   char[] _startString;
   static final int _OPT_ANCH_BOL = 1;
   static final int _OPT_ANCH_MBOL = 2;
   static final int _OPT_SKIP = 4;
   static final int _OPT_IMPLICIT = 8;
   static final int _OPT_ANCH = 3;

   Perl5Pattern() {
   }

   @Override
   public final String getPattern() {
      return this._expression;
   }

   @Override
   public final int getOptions() {
      return this._options;
   }
}
