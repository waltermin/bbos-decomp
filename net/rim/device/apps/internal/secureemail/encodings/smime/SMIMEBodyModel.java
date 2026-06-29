package net.rim.device.apps.internal.secureemail.encodings.smime;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import net.rim.device.api.crypto.cms.CMSContext;
import net.rim.device.api.crypto.cms.CMSInputStream;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.transmission.rim.CMIMEParameters;
import net.rim.device.apps.internal.secureemail.SecureEmailBodyModel;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;
import net.rim.vm.Array;

public final class SMIMEBodyModel extends SecureEmailBodyModel implements Persistable {
   boolean _isSignedReceipt;
   boolean _isSignedReceiptRequest;
   boolean _isStoredAsBase64;
   public static final int SIGNED_RECEIPT_BIT = 1;
   public static final int SIGNED_RECEIPT_REQUEST_BIT = 2;
   public static final int IS_STORED_AS_BASE64_BIT = 4;

   public SMIMEBodyModel(byte[] text, Object initialData) {
      super(text, initialData);
      Hashtable securityEncodingParameterTable = this.getSecurityEncodingParameters();
      this._isStoredAsBase64 = securityEncodingParameterTable.get("nv") == null;
   }

   public SMIMEBodyModel(byte[] text, boolean isSignedReceipt, boolean isSignedReceiptRequest, boolean isStoredAsBase64, Object initialData) {
      super(text, initialData);
      this._isSignedReceipt = isSignedReceipt;
      this._isSignedReceiptRequest = isSignedReceiptRequest;
      this._isStoredAsBase64 = isStoredAsBase64;
   }

   public SMIMEBodyModel(
      byte[] text,
      boolean isSignedReceipt,
      boolean isSignedReceiptRequest,
      boolean isStoredAsBase64,
      byte[] securityEncoding,
      byte[] securityEncodingParameters
   ) {
      super(text, securityEncoding, securityEncodingParameters);
      this._isSignedReceipt = isSignedReceipt;
      this._isSignedReceiptRequest = isSignedReceiptRequest;
      this._isStoredAsBase64 = isStoredAsBase64;
   }

   @Override
   public final boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof SMIMEBodyModel)) {
         return false;
      }

      SMIMEBodyModel other = (SMIMEBodyModel)object;
      return super.equals(other)
         && this._isSignedReceipt == other._isSignedReceipt
         && this._isSignedReceiptRequest == other._isSignedReceiptRequest
         && this._isStoredAsBase64 == other._isStoredAsBase64;
   }

   public final void setIsSignedReceipt() {
      this._isSignedReceipt = true;
   }

   @Override
   protected final int getBestGuessEncodingAction() {
      try {
         InputStream inputStream = new ByteArrayInputStream(this.getBytes());
         if (this._isStoredAsBase64) {
            inputStream = new Base64InputStream(inputStream, true);
         }

         CMSContext cmsContext = CMSInputStream.getCMSContext(inputStream);
         return cmsContext.isEncrypted() ? 3 : 1;
      } finally {
         ;
      }
   }

   @Override
   public final String getEncodingString() {
      return SMIMEFactory.getInstance().getEncodingString();
   }

   @Override
   public final long getEncodingUID() {
      return SMIMEFactory.getInstance().getEncodingUID();
   }

   @Override
   protected final Object cloneImpl(Object context) {
      return new SMIMEBodyModel(
         this.getBytes(),
         this._isSignedReceipt,
         this._isSignedReceiptRequest,
         this._isStoredAsBase64,
         super._securityEncoding,
         super._securityEncodingParameters
      );
   }

   @Override
   protected final void writeCMIMEParameters(CMIMEParameters parameters) {
      super.writeCMIMEParameters(parameters);
      int smimeParameters = 0;
      smimeParameters |= this._isSignedReceipt ? 1 : 0;
      smimeParameters |= this._isSignedReceiptRequest ? 2 : 0;
      smimeParameters |= this._isStoredAsBase64 ? 4 : 0;
      parameters.addCMIMEInteger((byte)-8, smimeParameters);
   }

   @Override
   protected final int getMessageStatusIcon(boolean inbound, int status, boolean opened, boolean filed) {
      if (status == 1 || filed) {
         return -1;
      } else if (!inbound) {
         return -1;
      } else if (this._isSignedReceipt) {
         return opened ? 131077 : 131076;
      } else {
         return opened ? 131074 : 131073;
      }
   }

   @Override
   protected final boolean isTextMultiByte() {
      return false;
   }

   @Override
   public final SecureEmailFactory getSecureEmailFactory() {
      return SMIMEFactory.getInstance();
   }

   @Override
   protected final boolean getShowShortFormDefault() {
      return !SMIMEFactory.getInstance().getShowMessageDetails();
   }

   private final void checkForBase64Removal() {
      if (this._isStoredAsBase64 && this.getAvailableLength() != 0 && this.getLengthOnDevice() != 0 && !this.isMoreAvailable()) {
         Object ticket = PersistentContent.getTicket();
         if (ticket != null) {
            try {
               byte[] data = this.getBytes();
               ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
               Base64InputStream base64Stream = new Base64InputStream(byteStream, true);
               byte[] binaryData = new byte[data.length];
               int length = base64Stream.read(binaryData);
               Array.resize(binaryData, length);
               this._isStoredAsBase64 = false;
               int lengthOnDevice = this.getLengthOnDevice();
               this.setBytes(binaryData);
               this.setLengthOnDevice(lengthOnDevice);
            } finally {
               return;
            }
         }
      }
   }

   @Override
   public final void setLengthOnDevice(int lengthOnDevice) {
      super.setLengthOnDevice(lengthOnDevice);
      this.checkForBase64Removal();
   }

   @Override
   public final void setAvailableLength(int availableLength) {
      super.setAvailableLength(availableLength);
      this.checkForBase64Removal();
   }
}
