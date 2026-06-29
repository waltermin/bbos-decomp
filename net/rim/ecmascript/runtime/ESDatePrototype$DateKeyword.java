package net.rim.ecmascript.runtime;

class ESDatePrototype$DateKeyword {
   String name;
   int type;
   int value;
   static final int DAY = 0;
   static final int MONTH = 1;
   static final int TZ = 2;
   static final int AMPM = 3;

   ESDatePrototype$DateKeyword(String nam, int typ, int val) {
      this.name = nam;
      this.type = typ;
      this.value = val;
   }

   ESDatePrototype$DateKeyword(String str, int typ) {
      this(str, typ, 0);
   }
}
