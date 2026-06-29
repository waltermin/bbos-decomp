package net.rim.device.apps.internal.bluetooth;

import java.io.OutputStream;
import javax.bluetooth.DataElement;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.obex.Authenticator;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.PasswordAuthentication;
import javax.obex.ServerRequestHandler;
import javax.obex.SessionNotifier;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.Phone;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.data.PhoneCallModelImpl;
import net.rim.device.apps.internal.phone.data.PhoneFolders;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.cldc.io.btspp.BluetoothStreamConnection;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;
import net.rim.device.internal.deviceoptions.Owner;
import net.rim.device.internal.system.Security;

class PBAPServer extends ServerRequestHandler implements Runnable, Authenticator {
   private int _currentFolder;
   private AddressCardWrapper[] _addressBookCards;
   private Comparator _addressCardNameComparator;
   private BluetoothStreamConnection _conn;
   private static final boolean DEBUG = true;
   private static final int FOLDER_ROOT = 0;
   private static final int FOLDER_TELECOM = 1;
   private static final int FOLDER_TELECOM_PB = 2;
   private static final int FOLDER_TELECOM_ICH = 3;
   private static final int FOLDER_TELECOM_OCH = 4;
   private static final int FOLDER_TELECOM_MCH = 5;
   private static final int FOLDER_TELECOM_CCH = 6;
   private static final int FOLDER_SIM1 = 7;
   private static final int FOLDER_SIM1_TELECOM = 8;
   private static final int FOLDER_SIM1_TELECOM_PB = 9;
   private static final int FOLDER_SIM1_TELECOM_ICH = 10;
   private static final int FOLDER_SIM1_TELECOM_OCH = 11;
   private static final int FOLDER_SIM1_TELECOM_MCH = 12;
   private static final int FOLDER_SIM1_TELECOM_CCH = 13;
   private static final int SEARCH_ORDER_INDEXED = 0;
   private static final int SEARCH_ORDER_ALPHANUMERIC = 1;
   private static final int SEARCH_ORDER_PHONETIC = 2;
   private static final int SEARCH_ATTRIBUTE_NAME = 0;
   private static final int SEARCH_ATTRIBUTE_NUMBER = 1;
   private static final int SEARCH_ATTRIBUTE_SOUND = 2;
   private static final int FORMAT_VCARD_2_1 = 0;
   private static final int FORMAT_VCARD_3_0 = 1;
   private static final byte[] LISTING_VERSION = new byte[]{60, 63, 120, 109, 108, 32, 118, 101, 114, 115, 105, 111, 110, 61, 34, 49, 46, 48, 34, 63, 62, 10};
   private static final byte[] LISTING_DOCTYPE = new byte[]{
      60,
      33,
      68,
      79,
      67,
      84,
      89,
      80,
      69,
      32,
      118,
      99,
      97,
      114,
      100,
      45,
      108,
      105,
      115,
      116,
      105,
      110,
      103,
      32,
      83,
      89,
      83,
      84,
      69,
      77,
      32,
      34,
      118,
      99,
      97,
      114,
      100,
      45,
      108,
      105,
      115,
      116,
      105,
      110,
      103,
      46,
      100,
      116,
      100,
      34,
      62,
      10,
      10
   };
   private static final byte[] LISTING_HEADER = new byte[]{
      60, 118, 67, 97, 114, 100, 45, 108, 105, 115, 116, 105, 110, 103, 32, 118, 101, 114, 115, 105, 111, 110, 61, 34, 49, 46, 48, 34, 62, 10
   };
   private static final byte[] LISTING_FOOTER = new byte[]{60, 47, 118, 67, 97, 114, 100, 45, 108, 105, 115, 116, 105, 110, 103, 62, 10};
   private static final byte[] LISTING_START = new byte[]{32, 32, 60, 99, 97, 114, 100, 32, 104, 97, 110, 100, 108, 101, 32, 61, 32, 34};
   private static final byte[] LISTING_MIDDLE = new byte[]{46, 118, 99, 102, 34, 32, 110, 97, 109, 101, 32, 61, 32, 34};
   private static final byte[] LISTING_END = new byte[]{34, 47, 62, 10};
   private static final byte[][][] TELECOM_NAMES = new byte[][][]{
      (byte[][])({112, 98}), (byte[][])({105, 99, 104}), (byte[][])({111, 99, 104}), (byte[][])({109, 99, 104}), (byte[][])({99, 99, 104})
   };
   private static final byte[][][] TELECOM_DESCRIPTIONS = new byte[][][]{
      (byte[][])({80, 104, 111, 110, 101, 98, 111, 111, 107}),
      (byte[][])({73, 110, 99, 111, 109, 105, 110, 103, 32, 67, 97, 108, 108, 32, 72, 105, 115, 116, 111, 114, 121}),
      (byte[][])({79, 117, 116, 103, 111, 105, 110, 103, 32, 67, 97, 108, 108, 32, 72, 105, 115, 116, 111, 114, 121}),
      (byte[][])({77, 105, 115, 115, 101, 100, 32, 67, 97, 108, 108, 32, 72, 105, 115, 116, 111, 114, 121}),
      (byte[][])({67, 111, 109, 98, 105, 110, 101, 100, 32, 67, 97, 108, 108, 32, 72, 105, 115, 116, 111, 114, 121})
   };
   private static final byte[] AUTH_USERNAME = new byte[]{117, 115, 101, 114, 110, 97, 109, 101};
   private static final byte[] AUTH_PASSWORD = new byte[]{48, 48, 48, 48};

