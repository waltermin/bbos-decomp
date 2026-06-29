package net.rim.device.apps.internal.calendar.eventprovider;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.controller.CalendarEventViewer;
import net.rim.device.apps.api.calendar.controller.CalendarEventViewerProvider;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

public class EditEventVerb extends CalendarEventVerb implements DialogClosedListener {
   private int _editRecurringDialogResult;
   private Event _event;
   private long _eventPartInstant;

   public EditEventVerb(Event event) {
      super(CommonResource.getBundle(), 15, 598288);
      this._event = event;
   }

   public EditEventVerb(Event recurringEvent, long eventPartInstant) {
      this(recurringEvent);
      this._eventPartInstant = eventPartInstant;
      this._event = recurringEvent;
   }

   public EditEventVerb(Event event, ResourceBundle rb, int displayStringId, int ordering) {
      super(rb, displayStringId, ordering);
      this._event = event;
   }

   public void setPartInstance(long eventPartInstance) {
      this._eventPartInstant = eventPartInstance;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public Object invoke(Object parameter) {
      boolean var15 = false /* VF: Semaphore variable */;

      try {
         var15 = true;
         this._event = (Event)ObjectGroup.expandGroup(this._event);
         var15 = false;
      } finally {
         if (var15) {
            LowMemoryManager.poll();
            return null;
         }
      }

      CalendarServiceManager calendarServiceManager = CalendarServiceManager.getInstance();
      Event eventToReturn = null;
      long relatedLUID = this._event.getRelatedLUID();
      CalDB calDB = calendarServiceManager.findCalendarDatabase(this._event);
      Event realEvent = (Event)calDB.get(this._event.getLUID());
      Application app = Application.getApplication();
      boolean wasBackground = !app.isForeground();
      if (realEvent == null) {
         CalDB baseCalDB = calendarServiceManager.getBaseSystemCalendarDatabase();
         realEvent = (Event)baseCalDB.get(this._event.getLUID());
         if (realEvent == null) {
            ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
            Dialog eventDeletedDialog = (Dialog)(new Object(0, rb.getString(113), 0, Bitmap.getPredefinedBitmap(2), 33554432));
            UiApplication.getUiApplication().pushGlobalScreen(eventDeletedDialog, 500, 0);
         } else {
            EventUtilities.moveEvent(
               realEvent, calendarServiceManager.getBaseSystemCalendarService(), calendarServiceManager.findCalendarService(realEvent), false, true
            );
         }
      }

      if (realEvent != null) {
         if (relatedLUID != 0 || this._event.isRecurring() && this._eventPartInstant != 0) {
            ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
            String[] choices = new Object[]{rb.getString(171), rb.getString(172), CommonResource.getString(19)};
            int[] values = new int[]{0, 1, -1, -805044213, 775162112, 774909491, 3420721, -805044199, 1699878656, 1918985587, 1226860643, 1867325550};
            Dialog editRecurringDialog = (Dialog)(new Object(rb.getString(170), choices, values, 1, Bitmap.getPredefinedBitmap(1), 33554432));
            editRecurringDialog.setDialogClosedListener(this);
            UiApplication.getUiApplication().pushGlobalScreen(editRecurringDialog, 500, 1);
            switch (this._editRecurringDialogResult) {
               case -1:
                  break;
               case 0:
               default:
                  eventToReturn = this.editSeries();
                  break;
               case 1:
                  eventToReturn = this.editOccurrence();
            }
         } else {
            if (wasBackground) {
               app.requestForeground();
            }

            eventToReturn = this.editEvent(this._event, true);
         }
      }

      if (wasBackground) {
         app.requestBackground();
      }

      return eventToReturn;
   }

   private Event editSeries() {
      Event originalEvent = this._event;
      long relatedLUID = originalEvent.getRelatedLUID();
      if (relatedLUID != 0) {
         CalDB calDB = CalendarServiceManager.getInstance().findCalendarDatabase(originalEvent);
         Event recurrenceSeries;
         synchronized (calDB.getLockObject()) {
            recurrenceSeries = (Event)calDB.get(relatedLUID);
         }

         return recurrenceSeries == null ? null : this.editEvent((Event)ObjectGroup.expandGroup(recurrenceSeries), true);
      } else {
         return originalEvent.isRecurring() ? this.editEvent(originalEvent, true) : null;
      }
   }

   private Event editOccurrence() {
      Event originalEvent = this._event;
      if (originalEvent.getRelatedLUID() != 0) {
         return this.editEvent(originalEvent, false);
      } else if (originalEvent.isRecurring() && this._eventPartInstant != 0) {
         Event eventToEdit = EventUtilities.createEventInstanceFromRecurrence(originalEvent, this._eventPartInstant);
         CalendarEventViewerProvider cevp = (CalendarEventViewerProvider)eventToEdit;
         ContextObject context = (ContextObject)(new Object());
         ContextObject.put(context, 245, originalEvent);
         CalendarEventViewer ev = cevp.getCalendarEventViewer(context);
         Verb[] verbs = new Object[]{
            new SaveViewedEventVerb(ev, originalEvent, this._eventPartInstant, eventToEdit),
            new DeleteViewedEventVerb(ev, originalEvent, this._eventPartInstant)
         };
         ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
         ev.openViewer(rb.getString(130), verbs, 0, -1, false);
         return eventToEdit;
      } else {
         return null;
      }
   }

   private Event editEvent(Event event, boolean allowRecurrence) {
      CalendarEventViewerProvider cevp = (CalendarEventViewerProvider)event;
      CalendarEventViewer ev = cevp.getCalendarEventViewer(null);
      Verb[] verbs = new Object[]{new SaveViewedEventVerb(ev, event, false), new DeleteViewedEventVerb(ev, event)};
      ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
      ev.openViewer(rb.getString(130), verbs, 0, -1, allowRecurrence);
      return event;
   }

   @Override
   public void dialogClosed(Dialog dialog, int choice) {
      this._editRecurringDialogResult = choice;
      Application app = Application.getApplication();
      boolean wasBackground = !app.isForeground();
      if (wasBackground) {
         app.requestForeground();
      }
   }
}
