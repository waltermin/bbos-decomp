package net.rim.device.apps.internal.options.items;

import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SMSParameters;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.apps.api.localeremoval.LocaleRemovalUtility;
import net.rim.device.apps.api.setupwizard.SetupWizardOrdering;
import net.rim.device.apps.api.setupwizard.WizardController;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.deviceoptions.SMSOptions;
import net.rim.device.internal.system.RadioInternal;

public final class LocalizationSetupWizard extends WizardController {
   private DisplayableLocale[] _displayableLocales;
   private DisplayableLocale[] _inputLocales;
   private int _localeSelectionIndex = -1;
   private int buildType = 0;

   public LocalizationSetupWizard() {
      super(OptionsResources.getResourceBundle(), 1803, 100, SetupWizardOrdering.LANGUAGE_CATEGORY);
      this.buildType = LocaleRemovalUtility.getMultiLanguageBuildType(true);
      Vector pages = (Vector)(new Object(3));
      if ((this.buildType & 1) != 0) {
         pages.addElement(new LocalizationSetupWizard$LanguageWizardPage(this));
      }

      if ((LocaleRemovalUtility.getMultiLanguageBuildType(false) & 1) != 0) {
         pages.addElement(new LocalizationSetupWizard$LanguageRemovalWizardPageImpl(this, null));
      }

      if ((this.buildType & 2) != 0) {
         pages.addElement(new LocalizationSetupWizard$InputMethodWizardPage(this));
      }

      this.setPages(pages);
   }

   @Override
   protected final void initialize() {
      this.resetDisplayableLocales(false);
   }

   protected final void resetDisplayableLocales(boolean force) {
      if (this._displayableLocales == null || force) {
         this._displayableLocales = LocalizationSetupWizard$Util.getDisplayableLocales();
      }

      if (this._inputLocales == null || force) {
         this._inputLocales = LocalizationSetupWizard$Util.getInputLocales();
      }
   }

   @Override
   public final boolean isValid(Object context) {
      return (!this.canSkipLanguage() || !this.canSkipInputMethod()) && super.isValid(context);
   }

   private final void saveLocale(int index) {
      Locale selectedLocale = this._displayableLocales[index].getLocale();
      Locale systemLocale = Locale.getDefaultForSystem();
      this._localeSelectionIndex = index;
      if (systemLocale != selectedLocale) {
         Locale.setDefaultForSystem(selectedLocale);
      }

      if (systemLocale != null && (systemLocale.getCode() & -65536) == 1784741888) {
         this.setJapaneseDefaultFont();
      }
   }

   final void saveInputLocale(int index) {
      Locale selectedLocale = this._inputLocales[index].getLocale();
      Locale systemLocale = Locale.getDefaultInputForSystem();
      if (systemLocale != selectedLocale) {
         if (selectedLocale.getCode() == 0) {
            selectedLocale = null;
         }

         Locale.setDefaultInputForSystem(selectedLocale);
      }

      systemLocale = Locale.getDefaultInputForSystem();
      if (systemLocale != null && (systemLocale.getCode() & -65536) == 1784741888) {
         this.setJapaneseDefaultFont();
      }

      int networkType = RadioInfo.getNetworkType();
      if (networkType == 3 || networkType == 7) {
         int originalSMSCoding = SMSOptions.getFallbackCoding();
         int messageCoding = Locale.isLatinOneCharacterSetLocale(selectedLocale) ? originalSMSCoding : 2;

         try {
            if (messageCoding != -1) {
               SMSParameters smsp = (SMSParameters)(new Object());
               RadioInternal.getDefaultSMSParameters(smsp);
               if (originalSMSCoding == -1) {
                  originalSMSCoding = smsp.getMessageCoding();
               } else if (originalSMSCoding == messageCoding) {
                  originalSMSCoding = -1;
               }

               SMSOptions.setFallbackCoding(originalSMSCoding);
               smsp.setMessageCoding(messageCoding);
               RadioInternal.setDefaultSMSParameters(smsp);
               return;
            }
         } finally {
            return;
         }
      }
   }

   private final int findMatchingInputLocale(int localeIndex) {
      int index = -1;
      int dispLocaleCode = this._displayableLocales[localeIndex].getLocale().getCode();
      if ((dispLocaleCode & -65536) == 2053636096) {
         return LocalizationSetupWizard$Util.getAppropriateChineseInputLocale(dispLocaleCode, this._inputLocales);
      }

      int candIndex = -1;

      for (int i = 0; i < this._inputLocales.length; i++) {
         int inputLocaleCode = this._inputLocales[i].getLocale().getCode();
         if (dispLocaleCode == inputLocaleCode) {
            index = i;
            break;
         }

         if (candIndex == -1 && (dispLocaleCode & -65536) == (inputLocaleCode & -65536)) {
            candIndex = i;
         }
      }

      if (index == -1 && candIndex != -1) {
         index = candIndex;
      }

      return index;
   }

   private final void setJapaneseDefaultFont() {
      Font f = null;

      label26:
      try {
         f = FontFamily.forName("BBJapanese").getFont(0, 8, 2, 1, 0);
      } finally {
         break label26;
      }

      if (f != null) {
         Font.setDefaultFontForSystem(f);
      }
   }

   private final boolean canSkipLanguage() {
      return (this.buildType & 1) == 0;
   }

   private final boolean canSkipInputMethod() {
      return (this.buildType & 2) == 0;
   }
}
