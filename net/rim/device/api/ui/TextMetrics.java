package net.rim.device.api.ui;

public final class TextMetrics {
   public int iAdvanceX;
   public int iAdvanceY;
   public int iBoundsTlX;
   public int iBoundsTlY;
   public int iBoundsBrX;
   public int iBoundsBrY;
   public int iCharacters;
   public int iNextAdvanceX;
   public int iNextAdvanceY;
   public int iNextCharacters;
   public int iNextOriginX;
   public int iNextOriginY;
   public int iFlags;
   public static int NEEDS_BIDI_REORDERING = 1;

   public final void reset() {
      this.iAdvanceX = this.iAdvanceY = this.iBoundsTlX = this.iBoundsTlY = this.iBoundsBrX = this.iBoundsBrY = this.iCharacters = this.iNextAdvanceX = this.iNextAdvanceY = this.iNextCharacters = this.iNextOriginX = this.iNextOriginY = this.iFlags = 0;
   }
}
