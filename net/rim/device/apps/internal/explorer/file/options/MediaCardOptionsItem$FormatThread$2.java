package net.rim.device.apps.internal.explorer.file.options;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

final class MediaCardOptionsItem$FormatThread$2 implements Runnable {
   private final boolean val$success;
   private final MediaCardOptionsItem$FormatThread this$0;

   MediaCardOptionsItem$FormatThread$2(MediaCardOptionsItem$FormatThread _1, boolean _2) {
      this.this$0 = _1;
      this.val$success = _2;
   }

   @Override
   public final void run() {
      Dialog.alert(ExplorerResources.getString(this.val$success ? 81 : 82));
   }
}
