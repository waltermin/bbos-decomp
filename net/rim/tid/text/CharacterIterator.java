package net.rim.tid.text;

public class CharacterIterator {
   protected int iBegin;
   protected int iEnd;
   protected int iIndex;
   public static final char DONE = '\uffff';

   public char current() {
      throw null;
   }

   public char setIndex(int _1) {
      throw null;
   }

   public char first() {
      return this.setIndex(this.iBegin);
   }

   public char last() {
      return this.setIndex(this.iEnd - 1);
   }

   public boolean hasNext() {
      return this.iIndex >= this.iBegin && this.iIndex < this.iEnd;
   }

   public char next() {
      char c = this.current();
      if (this.iIndex >= this.iBegin && this.iIndex < this.iEnd) {
         this.setIndex(this.iIndex + 1);
      }

      return c;
   }

   public char previous() {
      return this.iIndex > this.iBegin && this.iIndex <= this.iEnd ? this.setIndex(this.iIndex - 1) : '\uffff';
   }

   public int getBeginIndex() {
      return this.iBegin;
   }

   public int getEndIndex() {
      return this.iEnd;
   }

   public int getIndex() {
      return this.iIndex;
   }
}
