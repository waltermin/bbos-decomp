package net.rim.device.api.system;

import java.util.Vector;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.io.PushRegistryHelper;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.device.internal.system.CodeStore;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.LockEventLogger;
import net.rim.device.internal.system.SecurityManager;
import net.rim.device.internal.ui.MIDletApplication;
import net.rim.vm.Array;
import net.rim.vm.DebugSupport;
import net.rim.vm.Message;
import net.rim.vm.MessageQueue;
import net.rim.vm.Process;
import net.rim.vm.TooManyProcessesError;
import net.rim.vm.TraceBack;

final class ApplicationManagerImpl extends ApplicationManager implements ApplicationManagerInternal {
   private ApplicationManagerImpl$ApplicationProcessContainer _processes;
   private ApplicationProcess _foregroundProcess;
   private ApplicationProcess _consoleProcess;
   private ApplicationProcess _inHolsterInputProcess;
   private ApplicationProcess _redirectInputProcess;
   private ApplicationProcess _inputProcess;
   private ApplicationProcess _securityProcess;
   private ApplicationProcess _nativeSocketProcess;
   private int _securityLockSupercedingProcessId;
   private EventLogger _eventLogger;
   private Process _thisProcess;
   private Message _switchForegroundMessage;
   private Message _switchBackgroundMessage;
   private Message _refreshDisplayMessage;
   private Message _globalEventMessage;
   private SecurityManager _securityManager;
   private Vector _scheduledApps;
   private long _alarmTime = Long.MAX_VALUE;
   private ApplicationDescriptor _engScreenDescriptor;
   private Object[] _foregroundChangeListeners;
   private boolean _isFastReset;
   private boolean _fastResetHavePowerUp;
   private ApplicationManagerImpl$StartupGetMessageThread _startupThread;
   private ApplicationRegistry _applicationRegistry;
   private EventDispatchManager _eventDispatchManager;
   private int _currentPowerOnBehavior = 1;
   private boolean _usePowerOnBehaviourForScheduling;
   private boolean _dateTimeWasValidOnStartup;
   String[] _schedulingLog;
   int _schedulingLogIndex = 0;
   private long _lastQuincy;
   private static final boolean OUTPUT_APPLICATION_MESSAGES = true;
   private static final int TIER_TIMEOUT = 300000;
   private static final int MAX_TIMER_ID = -65536;

   final boolean postInternalGlobalEvent(int processId, long guid, int data0, int data1, Object object0, Object object1) {
      ApplicationProcess process = this.findProcess(processId);
      if (process == null) {
         return false;
      }

      this.postGlobalEventImpl(process, guid, data0, data1, object0, object1, true);
      return true;
   }

   final boolean postInternalGlobalEvent(long guid, int data0, int data1, Object object0, Object object1) {
      return this.postGlobalEventImpl(guid, data0, data1, object0, object1, true);
   }

   public final boolean postMessageToForegroundProcess(Message msg) {
      if (this._inputProcess != null) {
         this._inputProcess.postMessage(msg);
         return true;
      } else {
         return false;
      }
   }

   final EventLogger getEventLogger() {
      return this._eventLogger;
   }

   final boolean setForegroundProcess(ApplicationProcess newForegroundProcess, boolean bottomOfZOrder) {
      synchronized (this._processes) {
         if (!newForegroundProcess.isAlive()) {
            return false;
         }

         if (this._securityProcess != null
            && newForegroundProcess != this._securityProcess
            && newForegroundProcess.getProcessId() != this._securityLockSupercedingProcessId) {
            if (this._foregroundProcess == this._securityProcess && this._processes.numberOfProcesses() > 2) {
               this._processes.moveProcess(newForegroundProcess, 1);
            }

            return false;
         } else {
            int pid = newForegroundProcess.getProcessId();
            System.out.println("Foreground " + newForegroundProcess.toString());
            InternalServices.setVisibleProcess(pid);
            if ((this._redirectInputProcess == null || this._inputProcess != this._redirectInputProcess) && !DeviceInfo.isInHolster()) {
               this._inputProcess = newForegroundProcess;
            }

            if (this._foregroundProcess != newForegroundProcess) {
               if (this._foregroundProcess != null) {
                  this._foregroundProcess.postMessage(this._switchBackgroundMessage);
                  if (bottomOfZOrder) {
                     this._processes.moveProcessToRear(this._foregroundProcess);
                  }
               }

               this._processes.moveProcess(newForegroundProcess, 0);
               this._foregroundProcess = newForegroundProcess;
               if (this._foregroundChangeListeners != null) {
                  for (int i = this._foregroundChangeListeners.length - 1; i >= 0; i--) {
                     ((Runnable)this._foregroundChangeListeners[i]).run();
                  }
               }
            }

            return true;
         }
      }
   }

   final void requestForeground(ApplicationProcess newForegroundProcess, boolean wantsBackground) {
      synchronized (this._processes) {
         this._switchForegroundMessage.setSubMessage(0);
         if (newForegroundProcess == null || wantsBackground) {
            int numProcesses = this._processes.numberOfProcesses();
            int i = 0;
            if (this._foregroundProcess != null && this._foregroundProcess == newForegroundProcess) {
               i++;
               this._switchForegroundMessage.setSubMessage(1);
            }

            while (true) {
               if (i < numProcesses) {
                  newForegroundProcess = this._processes.getProcessAtIndex(i);
                  if (!newForegroundProcess.acceptsForeground()) {
                     i++;
                     continue;
                  }
               }

               if (newForegroundProcess == null || i == numProcesses) {
                  return;
               }
               break;
            }
         }

         if (newForegroundProcess.isAlive()) {
            newForegroundProcess.postMessage(this._switchForegroundMessage);
         }
      }
   }

   public final int runApplication(ApplicationDescriptor descriptor, boolean grabForeground, Thread testingThread) {
      int callingModule = TraceBack.getCallingModule(0);
      return this.runApplication(descriptor, grabForeground, testingThread, callingModule);
   }

   @Override
   public final boolean setSecurityManager(SecurityManager securityManager) {
      synchronized (this._processes) {
         if (this._securityManager != null) {
            return false;
         }

         this._securityManager = securityManager;
         return true;
      }
   }

   @Override
   public final SecurityManager getSecurityManager() {
      return this._securityManager;
   }

   @Override
   public final boolean setEngScreenDescriptor(ApplicationDescriptor descriptor) {
      synchronized (this._processes) {
         if (this._engScreenDescriptor != null) {
            return false;
         }

         this._engScreenDescriptor = descriptor;
         return true;
      }
   }

   @Override
   public final ApplicationDescriptor getEngScreenDescriptor() {
      return this._engScreenDescriptor;
   }

