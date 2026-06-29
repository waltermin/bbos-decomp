package net.rim.device.internal.system;

public final class OSEventLog {
   public static final int JAVA_EVENT_1 = 49152;
   public static final int JAVA_EVENT_THREAD_ID_FIRST = 49152;
   public static final int JAVA_EVENT_THREAD_ID_LAST = 49663;
   public static final int JAVA_EVENT_LAST = 53247;

   private OSEventLog() {
   }

   public static final native boolean post(int var0);

   public static final native void setThreadEvent(Thread var0, int var1);
}
