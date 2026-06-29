package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.verbs.SavePropertyVerb;
import net.rim.vm.Array;
import net.rim.vm.Memory;

public final class AdvancedGeneralProperty extends BrowserProperty {
   private CheckboxField _acceptCookiesField;
   private NumericChoiceField _rawDataCacheSizeField;
   private NumericChoiceField _pageCacheSizeField;
   private NumericChoiceField _menuDelayTimeField;

   @Override
   public final String getLabel() {
      return BrowserResources.getString(715);
   }

   @Override
   public final Screen getScreen(boolean restrictedAccess) {
      super._restrictedAccess = restrictedAccess;
      MainScreen screen = this.generateScreen(BrowserResources.getString(715));
      this._acceptCookiesField = (CheckboxField)(new Object(BrowserResources.getString(177), GeneralProperty.getCurrentPropertyAsBoolean(30)));
      screen.add(this._acceptCookiesField);
      this._rawDataCacheSizeField = (NumericChoiceField)(new Object(BrowserResources.getString(193), 0, 4096, 1, GeneralProperty.getCurrentPropertyAsInt(31)));
      screen.add(this._rawDataCacheSizeField);
      this._pageCacheSizeField = (NumericChoiceField)(new Object(BrowserResources.getString(723), 0, 5, 1, GeneralProperty.getCurrentPropertyAsInt(34)));
      if (Memory.getFlashTotal() > 16777216) {
         screen.add(this._pageCacheSizeField);
      }

      this._menuDelayTimeField = (NumericChoiceField)(new Object("Menu Delay Time:", 100, 1000, 50, (GeneralProperty.getCurrentPropertyAsInt(35) - 100) / 50));
      screen.add(this._menuDelayTimeField);
      return screen;
   }

   @Override
   public final Verb getVerbs(Verb[] verbs) {
      Array.resize(verbs, 1);
      verbs[0] = new SavePropertyVerb(this);
      return verbs[0];
   }

   @Override
   public final void saveProperty() {
      GeneralProperty.setCurrentProperty(30, this._acceptCookiesField.getChecked());
      GeneralProperty.setCurrentProperty(31, this._rawDataCacheSizeField.getSelectedIndex());
      GeneralProperty.setCurrentProperty(34, this._pageCacheSizeField.getSelectedIndex());
      GeneralProperty.setCurrentProperty(35, this._menuDelayTimeField.getSelectedValue());
      GeneralProperty.notifyListeners();
   }
}
