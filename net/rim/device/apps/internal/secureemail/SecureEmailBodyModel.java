package net.rim.device.apps.internal.secureemail;

import java.util.Hashtable;
import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ColumnPaintProvider;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncodingProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.messaging.EmailBodyProvider;
import net.rim.device.apps.api.messaging.MessageIcons;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEParameters;
import net.rim.device.apps.api.transmission.rim.RIMMessagingMoreMessage;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.MorePartModel;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.unknown.UnknownMimePartModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;

public class SecureEmailBodyModel
   extends UnknownMimePartModel
   implements BodyModel,
   FieldProvider,
   ColumnPaintProvider,
   ConversionProvider,
   CloneProvider,
   MatchProvider,
   EmailBodyProvider,
   MorePartModel,
   EncodingProvider,
   EncryptableProvider {
   protected Object _textEncoding;
   int _hashCode;
   protected byte[] _securityEncoding;
   protected byte[] _securityEncodingParameters;

   public byte[] getSecurityEncoding() {
      return this._securityEncoding;
   }

   public Hashtable getSecurityEncodingParameters() {
      Hashtable securityEncodingParameterTable = (Hashtable)(new Object());
      if (this._securityEncodingParameters != null && this._securityEncodingParameters.length != 0) {
         String securityEncodingParametersString = (String)(new Object(this._securityEncodingParameters));
         StringTokenizer stringTokenizer = (StringTokenizer)(new Object(securityEncodingParametersString, ','));

         while (stringTokenizer.hasMoreTokens()) {
            String typeValuePair = stringTokenizer.nextToken();
            int equalOffset = typeValuePair.indexOf(61);
            if (equalOffset != -1) {
               String typeString = typeValuePair.substring(0, equalOffset);
               String valueString = typeValuePair.substring(equalOffset + 1);
               securityEncodingParameterTable.put(typeString, valueString);
            }
         }

         return securityEncodingParameterTable;
      } else {
         return securityEncodingParameterTable;
      }
   }

   protected void setBytes(byte[] bytes) {
      this._textEncoding = PersistentContent.encode(bytes, true, true);
   }

   protected void appendBytes(byte[] bytes) {
      this._textEncoding = PersistentContent.encodeAndAppend(bytes, true, true, this._textEncoding);
   }

   protected byte[] getBytes() {
      try {
         return PersistentContent.decodeByteArray(this._textEncoding);
      } finally {
         return "<Content Protection is enabled>".getBytes();
      }
   }

   public SecureEmailFactory getSecureEmailFactory() {
      throw null;
   }

   protected byte[] getProperlyEncodedByteArray(byte[] rawByteArray, Object parametersObject) {
      return rawByteArray;
   }

   protected boolean getShowShortFormDefault() {
      return false;
   }

   protected int getMessageStatusIcon(boolean inbound, int status, boolean opened, boolean filed) {
      return -1;
   }

   protected int getBestGuessEncodingAction() {
      throw null;
   }

   protected boolean isTextMultiByte() {
      throw null;
   }

   protected String getTextCharset() {
      return this.isTextMultiByte() ? "utf-8" : "iso-8859-1";
   }

   public String getTextMIMEType() {
      return this.isTextMultiByte() ? "text/plain; charset=utf-8" : "text/plain; charset=iso-8859-1";
   }

   protected String getTextOutgoingMIMEType() {
      return this.isTextMultiByte() ? "text/plain; charset=utf-8" : "text/plain";
   }

   protected Object cloneImpl(Object _1) {
      throw null;
   }

   @Override
   public void paint(ColumnPainter painter, Object context) {
      EmailMessageModel message = (EmailMessageModel)ContextObject.get(context, 246);
      if (painter.isColumnEmpty(4)) {
         String stringToPaint = MessageFormat.format(SecureEmailResources.getString(28), new Object[]{this.getEncodingString()});
         painter.drawText(4, stringToPaint, false);
      }

      boolean inbound = message.inbound();
      int status = message.getStatus();
      int flags = message.getFlags();
      boolean opened = (flags & 1) != 0;
      boolean filed = (flags & 2) != 0;
      if (inbound && EmailHierarchy.isInPersonalFolder(message)) {
         filed = true;
      }

      int statusIcon = this.getMessageStatusIcon(inbound, status, opened, filed);
      if (statusIcon != -1) {
         painter.clear(1);
         painter.drawIcon(1, MessageIcons.getIcons(), statusIcon);
      }
   }

   @Override
   public Object clone(Object context) {
      boolean editable = ContextObject.getFlag(context, 0);
      if (editable) {
         StringBuffer buffer = (StringBuffer)(new Object());
         this.getDecodedBodyStringBuffer(buffer, context, true);
         return FactoryUtil.createInstance(5987399499453925075L, buffer.toString());
      } else {
         return this.cloneImpl(context);
      }
   }

   @Override
   public int paint(Graphics graphics, int x, int y, int width, int height, Object context) {
      return 0;
   }

   @Override
   public int match(Object param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: checkcast java/lang/Object
      // 04: astore 2
      // 05: aload 2
      // 06: invokeinterface net/rim/device/apps/api/search/SearchCriterion.getType ()I 1
      // 0b: lookupswitch 25 2 1 28 21 28
      // 24: bipush -1
      // 26: ireturn
      // 27: aconst_null
      // 28: astore 3
      // 29: aload 2
      // 2a: dup
      // 2b: instanceof java/lang/Object
      // 2e: ifne 35
      // 31: pop
      // 32: goto 85
      // 35: checkcast java/lang/Object
      // 38: astore 4
      // 3a: aload 4
      // 3c: invokevirtual net/rim/device/apps/api/search/criteria/TextSearchModel.getSubCriteria ()[Lnet/rim/device/apps/api/framework/model/PersistableRIMModel;
      // 3f: astore 5
      // 41: aload 5
      // 43: ifnull 85
      // 46: aload 5
      // 48: arraylength
      // 49: istore 6
      // 4b: bipush 0
      // 4c: istore 7
      // 4e: iload 7
      // 50: iload 6
      // 52: if_icmpge 85
      // 55: aload 5
      // 57: iload 7
      // 59: aaload
      // 5a: checkcast java/lang/Object
      // 5d: astore 8
      // 5f: aload 8
      // 61: invokeinterface net/rim/device/apps/api/search/SearchCriterion.getType ()I 1
      // 66: bipush 27
      // 68: if_icmpne 7f
      // 6b: aload 0
      // 6c: aload 8
      // 6e: invokespecial net/rim/device/apps/internal/secureemail/SecureEmailBodyModel.matchEncodingAction (Lnet/rim/device/apps/api/search/SearchCriterion;)I
      // 71: ifne 76
      // 74: bipush 0
      // 75: ireturn
      // 76: aload 8
      // 78: checkcast net/rim/device/apps/internal/secureemail/EncodingActionSearchModel
      // 7b: astore 3
      // 7c: goto 85
      // 7f: iinc 7 1
      // 82: goto 4e
      // 85: new java/lang/Object
      // 88: dup
      // 89: invokespecial java/lang/StringBuffer.<init> ()V
      // 8c: astore 4
      // 8e: aload 3
      // 8f: ifnull 9d
      // 92: aload 3
      // 93: invokevirtual net/rim/device/apps/internal/secureemail/EncodingActionSearchModel.ticketPromptsAllowed ()Z
      // 96: ifeq 9d
      // 99: bipush 1
      // 9a: goto 9e
      // 9d: bipush 0
      // 9e: istore 5
      // a0: aload 0
      // a1: aload 4
      // a3: aconst_null
      // a4: iload 5
      // a6: invokespecial net/rim/device/apps/internal/secureemail/SecureEmailBodyModel.getDecodedBodyStringBufferWithExceptions (Ljava/lang/StringBuffer;Ljava/lang/Object;Z)V
      // a9: aload 2
      // aa: invokeinterface net/rim/device/apps/api/search/SearchCriterion.getValue ()Ljava/lang/Object; 1
      // af: checkcast java/lang/Object
      // b2: astore 6
      // b4: aload 6
      // b6: aload 4
      // b8: bipush 0
      // b9: invokevirtual net/rim/device/api/util/StringMatch.indexOf (Ljava/lang/StringBuffer;I)I
      // bc: iflt d8
      // bf: bipush 1
      // c0: ireturn
      // c1: astore 4
      // c3: aload 4
      // c5: invokestatic net/rim/device/apps/internal/secureemail/SecureEmailUtilities.isCancelException (Ljava/lang/Exception;)Z
      // c8: ifeq d8
      // cb: aload 3
      // cc: ifnull d8
      // cf: aload 3
      // d0: bipush 0
      // d1: invokevirtual net/rim/device/apps/internal/secureemail/EncodingActionSearchModel.setTicketPromptsAllowed (Z)V
      // d4: bipush 0
      // d5: ireturn
      // d6: astore 4
      // d8: bipush 0
      // d9: ireturn
      // try (52 -> 80): 81 null
      // try (52 -> 80): 92 null
   }

   @Override
   public int getEncodingAction() {
      SecureEmailCache secureEmailCache = SecureEmailCache.getInstance();
      if (secureEmailCache.isMessageInCache(this)) {
         int encodingAction = 0;
         if (secureEmailCache.isMessageEncrypted(this)) {
            encodingAction |= 2;
         }

         if (secureEmailCache.isMessageSigned(this)) {
            encodingAction |= 1;
         }

         return encodingAction;
      } else {
         return this.getBestGuessEncodingAction();
      }
   }

   @Override
   public boolean isTextOpaque() {
      return true;
   }

   @Override
   public void setFocus(Field field, Application app, Object context) {
      synchronized (app.getAppEventLock()) {
         field.setFocus();
      }
   }

   @Override
   public int getCursorCount(Field field, Object context) {
      return !(field instanceof CursorProviderField) ? 0 : ((CursorProviderField)field).getCursorCount(context);
   }

   @Override
   public int getCurrentCursorPosition(Field field, Object context) {
      return !(field instanceof CursorProviderField) ? 0 : ((CursorProviderField)field).getCurrentCursorPosition(context);
   }

   @Override
   public void setCursorPosition(Field field, int pos, Object context) {
      if (field instanceof CursorProviderField) {
         ((CursorProviderField)field).setCursorPosition(pos, context);
      }
   }

   @Override
   public Object getTextEncoding() {
      throw new Object();
   }

   @Override
   public void setTextEncoding(Object textEncoding) {
      throw new Object();
   }

   @Override
   public String getText() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: new java/lang/Object
      // 03: dup
      // 04: invokespecial java/lang/StringBuffer.<init> ()V
      // 07: astore 1
      // 08: aload 0
      // 09: aload 1
      // 0a: aconst_null
      // 0b: bipush 0
      // 0c: invokespecial net/rim/device/apps/internal/secureemail/SecureEmailBodyModel.getDecodedBodyStringBufferWithExceptions (Ljava/lang/StringBuffer;Ljava/lang/Object;Z)V
      // 0f: goto 17
      // 12: astore 2
      // 13: goto 17
      // 16: astore 2
      // 17: aload 1
      // 18: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 1b: areturn
      // try (4 -> 9): 10 null
      // try (4 -> 9): 12 null
   }

   @Override
   public void setText(String text) {
      throw new Object();
   }

   @Override
   public long getEncodingUID() {
      throw null;
   }

   @Override
   public String getEncodingString() {
      throw null;
   }

   private void getDecodedBodyStringBufferWithExceptions(StringBuffer stringBuffer, Object context, boolean allowUI) {
      SecureEmailProcessor secureEmailProcessor = this.getSecureEmailFactory().createProcessor(this, stringBuffer, allowUI, context);
      secureEmailProcessor.runWithExceptions();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof SecureEmailBodyModel)) {
         return false;
      }

      SecureEmailBodyModel other = (SecureEmailBodyModel)object;
      return Arrays.equals(this.getBytes(), other.getBytes())
         && Arrays.equals(this._securityEncoding, other._securityEncoding)
         && Arrays.equals(this._securityEncodingParameters, other._securityEncodingParameters);
   }

   @Override
   public Field getField(Object context) {
      EmailMessageModel messageModel = (EmailMessageModel)ContextObject.get(context, 246);
      SecureEmailMessageManager secureEmailMessageManager = new SecureEmailMessageManager(1152921504606846976L, messageModel);
      secureEmailMessageManager.setCookie(this);
      SecureEmailCache emailCache = SecureEmailCache.getInstance();
      if (messageModel != null) {
         boolean showShortForm = emailCache.getShowShortForm(messageModel, this.getShowShortFormDefault());
         secureEmailMessageManager.showShortForm(showShortForm);
      }

      SecureEmailProcessor secureEmailProcessor = this.getSecureEmailFactory().createProcessor(this, secureEmailMessageManager, true, context);
      if (SecureEmailListener.getInstance().isMessageOpen(messageModel)) {
         secureEmailProcessor.run(true);
         return secureEmailMessageManager;
      } else {
         String decodingMessage = MessageFormat.format(SecureEmailResources.getString(29), new Object[]{this.getEncodingString()});
         ProcessingField processingField = new ProcessingField(decodingMessage);
         secureEmailMessageManager.add(processingField);
         secureEmailProcessor.run(false);
         return secureEmailMessageManager;
      }
   }

   private void getDecodedBodyStringBuffer(StringBuffer stringBuffer, Object context, boolean allowUI) {
      SecureEmailProcessor secureEmailProcessor = this.getSecureEmailFactory().createProcessor(this, stringBuffer, allowUI, context);
      secureEmailProcessor.run(true);
   }

   @Override
   public int getOrder(Object context) {
      return 5500;
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (!(target instanceof Object)) {
         if (target instanceof Object && ContextObject.getFlag(context, 70)) {
            StringBuffer stringBuffer = (StringBuffer)target;
            this.getDecodedBodyStringBuffer(stringBuffer, context, true);
            return true;
         }
      } else {
         RIMMessagingOutgoingMessage outgoingTransmission = (RIMMessagingOutgoingMessage)target;
         byte[] encodedBytes = this.getBytes();
         if (encodedBytes != null) {
            Parameters parameters = (Parameters)(new Object((DataBuffer)(new Object()), 1, 5));
            parameters.add((byte)-11, this._securityEncoding);

            try {
               String encodedBytesAsString = (String)(new Object(encodedBytes, this.getTextCharset()));
               outgoingTransmission.setText(encodedBytesAsString, parameters, this.getTextOutgoingMIMEType());
               return true;
            } finally {
               return super.convert(context, target);
            }
         }
      }

      return super.convert(context, target);
   }

   @Override
   public void receiveMore(Object context, Object moreObject) {
      if (moreObject instanceof Object) {
         RIMMessagingMoreMessage incomingTransmission = (RIMMessagingMoreMessage)moreObject;
         int existingLengthOnDevice = this.getLengthOnDevice();
         byte[] rawByteArray = incomingTransmission.getRawBytes();
         byte[] properlyEncodedByteArray = this.getProperlyEncodedByteArray(rawByteArray, incomingTransmission.getParameters());
         this.appendBytes(properlyEncodedByteArray);
         this.setLengthOnDevice(existingLengthOnDevice + rawByteArray.length);
      }
   }

   @Override
   protected void writeCMIMEParameters(CMIMEParameters parameters) {
      super.writeCMIMEParameters(parameters);
      parameters.add((byte)-11, this._securityEncoding);
      if (this._securityEncodingParameters != null) {
         parameters.add((byte)-5, this._securityEncodingParameters);
      }

      byte[] rawByteArray = this.getBytes();
      if (rawByteArray != null) {
         parameters.add((byte)-9, rawByteArray);
      }

      parameters.add((byte)1, new byte[]{2, 1});
   }

   @Override
   public int hashCode() {
      return this._hashCode;
   }

   @Override
   public boolean suppressNotification() {
      return false;
   }

   @Override
   public boolean isAutoMoreAvailable() {
      return SecureEmailCache.getInstance().isMessageBodyTruncated(this);
   }

   public SecureEmailBodyModel(byte[] rawByteArray, byte[] securityEncoding, byte[] securityEncodingParameters) {
      super(null);
      this.setBytes(rawByteArray);
      this.setLengthOnDevice(rawByteArray.length);
      this._hashCode = HashCodeCalculator.getCRC32(rawByteArray);
      this._securityEncoding = Arrays.copy(securityEncoding);
      this._securityEncodingParameters = Arrays.copy(securityEncodingParameters);
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._textEncoding, compress, encrypt);
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this._textEncoding = PersistentContent.reEncode(this._textEncoding, compress, encrypt);
      return null;
   }

   public SecureEmailBodyModel(byte[] rawByteArray, Object initialData) {
      super(initialData);
      ContextObject contextObject = ContextObject.castOrCreate(initialData);
      Object parametersObject = contextObject.get(-7353832199068708928L);
      byte[] properlyEncodedByteArray = this.getProperlyEncodedByteArray(rawByteArray, parametersObject);
      this.setBytes(properlyEncodedByteArray);
      this.setLengthOnDevice(rawByteArray.length);
      this._hashCode = HashCodeCalculator.getCRC32(properlyEncodedByteArray);
      if (parametersObject instanceof Object) {
         Parameters parameters = (Parameters)parametersObject;
         byte[] securityEncoding = parameters.getFirst((byte)-11);
         byte[] securityEncodingParameters = parameters.getFirst((byte)-5);
         if (securityEncoding == null) {
            throw new Object();
         }

         int securityEncodingBaseLength = Arrays.getIndex(securityEncoding, (byte)59);
         if (securityEncodingBaseLength < 0) {
            this._securityEncoding = Arrays.copy(securityEncoding);
         } else {
            this._securityEncoding = Arrays.copy(securityEncoding, 0, securityEncodingBaseLength);
         }

         if (securityEncodingParameters != null) {
            this._securityEncodingParameters = Arrays.copy(securityEncodingParameters);
         } else if (securityEncodingBaseLength >= 0 && securityEncodingBaseLength < securityEncoding.length) {
            this._securityEncodingParameters = Arrays.copy(
               securityEncoding, securityEncodingBaseLength + 1, securityEncoding.length - securityEncodingBaseLength - 1
            );
         }
      }

      super._isHidden = true;
   }

   private int matchEncodingAction(SearchCriterion searchCriterion) {
      int allowedEncodingActions = searchCriterion.getValue();
      int encodingAction = this.getEncodingAction();
      return (encodingAction & ~allowedEncodingActions) != 0 ? 0 : 1;
   }
}
