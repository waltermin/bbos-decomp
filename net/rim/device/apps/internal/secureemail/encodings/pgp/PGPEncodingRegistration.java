package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.api.system.SystemListener;
import net.rim.device.apps.internal.secureemail.encodings.pgp.server.PGPUniversalAuthenticationListener;
import net.rim.device.internal.proxy.Proxy;

final class PGPEncodingRegistration implements SystemListener {
   private PGPUniversalAuthenticationListener _universalAuthenticationListener;
   private boolean _powerUpReceived;

   static final void registerPGPEncoding() {
      new PGPEncodingRegistration().doBlockingStartupWork();
   }

   private PGPEncodingRegistration() {
   }

   private final void doBlockingStartupWork() {
      PGPFactory pgpFactory = PGPFactory.getInstance();
      pgpFactory.performRegistration();
      this._universalAuthenticationListener = new PGPUniversalAuthenticationListener();
      Proxy.getInstance().addGlobalEventListener(this._universalAuthenticationListener);
      Proxy.getInstance().addSystemListener(this);
   }

   @Override
   public final void powerUp() {
      synchronized (this) {
         if (this._powerUpReceived) {
            return;
         }

         this._powerUpReceived = true;
      }

      this._universalAuthenticationListener.verifyRequiredServer();
      Proxy.getInstance().removeSystemListener(this);
      new PGPEncodingRegistration$NonBlockingStartupThread().start();
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }
}
