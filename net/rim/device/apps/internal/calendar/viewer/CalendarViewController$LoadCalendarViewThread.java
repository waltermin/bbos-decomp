package net.rim.device.apps.internal.calendar.viewer;

import java.util.Vector;
import net.rim.device.api.system.Application;

final class CalendarViewController$LoadCalendarViewThread extends Thread {
   private Vector _loadViewRequests;
   private final CalendarViewController this$0;

   CalendarViewController$LoadCalendarViewThread(CalendarViewController _1) {
      this.this$0 = _1;
      this._loadViewRequests = (Vector)(new Object());
   }

   public final void addLoadRequest(
      long time,
      byte timeToUseFlag,
      Object object,
      byte objectToUseFlag,
      boolean updateSelectedDate,
      boolean reposition,
      boolean preserveSelectedTime,
      byte loadType
   ) {
      synchronized (this._loadViewRequests) {
         if (this._loadViewRequests.size() > 0) {
            this._loadViewRequests.removeElementAt(0);
         }

         CalendarViewController$LoadViewContentsData element = new CalendarViewController$LoadViewContentsData(
            time, timeToUseFlag, object, objectToUseFlag, updateSelectedDate, reposition, preserveSelectedTime, loadType
         );
         this._loadViewRequests.addElement(element);
         this._loadViewRequests.notify();
      }
   }

   @Override
   public final void run() {
      Application calApp = null;

      while (true) {
         try {
            CalendarViewController$LoadViewContentsData data = null;
            synchronized (this._loadViewRequests) {
               if (this._loadViewRequests.size() > 0) {
                  data = (CalendarViewController$LoadViewContentsData)this._loadViewRequests.elementAt(0);
                  this._loadViewRequests.removeElementAt(0);
               } else {
                  label126:
                  try {
                     this._loadViewRequests.wait();
                  } finally {
                     break label126;
                  }
               }
            }

            calApp = this.this$0.getApplication();
            if (data != null && calApp != null) {
               CalendarViewController$ShowOrganizingCalendar dialogThread = new CalendarViewController$ShowOrganizingCalendar(this.this$0);
               int organizingCalendarDialogID = this.this$0._calendarUIApplication.invokeLater(dialogThread, 200, false);
               if (organizingCalendarDialogID == -1) {
                  this.this$0.showOrganizingCalendarDialog();
               }

               Runnable r = this.this$0
                  .loadViewContentsNow(
                     data._time,
                     data._timeToUseFlag,
                     data._object,
                     data._objectToUseFlag,
                     data._updateSelectedDate,
                     data._reposition,
                     data._preserveSelectedTime,
                     data._loadType
                  );
               synchronized (calApp.getAppEventLock()) {
                  dialogThread.setInvalid(true);
                  if (organizingCalendarDialogID != -1 && !dialogThread.getThreadHasRun()) {
                     this.this$0._calendarUIApplication.cancelInvokeLater(organizingCalendarDialogID);
                  }

                  if (r != null) {
                     r.run();
                     if (data._updateSelectedDate) {
                        this.this$0.updateSelectedDate();
                     }
                  }
               }

               this.this$0.dismissOrganizingCalendarDialog();
            }

            calApp = null;
         } finally {
            continue;
         }
      }
   }
}
