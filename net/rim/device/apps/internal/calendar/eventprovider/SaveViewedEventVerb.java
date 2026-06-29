package net.rim.device.apps.internal.calendar.eventprovider;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.calendar.calconstants.CalOptionCache;
import net.rim.device.apps.api.calendar.controller.CalendarEventViewer;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.model.Recur$Handle;
import net.rim.device.cldc.util.CalendarExtensions;

final class SaveViewedEventVerb extends SaveEventVerb {
   private CalendarEventViewer _eventViewer;

   public SaveViewedEventVerb(CalendarEventViewer eventViewer, Event event, boolean newEvent) {
      super(event, newEvent);
      this._eventViewer = eventViewer;
   }

   public SaveViewedEventVerb(CalendarEventViewer eventViewer, Event series, long originalOccurrenceInstant, Event occurrence) {
      super(series, originalOccurrenceInstant, occurrence);
      this._eventViewer = eventViewer;
   }

   @Override
   public final Object invoke(Object parameter) {
      if (!this._eventViewer.validate(255)) {
         return null;
      }

      Event savedEvent = (Event)super.invoke(parameter);
      if (savedEvent != null) {
         this._eventViewer.closeViewer(false);
         TimeZone tz = TimeZone.getDefault();
         long currentFocus = CalOptionCache.getTimeWithFocus();
         long eventStartTime = savedEvent.getStartDate(tz);
         long timeToJumpTo = eventStartTime;
         if (!this.isNewEvent() && savedEvent.isRecurring()) {
            Calendar cal = Calendar.getInstance();
            ((CalendarExtensions)cal).setTimeLong(currentFocus);
            int year = cal.get(1);
            int month = cal.get(2);
            int day = cal.get(5);
            DateTimeUtilities.zeroCalendarTime(cal);
            long searchStartTime = ((CalendarExtensions)cal).getTimeLong();
            Recur$Handle foundOccurrence = new Recur$Handle();
            if (savedEvent.getHandleAfterTime(foundOccurrence, searchStartTime, tz)) {
               ((CalendarExtensions)cal).setTimeLong(foundOccurrence._handle);
               if (day == cal.get(5) && month == cal.get(2) && year == cal.get(1)) {
                  if (savedEvent.getAllDayFlag()) {
                     DateTimeUtilities.zeroCalendarTime(cal);
                  }

                  timeToJumpTo = ((CalendarExtensions)cal).getTimeLong();
               }
            }
         }

         if (this.hasEventTimeChanged(savedEvent)) {
            CalOptionCache.setTimeWithFocus(timeToJumpTo);
         }

         return savedEvent;
      } else {
         return null;
      }
   }

   @Override
   protected final boolean proceedWithSave(Event eventToSave) {
      ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
      byte modFlag = this._eventViewer.getConcurrentModificationFlag();
      if (modFlag != 0) {
         int msgID = 112;
         if ((modFlag & 4) != 0) {
            msgID = 114;
         } else if ((modFlag & 2) != 0) {
            msgID = 113;
         }

         Dialog.alert(rb.getString(msgID));
         this._eventViewer.closeViewer(false);
         return false;
      } else {
         return super.proceedWithSave(eventToSave);
      }
   }

   @Override
   protected final boolean hasEventTimeChanged(Event event) {
      return this.isNewEvent() || this._eventViewer.isEventTimeDirty();
   }

   @Override
   protected final void updateEventContents(Event event) {
      this._eventViewer.updateEventContents();
   }
}
