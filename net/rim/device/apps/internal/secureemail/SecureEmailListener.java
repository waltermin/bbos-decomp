package net.rim.device.apps.internal.secureemail;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.crypto.keystore.CertificateStatusManager;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.MultiMap;
import net.rim.device.apps.api.framework.registration.ModelViewListener;
import net.rim.device.apps.api.framework.registration.OpenViewer;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.secureemail.server.SecureEmailPolicyServer;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerManager;
import net.rim.device.internal.proxy.Proxy;

public class SecureEmailListener implements MemoryCleanerListener, ModelViewListener, CollectionListener {
   private MultiMap _messageClosedListeners = (MultiMap)(new Object());
   private MultiMap _collectionListeners = (MultiMap)(new Object());
   private Hashtable _openMessages = (Hashtable)(new Object());
   private static final long ID_LOCK;
   private static final long ID;

   private SecureEmailListener() {
      CertificateStatusManager.getInstance().addCollectionListener(this);
   }

   public static SecureEmailListener getInstance() {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      synchronized (appRegistry.getObject(-7546370436645685145L)) {
         SecureEmailListener secureEmailListener = (SecureEmailListener)appRegistry.get(-7678865984852433251L);
         if (secureEmailListener == null) {
            secureEmailListener = new SecureEmailListener();
            appRegistry.put(-7678865984852433251L, secureEmailListener);
         }

         return secureEmailListener;
      }
   }

   public void addKeyStore(KeyStore keyStore) {
      keyStore.addCollectionListener(this);
   }

   public void registerMessageClosedListener(EmailMessageModel message, MessageClosedListener listener) {
      this._messageClosedListeners.add(message, listener);
   }

   public void registerCollectionListener(EmailMessageModel message, CollectionListener listener) {
      this._collectionListeners.add(message, listener);
   }

   public void deregisterCollectionListener(EmailMessageModel message, CollectionListener listener) {
      this._collectionListeners.removeValue(message, listener);
   }

   public boolean isMessageOpen(Object message) {
      if (message instanceof Object) {
         return this._openMessages.containsKey(message);
      }

      if (!(message instanceof Object)) {
         return false;
      }

      BodyModel bodyModel = (BodyModel)message;
      synchronized (this._openMessages) {
         Enumeration enumeration = this._openMessages.keys();

         while (enumeration.hasMoreElements()) {
            EmailMessageModel currentMessageModel = (EmailMessageModel)enumeration.nextElement();
            if (SecureEmailUtilities.messageContainsBody(currentMessageModel, bodyModel)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean cleanNow(int event) {
      SecureEmailCache secureEmailCache = SecureEmailCache.getInstance();
      boolean cleaned = secureEmailCache.cleanMessageCache();
      cleaned |= secureEmailCache.cleanMessageStatusCache();
      cleaned |= secureEmailCache.cleanProcessingContextCache();
      if (event == 5) {
         cleaned |= secureEmailCache.cleanAutoFetchInformation();
      }

      return cleaned;
   }

   @Override
   public String getDescription() {
      return SecureEmailResources.getString(27);
   }

   @Override
   public void modelOpened(OpenViewer openViewer, Object context) {
      EmailMessageModel messageModel = (EmailMessageModel)openViewer.getOpenedModel(context);
      this._openMessages.put(messageModel, messageModel);
      if (messageModel.getStatus() == Integer.MAX_VALUE) {
         SecureEmailPolicyServer[] secureEmailPolicyServers = SecureEmailServerManager.getInstance().getPolicyServers();
         int numSecureEmailPolicyServers = secureEmailPolicyServers.length;
         if (numSecureEmailPolicyServers > 0) {
            SecureEmailPolicyServer[] serversRequiringPolicyUpdate = null;

            for (int i = 0; i < numSecureEmailPolicyServers; i++) {
               if (secureEmailPolicyServers[i].isPolicyUpdateRequired()) {
                  if (serversRequiringPolicyUpdate == null) {
                     serversRequiringPolicyUpdate = new SecureEmailPolicyServer[0];
                  }

                  Arrays.add(serversRequiringPolicyUpdate, secureEmailPolicyServers[i]);
               }
            }

            if (serversRequiringPolicyUpdate != null && serversRequiringPolicyUpdate.length > 0) {
               Proxy.getInstance().startThread(new SecureEmailListener$UpdateSecureEmailPolicyThread(serversRequiringPolicyUpdate));
            }
         }
      }
   }

   @Override
   public void modelClosed(OpenViewer openViewer, Object context) {
      EmailMessageModel messageModel = (EmailMessageModel)openViewer.getOpenedModel(context);
      this._openMessages.remove(messageModel);
      SecureEmailCache.getInstance().removeCachedOpenedMessage(messageModel);
      Enumeration messageClosedListeners = this._messageClosedListeners.elements(messageModel);

      while (messageClosedListeners.hasMoreElements()) {
         MessageClosedListener listener = (MessageClosedListener)messageClosedListeners.nextElement();
         listener.messageClosed(messageModel);
      }

      this._messageClosedListeners.removeKey(messageModel);
      this._collectionListeners.removeKey(messageModel);
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      Enumeration enumeration = this._collectionListeners.elements();

      while (enumeration.hasMoreElements()) {
         CollectionListener collectionListener = (CollectionListener)enumeration.nextElement();
         collectionListener.elementAdded(collection, element);
      }
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      Enumeration enumeration = this._collectionListeners.elements();

      while (enumeration.hasMoreElements()) {
         CollectionListener collectionListener = (CollectionListener)enumeration.nextElement();
         collectionListener.elementRemoved(collection, element);
      }
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      Enumeration enumeration = this._collectionListeners.elements();

      while (enumeration.hasMoreElements()) {
         CollectionListener collectionListener = (CollectionListener)enumeration.nextElement();
         collectionListener.elementUpdated(collection, oldElement, newElement);
      }
   }

   @Override
   public void reset(Collection collection) {
      Enumeration enumeration = this._collectionListeners.elements();

      while (enumeration.hasMoreElements()) {
         CollectionListener collectionListener = (CollectionListener)enumeration.nextElement();
         collectionListener.reset(collection);
      }
   }
}
