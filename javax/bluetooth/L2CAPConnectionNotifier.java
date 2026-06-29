package javax.bluetooth;

import javax.microedition.io.Connection;

public interface L2CAPConnectionNotifier extends Connection {
   L2CAPConnection acceptAndOpen();
}
