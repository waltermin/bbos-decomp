package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.system.SystemListener;
import net.rim.device.internal.proxy.Proxy;

final class SMIMEEncodingRegistration implements SystemListener {
   private boolean _powerUpReceived;

   static final void registerSMIMEEncoding() {
      new SMIMEEncodingRegistration().doBlockingStartupWork();
   }

   private SMIMEEncodingRegistration() {
   }

   private final void doBlockingStartupWork() {
      SMIMEFactory smimeFactory = SMIMEFactory.getInstance();
      smimeFactory.performRegistration();
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

      Proxy.getInstance().removeSystemListener(this);
      new SMIMEEncodingRegistration$NonBlockingStartupThread().start();
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