   boolean disconnect(byte[] address) {
      try {
         if (this._conn != null && Arrays.equals(this._conn.getRemoteAddress(), address)) {
            this._conn.close();
            this._conn = null;
            return true;
         }
      } finally {
         return false;
      }

      return false;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      try {
         SessionNotifier notifier = (SessionNotifier)Connector.open("btgoep://localhost:112F;name=Phonebook Access PSE");
         LocalDevice localDevice = LocalDevice.getLocalDevice();
         ServiceRecord serviceRecord = localDevice.getRecord(notifier);
         DataElement element = new DataElement(48);
         DataElement element1 = new DataElement(48);
         element1.addElement(new DataElement(24, new UUID("1130", true)));
         element1.addElement(new DataElement(9, 256));
         element.addElement(element1);
         serviceRecord.setAttributeValue(9, element);
         element = new DataElement(8, 1);
         serviceRecord.setAttributeValue(788, element);

         while (true) {
            this._conn = (BluetoothStreamConnection)notifier.acceptAndOpen(this, this);
         }
      } catch (Throwable var7) {
         throw new Object(ex.getMessage());
      }
   }

   @Override
   public PasswordAuthentication onAuthenticationChallenge(String description, boolean isUserIdRequired, boolean isFullAccess) {
      return isUserIdRequired ? new PasswordAuthentication(AUTH_USERNAME, AUTH_PASSWORD) : new PasswordAuthentication(null, AUTH_PASSWORD);
   }

   @Override
   public byte[] onAuthenticationResponse(byte[] userName) {
      return null;
   }

   @Override
   public void onDisconnect(HeaderSet request, HeaderSet reply) {
      this._currentFolder = 0;
      this._addressBookCards = null;
      this._conn = null;
      super.onDisconnect(request, reply);
   }

   @Override
   public int onPut(Operation op) {
      return 192;
   }

