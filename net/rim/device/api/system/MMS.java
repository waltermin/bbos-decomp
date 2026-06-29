package net.rim.device.api.system;

import net.rim.device.api.itpolicy.ITPolicy;

public final class MMS {
   public static final long BACKDOOR_INJECT_MMS_SERVICE_BOOK = -2734094174038131697L;

   public static final boolean isSupported() {
      try {
         Class.forName("net.rim.device.apps.internal.mms.model.MMSMessageModelImpl");
         return true;
      } catch (ClassNotFoundException cnfe) {
         return false;
      }
   }

   public static final boolean isEnabled() {
      return isSupported() && isITPolicyEnabled() && (MMSStatus.getInstance().hasServiceBook() || wasPreviouslyEnabled());
   }

   private static final boolean isITPolicyEnabled() {
      return !ITPolicy.getBoolean(21, 7, false);
   }

   public static final void setServiceBookStatus(boolean hasWAPServiceBook) {
      MMSStatus.getInstance().setServiceBookStatus(hasWAPServiceBook);
   }

   public static final void onEnabled(Runnable action) {
      if (isSupported()) {
         MMSStatus.getInstance().onEnabled(action);
      }
   }

   private static final boolean wasPreviouslyEnabled() {
      long MMS_OPTIONS_DATA_KEY = 572030951534635290L;
      return RIMPersistentStore.getPersistentObject(MMS_OPTIONS_DATA_KEY).getContents() != null;
   }

   public static final MMS$ClientOptions getClientOptions() {
      long MMS_CLIENT_OPTIONS_GUID = -4248845697481338338L;
      return (MMS$ClientOptions)ApplicationRegistry.getApplicationRegistry().get(MMS_CLIENT_OPTIONS_GUID);
   }
}
