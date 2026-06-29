package com.fourthpass.wmls;

final class Pragma {
   private int _type;
   private int _index;
   static final int ACCESS_DOMAIN;
   static final int ACCESS_PATH;
   static final int USR_AGENT_PROP;
   static final int USR_AGENT_PROP_SCHEME;

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
