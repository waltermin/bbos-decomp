package net.rim.device.apps.internal.sms;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.system.SMSParameters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringMatch;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.AddressMatch;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.messaging.messagelist.MessagePartsProvider;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.internal.sms.resources.SMSResources;
import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.Array;

public class SMSPayloadModel implements PersistableRIMModel, MatchProvider, EncryptableProvider {
   public boolean _inbound;
   public long _creationDate = System.currentTimeMillis();
   public long _transmissionDate;
   public byte[] _byteFields;
   public byte[] _userDataHeader;
   public int[] _segmentIDs;
   public int _imsiCRC;
   public int _refNumber;
   public Object[] _segments;
   private PersistableRIMModel[] _addresses;
   private int[] _TONAndNPI;
   private PersistableRIMModel _callbackAddress;
   public String _scAddress;
   public static final int BYTE_FIELD_MESSAGE_CODING = 0;
   public static final int BYTE_FIELD_MESSAGE_CLASS = 1;
   public static final int BYTE_FIELD_PROTOCOL_MEANING = 2;
   public static final int BYTE_FIELD_PROTOCOL_ID = 3;
   public static final int BYTE_FIELD_UNUSED = 4;
   public static final int BYTE_FIELD_PRIVACY = 5;
   public static final int BYTE_FIELD_PRIORITY = 6;
   public static final int BYTE_FIELD_LANGUAGE = 7;
   public static final int BYTE_FIELD_ADDRESS_TON_NOW_OBSOLETE = 8;
   public static final int BYTE_FIELD_ADDRESS_NPI_NOW_OBSOLETE = 9;
   public static final int BYTE_FIELD_SC_ADDRESS_TON = 10;
   public static final int BYTE_FIELD_SC_ADDRESS_NPI = 11;
   public static final int NUM_BYTE_FIELDS = 12;
   public static final int MAXIMUM_NUMBER_OF_ADDRESSES = 10;
   public static final int DEFAULT_NPI = 0;

   public int getTON(int index) {
      return index >= this._TONAndNPI.length ? 0 : this._TONAndNPI[index] >> 16 & 65535;
   }

   public int getNPI(int index) {
      return index >= this._TONAndNPI.length ? 0 : this._TONAndNPI[index] & 65535;
   }

   @Override
   public int match(Object criteria) {
      SearchCriterion crit = (SearchCriterion)criteria;
      switch (crit.getType()) {
         case 1:
         case 21:
            StringMatch matcher = (StringMatch)crit.getValue();
            String text = this.getText();
            if (text != null && matcher.indexOf(text) >= 0) {
               return 1;
            }

            return 0;
         case 4:
            if (this._inbound) {
               return this.performAddressMatch(crit);
            }

            return -1;
         case 5:
            return this.performAddressMatch(crit);
         case 6:
            if (this._inbound) {
               return -1;
            }

            return this.performAddressMatch(crit);
         default:
            return -1;
      }
   }

   public void setTONAndNPI(int[] data) {
      this._TONAndNPI = data;
   }

   public PersistableRIMModel[] getAddresses() {
      return this._addresses;
   }

   public PersistableRIMModel getFirstAddress() {
      return this._addresses.length > 0 ? this._addresses[0] : null;
   }

