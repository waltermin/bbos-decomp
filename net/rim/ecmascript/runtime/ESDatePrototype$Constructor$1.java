package net.rim.ecmascript.runtime;

class ESDatePrototype$Constructor$1 extends HostFunction {
   private final ESDatePrototype$Constructor this$0;

   ESDatePrototype$Constructor$1(ESDatePrototype$Constructor _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      double year = Convert.toDouble(this.getParm(0));
      double month = Convert.toDouble(this.getParm(1));
      double date = Convert.toDouble(this.getParm(2, Value.ONE));
      double hours = Convert.toDouble(this.getParm(3, Value.ZERO));
      double min = Convert.toDouble(this.getParm(4, Value.ZERO));
      double sec = Convert.toDouble(this.getParm(5, Value.ZERO));
      double ms = Convert.toDouble(this.getParm(6, Value.ZERO));
      if (year == year) {
         int iYear = (int)year;
         if (iYear >= 0 && iYear <= 99) {
            year += 4656071103817449472L;
         }
      }

      double day = ESDatePrototype.makeDay(year, month, date);
      double time = ESDatePrototype.makeTime(hours, min, sec, ms);
      time = ESDatePrototype.makeDate(day, time);
      return Value.makeDoubleValue(ESDatePrototype.timeClip(time));
   }
}
