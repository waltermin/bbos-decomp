package net.rim.device.apps.internal.docview.gui;

public final class DocViewTextHint {
   public int _fullContentIndex = -1;
   public int _tocIndex = -1;

   DocViewTextHint() {
   }

   DocViewTextHint(int fullContentIndex, int tocIndex) {
      this._fullContentIndex = fullContentIndex;
      this._tocIndex = tocIndex;
   }
}
