package net.rim.device.apps.internal.calendar.viewer;

final class CalendarViewController$ShowOrganizingCalendar extends Thread {
   private boolean _threadHasRun;
   private boolean _invalid;
   private final CalendarViewController this$0;

   CalendarViewController$ShowOrganizingCalendar(CalendarViewController _1) {
      this.this$0 = _1;
      this._threadHasRun = false;
      this._invalid = false;
   }

   @Override
   public final void run() {
      if (!this._invalid) {
         this.this$0.showOrganizingCalendarDialog();
      }

      this._threadHasRun = true;
   }

   public final void setInvalid(boolean invalid) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final boolean getThreadHasRun() {
      return this._threadHasRun;
   }
}
