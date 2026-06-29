package net.rim.device.internal.media;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

class MediaNatives$MediaNativesEventDispatcher extends EventDispatcher {
   @Override
   public void dispatch(Message message, Object oListener) {
      int event = message.getEvent();
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      int data1 = message.getData1();
      MediaEventListener listener = (MediaEventListener)oListener;
      switch (event) {
         case 8192:
         default:
            listener.mediaStopped(data0);
            return;
         case 8193:
            listener.mediaPauseComplete(data0);
            return;
         case 8194:
            listener.mediaError(data0, subMessage);
            return;
         case 8195:
            listener.mediaSeek(data0, data1);
            return;
         case 8196:
            listener.mediaLoaded(data0);
            return;
         case 8197:
            listener.mediaStatusUpdate(data0, data1);
            return;
         case 8198:
            Object object0 = message.getObject0();
            Object object1 = message.getObject1();
            listener.mediaAuthenticationRequired(data0, object0, object1);
            return;
         case 8199:
            listener.mediaParametersChangedComplete(data0);
         case 8191:
      }
   }
}
