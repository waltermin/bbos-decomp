package net.rim.device.apps.api.transmission.rim;

import com.sun.cldc.i18n.Helper;
import java.util.Vector;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.apps.api.transmission.Parameters;

public class RIMMessagingMessage extends RIMMessagingTransmission {
   private Vector _attachmentObjects;
   private Vector _attachmentParameters;
   private Vector _attachmentTypes;
   private Object _textObject;
   private Parameters _textParameters;
   private String _textType;
   protected CMIMEParameters _headerParameters;
   protected CMIMEParameters _bodyParameters;
   protected boolean _isEncoded;
   protected byte _encodingCode = -1;

   public RIMMessagingMessage() {
      this._headerParameters = new CMIMEParameters(16, 8);
      this._bodyParameters = new CMIMEParameters(5, 2);
      this._textObject = null;
      this._textParameters = new Parameters(5, 5);
      this._textType = CMIMEUtilities.getTextContentType();
      this.updateEncoding();
   }

   private void initializeAttachments() {
      this._attachmentObjects = (Vector)(new Object());
      this._attachmentParameters = (Vector)(new Object());
      this._attachmentTypes = (Vector)(new Object());
   }

   public void insertAttachment(int indexInt, Object attachmentObject, Parameters attachmentParameters, String attachmentTypeString) {
      if (this._attachmentObjects == null) {
         this.initializeAttachments();
      }

      this._attachmentObjects.insertElementAt(attachmentObject, indexInt);
      this._attachmentParameters.insertElementAt(attachmentParameters, indexInt);
      this._attachmentTypes.insertElementAt(attachmentTypeString, indexInt);
   }

   public void addAttachment(Object attachmentObject, Parameters attachmentParameters, String attachmentTypeString) {
      if (this._attachmentObjects == null) {
         this.initializeAttachments();
      }

      this._attachmentObjects.addElement(attachmentObject);
      this._attachmentParameters.addElement(attachmentParameters);
      this._attachmentTypes.addElement(attachmentTypeString);
   }

   public void removeAttachment(int indexInt) {
      if (this._attachmentObjects != null) {
         this._attachmentObjects.removeElementAt(indexInt);
         this._attachmentParameters.removeElementAt(indexInt);
         this._attachmentTypes.removeElementAt(indexInt);
      }
   }

   public Object getAttachment(int indexInt) {
      return this._attachmentObjects != null ? this._attachmentObjects.elementAt(indexInt) : null;
   }

   public Parameters getAttachmentParameters(int indexInt) {
      return this._attachmentParameters != null ? (Parameters)this._attachmentParameters.elementAt(indexInt) : null;
   }

   public String getAttachmentType(int indexInt) {
      return (String)(this._attachmentTypes != null ? this._attachmentTypes.elementAt(indexInt) : null);
   }

   public int getAttachmentCount() {
      return this._attachmentObjects != null ? this._attachmentObjects.size() : 0;
   }

   public void setText(String aString) {
      this.setText(aString, null, null);
   }

   public void setText(Object textObject, Parameters textParameters, String textTypeString) {
      this._textObject = textObject;
      this._textParameters = textParameters;
      this._textType = textTypeString != null ? textTypeString : CMIMEUtilities.getTextContentType();
      this.updateEncoding();
   }

   public byte getEncoding() {
      return this._encodingCode;
   }

   public void setEncodingHints(byte encodingHint) {
      this._encodingCode = CMIMEUtilities.replaceHints(this._encodingCode, encodingHint);
   }

