package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.bis.ApplicationResources;

final class FilterList$EmptyFilterField extends LabelField {
   private final FilterList this$0;

   public FilterList$EmptyFilterField(FilterList _1) {
      super(ApplicationResources.getString(277), 1152921504606846976L);
      this.this$0 = _1;
   }

   @Override
   public final boolean isFocusable() {
      return false;
   }

   @Override
   public final boolean isSelectionCopyable() {
      return false;
   }
}
