package net.rim.device.internal.system;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.internal.crypto.WipeablePolicyCryptoBlock;
import net.rim.device.internal.proxy.Proxy;

class ITPolicyInternal$WipeablePolicyWriter extends Thread implements GlobalEventListener {
   private Object _data;
   private boolean _completionEventPosted = false;
   private boolean _writingComplete = false;
   private boolean _isThisThreadTheWriter = false;
   private static final long ENCODING_ID = -4882009274413714329L;

   public ITPolicyInternal$WipeablePolicyWriter(byte[] data) {
      this._data = data;
      Proxy.getInstance().addGlobalEventListener(this);
   }

   @Override
   public void run() {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      ITPolicyInternal$WipeablePolicyWriter$EncodingContainer encodingContainer = (ITPolicyInternal$WipeablePolicyWriter$EncodingContainer)appRegistry.getOrWaitFor(
         -4882009274413714329L
      );
      if (encodingContainer == null) {
         this._isThisThreadTheWriter = true;
         encodingContainer = new ITPolicyInternal$WipeablePolicyWriter$EncodingContainer();
      }

      boolean wipeableCryptoBlockKeyAvailable = true;
      boolean encodeData = false;
      synchronized (encodingContainer) {
         wipeableCryptoBlockKeyAvailable = WipeablePolicyCryptoBlock.keyAvailable();
         if (!wipeableCryptoBlockKeyAvailable) {
            encodeData = true;
         }

         if (this._isThisThreadTheWriter) {
            appRegistry.put(-4882009274413714329L, encodingContainer);
         }

         if (encodeData) {
            this._data = PersistentContent.encode((byte[])this._data);
         }

         encodingContainer.setEncoding(this._data);
         this._data = null;
         if (!this._isThisThreadTheWriter) {
            Proxy.getInstance().removeGlobalEventListener(this);
            return;
         }
      }

      Object ticket = null;
      if (!wipeableCryptoBlockKeyAvailable) {
         ticket = PersistentContent.waitForTicket();
      }

      synchronized (encodingContainer) {
         this._data = encodingContainer.getEncoding();
         if (encodeData) {
            this._data = PersistentContent.decodeByteArray(this._data);
         }

         NvStore.writeData(42, WipeablePolicyCryptoBlock.encrypt((byte[])this._data));
         this._data = null;
         ticket = null;
         appRegistry.remove(-4882009274413714329L);
      }

      synchronized (this) {
         if (this._completionEventPosted) {
            Proxy.getInstance().removeGlobalEventListener(this);
            RIMGlobalMessagePoster.postGlobalEvent(-467871197336216166L);
         }

         this._writingComplete = true;
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         synchronized (this) {
            if (this._isThisThreadTheWriter && this._writingComplete) {
               Proxy.getInstance().removeGlobalEventListener(this);
               RIMGlobalMessagePoster.postGlobalEvent(-467871197336216166L);
            }

            this._completionEventPosted = true;
         }
      }
   }
}