   @Override
   public final void setNativeSocketProcess() {
      synchronized (this._processes) {
         if (this._nativeSocketProcess != null) {
            throw new RuntimeException();
         }

         this._nativeSocketProcess = (ApplicationProcess)Process.currentProcess();
      }
   }

   @Override
   public final int runApplication(ApplicationDescriptor descriptor, Thread testingThread) {
      int callingModule = TraceBack.getCallingModule(0);
      return this.runApplication(descriptor, true, testingThread, callingModule);
   }

   @Override
   public final boolean setSecurityLockSupercedingProcess() {
      synchronized (this._processes) {
         this._securityLockSupercedingProcessId = ((ApplicationProcess)Process.currentProcess()).getProcessId();
         return true;
      }
   }

   @Override
   public final boolean setConsoleProcess() {
      synchronized (this._processes) {
         if (this._consoleProcess != null) {
            return false;
         }

         this._consoleProcess = (ApplicationProcess)Process.currentProcess();
         this._consoleProcess.haltDeviceIfThisProcessDies(true);
         return true;
      }
   }

   @Override
   public final boolean isSecurityLockSupercedingProcessForeground() {
      return this.getForegroundProcessId() == this._securityLockSupercedingProcessId;
   }

   @Override
   public final void enableSchedulingLog(boolean enable, int size) {
      synchronized (this._scheduledApps) {
         if (enable) {
            if (size < 1) {
               size = 1;
            }

            this._schedulingLog = new String[size];
         } else {
            this._schedulingLog = null;
         }
      }
   }

   @Override
   public final ApplicationProcess[] getProcesses() {
      synchronized (this._processes) {
         return this._processes.getCopyOfProcesses();
      }
   }

   @Override
   public final void redirectInput(Process process, boolean repaint) {
      synchronized (this._processes) {
         if (process != null && process.isAlive()) {
            this._redirectInputProcess = (ApplicationProcess)process;
            if (!DeviceInfo.isInHolster()) {
               this._inputProcess = this._redirectInputProcess;
            }
         } else {
            this._redirectInputProcess = null;
            if (!DeviceInfo.isInHolster()) {
               this._inputProcess = this._foregroundProcess;
            }

            if (repaint) {
               this.repaintForeground();
            }
         }
      }
   }

   @Override
   public final void repaintForeground() {
      try {
         this._foregroundProcess.postMessage(this._refreshDisplayMessage);
      } catch (NullPointerException var2) {
      }
   }

   @Override
   public final boolean postMessage(int pid, Message message) {
      synchronized (this._processes) {
         if (pid != -1) {
            Process process = Process.getProcess(pid);
            if (process != null && process.isAlive()) {
               ((ApplicationProcess)process).postMessage(message);
               return true;
            } else {
               return false;
            }
         } else {
            for (int i = this._processes.numberOfProcesses() - 1; i >= 0; i--) {
               this._processes.getProcessAtIndex(i).postMessage(message);
            }

            return true;
         }
      }
   }

   @Override
   public final void postMessage(Message message) {
      if (this._eventDispatchManager.notify(message)) {
         int processId = this._eventDispatchManager.getNotifyProcessId(message);
         synchronized (this._processes) {
            ApplicationProcess currentProcess = null;

            for (int i = this._processes.numberOfProcesses() - 1; i >= 0; i--) {
               currentProcess = this._processes.getProcessAtIndex(i);
               if (processId == -1 || currentProcess.getProcessId() == processId) {
                  currentProcess.postMessage(message);
               }
            }
         }
      }
   }

   @Override
   public final String[] getSchedulingLog() {
      synchronized (this._scheduledApps) {
         String[] result = new String[this._schedulingLog.length];

         for (int i = 0; i < this._schedulingLog.length; i++) {
            result[i] = this._schedulingLog[i];
         }

         return result;
      }
   }

   @Override
   public final void addForegroundChangeListener(Runnable r) {
      this._foregroundChangeListeners = ListenerUtilities.addListener(this._foregroundChangeListeners, r);
   }

   @Override
   public final void removeForegroundChangeListener(Runnable r) {
      this._foregroundChangeListeners = ListenerUtilities.removeListener(this._foregroundChangeListeners, r);
   }

   @Override
   public final Application getForegroundApplication() {
      synchronized (this._processes) {
         return this._foregroundProcess == null ? null : this._foregroundProcess.getApplication();
      }
   }

   @Override
   public final void resetAppDescriptorOverrides() {
      synchronized (this._processes) {
         int numProcesses = this._processes.numberOfProcesses();

         for (int i = 0; i < numProcesses; i++) {
            ApplicationDescriptor appDescriptor = this._processes.getProcessAtIndex(i).getApplicationDescriptor();
            appDescriptor.setOverrideNameResourceBundle(null);
            appDescriptor.setOverrideNameResourceId(-1);
         }
      }
   }

   @Override
   public final void updateAppDescriptor(String moduleName, String overrideNameResourceBundle, int overrideNameResourceId) {
      synchronized (this._processes) {
         int numProcesses = this._processes.numberOfProcesses();
         int i = 0;
         ApplicationProcess currentProcess = null;

         while (i < numProcesses) {
            currentProcess = this._processes.getProcessAtIndex(i);
            if (currentProcess.getModuleName().equals(moduleName)) {
               ApplicationDescriptor appDescriptor = currentProcess.getApplicationDescriptor();
               appDescriptor.setOverrideNameResourceBundle(overrideNameResourceBundle);
               appDescriptor.setOverrideNameResourceId(overrideNameResourceId);
               break;
            }

            i++;
         }
      }
   }

   @Override
   protected final int runApplicationImpl(ApplicationDescriptor descriptor, boolean grabForeground, int callingModule) {
      return this.runApplication(descriptor, grabForeground, null, callingModule);
   }

   @Override
   public final boolean setInHolsterInputProcess() {
      synchronized (this._processes) {
         if (this._inHolsterInputProcess != null) {
            return false;
         }

         this._inHolsterInputProcess = (ApplicationProcess)Process.currentProcess();
         return true;
      }
   }

   private final void logSchedulingEvent(String msg) {
      System.out.println(msg);
      if (this._schedulingLog != null) {
         if (this._schedulingLogIndex >= this._schedulingLog.length) {
            this._schedulingLogIndex = 0;
         }

         this._schedulingLog[this._schedulingLogIndex++] = Long.toString(System.currentTimeMillis()) + ":" + msg;
      }
   }

