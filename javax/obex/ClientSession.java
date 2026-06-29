package javax.obex;

import javax.microedition.io.Connection;

public interface ClientSession extends Connection {
   void setAuthenticator(Authenticator var1);

   HeaderSet createHeaderSet();

   void setConnectionID(long var1);

   long getConnectionID();

   HeaderSet connect(HeaderSet var1);

   HeaderSet disconnect(HeaderSet var1);

   HeaderSet setPath(HeaderSet var1, boolean var2, boolean var3);

   HeaderSet delete(HeaderSet var1);

   Operation get(HeaderSet var1);

   Operation put(HeaderSet var1);
}