   @Override
   public int onGet(Operation op) {
      System.out.println("onGet");

      try {
         HeaderSet request = op.getReceivedHeaders();
         if (request == null) {
            return 192;
         }

         String type = (String)request.getHeader(66);
         String name = (String)request.getHeader(1);
         byte[] params = (byte[])request.getHeader(76);
         if (type != null && name != null) {
            int searchOrder = 0;
            String searchValue = null;
            boolean searchByNumber = false;
            int maxListCount = 65535;
            int listStartOffset = 0;
            long filter = 0;
            int format = 0;
            if (params != null) {
               DataBuffer db = (DataBuffer)(new Object(params, 0, params.length, true));

               while (!db.eof()) {
                  int value = db.readUnsignedByte();
                  int length = db.readUnsignedByte();
                  switch (value) {
                     case 0:
                        db.skipBytes(length);
                        break;
                     case 1:
                     default:
                        if (length != 1) {
                           return 204;
                        }

                        searchOrder = db.readUnsignedByte();
                        switch (searchOrder) {
                           case -1:
                              return 204;
                           case 0:
                           case 1:
                              System.out.println(((StringBuffer)(new Object("Order: "))).append(searchOrder).toString());
                              continue;
                           case 2:
                           default:
                              return 209;
                        }
                     case 2:
                        if (length > 0) {
                           byte[] data = new byte[length];
                           db.read(data);
                           if (data[data.length - 1] == 0) {
                              length--;
                           }

                           searchValue = ((String)(new Object(data, 0, length, "utf-8"))).toLowerCase();
                           System.out.println(((StringBuffer)(new Object("SearchValue: "))).append(searchValue).toString());
                        }
                        break;
                     case 3:
                        if (length != 1) {
                           return 204;
                        }

                        int attribute = db.readUnsignedByte();
                        switch (attribute) {
                           case -1:
                              return 204;
                           case 0:
                           default:
                              searchByNumber = false;
                              break;
                           case 1:
                              searchByNumber = true;
                              break;
                           case 2:
                              return 209;
                        }

                        System.out.println(((StringBuffer)(new Object("SearchAttribute: "))).append(attribute).toString());
                        break;
                     case 4:
                        if (length != 2) {
                           return 204;
                        }

                        maxListCount = db.readUnsignedShort();
                        System.out.println(((StringBuffer)(new Object("MaxListCount: "))).append(maxListCount).toString());
                        break;
                     case 5:
                        if (length != 2) {
                           return 204;
                        }

                        listStartOffset = db.readUnsignedShort();
                        System.out.println(((StringBuffer)(new Object("ListStartOffset: "))).append(listStartOffset).toString());
                        break;
                     case 6:
                        if (length != 8) {
                           return 204;
                        }

                        filter = db.readLong();
                        System.out.println(((StringBuffer)(new Object("Filter: 0x"))).append(NumberUtilities.toString(filter, 16)).toString());
                        break;
                     case 7:
                        if (length != 1) {
                           return 204;
                        }

                        format = db.readUnsignedByte();
                        switch (format) {
                           case -1:
                           default:
                              return 204;
                           case 0:
                           case 1:
                              System.out.println(((StringBuffer)(new Object("Format: 0x"))).append(Integer.toHexString(format)).toString());
                        }
                  }
               }
            }

            if (filter == 0) {
               filter = 4294967295L;
            } else {
               filter |= 1;
               filter |= format == 0 ? 132 : 130;
            }

            int folder = -1;
            if (!type.equals("x-bt/vcard-listing")) {
               if (type.equals("x-bt/phonebook")) {
                  if (name.startsWith("telecom/")) {
                     name = name.substring(8);
                     if (name.endsWith(".vcf")) {
                        name = name.substring(0, name.length() - 4);
                        folder = this.getFolder(name);
                        return folder == -1 ? 196 : this.dumpAddressBook(name, folder, listStartOffset, maxListCount, format, filter, op, false);
                     } else {
                        return 196;
                     }
                  } else {
                     return 196;
                  }
               } else {
                  if (!type.equals("text/x-vcard") && !type.equals("x-bt/vcard")) {
                     return 192;
                  }

                  if (name.endsWith(".vcf")) {
                     name = name.substring(0, name.length() - 4);
                     switch (this._currentFolder) {
                        case 0:
                        case 7:
                           return 196;
                        case 1:
                        case 8:
                        default:
                           folder = this.getFolder(name);
                           if (folder == -1) {
                              return 196;
                           }

                           return this.dumpAddressBook(name, folder, listStartOffset, maxListCount, format, filter, op, false);
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                           int index;
                           try {
                              index = Integer.parseInt(name);
                           } finally {
                              ;
                           }

                           return this.dumpAddressBook(name, this._currentFolder, index, 1, format, filter, op, true);
                     }
                  } else {
                     return 196;
                  }
               }
            } else {
               if (name != null && name.length() != 0) {
                  folder = this.getRelativeFolder(name);
               } else {
                  folder = this._currentFolder;
               }

               return folder == -1 ? 196 : this.dumpFolderListing(folder, listStartOffset, maxListCount, searchOrder, searchValue, searchByNumber, op);
            }
         } else {
            return 192;
         }
      } finally {
         ;
      }
   }

