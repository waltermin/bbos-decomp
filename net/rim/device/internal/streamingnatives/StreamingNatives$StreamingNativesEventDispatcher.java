package net.rim.device.internal.streamingnatives;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

class StreamingNatives$StreamingNativesEventDispatcher extends EventDispatcher {
   @Override
   public void dispatch(Message message, Object oListener) {
      int event = message.getEvent();
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      StreamingNativesListener listener = (StreamingNativesListener)oListener;
      switch (event) {
         case 3329:
         default:
            listener.streamHitWatermark(subMessage, data0);
            return;
         case 3331:
            listener.streamNewData(subMessage, data0);
            return;
         case 3332:
            listener.streamSourceDone(subMessage);
            return;
         case 3333:
            listener.streamErrorFromSource(subMessage, data0);
            return;
         case 3334:
            listener.streamErrorFromSink(subMessage, data0);
            return;
         case 3335:
            listener.streamSessionClosed(subMessage);
            return;
         case 3336:
            listener.streamLostData(subMessage, data0);
            return;
         case 3337:
            listener.streamSinkDone(subMessage);
         case 3328:
         case 3330:
      }
   }
}
