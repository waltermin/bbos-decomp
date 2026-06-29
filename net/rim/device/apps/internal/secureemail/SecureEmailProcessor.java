package net.rim.device.apps.internal.secureemail;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.mime.MIMEInputStream;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEContentType;
import net.rim.device.apps.api.transmission.rim.CMIMEConverterRegistry;
import net.rim.device.apps.api.transmission.rim.CMIMEParameters;
import net.rim.device.apps.api.transmission.rim.CMIMEStringConverter;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.secureemail.cache.CachedBodyField;
import net.rim.device.apps.internal.secureemail.cache.CachedManager;
import net.rim.device.apps.internal.secureemail.cache.CachedMessage;
import net.rim.device.internal.ui.component.PleaseWaitDialog;

public class SecureEmailProcessor extends Thread {
   protected SecureEmailFactory _secureEmailFactory;
   protected KeyStore _preferredKeyStore;
   protected SecureEmailBodyModel _secureEmailBodyModel;
   protected Hashtable _securityEncodingOptionalParameterTable;
   protected Object _target;
   protected boolean _allowUI;
   protected ContextObject _context;
   protected EmailMessageModel _emailMessageModel;
   protected ServiceRecord _serviceRecord;
   protected SecureEmailCache _secureEmailCache;
   protected CachedMessage _cachedMessage;
   protected Throwable _processingThrowable;
   protected SecureEmailProcessor _equalSecureEmailProcessor;
   private static Vector _processorQueue = (Vector)(new Object());
   private static SecureEmailProcessor$WorkerThread _workerThread;
   private static final boolean DEBUG = true;

   public SecureEmailProcessor(SecureEmailFactory secureEmailFactory, SecureEmailBodyModel secureEmailBodyModel, Object target, boolean allowUI, Object context) {
      this._secureEmailFactory = secureEmailFactory;
      this._secureEmailBodyModel = secureEmailBodyModel;
      this._target = target;
      this._allowUI = allowUI;
      if (context != null) {
         this._context = ContextObject.clone(context);
      }

      this._emailMessageModel = (EmailMessageModel)ContextObject.get(this._context, 246);
      this._serviceRecord = EmailMessageUtilities.getServiceRecordForMessage(this._emailMessageModel);
      this._secureEmailCache = SecureEmailCache.getInstance();
      this._preferredKeyStore = secureEmailFactory.getPreferredKeyStore();
   }

   public void run(boolean waitForResult) {
      if (waitForResult && Application.getApplication().isEventThread()) {
         PleaseWaitDialog pleaseWaitDialog = (PleaseWaitDialog)(new Object(new SecureEmailProcessor$1(this)));
         pleaseWaitDialog.display();
      } else {
         this.runProcessor(waitForResult);
      }
   }

   public void runWithExceptions() throws Throwable {
      this.run(true);
      if (this._processingThrowable != null) {
         throw this._processingThrowable;
      }
   }

   private synchronized void runProcessor(boolean waitForResult) {
      Object cpTicket = PersistentContent.getTicket();
      if (cpTicket != null) {
         this.queueProcessor();
         if (waitForResult) {
            try {
               this.wait();
            } finally {
               return;
            }
         }
      }
   }

   private void queueProcessor() {
      synchronized (_processorQueue) {
         int existingProcessorIndex = _processorQueue.indexOf(this);
         if (existingProcessorIndex >= 0) {
            System.out
               .println(((StringBuffer)(new Object("SecureEmail: Linking new processor with processor at index "))).append(existingProcessorIndex).toString());
            this._equalSecureEmailProcessor = (SecureEmailProcessor)_processorQueue.elementAt(existingProcessorIndex);
         }

         System.out.println("SecureEmail: Queuing processor");
         _processorQueue.addElement(this);
         if (_workerThread == null) {
            System.out.println("SecureEmail: Starting worker thread");
            _workerThread = new SecureEmailProcessor$WorkerThread();
            _workerThread.start();
         }
      }
   }

