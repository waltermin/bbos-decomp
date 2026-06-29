package net.rim.device.apps.api.vcal;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.task.TaskCollectionHolder;
import net.rim.device.apps.api.task.TaskModel;

public class VCal$AddToDevice extends Verb {
   Object _vcalItem;
   String _name;

   VCal$AddToDevice(Object vcalItem) {
      super(598288);
      this._vcalItem = vcalItem;
      ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
      if (this._vcalItem instanceof Event) {
         this._name = rb.getString(4);
      } else {
         if (this._vcalItem instanceof TaskModel) {
            this._name = rb.getString(5);
         }
      }
   }

   @Override
   public String toString() {
      return this._name;
   }

   @Override
   public Object invoke(Object parameter) {
      if (this._vcalItem instanceof Event) {
         CalDB calDB = (CalDB)CalendarServiceManager.getInstance().findCalendarDatabase((Event)this._vcalItem);
         calDB.add(this._vcalItem);
         return null;
      }

      if (this._vcalItem instanceof TaskModel) {
         TaskCollectionHolder.getTaskCollection().add(this._vcalItem);
      }

      return null;
   }
}
