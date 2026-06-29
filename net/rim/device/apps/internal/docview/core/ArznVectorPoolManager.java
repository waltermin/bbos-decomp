package net.rim.device.apps.internal.docview.core;

import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

final class ArznVectorPoolManager {
   private ArznObject[] _pool;
   private boolean[] _inUse;
   private int _iObjectType;

   ArznVectorPoolManager(int iCapacity, int iObjectType) {
      this._pool = new ArznObject[iCapacity];
      this._inUse = new boolean[iCapacity];
      this._iObjectType = iObjectType;
      iCapacity--;

      while (iCapacity >= 0) {
         this._pool[iCapacity] = this.createEmptyObject();
         iCapacity--;
      }
   }

   final synchronized ArznObject getObject() {
      int iPoolSize = this._pool.length;

      for (int i = 0; i < iPoolSize; i++) {
         if (!this._inUse[i]) {
            this._inUse[i] = true;
            return this._pool[i];
         }
      }

      ArznObject newEntry = this.createEmptyObject();
      Array.resize(this._inUse, iPoolSize + 1);
      this._inUse[iPoolSize] = true;
      Arrays.add(this._pool, newEntry);
      return newEntry;
   }

   final void putObject(Object obj) {
      int iPoolSize = this._pool.length;

      for (int i = iPoolSize - 1; i >= 0; i--) {
         if (obj == this._pool[i]) {
            this._inUse[i] = false;
            return;
         }
      }
   }

   private final ArznObject createEmptyObject() {
      switch (this._iObjectType) {
         case 0:
            return new ArznParagraph();
         case 16:
            return new ArznPage();
         case 21:
            return new ArznImageContainer();
         case 24:
            return new ArznSheet();
         case 26:
            return new ArznCell();
         case 28:
            return new ArznTableRow();
         case 39:
            return new ArznDocInfoContainer();
         case 54:
            return new ArznTrackChangeContainer();
         case 71:
            return new ArznRefContainer();
         case 80:
            return new ArznTableRegion();
         case 84:
            return new ArznAudioContainer();
         case 65534:
            return new ArznSimpleObject();
         case 65535:
            return new ArznObject();
         default:
            return null;
      }
   }
}
