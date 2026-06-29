package net.rim.device.apps.internal.camera;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

final class CameraPreviewScreen$RenameMenuItem extends MenuItem {
   private final CameraPreviewScreen this$0;

   CameraPreviewScreen$RenameMenuItem(CameraPreviewScreen _1) {
      super(ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer"), 45, 591109, Integer.MAX_VALUE);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.renameImage();
   }
}