   private final int runApplication(ApplicationDescriptor descriptor, boolean grabForeground, Thread testingThread, int callingModule) {
      this.checkDescriptorSecurity(descriptor, callingModule);
      synchronized (this._processes) {
         String name = null;

         int var10000;
         try {
            name = descriptor.getModuleName();
            System.out.println("Starting " + name);
            ApplicationProcess process = this.findProcess(descriptor);
            if (process != null) {
               System.out.println(name + " already running");
               if (grabForeground) {
                  Application a = process.getApplication();
                  if (!(a instanceof MIDletApplication)) {
                     this.requestForeground(process, false);
                  } else {
                     MIDletApplication ma = (MIDletApplication)a;
                     ma.bringToForeground();
                  }
               }

               if (testingThread != null) {
                  process.addThread(testingThread);
               }

               return process.getProcessId();
            }

            process = new ApplicationProcess(this, descriptor, grabForeground);
            int moduleHandle = descriptor.getModuleHandle();
            int index = 0;
            if (CodeModuleManager.isLibrary(moduleHandle)) {
               index = 1;
            }

            if (!CodeStore.checkDRMTrailer(moduleHandle)) {
               throw new RuntimeException("DRM violation");
            }

            process.start(moduleHandle, name, descriptor.getArgs(), index);
            this._processes.addProcess(process);
            int pid = process.getProcessId();
            System.out.println("Started " + name + '(' + pid + ')');
            if (testingThread != null) {
               process.addThread(testingThread);
            }

            var10000 = pid;
         } catch (Exception ex) {
            String msg = "Error starting " + name + ": " + ex.getMessage();
            appError(msg);
            throw new ApplicationManagerException(msg);
         }

         return var10000;
      }
   }

   @Override
   public final boolean scheduleApplication(ApplicationDescriptor descriptor, long time, boolean absolute) {
      int callingModule = TraceBack.getCallingModule(0);
      this.checkDescriptorSecurity(descriptor, callingModule);
      if (time == Long.MAX_VALUE) {
         return false;
      }

      synchronized (this._scheduledApps) {
         int index = this._scheduledApps.indexOf(descriptor);
         if (time < 0) {
            if (index != -1) {
               this.logSchedulingEvent("Un-scheduling " + descriptor.getModuleName());
               this._scheduledApps.removeElementAt(index);
               this.setNextAlarm(true, null);
               return true;
            } else {
               return false;
            }
         } else {
            if (absolute && time < System.currentTimeMillis()) {
               return false;
            }

            if (!absolute) {
               this.logSchedulingEvent("App " + descriptor.getModuleName() + " is trying to schedule relative time:" + time);
            } else {
               this.logSchedulingEvent("App " + descriptor.getModuleName() + " is trying to schedule for time: " + time);
            }

            boolean force;
            if (index == -1) {
               this._scheduledApps.addElement(descriptor);
               force = false;
            } else {
               descriptor = (ApplicationDescriptor)this._scheduledApps.elementAt(index);
               force = true;
            }

            descriptor.setScheduledTime(time, absolute);
            return this.setNextAlarm(force, descriptor);
         }
      }
   }

   private final void checkDescriptorSecurity(ApplicationDescriptor descriptor, int callingModule) {
      String[] args = descriptor.getArgs();
      if (args != null && args.length != 0 && ControlledAccess.verifyRRISignature(descriptor.getModuleHandle())) {
         ControlledAccess.assertRRISignature(callingModule);
      }
   }

   @Override
   public final void setCurrentPowerOnBehavior(int powerOnBehavior) {
      this.assertChangeDeviceSettingsPermission();
      synchronized (this._scheduledApps) {
         int oldBehavior = this._currentPowerOnBehavior;
         switch (powerOnBehavior) {
            case 0:
               this._currentPowerOnBehavior = 0;
               break;
            case 1:
            case 2:
            default:
               this._currentPowerOnBehavior = powerOnBehavior;
         }

         if (oldBehavior != this._currentPowerOnBehavior) {
            this.logSchedulingEvent("Power ON Behaviour Change: Rescheduling Alarms.");
            this.setNextAlarm(true, null);
         }
      }
   }

   @Override
   public final long getNextAlarm(int powerOnBehavior) {
      synchronized (this._scheduledApps) {
         long nextAlarmTime = Long.MAX_VALUE;
         int numApps = this._scheduledApps.size();

         for (int i = 0; i < numApps; i++) {
            ApplicationDescriptor descriptor = (ApplicationDescriptor)this._scheduledApps.elementAt(i);
            int behavior = descriptor.getPowerOnBehavior();
            if ((behavior & this._currentPowerOnBehavior) > 0) {
               long appTime = descriptor.getNextScheduledTime();
               if (appTime < nextAlarmTime) {
                  nextAlarmTime = appTime;
               }
            }
         }

         return nextAlarmTime;
      }
   }

   private final boolean setNextAlarm(boolean reset, ApplicationDescriptor desiredApp) {
      ApplicationDescriptor descriptor = null;
      int numApps = this._scheduledApps.size();
      boolean result = true;
      boolean scheduledSuccessfully = false;

      while (!scheduledSuccessfully && numApps > 0) {
         long nextAlarmTime = Long.MAX_VALUE;
         int appIndex = -1;

         for (int i = 0; i < numApps; i++) {
            descriptor = (ApplicationDescriptor)this._scheduledApps.elementAt(i);
            long appTime = descriptor.getNextScheduledTime();
            if (this._usePowerOnBehaviourForScheduling) {
               int behavior = descriptor.getPowerOnBehavior();
               if ((behavior & this._currentPowerOnBehavior) == 0) {
                  appTime = Long.MAX_VALUE;
               }
            }

            if (appTime < nextAlarmTime && appTime != Long.MAX_VALUE) {
               nextAlarmTime = appTime;
               appIndex = i;
            }
         }

         if ((nextAlarmTime <= this._alarmTime || reset) && nextAlarmTime != Long.MAX_VALUE) {
            long adjustedNowTime = System.currentTimeMillis() + 15000;
            if (appIndex >= 0 && nextAlarmTime < adjustedNowTime) {
               descriptor = (ApplicationDescriptor)this._scheduledApps.elementAt(appIndex);
               this.logSchedulingEvent(
                  "Scheduling error for " + descriptor.getModuleName() + " for " + nextAlarmTime + " which is in the past or offers insufficient lag time"
               );
               nextAlarmTime = adjustedNowTime;
               descriptor.setScheduledTime(nextAlarmTime, true);
               this.logSchedulingEvent("Adjusting scheduling for " + descriptor.getModuleName() + " for " + nextAlarmTime);
            }

            if (InternalServices.setAlarm(nextAlarmTime)) {
               descriptor = (ApplicationDescriptor)this._scheduledApps.elementAt(appIndex);
               this.logSchedulingEvent("Scheduled " + descriptor.getModuleName() + " for " + nextAlarmTime);
               scheduledSuccessfully = true;
               this._alarmTime = nextAlarmTime;
            } else {
               descriptor = (ApplicationDescriptor)this._scheduledApps.elementAt(appIndex);
               if (descriptor == desiredApp) {
                  result = false;
               }

               this.logSchedulingEvent("Scheduling error for " + descriptor.getModuleName() + " for " + nextAlarmTime);
               this._scheduledApps.removeElementAt(appIndex);
               numApps = this._scheduledApps.size();
            }
         } else {
            if (appIndex != -1 && appIndex < this._scheduledApps.size()) {
               descriptor = (ApplicationDescriptor)this._scheduledApps.elementAt(appIndex);
               this.logSchedulingEvent("SetNextAlarm: Earliest Alarm already set: " + descriptor.getModuleName() + "[" + nextAlarmTime + "]");
            }

            scheduledSuccessfully = true;
         }
      }

      if (numApps == 0) {
         InternalServices.killAlarm();
         this._alarmTime = Long.MAX_VALUE;
      }

      return result;
   }

