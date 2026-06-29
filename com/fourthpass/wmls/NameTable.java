package com.fourthpass.wmls;

import java.util.Hashtable;

final class NameTable {
   Hashtable _names;

   NameTable(WMLInputStream stream) {
      int functionCount = stream.readUInt8();
      this._names = new Hashtable(functionCount);

      for (int i = 0; i < functionCount; i++) {
         FunctionName fname = new FunctionName(stream);
         this._names.put(fname.toString(), new Integer(fname.getIndex()));
      }
   }

   final int getIndex(String s) throws Exception {
      Integer integer = (Integer)this._names.get(s);
      if (integer == null) {
         throw new Exception();
      } else {
         return integer;
      }
   }
}
