package com.fourthpass.wmls;

final class Pragma {
   private int _type;
   private int _index;
   static final int ACCESS_DOMAIN = 0;
   static final int ACCESS_PATH = 1;
   static final int USR_AGENT_PROP = 2;
   static final int USR_AGENT_PROP_SCHEME = 3;

   Pragma(WMLInputStream stream, int type) {
      this._type = type;
      this._index = stream.readMBInt();
      if (type >= 2) {
         stream.readMBInt();
         if (type >= 3) {
            stream.readMBInt();
         }
      }
   }

   public final int getType() {
      return this._type;
   }

   public final int getIndex() {
      return this._index;
   }
}
