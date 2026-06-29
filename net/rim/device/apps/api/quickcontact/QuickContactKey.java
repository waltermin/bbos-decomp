package net.rim.device.apps.api.quickcontact;

import net.rim.device.api.util.CharacterUtilities;

public class QuickContactKey {
   char _key;

   public QuickContactKey(char key) {
      this._key = key;
   }

   public char key() {
      return this._key;
   }

   @Override
   public String toString() {
      char[] buf = new char[]{CharacterUtilities.toUpperCase(this._key, 1701707776)};
      return String.valueOf(buf);
   }
}
