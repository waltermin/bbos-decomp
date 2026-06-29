package net.rim.wica.runtime.lifecycle.internal;

import net.rim.wica.runtime.resources.RuntimeResources;

final class LifecycleDialogManager$UpgradeAppAction extends Action {
   LifecycleDialogManager$UpgradeAppAction() {
      super(RuntimeResources.getString(4));
   }

   @Override
   public final Object invoke(Object parameter) {
      try {
         ((WicletImpl)parameter).upgrade();
         return null;
      } finally {
         ;
      }
   }
}
