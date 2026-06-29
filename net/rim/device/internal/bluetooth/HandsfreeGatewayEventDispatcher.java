package net.rim.device.internal.bluetooth;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class HandsfreeGatewayEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      HandsfreeGatewayListener hfListener = (HandsfreeGatewayListener)listener;
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      byte[] object0 = (byte[])message.getObject0();
      String object1 = (String)message.getObject1();
      switch (message.getEvent()) {
         case 10241:
            hfListener.handsfreeIncomingConnection(object0);
            return;
         case 10242:
         default:
            hfListener.handsfreeConnected(data0);
            return;
         case 10243:
            hfListener.handsfreeDisconnected();
            return;
         case 10244:
            hfListener.handsfreeAnswerCall();
            return;
         case 10245:
            hfListener.handsfreeHangupCall();
            return;
         case 10246:
            hfListener.handsfreeHoldCall(subMessage, 0);
            return;
         case 10247:
            hfListener.handsfreeSpeakerVolumeChange(subMessage);
            return;
         case 10248:
            hfListener.handsfreeAudioConnected(data0);
            return;
         case 10249:
            hfListener.handsfreeAudioDisconnected();
            return;
         case 10250:
            hfListener.handsfreeDialNumber(object1);
            return;
         case 10251:
            hfListener.handsfreeRedial();
            return;
         case 10252:
            hfListener.handsfreeEnableEventReporting(subMessage != 0);
            return;
         case 10253:
            hfListener.handsfreeEnableCallWaitingReporting(subMessage != 0);
            return;
         case 10254:
            hfListener.handsfreeEnableCallerIDReporting(subMessage != 0);
            return;
         case 10255:
            hfListener.handsfreeSendDTMF(subMessage);
            return;
         case 10256:
            hfListener.handsfreeFeatures(subMessage);
            return;
         case 10257:
            hfListener.handsfreeDialMemory(object1);
            return;
         case 10258:
            hfListener.handsfreeSendIndicators();
            return;
         case 10259:
            hfListener.handsfreeUnknownATData(object1);
            return;
         case 10260:
            hfListener.handsfreeDisableNREC();
            return;
         case 10261:
            hfListener.handsfreeSendHoldInfo();
            return;
         case 10262:
            hfListener.handsfreeVoiceRecog(subMessage != 0);
         case 10240:
      }
   }
}
