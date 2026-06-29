package net.rim.device.apps.internal.api.serialformats;

import net.rim.device.api.util.StringUtilities;

final class PropertyParser {
   private String _name = null;
   private String _token;

   final void reset() {
      this._name = null;
      this._token = null;
   }

   final boolean getNextParameterAndValue(TokenParser in) {
      return this.getNextParameterAndValue(in, false);
   }

   final boolean getNextParameterAndValue(TokenParser in, boolean lineBreak) {
      int c = in.getBreakingCharacter();
      this._token = null;
      if (lineBreak) {
         if (in.isLineBreak()) {
            return false;
         }
      } else if (c == 58) {
         return false;
      }

      if (c == 59) {
         this._name = null;
      }

      int state = 1;

      while (true) {
         this._token = in.nextToken();
         switch (state) {
            case 0:
               break;
            case 1:
            default:
               c = in.getBreakingCharacter();
               if (c != 61) {
                  this._token = StringUtilities.toUpperCase(this._token, 1701707776);
                  return true;
               }

               this._name = StringUtilities.toUpperCase(this._token, 1701707776);
               state = 2;
               break;
            case 2:
               if (this._token.compareTo("=") != 0) {
                  throw new InvalidFormatException();
               }

               state = 3;
               break;
            case 3:
               c = in.getBreakingCharacter();
               if (c == 61) {
                  throw new InvalidFormatException();
               } else {
                  int length = this._token.length();
                  if (this._token.charAt(0) == '"' && this._token.charAt(length - 1) == '"') {
                     this._token = this._token.substring(1, length - 1);
                     return true;
                  } else {
                     this._token = StringUtilities.toUpperCase(this._token, 1701707776);
                     return true;
                  }
               }
         }
      }
   }

   final String getParameterName() {
      return this._name;
   }

   final String getParameterValue() {
      return this._token;
   }
}
