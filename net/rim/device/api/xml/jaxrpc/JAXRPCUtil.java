package net.rim.device.api.xml.jaxrpc;

import javax.xml.rpc.JAXRPCException;
import net.rim.device.api.i18n.MessageFormat;

final class JAXRPCUtil {
   private JAXRPCUtil() {
   }

   static final void handleError(Exception e) {
      throw new JAXRPCException(e);
   }

   static final void handleError(String message) {
      throw new JAXRPCException(message);
   }

   static final void handleError(String message, String arg) {
      throw new JAXRPCException(MessageFormat.format(message, new Object[]{arg}));
   }

   static final void handleError(String message, Object[] args) {
      throw new JAXRPCException(MessageFormat.format(message, args));
   }
}
