package net.rim.device.api.ui.accessibility;

public interface AccessibleText {
   int CHAR;
   int WORD;
   int LINE;

   String getBeforeIndex(int var1, int var2);

   String getAtIndex(int var1, int var2);

   String getAfterIndex(int var1, int var2);

   int getCaretPosition();

   int getSelectionStart();

   int getSelectionEnd();
}
