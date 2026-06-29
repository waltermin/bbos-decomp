package net.rim.device.cldc.io.mdp;

final class MdpMTHUtil {
   static final long GUID;
   private static Transport _transport = Transport.getInstance();

   private MdpMTHUtil() {
   }

   static final Transport getTransport() {
      return _transport;
   }

   static {
      GUID = Transport.getInstance().GUID;
   }
}
