package net.rim.wica.runtime.ui;

import net.rim.wica.runtime.metadata.component.ui.ScreenModel;

public interface UiService {
   int DIALOG_OK = -1;
   int DIALOG_YES_NO = 2;
   int DIALOG_BUTTON_OK = 0;
   int DIALOG_BUTTON_YES = 2;
   int DIALOG_BUTTON_NO = 3;

   int displayModalDialog(int var1, String var2);

   void closeModalDialog();

   void displayScreen(ScreenModel var1, boolean var2);

   void performScreenBack();

   void showMenu(int var1);
}
