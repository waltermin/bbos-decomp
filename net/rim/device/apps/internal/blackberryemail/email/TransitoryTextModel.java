package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;

public class TransitoryTextModel implements RIMModel, FieldProvider {
   private EditField _editField;
   private int _fieldOrder;

   public void setParameters(String label, String textToShow, int fieldOrder, Tag tag) {
      this._fieldOrder = fieldOrder;
      this._editField = (EditField)(new Object(label, textToShow, 1000000, 9007199254740992L));
      this._editField.setAdjustAlignments(true);
      this._editField.setTag(tag);
   }

   public void setModel(RIMModel model) {
      if (model != null) {
         this._editField.setText(this.getString(model));
         this._editField.setCursorPosition(0);
      }
   }

   protected String getString(RIMModel model) {
      return "";
   }

   @Override
   public Field getField(Object context) {
      Font gFont = (Font)ContextObject.get(context, 77);
      if (gFont != null) {
         Font cFont = this._editField.getFont();
         int style = cFont.getStyle() & -3;
         style |= gFont.getStyle() & 7168;
         cFont = cFont.derive(style);
         this._editField.setFont(cFont);
      }

      return this._editField;
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public int getOrder(Object context) {
      return this._fieldOrder;
   }
}
