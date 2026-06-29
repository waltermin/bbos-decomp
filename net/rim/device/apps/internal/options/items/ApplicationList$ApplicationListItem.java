package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleContextProxy;

final class ApplicationList$ApplicationListItem implements AccessibleContextProxy {
   CodeModuleGroup _group;
   boolean _usesDefaults;

   ApplicationList$ApplicationListItem(CodeModuleGroup group, boolean usesDefaults) {
      this._group = group;
      this._usesDefaults = usesDefaults;
   }

   @Override
   public final AccessibleContext getAccessibleContext() {
      return (AccessibleContext)(new Object(this._group.getFriendlyName()));
   }
}
