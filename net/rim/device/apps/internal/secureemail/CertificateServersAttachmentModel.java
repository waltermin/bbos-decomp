package net.rim.device.apps.internal.secureemail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import net.rim.device.api.crypto.certificate.CertificateServerInfo;
import net.rim.device.api.crypto.certificate.CertificateServers;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.mime.MIMEOutputStream;
import net.rim.device.api.ui.Field;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.rim.CMIMEParameters;
import net.rim.device.apps.api.transmission.rim.ContentPartIDGenerator;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;
import net.rim.device.apps.internal.blackberryemail.unknown.UnknownMimePartModel;
import net.rim.device.internal.resource.crypto.CryptoIcons;
import net.rim.device.internal.ui.Image;
import net.rim.vm.Array;
import net.rim.vm.Persistable;

public class CertificateServersAttachmentModel extends UnknownMimePartModel implements Persistable {
   private String _displayName;
   private CertificateServerInfo _serverInfo;
   private static String NAME = "name";
   public static final String STRING_BASE64 = "base64";
   private static String CONTENT_DISPOSITION = "Content-Disposition: attachment;\r\n\tfilename=";
   private static final ResourceBundle _rb = ResourceBundle.getBundle(
      -3843310740975580338L, "net.rim.device.internal.resource.crypto.CertificateServersOptions"
   );

   public CertificateServersAttachmentModel(Object initialData) {
      super(initialData);
      ContextObject contextObject = ContextObject.castOrCreate(initialData);
      Object certificateServerDataObject = contextObject.get(-8616111138651597975L);
      if (certificateServerDataObject instanceof CertificateServerInfo) {
         CertificateServerInfo serverInfo = (CertificateServerInfo)certificateServerDataObject;
         this._serverInfo = serverInfo;
      }

      if (this._serverInfo != null) {
         this.setNameBytes(this._serverInfo.getFriendlyName().getBytes());
      } else {
         Object friendlyNameObject = contextObject.get(-4886909117188079897L);
         if (friendlyNameObject instanceof String) {
            String friendlyName = (String)friendlyNameObject;
            this.setNameBytes(friendlyName.getBytes());
         }
      }

      if (this._serverInfo == null) {
         this.parseCertificateServers();
      }
   }

   public CertificateServerInfo getCertificateServerInfo() {
      return this._serverInfo;
   }

   public String getLabel(int index) {
      if (this._displayName != null) {
         return this._displayName;
      }

      if (this._serverInfo == null) {
         return _rb.getString(112);
      }

      StringBuffer labelBuffer = new StringBuffer();
      if (CertificateServers.getInstance().contains(this._serverInfo)) {
         labelBuffer.append('☑');
      } else {
         labelBuffer.append('☐');
      }

      labelBuffer.append(' ');
      labelBuffer.append(this._serverInfo.getFriendlyName());
      return labelBuffer.toString();
   }

