package net.rim.device.apps.internal.calendar.eventprovider;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.calendar.controller.CalendarEventViewer;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;

class DeleteViewedEventVerb extends DeleteEventVerb {
   protected CalendarEventViewer _eventViewer;

   public DeleteViewedEventVerb(CalendarEventViewer eventViewer, Event event) {
      this(eventViewer, event, 0);
   }

   public DeleteViewedEventVerb(CalendarEventViewer eventViewer, Event event, long eventPartInstant) {
      super(event, eventPartInstant);
      this._eventViewer = eventViewer;
   }

   @Override
   public Object invoke(Object parameter) {
      Object deletedEvent = super.invoke(parameter);
      if (deletedEvent != null) {
         this._eventViewer.closeViewer(false);
      }

      return deletedEvent;
   }

   @Override
   protected boolean proceedWithDelete(Event eventToDelete) {
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
         return super.proceedWithDelete(eventToDelete);
      }
   }
}
