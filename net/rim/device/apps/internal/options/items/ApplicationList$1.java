package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.CodeModuleGroup;

final class ApplicationList$1 extends AbstractItemList$SimpleObjectIndexerHelper {
   private final ApplicationList this$0;

   ApplicationList$1(ApplicationList _1) {
      super(_1);
      this.this$0 = _1;
   }

   @Override
   protected final String getString(Object element) {
      CodeModuleGroup group = ((ApplicationList$ApplicationListItem)element)._group;
      return group.getFriendlyName();
   }
}