   protected void setEncoding(byte encoding) {
      if (encoding != -1 && !this._isEncoded) {
         if ((encoding & 128) == 128) {
            encoding = (byte)(encoding & -129);
         }

         String encodingName = CMIMEUtilities.getEncoding(encoding);
         if (encodingName != null && encodingName.length() > 0 && Helper.isSupportedEncoding(encodingName)) {
            String charset = "charset";
            if (this._textType == null || this._textType.length() <= 0) {
               this._textType = CMIMEUtilities.getTextContentType();
            }

            StringBuffer s = (StringBuffer)(new Object(CMIMEContentType.getBaseType(this._textType)));
            s.append(';');
            s.append(charset);
            s.append('=');
            s.append(encodingName);
            int index = this._textType.indexOf(charset);
            index = index > 0 ? index + 7 : 0;
            if (index < this._textType.length()) {
               index = this._textType.indexOf(59, index);
               if (index > 0) {
                  s.append(this._textType.substring(index, this._textType.length()));
               }
            }

            this._textType = s.toString();
            this._encodingCode = encoding;
            this._isEncoded = true;
         }
      }
   }

   public boolean isEncoded() {
      return this._isEncoded;
   }

   private void updateEncoding() {
      byte existingHints = 0;
      if (this._encodingCode != -1) {
         existingHints = (byte)(this._encodingCode & 112);
      }

      this._encodingCode = CMIMEUtilities.parseEncoding(this._textType);
      if (existingHints != 0) {
         this._encodingCode |= existingHints;
      } else {
         this._encodingCode = CMIMEUtilities.addHints(this._encodingCode);
      }

      this._isEncoded = this._encodingCode != -1;
   }

   public Object getText() {
      return this._textObject;
   }

   public Parameters getTextParameters() {
      return this._textParameters;
   }

   public String getTextType() {
      return this._textType;
   }

   public boolean hasText() {
      return this._textObject != null;
   }

   public void addTo(String addressString, String friendlyString) {
      this._headerParameters.addCMIMEEmailAddress((byte)4, addressString, friendlyString, this._isEncoded, this._encodingCode);
   }

   public String[][] getTo() {
      return this.getFieldAddresses((byte)4);
   }

   public void addCc(String addressString, String friendlyString) {
      this._headerParameters.addCMIMEEmailAddress((byte)5, addressString, friendlyString, this._isEncoded, this._encodingCode);
   }

   public String[][] getCc() {
      return this.getFieldAddresses((byte)5);
   }

   public void addBcc(String addressString, String friendlyString) {
      this._headerParameters.addCMIMEEmailAddress((byte)6, addressString, friendlyString, this._isEncoded, this._encodingCode);
   }

   public String[][] getBcc() {
      return this.getFieldAddresses((byte)6);
   }

   public void addFrom(String addressString, String friendlyString) {
      this._headerParameters.addCMIMEEmailAddress((byte)1, addressString, friendlyString, this._isEncoded, this._encodingCode);
   }

   public void addFrom(String addressString, String friendlyString, boolean encode) {
      this._headerParameters.addCMIMEEmailAddress((byte)1, addressString, friendlyString, encode, this._encodingCode);
   }

   public String[][] getFrom() {
      return this.getFieldAddresses((byte)1);
   }

   public void addReplyTo(String addressString, String friendlyString) {
      this._headerParameters.addCMIMEEmailAddress((byte)3, addressString, friendlyString, this._isEncoded, this._encodingCode);
   }

   public String[][] getReplyTo() {
      return this.getFieldAddresses((byte)3);
   }

   public void setSender(String addressString, String friendlyString) {
      this._headerParameters.addCMIMEEmailAddress((byte)2, addressString, friendlyString, this._isEncoded, this._encodingCode);
   }

   public String[] getSender() {
      byte[] rawData = this._headerParameters.getFirst((byte)2);
      if (rawData != null) {
         return CMIMEUtilities.decodeAddress(rawData, false);
      }

      rawData = this._headerParameters.getFirst((byte)-126);
      if (rawData != null && rawData.length > 0) {
         this.setEncoding(rawData[0]);
      }

      return CMIMEUtilities.decodeAddress(rawData, true);
   }

   public void addOriginalRecipient(String addressString, String friendlyString) {
      this._headerParameters.addCMIMEEmailAddress((byte)13, addressString, friendlyString, this._isEncoded, this._encodingCode);
   }

