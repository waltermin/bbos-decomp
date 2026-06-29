package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.vm.Array;

public class AddressGuesser {
   private PersistentObject _persistentObject = null;
   private CircularBuffer _buffer = null;
   private int _currentEntryLength;
   private EmailMessageModel _message = null;
   private boolean _loading = false;
   private EmailMessageModel[] _sentMessages = null;
   private static final int BUFFER_LENGTH = 1024;
   private static final int ILLEGAL_VALUE = 0;
   private static final int BUFFER_MARKER = 0;
   private static final long GUESSER_KEY = -3272901161760843350L;
   private static AddressGuesser _theInstance = new AddressGuesser();

   public static AddressGuesser getInstance() {
      return _theInstance;
   }

   public AddressGuesser() {
      this._persistentObject = RIMPersistentStore.getPersistentObject(-3272901161760843350L);
      this._buffer = (CircularBuffer)this._persistentObject.getContents();
      if (this._buffer == null) {
         this.buildLearningDatabase();
      }
   }

   public void addSentAddresses(EmailMessageModel message) {
      if (!this._loading) {
         int[] refs = this.getAddressReferences(message);
         if (refs.length > 1) {
            this._buffer.addList(refs);
            this._persistentObject.commit();
         }
      }
   }

   public void setEmailMessage(EmailMessageModel message) {
      this._message = message;
   }

   public AddressCardModel guessAddress(EmailMessageModel message, KeywordFilterList filter) {
      this.setEmailMessage(message);
      return this.guessAddress(filter);
   }

   public AddressCardModel guessAddress(KeywordFilterList filter) {
      if (this._loading) {
         return null;
      }

      if (this._message == null) {
         return null;
      }

      int[] refs = this.getAddressReferences(this._message);

      for (this._currentEntryLength = this._buffer.firstEntry(); this._buffer.hasNext(); this._currentEntryLength = this._buffer.nextEntry()) {
         if (this.isSubsetOfCurrentEntry(refs)) {
            int[] possibles = this.subtractFromCurrentEntry(refs);
            possibles = this.filterMatch(filter, possibles);
            if (possibles.length > 0) {
               return this.getAddress(this.tieBreaker(possibles));
            }
         }
      }

      return null;
   }

   public void buildLearningDatabase() {
      Thread loaderThread = new AddressGuesser$1(this);
      loaderThread.start();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private synchronized void buildLearningDatabase0() {
      this._loading = true;
      this._buffer = new CircularBuffer(1024, 0);
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         this.collectSentMessages();
         this.loadSentMessages();
         var3 = false;
      } finally {
         if (var3) {
            this._persistentObject.setContents(this._buffer, 51);
            this._persistentObject.commit();
            this._loading = false;
         }
      }

      this._persistentObject.setContents(this._buffer, 51);
      this._persistentObject.commit();
      this._loading = false;
   }

   private void collectSentMessages() {
      this._sentMessages = new EmailMessageModel[0];
      int hierarchyCount = EmailHierarchy.getEmailHierarchyCount();

      for (int i = 0; i < hierarchyCount; i++) {
         EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchy(i);
         this.collectSentAddressesFromFolder((EmailFolder)hierarchy.getUnfiledFolder());
      }

      Arrays.sort(this._sentMessages, new AddressGuesser$2(this));
   }

   private void loadSentMessages() {
      for (int i = 0; i < this._sentMessages.length; i++) {
         if (!this.loadSentAddressesFromMessage(this._sentMessages[i])) {
            return;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void collectSentAddressesFromFolder(EmailFolder folder) {
      Collection messages = folder.getContainedItems();
      if (messages instanceof Object) {
         ReadableList list = (ReadableList)messages;

         for (int i = list.size() - 1; i >= 0; i--) {
            boolean var10 = false /* VF: Semaphore variable */;

            try {
               var10 = true;
               Object exception = list.getAt(i);
               if (!(exception instanceof EmailMessageModel)) {
                  var10 = false;
               } else {
                  EmailMessageModel message = (EmailMessageModel)exception;
                  int status = message.getStatus();
                  if (status != 33554431 && status != Integer.MAX_VALUE) {
                     var10 = false;
                  } else {
                     int sentCount = this._sentMessages.length;
                     Array.resize(this._sentMessages, sentCount + 1);
                     this._sentMessages[sentCount] = message;
                     var10 = false;
                  }
               }
            } finally {
               if (var10) {
                  return;
               }
            }
         }
      }
   }

   private boolean loadSentAddressesFromMessage(EmailMessageModel message) {
      int[] refs = this.getAddressReferences(message);
      if (refs.length < 2) {
         return true;
      }

      if (!this._buffer.hasRoomFor(refs.length)) {
         return false;
      }

      this._buffer.addListToTail(refs);
      return true;
   }

   private boolean isSubsetOfCurrentEntry(int[] refs) {
      int refsLength = refs.length;

      for (int i = 0; i < refsLength; i++) {
         if (!this.isInCurrentEntry(refs[i])) {
            return false;
         }
      }

      return true;
   }

   private int[] subtractFromCurrentEntry(int[] refs) {
      int refsLength = refs.length;
      int[] difference = new int[this._currentEntryLength];
      int count = 0;

      for (int i = 0; i < this._currentEntryLength; i++) {
         int entryValue = this._buffer.getElement(i);
         boolean foundIt = false;

         for (int j = 0; j < refsLength; j++) {
            if (entryValue == refs[j]) {
               foundIt = true;
               break;
            }
         }

         if (!foundIt) {
            difference[count++] = entryValue;
         }
      }

      Array.resize(difference, count);
      return difference;
   }

   private boolean isInCurrentEntry(int ref) {
      for (int i = 0; i < this._currentEntryLength; i++) {
         if (this._buffer.getElement(i) == ref) {
            return true;
         }
      }

      return false;
   }

   private int[] filterMatch(KeywordFilterList filter, int[] refs) {
      if (filter == null) {
         return refs;
      }

      int[] filtered = new int[refs.length];
      int count = 0;

      for (int i = 0; i < refs.length; i++) {
         if (filter.matches(this.getAddress(refs[i]))) {
            filtered[count++] = refs[i];
         }
      }

      Array.resize(filtered, count);
      return filtered;
   }

   private int tieBreaker(int[] refs) {
      return refs[0];
   }

   private int[] getAddressReferences(EmailMessageModel message) {
      int[] refs = new int[0];
      int count = 0;

      for (int i = 0; i < message.size(); i++) {
         RIMModel model = (RIMModel)message.getAt(i);
         if (model instanceof EmailHeaderModel) {
            EmailHeaderModel ehm = (EmailHeaderModel)model;
            int type = ehm.getHeaderType();
            if (type == 0 || type == 1 || type == 2) {
               RIMModel inside = ehm.getInsideModel();
               if (inside instanceof Object) {
                  EmailAddressModel addressCard = (EmailAddressModel)inside;
                  int ref = this.getReference(addressCard);
                  if (ref != 0) {
                     Array.resize(refs, ++count);
                     refs[count - 1] = ref;
                  }
               }
            }
         }
      }

      return refs;
   }

   private int getReference(EmailAddressModel addrModel) {
      Object card = AddressBookServices.reverseLookup(addrModel);
      if (!(card instanceof Object)) {
         return 0;
      }

      SyncObject so = (SyncObject)card;
      return so.getUID();
   }

   private AddressCardModel getAddress(int ref) {
      Object obj = AddressBookServices.getAddressBook().getAddressCard(ref);
      return (AddressCardModel)(!(obj instanceof Object) ? null : obj);
   }
}
