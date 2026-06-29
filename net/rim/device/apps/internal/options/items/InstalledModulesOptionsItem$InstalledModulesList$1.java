package net.rim.device.apps.internal.options.items;

final class InstalledModulesOptionsItem$InstalledModulesList$1 extends AbstractItemList$SimpleObjectIndexerHelper {
   private final InstalledModulesOptionsItem$InstalledModulesList this$0;

   InstalledModulesOptionsItem$InstalledModulesList$1(InstalledModulesOptionsItem$InstalledModulesList _1) {
      super(_1);
      this.this$0 = _1;
   }

   @Override
   protected final String getString(Object element) {
      InstalledModulesOptionsItem$InstalledModuleListItem module = (InstalledModulesOptionsItem$InstalledModuleListItem)element;
      return module.getModuleName();
   }
}