   private final ApplicationProcess findProcess(ApplicationDescriptor descriptor) {
      synchronized (this._processes) {
         ApplicationProcess currentProcess = null;

         for (int i = this._processes.numberOfProcesses() - 1; i >= 0; i--) {
            currentProcess = this._processes.getProcessAtIndex(i);
            if (currentProcess.getApplicationDescriptor().equals(descriptor)) {
               return currentProcess;
            }
         }

         return null;
      }
   }

   private final ApplicationProcess findProcess(int processId) {
      synchronized (this._processes) {
         ApplicationProcess currentProcess = null;

         for (int i = this._processes.numberOfProcesses() - 1; i >= 0; i--) {
            currentProcess = this._processes.getProcessAtIndex(i);
            if (currentProcess.getProcessId() == processId) {
               return currentProcess;
            }
         }

         return null;
      }
   }

   @Override
   public final void requestForeground(int processId) {
      ApplicationProcess newForeground = this.findProcess(processId);
      if (newForeground != null) {
         this.requestForeground(newForeground, false);
      }
   }

   @Override
   public final void requestForegroundForConsole() {
      synchronized (this._processes) {
         if (this._consoleProcess != null) {
            this.requestForeground(this._consoleProcess, false);
         }
      }
   }

   private static final void appError(String msg) {
      System.out.println(msg);
      EventLogger.logEvent(-7509200465648525729L, msg.getBytes());
   }

   @Override
   public final int getProcessId(ApplicationDescriptor descriptor) {
      ApplicationProcess process = this.findProcess(descriptor);
      return process == null ? -1 : process.getProcessId();
   }

   @Override
   public final int getForegroundProcessId() {
      synchronized (this._processes) {
         return this._foregroundProcess == null ? -1 : this._foregroundProcess.getProcessId();
      }
   }

   @Override
   public final int runApplication(ApplicationDescriptor descriptor, boolean grabForeground) {
      int callingModule = TraceBack.getCallingModule(0);
      return this.runApplication(descriptor, grabForeground, null, callingModule);
   }

   private final void processExited(boolean inStartup) {
      synchronized (this._processes) {
         boolean foregroundProcessDied = false;
         int numProcesses = this._processes.numberOfProcesses();
         int[] deadProcesses = new int[0];
         Throwable[] exceptions = new Throwable[0];

         for (int i = numProcesses - 1; i >= 0; i--) {
            ApplicationProcess process = this._processes.getProcessAtIndex(i);
            if (!process.isAlive()) {
               int pid = process.getProcessId();
               System.out.println("Exit " + process.toString());
               Throwable ex = process.getException();
               String msg = null;
               Arrays.add(deadProcesses, pid);
               Arrays.add(exceptions, ex);
               if (ex != null) {
                  if (ex instanceof PersistentContentException) {
                     PersistentContentException pce = (PersistentContentException)ex;
                     ApplicationDescriptor appDescriptor = process.getApplicationDescriptor();
                     String string = "Content Protection Error: An application is persisting plaintext data.\nApp: "
                        + appDescriptor.getName()
                        + "\nModule: "
                        + appDescriptor.getModuleName()
                        + "\nObject: 0x"
                        + Integer.toHexString(pce.getObjectId());
                     System.out.println(string);
                     InternalServices.catastrophicFailure(204, string);
                  }

                  if (!(ex instanceof ControlledAccessException)) {
                     msg = ex.getMessage();
                     msg = "Uncaught exception: " + (msg == null ? ex.toString() : msg);
                  } else {
                     String deniedPermission = ((ControlledAccessException)ex).getDeniedPermissionString();
                     if (deniedPermission != null) {
                        ApplicationDescriptor appDescriptor = process.getApplicationDescriptor();
                        String appName = appDescriptor.getName();
                        ResourceBundle rb = ResourceBundle.getBundle(8732645638888225014L, "net.rim.device.internal.resource.PlatformSecurity");
                        msg = MessageFormat.format(rb.getString(21), new String[]{appName, deniedPermission});
                     } else {
                        msg = ex.getMessage();
                        msg = "Uncaught exception: " + (msg == null ? ex.toString() : msg);
                     }
                  }

                  Object backTrace = process.getException();
                  int j = 0;

                  while (true) {
                     String str = TraceBack.getMessage(backTrace, j);
                     if (str == null) {
                        this.postInternalGlobalEvent(this._consoleProcess, 9056933960126321432L, 0, 0, msg, ex);
                        break;
                     }

                     System.out.println(str);
                     j++;
                  }
               }

               if (process == this._inHolsterInputProcess) {
                  this._inHolsterInputProcess = null;
               } else if (process == this._redirectInputProcess) {
                  this._redirectInputProcess = null;
               }

               boolean wasForeground = process == this._foregroundProcess;
               if (wasForeground) {
                  foregroundProcessDied = true;
               }

               if (process == this._securityProcess) {
                  if (ex != null) {
                     throw new RuntimeException("Security process failure");
                  }

                  this._securityProcess = null;
                  this._processes.notifyAll();
               }

               process.cleanup();
               numProcesses--;
               this._processes.removeProcess(i);
               boolean restartApp = false;
               ApplicationDescriptor ad = process.getApplicationDescriptor();

               try {
                  if ((ad.getFlags() & 4) != 0) {
                     restartApp = true;
                  }
               } catch (Throwable var22) {
               }

               if (restartApp) {
                  try {
                     String appName = ad.getModuleName();
                     if (!ControlledAccess.verifyCodeModuleSignature(ad.getModuleHandle(), 51)) {
                        appError("module " + appName + " missing RRI signature");
                     } else {
                        try {
                           System.out.println("Restarting " + appName);
                           this.runApplication(ad, wasForeground);
                           if (wasForeground) {
                              foregroundProcessDied = false;
                           }
                        } catch (ApplicationManagerException var19) {
                        } catch (TooManyProcessesError tmpe) {
                           appError("cannot restart module " + appName);
                        }

                        numProcesses = this._processes.numberOfProcesses();
                     }
                  } catch (Throwable var21) {
                  }
               }
            }
         }

         if (!inStartup) {
            this.postInternalGlobalEvent(-1270659756336956134L, 0, 0, deadProcesses, exceptions);
         }

         if (foregroundProcessDied) {
            this._foregroundProcess = null;

            try {
               this.requestForeground(null, false);
            } catch (ArrayIndexOutOfBoundsException var18) {
            }
         }
      }
   }

