package net.rim.device.cldc.io.http;

import net.rim.device.api.system.ApplicationRegistry;

public final class DefaultHttpStack {
   private String _directive;
   private static final long ID;
   public static String TCP = "tcp";
   public static String MDS = "mds";
   private static DefaultHttpStack _instance;

   private DefaultHttpStack() {
   }

   public static final DefaultHttpStack getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (DefaultHttpStack)ar.getOrWaitFor(-3235705002630425866L);
         if (_instance == null) {
            _instance = new DefaultHttpStack();
            ar.put(-3235705002630425866L, _instance);
         }
      }

      return _instance;
   }

   public final String getDirective() {
      return this._directive;
   }

   public final void setDirective(String directive) {
      this._directive = directive;
   }
}
