package javax.microedition.io;

public interface HttpsConnection extends HttpConnection {
   SecurityInfo getSecurityInfo();

   @Override
   int getPort();
}
