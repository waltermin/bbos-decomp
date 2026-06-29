package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.StringMatch;
import net.rim.device.apps.api.ui.CommonResources;

public class FieldsSearch {
   FieldManagerEnumeration _fieldEnumeration;
   String _searchText;
   Field _startField;
   StringMatch _stringMatch;
   int _startOffset;

   public FieldsSearch(Manager baseManager, String searchText, Field startField, int startOffset) {
      this._searchText = searchText;
      this._startField = startField;
      this._startOffset = startOffset;
      this._fieldEnumeration = new FieldManagerEnumeration(baseManager);
      if (startField != null) {
         while (startField != this._fieldEnumeration.nextElement()) {
         }
      }

      this._stringMatch = (StringMatch)(new Object(this._searchText, false, false));
   }

   public FieldsSearchResult searchFields(FieldsSearchResult previous) {
      Field currentField = null;
      int currentOffset = 0;
      Object var12;
      if (null == previous) {
         previous = new FieldsSearchResult(null, this._startOffset);
         if (this._startField != null) {
            var12 = this._startField;
            currentOffset = this._startOffset;
         } else {
            try {
               var12 = this._fieldEnumeration.nextElement();
            } finally {
               ;
            }
         }
      } else {
         var12 = previous._resultField;
         currentOffset = previous.getOffset() + 1;
      }

      while (var12 != null) {
         String fieldText = null;
         if (!(var12 instanceof Object)) {
            if (!(var12 instanceof Object)) {
               if (!(var12 instanceof Object)) {
                  if (var12 instanceof Object) {
                     fieldText = ((CheckboxField)var12).getLabel();
                  }
               } else {
                  fieldText = ((RadioButtonField)var12).getLabel();
               }
            } else {
               fieldText = ((BasicEditField)var12).getText();
            }
         } else {
            fieldText = ((RichTextField)var12).getText();
         }

         if (fieldText != null && currentOffset < fieldText.length()) {
            int foundOffset = this._stringMatch.indexOf(fieldText, currentOffset);
            if (foundOffset >= 0) {
               previous._resultField = (Field)var12;
               previous._resultOffset = foundOffset;
               return previous;
            }
         }

         try {
            var12 = this._fieldEnumeration.nextElement();
            currentOffset = 0;
         } finally {
            ;
         }
      }

      Status.show(CommonResources.getString(9026), 750);
      return null;
   }
}
