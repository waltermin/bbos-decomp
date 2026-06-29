package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.util.Comparator;

class DocViewTextDisplayField$2 implements Comparator {
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$2(DocViewTextDisplayField _1) {
      this.this$0 = _1;
   }

   @Override
   public int compare(Object o1, Object o2) {
      return ((DocViewTextDisplayField$SkippedStatusField)o1).getIndex() - ((DocViewTextDisplayField$SkippedStatusField)o2).getIndex();
   }
}
