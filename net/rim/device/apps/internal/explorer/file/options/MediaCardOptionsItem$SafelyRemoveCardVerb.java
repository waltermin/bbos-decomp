package net.rim.device.apps.internal.explorer.file.options;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.io.file.RootRegister;

final class MediaCardOptionsItem$SafelyRemoveCardVerb extends Verb {
   public MediaCardOptionsItem$SafelyRemoveCardVerb() {
      super(1115488);
   }

   @Override
   public final String toString() {
      return ExplorerResources.getString(129);
   }

   @Override
   public final Object invoke(Object parameter) {
      RootRegister.getInstance().safelyRemoveCard();
      return null;
   }
}
