package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.framework.model.Recur;
import net.rim.wica.runtime.access.internal.data.handlers.LongFieldHandler;

final class RepeatRuleCollection$EndHandler implements LongFieldHandler {
   private RepeatRuleCollection$EndHandler() {
   }

   @Override
   public final long getValue(Object item) {
      return item instanceof Recur && ((Recur)item).isFinite() ? ((Recur)item).getEndDate() : 0;
   }

   RepeatRuleCollection$EndHandler(RepeatRuleCollection$1 x0) {
      this();
   }
}
