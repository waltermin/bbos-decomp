package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;

final class IOTAEditScreen$IOTAMenuItem extends MenuItem {
   private int _type;
   private final IOTAEditScreen this$0;
   public static final int ADD;
   public static final int DELETE;
   public static final int EDIT;

   public IOTAEditScreen$IOTAMenuItem(IOTAEditScreen _1, String title, int type) {
      super(title, 628224, 100);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public final void run() {
      synchronized (Application.getEventLock()) {
         switch (this._type) {
            case -1:
               break;
            case 0:
            default:
               UiApplication.getUiApplication().pushScreen(new IOTAObjectEditScreen(this.this$0, "", ""));
               break;
            case 1:
               this.this$0.keyChar('\u007f', 0, 0);
               break;
            case 2:
               this.this$0.keyChar('\n', 0, 0);
         }
      }
   }
}
