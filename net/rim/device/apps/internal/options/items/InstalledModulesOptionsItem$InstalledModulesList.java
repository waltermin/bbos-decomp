package net.rim.device.apps.internal.options.items;

import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.system.InternalServices;

final class InstalledModulesOptionsItem$InstalledModulesList extends AbstractItemList {
   public InstalledModulesOptionsItem$InstalledModulesList(boolean showAll, KeywordFilteredScreen screen) {
      super(showAll, screen);
   }

   @Override
   protected final void refresh() {
      int[] moduleHandles = CodeModuleManager.getModuleHandles();
      InstalledModulesOptionsItem$InstalledModuleListItem[] modules = new InstalledModulesOptionsItem$InstalledModuleListItem[0];

      for (int i = 0; i < moduleHandles.length; i++) {
         int moduleHandle = moduleHandles[i];
         if (this.isShowAll()
            || !ApplicationControl.isSignedWithRRI(moduleHandle)
            || !InternalServices.isDeviceSecure() && !CodeModuleManager.getModuleName(moduleHandle).startsWith("net_rim")) {
            Arrays.add(modules, new InstalledModulesOptionsItem$InstalledModuleListItem(moduleHandle));
         }
      }

      Arrays.sort(modules, new InstalledModulesOptionsItem$InstalledModuleListItemComparator());
      this.setList(modules);
      this.listUpdated();
   }

   @Override
   protected final KeywordIndexerHelper getKeywordIndexer() {
      return new InstalledModulesOptionsItem$InstalledModulesList$1(this);
   }

   @Override
   protected final String getEmptyString() {
      return OptionsResources.getString(1966);
   }

   @Override
   protected final void paintElement(Object element, Graphics graphics, int y, int width) {
      InstalledModulesOptionsItem$InstalledModuleListItem item = (InstalledModulesOptionsItem$InstalledModuleListItem)element;
      int widthDrawn = graphics.drawText(CodeModuleManager.getModuleVersion(item.getModuleHandle()), 0, y, 5, width);
      if (ApplicationControl.differsFromUserDefaults(item.getModuleHandle())) {
         Font f = graphics.getFont();
         graphics.setFont(f.derive(1, f.getHeight()));
         graphics.drawText(item.getModuleName(), 0, y, 70, width - widthDrawn - 2);
         graphics.setFont(f);
      } else {
         graphics.drawText(item.getModuleName(), 0, y, 70, width - widthDrawn - 2);
      }
   }
}
