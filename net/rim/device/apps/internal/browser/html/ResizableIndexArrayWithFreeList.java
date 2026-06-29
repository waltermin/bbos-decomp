package net.rim.device.apps.internal.browser.html;

import java.util.Vector;

class ResizableIndexArrayWithFreeList extends ResizableIndexArray {
   protected int _highWaterMark = 0;
   Vector _others = new Vector();

   ResizableIndexArrayWithFreeList() {
      this.addParallelArray(this);
      this.freeSlots(1, this.getLength() - 1);
   }

   void addParallelArray(ResizableIndexArray other) {
      this._others.addElement(other);
   }

   int grow(int minNeeded) {
      int oldLength = this.getLength();
      int newLength = ArrayResize.roundToSectionSize(minNeeded, super._sectionSize) + super._sectionSize;

      for (int i = this._others.size() - 1; i >= 0; i--) {
         ((ResizableIndexArray)this._others.elementAt(i)).setSize(newLength);
      }

      this.freeSlots(oldLength, newLength - 1);
      return newLength;
   }

   void shrink() {
      int newLength = ArrayResize.roundToSectionSize(this._highWaterMark + 1, super._sectionSize);
      int owner = 0;

      while (true) {
         int currFree = this.get(owner);
         if (currFree == 0) {
            for (int i = this._others.size() - 1; i >= 0; i--) {
               ((ResizableIndexArray)this._others.elementAt(i)).setSize(newLength);
            }

            return;
         }

         if (currFree >= newLength) {
            this.set(owner, this.get(currFree));
         } else {
            owner = currFree;
         }
      }
   }

   void freeSlots(int first, int last) {
      this.set(last, this.get(0));

      for (int i = last - 1; i >= first; i--) {
         this.set(i, i + 1);
      }

      this.set(0, first);
   }

   void freeSlot(int handle) {
      this.set(handle, this.get(0));
      this.set(0, handle);
   }

   int findFreeSlot() {
      if (this.get(0) == 0) {
         this.grow(this.getLength() + 1);
      }

      int rc = this.get(0);
      this.set(0, this.get(rc));
      if (rc > this._highWaterMark) {
         this._highWaterMark = rc;
      }

      return rc;
   }

   int getFirstFree() {
      return this.get(0);
   }

   int getNextFree(int i) {
      return this.get(i);
   }
}
