package net.rim.device.apps.api.setupwizard;

public interface WizardPage {
   int CANCEL;
   int BACK;
   int NEXT;
   int FINISH;
   int ESCAPE;
   int EXPLICIT_COMMAND;
   int AUTORUN_FROM_DIALOG;
   int FIRST_RUN;

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
