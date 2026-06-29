package net.rim.tid.im.conv.europe.repository;

public class RegularExpression$EmptyCharacterIterator implements RegularExpression$SimpleCharacterIterator {
   @Override
   public boolean hasNext() {
      return false;
   }

   @Override
   public char next() {
      throw new IllegalArgumentException();
   }

   @Override
   public void close() {
   }
}
