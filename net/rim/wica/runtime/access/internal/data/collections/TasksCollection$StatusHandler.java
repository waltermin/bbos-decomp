package net.rim.wica.runtime.access.internal.data.collections;

import javax.microedition.pim.PIMItem;
import net.rim.wica.runtime.access.internal.data.enumeration.StatusEnumConverter;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;

final class TasksCollection$StatusHandler implements IntFieldHandler {
   boolean _statusSupported;

   public TasksCollection$StatusHandler(boolean supported) {
      this._statusSupported = supported;
   }

   @Override
   public final int getValue(Object item) {
      if (item instanceof Object && this._statusSupported) {
         try {
            return StatusEnumConverter.deviceToCommon(((PIMItem)item).getInt(16777225, 0));
         } finally {
            return -1;
         }
      } else {
         return -1;
      }
   }
}
