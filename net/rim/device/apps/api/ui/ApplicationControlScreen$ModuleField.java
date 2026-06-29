package net.rim.device.apps.api.ui;

import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.component.LabelField;

final class ApplicationControlScreen$ModuleField extends LabelField {
   int _handle;
   private final ApplicationControlScreen this$0;

   ApplicationControlScreen$ModuleField(ApplicationControlScreen _1, int handle) {
      super(CodeModuleManager.getModuleName(handle), 18014398509481984L);
      this.this$0 = _1;
      this._handle = handle;
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      contextMenu.addItem(new ApplicationControlScreen$ModuleMenuItem(this.this$0, this._handle));
   }
}
