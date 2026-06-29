package net.rim.device.internal.eventLogViewer;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.util.Arrays;

final class EventLoggerOptions$ELMenu extends MenuItem {
   int _id;
   private final EventLoggerOptions this$0;

   EventLoggerOptions$ELMenu(EventLoggerOptions _1, ResourceBundle b, int id) {
      super(b, id, 0, 0);
      this.this$0 = _1;
      this._id = id;
   }

   final void setAllFilters(int v) {
      Arrays.fill(this.this$0._flags, (byte)v);
      this.this$0._list.invalidate();
      this.this$0._filterFlagsDirty = true;
   }

   @Override
   public final void run() {
      switch (this._id) {
         case 18:
            this.this$0.save();
         case 9:
            this.this$0.close();
         default:
            return;
         case 25:
            this.this$0.toggleCurr();
            return;
         case 27:
            this.setAllFilters(1);
            return;
         case 28:
            this.setAllFilters(0);
      }
   }
}
