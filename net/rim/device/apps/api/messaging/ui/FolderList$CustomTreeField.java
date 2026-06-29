package net.rim.device.apps.api.messaging.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.TreeField;
import net.rim.device.api.ui.component.TreeFieldCallback;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.im.InputMethodRequests;

final class FolderList$CustomTreeField extends TreeField {
   Field _imRequest;

   public FolderList$CustomTreeField(TreeFieldCallback callback, long style, Field imRequest) {
      super(callback, style);
      this._imRequest = imRequest;
   }

   @Override
   public final void dispatchEvent(Event rEvent) {
      if (rEvent.getID() == 1004) {
         rEvent.setSource(this._imRequest);
         this._imRequest.dispatchEvent(rEvent);
      } else {
         this._imRequest.dispatchEvent(rEvent);
      }
   }

   @Override
   public final InputMethodRequests getInputMethodRequests() {
      return this._imRequest.getInputMethodRequests();
   }
}
