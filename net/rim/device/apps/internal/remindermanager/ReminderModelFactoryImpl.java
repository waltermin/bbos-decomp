package net.rim.device.apps.internal.remindermanager;

import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.api.reminders.ReminderModelFactory;

final class ReminderModelFactoryImpl extends ReminderModelFactory {
   @Override
   protected final ReminderModel createInstance(boolean absolute) {
      return absolute ? new ReminderModelAbsoluteImpl() : new ReminderModelRelativeImpl();
   }
}
