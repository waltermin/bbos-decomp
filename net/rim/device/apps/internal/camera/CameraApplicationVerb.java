package net.rim.device.apps.internal.camera;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;

public final class CameraApplicationVerb extends Verb {
   public CameraApplicationVerb() {
      super(16986368, ResourceBundle.getBundle(7839140414916824787L, "net.rim.device.apps.internal.camera.Camera"), 0);
   }

   @Override
   public final Object invoke(Object parameter) {
      if (parameter instanceof Object) {
         ShowCameraApp.setCameraContext((ContextObject)parameter);
      }

      ShowCameraApp.showCameraApp();
      return null;
   }
}
