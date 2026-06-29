package net.rim.device.apps.internal.remindermanager;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.vm.Array;

public final class ReminderDeepTimerScheduler {
   private static final int MIN_NUMBER_DEEP_TIMERS;
   private static final long KEY;
   private static String DEEPTIMER_MODULE_NAME = "net_rim_bb_remindermanager";
   public static final long TIME_ADJUSTMENT;

   private ReminderDeepTimerScheduler() {
   }

   private static final Runnable scheduleEarliest(Object[] timeAndRunnable) {
      synchronized (timeAndRunnable) {
         long[] times = (long[])timeAndRunnable[0];
         Runnable[] runnables = (Object[])timeAndRunnable[1];
         int max = runnables.length;
         Runnable result = null;
         boolean scheduled = false;

         while (!scheduled) {
            long minimumTime = Long.MAX_VALUE;
            int index = -1;

            for (int i = 0; i < max; i++) {
               if (runnables[i] != null && minimumTime > times[i]) {
                  minimumTime = times[i];
                  index = i;
               }
            }

            ApplicationDescriptor deepTimerApp = getDeepTimerApp();
            if (minimumTime != Long.MAX_VALUE) {
               if (deepTimerApp != null) {
                  scheduled = ApplicationManager.getApplicationManager().scheduleApplication(deepTimerApp, minimumTime, true);
               }

               if (!scheduled) {
                  times[index] = Long.MAX_VALUE;
                  runnables[index] = null;
               } else {
                  result = runnables[index];
               }
            } else {
               scheduled = true;
               if (deepTimerApp != null) {
                  ApplicationManager.getApplicationManager().scheduleApplication(deepTimerApp, Long.MIN_VALUE, true);
               }
            }
         }

         return result;
      }
   }

   public static final boolean schedule(long time, Runnable runnable) {
      boolean result = true;
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      Object[] timeAndRunnable;
      synchronized (reg) {
         timeAndRunnable = (Object[])reg.get(-8995044480416259562L);
         if (timeAndRunnable == null) {
            timeAndRunnable = new Object[]{new long[5], new Object[5]};
            reg.put(-8995044480416259562L, timeAndRunnable);
         }
      }

      synchronized (timeAndRunnable) {
         purgeStaleRunnables(timeAndRunnable);
         long[] times = (long[])timeAndRunnable[0];
         Runnable[] runnables = (Object[])timeAndRunnable[1];
         boolean timerAvailable = false;
         int max = runnables.length;

         for (int i = 0; i < max; i++) {
            if (runnables[i] == null) {
               times[i] = time;
               runnables[i] = runnable;
               timerAvailable = true;
               resizeTimeAndRunnableList(timeAndRunnable);
               break;
            }
         }

         if (!timerAvailable) {
            Array.resize(runnables, max + 1);
            Array.resize(times, max + 1);
            times[max] = time;
            runnables[max] = runnable;
            max++;
         }

         Runnable r = scheduleEarliest(timeAndRunnable);
         result = r != null;
         if (result) {
            result = false;

            for (int i = 0; i < runnables.length; i++) {
               if (runnables[i] == runnable) {
                  result = true;
                  break;
               }
            }
         }

         return result;
      }
   }

   public static final void cancelAll() {
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      Object[] timeAndRunnable = (Object[])reg.get(-8995044480416259562L);
      if (timeAndRunnable != null) {
         synchronized (timeAndRunnable) {
            long[] times = (long[])timeAndRunnable[0];
            Runnable[] runnables = (Object[])timeAndRunnable[1];
            int max = runnables.length;

            for (int i = 0; i < max; i++) {
               times[i] = Long.MAX_VALUE;
               runnables[i] = null;
            }

            if (max > 5) {
               Array.resize(runnables, 5);
               Array.resize(times, 5);
            }

            scheduleEarliest(timeAndRunnable);
         }
      }
   }

   public static final void cancel(Runnable runnableToCancel) {
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      Object[] timeAndRunnable = (Object[])reg.get(-8995044480416259562L);
      if (timeAndRunnable != null) {
         synchronized (timeAndRunnable) {
            long[] times = (long[])timeAndRunnable[0];
            Runnable[] runnables = (Object[])timeAndRunnable[1];
            int max = runnables.length;

            for (int i = 0; i < max; i++) {
               if (runnableToCancel.equals(runnables[i])) {
                  times[i] = Long.MAX_VALUE;
                  runnables[i] = null;
                  break;
               }
            }

            resizeTimeAndRunnableList(timeAndRunnable);
            scheduleEarliest(timeAndRunnable);
         }
      }
   }

   private static final void resizeTimeAndRunnableList(Object[] timeAndRunnable) {
      synchronized (timeAndRunnable) {
         long[] times = (long[])timeAndRunnable[0];
         Runnable[] runnables = (Object[])timeAndRunnable[1];
         int max = runnables.length;
         int earliest = max;

         for (int i = 0; i < max; i++) {
            if (runnables[i] == null) {
               earliest = i;
               break;
            }
         }

         for (int i = 4; i < max; i++) {
            if (runnables[i] != null) {
               while (earliest < max && runnables[earliest] != null) {
                  earliest++;
               }

               if (earliest < max) {
                  times[earliest] = times[i];
                  runnables[earliest] = runnables[i];
                  times[i] = Long.MAX_VALUE;
                  runnables[i] = null;
               }
            }
         }

         if (earliest < max && earliest < 5 && max > 5) {
            if (earliest < 5) {
               earliest = 5;
            }

            Array.resize(runnables, earliest);
            Array.resize(times, earliest);
         }
      }
   }

   private static final void purgeStaleRunnables(Object[] timeAndRunnable) {
      long currentTime = System.currentTimeMillis();
      synchronized (timeAndRunnable) {
         long[] times = (long[])timeAndRunnable[0];
         Runnable[] runnables = (Object[])timeAndRunnable[1];
         int max = runnables.length;

         for (int i = 0; i < max; i++) {
            if (times[i] <= currentTime) {
               times[i] = Long.MAX_VALUE;
               runnables[i] = null;
               break;
            }
         }
      }
   }

   private static final ApplicationDescriptor getDeepTimerApp() {
      int moduleHandle = CodeModuleManager.getModuleHandle(DEEPTIMER_MODULE_NAME);
      ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(moduleHandle);
      if (descriptors.length < 1) {
         return null;
      }

      ApplicationDescriptor descriptor = descriptors[0];
      if (descriptor != null) {
         descriptor = (ApplicationDescriptor)(new Object(descriptor, null));
         descriptor.setPowerOnBehavior(2);
      }

      return descriptor;
   }

   public static final long getAdjustedCurrentTimeMillis() {
      return System.currentTimeMillis() + 30000;
   }
}
