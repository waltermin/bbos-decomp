package net.rim.device.internal.bluetooth;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class HeadsetGatewayEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      HeadsetGatewayListener hsListener = (HeadsetGatewayListener)listener;
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      byte[] object0 = (byte[])message.getObject0();
      String object1 = (String)message.getObject1();
      switch (message.getEvent()) {
         case 10497:
            hsListener.headsetIncomingConnection(object0);
            return;
         case 10498:
         default:
            hsListener.headsetConnected(data0);
            return;
         case 10499:
            hsListener.headsetDisconnected();
            return;
         case 10500:
            hsListener.headsetButtonPressed();
            return;
         case 10501:
            hsListener.headsetSpeakerVolumeChange(subMessage);
            return;
         case 10502:
            hsListener.headsetAudioConnected(data0);
            return;
         case 10503:
            hsListener.headsetAudioDisconnected();
            return;
         case 10504:
            hsListener.headsetUnknownATData(object1);
         case 10496:
      }
   }
}
