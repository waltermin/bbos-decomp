package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.setupwizard.ListWizardPage;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.tid.util.Utils;

final class LocalizationSetupWizard$InputMethodWizardPage extends ListWizardPage {
   private final LocalizationSetupWizard this$0;

   public LocalizationSetupWizard$InputMethodWizardPage(LocalizationSetupWizard _1) {
      super(OptionsResources.getResourceBundle(), 2101, 300, null, 131072);
      this.this$0 = _1;
   }

   @Override
   protected final boolean canSkipWizardInternal() {
      return this.this$0.canSkipInputMethod();
   }

   @Override
   protected final void populateFields() {
      LabelField headerLabel = new LabelField(OptionsResources.getString(2102));
      LabelField headerLabel2 = new LabelField(OptionsResources.getString(2103));
      Manager header = new VerticalFieldManager();
      header.setFont(this.getHeaderFont());
      headerLabel.setBorder(0, 0, 5, 0);
      header.add(headerLabel);
      header.add(headerLabel2);
      this.setHeaderField(header);
      int numLocales = this.this$0._inputLocales.length;
      String[] items = new String[numLocales];

      for (int i = 0; i < numLocales; i++) {
         items[i] = "\u200e" + this.this$0._inputLocales[i].getLocale().getDisplayName();
      }

      this.setListItems(items);
      Locale defaultInputLocale = Locale.getDefaultInputForSystem();
      int localeIndex = LocalizationSetupWizard$Util.findLocale(defaultInputLocale, this.this$0._inputLocales, false);
      if (localeIndex == -1) {
         int defaultReplCode = Utils.getDefaultCountryForLanguage(defaultInputLocale);
         if (defaultReplCode != 0) {
            localeIndex = LocalizationSetupWizard$Util.findLocale(Locale.get(defaultReplCode), this.this$0._inputLocales, true);
         }

         if (localeIndex == -1) {
            localeIndex = LocalizationSetupWizard$Util.findLocale(defaultInputLocale, this.this$0._inputLocales, true);
         }
      }

      if (localeIndex != -1) {
         this.setSelectedIndex(localeIndex);
      }
   }

   @Override
   protected final boolean saveWizard(Verb sender) {
      this.this$0.saveInputLocale(this.getSelectedIndex());
      return true;
   }
}
