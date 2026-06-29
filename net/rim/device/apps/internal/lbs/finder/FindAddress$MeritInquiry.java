package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class FindAddress$MeritInquiry implements Runnable {
   int _resultIndex;
   String _label;
   private final FindAddress this$0;

   FindAddress$MeritInquiry(FindAddress this$0, String label) {
      this.this$0 = this$0;
      this._resultIndex = -1;
      this._label = label;
   }

   @Override
   public final void run() {
      synchronized (Application.getEventLock()) {
         if (Dialog.ask(3, MessageFormat.format(LBSResources.getString(144), new String[]{this._label}), 4) == 4) {
            this._resultIndex = 0;
         }
      }
   }
}