   private boolean snapshotAddressBook() {
      if (this._addressBookCards != null) {
         return true;
      }

      if (ApplicationManager.getApplicationManager().isSystemLocked()
         && PersistentContent.isEncryptionEnabled()
         && !Security.getInstance().getExcludeAddressBookFromContentProtection()) {
         return false;
      }

      BluetoothDeviceManagerImpl btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
      this._addressBookCards = new AddressCardWrapper[0];
      AddressBook ab = AddressBookServices.getAddressBook(true);
      if (ab == null) {
         return false;
      }

      this._addressCardNameComparator = new AddressCardWrapperNameComparator(ab.getComparator(null, -227891759293611117L));
      String name = Owner.getOwnerName();
      AddressCardModel ownerCard = null;
      if (name != null) {
         if (name.length() == 0) {
            name = null;
         } else {
            Object[] array = ab.lookup(name, 1);
            if (array != null) {
               ownerCard = (AddressCardModel)array[0];
            }
         }
      }

      if (ownerCard == null) {
         ownerCard = this.createAddressCard();
         String firstName = null;
         String lastName = null;
         if (name != null) {
            int i = name.indexOf(32);
            if (i == -1) {
               lastName = name;
            } else {
               lastName = name.substring(i);
               firstName = name.substring(i + 1, name.length());
            }
         } else {
            lastName = btManager.getLocalName();
         }

         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         ContextObject context = (ContextObject)(new Object());
         Factory personNameModelFactory = (Factory)ar.get(5149066071290992769L);
         String[] personName = new Object[2];
         personName[0] = firstName;
         personName[1] = lastName;
         context.put(251, personName);
         Object personNameModel = personNameModelFactory.createInstance(context);
         ownerCard.add(personNameModel);
         String phoneNumber = null;

         label96:
         try {
            phoneNumber = Phone.getInstance().getNumber(0);
         } finally {
            break label96;
         }

         if (phoneNumber != null) {
            Factory phoneNumberModelFactory = (Factory)ar.get(3797587162219887872L);
            PhoneNumberModel pnm = (PhoneNumberModel)phoneNumberModelFactory.createInstance(null);
            pnm.setValue(phoneNumber);
            pnm.setType(5);
            ownerCard.add(pnm);
         }
      }

      int index = 0;
      AddressCardWrapper wrapper = new AddressCardWrapper(ownerCard, index++);
      Arrays.add(this._addressBookCards, wrapper);
      AddressCardModel[] cards = btManager.getAddressCards(false);
      if (cards == null) {
         return true;
      }

      int count = cards.length;

      for (int i = 0; i < count; i++) {
         if (cards[i] != ownerCard) {
            wrapper = new AddressCardWrapper(cards[i], index++);
            Arrays.add(this._addressBookCards, wrapper);
         }
      }

      return true;
   }

