package net.rim.device.internal.camera;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class CameraEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      CameraListener cameraListener = (CameraListener)listener;
      boolean video = cameraListener instanceof CameraVideoListener;
      int event = message.getEvent();
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      switch (event) {
         case 9216:
         case 9219:
            System.out.println("Unhandled Camera Event:" + event);
            break;
         case 9217:
         default:
            cameraListener.setNightMode(subMessage);
            return;
         case 9218:
            if (video) {
               ((CameraVideoListener)cameraListener).recordError(subMessage);
               return;
            }
            break;
         case 9220:
            if (video) {
               ((CameraVideoListener)cameraListener).recordComplete(data0);
               return;
            }
            break;
         case 9221:
            if (video) {
               ((CameraVideoListener)cameraListener).recordStatusUpdate(data0);
               return;
            }
      }
   }
}
