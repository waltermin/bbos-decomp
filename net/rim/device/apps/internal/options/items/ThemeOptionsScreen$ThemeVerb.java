package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.i18n.CommonResource;

final class ThemeOptionsScreen$ThemeVerb extends Verb {
   private String _name;
   private String _localizedName;

   ThemeOptionsScreen$ThemeVerb(String localizedName, String name, int ordering, ResourceBundleFamily bundle, int rbKey) {
      super(ordering, bundle, rbKey);
      this._name = name;
      this._localizedName = localizedName;
   }

   @Override
   public final Object invoke(Object parameter) {
      Object result = null;
      switch (this.getOrdering()) {
         case 611472:
            String prompt = CommonResource.format(10025, this._localizedName);
            if (Dialog.ask(2, prompt, -1) == 3) {
               ThemeManager.remove(this._name);
               return this._name;
            }
            break;
         case 16865360:
            if (ThemeManager.isActivatable(this._name)) {
               ThemeManager.setActiveTheme(this._name);
               return new Object(39);
            }
            break;
         case 33554432:
            String text = OptionsResources.getResourceBundle().getString(2038);
            int r = Dialog.ask(3, text);
            if (r == 4) {
               RIMGlobalMessagePoster.postGlobalEvent(-4645495483836102462L);
            }
      }

      return result;
   }
}
