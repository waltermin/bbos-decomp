package net.rim.device.internal.io.tcp;

public interface TcpConnectionIdentifier {
   int getConnectionIpAddress();

   int getConnectionLocalPort();

   void setConnectionLocalPort(int var1);

   int getConnectionDestinationPort();

   String getConnectionApn();
}
