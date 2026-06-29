package javax.obex;

import net.rim.device.cldc.io.btgoep.HeaderSetImpl;

public class ServerRequestHandler {
   private long _connectionID = -1;

   protected ServerRequestHandler() {
   }

   public final HeaderSet createHeaderSet() {
      return new HeaderSetImpl();
   }

   public void setConnectionID(long id) {
      if (id >= -1 && id <= 4294967295L) {
         this._connectionID = id;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public long getConnectionID() {
      return this._connectionID;
   }

   public int onConnect(HeaderSet request, HeaderSet reply) {
      return 160;
   }

   public void onDisconnect(HeaderSet request, HeaderSet reply) {
   }

   public int onSetPath(HeaderSet request, HeaderSet reply, boolean backup, boolean create) {
      return 209;
   }

   public int onDelete(HeaderSet request, HeaderSet reply) {
      return 209;
   }

   public int onPut(Operation op) {
      return 209;
   }

   public int onGet(Operation op) {
      return 209;
   }

   public void onAuthenticationFailure(byte[] userName) {
   }
}
