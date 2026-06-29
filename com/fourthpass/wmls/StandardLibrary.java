package com.fourthpass.wmls;

final class StandardLibrary {
   private short _cacheCount;
   private short _cacheIndex;
   private Lib[] _libs = new Lib[5];
   private int[] _keys = new int[5];
   private static final int MAX_CACHE;
   private static final String[] _libPath = new String[]{
      "com.fourthpass.wmls.L0",
      "com.fourthpass.wmls.L1",
      "com.fourthpass.wmls.L2",
      "com.fourthpass.wmls.L3",
      "com.fourthpass.wmls.L4",
      "com.fourthpass.wmls.L5"
   };

   public StandardLibrary() {
   }

   public final Value invoke(int lib, int func, Interpreter$Engine engine) {
      int libIndex;
      if ((libIndex = this.isCached(lib)) == -1) {
         Lib obj = this.loadLibrary(lib);
         this.cache(lib, obj);
         return obj.invoke(func, engine);
      } else {
         return this._libs[libIndex].invoke(func, engine);
      }
   }

   private final Lib loadLibrary(int i) {
      return (Lib)Class.forName(_libPath[i]).newInstance();
   }

   private final int isCached(int lib) {
      for (int i = 0; i < this._cacheCount; i++) {
         if (this._keys[i] == lib) {
            return i;
         }
      }

      return -1;
   }

   private final void cache(int lib, Lib obj) {
      if (this._cacheIndex == 5) {
         this._cacheIndex = 0;
      }

      this._keys[this._cacheIndex] = lib;
      this._libs[this._cacheIndex] = obj;
      this._cacheIndex++;
      if (this._cacheCount < 5) {
         this._cacheCount++;
      }
   }
}
