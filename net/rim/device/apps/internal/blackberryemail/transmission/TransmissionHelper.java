package net.rim.device.apps.internal.blackberryemail.transmission;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;

public final class TransmissionHelper {
   private TransmissionHelper$TransmissionSendRunnable _sendRunnable = new TransmissionHelper$TransmissionSendRunnable(
      TransmissionServiceManager.get(8399767144006445082L)
   );
   private static long MESSAGING_TRANSMISSION_HELPER = -1373616754082319289L;
   public static final byte TRANSMISSION_STATUS_FAILED = 1;
   public static final byte TRANSMISSION_STATUS_SUCCESFUL = 2;
   public static final byte TRANSMISSION_STATUS_RETRY_QUEUED = 3;
   public static final byte TRANSMISSION_STATUS_SEND_QUEUED = 4;
   public static final byte TRANSMISSION_SERIALZATION_FAILED = 5;
   public static final byte TRANSMISSION_STATUS_SENT = 6;
   public static final byte NO_RETRY_LIMIT = -1;
   private static final int SUCCESS_TIMEOUT = 21600000;

   private TransmissionHelper() {
   }

   public static final TransmissionHelper getInstance() {
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      TransmissionHelper helper = (TransmissionHelper)appReg.get(MESSAGING_TRANSMISSION_HELPER);
      if (helper == null) {
         helper = new TransmissionHelper();
         appReg.put(MESSAGING_TRANSMISSION_HELPER, helper);
      }

      return helper;
   }

   public final void transmitObject(TransmissionWrapper wrapper) {
      wrapper.updateTransmissionStatus((byte)4, 0);
      this._sendRunnable.sendWrapper(wrapper);
   }
}
