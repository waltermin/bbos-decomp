package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.internal.ui.component.PopupDialog;

public final class SearchEntryDialog extends PopupDialog {
   private BasicEditField _editField;
   private String _initialText = "";
   private RichTextField _promptField = (RichTextField)(new Object("", 36028797018963968L));
   private int _minLength = 1;
   private int _maxLength = 256;

   private SearchEntryDialog(String initialPattern) {
      super((Manager)(new Object(1153202979583557632L)), 0);
      this._promptField.setText(AddressBookResources.getString(1717));
      this.add(this._promptField);
      this._editField = (BasicEditField)(new Object(null, initialPattern, this._maxLength, 4503601774854144L));
      this.add(this._editField);
   }

   private final String getText() {
      return this._editField.getText();
   }

   @Override
   public final void show() {
      this._editField.setCursorPosition(this._initialText.length());
      super.show();
   }

   protected final boolean cancel() {
      this._editField.setText("");
      this.close(-1);
      return true;
   }

   protected final boolean accept() {
      if (this._editField.getText().length() < this._minLength) {
         return false;
      }

      this.close(0);
      return true;
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               return this.accept();
         }
      }

      return handled;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return super.trackwheelClick(status, time) ? true : this.accept();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      String currentText = this.getText();
      int currentLength = currentText.length();
      if (key != ' ' || currentLength != 0 && (currentLength <= 0 || currentText.charAt(currentLength - 1) != ' ')) {
         if (currentLength == 0 || currentText.charAt(currentLength - 1) == ' ') {
            key = Character.toUpperCase(key);
         }

         switch (key) {
            case '\n':
               return this.accept();
            case '\u001b':
               return this.cancel();
            default:
               return super.keyChar(key, status, time);
         }
      } else {
         return false;
      }
   }

   public static final String getSearchPattern() {
      return getSearchPattern(null);
   }

   static final String getSearchPattern(String default_pattern) {
      SearchEntryDialog searchDialog = new SearchEntryDialog(default_pattern);
      searchDialog.show();
      if (searchDialog.getCloseReason() != -1) {
         String dialogText = searchDialog.getText();
         if (dialogText != null && dialogText.length() > 0) {
            return dialogText;
         }
      }

      return null;
   }
}
