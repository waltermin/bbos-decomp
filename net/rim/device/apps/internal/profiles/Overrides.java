package net.rim.device.apps.internal.profiles;

import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.PINAddressModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.vm.Array;

public final class Overrides implements ReadableList, CollectionEventSource, CollectionListener, PersistentContentListener {
   private Vector _overrides;
   private IntHashtable _uidHashtable = new IntHashtable();
   private IntHashtable _overridesToValidate;
   private CollectionListenerManager _collectionListenerManager;
   private static final long OVERRIDES_ID = -6786592476105735731L;
   private static final long OVERRIDES_PERSISTENT_ID = -1745202328598837165L;
   static final int INVALID_EXISTING_ADDRESS_CARD_UID = 0;
   private static final int MIN_IDLE_TIME_SEC = 5;
   private static final int MIN_IDLE_TIME_MSEC = 1000;

   final Override createNewOverride(String name) {
      return this.createNewOverride(name, UIDGenerator.getUID());
   }

   final Override createNewOverride(String name, int uid) {
      return new Override(name, uid);
   }

   final void switchOrder(int ind1, int ind2) {
      synchronized (this._overrides) {
         int high = ind2;
         int low = ind1;
         if (ind2 < ind1) {
            high = ind1;
            low = ind2;
         }

         Override override1 = (Override)this._overrides.elementAt(low);
         Override override2 = (Override)this._overrides.elementAt(high);
         if (override1.isEnabled() && override2.isEnabled()) {
            FromContact[] fromContacts = override2.getFromContacts();
            int numFrom = fromContacts.length;

            for (int i = 0; i < numFrom; i++) {
               int addressCardUID = fromContacts[i]._addressCardUID;
               if (this._uidHashtable.get(addressCardUID) == override1) {
                  this._uidHashtable.put(addressCardUID, override2);
               }
            }
         }

         override1.ensureContentProtected();
         override2.ensureContentProtected();
         this._overrides.setElementAt(override2, low);
         this._overrides.setElementAt(override1, high);
         PersistentObject.commit(this._overrides);
      }
   }

   final void add(Override override, boolean fireNotification) {
      synchronized (this._overrides) {
         override.ensureContentProtected();
         this._overrides.addElement(override);
         PersistentObject.commit(this._overrides);
         FromContact[] fromContacts = override.getFromContacts();
         if (override.isEnabled()) {
            this.addToHashtable(this._overrides.size() - 1, override, fromContacts);
         }
      }

      if (fireNotification) {
         this._collectionListenerManager.fireElementAdded(this, override);
      }
   }

   final void updateHashtable(Override override, FromContact[] newFromContacts) {
      int indexOfOverride = this._overrides.indexOf(override);
      if (override.isEnabled()) {
         this.removeFromHashtable(indexOfOverride, override, newFromContacts);
         if (newFromContacts != null) {
            this.addToHashtable(indexOfOverride, override, newFromContacts);
         }
      }
   }

   final void statusToggled(Override override) {
      int overrideIndex = this._overrides.indexOf(override);
      if (override.isEnabled()) {
         this.addToHashtable(overrideIndex, override, override.getFromContacts());
      } else {
         this.removeFromHashtable(overrideIndex, override, null);
      }

      synchronized (this._overrides) {
         this.commitChanges(override, true);
      }
   }

   public final void setDefaultOverridesUsingTune(String tune) {
      int numOverrides = this._overrides.size();
      String[] defaultTuneNames = Profiles.getDefaultTuneNames(2868625504212929964L);
      String selectedTuneName = defaultTuneNames[0];
      if (selectedTuneName != null) {
         for (int i = numOverrides - 1; i >= 0; i--) {
            Override override = (Override)this._overrides.elementAt(i);
            String overrideTune = override.getTuneName();
            if (overrideTune != null && overrideTune.equals(tune)) {
               override.setTuneName(selectedTuneName);
            }
         }
      }
   }

   final FromContact[] getFromContactsInAddressBookData(Override override) {
      FromContact[] revisedFromContacts = null;
      if (override != null) {
         FromContact[] fromContacts = override.getFromContacts();
         if (fromContacts != null && fromContacts.length > 0) {
            revisedFromContacts = new FromContact[fromContacts.length];
            int numContacts = 0;
            ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
            Factory addressCardModelFactory = (Factory)ar.get(-3124646573404667739L);
            AddressCardModel acm = (AddressCardModel)addressCardModelFactory.createInstance(null);
            WritableSet addressBookCollection = (WritableSet)AddressBookServices.getAddressBookCollection();

            for (int count = 0; count < fromContacts.length; count++) {
               acm.setUID(fromContacts[count]._addressCardUID);
               if (addressBookCollection.contains(acm)) {
                  revisedFromContacts[numContacts] = fromContacts[count];
                  numContacts++;
               }
            }

            if (numContacts != revisedFromContacts.length) {
               Array.resize(revisedFromContacts, numContacts);
            }
         }
      }

      if (revisedFromContacts == null) {
         revisedFromContacts = new FromContact[0];
      }

      return revisedFromContacts;
   }

