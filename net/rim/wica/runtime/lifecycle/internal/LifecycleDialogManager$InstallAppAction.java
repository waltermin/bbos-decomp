package net.rim.wica.runtime.lifecycle.internal;

import net.rim.wica.runtime.lifecycle.UpgradeTaskInfo;
import net.rim.wica.runtime.resources.RuntimeResources;

final class LifecycleDialogManager$InstallAppAction extends Action {
   private final LifecycleDialogManager this$0;

   LifecycleDialogManager$InstallAppAction(LifecycleDialogManager this$0) {
      super(RuntimeResources.getString(65));
      this.this$0 = this$0;
   }

   @Override
   public final Object invoke(Object parameter) {
      try {
         this.this$0._lifecycleService.installRecommended((UpgradeTaskInfo)parameter);
         return null;
      } finally {
         ;
      }
   }
}
