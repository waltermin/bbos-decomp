package net.rim.device.apps.api.vcal;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.calendar.controller.CalendarEventViewer;
import net.rim.device.apps.api.calendar.controller.CalendarEventViewerProvider;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.task.TaskModel;

public class VCal$OpenVcal extends Verb {
   Object _vcalItem;
   String _name;

   VCal$OpenVcal(Object vcalItem) {
      super(598288);
      this._vcalItem = vcalItem;
      ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
      if (this._vcalItem instanceof Event) {
         this._name = rb.getString(630);
      } else {
         if (this._vcalItem instanceof TaskModel) {
            this._name = rb.getString(631);
         }
      }
   }

   @Override
   public String toString() {
      return this._name;
   }

   @Override
   public Object invoke(Object parameter) {
      ContextObject context = ContextObject.castOrCreate(parameter);
      if (this._vcalItem instanceof CalendarEventViewerProvider) {
         CalendarEventViewerProvider viewerProvider = (CalendarEventViewerProvider)this._vcalItem;
         CalendarEventViewer viewer = viewerProvider.getCalendarEventViewer(context);
         if (viewer != null) {
            ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
            String title = rb.getString(3);
            Verb[] verbs = new Verb[]{new VCal$AddToDevice(this._vcalItem)};
            viewer.openViewer(title, verbs, -1, -1, true);
         }
      }

      return null;
   }
}
