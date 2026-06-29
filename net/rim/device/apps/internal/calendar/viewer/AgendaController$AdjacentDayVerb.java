package net.rim.device.apps.internal.calendar.viewer;

final class AgendaController$AdjacentDayVerb extends CalendarViewerVerb {
   boolean _nextDay;
   private final AgendaController this$0;

   public AgendaController$AdjacentDayVerb(AgendaController _1, boolean nextDay) {
      super(nextDay ? 329 : 328, nextDay ? 479525 : 479520, nextDay ? 340 : 341);
      this.this$0 = _1;
      this._nextDay = nextDay;
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0._agenda.gotoAdjacentDay(this._nextDay);
      return null;
   }
}
