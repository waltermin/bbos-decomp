package net.rim.device.internal.system;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.vm.Process;

public class RIMProcessLauncher implements GlobalEventListener {
   private int _pid = 0;
   public static final long GUID;
   public static final int FLAG_START_APP;
   public static final int FLAG_START_UI_APP;
   public static final int FLAG_WAIT_FOR_TERMINATION;
   private static Object _processTermination = new Object();
   private static RIMProcessLauncher _instance = new RIMProcessLauncher();
   private static RIMProcessLauncher$Data _data;

   private RIMProcessLauncher() {
   }

   private void addAsGlobalEventListener(int pid) {
      Application app = Application.getApplication();
      if (app != null) {
         this._pid = pid;
         app.addGlobalEventListener(this);
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -1270659756336956134L && object0 instanceof int[]) {
         int[] deadProcesses = (int[])object0;

         for (int i = deadProcesses.length - 1; i >= 0; i--) {
            if (deadProcesses[i] == this._pid) {
               synchronized (_processTermination) {
                  _processTermination.notify();
               }
            }
         }
      }
   }

   public static int launch(Runnable runnable) {
      return launch(runnable, 0);
   }

   public static int launchApplication(RIMProcessLauncher$ApplicationCallback callback) {
      return launch(new RIMProcessLauncher$1(), 1, callback);
   }

   public static int launch(Runnable runnable, int flags) {
      return launch(runnable, flags, null);
   }

   private static int launch(Runnable runnable, int flags, RIMProcessLauncher$ApplicationCallback callback) {
      int pid = 0;
      if (runnable == null) {
         throw new NullPointerException();
      }

      try {
         if (_data == null) {
            _data = (RIMProcessLauncher$Data)ApplicationRegistry.getApplicationRegistry().waitFor(-7293675776592666707L);
         }

         Process process = null;
         synchronized (_data) {
            String arg = Integer.toString(RIMProcessLauncher$Data.access$008(_data));
            _data.setFlags(flags);
            _data.setRunnable(runnable);
            if (callback != null) {
               _data.setCallBack(callback);
            }

            ApplicationDescriptor descriptor = new ApplicationDescriptor(_data.getDescriptor(), new String[]{arg});
            pid = ApplicationManager.getApplicationManager().runApplication(descriptor);
            if ((flags & 4) != 0) {
               process = Process.getProcess(pid);
               _instance.addAsGlobalEventListener(pid);
            }

            synchronized (_data.getDescriptor()) {
               if (_data.getRunnable() != null) {
                  _data.getDescriptor().wait();
               }
            }
         }

         while (process != null) {
            synchronized (_processTermination) {
               if (!process.isAlive()) {
                  Process var16 = null;
                  break;
               }

               _processTermination.wait();
            }
         }
      } catch (Exception var15) {
      }

      return pid;
   }

   static {
      _instance = _instance;
   }
}