   @Override
   public Field getField(Object context) {
      return new CertificateServersAttachmentField(this);
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (!(target instanceof RIMMessagingOutgoingMessage)) {
         if (target instanceof MIMEOutputStream && ContextObject.getFlag(context, 54) && ContextObject.getFlag(context, 43)) {
            MIMEOutputStream outputStream = (MIMEOutputStream)target;
            MIMEOutputStream mime = outputStream.getPartOutputStream(false, this.getOutgoingMIMEEncoding());
            mime.setContentType(this.getOutgoingMIMEContentType());
            String name = new String(this.getNameBytes());
            mime.addContentTypeParameter(NAME, name);
            mime.addHeaderField(CONTENT_DISPOSITION + name);
            return this.writeToOutputStream(mime);
         } else {
            return false;
         }
      } else {
         RIMMessagingOutgoingMessage message = (RIMMessagingOutgoingMessage)target;
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         this.writeToOutputStream(byteArrayOutputStream);
         ContextObject contextObject = (ContextObject)context;
         ContentPartIDGenerator contentPartIDGenerator = (ContentPartIDGenerator)contextObject.get(-1943436819741481055L);
         CMIMEParameters parameters = new CMIMEParameters(new DataBuffer(), 2, 1);
         parameters.add((byte)-14, this.getNameBytes());
         parameters.addCMIMEInteger((byte)-15, contentPartIDGenerator.generateContentPartID());
         message.addAttachment(byteArrayOutputStream.toByteArray(), parameters, SendCertificateServerVerb.TYPE_APPLICATION_CERTIFICATE_SERVERS);
         return true;
      }
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   public Image getImage() {
      return this._serverInfo == null ? CryptoIcons.getImage(19) : CryptoIcons.getImage(22);
   }

   private void parseCertificateServers() {
      byte[] originalData = this.getData();

      label26:
      try {
         Base64InputStream base64Stream = new Base64InputStream(new ByteArrayInputStream(originalData));
         byte[] binaryData = new byte[originalData.length];
         int length = base64Stream.read(binaryData);
         Array.resize(binaryData, length);
         this._serverInfo = CertificateServersConverter.convert(binaryData);
      } finally {
         break label26;
      }

      if (this._serverInfo == null) {
         this._serverInfo = CertificateServersConverter.convert(originalData);
      }
   }

   protected String getOutgoingMIMEContentType() {
      return SendCertificateServerVerb.TYPE_APPLICATION_CERTIFICATE_SERVERS;
   }

   protected String getOutgoingMIMEEncoding() {
      return "base64";
   }

   protected boolean writeToOutputStream(OutputStream param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: bipush 0
      // 01: istore 2
      // 02: aload 0
      // 03: getfield net/rim/device/apps/internal/secureemail/CertificateServersAttachmentModel._serverInfo Lnet/rim/device/api/crypto/certificate/CertificateServerInfo;
      // 06: ifnonnull 15
      // 09: bipush 0
      // 0a: istore 3
      // 0b: aload 1
      // 0c: invokevirtual java/io/OutputStream.close ()V
      // 0f: iload 3
      // 10: ireturn
      // 11: astore 4
      // 13: iload 3
      // 14: ireturn
      // 15: aload 0
      // 16: getfield net/rim/device/apps/internal/secureemail/CertificateServersAttachmentModel._serverInfo Lnet/rim/device/api/crypto/certificate/CertificateServerInfo;
      // 19: invokestatic net/rim/device/apps/internal/secureemail/CertificateServersConverter.convert (Lnet/rim/device/api/crypto/certificate/CertificateServerInfo;)[B
      // 1c: astore 3
      // 1d: aload 3
      // 1e: ifnull 3a
      // 21: new net/rim/device/api/io/Base64OutputStream
      // 24: dup
      // 25: aload 1
      // 26: bipush 1
      // 27: bipush 1
      // 28: invokespecial net/rim/device/api/io/Base64OutputStream.<init> (Ljava/io/OutputStream;ZZ)V
      // 2b: astore 4
      // 2d: aload 4
      // 2f: aload 3
      // 30: invokevirtual net/rim/device/api/io/Base64OutputStream.write ([B)V
      // 33: aload 4
      // 35: invokevirtual net/rim/device/api/io/Base64OutputStream.close ()V
      // 38: bipush 1
      // 39: istore 2
      // 3a: aload 1
      // 3b: invokevirtual java/io/OutputStream.close ()V
      // 3e: iload 2
      // 3f: ireturn
      // 40: astore 3
      // 41: iload 2
      // 42: ireturn
      // 43: astore 3
      // 44: aload 1
      // 45: invokevirtual java/io/OutputStream.close ()V
      // 48: iload 2
      // 49: ireturn
      // 4a: astore 3
      // 4b: iload 2
      // 4c: ireturn
      // 4d: astore 5
      // 4f: aload 1
      // 50: invokevirtual java/io/OutputStream.close ()V
      // 53: goto 58
      // 56: astore 6
      // 58: aload 5
      // 5a: athrow
      // try (7 -> 9): 11 null
      // try (34 -> 36): 38 null
      // try (2 -> 7): 41 null
      // try (14 -> 34): 41 null
      // try (42 -> 44): 46 null
      // try (2 -> 7): 49 null
      // try (14 -> 34): 49 null
      // try (41 -> 42): 49 null
      // try (50 -> 52): 53 null
      // try (49 -> 50): 49 null
   }
}
