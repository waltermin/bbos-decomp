package net.rim.device.cldc.io.tunnel;

public interface TunnelApnList {
   void removeFirst();

   void addFirst(String var1);

   void addLast(String var1);

   String getFirst();

   int getSize();

   void resetList();

   void initializeList(Object var1);
}
