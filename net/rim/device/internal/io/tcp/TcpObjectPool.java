package net.rim.device.internal.io.tcp;

public interface TcpObjectPool {
   TcpDatagramBase getNewTcpDatagram();

   TcpDatagramBase getNewTcpDatagram(TcpAddress var1);

   void giveBackDatagram(TcpDatagramBase var1);

   TcpDatagramProperties getNewTcpDatagramProperties();

   void giveBackTcpDatagramProperties(TcpDatagramProperties var1);
}