   public void parseSecurityEncodingParameters() {
      if (Arrays.equals(this.getSupportedSecurityEncoding(), this._secureEmailBodyModel.getSecurityEncoding())) {
         this._securityEncodingOptionalParameterTable = this._secureEmailBodyModel.getSecurityEncodingParameters();
      }
   }

   protected int mapParameterValueStringToInt(String parameter, String[] parameterValueStrings) {
      return Arrays.getIndex(parameterValueStrings, this._securityEncodingOptionalParameterTable.get(parameter));
   }

   protected void doActualProcessingWork() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._equalSecureEmailProcessor Lnet/rim/device/apps/internal/secureemail/SecureEmailProcessor;
      // 004: ifnull 020
      // 007: aload 0
      // 008: aload 0
      // 009: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._equalSecureEmailProcessor Lnet/rim/device/apps/internal/secureemail/SecureEmailProcessor;
      // 00c: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._cachedMessage Lnet/rim/device/apps/internal/secureemail/cache/CachedMessage;
      // 00f: putfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._cachedMessage Lnet/rim/device/apps/internal/secureemail/cache/CachedMessage;
      // 012: aload 0
      // 013: aload 0
      // 014: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._equalSecureEmailProcessor Lnet/rim/device/apps/internal/secureemail/SecureEmailProcessor;
      // 017: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._processingThrowable Ljava/lang/Throwable;
      // 01a: putfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._processingThrowable Ljava/lang/Throwable;
      // 01d: goto 038
      // 020: aload 0
      // 021: aload 0
      // 022: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailCache Lnet/rim/device/apps/internal/secureemail/SecureEmailCache;
      // 025: aload 0
      // 026: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailBodyModel Lnet/rim/device/apps/internal/secureemail/SecureEmailBodyModel;
      // 029: aload 0
      // 02a: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._allowUI Z
      // 02d: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailCache.getMessage (Lnet/rim/device/apps/internal/commonmodels/body/BodyModel;Z)Lnet/rim/device/apps/internal/secureemail/cache/CachedMessage;
      // 030: putfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._cachedMessage Lnet/rim/device/apps/internal/secureemail/cache/CachedMessage;
      // 033: aload 0
      // 034: aconst_null
      // 035: putfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._processingThrowable Ljava/lang/Throwable;
      // 038: aload 0
      // 039: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._cachedMessage Lnet/rim/device/apps/internal/secureemail/cache/CachedMessage;
      // 03c: ifnull 042
      // 03f: goto 133
      // 042: aload 0
      // 043: aload 0
      // 044: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailFactory Lnet/rim/device/apps/internal/secureemail/SecureEmailFactory;
      // 047: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.createCachedMessage ()Ljava/lang/Object;
      // 04a: putfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._cachedMessage Lnet/rim/device/apps/internal/secureemail/cache/CachedMessage;
      // 04d: aload 0
      // 04e: aconst_null
      // 04f: putfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._processingThrowable Ljava/lang/Throwable;
      // 052: bipush -1
      // 054: istore 1
      // 055: aload 0
      // 056: aload 0
      // 057: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._cachedMessage Lnet/rim/device/apps/internal/secureemail/cache/CachedMessage;
      // 05a: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailProcessor.fillCachedMessage (Ljava/lang/Object;)V
      // 05d: goto 10f
      // 060: astore 2
      // 061: bipush 43
      // 063: istore 1
      // 064: aload 0
      // 065: aload 2
      // 066: putfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._processingThrowable Ljava/lang/Throwable;
      // 069: goto 10f
      // 06c: astore 2
      // 06d: aload 2
      // 06e: invokevirtual net/rim/device/api/crypto/CryptoIOException.getCryptoException ()Lnet/rim/device/api/crypto/CryptoException;
      // 071: instanceof java/lang/Object
      // 074: ifeq 07d
      // 077: bipush 43
      // 079: istore 1
      // 07a: goto 094
      // 07d: aload 0
      // 07e: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailFactory Lnet/rim/device/apps/internal/secureemail/SecureEmailFactory;
      // 081: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.getEventLoggerGUID ()J
      // 084: aload 2
      // 085: invokevirtual net/rim/device/api/crypto/CryptoIOException.toString ()Ljava/lang/String;
      // 088: invokevirtual java/lang/String.getBytes ()[B
      // 08b: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[B)Z
      // 08e: pop
      // 08f: aload 0
      // 090: invokespecial net/rim/device/apps/internal/secureemail/SecureEmailProcessor.getGeneralErrorResourceID ()I
      // 093: istore 1
      // 094: aload 0
      // 095: aload 2
      // 096: putfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._processingThrowable Ljava/lang/Throwable;
      // 099: goto 10f
      // 09c: astore 2
      // 09d: aload 0
      // 09e: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailFactory Lnet/rim/device/apps/internal/secureemail/SecureEmailFactory;
      // 0a1: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.getEventLoggerGUID ()J
      // 0a4: aload 2
      // 0a5: invokevirtual net/rim/device/apps/api/utility/serialization/SerializationException.toString ()Ljava/lang/String;
      // 0a8: invokevirtual java/lang/String.getBytes ()[B
      // 0ab: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[B)Z
      // 0ae: pop
      // 0af: sipush 151
      // 0b2: istore 1
      // 0b3: aload 0
      // 0b4: aload 2
      // 0b5: putfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._processingThrowable Ljava/lang/Throwable;
      // 0b8: goto 10f
      // 0bb: astore 2
      // 0bc: aload 0
      // 0bd: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailFactory Lnet/rim/device/apps/internal/secureemail/SecureEmailFactory;
      // 0c0: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.getEventLoggerGUID ()J
      // 0c3: aload 2
      // 0c4: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 0c7: invokevirtual java/lang/String.getBytes ()[B
      // 0ca: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[B)Z
      // 0cd: pop
      // 0ce: sipush 167
      // 0d1: istore 1
      // 0d2: aload 0
      // 0d3: aload 2
      // 0d4: putfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._processingThrowable Ljava/lang/Throwable;
      // 0d7: goto 10f
      // 0da: astore 2
      // 0db: aload 0
      // 0dc: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailBodyModel Lnet/rim/device/apps/internal/secureemail/SecureEmailBodyModel;
      // 0df: invokevirtual net/rim/device/apps/internal/blackberryemail/unknown/UnknownMimePartModel.isMoreAvailable ()Z
      // 0e2: ifne 0f7
      // 0e5: aload 0
      // 0e6: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailFactory Lnet/rim/device/apps/internal/secureemail/SecureEmailFactory;
      // 0e9: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.getEventLoggerGUID ()J
      // 0ec: aload 2
      // 0ed: invokevirtual java/io/EOFException.toString ()Ljava/lang/String;
      // 0f0: invokevirtual java/lang/String.getBytes ()[B
      // 0f3: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[B)Z
      // 0f6: pop
      // 0f7: aload 0
      // 0f8: invokespecial net/rim/device/apps/internal/secureemail/SecureEmailProcessor.getGeneralErrorResourceID ()I
      // 0fb: istore 1
      // 0fc: aload 0
      // 0fd: aload 2
      // 0fe: putfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._processingThrowable Ljava/lang/Throwable;
      // 101: goto 10f
      // 104: astore 2
      // 105: aload 0
      // 106: invokespecial net/rim/device/apps/internal/secureemail/SecureEmailProcessor.getGeneralErrorResourceID ()I
      // 109: istore 1
      // 10a: aload 0
      // 10b: aload 2
      // 10c: putfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._processingThrowable Ljava/lang/Throwable;
      // 10f: iload 1
      // 110: iflt 133
      // 113: bipush 1
      // 114: anewarray 816
      // 117: dup
      // 118: bipush 0
      // 119: aload 0
      // 11a: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailFactory Lnet/rim/device/apps/internal/secureemail/SecureEmailFactory;
      // 11d: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.getEncodingString ()Ljava/lang/String;
      // 120: aastore
      // 121: astore 2
      // 122: iload 1
      // 123: invokestatic net/rim/device/apps/internal/secureemail/SecureEmailResources.getString (I)Ljava/lang/String;
      // 126: aload 2
      // 127: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 12a: astore 3
      // 12b: aload 0
      // 12c: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._cachedMessage Lnet/rim/device/apps/internal/secureemail/cache/CachedMessage;
      // 12f: aload 3
      // 130: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedMessage.setErrorString (Ljava/lang/String;)V
      // 133: aload 0
      // 134: aload 0
      // 135: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._cachedMessage Lnet/rim/device/apps/internal/secureemail/cache/CachedMessage;
      // 138: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailProcessor.fillTarget (Ljava/lang/Object;)V
      // 13b: return
      // try (40 -> 44): 45 null
      // try (40 -> 44): 52 null
      // try (40 -> 44): 75 null
      // try (40 -> 44): 90 null
      // try (40 -> 44): 105 null
      // try (40 -> 44): 125 null
   }

   private int getGeneralErrorResourceID() {
      return this._secureEmailBodyModel.isMoreAvailable() ? 148 : 44;
   }

   protected void fillCachedMessage(CachedMessage cachedMessage) {
      byte[] modelData = this._secureEmailBodyModel.getBytes();
      if (this._secureEmailBodyModel.getLengthOnDevice() < this._secureEmailBodyModel.getTrueLength()) {
         cachedMessage.setBodyTruncated(true);
      }

      this.parseSecurityEncodingParameters();
      if (this._securityEncodingOptionalParameterTable != null) {
         InputStream inputStream = this.getInputStream(modelData, cachedMessage);
         if (inputStream != null) {
            this.getFieldsFromInputStream(inputStream, cachedMessage);
            if (!cachedMessage.doNotCache()) {
               this._secureEmailCache.putMessage(this._secureEmailBodyModel, cachedMessage);
            }
         }
      }
   }

   protected void fillTarget(CachedMessage cachedMessage) {
      if (!(this._target instanceof SecureEmailMessageManager)) {
         if (this._target instanceof Object) {
            StringBuffer sb = (StringBuffer)this._target;
            cachedMessage.fillStringBuffer(sb, this._context);
         }
      } else {
         SecureEmailMessageManager secureEmailMessageManager = (SecureEmailMessageManager)this._target;
         Object displayContext = SecureEmailUtilities.getDisplayContext(cachedMessage, this._secureEmailBodyModel, this._serviceRecord, this._context);
         synchronized (Application.getEventLock()) {
            Manager mainManager = null;
            int oldVerticalScroll = -1;
            Screen screen = secureEmailMessageManager.getScreen();
            if (screen instanceof Object) {
               mainManager = ((MainScreen)screen).getMainManager();
               oldVerticalScroll = mainManager.getVerticalScroll();
            }

            Field oldFieldWithFocus = secureEmailMessageManager.getFieldWithFocus();
            secureEmailMessageManager.deleteAll();
            cachedMessage.fillManager(secureEmailMessageManager, displayContext);
            if (oldFieldWithFocus != null && secureEmailMessageManager.getCursorCount(null) > 0) {
               secureEmailMessageManager.setCursorPosition(0, null);
               secureEmailMessageManager.setFocus();
               if (mainManager != null && oldVerticalScroll >= 0) {
                  mainManager.setVerticalScroll(oldVerticalScroll);
               }
            }
         }
      }
   }

   protected SymmetricKey getCachedSessionKey() {
      return this._emailMessageModel == null ? null : this._secureEmailCache.getSessionKey(this._emailMessageModel);
   }

   protected byte[] getBESSignerCertificateHash() {
      String signerCertificateHashString = (String)this._securityEncodingOptionalParameterTable.get("sc");
      if (signerCertificateHashString == null) {
         return null;
      }

      byte[] signerCertificateHashASCII = signerCertificateHashString.getBytes();
      int length = signerCertificateHashASCII.length;
      if (length != 40) {
         return null;
      }

      byte[] signerCertificateHash = new byte[20];
      int shift = 4;

      for (int i = 0; i < length; i++) {
         byte c = signerCertificateHashASCII[i];
         if (c >= 48 && c <= 57) {
            c = (byte)(c - 48);
         } else {
            if (c < 65 || c > 70) {
               return null;
            }

            c = (byte)(c - 55);
         }

         signerCertificateHash[i >> 1] = (byte)(signerCertificateHash[i >> 1] | (byte)(c << shift));
         shift ^= 4;
      }

      return signerCertificateHash;
   }

   protected byte[] getSupportedSecurityEncoding() {
      throw null;
   }

   protected InputStream getInputStream(byte[] _1, CachedMessage _2) {
      throw null;
   }

   protected boolean getFieldsFromInputStream(InputStream inputStream, CachedManager cachedManager) {
      if (!(inputStream instanceof Object)) {
         return this.getFieldsFromInputStream_Text(inputStream, cachedManager);
      }

      MIMEInputStream mimeStream = (MIMEInputStream)inputStream;
      return this.getFieldsFromInputStream_MIME(mimeStream, cachedManager);
   }

   protected boolean getFieldsFromInputStream_MIME(MIMEInputStream mimeStream, CachedManager cachedManager) {
      String contentType = mimeStream.getContentType();
      if (contentType != null) {
         if (contentType.startsWith("multipart/")) {
            return this.getFieldsFromInputStream_MIME_Multipart(mimeStream, cachedManager);
         } else if (contentType.startsWith("application/")) {
            return this.getFieldsFromInputStream_MIME_Application(mimeStream, cachedManager);
         } else {
            return contentType.startsWith("text/")
               ? this.getFieldsFromInputStream_MIME_Text(mimeStream, cachedManager)
               : this.getFieldsFromInputStream_MIME_Attachment(mimeStream, cachedManager);
         }
      } else {
         return false;
      }
   }

   protected boolean getFieldsFromInputStream_MIME_Multipart(MIMEInputStream mimeStream, CachedManager cachedManager) {
      String contentType = mimeStream.getContentType();
      if (contentType != null) {
         return contentType.startsWith("multipart/alternative")
            ? this.getFieldsFromInputStream_MIME_MultipartAlternative(mimeStream, cachedManager)
            : this.getFieldsFromInputStream_MIME_MultipartMixed(mimeStream, cachedManager);
      } else {
         return false;
      }
   }

   private boolean getFieldsFromInputStream_MIME_MultipartMixed(MIMEInputStream mimeStream, CachedManager cachedManager) {
      boolean gotFields = false;
      MIMEInputStream[] innerParts = mimeStream.getParts();

      for (int i = 0; i < innerParts.length; i++) {
         gotFields |= this.getFieldsFromInputStream(innerParts[i], cachedManager);
      }

      return gotFields;
   }

   private boolean getFieldsFromInputStream_MIME_MultipartAlternative(MIMEInputStream mimeStream, CachedManager cachedManager) {
      MIMEInputStream[] innerParts = mimeStream.getParts();

      for (int i = 0; i < innerParts.length; i++) {
         if (this.getFieldsFromInputStream(innerParts[i], cachedManager)) {
            return true;
         }
      }

      return false;
   }

   protected boolean getFieldsFromInputStream_MIME_Text(MIMEInputStream mimeStream, CachedManager cachedManager) {
      String contentDisposition = mimeStream.getHeader("Content-Disposition");
      if (contentDisposition != null) {
         return this.getFieldsFromInputStream_MIME_Attachment(mimeStream, cachedManager);
      }

      String contentType = mimeStream.getContentType();
      if (contentType != null && contentType.startsWith("text/plain")) {
         if (mimeStream.isPartComplete() == 1) {
            cachedManager.getCachedMessage().setBodyTruncated(false);
         }

         Parameters parameters = (Parameters)(new Object((DataBuffer)(new Object()), 1, 1));
         parameters.add((byte)1, contentType.getBytes());
         return this.getFieldsFromInputStream_Text(mimeStream, parameters, cachedManager);
      } else {
         return false;
      }
   }

   protected boolean getFieldsFromInputStream_Text(InputStream inputStream, CachedManager cachedManager) {
      return this.getFieldsFromInputStream_Text(inputStream, null, cachedManager);
   }

   protected boolean getFieldsFromInputStream_Text(InputStream inputStream, Parameters conversionParameters, CachedManager cachedManager) {
      byte[] textBytes = SecureEmailUtilities.readStreamCompletely(inputStream);
      if (conversionParameters == null) {
         conversionParameters = (Parameters)(new Object((DataBuffer)(new Object()), 1, 1));
         conversionParameters.add((byte)1, this._secureEmailBodyModel.getTextMIMEType().getBytes());
      }

      String bodyText = (String)CMIMEStringConverter.getInstance().convert(textBytes, conversionParameters);
      if (bodyText.length() > 0) {
         PersistentContent.markAsPlaintext(bodyText);
         cachedManager.addField(new CachedBodyField(bodyText));
      }

      return true;
   }

   protected boolean getFieldsFromInputStream_MIME_Application(MIMEInputStream mimeStream, CachedManager cachedManager) {
      return this.getFieldsFromInputStream_MIME_Attachment(mimeStream, cachedManager);
   }

   private boolean getFieldsFromInputStream_MIME_Attachment(MIMEInputStream mimeStream, CachedManager cachedManager) {
      int displayAttachments = this.mapParameterValueStringToInt("da", SecureEmailConstants.DISPLAY_ATTACHMENTS_VALUES);
      if (displayAttachments == 0) {
         return false;
      }

      byte[] data = SecureEmailUtilities.readStreamCompletely(mimeStream);
      CMIMEParameters parameters = (CMIMEParameters)(new Object((DataBuffer)(new Object()), 4, 1));
      String contentType = mimeStream.getContentType();
      if (contentType != null) {
         DataBuffer cmimeContentTypeBuffer = (DataBuffer)(new Object());
         CMIMEContentType.encodeType(contentType, cmimeContentTypeBuffer);
         cmimeContentTypeBuffer.rewind();
         parameters.add((byte)1, cmimeContentTypeBuffer.readByteArray());
      }

      String name = mimeStream.getContentTypeParameter("name");
      if (name == null) {
         name = SecureEmailResources.getString(45);
      }

      parameters.add((byte)-14, name.getBytes());
      parameters.addCMIMEInteger((byte)-13, data.length);
      parameters.addCMIMEInteger((byte)-16, data.length);

      try {
         Converter converter = CMIMEConverterRegistry.getDefaultConverter(parameters);
         Object attachment = converter.convert(data, parameters);
         if (!(attachment instanceof Object)) {
            converter = CMIMEConverterRegistry.getDefaultConverter();
            attachment = converter.convert(data, parameters);
         }

         if (attachment instanceof Object) {
            cachedManager.getCachedMessage().addAttachment(attachment);
            return true;
         }
      } finally {
         return false;
      }

      return false;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (!(o instanceof SecureEmailProcessor)) {
         return false;
      }

      SecureEmailProcessor other = (SecureEmailProcessor)o;
      return other._allowUI == this._allowUI && other._secureEmailBodyModel.equals(this._secureEmailBodyModel);
   }

   @Override
   public int hashCode() {
      return this._secureEmailBodyModel.hashCode();
   }

   static SecureEmailProcessor$WorkerThread access$202(SecureEmailProcessor$WorkerThread x0) {
      _workerThread = x0;
      return x0;
   }
}
