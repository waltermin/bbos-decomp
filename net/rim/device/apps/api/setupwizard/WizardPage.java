package net.rim.device.apps.api.setupwizard;

public interface WizardPage {
   int CANCEL = 0;
   int BACK = 1;
   int NEXT = 2;
   int FINISH = 3;
   int ESCAPE = 4;
   int EXPLICIT_COMMAND = 1;
   int AUTORUN_FROM_DIALOG = 2;
   int FIRST_RUN = 4;

   String getTitle();

   int getPriority();

   int showPage(int var1, int var2);

   boolean canSkipWizard();

   boolean isHidden();

   WizardCategory getCategory();

   void reloadTitle();

   void setProgress(int var1, int var2, int var3);

   int getPageCount();

   void setLogManager(LogManager var1);
}
