package net.rim.device.internal.eventLogViewer;

import java.util.Calendar;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.vm.EventLog;

final class EventLoggerContents$MyMenuItem extends MenuItem {
   private int _id;
   private final EventLoggerContents this$0;

   EventLoggerContents$MyMenuItem(EventLoggerContents _1, int id) {
      super(_1.getMenuResourceBundle(id), _1.getMenuId(id), _1.getMenuOrdinal(id), 0);
      this.this$0 = _1;
      this._id = id;
   }

   @Override
   public final void run() {
      switch (this._id) {
         case 0:
         default:
            this.this$0.close();
            return;
         case 5:
            this.this$0.viewItem();
            return;
         case 6:
            int result = Dialog.ask(2, this.this$0._rb.getString(61));
            if (result != 3) {
               return;
            } else {
               EventLogger.clearLog();
            }
         case 2:
            this.this$0.refresh();
            return;
         case 7:
            EventLoggerOptions options = new EventLoggerOptions(this.this$0, this.this$0._filterPersist);
            UiApplication.getUiApplication().pushScreen(options);
            return;
         case 8:
            int resultx = Dialog.ask(this.this$0._rb.getString(29), this.this$0._rb.getStringArray(30), 0);
            int[] handles;
            int index;
            if (resultx == 0) {
               handles = this.this$0._eventHandles;
               index = this.this$0.listIndex2UnfilteredIndex(this.this$0._list.getSelectedIndex());
            } else {
               if (resultx != 1) {
                  return;
               }

               handles = this.this$0._filteredEventHandles;
               index = this.this$0.listIndex2FilteredIndex(this.this$0._list.getSelectedIndex());
            }

            StringBuffer strBuf = new StringBuffer(128);
            Calendar curCal = Calendar.getInstance();
            ((CalendarExtensions)curCal).setTimeLong(EventLog.getTime(handles[index]));
            int startIndex = this.this$0.findDateBoundaryIndex(handles, index, curCal, -1);

            for (int var9 = this.this$0.findDateBoundaryIndex(handles, index, curCal, 1); var9 >= startIndex; var9--) {
               this.this$0.prepareStringBuffer(strBuf, handles[var9], 255);
               strBuf.append('\n');
            }

            Clipboard.getClipboard().put(strBuf.toString());
         case -1:
         case 1:
         case 3:
         case 4:
      }
   }
}
