package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.ui.Field;

public class FieldsSearchResult {
   Field _resultField;
   int _resultOffset;
   public static final int NOT_FOUND = -1;

   public FieldsSearchResult(Field resultField, int resultStartOffset) {
      this._resultField = resultField;
      this._resultOffset = resultStartOffset;
   }

   public Field getField() {
      return this._resultField;
   }

   public int getOffset() {
      return this._resultOffset;
   }
}
