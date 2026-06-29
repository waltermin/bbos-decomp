package net.rim.device.apps.internal.explorer.file.options;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

final class MediaCardOptionsItem$FormatThread$1 implements Runnable {
   private final MediaCardOptionsItem$FormatThread this$0;

   MediaCardOptionsItem$FormatThread$1(MediaCardOptionsItem$FormatThread _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      Status.show(ExplorerResources.getString(122), Bitmap.getPredefinedBitmap(0), 2000, 0, false, false, 50);
   }
}
