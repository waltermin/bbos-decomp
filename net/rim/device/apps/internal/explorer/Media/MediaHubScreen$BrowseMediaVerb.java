package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.explorer.file.ExploreScreen;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

final class MediaHubScreen$BrowseMediaVerb extends Verb {
   int _entryIndex = 0;

   MediaHubScreen$BrowseMediaVerb() {
      super(569352, ExplorerResources.getResourceBundleFamily(), 116);
   }

   @Override
   public final Object invoke(Object parameter) {
      ContextObject context = (ContextObject)(new Object());
      if (this._entryIndex >= 0) {
         ResourceBundle rb = ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer");
         String[] localizedNames = rb.getFamily().getBundle(Locale.get(0)).getStringArray(12);
         if (this._entryIndex < localizedNames.length) {
            label25:
            try {
               int mediaType = Integer.parseInt(localizedNames[this._entryIndex]);
               context.putIntegerData(mediaType);
            } finally {
               break label25;
            }
         }

         ContextObject.put(context, 3941043584844673548L, new Object(this._entryIndex));
      } else {
         ContextObject.put(context, 3941043584844673548L, new Object(-1));
      }

      UiApplication.getUiApplication().pushScreen(new ExploreScreen(context));
      return null;
   }
}
