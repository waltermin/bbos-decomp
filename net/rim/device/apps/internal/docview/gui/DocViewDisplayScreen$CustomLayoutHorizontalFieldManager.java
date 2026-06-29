package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.container.HorizontalFieldManager;

final class DocViewDisplayScreen$CustomLayoutHorizontalFieldManager extends HorizontalFieldManager {
   private final DocViewDisplayScreen this$0;

   public DocViewDisplayScreen$CustomLayoutHorizontalFieldManager(DocViewDisplayScreen _1) {
      this.this$0 = _1;
   }

   @Override
   protected final boolean incrementalLayout(int index, int added, int deleted) {
      return this.this$0._justChangingTitleName ? true : super.incrementalLayout(index, added, deleted);
   }
}
