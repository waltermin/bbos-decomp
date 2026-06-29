package net.rim.device.apps.internal.camera;

import java.io.InputStream;
import net.rim.plazmic.internal.mediaengine.ResourceContext;
import net.rim.plazmic.mediaengine.MediaManager;

final class CameraPreviewScreen$MyMediaManager extends MediaManager {
   private final CameraPreviewScreen this$0;

   CameraPreviewScreen$MyMediaManager(CameraPreviewScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final Object createResource(String type, Object data, ResourceContext context, Object referer) {
      if (type != null && type.startsWith("x-object:")) {
         String name = type.substring(type.indexOf(47) + 1);
         if (name.equals("CustomFocusOrder")) {
            InputStream stream = (InputStream)(new Object(((String)data).getBytes()));
            this.this$0._focusManager.parseXml(stream);
            return null;
         }
      }

      return super.createResource(type, data, context, referer);
   }
}
