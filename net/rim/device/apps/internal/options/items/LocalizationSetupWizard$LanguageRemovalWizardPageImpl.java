package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.internal.options.resources.OptionsResources;

final class LocalizationSetupWizard$LanguageRemovalWizardPageImpl extends LanguageRemovalWizardPage {
   private final LocalizationSetupWizard this$0;

   private LocalizationSetupWizard$LanguageRemovalWizardPageImpl(LocalizationSetupWizard _1) {
      super(OptionsResources.getResourceBundle());
      this.this$0 = _1;
   }

   @Override
   protected final DisplayableLocale[] getDisplayableLocales() {
      return this.this$0._displayableLocales;
   }

   @Override
   protected final int getLocaleSelectionIndex() {
      return this.this$0._localeSelectionIndex;
   }

   @Override
   protected final boolean canSkipLanguageRemoval() {
      return false;
   }

   @Override
   protected final void updateLocaleListing() {
      this.this$0.resetDisplayableLocales(true);
      this.this$0._localeSelectionIndex = -1;
   }

   LocalizationSetupWizard$LanguageRemovalWizardPageImpl(LocalizationSetupWizard x0, LocalizationSetupWizard$1 x1) {
      this(x0);
   }
}
