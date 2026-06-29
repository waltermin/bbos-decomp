package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.ui.component.SimpleInputDialog;

class FindVerb extends Verb {
   private int MAX_CHARS = 256;
   FindVerbManager _findVerbManager;

   FindVerb(FindVerbManager findVerbManager) {
      super(196624);
      this._findVerbManager = findVerbManager;
   }

   @Override
   public Object invoke(Object context) {
      SimpleInputDialog dialog = (SimpleInputDialog)(new Object(0, CommonResources.getString(9025), 0, this.MAX_CHARS, 0));
      dialog.show();
      String searchString = dialog.getText();
      if (searchString != null && searchString.length() > 0) {
         Field startField = this._findVerbManager._baseManager.getLeafFieldWithFocus();
         int startOffset = 0;
         if (!(startField instanceof Object)) {
            if (startField instanceof Object) {
               startOffset = ((RichTextField)startField).getCursorPosition();
            }
         } else {
            startOffset = ((BasicEditField)startField).getCursorPosition();
         }

         this._findVerbManager._fieldsSearch = new FieldsSearch(this._findVerbManager._baseManager, searchString, startField, startOffset);
         FieldsSearchResult result = this._findVerbManager._fieldsSearchResult = this._findVerbManager._fieldsSearch.searchFields(null);
         if (result != null) {
            Field field = result.getField();
            field.setFocus();
            if (field instanceof Object) {
               ((BasicEditField)field).setCursorPosition(result.getOffset());
               return null;
            }

            if (field instanceof Object) {
               ((RichTextField)field).setCursorPosition(result.getOffset());
            }
         }
      }

      return null;
   }

   @Override
   public String toString() {
      return CommonResources.getString(9023);
   }
}
