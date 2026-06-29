package net.rim.device.internal.streamingnatives;

import java.io.IOException;
import net.rim.device.api.system.Application;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.InternalServices;

public final class StreamingNatives {
   public static final int STREAMING_SESSION_WATERMARK = 3329;
   public static final int STREAMING_SESSION_NEW_DATA = 3331;
   public static final int STREAMING_SESSION_SOURCE_DONE = 3332;
   public static final int STREAMING_SESSION_SOURCE_ERROR = 3333;
   public static final int STREAMING_SESSION_SINK_ERROR = 3334;
   public static final int STREAMING_SESSION_CLOSED = 3335;
   public static final int STREAMING_SESSION_LOST_DATA_IND = 3336;
   public static final int STREAMING_SESSION_SINK_DONE = 3337;

   private StreamingNatives() {
   }

   public static final void addListener(Application app, StreamingNativesListener listener) {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(13) == null) {
            dispatchManager.setDispatcher(13, new StreamingNatives$StreamingNativesEventDispatcher());
         }
      }

      app.addListener(13, listener);
   }

   public static final native int endSink(int var0);

   public static final native int endSource(int var0);

   public static final native boolean isBufferEmpty(int var0);

   public static final boolean isSupported() {
      return InternalServices.isSoftwareCapable(1);
   }

   public static final native int lostData(int var0, int var1);

   public static final int readBuffer(int handle, byte[] data, int startOffset, int length) {
      try {
         return readBuffer0(handle, data, startOffset, length);
      } catch (IOException e) {
         throw new IllegalStateException();
      }
   }

   public static final native int readBuffer0(int var0, byte[] var1, int var2, int var3);

   public static final void removeListener(Application app, StreamingNativesListener listener) {
      app.removeListener(13, listener);
   }

   public static final native int sendNotification(int var0, int var1);

   public static final native int startSink(int var0);

   public static final native int startSource(int var0);

   public static final int writeBuffer(int handle, byte[] data, int startOffset, int length) {
      try {
         return writeBuffer0(handle, data, startOffset, length);
      } catch (IOException e) {
         throw new IllegalStateException();
      }
   }

   public static final native int writeBuffer0(int var0, byte[] var1, int var2, int var3);
}
