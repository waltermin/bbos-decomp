package net.rim.device.api.crypto.keystore;

import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.CloneableVector;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.internal.commonmodels.title.TitleModel;

public final class KeyStoreAddressInjector implements CollectionListener, SyncEventListener {
   Factory _addressCardFactory;
   Factory _personNameFactory;
   Factory _companyInfoFactory;
   Factory _emailAddressFactory;
   Factory _phoneNumberFactory;
   Factory _addressFactory;
   Factory _titleModelFactory;
   AddressBook _theAddressBook;
   private boolean _syncInProgress;
   private Vector _itemsToProcess;
   private static final long ADDRESSINJECTOR = 3765149499855602226L;
   private static final long ADDRESSINJECTOR_LOCK = 4442458046397864247L;

   public static final KeyStoreAddressInjector getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar.getObject(4442458046397864247L)) {
         KeyStoreAddressInjector injector = (KeyStoreAddressInjector)ar.get(3765149499855602226L);
         if (injector == null) {
            injector = new KeyStoreAddressInjector();
            ar.put(3765149499855602226L, injector);
         }

         return injector;
      }
   }

   public final void addKeyStore(KeyStore keyStore) {
      if (keyStore == null) {
         throw new IllegalArgumentException();
      }

      keyStore.addCollectionListener(this);
   }

   private KeyStoreAddressInjector() {
      SyncManager.getInstance().addSyncEventListener(this);
      this._syncInProgress = false;
   }

   @Override
   public final synchronized void elementAdded(Collection collection, Object element) {
      KeyStoreData data = (KeyStoreData)element;
      if (!data.isPrivateKeySet()) {
         if (this._syncInProgress) {
            if (this._itemsToProcess == null) {
               this._itemsToProcess = new Vector();
            }

            this._itemsToProcess.addElement(data);
         } else {
            this.processCertificate(data);
         }
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
   }

   @Override
   public final void reset(Collection collection) {
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
   }

   private final synchronized void processCertificate(KeyStoreData data) {
      if (KeyStoreOptions.getKeyStoreAddressInjectorEnabled()) {
         Certificate certificate = data.getCertificate();
         if (certificate != null) {
            this.fillAddressData(certificate);
         }
      }
   }

   private final synchronized void fillAddressData(Certificate certificate) {
      Boolean isEndEntity = (Boolean)certificate.getInformation(-7341435958452683242L, null, null);
      if (isEndEntity != null && isEndEntity) {
         DistinguishedName distinguishedName = certificate.getSubject();
         if (distinguishedName != null) {
            String[] emails = (String[])certificate.getInformation(-7850001002262082664L, null, null);
            if (emails == null) {
               return;
            }

            for (int i = emails.length - 1; i >= 0; i--) {
               emails[i] = this.copy(emails[i]);
            }

            String firstName = null;
            String lastName = null;
            String phoneNumber = null;
            String company = null;
            String commonName = distinguishedName.getCommonName();
            String givenName = distinguishedName.getString(OIDs.getOID(-1250500949));
            String initial = distinguishedName.getString(OIDs.getOID(-1250435413));
            String title = distinguishedName.getString(OIDs.getOID(-1252467029));
            String organizationalUnit = distinguishedName.getOrganizationalUnit();
            String organizationName = distinguishedName.getOrganization();
            String stateOrProvince = distinguishedName.getStateOrProvince();
            String country = distinguishedName.getCountry();
            String city = distinguishedName.getLocality();
            if (givenName != null) {
               firstName = givenName;
               if (initial != null) {
                  firstName = firstName + " " + initial;
               }
            } else if (commonName != null) {
               firstName = commonName;
            }

            if (givenName != null) {
               lastName = givenName;
            } else if (commonName != null && !firstName.equals(commonName)) {
               lastName = commonName;
            }

            if (organizationName != null) {
               company = organizationName;
            } else if (organizationalUnit != null) {
               company = organizationalUnit;
            }

            this.addAddressEntry(
               this.copy(firstName),
               this.copy(lastName),
               emails,
               this.copy(phoneNumber),
               this.copy(company),
               this.copy(title),
               this.copy(country),
               this.copy(stateOrProvince),
               this.copy(city)
            );
         }
      }
   }

   private final String copy(String s) {
      return s == null ? null : s;
   }

   private final synchronized void addAddressEntry(
      String firstName, String lastName, String[] emails, String phoneNumber, String company, String title, String country, String stateOrProvince, String city
   ) {
      if (this._addressCardFactory == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         this._addressCardFactory = (Factory)ar.waitFor(-3124646573404667739L);
         this._personNameFactory = (Factory)ar.waitFor(5149066071290992769L);
         this._companyInfoFactory = (Factory)ar.waitFor(-2467076596918202204L);
         this._titleModelFactory = (Factory)ar.waitFor(-4904857078378172834L);
         this._emailAddressFactory = (Factory)ar.waitFor(-2985347935260258684L);
         this._phoneNumberFactory = (Factory)ar.waitFor(8414046446004092553L);
         this._addressFactory = (Factory)ar.waitFor(-7593463283570535867L);
         this._theAddressBook = AddressBookServices.getAddressBook();
      }

      WritableSet acm = (WritableSet)this._addressCardFactory.createInstance(null);
      int numEmailAddresses = emails.length;

      for (int i = 0; i < numEmailAddresses; i++) {
         ContextObject emailContext = new ContextObject();
         emailContext.put(253, emails[i]);
         Object eam = this._emailAddressFactory.createInstance(emailContext);
         acm.add(eam);
         Object obj = AddressBookServices.reverseLookup(eam);
         if (obj != null) {
            return;
         }
      }

      if (firstName != null || lastName != null) {
         Object pnm = this._personNameFactory.createInstance(null);
         if (pnm instanceof PersonNameModel) {
            PersonNameModel personModel = (PersonNameModel)pnm;
            personModel.setFirstName(firstName);
            personModel.setLastName(lastName);
            Object[] objArray = AddressBookServices.lookup(pnm, 1);
            if (objArray != null) {
               return;
            }

            acm.add(pnm);
         }
      }

      if (company != null) {
         Object cim = this._companyInfoFactory.createInstance(null);
         if (cim instanceof CompanyInfoModel) {
            CompanyInfoModel companyModel = (CompanyInfoModel)cim;
            companyModel.setCompanyName(company);
            acm.add(cim);
         }
      }

      if (title != null) {
         TitleModel titleModel = (TitleModel)this._titleModelFactory.createInstance(null);
         titleModel.setTitle(title);
         acm.add(titleModel);
      }

      if (stateOrProvince != null || country != null || city != null) {
         Object mam = this._addressFactory.createInstance(null);
         if (mam instanceof MailingAddressModel) {
            MailingAddressModel addressModel = (MailingAddressModel)mam;
            addressModel.setArea(stateOrProvince);
            addressModel.setCity(city);
            addressModel.setCountry(country);
            acm.add(mam);
         }
      }

      if (this._phoneNumberFactory != null && phoneNumber != null) {
         ContextObject phoneContext = new ContextObject();
         phoneContext.put(253, phoneNumber);
         acm.add(this._phoneNumberFactory.createInstance(phoneContext));
      }

      try {
         if (acm instanceof EditableProvider) {
            EditableProvider ep = (EditableProvider)acm;
            Object readOnlyObject = ep.makeReadOnly();
            this._theAddressBook.addAddressCard(readOnlyObject);
            return;
         }
      } finally {
         return;
      }
   }

   @Override
   public final void syncEventOccurred(int eventId, Object object) {
      switch (eventId) {
         case 1:
         default:
            this._syncInProgress = true;
            return;
         case 2:
            this._syncInProgress = false;
            if (this._itemsToProcess != null) {
               Vector clone = CloneableVector.clone(this._itemsToProcess);
               this._itemsToProcess = null;
               new KeyStoreAddressInjector$WorkerThread(this, clone).start();
            }
         case 0:
      }
   }
}
