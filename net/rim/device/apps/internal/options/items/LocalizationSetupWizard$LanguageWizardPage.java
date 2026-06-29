package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.setupwizard.ListWizardPage;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class LocalizationSetupWizard$LanguageWizardPage extends ListWizardPage {
   private RichTextField _headerLabel;
   private final LocalizationSetupWizard this$0;

   public LocalizationSetupWizard$LanguageWizardPage(LocalizationSetupWizard _1) {
      super(OptionsResources.getResourceBundle(), 1803, 100, null, 131072);
      this.this$0 = _1;
   }

   @Override
   protected final void populateFields() {
      this._headerLabel = new RichTextField(36028797018963968L);
      this._headerLabel.setFont(this.getHeaderFont());
      this._headerLabel.setText(OptionsResources.getString(2100));
      this.setHeaderField(this._headerLabel);
      Locale currentLocale = Locale.getDefaultForSystem();
      int numLocales = this.this$0._displayableLocales.length;
      String[] items = new String[numLocales];
      int selectedIndex = 0;

      for (int i = 0; i < numLocales; i++) {
         items[i] = "\u200e" + this.this$0._displayableLocales[i].getLocale().getDisplayName();
         if (currentLocale.equals(this.this$0._displayableLocales[i].getLocale())) {
            selectedIndex = i;
         }
      }

      this.setListItems(items);
      this.setSelectedIndex(selectedIndex);
      this.reloadTitle();
   }

   @Override
   protected final boolean saveWizard(Verb sender) {
      if (this.getMainScreen().isDirty()) {
         int localeIndex = this.getSelectedIndex();
         this.this$0.saveLocale(localeIndex);
         int inputLocaleIndex = this.this$0.findMatchingInputLocale(localeIndex);
         if (inputLocaleIndex != -1) {
            this.this$0.saveInputLocale(inputLocaleIndex);
            return true;
         }
      } else {
         this.this$0._localeSelectionIndex = this.getSelectedIndex();
      }

      return true;
   }

   @Override
   protected final void discardWizard() {
      Locale.setDefault(Locale.getDefaultForSystem());
   }

   @Override
   protected final boolean canSkipWizardInternal() {
      return this.this$0.canSkipLanguage();
   }

   @Override
   protected final void selectedIndexChanged(int index) {
      Locale locale = this.this$0._displayableLocales[index].getLocale();
      String lang = locale.getDisplayName();
      this.log("Changing Language: " + lang);
      Locale.setDefaultForSystem(locale);
      this.getWizardTitleBar().setLabel(OptionsResources.getString(1803));
      this._headerLabel.setText(OptionsResources.getString(2100));
      this.reloadLabels();
      this.reloadTitle();
   }
}
