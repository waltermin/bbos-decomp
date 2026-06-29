package net.rim.device.apps.internal.browser.plugin.docview;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.docview.gui.DocViewParseNotify;

final class UCSPluginParseNotify extends DocViewParseNotify {
   Field _fld;
   boolean _notified;

   @Override
   protected final void parseSucceeded(Field displayField) {
      this._fld = displayField;
      this.notifyWaitingThreads();
   }

   @Override
   protected final void parseFailed(byte errorCode) {
      this.notifyWaitingThreads();
      super.parseFailed(errorCode);
   }

   private final synchronized void notifyWaitingThreads() {
      this._notified = true;
      this.notifyAll();
   }
}
