package net.rim.wica.runtime.lifecycle.internal;

import net.rim.wica.runtime.resources.RuntimeResources;

final class StartApplicationAction extends Action {
   StartApplicationAction() {
      super(RuntimeResources.getString(1));
   }

   StartApplicationAction(String description) {
      super(description);
   }

   @Override
   public final Object invoke(Object parameter) {
      try {
         ((WicletImpl)parameter).start();
         return null;
      } finally {
         ;
      }
   }
}
