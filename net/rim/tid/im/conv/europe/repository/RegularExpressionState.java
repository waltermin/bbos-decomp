package net.rim.tid.im.conv.europe.repository;

public interface RegularExpressionState {
   Object newMark();

   void rollback(Object var1);

   long mark();

   void rollback(long var1);

   boolean isFinal();
}
