package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESDatePrototype$ESDatePutFunction extends ESDatePrototype$ESDateFunction {
   protected double _p0;
   protected double _p1;
   protected double _p2;
   protected double _p3;
   protected int _nParms;

   ESDatePrototype$ESDatePutFunction(String name, int length) {
      super(name, length);
   }

   ESDatePrototype$ESDatePutFunction(String name) {
      this(name, 0);
   }

   protected void setUTCDate(double hour, double min, double sec, double ms, double day) {
      super._date.setValue(ESDatePrototype.timeClip(ESDatePrototype.makeDate(day, ESDatePrototype.makeTime(hour, min, sec, ms))));
   }

   protected void setDate(double hour, double min, double sec, double ms, double day) {
      super._date.setValue(ESDatePrototype.timeClip(ESDatePrototype.UTC(ESDatePrototype.makeDate(day, ESDatePrototype.makeTime(hour, min, sec, ms)))));
   }

   protected void setUTCDate(double year, double month, double date, double time) {
      super._date.setValue(ESDatePrototype.timeClip(ESDatePrototype.makeDate(ESDatePrototype.makeDay(year, month, date), time)));
   }

   protected void setDate(double year, double month, double date, double time) {
      super._date.setValue(ESDatePrototype.timeClip(ESDatePrototype.UTC(ESDatePrototype.makeDate(ESDatePrototype.makeDay(year, month, date), time))));
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() throws ThrownValue {
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         super._date = (ESDate)this.getThis();
         double cce = super._date.getValue();
         if (Double.isNaN(cce)) {
            String name = this.getName();
            if (name != "setFullYear" && name != "setYear" && name != "setUTCFullYear") {
               return Value.NaN;
            }

            super._t = 0;
         }

         super._t = (long)cce;
         this._nParms = this.getNumParms();
         this._p0 = Convert.toDouble(this.getParm(0));
         this._p1 = Convert.toDouble(this.getParm(1));
         this._p2 = Convert.toDouble(this.getParm(2));
         this._p3 = Convert.toDouble(this.getParm(3));
         var5 = false;
      } finally {
         if (var5) {
            throw ThrownValue.typeError(Resources.getString(50), this.getName());
         }
      }

      this.put();
      return Value.makeDoubleValue(super._date.getValue());
   }

   void put() {
      throw null;
   }
}
