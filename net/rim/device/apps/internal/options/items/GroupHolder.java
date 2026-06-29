package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.CodeModuleGroup;

final class GroupHolder {
   CodeModuleGroup _cmg;
   int[] _moduleHandles;

   GroupHolder(CodeModuleGroup cmg, int[] moduleHandles) {
      this._cmg = cmg;
      this._moduleHandles = moduleHandles;
   }

   @Override
   public final boolean equals(Object o) {
      if (o instanceof GroupHolder) {
         return this._cmg == ((GroupHolder)o)._cmg;
      } else {
         return o instanceof CodeModuleGroup ? this._cmg == o : super.equals(o);
      }
   }
}