   @Override
   public final boolean wasDeviceTimeValidOnStartup() {
      return this._dateTimeWasValidOnStartup;
   }

   @Override
   public final ApplicationDescriptor[] getVisibleApplications() {
      synchronized (this._processes) {
         int numProcesses = this._processes.numberOfProcesses();
         ApplicationDescriptor[] descriptors = new ApplicationDescriptor[0];
         int i = 0;
         ApplicationProcess currentProcess = null;

         while (i < numProcesses) {
            currentProcess = this._processes.getProcessAtIndex(i);
            if (currentProcess.acceptsForeground()) {
               Arrays.add(descriptors, currentProcess.getApplicationDescriptor());
            }

            i++;
         }

         return descriptors;
      }
   }

   private final void lockSystemInternal(boolean force) {
      synchronized (this._processes) {
         if (this._securityManager != null && this._securityProcess == null) {
            int pid = this._securityManager.lockSystem(force);
            if (pid != -1) {
               this._securityProcess = this.findProcess(pid);
            }
         }
      }
   }

   @Override
   public final boolean isConsoleDescriptor(ApplicationDescriptor descriptor) {
      synchronized (this._processes) {
         return this._consoleProcess != null ? this._consoleProcess.getApplicationDescriptor() == descriptor : false;
      }
   }

   @Override
   public final boolean isInHolsterInputProcess() {
      synchronized (this._processes) {
         return this._inHolsterInputProcess == null ? false : this._inHolsterInputProcess == (ApplicationProcess)Process.currentProcess();
      }
   }

   @Override
   public final void launch(String url) {
      String name = null;
      int dot = url.indexOf(46);
      int question = url.indexOf(63);
      if (dot > question) {
         dot = -1;
      }

      String module;
      if (dot != -1) {
         module = url.substring(0, dot);
         if (question == -1) {
            name = url.substring(dot + 1);
         } else {
            name = url.substring(dot + 1, question);
         }
      } else if (question == -1) {
         module = url;
      } else {
         module = url.substring(0, question);
      }

      int handle = CodeModuleManager.getModuleHandle(module);
      if (handle <= 0) {
         throw new ApplicationManagerException("Module not found.");
      }

      ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(handle);
      if (descriptors != null && descriptors.length != 0) {
         ApplicationDescriptor descriptor = descriptors[0];
         if (name == null) {
            descriptor = descriptors[0];
         } else {
            int i;
            for (i = descriptors.length - 1; i >= 0; i--) {
               if (descriptors[i].getName().equals(name)) {
                  descriptor = descriptors[i];
                  break;
               }
            }

            if (i < 0) {
               throw new ApplicationManagerException("Entry point not found.");
            }
         }

         String[] args = null;
         if (question != -1) {
            StringTokenizer tokenizer = new StringTokenizer(url.substring(question + 1), '&');
            int count = tokenizer.countTokens();
            args = new String[count];

            for (int i = 0; i < count; i++) {
               args[i] = tokenizer.nextToken();
            }
         }

         descriptor = getNewDescriptor(descriptor, args);
         int callingModule = TraceBack.getCallingModule(0);
         this.runApplication(descriptor, true, null, callingModule);
      } else {
         throw new ApplicationManagerException("Module not an application.");
      }
   }

   @Override
   public final void lockSystem(boolean force) {
      this.assertChangeDeviceSettingsPermission();
      this.lockSystemInternal(force);
   }

   private final void assertIPCPermission() {
      ApplicationControl.assertIPCAllowed(true);
   }

   @Override
   public final boolean isSystemLocked() {
      return this._securityProcess != null;
   }

   @Override
   public final void unlockSystem() {
      this.assertChangeDeviceSettingsPermission();
      synchronized (this._processes) {
         if (this._securityProcess != null) {
            this.postInternalGlobalEvent(this._securityProcess, 1597563888101360867L, 0, 0, null, null);

            try {
               this._processes.wait();
            } catch (InterruptedException var4) {
            }
         }
      }
   }

   private static final ApplicationDescriptor getNewDescriptor(ApplicationDescriptor descriptor, String[] args) {
      int flags = descriptor.getFlags();
      if (args == null && (flags & 4) == 0) {
         return descriptor;
      }

      flags &= -5;
      return new ApplicationDescriptor(
         descriptor,
         descriptor.getName(),
         args,
         descriptor.getIcon(),
         descriptor.getPosition(),
         descriptor.getNameResourceBundle(),
         descriptor.getNameResourceId(),
         flags
      );
   }

   @Override
   protected final boolean postGlobalEventImpl(long guid, int data0, int data1, Object object0, Object object1, int callingModule) {
      return this.postGlobalEvent(guid, data0, data1, object0, object1, callingModule);
   }

   @Override
   public final boolean postGlobalEvent(long guid, int data0, int data1, Object object0, Object object1) {
      int callingModule = TraceBack.getCallingModule(0);
      return this.postGlobalEvent(guid, data0, data1, object0, object1, callingModule);
   }

   private final boolean postGlobalEvent(long guid, int data0, int data1, Object object0, Object object1, int callingModule) {
      this.assertIPCPermission();
      boolean internal = ControlledAccess.verifyRRISignature(callingModule);
      return this.postGlobalEventImpl(guid, data0, data1, object0, object1, internal);
   }

   private final void assertChangeDeviceSettingsPermission() {
      ApplicationControl.assertChangeDeviceSettingsPermitted(true, CommonResource.getBundle(), 10133);
   }