   public String[][] getOriginalRecipient() {
      return this.getFieldAddresses((byte)13);
   }

   public void setDate(long millisecondsLong) {
      this._headerParameters.addCMIMEDate((byte)7, millisecondsLong);
   }

   public long getDate() {
      int firstIndex = this._headerParameters.resolveInIndex((byte)7, 0);
      return firstIndex != -1 ? this._headerParameters.getCMIMEDate(firstIndex) : System.currentTimeMillis();
   }

   public void setSubject(Object anObject) {
      String tempType = this._textType;
      if (anObject != null) {
         if (tempType == null) {
            tempType = CMIMEUtilities.getTextContentType();
         }

         if (!this._isEncoded) {
            byte[] text = CMIMEUtilities.getTextByteArray(anObject, tempType);
            if (text != null && text.length > 0) {
               this._headerParameters.add((byte)8, text);
               return;
            }
         } else {
            boolean toBeEncoded = !(anObject instanceof Object) || !ConverterUtilities.isIntellisyncCompatible((String)anObject);
            byte[] text = CMIMEUtilities.getTextByteArray(anObject, toBeEncoded ? tempType : CMIMEUtilities.getTextContentType());
            if (text != null && text.length > 0) {
               if (toBeEncoded) {
                  this._headerParameters.add((byte)-120, this._encodingCode, text);
                  return;
               }

               this._headerParameters.add((byte)8, text);
            }
         }
      }
   }

   public Object getSubject() {
      byte[] rawDataEnc = this._headerParameters.getFirst((byte)-120);
      if (rawDataEnc != null) {
         if (rawDataEnc.length > 0) {
            this.setEncoding(rawDataEnc[0]);
         }

         return CMIMEUtilities.getTextObject(rawDataEnc, true);
      } else {
         return CMIMEUtilities.getTextObject(this._headerParameters.getFirst((byte)8), false);
      }
   }

   public void setSensitivity(byte aByte) {
      this._headerParameters.add((byte)12, aByte);
   }

   public byte getSensitivity() {
      byte[] bytes = this._headerParameters.getFirst((byte)12);
      return bytes != null && bytes.length > 0 ? bytes[0] : 1;
   }

   public void setImportance(byte aByte) {
      this._headerParameters.add((byte)11, aByte);
   }

   public byte getImportance() {
      byte[] bytes = this._headerParameters.getFirst((byte)11);
      return bytes != null && bytes.length > 0 ? bytes[0] : 1;
   }

   public void setMessageIcon(byte[] byteArray) {
      this._headerParameters.add((byte)-11, byteArray);
   }

   public void setMessageIconCharacter(char aCharacter) {
      if (aCharacter == '\uf3f1') {
         this._headerParameters.add((byte)-11, (byte)21);
      } else {
         throw new Object("UI");
      }
   }

   public byte[] getMessageIcon() {
      return this._headerParameters.getFirst((byte)-11);
   }

   public char getMessageIconCharacter() {
      byte[] bytes = this._headerParameters.getFirst((byte)-11);
      char result = ' ';
      if (bytes != null && bytes.length == 1 && bytes[0] == 21) {
         result = '\uf3f1';
      }

      return result;
   }

   public boolean hasMessageIconCharacter() {
      return this._headerParameters.getFirst((byte)-11) != null;
   }

   public void setFolderIdentifier(int identifierInt) {
      this._headerParameters.addCMIMEInteger((byte)-9, identifierInt);
   }

   public int getFolderIdentifier() {
      byte[] bytes = this._headerParameters.getFirst((byte)-9);
      return bytes != null ? CMIMEUtilities.decodeInteger(bytes) : 0;
   }

   public void setReferenceIdentifier(int identifierInt) {
      this._headerParameters.addCMIMEInteger((byte)-15, identifierInt);
   }

