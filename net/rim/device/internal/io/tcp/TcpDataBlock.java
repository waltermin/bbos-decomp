package net.rim.device.internal.io.tcp;

public interface TcpDataBlock {
   int getLeftEdge();

   int getRightEdge();

   void removeUpTo(int var1);

   TcpDataBlock getNext();

   TcpDataBlock getPrev();

   void setNext(TcpDataBlock var1);

   void setPrev(TcpDataBlock var1);

   void append(TcpDataBlock var1);

   int size();

   int getMemoryUsed();
}