   private final boolean postGlobalEventImpl(long guid, int data0, int data1, Object object0, Object object1, boolean internal) {
      synchronized (this._processes) {
         this.setGlobalEventMessage(guid, data0, data1, object0, object1);
         ApplicationProcess currentProcess = null;

         for (int i = this._processes.numberOfProcesses() - 1; i >= 0; i--) {
            currentProcess = this._processes.getProcessAtIndex(i);
            this.postMessageToProcess(guid, currentProcess, this._globalEventMessage, internal);
         }

         this.setGlobalEventMessage(0, 0, 0, null, null);
      }

      this.processGlobalEvent(guid);
      return true;
   }

   @Override
   public final boolean postGlobalEvent(int processId, long guid, int data0, int data1, Object object0, Object object1) {
      this.assertIPCPermission();
      ApplicationProcess process = this.findProcess(processId);
      if (process == null) {
         return false;
      }

      int callingModule = TraceBack.getCallingModule(0);
      boolean internal = ControlledAccess.verifyRRISignature(callingModule);
      this.postGlobalEventImpl(process, guid, data0, data1, object0, object1, internal);
      return true;
   }

   ApplicationManagerImpl() {
      this._applicationRegistry = new ApplicationRegistry();
      this._eventLogger = new EventLogger();
      this._processes = new ApplicationManagerImpl$ApplicationProcessContainer();
      this._scheduledApps = new Vector();
      this._thisProcess = Process.currentProcess();
      this._switchForegroundMessage = new Message(0, 12);
      this._switchBackgroundMessage = new Message(0, 13);
      this._refreshDisplayMessage = new Message(0, 3);
      this._globalEventMessage = new Message(32, 0);
      this._eventDispatchManager = EventDispatchManager.getInstance();
      if (!InternalServices.isDateTimeValid()) {
         this._dateTimeWasValidOnStartup = false;
         DeviceInternal.setDateTime(1141171200000L);
      } else {
         this._dateTimeWasValidOnStartup = true;
      }

      this.run();
   }

   private final void postInternalGlobalEvent(ApplicationProcess process, long guid, int data0, int data1, Object object0, Object object1) {
      this.postGlobalEventImpl(process, guid, data0, data1, object0, object1, true);
   }

   private final void postGlobalEventImpl(ApplicationProcess process, long guid, int data0, int data1, Object object0, Object object1, boolean internal) {
      synchronized (this._processes) {
         if (process != null) {
            this.setGlobalEventMessage(guid, data0, data1, object0, object1);
            this.postMessageToProcess(guid, process, this._globalEventMessage, internal);
            this.setGlobalEventMessage(0, 0, 0, null, null);
         }
      }

      this.processGlobalEvent(guid);
   }

   private final void postMessageToProcess(long guid, ApplicationProcess process, Message message, boolean internal) {
      if (internal == process.isRIMProcess()) {
         process.postMessage(message);
      } else {
         if (internal
            && (
               guid == 7207871974803693937L
                  || guid == 8877632280522743328L
                  || guid == 3596208183088439728L
                  || guid == -8040378802380461050L
                  || guid == -7464003439710973532L
                  || guid == 8508406279413621091L
                  || guid == -594020114676189989L
                  || guid == 1309561383038111736L
                  || guid == -8392006003204551101L
                  || guid == 4681343386835470834L
                  || guid == 945659952435832745L
                  || guid == -4394903006263251010L
                  || guid == -4220058463650496006L
                  || guid == -583230596614878690L
                  || guid == 1348796660760556312L
                  || guid == 8288627527798139133L
                  || guid == -5256071285987383000L
                  || guid == 6213587377148297993L
                  || guid == 1077267820605375385L
                  || guid == 2522898683889177438L
                  || guid == -5448760422790860711L
                  || guid == -3769281743063593175L
                  || guid == 6498096261923284925L
                  || guid == 158775118060600435L
                  || guid == -860845403685493259L
                  || guid == 8478935834746748823L
                  || guid == -7853136852381124900L
                  || guid == 5061624963542184609L
            )) {
            process.postMessage(message);
         }
      }
   }

   private final void setGlobalEventMessage(long guid, int data0, int data1, Object object0, Object object1) {
      this._globalEventMessage.setEvent((int)guid);
      this._globalEventMessage.setSubMessage((int)(guid >> 32));
      this._globalEventMessage.setData0(data0);
      this._globalEventMessage.setData1(data1);
      this._globalEventMessage.setObject0(object0);
      this._globalEventMessage.setObject1(object1);
   }

   static final long getGlobalEventGUID(Message message) {
      return ((message.getSubMessage() & 4294967295L) << 32) + (message.getEvent() & 4294967295L);
   }

   private final void processGlobalEvent(long guid) {
      if (guid == 3596208183088439728L || guid == 8877632280522743328L) {
         synchronized (this._scheduledApps) {
            this.logSchedulingEvent("Device Time/TimeZone Change: Rescheduling Alarms.");
            this.setNextAlarm(true, null);
         }
      }
   }

