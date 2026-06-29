package net.rim.device.apps.internal.explorer.file.options;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.io.file.RootRegister;

final class MediaCardOptionsItem$MountCardVerb extends Verb {
   public MediaCardOptionsItem$MountCardVerb() {
      super(1115488);
   }

   @Override
   public final String toString() {
      return ExplorerResources.getString(128);
   }

   @Override
   public final Object invoke(Object parameter) {
      RootRegister.getInstance().mountSDCard();
      return null;
   }
}
