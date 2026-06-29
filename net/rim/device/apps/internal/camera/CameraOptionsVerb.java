package net.rim.device.apps.internal.camera;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

public final class CameraOptionsVerb extends Verb {
   public CameraOptionsVerb() {
      super(16986368, CommonResource.getBundle(), 20);
   }

   @Override
   public final Object invoke(Object parameter) {
      CameraOptionsScreen.showEditOptionsScreen();
      return null;
   }
}
