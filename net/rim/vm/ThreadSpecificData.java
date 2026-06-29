package net.rim.vm;

public class ThreadSpecificData {
   private WeakReference[] data = new WeakReference[3];
   public static final int ECMA_GLOBAL_OBJECT = 0;
   public static final int APPCONTROL_GLOBAL_OBJECT = 1;
   public static final int JAVASCRIPT_ENGINE_GLOBAL_OBJECT = 2;
   private static final int NUM_DATA_SLOTS = 3;

   private static native ThreadSpecificData getData(Thread var0);

   private static native void setData(Thread var0, ThreadSpecificData var1);

   private static ThreadSpecificData get(Thread thread) {
      ThreadSpecificData tsd = getData(thread);
      if (tsd == null) {
         tsd = new ThreadSpecificData();
         setData(thread, tsd);
      }

      return tsd;
   }

   public static Object get(Thread thread, int index) {
      ThreadSpecificData tsd = get(thread);
      WeakReference wr = tsd.data[index];
      return wr == null ? null : wr.get();
   }

   public static void set(Thread thread, int index, Object obj) {
      ThreadSpecificData tsd = get(thread);
      WeakReference wr = tsd.data[index];
      if (wr == null) {
         wr = new WeakReference(obj);
         tsd.data[index] = wr;
      } else {
         wr.set(obj);
      }
   }
}
