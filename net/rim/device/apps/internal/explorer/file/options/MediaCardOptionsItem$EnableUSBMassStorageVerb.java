package net.rim.device.apps.internal.explorer.file.options;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.io.file.RootRegister;

final class MediaCardOptionsItem$EnableUSBMassStorageVerb extends Verb {
   public MediaCardOptionsItem$EnableUSBMassStorageVerb() {
      super(1115489);
   }

   @Override
   public final String toString() {
      return ExplorerResources.getString(63);
   }

   @Override
   public final Object invoke(Object parameter) {
      RootRegister.getInstance().enableUSBMassStoragePromptIfNecessary();
      return null;
   }
}
