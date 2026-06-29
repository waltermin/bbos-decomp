package javax.microedition.io;

import java.io.DataOutputStream;
import java.io.OutputStream;

public interface OutputConnection extends Connection {
   OutputStream openOutputStream();

   DataOutputStream openDataOutputStream();
}
