package net.rim.device.apps.internal.docview.gui;

final class PageBreak extends BreakObj {
   final int _pageIndex;
   final String _domID;

   PageBreak(int pageIndex, int charOffset, String domID) {
      super(charOffset);
      this._pageIndex = pageIndex;
      this._domID = domID;
   }

   @Override
   final BreakObj cloneObject() {
      throw new Object("Base class cannot be cloned");
   }
}
