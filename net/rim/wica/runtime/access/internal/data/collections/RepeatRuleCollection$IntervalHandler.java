package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.framework.model.Recur;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;

final class RepeatRuleCollection$IntervalHandler implements IntFieldHandler {
   private RepeatRuleCollection$IntervalHandler() {
   }

   @Override
   public final int getValue(Object item) {
      return !(item instanceof Object) ? 1 : ((Recur)item).getRecurPeriod();
   }

   RepeatRuleCollection$IntervalHandler(RepeatRuleCollection$1 x0) {
      this();
   }
}
