package net.rim.device.apps.api.messaging.util;

import net.rim.device.api.system.ApplicationRegistry;

public class AnonymousMessages {
   protected static final long KEY = -7420407412231626381L;

   private static AnonymousMessages getInstance() {
      return (AnonymousMessages)ApplicationRegistry.getApplicationRegistry().waitFor(-7420407412231626381L);
   }

   public static void createAnonymousMessage(String from, String subject, String body) {
      getInstance().createAnonymousMessageImpl(from, subject, body);
   }

   public static boolean isFirstBoot() {
      return getInstance().isFirstBootImpl();
   }

   protected void createAnonymousMessageImpl(String _1, String _2, String _3) {
      throw null;
   }

   protected boolean isFirstBootImpl() {
      throw null;
   }
}
