package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Manager;

final class DocViewTextDisplayField$DocViewDeleteSkippedFieldRunnable extends DocViewTextDisplayField$DocViewDeleteFieldRunnable {
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$DocViewDeleteSkippedFieldRunnable(
      DocViewTextDisplayField _1, Manager manager, DocViewTextDisplayField$SkippedStatusField deleteField
   ) {
      super(manager, deleteField);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0._hasPageBreaks) {
         int index = super._field.getIndex();
         if (index >= 0 && index < this.this$0.getFieldCount() - 1) {
            DocViewTextDisplayField$PageBreakStatusField pgBrk = this.this$0.getNextPageStatusField(index);
            if (pgBrk != null && pgBrk._pageDisplay == this.this$0.getField(index + 1)) {
               DocViewTextDisplayField$PageBreakStatusField pgPrevBrk = this.this$0.getPrevPageStatusField(index);
               this.this$0.delete(pgBrk._pageDisplay);
               this.this$0.insert(pgBrk._pageDisplay, pgPrevBrk == null ? 0 : pgPrevBrk.getIndex() + 1);
            }
         }
      }

      super.run();
   }
}
