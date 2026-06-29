package net.rim.device.apps.internal.remindermanager;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.reminders.ReminderElement;
import net.rim.device.apps.api.reminders.ReminderManager;
import net.rim.device.apps.api.ui.CommonResources;

final class ReminderDisplayManager$Snooze extends Verb {
   private int _snoozeChoiceIndex;

   public ReminderDisplayManager$Snooze(int snoozeChoiceIndex) {
      super(0);
      this._snoozeChoiceIndex = snoozeChoiceIndex;
   }

   @Override
   public final Object invoke(Object parameter) {
      if (parameter instanceof ReminderElement) {
         ReminderElement element = (ReminderElement)parameter;
         long time = System.currentTimeMillis() + ReminderManager.SNOOZE_CHOICES[this._snoozeChoiceIndex];
         Runnable runnable = new ReminderDisplayManager$ProceedRunnable(element, true, time);
         ReminderDeepTimerScheduler.schedule(time, runnable);
      }

      return null;
   }

   @Override
   public final String toString() {
      String snoozeString = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar").getString(645);
      return MessageFormat.format(snoozeString, new String[]{CommonResources.getString(ReminderManager.SNOOZE_CHOICE_STRINGS[this._snoozeChoiceIndex])});
   }
}
