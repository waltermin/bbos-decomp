package net.rim.device.apps.internal.camera;

import net.rim.device.apps.api.framework.verb.Verb;

final class CameraScreen$FullscreenVerb extends Verb {
   private final CameraScreen this$0;

   public CameraScreen$FullscreenVerb(CameraScreen _1) {
      super(590855, 7839140414916824787L, "net.rim.device.apps.internal.camera.Camera", 40);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0._options.setViewfinderMode(-1);
      return null;
   }

   @Override
   public final String toString() {
      String s = super.toString();
      return this.this$0._viewfinderMode == 1 ? '✓' + s : s;
   }
}
