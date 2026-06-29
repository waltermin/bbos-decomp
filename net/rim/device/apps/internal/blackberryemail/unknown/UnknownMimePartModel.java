package net.rim.device.apps.internal.blackberryemail.unknown;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.icons.FileIcon;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEContentType;
import net.rim.device.apps.api.transmission.rim.CMIMEParameters;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.RIMMessagingMoreMessage;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;
import net.rim.device.apps.internal.blackberryemail.email.MorePartModel;
import net.rim.device.internal.i18n.UnicodeServiceRegistry;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.vm.Array;

public class UnknownMimePartModel implements PersistableRIMModel, ConversionProvider, FieldProvider, MorePartModel, EncryptableProvider {
   private Object _dataEncoding;
   private int _lengthOnDevice;
   private Object _contentTypeEncoding;
   private int _trueLength;
   private int _availableLength;
   private int _morePartID;
   private int _flags;
   private Object _nameEncoding;
   protected boolean _isHidden;
   private boolean _isEncoded;
   public static final int MORE_REQUEST_SENT = 1;
   public static final int MORE_REQUEST_RECEIVED = 2;
   private static final int UNKNOWN_ATTACHMENT_HEADER_LENGTH = 10;
   private static final byte[] UNKNOWN_ATTACHMENT_TRAILER = new byte[]{
      0, 97, 112, 112, 108, 105, 99, 97, 116, 105, 111, 110, 47, 111, 99, 116, 101, 116, 45, 115, 116, 114, 101, 97, 109, 0
   };
   private static final int UNKNOWN_ATTACHMENT_TRAILER_LENGTH = UNKNOWN_ATTACHMENT_TRAILER.length;

   public void setData(byte[] data) {
      this._dataEncoding = PersistentContent.encode(data);
      this._lengthOnDevice = data.length;
   }

   public byte[] getData() {
      try {
         return PersistentContent.decodeByteArray(this._dataEncoding);
      } finally {
         ;
      }
   }

   public byte[] getNameBytes() {
      try {
         return PersistentContent.decodeByteArray(this._nameEncoding);
      } finally {
         return "<Content Protection is enabled>".getBytes();
      }
   }

   protected void setNameBytes(byte[] name) {
      this._nameEncoding = PersistentContent.encode(name);
      this._isEncoded = false;
   }

