package net.rim.device.api.system;

public final class RIMGlobalMessagePoster {
   private static ApplicationManagerImpl _ami = (ApplicationManagerImpl)ApplicationManager.getApplicationManager();

   private RIMGlobalMessagePoster() {
   }

   public static final boolean postGlobalEvent(long guid) {
      return postGlobalEvent(guid, 0, 0, null, null);
   }

   public static final boolean postGlobalEvent(long guid, int data0, int data1) {
      return postGlobalEvent(guid, data0, data1, null, null);
   }

   public static final boolean postGlobalEvent(long guid, int data0, int data1, Object object0, Object object1) {
      return _ami.postInternalGlobalEvent(guid, data0, data1, object0, object1);
   }

   public static final boolean postGlobalEvent(int processId, long guid, int data0, int data1, Object object0, Object object1) {
      return _ami.postInternalGlobalEvent(processId, guid, data0, data1, object0, object1);
   }
}
