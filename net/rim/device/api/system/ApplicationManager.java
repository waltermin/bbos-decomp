package net.rim.device.api.system;

import net.rim.vm.Process;
import net.rim.vm.TraceBack;

public class ApplicationManager {
   private static ApplicationManager _appManager;

   ApplicationManager() {
      _appManager = this;
   }

   public boolean setInHolsterInputProcess() {
      throw null;
   }

   public boolean isInHolsterInputProcess() {
      throw null;
   }

   public void launch(String _1) {
      throw null;
   }

   public void lockSystem(boolean _1) {
      throw null;
   }

   public boolean isSystemLocked() {
      throw null;
   }

   public void unlockSystem() {
      throw null;
   }

   public final int runApplication(ApplicationDescriptor descriptor) {
      int callingModule = TraceBack.getCallingModule(0);
      return this.runApplicationImpl(descriptor, true, callingModule);
   }

   protected int runApplicationImpl(ApplicationDescriptor descriptor, boolean grabForeground, int callingModule) {
      return this.runApplication(descriptor, grabForeground);
   }

   public int runApplication(ApplicationDescriptor _1, boolean _2) {
      throw null;
   }

   public boolean scheduleApplication(ApplicationDescriptor _1, long _2, boolean _4) {
      throw null;
   }

   public void requestForeground(int _1) {
      throw null;
   }

   public void requestForegroundForConsole() {
      throw null;
   }

   public boolean isConsoleDescriptor(ApplicationDescriptor _1) {
      throw null;
   }

   public int getProcessId(ApplicationDescriptor _1) {
      throw null;
   }

   public int getForegroundProcessId() {
      throw null;
   }

   public ApplicationDescriptor[] getVisibleApplications() {
      throw null;
   }

   public final boolean postGlobalEvent(long guid) {
      int callingModule = TraceBack.getCallingModule(0);
      return this.postGlobalEventImpl(guid, 0, 0, null, null, callingModule);
   }

   public final boolean postGlobalEvent(long guid, int data0, int data1) {
      int callingModule = TraceBack.getCallingModule(0);
      return this.postGlobalEventImpl(guid, data0, data1, null, null, callingModule);
   }

   public boolean postGlobalEvent(long _1, int _3, int _4, Object _5, Object _6) {
      throw null;
   }

   protected boolean postGlobalEventImpl(long guid, int data0, int data1, Object object0, Object object1, int callingModule) {
      return this.postGlobalEvent(guid, data0, data1, object0, object1);
   }

   public boolean postGlobalEvent(int _1, long _2, int _4, int _5, Object _6, Object _7) {
      throw null;
   }

   public boolean inStartup() {
      throw null;
   }

   public void setCurrentPowerOnBehavior(int _1) {
      throw null;
   }

   public long getNextAlarm(int _1) {
      throw null;
   }

   public static final ApplicationManager getApplicationManager() {
      Process process = Process.currentProcess();
      return !(process instanceof ApplicationProcess) ? _appManager : ((ApplicationProcess)process).getApplicationManager();
   }

   public boolean wasDeviceTimeValidOnStartup() {
      throw null;
   }
}
