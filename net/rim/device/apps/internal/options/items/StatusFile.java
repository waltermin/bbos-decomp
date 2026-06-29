package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.Memory;
import net.rim.device.apps.internal.options.resources.OptionsResources;

public final class StatusFile extends StatusListItem {
   private boolean _total;

   public StatusFile(boolean total) {
      super(total ? 404 : 403);
      this._total = total;
   }

   @Override
   public final String getDisplayValue() {
      return ((StringBuffer)(new Object()))
         .append(String.valueOf(this._total ? Memory.getFlashTotal() : Memory.getFlashFree()))
         .append(OptionsResources.getString(406))
         .toString();
   }
}
