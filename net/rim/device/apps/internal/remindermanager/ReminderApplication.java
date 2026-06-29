package net.rim.device.apps.internal.remindermanager;

import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.reminders.ReminderManager;
import net.rim.device.internal.proxy.Proxy;

final class ReminderApplication extends UiApplication {
   private int _foregroundable;
   private static final long TIME_AND_RUNNABLE_TABLE_KEY;

   @Override
   protected final boolean acceptsForeground() {
      return this._foregroundable > 0;
   }

   final synchronized void setForegroundable(boolean foregroundable) {
      if (foregroundable) {
         this._foregroundable++;
      } else {
         this._foregroundable--;
      }
   }

   public static final void main(String[] args) {
      if (args != null && args.length > 0 && "init".equals(args[0])) {
         ReminderApplication app = new ReminderApplication();
         ReminderManagerImpl rm = new ReminderManagerImpl();
         rm.init(app);
         ReminderDisplayManager dm = new ReminderDisplayManager();
         dm.init(app, rm);
         NotificationsManager.registerNotificationsEngineListener(-7539463754156969819L, dm);
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         ar.put(-7539463754156969819L, rm);
         ar.put(813899564474876953L, new ReminderModelFactoryImpl());
         app.setStyle(1);
         app.enterEventDispatcher();
         System.exit(0);
      }

      proceedWithNextReminder(args);
   }

   public static final void proceedWithNextReminder(String[] args) {
      Proxy p = Proxy.getInstance();
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      Object[] timeAndRunnable = (Object[])reg.get(-8995044480416259562L);
      ReminderManager reminderManager = ReminderManager.getInstance();
      synchronized (timeAndRunnable) {
         long[] times = (long[])timeAndRunnable[0];
         Runnable[] runnables = (Object[])timeAndRunnable[1];
         int max = runnables.length;
         long minimumTime = Long.MAX_VALUE;
         int indexScheduled = -1;

         for (int i = 0; i < max; i++) {
            if (runnables[i] != null) {
               long adjustedCurrentTime = reminderManager.getAdjustedCurrentTimeMillis();
               if (times[i] <= adjustedCurrentTime) {
                  p.invokeLater(runnables[i]);
                  runnables[i] = null;
                  times[i] = Long.MAX_VALUE;
               } else if (minimumTime > times[i]) {
                  minimumTime = times[i];
                  indexScheduled = i;
               }
            }
         }

         ApplicationDescriptor deepTimerApp = getDeepTimerApp();
         if (deepTimerApp != null) {
            while (minimumTime != Long.MAX_VALUE && !ApplicationManager.getApplicationManager().scheduleApplication(deepTimerApp, minimumTime, true)) {
               reminderManager.logReminderEvent(1145393222);
               runnables[indexScheduled] = null;
               times[indexScheduled] = Long.MAX_VALUE;
               minimumTime = Long.MAX_VALUE;

               for (int i = 0; i < max; i++) {
                  if (runnables[i] != null && minimumTime > times[i]) {
                     minimumTime = times[i];
                     indexScheduled = i;
                  }
               }
            }
         }
      }
   }

   private static final ApplicationDescriptor getDeepTimerApp() {
      ApplicationDescriptor descriptor = ApplicationDescriptor.currentApplicationDescriptor();
      return (ApplicationDescriptor)(descriptor == null ? null : new Object(descriptor, null));
   }
}
