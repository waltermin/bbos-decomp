package net.rim.tid.im.conv.europe.repository;

public class RegularExpression$StringCharacterIterator implements RegularExpression$SimpleCharacterIterator {
   String str;
   int index;

   protected void setString(String str) {
      this.str = str;
      this.index = 0;
   }

   @Override
   public char next() {
      return this.str.charAt(this.index++);
   }

   @Override
   public void close() {
   }

   @Override
   public boolean hasNext() {
      return this.index < this.str.length();
   }

   public RegularExpression$StringCharacterIterator(String str) {
      this.setString(str);
   }
}