   public String getFilename() {
      byte[] nameBytes = this.getNameBytes();
      String name = null;
      if (nameBytes == null) {
         return name;
      }

      if (this._isEncoded) {
         return ConverterUtilities.readStringEncoded(nameBytes, 0, nameBytes.length, false);
      }

      try {
         return new String(nameBytes, 0, nameBytes.length, "windows-1252\r");
      } finally {
         name = new String(nameBytes, 0, nameBytes.length);
         return name;
      }
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (!(target instanceof RIMMessagingOutgoingMessage)) {
         if (ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 19)) {
            int fieldType = this._isHidden ? 24 : 22;
            byte[] name = this.getNameBytes();
            if (this._isEncoded) {
               fieldType |= -128;
               UnicodeServiceRegistry ur = UnicodeServiceRegistry.getInstance();
               if (ur != null) {
                  ur.setFlags(ur.getFlags() | 1);
               }
            }

            int namelen = name != null ? name.length : 0;
            byte[] result = new byte[10 + namelen + UNKNOWN_ATTACHMENT_TRAILER_LENGTH];
            result[4] = (byte)this._lengthOnDevice;
            result[5] = (byte)(this._lengthOnDevice >> 8);
            result[6] = (byte)(this._lengthOnDevice >> 16);
            result[7] = (byte)(this._lengthOnDevice >> 24);
            result[9] = 1;
            if (name != null) {
               System.arraycopy(name, 0, result, 10, namelen);
            }

            System.arraycopy(UNKNOWN_ATTACHMENT_TRAILER, 0, result, namelen + 10, UNKNOWN_ATTACHMENT_TRAILER_LENGTH);
            int oldLength = result.length;
            DataBuffer buffer = new DataBuffer();
            CMIMEParameters parameters = new CMIMEParameters(buffer, 2, 5);
            this.writeCMIMEParameters(parameters);
            buffer.writeByte(0);
            if (this._dataEncoding != null) {
               buffer.writeByteArray(PersistentContent.decodeByteArray(this._dataEncoding));
            }

            int bufferLength = buffer.getLength();
            Array.resize(result, oldLength + 4 + bufferLength);
            result[oldLength] = (byte)bufferLength;
            result[oldLength + 1] = (byte)(bufferLength >> 8);
            result[oldLength + 2] = (byte)(bufferLength >> 16);
            result[oldLength + 3] = (byte)(bufferLength >> 24);
            buffer.rewind();
            buffer.read(result, oldLength + 4, bufferLength);
            ((SyncBuffer)target).addBytes(fieldType, result);
            return true;
         } else {
            return false;
         }
      } else {
         RIMMessagingOutgoingMessage outgoingTransmission = (RIMMessagingOutgoingMessage)target;
         byte[] name = this.getNameBytes();
         return false;
      }
   }

   public String getContentType() {
      try {
         return PersistentContent.decodeString(this._contentTypeEncoding);
      } finally {
         ;
      }
   }

   public void setContentType(String ctype) {
      this._contentTypeEncoding = PersistentContent.encode(ctype);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void setFilename(String filename) {
      if (filename != null) {
         if (ConverterUtilities.isIntellisyncCompatible(filename)) {
            try {
               this.setNameBytes(filename.getBytes("windows-1252\r"));
            } finally {
               this.setNameBytes(filename.getBytes());
               return;
            }
         } else {
            byte[] nameBytes = null;
            boolean var6 = false /* VF: Semaphore variable */;

            try {
               var6 = true;
               nameBytes = filename.getBytes("UTF-8\r");
               if (nameBytes == null) {
                  var6 = false;
               } else {
                  byte[] temp = new byte[nameBytes.length + 1];
                  temp[0] = 0;
                  System.arraycopy(nameBytes, 0, temp, 1, nameBytes.length);
                  nameBytes = temp;
                  this.setNameBytes(nameBytes);
                  this._isEncoded = true;
               }
            } finally {
               if (var6) {
                  this.setNameBytes(filename.getBytes());
                  return;
               }
            }
         }
      }
   }

   protected void writeCMIMEParameters(CMIMEParameters parameters) {
      parameters.addCMIMEInteger((byte)-13, this._trueLength);
      parameters.addCMIMEInteger((byte)-16, this._availableLength);
      parameters.addCMIMEInteger((byte)-15, this._morePartID);
      byte[] name = this.getNameBytes();
      if (name != null) {
         if (this._isEncoded) {
            parameters.add((byte)-6, name);
         } else {
            parameters.add((byte)-14, name);
         }
      }

      if (this._contentTypeEncoding != null) {
         String contentType = PersistentContent.decodeString(this._contentTypeEncoding);
         parameters.add((byte)1, contentType.getBytes());
      }
   }

   public boolean isViewable() {
      return false;
   }

   @Override
   public void setLengthOnDevice(int lengthOnDevice) {
      this._lengthOnDevice = lengthOnDevice;
   }

   @Override
   public int getTrueLength() {
      return this._trueLength;
   }

   @Override
   public void setTrueLength(int trueLength) {
      this._trueLength = trueLength;
   }

   @Override
   public int getAvailableLength() {
      return this._availableLength;
   }

   @Override
   public void setAvailableLength(int availableLength) {
      this._availableLength = availableLength;
   }

   @Override
   public boolean isMoreAvailable() {
      return this._availableLength > 0 && this._lengthOnDevice < this._availableLength;
   }

   @Override
   public boolean isTruncated() {
      return this._availableLength < this._trueLength;
   }

   @Override
   public int getLengthOnDevice() {
      return this._lengthOnDevice;
   }

   @Override
   public boolean getMoreRequestSent() {
      return (this._flags & 1) != 0;
   }

   @Override
   public void setMoreRequestSent() {
      this._flags |= 1;
   }

   @Override
   public void clearMoreRequestSent() {
      this._flags &= -2;
   }

   @Override
   public boolean isAutoMoreAvailable() {
      return true;
   }

   @Override
   public boolean suppressNotification() {
      return false;
   }

   @Override
   public void setMorePartID(int morePartID) {
      this._morePartID = morePartID;
   }

   @Override
   public int getMorePartID() {
      return this._morePartID;
   }

   @Override
   public Field getField(Object context) {
      String name = this.getFilename();
      if (name != null && name.length() > 0) {
         StringBuffer buffer = new StringBuffer();
         String rimPrefix = "x-rimdevice";
         if (StringUtilities.startsWithIgnoreCase(name, rimPrefix, 1701707776)) {
            buffer.append(name.substring(rimPrefix.length()));
         } else {
            buffer.append(name);
         }

         int length = this.getTrueLength();
         if (length > 0) {
            buffer.append(' ');
            buffer.append('(');
            if (length < 1024) {
               buffer.append(length);
            } else {
               buffer.append(length / 1024);
               buffer.append('K');
            }

            buffer.append(')');
         }

         HorizontalFieldManager hfm = new HorizontalFieldManager();
         ImageField iconField = new ImageField(65536);
         iconField.setImage(FileIcon.getFileIconImage(this.getContentType()));
         hfm.add(iconField);
         Field descriptionField = new RichTextField(buffer.toString());
         descriptionField.setEditable(false);
         hfm.add(descriptionField);
         hfm.setCookie(this);
         return hfm;
      } else {
         return null;
      }
   }

   @Override
   public int getOrder(Object context) {
      return 6500;
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      return false;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public void receiveMore(Object context, Object moreObject) {
      if (moreObject instanceof RIMMessagingMoreMessage) {
         RIMMessagingMoreMessage incomingTransmission = (RIMMessagingMoreMessage)moreObject;
         byte[] moreData = incomingTransmission.getRawBytes();
         this._dataEncoding = PersistentContent.encodeAndAppend(moreData, this._dataEncoding);
         this._lengthOnDevice += moreData.length;
         this._availableLength = incomingTransmission.getConvertedAvailableLength();
      }
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._nameEncoding, compress, encrypt)
         && PersistentContent.checkEncoding(this._dataEncoding, compress, encrypt)
         && PersistentContent.checkEncoding(this._contentTypeEncoding, compress, encrypt);
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this._nameEncoding = PersistentContent.reEncode(this._nameEncoding, compress, encrypt);
      this._dataEncoding = PersistentContent.reEncode(this._dataEncoding, compress, encrypt);
      this._contentTypeEncoding = PersistentContent.reEncode(this._contentTypeEncoding, compress, encrypt);
      return null;
   }

   protected UnknownMimePartModel(Object initialData) {
      ContextObject contextObject = ContextObject.castOrCreate(initialData);
      Object inputBytesObject = contextObject.get(8849067667159082262L);
      if (inputBytesObject instanceof byte[]) {
         byte[] data = (byte[])inputBytesObject;
         this.setData(data);
      }

      Object parametersObject = contextObject.get(-7353832199068708928L);
      if (parametersObject instanceof Parameters) {
         Parameters parameters = (Parameters)parametersObject;
         byte[] contentBytes = parameters.getFirst((byte)-13);
         if (contentBytes != null) {
            this._trueLength = CMIMEUtilities.decodeInteger(contentBytes);
         }

         contentBytes = parameters.getFirst((byte)-16);
         if (contentBytes != null) {
            this._availableLength = CMIMEUtilities.decodeInteger(contentBytes);
         }

         this._morePartID = CMIMEUtilities.decodeInteger(parameters, (byte)-15);
         byte[] name = parameters.getFirst((byte)-14);
         boolean isEncoded = false;
         if (name == null) {
            name = parameters.getFirst((byte)-6);
            if (name != null) {
               isEncoded = true;
               String s = null;

               try {
                  label121:
                  try {
                     s = (String)CMIMEUtilities.getTextObject(name, true);
                     if (s != null) {
                        this.setFilename(s);
                        name = this.getNameBytes();
                        isEncoded = this._isEncoded;
                     }
                  } finally {
                     break label121;
                  }
               } finally {
                  if (s == null) {
                     byte[] var21 = null;
                     isEncoded = false;
                  }
               }
            }
         }

         String contentType = CMIMEContentType.getFullType(parameters.getFirst((byte)1));
         this._contentTypeEncoding = PersistentContent.encode(contentType);
         if (name == null) {
            name = contentType.getBytes();
         }

         this.setNameBytes(name);
         this._isEncoded = isEncoded;
      }

      Boolean hiddenBoolean = (Boolean)contextObject.get(4086083307293257364L);
      if (hiddenBoolean != null) {
         this._isHidden = hiddenBoolean;
      }
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof UnknownMimePartModel)) {
         return false;
      } else {
         UnknownMimePartModel otherModel = (UnknownMimePartModel)object;
         if (otherModel._isHidden != this._isHidden) {
            return false;
         } else {
            return otherModel._lengthOnDevice != this._lengthOnDevice ? false : Arrays.equals(otherModel.getNameBytes(), this.getNameBytes());
         }
      }
   }
}
