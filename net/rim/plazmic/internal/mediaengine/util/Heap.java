package net.rim.plazmic.internal.mediaengine.util;

import net.rim.plazmic.internal.mediaengine.event.Event;

public class Heap {
   protected Array _heap;
   protected int _end;
   protected int _size;
   private static final int START_SIZE = 10;
   private static final int SIZE_INCREMENT = 10;

   public Heap(Array h) {
      this._heap = h;
      this._end = 0;
      this._size = 0;
   }

   public int getSize() {
      return this._end;
   }

   public boolean put(Object o) {
      this.add(o);
      this.fixUp(this._end - 1);
      return true;
   }

   public boolean peek(Object o) {
      if (this._end <= 0) {
         return false;
      }

      this._heap.get(o, 0);
      return true;
   }

   public boolean pop(Object e) {
      if (this._end <= 0) {
         return false;
      }

      this._heap.get(e, 0);

      while (this._heap.equals(0, e) && this._end != 0) {
         this._heap.copy(--this._end, 0, 1);
         this._heap.clear(this._end);
         this.fixDown(0);
      }

      return true;
   }

   public void remove(Object o) {
      int count = 0;

      while (count < this._end) {
         if (this._heap.equals(count, o)) {
            this._end--;
            if (count < this._end) {
               this._heap.swap(this._end, count);
            }

            this._heap.clear(this._end);
            this.fixDown(count);
         } else {
            count++;
         }
      }
   }

   public void flush() {
      while (this._end > 0) {
         this._heap.clear(this._end--);
      }
   }

   public boolean move(Heap heap, Object o) {
      boolean ok = true;

      while (ok && heap._end != 0) {
         heap._heap.get(o, --heap._end);
         heap._heap.clear(heap._end);
         ok &= this.put(o);
      }

      return ok;
   }

   private void add(Object o) {
      if (this._size - 1 == this._end) {
         this._size += 10;
         this._heap.growTo(this._size, 0, this._end, false);
      } else if (this._size == 0) {
         this._size = 10;
         this._heap.init(this._size);
      }

      this._heap.set(o, this._end++);
   }

   protected void fixDown(int cur) {
      int j;
      while ((j = (cur << 1) + 1) < this._end) {
         if (j + 1 < this._end && this._heap.compare(j, j + 1) > 0) {
            j++;
         }

         if (this._heap.compare(j, cur) >= 0) {
            return;
         }

         this._heap.swap(cur, j);
         cur = j;
      }
   }

   protected void fixUp(int curPos) {
      while (curPos > 0) {
         int parent = curPos - (2 - curPos % 2) >> 1;
         if (this._heap.compare(parent, curPos) > 0) {
            this._heap.swap(parent, curPos);
            curPos = parent;
            continue;
         }
         break;
      }
   }

   @Override
   public String toString() {
      if (this._end <= 0) {
         return "Empty";
      }

      Event o = new Event();
      StringBuffer buff = new StringBuffer();

      for (int i = 0; i < this._end; i++) {
         this._heap.get(o, i);
         buff.append(o).append('\n');
      }

      return buff.toString();
   }
}
