package net.rim.device.apps.internal.browser.page;

final class PageHeaderField$RedrawRunnable implements Runnable {
   private PageHeaderField _pageHeaderField;
   private boolean _invokeLaterPending;

   PageHeaderField$RedrawRunnable(PageHeaderField pageHeaderField) {
      this._pageHeaderField = pageHeaderField;
   }

   @Override
   public final void run() {
      synchronized (this) {
         this._invokeLaterPending = false;
      }

      if (this._pageHeaderField._layoutUpdateRequired) {
         this._pageHeaderField._layoutUpdateRequired = false;
         PageHeaderField.access$100(this._pageHeaderField);
      }

      PageHeaderField.access$200(this._pageHeaderField);
   }
}
