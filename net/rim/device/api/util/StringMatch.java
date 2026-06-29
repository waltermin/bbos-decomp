package net.rim.device.api.util;

import net.rim.vm.Array;

public final class StringMatch implements Persistable {
   private String[] _patterns;
   private int[][] _failLinks;
   private boolean _caseSensitivity;
   private boolean _allMatch;
   private int _lastMatchedIndex;

   private static final native void KMPInit(String var0, int[] var1);

   private final native int KMPMatch(String var1, int var2);

   private final native int KMPMatch(StringBuffer var1, int var2);

   public StringMatch(String patternToMatch, boolean caseSensitivity) {
      this(patternToMatch, caseSensitivity, true);
   }

   public StringMatch(String patternToMatch, boolean caseSensitivity, boolean spaceDelimitsNewPattern) {
      this(patternToMatch, caseSensitivity, spaceDelimitsNewPattern, true);
   }

   public StringMatch(String patternToMatch, boolean caseSensitivity, boolean spaceDelimitsNewPattern, boolean allMatch) {
      this._caseSensitivity = caseSensitivity;
      this._allMatch = allMatch;
      String pattern;
      if (caseSensitivity) {
         pattern = patternToMatch;
      } else {
         pattern = patternToMatch.toUpperCase();
      }

      if (!spaceDelimitsNewPattern) {
         this._failLinks = new int[1][];
         this._patterns = new String[1];
         this._patterns[0] = pattern;
         this._failLinks[0] = new int[pattern.length()];
         KMPInit(pattern, this._failLinks[0]);
      } else {
         int start = 0;
         int len = pattern.length();
         this._failLinks = new int[0][];
         this._patterns = new String[0];
         int numPatterns = 0;

         while (start < len) {
            while (start < len && pattern.charAt(start) == ' ') {
               start++;
            }

            int end = start + 1;

            while (end < len && pattern.charAt(end) != ' ') {
               end++;
            }

            if (start < len) {
               Array.resize(this._failLinks, ++numPatterns);
               Array.resize(this._patterns, numPatterns);
               String subPattern = pattern.substring(start, end);
               this._patterns[numPatterns - 1] = subPattern;
               this._failLinks[numPatterns - 1] = new int[subPattern.length()];
               KMPInit(subPattern, this._failLinks[numPatterns - 1]);
            }

            start = end;
         }
      }
   }

   public StringMatch(String[] patterns, boolean caseSensitivity, boolean allMatch) {
      if (patterns == null) {
         throw new IllegalArgumentException();
      }

      int patternCount = patterns.length;
      this._caseSensitivity = caseSensitivity;
      this._allMatch = allMatch;
      this._failLinks = new int[patternCount][];
      this._patterns = new String[patternCount];
      if (caseSensitivity) {
         System.arraycopy(patterns, 0, this._patterns, 0, patternCount);
      }

      for (int i = 0; i < patternCount; i++) {
         if (patterns[i] == null) {
            throw new IllegalArgumentException();
         }

         if (!caseSensitivity) {
            this._patterns[i] = patterns[i].toUpperCase();
         }

         this._failLinks[i] = new int[this._patterns[i].length()];
         KMPInit(this._patterns[i], this._failLinks[i]);
      }
   }

   public StringMatch(String pattern) {
      this(pattern, true);
   }

   public final int indexOf(String text) {
      return this.KMPMatch(text, 0);
   }

   public final int indexOf(String text, int offset) {
      return this.KMPMatch(text, offset);
   }

   public final int indexOf(StringBuffer text, int offset) {
      return this.KMPMatch(text, offset);
   }

   public final int numStringsInPattern() {
      return this._patterns.length;
   }

   public final int getLastMatchedPattern() {
      return this._lastMatchedIndex;
   }
}