   private AddressCardModel createAddressCard() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Factory addressCardModelFactory = (Factory)ar.get(-3124646573404667739L);
      return (AddressCardModel)addressCardModelFactory.createInstance(null);
   }

   private AddressCardWrapper[] snapshotCallHistory(int folder) {
      AddressCardWrapper[] list = new AddressCardWrapper[0];
      switch (folder) {
         case 2:
            break;
         case 3:
         default:
            this.buildCallHistory(list, PhoneFolders.getDefaultFolder(), folder, false);
            break;
         case 4:
            this.buildCallHistory(list, PhoneFolders.getDefaultFolder(), folder, false);
            break;
         case 5:
            this.buildCallHistory(list, PhoneFolders.getMissedCallFolder(), folder, true);
            break;
         case 6:
            this.buildCallHistory(list, PhoneFolders.getDefaultFolder(), folder, false);
            this.buildCallHistory(list, PhoneFolders.getMissedCallFolder(), folder, true);
      }

      Arrays.sort(list, new AddressCardWrapperTimeComparator());
      int i = list.length;

      while (i > 0) {
         list[i - 1]._index = i--;
      }

      return list;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void buildCallHistory(AddressCardWrapper[] list, SimpleFolder phoneFolder, int folder, boolean missed) {
      SimpleDateFormat dateFormat = (SimpleDateFormat)(new Object("yyyyMMddTHHmmss"));
      StringBuffer sb = (StringBuffer)(new Object());
      ReadableList collection = (ReadableList)phoneFolder.getContainedItems();
      int items = collection.size();

      for (int i = 0; i < items; i++) {
         boolean var20 = false /* VF: Semaphore variable */;

         try {
            var20 = true;
            PhoneCallModelImpl call = collection.getAt(i);
            String vCardExtensionName;
            if (((PhoneCallModelImpl)call).isIncoming()) {
               if (folder == 4) {
                  var20 = false;
                  continue;
               }

               if (missed) {
                  vCardExtensionName = "IRMC-CALL-DATETIME;MISSED";
               } else {
                  vCardExtensionName = "IRMC-CALL-DATETIME;RECEIVED";
               }
            } else {
               if (folder == 3) {
                  var20 = false;
                  continue;
               }

               vCardExtensionName = "IRMC-CALL-DATETIME;DIALED";
            }

            long timestamp = ((PhoneCallModelImpl)call).getTimeStamp();
            sb.setLength(0);
            dateFormat.formatLocal(sb, timestamp);
            String vCardExtensionData = sb.toString();
            CallerIDInfo clid = ((PhoneCallModelImpl)call).getCallerIDInfo();
            Object o = clid.getAddress();
            if (!clid.isPrivateNumber()) {
               if (clid.isUnknownNumber()) {
                  var20 = false;
               } else {
                  AddressCardModel card;
                  if (!(o instanceof Object)) {
                     PersistableRIMModel phoneNumber = clid.getNumber();
                     if (phoneNumber == null) {
                        var20 = false;
                        continue;
                     }

                     card = this.createAddressCard();
                     card.add(phoneNumber);
                  } else {
                     card = (AddressCardModel)o;
                  }

                  Arrays.add(list, new AddressCardWrapper(card, list.length + 1, timestamp, vCardExtensionName, vCardExtensionData));
                  var20 = false;
               }
            } else {
               var20 = false;
            }
         } finally {
            if (var20) {
               return;
            }
         }
      }
   }

   @Override
   public int onSetPath(HeaderSet request, HeaderSet reply, boolean backup, boolean create) {
      System.out.println("onSetPath");

      String name;
      try {
         name = (String)request.getHeader(1);
      } finally {
         ;
      }

      if (create) {
         return 195;
      }

      if (backup) {
         switch (this._currentFolder) {
            case -1:
               break;
            case 0:
            default:
               return 195;
            case 1:
               this._currentFolder = 0;
               break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
               this._currentFolder = 1;
               break;
            case 7:
               this._currentFolder = 0;
               break;
            case 8:
               this._currentFolder = 7;
               break;
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
               this._currentFolder = 8;
         }
      }

      int newFolder;
      if (name != null && name.length() != 0) {
         newFolder = this.getRelativeFolder(name);
      } else if (backup) {
         newFolder = this._currentFolder;
      } else {
         newFolder = 0;
      }

      if (newFolder == -1) {
         return 196;
      }

      System.out.println(((StringBuffer)(new Object("New folder: "))).append(newFolder).toString());
      this._currentFolder = newFolder;
      return 160;
   }

   private int getFolder(String name) {
      if (name.equals("pb")) {
         return 2;
      } else if (name.equals("ich")) {
         return 3;
      } else if (name.equals("och")) {
         return 4;
      } else if (name.equals("mch")) {
         return 5;
      } else {
         return name.equals("cch") ? 6 : -1;
      }
   }

   private int getRelativeFolder(String name) {
      int newFolder = -1;
      if (name.equals("telecom")) {
         switch (this._currentFolder) {
            case 0:
               return 1;
            case 7:
               return 8;
         }
      } else {
         if (name.equals("SIM1")) {
            return newFolder;
         }

         if (name.equals("pb")) {
            switch (this._currentFolder) {
               case 1:
                  return 2;
               case 8:
                  return 9;
            }
         } else if (name.equals("ich")) {
            switch (this._currentFolder) {
               case 1:
                  return 3;
               case 8:
                  return 10;
            }
         } else if (name.equals("och")) {
            switch (this._currentFolder) {
               case 1:
                  return 4;
               case 8:
                  return 11;
            }
         } else if (name.equals("mch")) {
            switch (this._currentFolder) {
               case 1:
                  return 5;
               case 8:
                  return 12;
            }
         } else if (name.equals("cch")) {
            switch (this._currentFolder) {
               case 1:
                  return 6;
               case 8:
                  newFolder = 13;
            }
         }
      }

      return newFolder;
   }

   private int sendListSize(int size, Operation op) {
      try {
         HeaderSet hs = this.createHeaderSet();
         byte[] phonebookSize = new byte[]{8, 2, (byte)(size >> 8 & 0xFF), (byte)(size & 0xFF)};
         hs.setHeader(76, phonebookSize);
         op.sendHeaders(hs);
         op.close();
         return 160;
      } finally {
         ;
      }
   }

   private int dumpAddressBook(
      String addressBookName, int folder, int startOffset, int maxListCount, int format, long filter, Operation op, boolean singleEntry
   ) {
      AddressCardWrapper[] list;
      if (folder == 2) {
         if (!this.snapshotAddressBook()) {
            return 211;
         }

         list = this._addressBookCards;
      } else {
         list = this.snapshotCallHistory(folder);
         if (singleEntry) {
            startOffset--;
         }
      }

      if (maxListCount == 0) {
         return this.sendListSize(list.length, op);
      }

      try {
         if ((list.length == 0 || startOffset < list.length) && startOffset >= 0) {
            ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
            Factory converter = (Factory)ar.get(-5888220356524146836L);
            ContextObject context = (ContextObject)(new Object());
            StupidCLDCByteArrayOutputStream vCardStream = new StupidCLDCByteArrayOutputStream();
            context.put(-980891548873596767L, vCardStream);
            context.put(-4054673099568009991L, new Object((int)filter));
            context.put(4086083307293257364L, new Object(format == 1));
            context.setFlag(127);

            for (int i = startOffset; i < list.length && maxListCount > 0; maxListCount--) {
               AddressCardWrapper wrapper = list[i];
               context.put(254, wrapper._card);
               if (wrapper._vCardExtensionName != null) {
                  String[] data = new Object[2];
                  data[0] = wrapper._vCardExtensionName;
                  data[1] = wrapper._vCardExtensionData;
                  context.put(251, data);
               }

               converter.createInstance(context);
               i++;
            }

            HeaderSet hs = this.createHeaderSet();
            hs.setHeader(1, ((StringBuffer)(new Object())).append(addressBookName).append(".vcf").toString());
            hs.setHeader(195, new Object(vCardStream.size()));
            op.sendHeaders(hs);
            OutputStream out = op.openOutputStream();
            vCardStream.writeTo(out);
            out.close();
            op.close();
            return 160;
         } else {
            return maxListCount == 1 ? 196 : 204;
         }
      } finally {
         ;
      }
   }

   private int dumpFolderListing(int folder, int startOffset, int maxListCount, int searchOrder, String searchValue, boolean searchNumber, Operation op) {
      switch (folder) {
         case 0:
            return 196;
         case 1:
         default:
            return this.dumpTelecomListing(startOffset, maxListCount, op);
         case 2:
            return this.dumpPhonebookListing(startOffset, maxListCount, searchOrder, searchValue, searchNumber, op);
         case 3:
         case 4:
         case 5:
         case 6:
            return this.dumpCallHistoryListing(folder, startOffset, maxListCount, op);
      }
   }

   private int dumpTelecomListing(int startOffset, int maxListCount, Operation op) {
      if (maxListCount == 0) {
         return this.sendListSize(5, op);
      }

      if (startOffset <= 0 && maxListCount >= 1) {
         try {
            StupidCLDCByteArrayOutputStream listingStream = new StupidCLDCByteArrayOutputStream();
            listingStream.write(LISTING_VERSION);
            listingStream.write(LISTING_DOCTYPE);
            listingStream.write(LISTING_HEADER);

            for (int i = 0; i < TELECOM_NAMES.length; i++) {
               listingStream.write(LISTING_START);
               listingStream.write((byte[])TELECOM_NAMES[i]);
               listingStream.write(LISTING_MIDDLE);
               listingStream.write((byte[])TELECOM_DESCRIPTIONS[i]);
               listingStream.write(LISTING_END);
            }

            listingStream.write(LISTING_FOOTER);
            HeaderSet hs = this.createHeaderSet();
            hs.setHeader(195, new Object(listingStream.size()));
            op.sendHeaders(hs);
            OutputStream out = op.openOutputStream();
            listingStream.writeTo(out);
            out.close();
            op.close();
            return 160;
         } finally {
            ;
         }
      } else {
         return 204;
      }
   }

   private StupidCLDCByteArrayOutputStream listingStreamInit() {
      StupidCLDCByteArrayOutputStream listingStream = new StupidCLDCByteArrayOutputStream();
      listingStream.write(LISTING_VERSION);
      listingStream.write(LISTING_DOCTYPE);
      listingStream.write(LISTING_HEADER);
      return listingStream;
   }

   private void listingStreamFini(StupidCLDCByteArrayOutputStream listingStream, Operation op) {
      listingStream.write(LISTING_FOOTER);
      HeaderSet hs = this.createHeaderSet();
      hs.setHeader(195, new Object(listingStream.size()));
      op.sendHeaders(hs);
      OutputStream out = op.openOutputStream();
      listingStream.writeTo(out);
      out.close();
      op.close();
   }

   private int dumpPhonebookListing(int startOffset, int maxListCount, int searchOrder, String searchValue, boolean searchNumber, Operation op) {
      if (!this.snapshotAddressBook()) {
         return 211;
      }

      if (maxListCount == 0) {
         return this.sendListSize(this._addressBookCards.length, op);
      }

      try {
         if (this._addressBookCards.length != 0 && startOffset >= this._addressBookCards.length) {
            return 204;
         }

         StupidCLDCByteArrayOutputStream listingStream = this.listingStreamInit();
         if (searchValue != null) {
            if (searchNumber) {
               this.searchByNumber(startOffset, maxListCount, searchOrder, searchValue, listingStream);
            } else {
               this.searchByName(startOffset, maxListCount, searchOrder, searchValue, listingStream);
            }
         } else {
            AddressCardWrapper[] list;
            if (searchOrder == 1) {
               list = new AddressCardWrapper[this._addressBookCards.length];
               System.arraycopy(this._addressBookCards, 0, list, 0, this._addressBookCards.length);
               Arrays.sort(list, this._addressCardNameComparator);
            } else {
               list = this._addressBookCards;
            }

            for (int i = startOffset; i < this._addressBookCards.length && maxListCount > 0; maxListCount--) {
               this.writeListing(list[i], listingStream);
               i++;
            }
         }

         this.listingStreamFini(listingStream, op);
         return 160;
      } finally {
         ;
      }
   }

   private int dumpCallHistoryListing(int folder, int startOffset, int maxListCount, Operation op) {
      AddressCardWrapper[] list = this.snapshotCallHistory(folder);
      if (maxListCount == 0) {
         return this.sendListSize(list.length, op);
      }

      try {
         if (list.length != 0 && startOffset >= list.length) {
            return 204;
         }

         StupidCLDCByteArrayOutputStream listingStream = this.listingStreamInit();

         for (int i = startOffset; i < list.length && maxListCount > 0; maxListCount--) {
            this.writeListing(list[i], listingStream);
            i++;
         }

         this.listingStreamFini(listingStream, op);
         return 160;
      } finally {
         ;
      }
   }

   private void searchByName(int startOffset, int maxListCount, int searchOrder, String searchValue, OutputStream out) {
      AddressCardWrapper[] list = new AddressCardWrapper[0];
      int i = startOffset;

      while (i < this._addressBookCards.length && maxListCount > 0) {
         String name = this.getListingName(this._addressBookCards[i]._card);
         if (name != null) {
            name = name.toLowerCase();
            if (name.startsWith(searchValue)) {
               Arrays.add(list, this._addressBookCards[i]);
            }
         }

         i++;
         maxListCount--;
      }

      if (searchOrder == 1) {
         Arrays.sort(list, this._addressCardNameComparator);
      }

      for (int ix = 0; ix < list.length; ix++) {
         this.writeListing(list[ix], out);
      }
   }

   private void searchByNumber(int startOffset, int maxListCount, int searchOrder, String searchValue, OutputStream out) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Factory phoneNumberModelFactory = (Factory)ar.get(3797587162219887872L);
      PhoneNumberModel pnm = (PhoneNumberModel)phoneNumberModelFactory.createInstance(null);
      pnm.setValue(searchValue);
      AddressCardWrapper[] list = new AddressCardWrapper[0];
      int i = startOffset;

      while (i < this._addressBookCards.length && maxListCount > 0) {
         AddressCardModel card = this._addressBookCards[i]._card;
         Object[] models = card.getPhoneNumberModels();
         if (models != null) {
            for (int j = 0; j < models.length; j++) {
               if (pnm.equals(models[j], true)) {
                  Arrays.add(list, this._addressBookCards[i]);
                  break;
               }
            }
         }

         i++;
         maxListCount--;
      }

      if (searchOrder == 1) {
         Arrays.sort(list, this._addressCardNameComparator);
      }

      for (int ix = 0; ix < list.length; ix++) {
         this.writeListing(list[ix], out);
      }
   }

   private String getListingName(AddressCardModel card) {
      String name = null;
      PersonNameModel pnm = card.getName();
      if (pnm == null) {
         CompanyInfoModel cim = card.getCompanyInfo();
         if (cim == null) {
            Object[] models = card.getPhoneNumberModels();
            return models != null ? models[0].toString() : null;
         } else {
            return cim.getCompanyName();
         }
      } else {
         name = pnm.getLastName();
         String firstName = pnm.getFirstName();
         if (firstName != null) {
            if (name == null) {
               return firstName;
            }

            name = ((StringBuffer)(new Object())).append(name).append(';').append(firstName).toString();
         }

         return name;
      }
   }

   private void writeListing(AddressCardWrapper wrapper, OutputStream out) {
      String name = this.getListingName(wrapper._card);
      if (name != null) {
         out.write(LISTING_START);
         out.write(NumberUtilities.toString(wrapper._index, 10).getBytes());
         out.write(LISTING_MIDDLE);
         out.write(name.getBytes("utf-8"));
         out.write(LISTING_END);
      }
   }

   @Override
   public int onConnect(HeaderSet request, HeaderSet reply) {
      this._currentFolder = 0;
      this._addressBookCards = null;
      return super.onConnect(request, reply);
   }
}
