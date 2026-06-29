package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CommandEvent;
import net.rim.device.apps.internal.bis.session.ClientSessionState;

public final class ChangeLanguageScreen extends UserSettingsScreen {
   private Locale[] _availableLocales;
   private RadioButtonGroup _languageGroup;
   private static final String PARAM_LOCALE = "locale";

   public ChangeLanguageScreen() {
      super(27);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(160));
      this.addContentField(new LabelField(ApplicationResources.getString(161)));
      this._languageGroup = new RadioButtonGroup();
      String brandLocales = ClientSessionState.getInstance().getBrandingInfo().getAvailableLanguages();
      this._availableLocales = ApplicationResources.getSupportedLocales(brandLocales);
      int numLocales = this._availableLocales.length;

      for (int i = 0; i < numLocales; i++) {
         boolean selected = this._availableLocales[i].equals(ClientSessionState.getInstance().getUserInfo().getLocale());
         this.addContentField(new RadioButtonField(this._availableLocales[i].getDisplayName(), this._languageGroup, selected));
      }

      Button cancel = new Button(ApplicationResources.getString(28));
      Button save = new Button(ApplicationResources.getString(29));
      this.addButtonBarButtons(new Button[]{cancel, save}, false, 1);
      this.attachEventToField(cancel, new BackEvent(28));
      CommandEvent saveEvent = new CommandEvent(29, 6, new String[]{"locale"});
      this.attachEventToField(save, saveEvent);
      this.setDefaultEvent(saveEvent);
      this.setHelp("124051.wml");
   }

   @Override
   public final boolean importFormDataFromUI(Hashtable inputMap) {
      int selectedLocaleIndex = this._languageGroup.getSelectedIndex();
      String selectedLocaleName = this._availableLocales[selectedLocaleIndex].toString();
      inputMap.put("locale", selectedLocaleName);
      return true;
   }
}
