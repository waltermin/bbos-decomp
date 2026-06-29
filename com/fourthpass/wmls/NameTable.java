package com.fourthpass.wmls;

import java.util.Hashtable;

final class NameTable {
   Hashtable _names;

   NameTable(WMLInputStream stream) {
      int functionCount = stream.readUInt8();
      this._names = (Hashtable)(new Object(functionCount));

      for (int i = 0; i < functionCount; i++) {
         FunctionName fname = new FunctionName(stream);
         this._names.put(fname.toString(), new Object(fname.getIndex()));
      }
   }

   final int getIndex(String s) {
      Integer integer = (Integer)this._names.get(s);
      if (integer == null) {
         throw new Object();
      } else {
         return integer;
      }
   }
}
