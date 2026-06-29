package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

public final class EditUserDefinedFieldLabelScreen extends PopupScreen {
   private String _labelText;
   private EditField _editField;
   private static final int MAX_FIELD_LENGTH = 30;

   private EditUserDefinedFieldLabelScreen(String value) {
      super(new VerticalFieldManager(1153202979583557632L));
      if (value == null) {
         value = "";
      }

      this._labelText = value;
      this._editField = new AutoTextEditField(AddressBookResources.getString(1202), value, 30, 1157425106381701120L);
      Field originalField = new RichTextField(value, 1197957500880551936L);
      this.add(originalField);
      this.add(this._editField);
   }

   public static final String editUserDefinedFieldLabel(String value) {
      EditUserDefinedFieldLabelScreen screen = new EditUserDefinedFieldLabelScreen(value);
      UiApplication.getUiApplication().pushModalScreen(screen);
      String newValue = screen._labelText;
      if (newValue != null && newValue.length() == 0) {
         newValue = null;
      }

      return newValue;
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      this._labelText = this._editField.getText();
      UiApplication.getUiApplication().popScreen(this);
      return true;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         UiApplication.getUiApplication().popScreen(this);
         return true;
      } else if (key == '\n') {
         this._labelText = this._editField.getText();
         UiApplication.getUiApplication().popScreen(this);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }
}
