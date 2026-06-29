package net.rim.device.apps.internal.calendar.eventprovider;

import java.util.TimeZone;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.RecurUtilities;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.internal.i18n.CommonResource;

public class DeleteEventVerb extends CalendarEventVerb {
   private Event _event;
   private long _eventPartInstant;
   private DeleteEventVerb$DeleteEventVerbListener _listener;

   public DeleteEventVerb(Event event) {
      super(CommonResource.getBundle(), 17, 598352);
      this._event = event;
   }

   public DeleteEventVerb(Event recurringEvent, long eventPartInstant) {
      this(recurringEvent);
      this._eventPartInstant = eventPartInstant;
   }

   public void setDeleteEventVerbListener(DeleteEventVerb$DeleteEventVerbListener listener) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public Object invoke(Object parameter) {
      ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
      Application app = Application.getApplication();
      boolean wasBackground = !app.isForeground();
      Event eventToReturn = null;
      long relatedLUID = this._event.getRelatedLUID();
      if (relatedLUID != 0 || this._event.isRecurring() && this._eventPartInstant != 0) {
         String[] choices = new String[]{rb.getString(163), rb.getString(164), rb.getString(650)};
         int[] values = new int[]{0, 1, -1, -805044213, 775162112, 774909491, 3420721, -805044199, 1699878656, 1918985587, 1226860643, 1867325550};
         if (wasBackground) {
            app.requestForeground();
         }

         switch (Dialog.ask(rb.getString(162), choices, values, -1)) {
            case -1:
               break;
            case 0:
            default:
               eventToReturn = this.deleteSeries();
               break;
            case 1:
               eventToReturn = this.deleteOccurrence();
         }

         if (wasBackground) {
            app.requestBackground();
         }
      } else if (CalendarOptions.getOptions().getConfirmDelete()) {
         if (wasBackground) {
            app.requestForeground();
         }

         if (Dialog.ask(2, rb.getString(160), -1) == 3) {
            eventToReturn = this.deleteEvent(this._event);
         }

         if (wasBackground) {
            app.requestBackground();
         }
      } else {
         eventToReturn = this.deleteEvent(this._event);
      }

      return eventToReturn;
   }

   protected boolean proceedWithDelete(Event eventToDelete) {
      return this._listener != null ? this._listener.proceedWithDelete(eventToDelete) : true;
   }

   private Event deleteSeries() {
      Event originalEvent = this._event;
      long relatedLUID = originalEvent.getRelatedLUID();
      if (relatedLUID != 0) {
         CalDB calDB = CalendarServiceManager.getInstance().findCalendarDatabase(originalEvent);
         Event recurrenceSeries;
         synchronized (calDB.getLockObject()) {
            recurrenceSeries = (Event)calDB.get(relatedLUID);
         }

         return this.deleteEvent(recurrenceSeries);
      } else {
         return originalEvent.isRecurring() ? this.deleteEvent(originalEvent) : null;
      }
   }

   private Event deleteOccurrence() {
      Event originalEvent = this._event;
      if (originalEvent.getRelatedLUID() != 0) {
         return this.deleteEvent(originalEvent);
      }

      if (!originalEvent.isRecurring() || this._eventPartInstant == 0) {
         return null;
      }

      if (!this.proceedWithDelete(this._event)) {
         return null;
      }

      CalDB calDB = CalendarServiceManager.getInstance().findCalendarDatabase(originalEvent);
      synchronized (calDB.getLockObject()) {
         Recur recur = originalEvent.getRecurrenceCopy();
         if (RecurUtilities.deleteRecurringEventIfEmpty(originalEvent, recur, this._eventPartInstant, true, calDB)) {
            return this._event;
         }

         originalEvent = (Event)ObjectGroup.expandGroup(originalEvent);
         if (!originalEvent.isAnInclusion(this._eventPartInstant)) {
            recur.addExclusion(this._eventPartInstant);
         } else {
            recur.removeInclusion(this._eventPartInstant);
         }

         originalEvent.setRecurrence(recur);
         calDB.add(originalEvent);
         return this._event;
      }
   }

   private Event deleteEvent(Event event) {
      if (event != null && this.proceedWithDelete(event)) {
         CalDB calDB = CalendarServiceManager.getInstance().findCalendarDatabase(event);
         synchronized (calDB.getLockObject()) {
            EventUtilities.removeEvent(event, false);
            long parentLUID = event.getRelatedLUID();
            if (parentLUID != 0) {
               Event parentEvent = (Event)calDB.get(parentLUID);
               if (parentEvent != null) {
                  Recur recur = parentEvent.getReadOnlyRecurrence();
                  TimeZone tz = TimeZone.getTimeZone(parentEvent.getTimeZoneID());
                  if (RecurUtilities.deleteRecurringEventIfEmpty(parentEvent, recur, parentEvent.getStartDate(tz), false, calDB)) {
                     return parentEvent;
                  }
               }
            }

            RecurUtilities.scanAndDeleteRelatedEvents(event);
            return event;
         }
      } else {
         return null;
      }
   }
}
