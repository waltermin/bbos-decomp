package net.rim.device.apps.internal.explorer.file;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

final class ExploreManager$InvalidFileAlertVerb extends Verb {
   String _fileError;

   ExploreManager$InvalidFileAlertVerb() {
      super(0);
   }

   @Override
   public final Object invoke(Object parameter) {
      Dialog.alert(ExplorerResources.getString(113) + this._fileError);
      return null;
   }
}
