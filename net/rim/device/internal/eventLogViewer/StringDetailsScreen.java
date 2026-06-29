package net.rim.device.internal.eventLogViewer;

import net.rim.vm.EventLog;

final class StringDetailsScreen extends BaseDetailsScreen {
   public StringDetailsScreen(EventLoggerContents contents) {
      super(contents);
   }

   @Override
   public final String getEventSummary(int eventHandle) {
      return new String(EventLog.getData(eventHandle));
   }

   @Override
   public final void displayEventDetails(int eventHandle) {
      this.setTitle(super._contents._rb.getString(51));
      super.displayEventDetails(eventHandle);
   }

   @Override
   protected final String getDataFieldString(int eventHandle) {
      return new String(EventLog.getData(eventHandle));
   }
}
