package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.ui.container.VerticalFieldManager;

final class AgendaController$AgendaVerticalFieldManager extends VerticalFieldManager {
   private final AgendaController this$0;

   public AgendaController$AgendaVerticalFieldManager(AgendaController _1) {
      super(2306142076376449024L);
      this.this$0 = _1;
   }

   @Override
   protected final boolean isDownArrowShown() {
      return super.isDownArrowShown() || this.this$0._agenda.eventsExistAfterLast();
   }

   @Override
   protected final boolean isUpArrowShown() {
      return super.isUpArrowShown() || this.this$0._agenda.eventsExistBeforeFirst();
   }
}