   public int getReferenceIdentifier() {
      byte[] bytes = this._headerParameters.getFirst((byte)-15);
      return bytes != null ? CMIMEUtilities.decodeInteger(bytes) : 0;
   }

   public void setReferenceIdentifier(byte[] identifierBytes) {
      this._headerParameters.add((byte)-15, identifierBytes);
   }

   public void setOriginalReferenceIdentifier(int identifierInt) {
      this._headerParameters.addCMIMEInteger((byte)-14, identifierInt);
   }

   public int getOriginalReferenceIdentifier() {
      byte[] bytes = this._headerParameters.getFirst((byte)-14);
      return bytes != null ? CMIMEUtilities.decodeInteger(bytes) : 0;
   }

   public void setNotificationLevel(byte aByte) {
      this._headerParameters.add((byte)-7, aByte);
   }

   public byte getNotificationLevel() {
      byte[] bytes = this._headerParameters.getFirst((byte)-7);
      return bytes != null && bytes.length > 0 ? bytes[0] : 0;
   }

   public Parameters getMessageParameters() {
      return this._headerParameters;
   }

   public Parameters getMessageBodyParameters() {
      return this._bodyParameters;
   }

   public byte getAcknowledgementRequestSettings() {
      byte[] bytes = this._headerParameters.getFirst((byte)-8);
      return bytes != null && bytes.length > 0 ? bytes[0] : 0;
   }

   public void setAcknowledgementRequestSettings(byte aByte) {
      this._headerParameters.add((byte)-8, aByte);
   }

   public boolean isMessageToBeMarkedReadOnReceipt() {
      byte[] bytes = this._headerParameters.getFirst((byte)-4);
      return bytes != null && bytes.length > 0 ? (bytes[0] & 1) > 0 : false;
   }

   public void markMessageReadOnReceipt() {
      this._headerParameters.add((byte)-4, (byte)1);
   }

   public void markMessageUnreadOnReceipt() {
      this._headerParameters.add((byte)-4, (byte)0);
   }

   public void saveMessageInSentItems() {
      this._headerParameters.add((byte)-6, (byte)1);
   }

   public void doNotSaveMessageInSentItems() {
      this._headerParameters.add((byte)-6, (byte)0);
   }

   public byte getOriginalTextRequestSettings() {
      byte[] bytes = this._headerParameters.getFirst((byte)-5);
      return bytes != null && bytes.length > 0 ? bytes[0] : 0;
   }

   public void setOriginalTextRequestSettings(byte aByte) {
      this._headerParameters.add((byte)-5, aByte);
   }

   public void setLocaleCode(int localeCode) {
      this._headerParameters.addCMIMEInteger((byte)-2, localeCode);
   }

   private String[][] getFieldAddresses(byte fieldID) {
      byte[][] rawData = this._headerParameters.get(fieldID);
      byte[][] rawDataEnc = this._headerParameters.get((byte)(-128 | fieldID));
      boolean hasEncoded = rawDataEnc != null;
      if (hasEncoded) {
         if (rawDataEnc.length > 0 && rawDataEnc[0].length > 0) {
            this.setEncoding(rawDataEnc[0][0]);
         }

         if (rawData != null && fieldID != 1) {
            String[][] unencoded = CMIMEUtilities.decodeAddresses(rawData, false);
            String[][] encoded = CMIMEUtilities.decodeAddresses(rawDataEnc, true);
            int unencodedLength = unencoded != null ? unencoded.length : 0;
            int encodedLength = encoded != null ? encoded.length : 0;
            String[][] all = new Object[unencodedLength + encodedLength][];

            int i;
            for (i = 0; i < unencodedLength; i++) {
               all[i] = unencoded[i];
            }

            for (int j = 0; j < encodedLength; i++) {
               all[i] = encoded[j];
               j++;
            }

            return all;
         }

         rawData = rawDataEnc;
      }

      return CMIMEUtilities.decodeAddresses(rawData, hasEncoded);
   }
}
