package net.rim.device.internal.content;

import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;

public class ContentHandlerRegistrationHelper {
   public static final String HANDLER_PREFIX;
   public static final String APPLICATION_DESCRIPTOR_ARG_CONTENT_HANDLER_WORK;
   protected static final int VERIFICATION_SUCCESS;
   protected static final int VERIFICATION_AUTHENTICATION_FAILED;
   protected static final int VERIFICATION_DUPLICATE_ID;
   protected static final int VERIFICATION_FAILED;
   protected static final long APP_REGISTRY_KEY;

   protected ContentHandlerRegistrationHelper() {
   }

   public static ContentHandlerRegistrationHelper getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (ContentHandlerRegistrationHelper)ar.waitFor(-352407102385872585L);
   }

   public int verifyJadAttributes(Hashtable _1, boolean _2) {
      throw null;
   }

   public void registerContentHandlers(int _1) {
      throw null;
   }

   public void unregisterContentHandler(String _1) {
      throw null;
   }

   public void moduleUpgraded(String _1, int _2) {
      throw null;
   }
}
