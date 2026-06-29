package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.ui.component.PopupDialog;

public final class FindStringDlg extends PopupDialog {
   private BasicEditField _editField = new EditField(4503601774854144L);
   private CheckboxField _checkBoxField = new CheckboxField(
      ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView").getString(19), false
   );
   private RichTextField _promptField = new RichTextField(CommonResources.getString(9025), 36028797018963968L);

   public FindStringDlg() {
      super(new VerticalFieldManager(1153202979583557632L));
      this.add(this._promptField);
      this.add(this._editField);
      this.add(this._checkBoxField);
   }

   public final void setText(String text) {
      if (text == null) {
         text = "";
      }

      this._editField.setText(text);
   }

   public final void setCaseSensitiveSearch(boolean caseSensitiveSearch) {
      this._checkBoxField.setChecked(caseSensitiveSearch);
   }

   @Override
   protected final void close(int closeReason) {
      if (closeReason == -1) {
         this._editField.setText("");
         this._checkBoxField.setChecked(false);
      }

      super.close(closeReason);
   }

   @Override
   public final boolean onMenu(int instance) {
      this.close(0);
      return true;
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      boolean retValue = super.keyChar(c, status, time);
      if (!retValue) {
         switch (c) {
            case '\n':
               this.close(0);
               return true;
            case '\u001b':
               this.close(-1);
               return true;
         }
      }

      return retValue;
   }

   public final String getText() {
      return this._editField.getText();
   }

   public final boolean isCaseSensitiveSearch() {
      return this._checkBoxField.getChecked();
   }
}
