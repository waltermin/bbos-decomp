package javax.obex;

import javax.microedition.io.Connection;

public interface SessionNotifier extends Connection {
   Connection acceptAndOpen(ServerRequestHandler var1);

   Connection acceptAndOpen(ServerRequestHandler var1, Authenticator var2);
}
