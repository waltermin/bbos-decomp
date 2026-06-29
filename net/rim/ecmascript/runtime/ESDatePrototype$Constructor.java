package net.rim.ecmascript.runtime;

class ESDatePrototype$Constructor extends Constructor {
   ESDatePrototype$Constructor() {
      super("Date", GlobalObject.getInstance().datePrototype);
      this.addHostFunction(new ESDatePrototype$Constructor$1(this, "Date", "UTC", 7));
      this.addHostFunction(new ESDatePrototype$Constructor$2(this, "Date", "parse", 1));
   }

   @Override
   public long run() {
      if (!this.calledAsConstructor()) {
         return Value.makeStringValue(ESDatePrototype.toString(ESDatePrototype.now(), 2));
      }

      int nParms = this.getNumParms();
      if (nParms == 0) {
         return Value.makeObjectValue(new ESDate(ESDatePrototype.now()));
      }

      if (nParms == 1) {
         long parm = Convert.toPrimitive(this.getParm(0));
         if (Value.getType(parm) == 5) {
            double d;
            try {
               d = ESDatePrototype.parseDate(Value.getStringValue(parm));
            } catch (ESDatePrototype$CantParse cp) {
               d = (double)9221120237041090560L;
            }

            return Value.makeObjectValue(new ESDate(d));
         } else {
            return Value.makeObjectValue(new ESDate(ESDatePrototype.timeClip(Convert.toDouble(parm))));
         }
      } else {
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
         return Value.makeObjectValue(new ESDate(ESDatePrototype.timeClip(ESDatePrototype.UTC(time))));
      }
   }
}
