package net.rim.device.internal.system;

public final class OSEventLog {
   public static final int JAVA_EVENT_1;
   public static final int JAVA_EVENT_THREAD_ID_FIRST;
   public static final int JAVA_EVENT_THREAD_ID_LAST;
   public static final int JAVA_EVENT_LAST;

   private OSEventLog() {
   }

   public static final native boolean post(int var0);

   public static final native void setThreadEvent(Thread var0, int var1);
}