   public final String getCustomTune(int addresscardUID) {
      Override override = this.getCustomTuneOverride(addresscardUID);
      return override != null ? override.getTuneName() : null;
   }

   public final void setCustomTune(AddressCardModel ac, String customTune) {
      if (ac != null && customTune != null) {
         Override override = null;
         boolean tuneSet = false;
         this.removeAddressCardUIDFromOverrides(ac.getUID(), 0, true);
         int size = this._overrides.size();

         for (int i = 0; i < size; i++) {
            override = (Override)this._overrides.elementAt(i);
            if (override != null) {
               String tune = override.getTuneName();
               if (tune != null && override.isFromAddressBook() && tune.equalsIgnoreCase(customTune)) {
                  override.addFromContact(getFromContact(ac));
                  override.updateOverrideName();
                  this.addIndividualContactToHashTable(ac.getUID(), i, override);
                  tuneSet = true;
                  break;
               }
            }
         }

         if (!tuneSet) {
            FromContact fromContact = getFromContact(ac);
            ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
            override = this.createNewOverride(resources.getString(247) + fromContact.getName());
            override.setFromContacts(new FromContact[]{fromContact});
            override.setUseTune(true);
            override.setProfileUID(-1);
            override.setFromAddressBook(true);
            this.add(override, false);
            override.setTuneName(customTune);
         }

         if (!override.isEnabled()) {
            override.setEnabled(true);
            this.statusToggled(override);
         } else {
            synchronized (this._overrides) {
               this.commitChanges(override, true);
            }
         }
      }
   }

   public final void deleteCustomTune(int addresscardUID) {
      this.removeAddressCardUIDFromOverrides(addresscardUID, 0, false);
   }

   final Override getFirstOverrideContaining(int addresscardUID) {
      return (Override)this._uidHashtable.get(addresscardUID);
   }

   public final void validateFromNames(boolean deleteIfNotFound) {
      if (this._overridesToValidate != null) {
         int[] overrideKeys = new int[this._overridesToValidate.size()];
         this._overridesToValidate.keysToArray(overrideKeys);

         for (int index = 0; index < overrideKeys.length; index++) {
            Override override = (Override)this._overridesToValidate.get(overrideKeys[index]);
            if (override != null) {
               this.validateFromNamesInternal(override, deleteIfNotFound);
            }
         }

         this._overridesToValidate.clear();
         this._overridesToValidate = null;
      }
   }

   final void addToOverridesToValidate(Override override) {
      if (this._overridesToValidate == null) {
         this._overridesToValidate = new IntHashtable();
      }

      this._overridesToValidate.put(override.getUID(), override);
   }

   final void delete(Override override) {
      if (this._overridesToValidate != null) {
         this._overridesToValidate.remove(override.getUID());
      }

      synchronized (this._overrides) {
         int index = this._overrides.indexOf(override);
         this._overrides.removeElementAt(index);
         PersistentObject.commit(this._overrides);
         this.removeFromHashtable(index, override, null);
      }

      this._collectionListenerManager.fireElementRemoved(this, override);
   }

   final void setElementAt(Object object, int index) {
      synchronized (this._overrides) {
         Override override = (Override)object;
         override.ensureContentProtected();
         this.updateHashtable((Override)this.getAt(index), override.getFromContacts());
         this._overrides.setElementAt(override, index);
      }
   }

   final void removeAll() {
      synchronized (this._overrides) {
         this._overrides.removeAllElements();
         PersistentObject.commit(this._overrides);
         this._uidHashtable.clear();
      }

      this._collectionListenerManager.fireReset(this);
   }

   final void commitChanges(Override override, boolean fireNotification) {
      synchronized (this._overrides) {
         PersistentObject.commit(override);
      }

      if (fireNotification) {
         this._collectionListenerManager.fireElementUpdated(this, override, override);
      }
   }

   @Override
   public final int getIndex(Object element) {
      return this._overrides.indexOf(element);
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return ReadableListUtil.getAt(index, count, elements, destIndex, this);
   }

