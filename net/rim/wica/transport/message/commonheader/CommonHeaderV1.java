package net.rim.wica.transport.message.commonheader;

public interface CommonHeaderV1 {
   long getSenderId();

   void setSenderId(long var1);

   long getWicletId();

   void setWicletId(long var1);

   boolean isBundle();

   int getMessageCount();

   boolean hasMore();

   void haveMore(boolean var1);
}
