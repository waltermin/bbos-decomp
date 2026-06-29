package javax.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.internal.io.RIMConnector;
import net.rim.vm.TraceBack;

public class Connector {
   public static final int READ = 1;
   public static final int WRITE = 2;
   public static final int READ_WRITE = 3;

   private Connector() {
   }

   public static Connection open(String name) {
      int caller = TraceBack.getCallingModule(0);
      return RIMConnector.open(caller, name, 3, false);
   }

   public static Connection open(String name, int mode) {
      int caller = TraceBack.getCallingModule(0);
      return RIMConnector.open(caller, name, mode, false);
   }

   public static Connection open(String name, int mode, boolean timeouts) {
      int caller = TraceBack.getCallingModule(0);
      return RIMConnector.open(caller, name, mode, timeouts);
   }

   public static DataInputStream openDataInputStream(String name) {
      int caller = TraceBack.getCallingModule(0);
      return RIMConnector.openDataInputStream(caller, name);
   }

   public static DataOutputStream openDataOutputStream(String name) {
      int caller = TraceBack.getCallingModule(0);
      return RIMConnector.openDataOutputStream(caller, name);
   }

   public static InputStream openInputStream(String name) {
      int caller = TraceBack.getCallingModule(0);
      return RIMConnector.openDataInputStream(caller, name);
   }

   public static OutputStream openOutputStream(String name) {
      int caller = TraceBack.getCallingModule(0);
      return RIMConnector.openDataOutputStream(caller, name);
   }
}