   @Override
   public final Object getAt(int index) {
      return this._overrides.elementAt(index);
   }

   @Override
   public final void addCollectionListener(Object listenerObject) {
      this._collectionListenerManager.addCollectionListener(listenerObject);
   }

   @Override
   public final void removeCollectionListener(Object listenerObject) {
      this._collectionListenerManager.removeCollectionListener(listenerObject);
   }

   @Override
   public final void reset(Collection collection) {
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (oldElement instanceof AddressCardModel && newElement instanceof AddressCardModel) {
         int oldUID = ((AddressCardModel)oldElement).getUID();
         AddressCardModel newAddressCard = (AddressCardModel)newElement;
         synchronized (this._overrides) {
            int numOverrides = this._overrides.size();

            for (int i = 0; i < numOverrides; i++) {
               Override override = (Override)this._overrides.elementAt(i);
               FromContact[] fromContacts = override.getFromContacts();
               boolean changed = false;
               int numids = fromContacts.length;

               for (int j = 0; j < numids; j++) {
                  FromContact oldFrom = fromContacts[j];
                  if (oldFrom._addressCardUID == oldUID) {
                     FromContact newFrom = getFromContact(newAddressCard);
                     if (!newFrom.equals(oldFrom)) {
                        fromContacts[j] = newFrom;
                        changed = true;
                     }
                  }
               }

               if (changed) {
                  this.commitChanges(override, true);
               }
            }

            int newUID = newAddressCard.getUID();
            if (newUID != oldUID) {
               Object firstOverride = this._uidHashtable.get(oldUID);
               this._uidHashtable.remove(oldUID);
               if (firstOverride != null) {
                  ((Override)firstOverride).ensureContentProtected();
                  this._uidHashtable.put(newUID, firstOverride);
               }
            }
         }
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (element instanceof AddressCardModel) {
         AddressCardModel ac = (AddressCardModel)element;
         int addresscardUID = ac.getUID();
         this.removeAddressCardUIDFromOverrides(addresscardUID, 0, false);
      }
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         ticket.hashCode();
      }

      synchronized (this._overrides) {
         int numOverrides = this._overrides.size();

         for (int i = numOverrides - 1; i >= 0; i--) {
            Override override = (Override)this._overrides.elementAt(i);
            override.ensureContentProtected();
         }

         this._uidHashtable.clear();
         this.loadUIDHashtable();
      }
   }

   @Override
   public final int size() {
      return this._overrides.size();
   }

   private final void loadUIDHashtable() {
      int numOverrides = this._overrides.size();

      for (int i = numOverrides - 1; i >= 0; i--) {
         Override override = (Override)this._overrides.elementAt(i);
         if (override.isEnabled()) {
            FromContact[] fromContacts = override.getFromContacts();
            int numFrom = fromContacts.length;

            for (int j = numFrom - 1; j >= 0; j--) {
               int addressCardUID = fromContacts[j]._addressCardUID;
               if (addressCardUID != 0) {
                  override.ensureContentProtected();
                  this._uidHashtable.put(fromContacts[j]._addressCardUID, override);
               }
            }
         }
      }
   }

   static final int validateFromNames(int uid, String name, String contactInfo, byte contactInfoType, boolean deleteIfNotFound) {
      Object obj = null;
      if (uid != 0) {
         obj = AddressBookServices.getAddressCard(uid);
      }

      if (name != null) {
         if (obj instanceof AddressCardModel && obj.toString().equals(name)) {
            return uid;
         }

         if (contactInfo != null) {
            ContextObject context = new ContextObject();
            context.put(253, contactInfo);
            PersistableRIMModel contactInfoModel = null;
            if (contactInfoType == 48) {
               contactInfoModel = (PersistableRIMModel)FactoryUtil.createInstance(4246852237058296601L, context);
            } else if (contactInfoType == 32) {
               contactInfoModel = (PersistableRIMModel)FactoryUtil.createInstance(-2985347935260258684L, context);
            } else if (contactInfoType == 16) {
               contactInfoModel = (PersistableRIMModel)FactoryUtil.createInstance(3797587162219887872L, context);
            }

            AddressCardModel ac = doLookup(contactInfoModel, name);
            if (ac != null) {
               return ac.getUID();
            }
         } else {
            ContextObject context = new ContextObject();
            context.put(253, name);
            PersistableRIMModel friendlyNameModel = (PersistableRIMModel)FactoryUtil.createInstance(5149066071290992769L, context);
            AddressCardModel ac = doLookup(friendlyNameModel, name);
            if (ac != null) {
               return ac.getUID();
            }

            friendlyNameModel = (PersistableRIMModel)FactoryUtil.createInstance(-2467076596918202204L, context);
            ac = doLookup(friendlyNameModel, name);
            if (ac != null) {
               return ac.getUID();
            }
         }
      }

      return uid != 0 && !(obj instanceof AddressCardModel) && !deleteIfNotFound ? uid : 0;
   }

