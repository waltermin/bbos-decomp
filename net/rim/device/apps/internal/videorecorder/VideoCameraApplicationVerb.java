package net.rim.device.apps.internal.videorecorder;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;

public final class VideoCameraApplicationVerb extends Verb {
   public VideoCameraApplicationVerb() {
      super(16986368, ResourceBundle.getBundle(5325037524743121547L, "net.rim.device.apps.internal.resource.VideoRecorder"), 2);
   }

   @Override
   public final Object invoke(Object parameter) {
      if (parameter instanceof ContextObject) {
         ShowVideoCameraApp.setVideoCameraContext((ContextObject)parameter);
      }

      ShowVideoCameraApp.showVideoCameraApp();
      return null;
   }
}
