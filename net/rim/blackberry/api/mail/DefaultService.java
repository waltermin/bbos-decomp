package net.rim.blackberry.api.mail;

import net.rim.blackberry.api.mail.event.FolderListener;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.MergedCollection;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.vm.Array;

final class DefaultService extends Store {
   private Transport _transport;
   private ServiceRecord _serviceRecord;
   private static final long DEFAULT_SERVICE_ID;
   private static final long DEFAULT_SERVICE_ID_BASE;
   static long EVENT_LOGGER_GUID = 3213487877773466990L;
   static final String EVENT_LOGGER_NAME;

   public static final DefaultService getInstance(ServiceConfiguration sc) {
      MergedCollection c = (MergedCollection)FolderMerge.getMergeCollection(-5581791943352753293L);
      c.size();
      DefaultService s = null;
      synchronized (FolderHierarchies.getLockObject()) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         if (sc == null) {
            synchronized (ar) {
               s = (DefaultService)ar.get(3213487877773466990L);
            }

            if (s == null) {
               s = new DefaultService();
               synchronized (ar) {
                  ar.put(3213487877773466990L, s);
               }
            }
         } else {
            long srGUID = 7344728658407325696L + sc.hashCode();
            synchronized (ar) {
               s = (DefaultService)ar.get(srGUID);
            }

            if (s == null) {
               s = new DefaultService(sc.getServiceRecord());
               synchronized (ar) {
                  ar.put(srGUID, s);
               }
            }
         }

         return s;
      }
   }

   public static final DefaultService getInstance() {
      return getInstance(null);
   }

   public static final DefaultService waitForDefaultService() {
      RIMMessagingService s = (RIMMessagingService)TransmissionServiceManager.get(8399767144006445082L);
      if (null == s.getOutgoingServiceRecord()) {
         new DefaultService$Helper(s).waitForService();
      }

      try {
         return getInstance();
      } catch (NoSuchServiceException e) {
         throw new Object("Default service does not exist!");
      }
   }

   private DefaultService() {
      this(null);
   }

   private DefaultService(ServiceRecord sr) {
      if (sr == null) {
         RIMMessagingService rms = (RIMMessagingService)TransmissionServiceManager.get(8399767144006445082L);
         if (null == rms) {
            throw new NoSuchServiceException("Default service does not exist!");
         }

         sr = rms.getOutgoingServiceRecord();
      }

      this._serviceRecord = sr;
      if (sr != null) {
         EmailHierarchy emailHierarchy = EmailHierarchy.getEmailHierarchy(sr, true);
         if (emailHierarchy == null) {
            throw new NoSuchServiceException("Failure finding email hierarchy for service.");
         }

         emailHierarchy.setFriendlyName(sr.getName());
         super._emailHierarchy = emailHierarchy;
         this._transport = new DefaultService$DefaultTransport(this);
         EventLogger.register(EVENT_LOGGER_GUID, "Mail Api", 2);
         this.setConnected(true);
         this._transport.setConnected(true);
      } else {
         throw new NoSuchServiceException("Default service does not exist!");
      }
   }

   @Override
   public final ServiceConfiguration getServiceConfiguration() {
      ServiceRecord sr = this.getServiceRecord();
      return sr != null ? new ServiceConfiguration(sr) : null;
   }

   final ServiceRecord getServiceRecord() {
      return this._serviceRecord;
   }

   public final Store getStore() {
      return this;
   }

   public final Transport getTransport() {
      return this._transport;
   }

   @Override
   public final String toString() {
      return this._serviceRecord != null ? this._serviceRecord.getName() : "no service book";
   }

   @Override
   public final Folder[] list() {
      Folder[] list = super.list();
      Folder[] listAnonymous = super.list(EmailHierarchy.getAnonymousEmailHierarchy());
      int startOffset = list.length;
      Array.resize(list, list.length + listAnonymous.length);

      for (int i = startOffset; i < list.length; i++) {
         list[i] = listAnonymous[i - startOffset];
      }

      return list;
   }

   @Override
   public final void addFolderListener(FolderListener l) {
      super.addFolderListener(l);
      ListenerManager lm = ListenerManager.getInstance();
      EmailHierarchy eh = EmailHierarchy.getAnonymousEmailHierarchy();
      lm.addStoreFolderListener(eh.getServiceUidHash(), l);
   }

   @Override
   public final void removeFolderListener(FolderListener l) {
      super.removeFolderListener(l);
      ListenerManager lm = ListenerManager.getInstance();
      EmailHierarchy eh = EmailHierarchy.getAnonymousEmailHierarchy();
      lm.removeStoreFolderListener(eh.getServiceUidHash(), l);
   }
}
