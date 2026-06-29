package net.rim.device.apps.internal.task;

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.reminders.ReminderElement;
import net.rim.device.apps.api.reminders.ReminderProvider;

final class CollectionUpdateWrapper extends Verb {
   private Object _record;
   private Verb _innerVerb;

   CollectionUpdateWrapper(Object record, Verb innerVerb) {
      super(innerVerb.getOrdering());
      this._record = record;
      this._innerVerb = innerVerb;
   }

   @Override
   public final String toString() {
      return this._innerVerb.toString();
   }

   @Override
   public final Object invoke(Object p) {
      if (p instanceof Object) {
         ReminderElement re = (ReminderElement)p;
         ReminderProvider rp = re.getReminderProvider();
         this._record = rp.getReminder(re.getReminderID());
         if (this._record == null) {
            return null;
         }
      }

      Object record = this._record;
      if (ObjectGroup.isInGroup(record)) {
         record = ObjectGroup.expandGroup(record);
      }

      Object result = this._innerVerb.invoke(record);
      if (result != null) {
         ObjectGroup.createGroupIgnoreTooBig(record);
         Task.getInstance().getUICollection().update(this._record, record);
      } else {
         Task.getInstance().getUICollection().remove(this._record);
      }

      if (p instanceof Object) {
         ((ContextObject)p).setPrivateFlag(-3866311304884942232L, 1);
      }

      return result;
   }
}
