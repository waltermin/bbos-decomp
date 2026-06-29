package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.util.Comparator;

class DocViewTextDisplayField$3 implements Comparator {
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$3(DocViewTextDisplayField _1) {
      this.this$0 = _1;
   }

   @Override
   public int compare(Object o1, Object o2) {
      DocViewTextDisplayField$EmbeddedStatusField e1 = (DocViewTextDisplayField$EmbeddedStatusField)o1;
      DocViewTextDisplayField$EmbeddedStatusField e2 = (DocViewTextDisplayField$EmbeddedStatusField)o2;
      int idx1 = e1.getIndex();
      if (idx1 == -1 && e1._previewField != null) {
         idx1 = e1._previewField.getIndex();
      }

      int idx2 = e2.getIndex();
      if (idx2 == -1 && e2._previewField != null) {
         idx2 = e2._previewField.getIndex();
      }

      return idx1 - idx2;
   }
}
