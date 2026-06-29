package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.framework.model.Recur;
import net.rim.wica.runtime.access.internal.data.enumeration.FrequencyEnumConverter;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;

final class RepeatRuleCollection$FrequencyHandler implements IntFieldHandler {
   private RepeatRuleCollection$FrequencyHandler() {
   }

   @Override
   public final int getValue(Object item) {
      return !(item instanceof Object) ? 0 : FrequencyEnumConverter.deviceToCommon(((Recur)item).getRecurType());
   }

   RepeatRuleCollection$FrequencyHandler(RepeatRuleCollection$1 x0) {
      this();
   }
}
