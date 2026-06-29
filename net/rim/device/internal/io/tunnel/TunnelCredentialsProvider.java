package net.rim.device.internal.io.tunnel;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.internal.proxy.Proxy;

public final class TunnelCredentialsProvider implements GlobalEventListener {
   private TunnelCredentials _tunnelCredentials;
   private PersistentObject _persistentObject = RIMPersistentStore.getPersistentObject(1004147189966295995L);
   private static final long GUID;

   public static final TunnelCredentialsProvider getInstance() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      TunnelCredentialsProvider tunnelCredentialsProvider = (TunnelCredentialsProvider)applicationRegistry.getOrWaitFor(1004147189966295995L);
      if (tunnelCredentialsProvider == null) {
         tunnelCredentialsProvider = new TunnelCredentialsProvider();
         applicationRegistry.put(1004147189966295995L, tunnelCredentialsProvider);
      }

      return tunnelCredentialsProvider;
   }

   private TunnelCredentialsProvider() {
      this._tunnelCredentials = (TunnelCredentials)this._persistentObject.getContents();
      if (this._tunnelCredentials == null) {
         this._tunnelCredentials = new TunnelCredentials(true, true, true);
         this._persistentObject.setContents(this._tunnelCredentials, 51);
         this._persistentObject.commit();
      }

      Proxy.getInstance().addGlobalEventListener(this);
   }

   public final String getApn() {
      return this._tunnelCredentials.apn;
   }

   public final String getApnUsername() {
      return this._tunnelCredentials.apnUsername;
   }

   public final String getApnPassword() {
      return this._tunnelCredentials.apnPassword;
   }

   public final boolean isEditingOptionsAllowed() {
      return this._tunnelCredentials.editingOptionsAllowed;
   }

   public final boolean isOutgoingSocketsAllowed() {
      return this._tunnelCredentials.outgoingSocketsAllowed;
   }

   public final boolean isIncomingSocketsAllowed() {
      return this._tunnelCredentials.incomingSocketsAllowed;
   }

   public final synchronized void setApn(String apn) {
      this._tunnelCredentials.apn = apn;
      this._persistentObject.commit();
   }

   public final synchronized void setApnUsername(String apnUsername) {
      this._tunnelCredentials.apnUsername = apnUsername;
      this._persistentObject.commit();
   }

   public final synchronized void setApnPassword(String apnPassword) {
      this._tunnelCredentials.apnPassword = apnPassword;
      this._persistentObject.commit();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         if (!ITPolicy.getBoolean(32, 2, false)) {
            synchronized (this) {
               this._tunnelCredentials.loadValuesFromITPolicy();
               this._persistentObject.commit();
            }
         } else {
            TunnelCredentials tunnelCredentials = new TunnelCredentials(true, true, false);
            synchronized (this) {
               this._tunnelCredentials = tunnelCredentials;
               this._persistentObject.setContents(this._tunnelCredentials, 51);
               this._persistentObject.commit();
            }
         }
      }
   }
}