   private final void runOnStartup(int[] handles, boolean inStartup) {
      Vector[] tiers = new Vector[8];
      Vector localizationTier = new Vector();
      CodeSigningKey rri = CodeSigningKey.getBuiltInKey(51);
      CodeSigningKey rrt = CodeSigningKey.getBuiltInKey(5526098);

      for (int i = 0; i < handles.length; i++) {
         int handle = handles[i];

         try {
            ApplicationDescriptor[] descriptors;
            if ((descriptors = CodeModuleManager.getApplicationDescriptors(handle)) != null) {
               for (int j = 0; j < descriptors.length; j++) {
                  ApplicationDescriptor descriptor = descriptors[j];
                  if ((descriptor.getFlags() & 1) != 0) {
                     int tier = descriptor.getStartupTier();
                     if (0 <= tier && tier < 8) {
                        if (!ControlledAccess.verifyCodeModuleSignature(handle, rri)) {
                           if (tier <= 5) {
                              appError("module " + descriptor.getModuleName() + " missing RRI signature");
                              continue;
                           }

                           if (!ControlledAccess.verifyCodeModuleSignature(handle, rrt)) {
                              appError("module " + descriptor.getModuleName() + " missing RRT signature");
                              continue;
                           }
                        }

                        if (tier == 0) {
                           byte[] localeData = CodeModuleManager.getModuleLanguageData(handle);
                           if (localeData != null && localeData.length > 0) {
                              String localeString = new String(localeData);
                              if (!localeString.toLowerCase().startsWith("en")) {
                                 localizationTier.addElement(descriptor);
                                 continue;
                              }
                           }
                        }

                        if (tiers[tier] == null) {
                           tiers[tier] = new Vector();
                        }

                        tiers[tier].addElement(descriptor);
                     }
                  }
               }
            }
         } catch (IllegalArgumentException var20) {
         }
      }

      long[] timeTiers = new long[10];

      for (int i = 0; i < 8; i++) {
         timeTiers[i] = InternalServices.getUptime();
         Vector tier = tiers[i];
         if (tier != null) {
            System.out.println("Starting tier " + i);
            this.startTier(tier, inStartup, rri);
            this.waitForTier(inStartup, i == 7);
            if (i == 0 && !localizationTier.isEmpty()) {
               this.startTier(localizationTier, inStartup, rri);
               this.waitForTier(inStartup, false);
            }
         }
      }

      timeTiers[8] = InternalServices.getUptime();
      System.out.println("MIDlet PushRegistry startup");

      for (int i = 0; i < handles.length; i++) {
         int handle = handles[i];

         try {
            if (CodeModuleManager.isMidlet(handle)) {
               ApplicationDescriptor[] array = CodeModuleManager.getApplicationDescriptors(handle);
               if (array != null) {
                  ApplicationDescriptor original = array[0];
                  String[] args = original.getArgs();
                  Array.resize(args, 2);
                  args[1] = args[0];
                  args[0] = PushRegistryHelper.APPLICATION_DESCRIPTOR_ARG_PUSH_REGISTRY_WORK;
                  ApplicationDescriptor doPushRegistryWorkDescriptor = new ApplicationDescriptor(array[0], array[0].getName(), args);

                  try {
                     this.runApplication(doPushRegistryWorkDescriptor, false);
                  } catch (ApplicationManagerException var18) {
                  }
               }
            }
         } catch (IllegalArgumentException e) {
            EventLogger.logEvent(-7509200465648525729L, e.toString().getBytes(), 5);
         }
      }

      System.out.println("MIDlet PushRegistry startup finished");
      timeTiers[9] = InternalServices.getUptime();
      if (inStartup) {
         StringBuffer buffer = new StringBuffer();
         buffer.append("Startup Time ");
         long total = 0;

         for (int index = 0; index < timeTiers.length - 1; index++) {
            total += timeTiers[index + 1] - timeTiers[index];
         }

         buffer.append(total);
         buffer.append("ms\n");

         for (int index = 0; index < timeTiers.length - 1; index++) {
            buffer.append(" tier ");
            buffer.append(index);
            buffer.append(" took ");
            buffer.append(timeTiers[index + 1] - timeTiers[index]);
            buffer.append("ms\n");
         }

         buffer.deleteCharAt(buffer.length() - 1);
         String message = buffer.toString();
         EventLogger.logEvent(-7509200465648525729L, message.getBytes(), 0);
         System.out.println(message);
      }
   }

   private final void startTier(Vector tier, boolean inStartup, CodeSigningKey rri) {
      for (int j = tier.size() - 1; j >= 0; j--) {
         ApplicationDescriptor descriptor = (ApplicationDescriptor)tier.elementAt(j);

         try {
            this.runApplication(descriptor, false);
         } catch (ApplicationManagerException var11) {
         } catch (TooManyProcessesError tmpe) {
            System.out.println("Too many processes; waiting...");
            this.waitForTier(inStartup, true);

            try {
               this.runApplication(descriptor, false);
            } catch (ApplicationManagerException var9) {
            } catch (TooManyProcessesError tmpe1) {
               int handle = descriptor.getModuleHandle();
               if (ControlledAccess.verifyCodeModuleSignature(handle, rri)) {
                  throw tmpe1;
               }

               appError("Unable to start " + descriptor.getModuleName() + "; too many processes");
            }
         }
      }
   }

   private final void waitForTier(boolean inStartup, boolean allowTimeout) {
      if (inStartup) {
         if (allowTimeout) {
            Process.waitForIdle(300000);
         } else {
            Process.waitForIdle();
         }

         this.processExited(true);
         net.rim.vm.Memory.quickGC();
         LowMemoryManager.poll();
      }
   }

   private final void holsterStateChange(boolean inHolster) {
      synchronized (this._processes) {
         if (inHolster) {
            this._inputProcess = this._inHolsterInputProcess;
            if (this._securityManager != null && this._securityManager.isLockRequired()) {
               LockEventLogger.logLockEvent(1281912684);
               this.lockSystemInternal(true);
            }
         } else {
            this._inputProcess = this._redirectInputProcess == null ? this._foregroundProcess : this._redirectInputProcess;
         }
      }
   }

   @Override
   public final boolean inStartup() {
      return this._startupThread != null;
   }

   private final void run() {
      this._startupThread = new ApplicationManagerImpl$StartupGetMessageThread();
      this._startupThread.start();
      int[] handles = CodeModuleManager.getModuleHandles();
      this.runOnStartup(handles, true);
      int[] var6 = null;
      this._applicationRegistry.kickAllWaitingThreads();
      net.rim.vm.Memory.persistentGC();
      net.rim.vm.Memory.RAMRecover();
      startupDone();
      if (this._consoleProcess == null) {
         int handle = CodeModuleManager.getModuleHandle("net_rim_app_manager_console");
         if (handle != 0) {
            ApplicationDescriptor[] ads = CodeModuleManager.getApplicationDescriptors(handle);

            try {
               this.runApplication(ads[0], true);
            } catch (ApplicationManagerException var5) {
            }
         }
      } else {
         this.requestForeground(this._consoleProcess, false);
      }

      MessageQueue startupMessages = this._startupThread.getMessages();
      Message message = new Message();

      while (startupMessages.dequeue(message, false)) {
         this.processMessage(message);
      }

      MessageQueue var8 = null;
      this._startupThread = null;
      Thread.currentThread().setPriority(10);

      while (true) {
         message.get();
         this.processMessage(message);
      }
   }