   private static final AddressCardModel doLookup(PersistableRIMModel lookupModel, String name) {
      Object[] entries = AddressBookServices.reverseLookup(lookupModel, null);
      if (name != null && entries != null && entries.length > 0) {
         for (int i = 0; i < entries.length; i++) {
            Object entry = entries[i];
            if (entry instanceof AddressCardModel) {
               AddressCardModel ac = (AddressCardModel)entry;
               if (name.equals(ac.toString())) {
                  return ac;
               }
            }
         }
      }

      return null;
   }

   static final FromContact getFromContact(AddressCardModel ac) {
      int uid = ac.getUID();
      String name = ac.toString();
      int acSize = ac.size();
      String contactInfo = null;
      byte contactInfoType = 0;

      for (int i = 0; i < acSize; i++) {
         Object element = ac.getAt(i);
         if (element instanceof PINAddressModel) {
            contactInfo = ((PINAddressModel)element).toString();
            contactInfoType = 48;
            break;
         }

         if (element instanceof EmailAddressModel) {
            if (contactInfoType < 32) {
               contactInfo = ((EmailAddressModel)element).toString();
               contactInfoType = 32;
            }
         } else if (element instanceof PhoneNumberModel && contactInfoType < 16) {
            contactInfo = ((PhoneNumberModel)element).toString();
            contactInfoType = 16;
         }
      }

      return new FromContact(uid, name, contactInfo, contactInfoType);
   }

   private final void addToHashtable(int overrideIndex, Override override, FromContact[] fromContacts) {
      for (FromContact fromContact : fromContacts) {
         int addressCardUID = fromContact._addressCardUID;
         this.addIndividualContactToHashTable(addressCardUID, overrideIndex, override);
      }
   }

   public static final Overrides getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      Overrides overrides = (Overrides)registry.getOrWaitFor(-6786592476105735731L);
      if (overrides == null) {
         overrides = new Overrides();
         PersistentContent.addListener(overrides);
         registry.put(-6786592476105735731L, overrides);
      }

      return overrides;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean validateFromNamesInternal(Override override, boolean deleteIfNotFound) {
      boolean overrideRemoved = false;

      try {
         FromContact[] fromContacts = override.getFromContacts();

         for (int i = 0; i < fromContacts.length; i++) {
            int currentUID = fromContacts[i]._addressCardUID;
            int newUID = validateFromNames(
               currentUID, fromContacts[i].getName(), fromContacts[i].getContactInfo(), fromContacts[i]._contactInfoType, deleteIfNotFound
            );
            if (newUID != currentUID) {
               if (newUID == 0) {
                  if (deleteIfNotFound) {
                     if (!this._overrides.contains(override)) {
                        overrideRemoved = true;
                     } else {
                        overrideRemoved = this.removeAddressCardUIDFromOverrides(currentUID, 0, false);
                     }

                     i--;
                     fromContacts = override.getFromContacts();
                  } else {
                     fromContacts[i]._addressCardUID = newUID;
                  }

                  this._uidHashtable.remove(currentUID);
               } else {
                  Object firstOverride = this._uidHashtable.get(currentUID);
                  this._uidHashtable.remove(currentUID);
                  if (firstOverride != null) {
                     ((Override)firstOverride).ensureContentProtected();
                     this._uidHashtable.put(newUID, firstOverride);
                  }

                  fromContacts[i]._addressCardUID = newUID;
               }

               this.commitChanges(override, false);
            }

            if (i == -1 && fromContacts.length == 1) {
               break;
            }
         }
      } catch (Throwable var10) {
         System.out.println("Overrides: validateFromNamesInternal " + e.toString());
         return overrideRemoved;
      }

      return overrideRemoved;
   }

   private final void addIndividualContactToHashTable(int addressCardUID, int overrideIndex, Override override) {
      if (addressCardUID != 0) {
         Override oldOverride = (Override)this._uidHashtable.get(addressCardUID);
         if (oldOverride == null || this._overrides.indexOf(oldOverride) > overrideIndex) {
            override.ensureContentProtected();
            this._uidHashtable.put(addressCardUID, override);
         }
      }
   }