   protected void addAddress(PersistableRIMModel newAddress, int TON, int NPI) {
      if (newAddress instanceof Copyable) {
         Arrays.add(this._addresses, (PersistableRIMModel)((Copyable)newAddress).copy());
         Arrays.add(this._TONAndNPI, this.meshTwoInts(TON, NPI));
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected int removeAddress(RIMModel address) {
      int index = Arrays.getIndex(this._addresses, address);
      if (index < this._addresses.length) {
         Arrays.removeAt(this._addresses, index);
         Arrays.removeAt(this._TONAndNPI, index);
         return index;
      } else {
         return -1;
      }
   }

   protected void resetAddresses() {
      this._addresses = new PersistableRIMModel[0];
      this._TONAndNPI = new int[0];
   }

   protected int changeAddress(RIMModel oldAddress, PersistableRIMModel newAddress) {
      if (newAddress instanceof Copyable) {
         for (int i = this._addresses.length - 1; i > -1; i--) {
            if (this._addresses[i].equals(oldAddress)) {
               this._addresses[i] = (PersistableRIMModel)((Copyable)newAddress).copy();
               this._TONAndNPI[i] = this.meshTwoInts(getTON(newAddress), 0);
               return i;
            }
         }

         return -1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected int copyAddresses(SMSModel sourceModel) {
      SMSPayloadModel payloadModel = sourceModel._payload;
      return this.addAddresses(payloadModel._addresses, payloadModel._TONAndNPI);
   }

   public boolean copySCAddress(SMSPayloadModel model) {
      String scAddress = model._scAddress;
      if (scAddress == null) {
         return false;
      }

      this._scAddress = scAddress;
      this.setByteField(10, model.getByteField(10));
      this.setByteField(11, model.getByteField(11));
      return true;
   }

   public PersistableRIMModel getCallbackAddress() {
      return this._callbackAddress;
   }

   public void setCallbackAddress(PersistableRIMModel newAddress) {
      this._callbackAddress = newAddress;
   }

   public int getByteField(int field) {
      return this._byteFields[field] & 0xFF;
   }

   public void setByteField(int field, int value) {
      this._byteFields[field] = (byte)(value & 0xFF);
   }

   public byte getPaintPriority() {
      switch (this.getByteField(6)) {
         case 1:
            if (this.isReplyRequested()) {
               return 2;
            }

            return 1;
         case 2:
         case 3:
         default:
            return 2;
      }
   }

   public char getPriorityChar() {
      switch (this.getByteField(6)) {
         case 1:
            if (this.isReplyRequested()) {
               return '\uf3e8';
            }

            return '\uf3e7';
         case 2:
         case 3:
         default:
            return '\uf3e8';
      }
   }

   public boolean isReplyRequested() {
      return this._inbound && this._scAddress != null;
   }

   public void setByteFields(SMSPacketHeader header) {
      this.setByteField(0, header.getMessageCoding());
      this.setByteField(1, header.getMessageClass());
      this.setByteField(2, header.getProtocolMeaning());
      this.setByteField(3, header.getProtocolId());
      this.setByteField(5, header.getPrivacy());
      this.setByteField(6, header.getPriority());
      this.setByteField(7, header.getLanguage());
      this.setByteField(10, header.getSCType());
      this.setByteField(11, header.getSCPlan());
   }

   public int getData0() {
      int data = this.getByteField(0) & 0xFF;
      data |= (this.getByteField(1) & 0xFF) << 8;
      data |= (this.getByteField(2) & 0xFF) << 16;
      return data | (this.getByteField(3) & 0xFF) << 24;
   }

   public void setData0(int value) {
      this.setByteField(0, value & 0xFF);
      this.setByteField(1, value >> 8 & 0xFF);
      this.setByteField(2, value >> 16 & 0xFF);
      this.setByteField(3, value >> 24 & 0xFF);
   }

   public int getData1() {
      int data = (this.getByteField(5) & 0xFF) << 8;
      data |= (this.getByteField(6) & 0xFF) << 16;
      return data | (this.getByteField(7) & 0xFF) << 24;
   }

   public void setData1(int value) {
      this.setByteField(5, value >> 8 & 0xFF);
      this.setByteField(6, value >> 16 & 0xFF);
      this.setByteField(7, value >> 24 & 0xFF);
   }

   public String getText() {
      return this.getText(false);
   }

   public long getDisplayDate() {
      switch (this.getMessageCoding()) {
         case 3:
            return this._creationDate;
         case 4:
         case 5:
         default:
            return this._transmissionDate;
      }
   }

   public String getTextSummary() {
      if (this._segments == null) {
         return SMSResources.getString(385);
      }

      String text = this.getText(0);
      return text == null ? SMSResources.getString(385) : StringUtilities.removeLineBreaksInString(text);
   }

   public String getText(boolean firstSegmentOnly) {
      if (this._segments == null) {
         return null;
      }

      StringBuffer result = new StringBuffer();
      StringBuffer sb = new StringBuffer();
      int length = this._segments.length;

      for (int i = 0; i < length; i++) {
         String s = this.getText(i);
         if (s == null) {
            if (sb.length() != 0) {
               result.append(sb);
               sb.setLength(0);
               result.append(SMSResources.getString(385));
            } else {
               if (i != 0) {
                  continue;
               }

               result.append(SMSResources.getString(385));
            }
         } else {
            sb.append(s);
         }

         if (firstSegmentOnly) {
            if (s == null) {
               return SMSResources.getString(385);
            }

            return s;
         }
      }

      if (sb.length() != 0) {
         result.append(sb);
      }

      return result.toString();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void setText(String text) {
      byte[] data;
      try {
         data = text.getBytes(SMSService.getSmsEncoder(this.getMessageCoding()));
      } catch (Throwable var5) {
         throw new RuntimeException(ex.getMessage());
      }

      this.setData(data);
   }

   public byte[] getData() {
      try {
         if (this._segments == null) {
            return null;
         }

         byte[] buf = new byte[0];

         for (int i = 0; i < this._segments.length; i++) {
            Object encoding = this._segments[i];
            if (encoding == null) {
               return null;
            }

            byte[] data = PersistentContent.decodeByteArray(encoding);
            int length = buf.length;
            Array.resize(buf, length + data.length);
            System.arraycopy(data, 0, buf, length, data.length);
         }

         return buf;
      } finally {
         ;
      }
   }

   public int getSegmentCount() {
      return this._segments == null ? 0 : this._segments.length;
   }

   boolean addSegment(int totalSegments, int segmentNumber, int segmentID, byte[] data) {
      if (this._segments == null) {
         this._segments = new Object[totalSegments];
         this._segmentIDs = new int[totalSegments];
         Arrays.fill(this._segmentIDs, 65535);
      }

      segmentNumber--;

      try {
         if (this._segments[segmentNumber] != null) {
            return false;
         }

         this._segments[segmentNumber] = PersistentContent.encode(data, true, true);
         this._segmentIDs[segmentNumber] = segmentID;
         return true;
      } finally {
         ;
      }
   }

   public int getMessageCoding() {
      return this.getByteField(0);
   }

   void setData(byte[] data) {
      this._segmentIDs = new int[1];
      this._segments = new Object[1];
      this._segmentIDs[0] = 65535;
      this._segments[0] = PersistentContent.encode(data, true, true);
   }

   protected int[] getTONAndNPI() {
      return this._TONAndNPI;
   }

   public Field getMessageBodyField() {
      if (this._segments == null) {
         return new RichTextField(18014398509481984L);
      }

      VerticalFieldManager field = new VerticalFieldManager(1152921504606846976L);
      field.setCookie(this);
      if (this.messageBodyDecodeFailed()) {
         field.add(new SystemMessageField(395));
      }

      StringBuffer sb = new StringBuffer();
      int length = this._segments.length;

      for (int i = 0; i < length; i++) {
         if (this._segments[i] == null) {
            if (sb.length() != 0) {
               ActiveRichTextField text = new ActiveRichTextField(sb.toString(), 18014398509481984L);
               field.add(text);
               text.setAdjustAlignments(true);
               sb.setLength(0);
               field.add(new SystemMessageField(385));
            } else if (i == 0) {
               field.add(new SystemMessageField(385));
            }
         } else {
            sb.append(this.getText(i));
         }
      }

      if (sb.length() != 0) {
         ActiveRichTextField text = new ActiveRichTextField(sb.toString(), 18014398509481984L);
         field.add(text);
         text.setAdjustAlignments(true);
      }

      return field;
   }

   public boolean checkSegmentID(int id) {
      if (this._segments == null) {
         return false;
      }

      for (int i = this._segments.length - 1; i >= 0; i--) {
         if (this._segmentIDs[i] == id) {
            return true;
         }
      }

      return false;
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      if (this._segments != null) {
         for (int i = this._segments.length - 1; i >= 0; i--) {
            this._segments[i] = PersistentContent.reEncode(this._segments[i], compress, encrypt);
         }
      }

      if (this._addresses != null) {
         for (int i = this._addresses.length - 1; i > -1; i--) {
            Object address = this._addresses[i];
            if (address instanceof EncryptableProvider) {
               EncryptableProvider encryptable = (EncryptableProvider)address;
               PersistableRIMModel newAddress = (PersistableRIMModel)encryptable.reCrypt(compress, encrypt);
               if (newAddress != null) {
                  this._addresses[i] = newAddress;
               }
            }
         }
      }

      return null;
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      if (this._segments != null) {
         for (int i = this._segments.length - 1; i >= 0; i--) {
            if (!PersistentContent.checkEncoding(this._segments[i], compress, encrypt)) {
               return false;
            }
         }
      }

      if (this._addresses != null) {
         for (int i = this._addresses.length - 1; i > -1; i--) {
            Object address = this._addresses[i];
            if (address instanceof EncryptableProvider) {
               EncryptableProvider encryptable = (EncryptableProvider)address;
               if (!encryptable.checkCrypt(compress, encrypt)) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private int addAddresses(PersistableRIMModel[] newAddresses, int[] newTONandNPI) {
      int oldSize = this._addresses.length;
      int newSize = oldSize + newAddresses.length;
      Array.resize(this._addresses, newSize);
      Array.resize(this._TONAndNPI, newSize);
      int i = oldSize;
      int j = 0;

      while (i < newSize) {
         this._TONAndNPI[i] = newTONandNPI[j];
         PersistableRIMModel newAddress = newAddresses[j++];
         if (!(newAddress instanceof Copyable)) {
            throw new IllegalArgumentException();
         }

         this._addresses[i] = (PersistableRIMModel)((Copyable)newAddress).copy();
         i++;
      }

      return newSize;
   }

   private String getText(int segment) {
      try {
         Object encoding = this._segments[segment];
         if (encoding == null) {
            return null;
         }

         byte[] data = PersistentContent.decodeByteArray(encoding);
         return SMSService.decodeSMSData(this.getMessageCoding(), data);
      } finally {
         ;
      }
   }

   public static int getPreferredMessageCoding() {
      if ((RadioInfo.getSupportedWAFs() & 2) != 0 && (RadioInfo.getActiveWAFs() & 1) != 0) {
         return 0;
      }

      int modelCoding = 0;

      try {
         SMSParameters params = new SMSParameters();
         RadioInternal.getDefaultSMSParameters(params);
         modelCoding = params.getMessageCoding();
         if (modelCoding == 1) {
            return 0;
         }
      } finally {
         return modelCoding;
      }

      return modelCoding;
   }

   public static final int getTON(PersistableRIMModel address) {
      return address instanceof EmailAddressModel ? 100 : 0;
   }

   private boolean messageBodyDecodeFailed() {
      return this.getByteField(0) == 1 && this.getByteField(1) == 2 && this.getByteField(3) >= 24 && this.getByteField(3) <= 30 && this.getByteField(2) == 32;
   }

   SMSPayloadModel(Object initialData) {
      this.resetAddresses();
      if (!(initialData instanceof SMSModel)) {
         this._inbound = ContextObject.getFlag(initialData, 38);
         this._transmissionDate = this._creationDate;
         this._byteFields = new byte[12];
         this.setByteField(0, getPreferredMessageCoding());
         if (initialData instanceof MessagePartsProvider) {
            MessagePartsProvider m = (MessagePartsProvider)initialData;
            StringBuffer body = new StringBuffer();
            String subject = m.getSubject();
            if (subject != null && subject.length() > 0) {
               body.append(subject);
            }

            String originalMessageBody = m.getBody();
            if (originalMessageBody != null && originalMessageBody.length() > 0) {
               body.append('\n');
               body.append(originalMessageBody);
            }

            if (body.length() > 0) {
               this.setText(body.toString());
            }
         }
      } else {
         SMSPayloadModel payload = ((SMSModel)initialData)._payload;
         this._byteFields = Arrays.copy(payload._byteFields);
         this._userDataHeader = Arrays.copy(payload._userDataHeader);
         if (payload._segments != null) {
            this._segments = new Object[payload._segments.length];

            for (int i = 0; i < payload._segments.length; i++) {
               if (payload._segments[i] != null) {
                  this._segments[i] = PersistentContent.copyEncoding(payload._segments[i]);
               }
            }
         }

         this.addAddresses(payload._addresses, payload._TONAndNPI);
         if (payload._callbackAddress instanceof Copyable) {
            this._callbackAddress = (PersistableRIMModel)((Copyable)payload._callbackAddress).copy();
         }

         this._scAddress = payload._scAddress;
      }
   }

   private int meshTwoInts(int a, int b) {
      return a << 16 & -65536 | b & 65535;
   }

   private int performAddressMatch(SearchCriterion crit) {
      if (this._addresses != null) {
         for (int i = this._addresses.length - 1; i > -1; i--) {
            RIMModel address = this._addresses[i];
            if (address instanceof EmailAddressModel) {
               Object addressCard = AddressBookServices.reverseLookup(address);
               if (addressCard instanceof AddressCardModel) {
                  if (AddressMatch.match((AddressCardModel)addressCard, crit) == 1) {
                     return 1;
                  }
                  continue;
               }
            }

            if (Match.performMatch(address, crit) == 1) {
               return 1;
            }
         }
      }

      return 0;
   }
}
