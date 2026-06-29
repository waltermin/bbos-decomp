package net.rim.device.api.gps;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class GPSEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object _listener) {
      int event = message.getEvent();
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      int data1 = message.getData1();
      if (!(_listener instanceof GPSListener)) {
         if (_listener instanceof LCSListener) {
            LCSListener lcsListener = (LCSListener)_listener;
            switch (event) {
               case 5903:
                  break;
               case 5904:
                  lcsListener.notificationRequest(subMessage, data0);
                  return;
               case 5905:
                  lcsListener.verificationTimerExpiry();
                  break;
               case 5906:
                  lcsListener.RRLPPayloadIndicationEvent(data0, data1);
                  return;
               case 5907:
               default:
                  lcsListener.reqAssistDataEvent();
                  return;
            }
         }
      } else {
         GPSListener gpsListener = (GPSListener)_listener;
         switch (event) {
            case 5889:
               gpsListener.gpsModeChangeComplete(subMessage != 0, data0);
               return;
            case 5890:
               gpsListener.gpsLocationUpdated(data0, subMessage, data1);
               return;
            case 5891:
               gpsListener.gpsResponseGetLPS(subMessage);
               return;
            case 5892:
               gpsListener.gpsResponseSetLPS(subMessage);
               return;
            case 5893:
               gpsListener.gpsResponseEnablePIN(subMessage);
               return;
            case 5894:
               gpsListener.gpsResponseChangePIN(subMessage);
               return;
            case 5895:
               gpsListener.gpsPDEChangeComplete(subMessage != 0, data0, data1);
               return;
            case 5896:
               gpsListener.gpsCredentialChangeComplete(subMessage != 0, data0);
               return;
            case 5908:
               gpsListener.gpsEphemerisDataRequired(data0);
               return;
            case 5922:
               gpsListener.gpsLocationAidingRequest();
               return;
         }
      }
   }
}
