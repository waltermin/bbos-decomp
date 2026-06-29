package net.rim.vm;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Phone;

public class Process {
   private int _pid = nextProcessId();
   private Throwable _exception;
   private int _state;
   private int _moduleHandle;
   private String _moduleName;
   private String[] _args;
   private int _haltOnProcessDeath;
   private String _displayName;
   protected MessageQueue _messageQueue = new MessageQueue();
   public static final int ID_NONE;

   private static native int nextProcessId();

   private static native boolean createProcess(Process var0, Thread var1);

   private static native void destroy(int var0);

   private static native Process getProcessFromThread(Thread var0);

   private static native Process getProcessFromId(int var0);

   private static native boolean addProcess(Process var0, Thread var1);

   private static native String loadModule(int var0, int var1, byte[] var2);

   private static native boolean start(int var0, Thread var1, String[] var2, int var3);

   private static native void startThreadInProcess(int var0, Thread var1);

   public void addThread(Thread thread) {
      startThreadInProcess(this._pid, thread);
   }

   public static native void bootComplete();

   public static void waitForIdle() {
      waitForIdle(0);
   }

   public static native void waitForIdle(long var0);

   public static native int getLastIdleCounter();

   public static native void killProcessIfThisThreadDies(boolean var0);

   public static native void interrupt(Thread var0);

   public static native void currentThreadSuicide();

   public void haltDeviceIfThisProcessDies(boolean on) {
      this._haltOnProcessDeath = on ? 1 : 0;
      if (on) {
         this._messageQueue.setSystemProcess();
      }
   }

   private static native void setHeapQuota(int var0, int var1);

   private static native int getHeapSize(int var0);

   public static native void setExceptionLogging(int var0, boolean var1);

   public static native void registerAppRegistry(Object var0);

   public static native Object getAppRegistry();

   public native void setThreadLimit(int var1);

   protected Process() {
   }

   public void setHeapQuota(int heapSize) {
      setHeapQuota(this._pid, heapSize);
   }

   public int getHeapSize() {
      return getHeapSize(this._pid);
   }

   public void setExceptionLogging(boolean extraLogging) {
      setExceptionLogging(this._pid, extraLogging);
   }

   public static int getThrowableHash(Throwable e) {
      int hash = getThrowableHash0(e);
      String msg = e.getMessage();
      if (msg != null) {
         hash ^= msg.hashCode();
      }

      return hash;
   }

   public static native int getThrowableHash0(Throwable var0);

   public Throwable getException() {
      return this._exception;
   }

   public synchronized Thread start(int moduleHandle, String moduleName, String[] args) {
      return this.start(moduleHandle, moduleName, args, 0);
   }

   public synchronized Thread start(int moduleHandle, String moduleName, String[] args, int index) {
      if (this._state != 0) {
         throw new IllegalArgumentException("Process.start() already called");
      } else {
         this._state = 1;
         this._moduleHandle = moduleHandle;
         this._moduleName = moduleName;
         this._displayName = moduleName + '(' + this._pid + ')';
         this._args = args;
         Thread t = new Thread();
         if (!createProcess(this, t)) {
            return null;
         } else {
            String error = loadModule(this._pid, moduleHandle, moduleName.getBytes());
            if (error != null) {
               throw new ModuleNotFoundException(error);
            } else if (!start(this._pid, t, args, index)) {
               throw new IllegalArgumentException("Can't find entry point");
            } else {
               return t;
            }
         }
      }
   }

   public synchronized void start(Thread thread) {
      if (this._state != 0) {
         throw new IllegalArgumentException("Process.start() already called");
      }

      this._state = 1;
      if (thread.isAlive()) {
         throw new IllegalArgumentException("Can't start process with running thread");
      }

      createProcess(this, thread);
   }

   public void destroy() {
      this.destroy(null);
   }

   public void destroy(Throwable ex) {
      this._exception = ex;

      try {
         destroy(this._pid);
      } catch (IllegalArgumentException var3) {
      }
   }

   public final int getProcessId() {
      return this._pid;
   }

   public static Process currentProcess() {
      return getProcess(Thread.currentThread());
   }

   public static Process getProcess(int pid) {
      return getProcessFromId(pid);
   }

   public final synchronized boolean isAlive() {
      return this._state == 1;
   }

   public static Process getProcess(Thread thread) {
      Process p = getProcessFromThread(thread);
      if (p != null) {
         return p;
      }

      p = new Process();
      return !addProcess(p, thread) ? null : p;
   }

   public final int getModuleHandle() {
      return this._moduleHandle;
   }

   public String getModuleName() {
      return this._moduleName;
   }

   public static native int getModuleHandle(String var0);

   public String[] getArgs() {
      return this._args;
   }

   public static native boolean isModuleEldestSibling(int var0);

   public static native byte[] getModuleData(int var0, String var1);

   public static native String getModuleName(int var0, int var1);

   public static native int getModuleHandle(int var0, int var1);

   public MessageQueue getMessageQueue() {
      return this._messageQueue;
   }

   @Override
   public String toString() {
      return this._displayName;
   }

   public static native Class classForName(String var0, int var1);

   public static long ensureMinimumIdleTime(long minimumIdleTime) {
      if (minimumIdleTime <= 0) {
         throw new IllegalArgumentException();
      }

      if (peekMessage(false)) {
         return 0;
      }

      if (Phone.isSupported() && Phone.getInstance().isActive()) {
         return 0;
      }

      long idleTime = DeviceInfo.getIdleTime();
      return idleTime < minimumIdleTime ? 0 : idleTime;
   }

   public static native boolean peekMessage(boolean var0);

   public static native long getRecentCPUTime(Process var0);

   public static native long getRecentCPUTime(Thread var0);

   public static native long getTotalCPUTime(Process var0);

   public static native long getTotalCPUTime(Thread var0);
}
