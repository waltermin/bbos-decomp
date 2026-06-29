package net.rim.wica.runtime.metadata.internal.component.ui.control;

import java.util.Date;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.control.EditControl;
import net.rim.wica.runtime.metadata.internal.component.ui.UIControlImpl;
import net.rim.wica.runtime.util.Util;

public class EditControlImpl extends UIControlImpl implements EditControl {
   private int _editType;
   private String _format;
   private SimpleDateFormat _dateFormatter;

   @Override
   public boolean getBorder() {
      return true;
   }

   @Override
   public boolean validateText(String text) {
      switch (this._editType) {
         case 0:
         case 3:
         case 4:
            if (text.trim().length() > 0) {
               return true;
            }

            return false;
         case 2:
         case 5:
         default:
            text = Util.filterNumber(text);
         case 1:
            return Util.isValidNumber(text);
         case 6:
            return Util.isValidURL(text);
      }
   }

   EditControlImpl(
      int id,
      int type,
      UIContainer parent,
      int style,
      int bits,
      int x,
      int y,
      int initId,
      Object inValue,
      int editType,
      int focusOutId,
      int[] mapping,
      String format
   ) {
      super(id, type, parent, style, bits, x, y, initId, inValue);
      this._editType = editType;
      this._format = format;
      this.setEvent(0, focusOutId);
      this.setMapping(mapping);
   }

   public EditControlImpl(
      int id, UIContainer parent, int style, int bits, int x, int y, int initId, Object inValue, int editType, int focusOutId, int[] mapping, String format
   ) {
      super(id, 129, parent, style, bits, x, y, initId, inValue);
      this._editType = editType;
      this._format = format;
      if (this._editType != 3 && this._editType != 4) {
         if (this._format != null) {
            this._dateFormatter = new SimpleDateFormat(this._format);
         } else {
            this._dateFormatter = Util.DEFAULT_DATE_FORMATTER;
         }
      } else {
         if (inValue == null) {
            super._valueType = 4;
         }

         if (inValue instanceof int[]) {
            int type = ((int[])inValue)[0];
            if (type == 4) {
               super._valueType = type;
               if (this.requireInValueArray()) {
                  super._valueType |= 32768;
               }
            }
         }
      }

      if (this._editType == 10 && inValue instanceof int[]) {
         int type = ((int[])inValue)[0];
         if (type == 8 || type == 1) {
            super._valueType = 8;
            if (this.requireInValueArray()) {
               super._valueType |= 32768;
            }
         }
      }

      this.setEvent(0, focusOutId);
      this.setMapping(mapping);
   }

   @Override
   public int getEditType() {
      return this._editType;
   }

   @Override
   public String getFormat() {
      return this._format;
   }

   @Override
   public String getFormattedDate(long longValue) {
      return this._dateFormatter != null ? this._dateFormatter.format(new Date(longValue)) : null;
   }

   @Override
   public boolean isMandatorySatisfied() {
      if (this.isMandatory()) {
         return super._value instanceof String ? this.validateText((String)super._value) : super._value != null;
      } else {
         return true;
      }
   }

   @Override
   protected boolean isEmptyKey(Object value) {
      return (int)(super._mappingType & 4294967295L) == 6 && value.equals("");
   }
}
