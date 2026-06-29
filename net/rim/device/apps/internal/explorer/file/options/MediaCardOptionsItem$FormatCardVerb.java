package net.rim.device.apps.internal.explorer.file.options;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

final class MediaCardOptionsItem$FormatCardVerb extends Verb {
   public MediaCardOptionsItem$FormatCardVerb() {
      super(1115489);
   }

   @Override
   public final String toString() {
      return ExplorerResources.getString(57);
   }

   @Override
   public final Object invoke(Object parameter) {
      if (Dialog.ask(3, ExplorerResources.getString(80)) == 4) {
         new MediaCardOptionsItem$FormatThread().start();
      }

      return null;
   }
}
