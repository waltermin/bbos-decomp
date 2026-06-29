package net.rim.device.apps.internal.addressbook.groupaddresscard;

import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringMatch;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardElement;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.apps.api.framework.model.ValidationProvider;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.internal.i18n.UnicodeServiceRegistry;
import net.rim.device.internal.system.Security;
import net.rim.vm.Array;

public final class GroupAddressCardModelImpl
   implements GroupAddressCardModel,
   SyncObject,
   FieldProvider,
   PaintProvider,
   VerbProvider,
   VerbDescriptionProvider,
   KeyProvider,
   ConversionProvider,
   UniqueIDProvider,
   ValidationProvider,
   EditableProvider,
   MatchProvider,
   Copyable,
   EncryptableProvider {
   private int _uid;
   private Object _nameEncoding;
   private GroupAddressCardMember[] _members = new GroupAddressCardMember[0];
   private static int[] _hints = new int[0];

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context) {
      if (ContextObject.get(context, 5141706140756983937L) != null && x == 0) {
         return 0;
      }

      int flags = ContextObject.getFlag(context, 17) ? 64 : 0;
      return g.drawText(this.getName(), x, y, flags, width);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      ContextObject contextObject = ContextObject.castOrCreate(context);
      Verb defaultVerb = null;
      GroupAddressCardModelImpl gacm = (GroupAddressCardModelImpl)AddressBookServices.getAddressCard(this._uid);
      if (!contextObject.getFlag(87) && gacm != null) {
         if (gacm.isValid() && !ContextObject.getFlag(context, 5)) {
            Array.resize(verbs, 0);
            boolean pin = false;
            boolean email = false;
            boolean phone = false;
            int size = gacm.size();

            for (int i = 0; i < size; i++) {
               switch (gacm._members[i].getType()) {
                  case -1:
                     break;
                  case 0:
                  default:
                     email = true;
                     break;
                  case 1:
                     pin = true;
                     break;
                  case 2:
                     phone = true;
               }
            }

            Verb[] composeVerbs = new Object[0];
            Verb[] tmpVerbs = null;
            if (email) {
               tmpVerbs = VerbRepository.getVerbRepository(-7881764549058890736L).getVerbs(-2985347935260258684L);
               if (tmpVerbs != null) {
                  Arrays.append(composeVerbs, tmpVerbs);
               }
            }

            if (phone) {
               tmpVerbs = VerbRepository.getVerbRepository(-7881764549058890736L).getVerbs(3797587162219887872L);
               if (tmpVerbs != null) {
                  Arrays.append(composeVerbs, tmpVerbs);
               }
            }

            if (pin) {
               tmpVerbs = VerbRepository.getVerbRepository(-7881764549058890736L).getVerbs(4246852237058296601L);
               if (tmpVerbs != null) {
                  Arrays.append(composeVerbs, tmpVerbs);
               }
            }

            for (int i = 0; i < composeVerbs.length; i++) {
               for (int j = i + 1; j < composeVerbs.length; j++) {
                  if (composeVerbs[i] == composeVerbs[j]) {
                     Arrays.removeAt(composeVerbs, j);
                     j--;
                  }
               }
            }

            Verb composeVerb = null;
            int pickOrder = 0;
            if (ContextObject.getFlag(context, 7)) {
               pickOrder = 327952;
            }

            int oldLength = verbs.length;
            int increase = composeVerbs.length;
            Array.resize(verbs, oldLength + increase);

            for (int i = 0; i < increase; i++) {
               composeVerb = composeVerbs[i];
               verbs[oldLength + i] = new GroupComposeAdapter(gacm, composeVerb, pickOrder != 0 ? pickOrder : composeVerb.getOrdering(), context);
            }
         }

         boolean addViewVerb = false;
         boolean addEditVerb = false;
         if (ContextObject.getFlag(context, 18)) {
            if ((contextObject.getFlag(2) || contextObject.getFlag(4)) && !contextObject.getFlag(108)) {
               if (contextObject.getFlag(3)) {
                  addViewVerb = true;
                  if (this.isEditable() && !contextObject.getFlag(85)) {
                     addEditVerb = true;
                  }
               } else if (this.isEditable() && !contextObject.getFlag(0) && !contextObject.getFlag(85)) {
                  addEditVerb = true;
               }
            }
         } else {
            addViewVerb = true;
         }

         if (addViewVerb) {
            Array.resize(verbs, verbs.length + 1);
            defaultVerb = verbs[verbs.length - 1] = new GroupAddressCardVerb(2, gacm);
         }

         if (addEditVerb) {
            Array.resize(verbs, verbs.length + 1);
            verbs[verbs.length - 1] = new GroupAddressCardVerb(1, gacm);
         }

         if (defaultVerb != null) {
            return defaultVerb;
         } else {
            return verbs.length > 0 ? verbs[0] : null;
         }
      } else {
         return null;
      }
   }

   @Override
   public final int match(Object searchCriteria) {
      if (!(searchCriteria instanceof Object)) {
         return Match.match(this, this, (Object[])searchCriteria, _hints);
      }

      SearchCriterion crit = (SearchCriterion)searchCriteria;
      int criteriaType = crit.getType();
      if (criteriaType == 24) {
         return crit.getValue() == this.getUID() ? 1 : 0;
      }

      if (criteriaType != 5) {
         return -1;
      }

      Object value = crit.getValue();
      if (!(value instanceof Object[])) {
         return -1;
      }

      Object[] values = (Object[])value;
      Object[] cards = (Object[])values[0];
      long[] cardLUIDs = (long[])values[1];
      int length = cards != null ? cards.length : 0;
      if (length > 0) {
         int luid = this._uid;

         for (int i = length - 1; i >= 0; i--) {
            if (luid == cardLUIDs[i] || this.equals(cards[i])) {
               return 1;
            }
         }
      }

      StringMatch matcher = (StringMatch)(new Object((Object[])values[2], false, false));
      String name = this.getName();
      return name != null && name.length() > 0 && matcher.indexOf(name) >= 0 ? 1 : 0;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   @Override
   public final String getVerbDescription(Object context) {
      return this.getName();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 18) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         byte[] groupIdentifier = new byte[]{71};
         syncBuffer.addBytes(44, groupIdentifier);
         syncBuffer.addField(32, this.getName());

         for (int i = 0; i < this.size(); i++) {
            DataBuffer infoBuff = (DataBuffer)(new Object(false));
            infoBuff.writeInt(this._members[i].getUID());
            infoBuff.writeByte(this._members[i].getEmailIndex() + 1);
            infoBuff.writeInt(this._members[i].getAddressCardSyncFieldId());
            syncBuffer.addBytes(52, infoBuff.getArray());
         }

         syncBuffer.addInt(46, this.size(), 4);
         return true;
      } else if (ContextObject.getFlag(context, 10) && target instanceof Object[]) {
         GroupAddressCardModelImpl gacm = (GroupAddressCardModelImpl)AddressBookServices.getAddressCard(this._uid);
         if (gacm == null) {
            Status.show(((StringBuffer)(new Object())).append(AddressBookResources.getString(1716)).append(this.getName()).toString());
         }

         String[] results = (Object[])target;
         Array.resize(results, this.size() * 2);

         for (int i = 0; i < this.size(); i++) {
            String[] nameAndAddressStrings = new Object[2];
            RIMModel rm = this._members[i].getAddressModel();
            if (rm instanceof Object) {
               ConversionProvider converter = (ConversionProvider)rm;
               converter.convert(context, nameAndAddressStrings);
               results[i * 2] = nameAndAddressStrings[0];
               results[i * 2 + 1] = nameAndAddressStrings[1];
            }
         }

         return true;
      } else {
         if (ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 19)) {
            byte bytesToAdd = 1;
            String encoding = null;
            boolean isEncoded = false;
            byte encodingByte = -1;
            String name = this.getName();
            if (name != null && !ConverterUtilities.isIntellisyncCompatible(name)) {
               isEncoded = true;
               encodingByte = ConverterUtilities.getConversionCurrentEncodingByte();
               encoding = ConverterUtilities.getConversionCurrentEncodingName();
               if (encodingByte == -1 || encoding == null || encoding.length() == 0) {
                  isEncoded = false;
                  encoding = null;
               }
            }

            if (encoding == null) {
               encoding = "windows-1252\r";
            }

            byte[] nameBytes = null;
            if (name != null) {
               boolean var13 = false /* VF: Semaphore variable */;

               label127:
               try {
                  var13 = true;
                  nameBytes = name.getBytes(encoding);
                  var13 = false;
               } finally {
                  if (var13) {
                     nameBytes = name.getBytes();
                     isEncoded = false;
                     break label127;
                  }
               }
            }

            if (nameBytes != null) {
               byte[] buf = new byte[nameBytes.length + 8 + bytesToAdd];
               if (isEncoded) {
                  buf[0] = encodingByte;
                  System.arraycopy(nameBytes, 0, buf, 1, nameBytes.length);
                  UnicodeServiceRegistry ur = UnicodeServiceRegistry.getInstance();
                  if (ur != null) {
                     ur.setFlags(ur.getFlags() | 1);
                  }
               } else {
                  System.arraycopy(nameBytes, 0, buf, 0, nameBytes.length);
               }

               int offset = nameBytes.length + bytesToAdd;
               buf[offset++] = 0;
               buf[offset++] = 0;
               buf[offset++] = -1;
               buf[offset++] = -1;
               buf[offset++] = (byte)this._uid;
               buf[offset++] = (byte)(this._uid >> 8);
               buf[offset++] = (byte)(this._uid >> 16);
               buf[offset++] = (byte)(this._uid >> 24);
               Object[] targetArray = (Object[])target;
               targetArray[0] = buf;
               if (((Object[])target).length > 1) {
                  ((byte[])((Object[])target)[1])[0] = bytesToAdd;
               }

               return true;
            }
         }

         return false;
      }
   }

   final void getMembers(GroupAddressCardMember[] members) {
      Array.resize(members, this._members.length);
      System.arraycopy(this._members, 0, members, 0, this._members.length);
   }

   @Override
   public final boolean membersAreEqual(Object o) {
      if (o instanceof GroupAddressCardModelImpl) {
         int minCount = this._members.length;
         Object[] otherFields = ((GroupAddressCardModelImpl)o)._members;
         int otherCount = otherFields.length;
         if (otherCount == minCount) {
            for (int i = 0; i < minCount; i++) {
               if (!this._members[i].equals(otherFields[i])) {
                  return false;
               }
            }

            return true;
         }
      }

      return false;
   }

   @Override
   public final boolean isValid(Object context) {
      return this.isValid();
   }

   final void setMembers(GroupAddressCardMember[] members) {
      Array.resize(this._members, members.length);
      System.arraycopy(members, 0, this._members, 0, members.length);
   }

   @Override
   public final void insertAt(int index, Object element) {
      if (element instanceof GroupAddressCardMember) {
         Array.resize(this._members, this._members.length + 1);
         System.arraycopy(this._members, index, this._members, index + 1, this._members.length - index - 1);
         this._members[index] = (GroupAddressCardMember)element;
      }
   }

   @Override
   public final boolean contains(Object element) {
      return this.getIndex(element) != -1;
   }

   @Override
   public final void remove(Object member) {
      Arrays.remove(this._members, member);
   }

   @Override
   public final void removeAll() {
      this.setName("");
      Array.resize(this._members, 0);
   }

   @Override
   public final void removeAt(int index) {
      if (index >= 0 && this._members.length > 0) {
         int newLength = this._members.length - 1;
         System.arraycopy(this._members, index + 1, this._members, index, newLength - index);
         Array.resize(this._members, newLength);
      }
   }

   @Override
   public final void add(Object member) {
      Arrays.add(this._members, member);
   }

   @Override
   public final int getIndex(Object element) {
      return Arrays.getIndex(this._members, element);
   }

   @Override
   public final void setUID(int uid) {
      this._uid = uid;
   }

   @Override
   public final String getName() {
      try {
         String name = PersistentContent.decodeString(this._nameEncoding);
         if (name != null && name.length() != 0) {
            return name;
         }
      } finally {
         return null;
      }

      return null;
   }

   @Override
   public final void setName(String name) {
      boolean encrypt = !Security.getInstance().isAddressBookExcludedFromContentProtection();
      this._nameEncoding = PersistentContent.encode(name, false, encrypt);
   }

   @Override
   public final RIMModel getAddressModelAt(int index) {
      return index >= 0 && index < this.size() ? this._members[index].getAddressModel() : null;
   }

   @Override
   public final Object getAddressCardModelAt(int index) {
      return index >= 0 && index < this.size() ? this._members[index].getAddressCardModel() : null;
   }

   @Override
   public final byte getAddressModelTypeAt(int index) {
      return index >= 0 && index < this.size() ? this._members[index].getType() : -1;
   }

   @Override
   public final Field getField(Object context) {
      long flags = 18014398509481984L;
      if (ContextObject.getFlag(context, 16)) {
         return (Field)(new Object("", this.getName()));
      }

      if (ContextObject.getFlag(context, 17)) {
         flags |= 64;
      }

      LabelField field = (LabelField)(new Object(
         ((StringBuffer)(new Object())).append(this.getName()).append(" (").append(AddressBookResources.getString(1006)).append(')').toString(), flags
      ));
      field.setCookie(this);
      return field;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final int getOrder(Object context) {
      if (ContextObject.getFlag(context, 24)) {
         return 15300;
      } else {
         return ContextObject.getFlag(context, 43) ? 6500 : 0;
      }
   }

   @Override
   public final Object copy() {
      return new GroupAddressCardModelImpl(this);
   }

   @Override
   public final boolean isValid() {
      return this.getName() != null && this.size() > 0;
   }

   @Override
   public final void warnUserSomeAddressesCannotReceive(String messageTypeString) {
      String[] messageType = new Object[]{messageTypeString};
      String text = MessageFormat.format(AddressBookResources.getString(1739), messageType);
      Dialog.alert(text);
   }

   @Override
   public final boolean isEditable() {
      return true;
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      if (keyRequested != -6544199576583918793L && keyRequested != -6544199576583918792L) {
         keyArray[index] = this.getName();
         return index + 1;
      } else {
         Array.resize(keyArray, index + 4);
         keyArray[index++] = null;
         keyArray[index++] = this.getName();
         keyArray[index++] = null;
         keyArray[index++] = null;
         return 4;
      }
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final Object getAt(int index) {
      return this._members[index];
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return ReadableListUtil.getAt(index, count, elements, destIndex, this);
   }

   @Override
   public final int size() {
      return this._members.length;
   }

   @Override
   public final long getLUID(Object context) {
      return this.getUID();
   }

   @Override
   public final Object makeReadOnly() {
      if (!ObjectGroup.isInGroup(this)) {
         ObjectGroup.createGroupIgnoreTooBig(this);
         PersistentObject.commit(this);
      }

      return this;
   }

   @Override
   public final Object makeReadWrite() {
      return ObjectGroup.isInGroup(this) ? ObjectGroup.expandGroup(this) : this;
   }

   @Override
   public final boolean isReadOnly() {
      return ObjectGroup.isInGroup(this);
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._nameEncoding, false, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      boolean readOnly = this.isReadOnly();
      GroupAddressCardModelImpl groupAddress = readOnly ? (GroupAddressCardModelImpl)this.makeReadWrite() : this;
      groupAddress._nameEncoding = PersistentContent.reEncode(groupAddress._nameEncoding, false, encrypt);
      return readOnly ? groupAddress.makeReadOnly() : null;
   }

   @Override
   public final String toString() {
      String name = this.getName();
      return name != null ? name : "";
   }

   private final void initializeGroup(Object initialData) {
      this.removeAll();
      Object obj = ContextObject.get(initialData, 254);
      if (!(initialData instanceof GroupAddressCardModelImpl)) {
         if (obj instanceof GroupAddressCardModelImpl) {
            this.initializeGroup(obj);
            return;
         }

         if (initialData != null) {
            ContextObject contextObject = ContextObject.verifyNonNull(initialData);
            Object test = contextObject.get(253);
            this.setName((String)(test != null ? test : ""));
            this._uid = contextObject.getIntegerData(UIDGenerator.getUID());
            return;
         }

         this._uid = UIDGenerator.getUID();
      } else {
         GroupAddressCardModelImpl other = (GroupAddressCardModelImpl)initialData;
         this._uid = other._uid;
         this.setName(other.getName());

         for (int i = 0; i < other._members.length; i++) {
            Object element = other._members[i];
            if (element instanceof Object) {
               this.add(((Copyable)element).copy());
            }
         }
      }
   }

   GroupAddressCardModelImpl(Object initialData) {
      this();
      this.initializeGroup(initialData);
   }

   GroupAddressCardModelImpl() {
   }

   @Override
   public final boolean equals(Object o) {
      return this == o ? true : o instanceof Object && ((AddressCardElement)o).getUID() == this._uid;
   }
}