   private final boolean removeAddressCardUIDFromOverrides(int addresscardUID, int startAtOverrideIndex, boolean addressBookOverridesOnly) {
      boolean overrideRemoved = false;
      synchronized (this._overrides) {
         try {
            int numOverrides = this._overrides.size();

            for (int i = startAtOverrideIndex; i < numOverrides; i++) {
               Override override = null;
               override = (Override)this._overrides.elementAt(i);
               if (override != null
                  && override.getFromContacts() != null
                  && override.getFromContacts().length > 0
                  && (override.isFromAddressBook() || !addressBookOverridesOnly)) {
                  int index = override.removeFromContact(addresscardUID, 0);
                  if (index != -1) {
                     Override mappedOverride = (Override)this._uidHashtable.get(addresscardUID);
                     if (mappedOverride != null && mappedOverride.getUID() == override.getUID()) {
                        this._uidHashtable.remove(addresscardUID);
                     }

                     if (override.isFromAddressBook()) {
                        if (override.getFromContacts() != null && override.getFromContacts().length != 0) {
                           override.updateOverrideName();
                           this.commitChanges(override, true);
                        } else {
                           override.setFromContacts(new FromContact[0]);
                           this.delete(override);
                           overrideRemoved = true;
                           i--;
                           numOverrides--;
                        }
                     } else {
                        this.commitChanges(override, true);
                     }
                  }
               }
            }
         } finally {
            ;
         }

         return overrideRemoved;
      }
   }

   private final Override getCustomTuneOverride(int addresscardUID) {
      Override override = null;
      boolean foundOverride = false;
      if (this._overrides != null) {
         int size = this._overrides.size();

         for (int i = 0; i < size; i++) {
            override = (Override)this._overrides.elementAt(i);
            if (override != null && override.isFromAddressBook()) {
               FromContact[] fromContacts = override.getFromContacts();
               if (fromContacts != null && fromContacts.length > 0) {
                  for (int count = 0; count < fromContacts.length; count++) {
                     if (addresscardUID == fromContacts[count]._addressCardUID) {
                        foundOverride = true;
                        break;
                     }
                  }
               }
            }

            if (foundOverride) {
               break;
            }
         }
      }

      if (!foundOverride) {
         override = null;
      }

      return override;
   }

   private final void removeFromHashtable(int overrideIndex, Override override, FromContact[] ignoreFromContacts) {
      FromContact[] fromContacts = override.getFromContacts();
      int numFrom = fromContacts.length;

      for (int i = 0; i < numFrom; i++) {
         int addressCardUID = fromContacts[i]._addressCardUID;
         if (addressCardUID != 0 && this._uidHashtable.get(addressCardUID) == override && !this.fromContactsContains(ignoreFromContacts, addressCardUID)) {
            int nextIndex = this.indexOfEnabledOverrideContaining(addressCardUID, overrideIndex + 1);
            if (nextIndex != -1) {
               Override nextOverride = (Override)this._overrides.elementAt(nextIndex);
               nextOverride.ensureContentProtected();
               this._uidHashtable.put(addressCardUID, nextOverride);
            } else {
               this._uidHashtable.remove(addressCardUID);
            }
         }
      }
   }

   private final boolean fromContactsContains(FromContact[] fromContacts, int addressCardUID) {
      if (fromContacts != null) {
         int numFrom = fromContacts.length;

         for (int i = 0; i < numFrom; i++) {
            if (fromContacts[i]._addressCardUID == addressCardUID) {
               return true;
            }
         }
      }

      return false;
   }

   private final int indexOfEnabledOverrideContaining(int addressCardUID, int startIndex) {
      int numOverrides = this._overrides.size();
      if (startIndex >= numOverrides) {
         return -1;
      }

      for (int i = startIndex; i < numOverrides; i++) {
         Override override = (Override)this._overrides.elementAt(i);
         if (this.fromContactsContains(override.getFromContacts(), addressCardUID) && override.isEnabled()) {
            return i;
         }
      }

      return -1;
   }

   private Overrides() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-1745202328598837165L);
      synchronized (persistentObject) {
         if ((this._overrides = (Vector)persistentObject.getContents()) == null) {
            this._overrides = new Vector();
            ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
            this.add(this.createNewOverride(resources.getString(246), 7), false);
            persistentObject.setContents(this._overrides, 51);
            persistentObject.commit();
         } else {
            this.loadUIDHashtable();
         }
      }

      this._collectionListenerManager = new CollectionListenerManager();
   }
}
