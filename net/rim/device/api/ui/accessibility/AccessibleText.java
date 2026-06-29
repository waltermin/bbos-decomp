package net.rim.device.api.ui.accessibility;

public interface AccessibleText {
   int CHAR = 0;
   int WORD = 1;
   int LINE = 2;

   String getBeforeIndex(int var1, int var2);

   String getAtIndex(int var1, int var2);

   String getAfterIndex(int var1, int var2);

   int getCaretPosition();

   int getSelectionStart();

   int getSelectionEnd();
}
