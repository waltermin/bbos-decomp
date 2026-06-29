package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.util.SimpleSortingVector;

class DocViewTextDisplayField$DocViewAddSkippedFieldToVector implements Runnable {
   private final SimpleSortingVector _vector;
   private final DocViewTextDisplayField$SkippedStatusField _field;

   DocViewTextDisplayField$DocViewAddSkippedFieldToVector(SimpleSortingVector vector, DocViewTextDisplayField$SkippedStatusField skippedField) {
      if (vector != null && skippedField != null) {
         this._vector = vector;
         this._field = skippedField;
      } else {
         throw new IllegalArgumentException("Invalid vector insert parameters");
      }
   }

   @Override
   public void run() {
      if (this._field.getIndex() == -1) {
         throw new IllegalArgumentException("The field should have been added to manager prior to this action");
      }

      this._vector.addElement(this._field);
   }
}
