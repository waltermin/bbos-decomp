package net.rim.device.apps.internal.docview.core;

import net.rim.device.api.util.IntIntHashtable;

final class FormatObject {
   private IntIntHashtable _fontStyleHash = (IntIntHashtable)(new Object());
   private IntIntHashtable _foreColorHash = (IntIntHashtable)(new Object());
   private IntIntHashtable _bgColorHash = (IntIntHashtable)(new Object());
   private IntIntHashtable _fontSizeHash = (IntIntHashtable)(new Object());

   final int getCurrentInfo(int key, int tag) {
      IntIntHashtable hash = this.getCorrectHashtable(tag);
      synchronized (hash) {
         for (int i = key; i >= 0; i--) {
            if (hash.containsKey(i)) {
               return hash.get(i);
            }
         }

         return -1;
      }
   }

   final void setCurrentInfo(int key, int tag, int param) {
      this.getCorrectHashtable(tag).put(key, param);
   }

   final void discardCurrentInfo(int key) {
      this._fontStyleHash.remove(key);
      this._foreColorHash.remove(key);
      this._bgColorHash.remove(key);
      this._fontSizeHash.remove(key);
   }

   final void clear() {
      this._fontStyleHash.clear();
      this._foreColorHash.clear();
      this._bgColorHash.clear();
      this._fontSizeHash.clear();
   }

   final boolean isDefaultInfo(int key) {
      if (this.getCurrentInfo(key, 0) != -1) {
         return false;
      } else if (this.getCurrentInfo(key, 1) != -1) {
         return false;
      } else {
         return this.getCurrentInfo(key, 2) != -1 ? false : this.getCurrentInfo(key, 3) == -1;
      }
   }

   private final IntIntHashtable getCorrectHashtable(int tag) {
      switch (tag) {
         case -1:
            throw new Object();
         case 0:
         default:
            return this._fontStyleHash;
         case 1:
            return this._foreColorHash;
         case 2:
            return this._bgColorHash;
         case 3:
            return this._fontSizeHash;
      }
   }
}
