package net.rim.device.internal.ui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;

class Backdoor$MyMenuItem extends MenuItem {
   int _id;
   private final Backdoor this$0;

   Backdoor$MyMenuItem(Backdoor _1, String label, int id) {
      super(label, 0, 0);
      this.this$0 = _1;
      this._id = id;
   }

   @Override
   public void run() {
      switch (this._id) {
         case 1:
         default:
            this.this$0.validate(UiApplication.getUiApplication().getActiveScreen());
            return;
         case 2:
            RecordKeystrokes keys = new RecordKeystrokes();
            UiApplication.getUiApplication().pushModalScreen(keys);
            keys.fillqueue(100);
            Application.getApplication().invokeLater(new MessageTiming());
            return;
         case 3:
            UiApplication.getUiApplication().repaint();
            return;
         case 4:
            this.this$0.timeRepaint();
            return;
         case 5:
            this.this$0.dumpCacheStats();
            return;
         case 6:
            UiInternal.clearCacheStatistics();
         case 0:
      }
   }
}
