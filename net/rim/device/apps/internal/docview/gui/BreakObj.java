package net.rim.device.apps.internal.docview.gui;

class BreakObj {
   int _charOffset;

   protected BreakObj(int charOffset) {
      if (charOffset < 0) {
         throw new Object("Negative character offset.");
      }

      this._charOffset = charOffset;
   }

   BreakObj cloneObject() {
      return new BreakObj(this._charOffset);
   }
}
