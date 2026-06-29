package net.rim.ecmascript.runtime;

class ESDatePrototype$DateKeyword {
   String name;
   int type;
   int value;
   static final int DAY;
   static final int MONTH;
   static final int TZ;
   static final int AMPM;

   ESDatePrototype$DateKeyword(String nam, int typ, int val) {
      this.name = nam;
      this.type = typ;
      this.value = val;
   }

   ESDatePrototype$DateKeyword(String str, int typ) {
      this(str, typ, 0);
   }
}
