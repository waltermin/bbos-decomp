package net.rim.device.api.ui.theme;

import java.util.NoSuchElementException;
import net.rim.device.api.util.StringTokenizer;

class BaseThemeIterator {
   private final String _base;
   private boolean _baseUsed;
   private final StringTokenizer _alternateBase;
   private int _alternateBaseIndex;
   private final int _alternateBaseFirstRedundant;

   public BaseThemeIterator(String base, String alternateBase, Integer alternateBaseFirstRedundant) {
      this._base = base;
      if (alternateBase == null) {
         this._alternateBase = null;
         this._alternateBaseFirstRedundant = 0;
      } else {
         this._alternateBase = new StringTokenizer(alternateBase, ", ");
         int alternateBaseLen = this._alternateBase.countTokens();
         this._alternateBaseFirstRedundant = alternateBaseFirstRedundant == null ? alternateBaseLen : Math.min(alternateBaseFirstRedundant, alternateBaseLen);
      }
   }

   public boolean hasNextBaseTheme() {
      return !this._baseUsed || this._alternateBaseIndex < this._alternateBaseFirstRedundant;
   }

   public String nextBaseTheme() {
      if (!this._baseUsed) {
         String result = this._base;
         this._baseUsed = true;
         return result;
      } else if (this._alternateBaseIndex < this._alternateBaseFirstRedundant) {
         String result = this._alternateBase.nextToken();
         this._alternateBaseIndex++;
         return result;
      } else {
         throw new NoSuchElementException("no more base theme names");
      }
   }
}
