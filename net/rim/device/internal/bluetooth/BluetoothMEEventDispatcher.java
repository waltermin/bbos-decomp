package net.rim.device.internal.bluetooth;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class BluetoothMEEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      BluetoothMEListener meListener = (BluetoothMEListener)listener;
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      int data1 = message.getData1();
      byte[] object0 = (byte[])message.getObject0();
      byte[] object1 = (byte[])message.getObject1();
      switch (message.getEvent()) {
         case 9992:
            if (meListener instanceof BluetoothMEListener2) {
               BluetoothMEListener2 meListener2 = (BluetoothMEListener2)listener;
               switch (message.getEvent()) {
                  case 9984:
                  case 9993:
                  case 9994:
                     break;
                  case 9985:
                  default:
                     meListener2.deviceNameRetrieved(object0, object1);
                     return;
                  case 9986:
                     meListener2.pairingComplete(object0, data0);
                     return;
                  case 9987:
                     meListener2.pinCodeRequired(object0, data0);
                     return;
                  case 9988:
                     meListener2.inquiryResult(object0, subMessage, data0, data1);
                     return;
                  case 9989:
                     meListener2.inquiryComplete();
                     return;
                  case 9990:
                     meListener2.deviceConnected(object0, data0, data1);
                     return;
                  case 9991:
                     meListener2.deviceDisconnected(object0, data0);
                     return;
                  case 9992:
                     meListener2.authorizationRequired(object0, data0);
                     return;
                  case 9995:
                     meListener2.serviceDiscoveryComplete(object0, data0, object1);
                     return;
                  case 9996:
                     meListener2.linkModeChanged(object0, data0, data1);
                     return;
                  case 9997:
                     meListener2.inquiryCancelled();
                     return;
                  case 9998:
                     meListener2.connectionAccepted(object0);
                     return;
                  case 9999:
                     meListener2.linkKeyChangeComplete(object0, data0);
                     return;
                  case 10000:
                     meListener2.authenticationComplete(object0, data0);
                     return;
                  case 10001:
                     meListener2.encryptionComplete(object0, data0);
                     return;
                  case 10002:
                     meListener2.hciFatalError(object0, data0);
               }
            }

            return;
         case 9993:
         default:
            meListener.powerOnComplete(subMessage != 0);
            return;
         case 9994:
            meListener.powerOffComplete();
      }
   }
}
