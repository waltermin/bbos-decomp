package net.rim.device.apps.internal.security;

import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.IntHashtable;

final class StringSet {
   private IntHashtable roots = (IntHashtable)(new Object(47));

   final void add(String text, int key) {
      int len = text.length();
      IntHashtable currentLevel = this.roots;

      for (int i = 0; i < len; i++) {
         char c = Character.toUpperCase(text.charAt(i));
         if (c == ' ') {
            currentLevel.put(0, new Object(key));
            currentLevel = this.roots;
         } else {
            IntHashtable nextLevel = (IntHashtable)currentLevel.get(c);
            if (nextLevel == null) {
               nextLevel = (IntHashtable)(new Object(5));
               currentLevel.put(c, nextLevel);
            }

            currentLevel = nextLevel;
         }
      }

      currentLevel.put(0, new Object(key));
   }

   public final boolean findMatch(AbstractString str, int start, int end, StringSetMatch match) {
      IntHashtable currentLevel = this.roots;
      int startMatch = start;

      for (int i = start; i < end; i++) {
         char c = Character.toUpperCase(str.charAt(i));
         IntHashtable nextLevel = (IntHashtable)currentLevel.get(c);
         if (nextLevel == null) {
            Integer sm = (Integer)currentLevel.get(0);
            if (sm != null) {
               match._start = startMatch;
               match._end = i;
               match._key = sm;
               return true;
            }

            i = startMatch;
            startMatch = i + 1;
            currentLevel = this.roots;
         } else {
            currentLevel = nextLevel;
         }
      }

      Integer sm = (Integer)currentLevel.get(0);
      if (sm != null) {
         match._start = startMatch;
         match._end = end;
         match._key = sm;
         return true;
      } else {
         return false;
      }
   }
}
