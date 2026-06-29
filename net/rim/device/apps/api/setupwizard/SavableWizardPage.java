package net.rim.device.apps.api.setupwizard;

public interface SavableWizardPage {
   byte SAVABLE_WIZARD_PAGE_LANG_REMOVAL = 1;
   byte SAVABLE_WIZARD_PAGE_EMAIL_SETUP = 2;

   byte getPageKey();

   int[] getPageOptions();

   void setPageOptions(int[] var1);
}
