package javax.microedition.jcrmi;

import java.rmi.Remote;
import javax.microedition.io.Connection;

public interface JavaCardRMIConnection extends Connection {
   short PINENTRY_CANCELLED = -1;

   Remote getInitialReference();

   short enterPin(int var1);

   short changePin(int var1);

   short disablePin(int var1);

   short enablePin(int var1);

   short unblockPin(int var1, int var2);
}