   private final void checkForKeyboardLag(Message message) {
      if (!InternalServices.isDeviceSecure()) {
         if (!DebugSupport.isDebuggerAttached()) {
            if (!DeviceInfo.isSimulator()) {
               long keyTime = message.getData1();
               long lastHourglass = InternalServices.getLastHourglass();
               long upTime = InternalServices.getUptime();
               if (upTime >= 600000) {
                  if (keyTime <= upTime && lastHourglass <= upTime) {
                     if (lastHourglass <= keyTime) {
                        int lag = (int)upTime - message.getData1();
                        if (upTime - keyTime >= 3000) {
                           if (this._lastQuincy == 0 || upTime - this._lastQuincy >= 3600000) {
                              this._lastQuincy = upTime;
                              long LOGWORTHY_REPORT_REQUEST = 2888237357036234703L;
                              String lagString = "Lag(D):" + Integer.toString(lag);
                              System.out.println(lagString);
                              System.out.println(message);
                              this.postInternalGlobalEvent(LOGWORTHY_REPORT_REQUEST, 0, 0, lagString, null);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private final void processMessage(Message message) {
      label140:
      switch (message.getDevice()) {
         case 0:
            switch (message.getEvent()) {
               case 0:
               case 2:
               case 12:
               case 13:
               case 14:
               case 15:
               case 16:
               case 17:
                  break label140;
               case 1:
               default:
                  this.processExited(false);
                  return;
               case 3:
                  this.repaintForeground();
                  return;
               case 4:
                  this.postInternalGlobalEvent(-5179361672050507927L, 0, 0, null, null);
                  return;
               case 5:
                  int handle = message.getSubMessage();
                  int[] handles = new int[]{handle};
                  this.runOnStartup(handles, false);
                  this.postInternalGlobalEvent(256826950193107649L, handle, 0, null, null);
                  return;
               case 6:
                  this.postInternalGlobalEvent(-4232371946002803201L, 0, 0, null, null);
                  return;
               case 7:
                  if (this._securityProcess == null) {
                     this.postInternalGlobalEvent(this._consoleProcess, 7563637690172082503L, message.getData0(), 0, null, null);
                  }

                  return;
               case 8:
                  if (this._securityProcess == null && this._engScreenDescriptor != null) {
                     try {
                        this.runApplication(this._engScreenDescriptor, true);
                        return;
                     } catch (ApplicationManagerException var10) {
                     }
                  }

                  return;
               case 9:
                  this.postInternalGlobalEvent(945659952435832745L, 0, 0, null, null);
                  return;
               case 10:
                  if (this._foregroundProcess != null) {
                     this._foregroundProcess.postMessage(message);
                  }

                  return;
               case 11:
                  this.postInternalGlobalEvent(8877632280522743328L, 0, 0, null, null);
                  return;
               case 18:
                  if (this._securityProcess == null) {
                     this.postInternalGlobalEvent(this._consoleProcess, -8249535590121003989L, message.getData0(), 0, null, null);
                  }

                  return;
               case 19:
                  if (message.getSubMessage() == 0) {
                     Radio.deactivateWAFs(message.getData0());
                     return;
                  }

                  Radio.activateWAFs(message.getData0());
                  return;
            }
         case 1:
            switch (message.getEvent()) {
               case 259:
                  this._usePowerOnBehaviourForScheduling = true;
                  synchronized (this._scheduledApps) {
                     this.logSchedulingEvent("System Powering Off: Rescheduling Alarms.");
                     this.setNextAlarm(true, null);
                  }

                  this.lockSystemInternal(false);
                  break label140;
               case 260:
                  EventLogger.logEvent(-7509200465648525729L, "POWER_UP".getBytes(), 0);
                  this._usePowerOnBehaviourForScheduling = false;
                  this.logSchedulingEvent("System Powering On: Executing Alarms.");
                  this.executeScheduledApplications();
                  if (this._isFastReset && !this._fastResetHavePowerUp) {
                     this._fastResetHavePowerUp = true;
                  } else {
                     this.holsterStateChange(DeviceInfo.isInHolster());
                     this.lockSystemInternal(false);
                  }
                  break label140;
               case 265:
                  message.setSubMessage(DeviceInfo.getBatteryStatus());
                  break label140;
               case 511:
                  this._usePowerOnBehaviourForScheduling = false;
                  this._isFastReset = true;
                  this._fastResetHavePowerUp = false;
                  this.repaintForeground();
                  synchronized (this._scheduledApps) {
                     this.logSchedulingEvent("Fast VM Reset: Rescheduling Alarms.");
                     this.setNextAlarm(true, null);
                  }
               default:
                  break label140;
            }
         case 2:
         case 26:
         case 27:
            try {
               this.checkForKeyboardLag(message);
               this._inputProcess.postMessage(message);
               ApplicationProcess securityProcess = this._securityProcess;
               if (securityProcess != null && message.getDevice() == 2) {
                  message.setDevice(49);
                  securityProcess.postMessage(message);
                  return;
               }
            } catch (NullPointerException var7) {
            }

            return;
         case 3:
            switch (message.getEvent()) {
               case 767:
                  break label140;
               case 768:
               default:
                  this.logSchedulingEvent("RTC Expired: Running Alarms.");
                  this.executeScheduledApplications();
                  return;
               case 769:
                  if (this._securityManager != null && this._securityManager.isLockRequired()) {
                     if (!this.isSystemLocked()) {
                        LockEventLogger.logLockEvent(1281977448);
                     }

                     this.lockSystemInternal(false);
                  }
                  break label140;
            }
         case 4:
            int osTimerId = message.getEvent();
            int processId = ApplicationProcess.getProcessIdFromOSTimerId(osTimerId);
            if (processId == 0) {
               return;
            }

            ApplicationProcess process = this.findProcess(processId);
            if (process == null) {
               InternalServices.killTimer(osTimerId);
               return;
            }

            process.postMessage(message);
            return;
         case 7:
            this.holsterStateChange(message.getEvent() == 1793);
            break;
         case 38:
            if (message.getEvent() == 9729) {
               int width = message.getSubMessage() & 65535;
               int height = message.getSubMessage() >> 16 & 65535;
               this.postInternalGlobalEvent(-2650018024822507413L, width, height, null, null);
            }
            break;
         case 44:
            if (message.getEvent() == 2306) {
               this.lockSystemInternal(true);
            }
            break;
         case 46:
            int processId = message.getDataLength();
            if (processId != 0) {
               ApplicationProcess process = this.findProcess(processId);
               if (process == null) {
                  return;
               }

               process.postMessage(message);
               return;
            }
            break;
         case 57:
            if (this._nativeSocketProcess != null) {
               this._nativeSocketProcess.postMessage(message);
            }

            return;
      }

      this.postMessage(message);
   }

   private final void executeScheduledApplications() {
      synchronized (this._scheduledApps) {
         for (int i = this._scheduledApps.size() - 1; i >= 0; i--) {
            ApplicationDescriptor app = (ApplicationDescriptor)this._scheduledApps.elementAt(i);
            long appTime = app.getScheduledTime();
            long curTime = System.currentTimeMillis();
            this.logSchedulingEvent(
               "Checking " + app.getModuleName() + " Scheduled For:" + appTime + " against alarm time:" + this._alarmTime + " current time:" + curTime
            );
            if (appTime <= curTime + 30000) {
               try {
                  this.runApplication(app, true);
               } catch (ApplicationManagerException var10) {
               }

               this._scheduledApps.removeElementAt(i);
            }
         }

         this.logSchedulingEvent("RTC Expired: Rescheduling Alarms.");
         this.setNextAlarm(true, null);
      }
   }

   private static final native void startupDone();
}
