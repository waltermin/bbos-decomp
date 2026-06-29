package net.rim.blackberry.api.stringpattern;

import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.internal.util.ExternalStringPattern;

class ExternalStringPatternImpl extends StringPattern implements ExternalStringPattern {
   protected long _id;
   protected String _stringPattern;
   protected String _match;

   protected boolean doMatch(String _1, int _2, int _3, StringPattern$Match _4) {
      throw null;
   }

   @Override
   public long getID() {
      return this._id;
   }

   @Override
   public boolean findMatch(AbstractString str, int beginIndex, int maxIndex, StringPattern$Match match) {
      if (str == null) {
         return false;
      }

      char[] searchChars = new char[str.length()];
      str.getChars(0, str.length(), searchChars, 0);
      String searchString = (String)(new Object(searchChars));
      return this.doMatch(searchString, beginIndex, maxIndex, match);
   }

   ExternalStringPatternImpl(String stringPattern, long id) {
      this._id = id;
      this._stringPattern = stringPattern;
   }
}
