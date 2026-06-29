package net.rim.tid.im.conv;

public class SlChVariants$BigramPair {
   public int _left;
   public int _rigth;
   public int _index;

   public SlChVariants$BigramPair() {
      this.reset();
   }

   public boolean isEmpty() {
      return this._index == -1;
   }

   public void reset() {
      this._left = -1;
      this._rigth = -1;
      this._index = -1;
   }
}
