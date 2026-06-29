package net.rim.device.apps.internal.lbs.verbs;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.StringPatternRepository;
import net.rim.device.apps.internal.lbs.LBSOptions;

public final class LBSStringPattern extends StringPattern {
   StringSetSearch _search = new StringSetSearch();
   private static final long LBS_HYPERLINK_FACTORY = -4732549676614397488L;
   static LBSOptions _options = LBSOptions.getInstance();

   public static final void registerOnStartup() {
      if (ApplicationRegistry.getApplicationRegistry().get(-4732549676614397488L) == null) {
         LBSHyperlinkFactory factory = new LBSHyperlinkFactory();
         ApplicationRegistry.getApplicationRegistry().put(-4732549676614397488L, factory);
         StringPatternRepository.addPattern(new LBSStringPattern());
      }
   }

   public static final void unregister() {
      ApplicationRegistry.getApplicationRegistry().remove(-4732549676614397488L);
   }

   public LBSStringPattern() {
      this._search.add("http://maps.blackberry.com?lat=", -4732549676614397488L);
      this._search.add("maps.blackberry.com?lat=", -4732549676614397488L);
      this._search.add("http://bbplanet1.ottawa.testnet.rim.net?lat=", -4732549676614397488L);
      this._search.add("http://maps.blackberry.com?startLat=", -4732549676614397488L);
      this._search.add("maps.blackberry.com?startLat=", -4732549676614397488L);
      this._search.add("http://bbplanet1.ottawa.testnet.rim.net?startLat=", -4732549676614397488L);
      this._search.add("http://maps.blackberry.com?endLat=", -4732549676614397488L);
      this._search.add("maps.blackberry.com?endLat=", -4732549676614397488L);
      this._search.add("http://bbplanet1.ottawa.testnet.rim.net?endLat=", -4732549676614397488L);
      this._search.buildSkipJump();
   }

   @Override
   public final synchronized boolean findMatch(AbstractString str, int index, int maxIndex, StringPattern$Match match) {
      if (_options.isDisabled() || !(ApplicationRegistry.getApplicationRegistry().get(-4732549676614397488L) instanceof LBSHyperlinkFactory)) {
         return false;
      } else if (this._search.findMatch(str, index, maxIndex, match)) {
         match.prefixLength = 0;
         match.endIndex = getEndIndex(str, match.endIndex, maxIndex);
         return true;
      } else {
         return false;
      }
   }

   private static final int getEndIndex(AbstractString str, int beginIndex, int maxIndex) {
      int endIndex = beginIndex + 1;

      while (endIndex < maxIndex && isValidChar(str.charAt(endIndex))) {
         endIndex++;
      }

      while (endIndex > beginIndex && isPunctuation(str.charAt(endIndex - 1))) {
         endIndex--;
      }

      return endIndex;
   }

   private static final boolean isValidChar(char c) {
      if (c >= 'a' && c <= 'z') {
         return true;
      }

      if (c >= 'A' && c <= 'Z') {
         return true;
      }

      if (c >= '0' && c <= '9') {
         return true;
      }

      switch (c) {
         case '!':
         case '#':
         case '$':
         case '%':
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
         case '=':
         case '?':
         case '@':
         case '_':
         case '{':
         case '}':
         case '~':
            return true;
         default:
            return false;
      }
   }

   private static final boolean isPunctuation(char c) {
      switch (c) {
         case '!':
         case '"':
         case '\'':
         case '(':
         case ')':
         case ',':
         case '.':
         case ':':
         case ';':
         case '<':
         case '>':
         case '?':
         case '[':
         case ']':
         case '{':
         case '|':
         case '}':
            return true;
         default:
            return false;
      }
   }
}
