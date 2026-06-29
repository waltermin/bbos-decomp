package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.internal.resource.crypto.CryptoIcons;
import net.rim.device.internal.ui.Image;

public class SecureEmailEncryptionField extends StatusField implements ContextMenuDelegate {
   protected SecureEmailFactory _secureEmailFactory;
   protected SecureEmailUtilities _utilities;
   protected EmailMessageModel _emailMessageModel;
   protected int _besEncryptionState;
   protected int _besWeakRecipientState;
   protected String _contentCipherAlgorithm;
   protected Certificate _recipientCertificate;
   protected VerbMenuItem _displayRecipientCertificateMenuItem;
   protected VerbMenuItem _displayWeakEncryptionReasonMenuItem;
   private static final long STATUS_TYPE_ENCRYPTION = 356383575838032505L;
   protected static final int WEAKLY_ENCRYPTED = 0;
   protected static final int STRONGLY_ENCRYPTED = 1;
   protected static final int PASSWORD_ENCRYPTED = 2;
   protected static final int BES_ENCRYPTED = 3;
   protected static final int BES_WEAK_RECIPIENT = 4;

   public SecureEmailEncryptionField(SecureEmailFactory secureEmailFactory, int besEncryptionState, int besWeakRecipientState, Object context) {
      super(Application.getApplication());
      this._secureEmailFactory = secureEmailFactory;
      this._utilities = this._secureEmailFactory.getUtilities();
      this._emailMessageModel = (EmailMessageModel)ContextObject.get(context, 246);
      this._besEncryptionState = besEncryptionState;
      this._besWeakRecipientState = besWeakRecipientState;
   }

   protected void initialize() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: invokestatic net/rim/device/apps/internal/secureemail/SecureEmailCache.getInstance ()Lnet/rim/device/apps/internal/secureemail/SecureEmailCache;
      // 003: astore 1
      // 004: aconst_null
      // 005: astore 2
      // 006: aconst_null
      // 007: astore 3
      // 008: bipush 0
      // 009: istore 4
      // 00b: aload 0
      // 00c: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._emailMessageModel Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;
      // 00f: ifnull 037
      // 012: aload 1
      // 013: aload 0
      // 014: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._emailMessageModel Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;
      // 017: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailCache.getSessionKey (Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;)Lnet/rim/device/api/crypto/SymmetricKey;
      // 01a: astore 2
      // 01b: aload 1
      // 01c: aload 0
      // 01d: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._emailMessageModel Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;
      // 020: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailCache.getMessageStatusData (Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;)Lnet/rim/device/apps/internal/secureemail/cache/CachedMessageStatusData;
      // 023: astore 5
      // 025: aload 5
      // 027: ifnull 037
      // 02a: aload 5
      // 02c: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedMessageStatusData.getPublicKeyEncryptionAlgorithm ()Ljava/lang/String;
      // 02f: astore 3
      // 030: aload 5
      // 032: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedMessageStatusData.getPublicKeyEncryptionBitLength ()I
      // 035: istore 4
      // 037: aload 2
      // 038: ifnonnull 050
      // 03b: aload 0
      // 03c: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField.getSessionKey ()Lnet/rim/device/api/crypto/SymmetricKey;
      // 03f: astore 2
      // 040: aload 0
      // 041: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._emailMessageModel Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;
      // 044: ifnull 050
      // 047: aload 1
      // 048: aload 0
      // 049: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._emailMessageModel Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;
      // 04c: aload 2
      // 04d: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailCache.putSessionKey (Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;Lnet/rim/device/api/crypto/SymmetricKey;)V
      // 050: aload 3
      // 051: ifnonnull 071
      // 054: aload 0
      // 055: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField.getRecipientPublicKeyAlgorithm ()Ljava/lang/String;
      // 058: astore 3
      // 059: aload 0
      // 05a: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField.getRecipientPublicKeyBitLength ()I
      // 05d: istore 4
      // 05f: aload 0
      // 060: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._emailMessageModel Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;
      // 063: ifnull 071
      // 066: aload 1
      // 067: aload 0
      // 068: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._emailMessageModel Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;
      // 06b: aload 3
      // 06c: iload 4
      // 06e: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailCache.putEncryptionStatus (Lnet/rim/device/apps/internal/blackberryemail/email/EmailMessageModel;Ljava/lang/String;I)V
      // 071: aload 0
      // 072: aload 0
      // 073: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField.getRecipientCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate;
      // 076: putfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._recipientCertificate Lnet/rim/device/api/crypto/certificate/Certificate;
      // 079: aload 0
      // 07a: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._recipientCertificate Lnet/rim/device/api/crypto/certificate/Certificate;
      // 07d: ifnull 0c8
      // 080: bipush 1
      // 081: anewarray 250
      // 084: dup
      // 085: bipush 0
      // 086: aload 0
      // 087: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._secureEmailFactory Lnet/rim/device/apps/internal/secureemail/SecureEmailFactory;
      // 08a: bipush 1
      // 08b: bipush 0
      // 08c: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.getPublicKeyContainerString (ZZ)Ljava/lang/String;
      // 08f: aastore
      // 090: astore 5
      // 092: bipush 48
      // 094: invokestatic net/rim/device/apps/internal/secureemail/SecureEmailResources.getString (I)Ljava/lang/String;
      // 097: aload 5
      // 099: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 09c: astore 6
      // 09e: aload 0
      // 09f: new java/lang/Object
      // 0a2: dup
      // 0a3: new java/lang/Object
      // 0a6: dup
      // 0a7: aload 6
      // 0a9: aload 0
      // 0aa: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._recipientCertificate Lnet/rim/device/api/crypto/certificate/Certificate;
      // 0ad: aconst_null
      // 0ae: aload 0
      // 0af: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._secureEmailFactory Lnet/rim/device/apps/internal/secureemail/SecureEmailFactory;
      // 0b2: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.getPreferredKeyStore ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 0b5: aload 0
      // 0b6: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._secureEmailFactory Lnet/rim/device/apps/internal/secureemail/SecureEmailFactory;
      // 0b9: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.getCryptoSystemProperties ()Lnet/rim/device/apps/internal/secureemail/SecureEmailCryptoSystemProperties;
      // 0bc: aconst_null
      // 0bd: invokespecial net/rim/device/apps/internal/api/crypto/verb/DisplayCertificateVerb.<init> (Ljava/lang/String;Lnet/rim/device/api/crypto/certificate/Certificate;[Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/keystore/KeyStore;Lnet/rim/device/api/crypto/CryptoSystemProperties;Lnet/rim/device/api/crypto/keystore/CertificateStatusManagerTicket;)V
      // 0c0: bipush 10
      // 0c2: invokespecial net/rim/device/apps/api/ui/VerbMenuItem.<init> (Lnet/rim/device/apps/api/framework/verb/Verb;I)V
      // 0c5: putfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._displayRecipientCertificateMenuItem Lnet/rim/device/apps/api/ui/VerbMenuItem;
      // 0c8: aload 0
      // 0c9: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField.getContentCipher ()I
      // 0cc: istore 5
      // 0ce: iload 5
      // 0d0: invokestatic net/rim/device/apps/internal/secureemail/ContentCiphers.getLabel (I)Ljava/lang/String;
      // 0d3: astore 6
      // 0d5: aload 0
      // 0d6: aload 6
      // 0d8: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField.setContentCipherAlgorithm (Ljava/lang/String;)V
      // 0db: iload 5
      // 0dd: invokestatic net/rim/device/apps/internal/secureemail/ContentCiphers.isStrong (I)Z
      // 0e0: istore 7
      // 0e2: bipush 0
      // 0e3: istore 8
      // 0e5: bipush 0
      // 0e6: istore 9
      // 0e8: bipush 0
      // 0e9: istore 10
      // 0eb: aload 0
      // 0ec: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._besWeakRecipientState I
      // 0ef: ifne 0fc
      // 0f2: aload 0
      // 0f3: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._besEncryptionState I
      // 0f6: ifeq 0fc
      // 0f9: bipush 1
      // 0fa: istore 10
      // 0fc: aload 3
      // 0fd: ifnonnull 116
      // 100: iload 7
      // 102: istore 8
      // 104: aload 0
      // 105: iload 8
      // 107: ifeq 10f
      // 10a: bipush 2
      // 10c: goto 110
      // 10f: bipush 0
      // 110: invokevirtual net/rim/device/apps/internal/secureemail/StatusField.setStatus (I)V
      // 113: goto 17c
      // 116: aload 0
      // 117: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._secureEmailFactory Lnet/rim/device/apps/internal/secureemail/SecureEmailFactory;
      // 11a: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.getCryptoSystemProperties ()Lnet/rim/device/apps/internal/secureemail/SecureEmailCryptoSystemProperties;
      // 11d: aload 3
      // 11e: iload 4
      // 120: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailCryptoSystemProperties.isCryptoSystemStrong (Ljava/lang/String;I)Z
      // 123: istore 9
      // 125: goto 12f
      // 128: astore 11
      // 12a: goto 12f
      // 12d: astore 11
      // 12f: iload 7
      // 131: ifeq 13d
      // 134: iload 9
      // 136: ifeq 13d
      // 139: bipush 1
      // 13a: goto 13e
      // 13d: bipush 0
      // 13e: istore 8
      // 140: iload 8
      // 142: ifne 14d
      // 145: aload 0
      // 146: bipush 0
      // 147: invokevirtual net/rim/device/apps/internal/secureemail/StatusField.setStatus (I)V
      // 14a: goto 17c
      // 14d: aload 0
      // 14e: getfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._besEncryptionState I
      // 151: lookupswitch 28 1 0 19
      // 164: aload 0
      // 165: bipush 3
      // 167: invokevirtual net/rim/device/apps/internal/secureemail/StatusField.setStatus (I)V
      // 16a: goto 17c
      // 16d: aload 0
      // 16e: iload 10
      // 170: ifeq 178
      // 173: bipush 4
      // 175: goto 179
      // 178: bipush 1
      // 179: invokevirtual net/rim/device/apps/internal/secureemail/StatusField.setStatus (I)V
      // 17c: iload 8
      // 17e: ifeq 186
      // 181: iload 10
      // 183: ifeq 1b3
      // 186: aload 0
      // 187: new java/lang/Object
      // 18a: dup
      // 18b: new net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField$DisplayWeakEncryptionReasonVerb
      // 18e: dup
      // 18f: iload 7
      // 191: ifeq 198
      // 194: aconst_null
      // 195: goto 19a
      // 198: aload 6
      // 19a: iload 9
      // 19c: ifeq 1a3
      // 19f: aconst_null
      // 1a0: goto 1a4
      // 1a3: aload 3
      // 1a4: iload 4
      // 1a6: iload 10
      // 1a8: invokespecial net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField$DisplayWeakEncryptionReasonVerb.<init> (Ljava/lang/String;Ljava/lang/String;IZ)V
      // 1ab: bipush 10
      // 1ad: invokespecial net/rim/device/apps/api/ui/VerbMenuItem.<init> (Lnet/rim/device/apps/api/framework/verb/Verb;I)V
      // 1b0: putfield net/rim/device/apps/internal/secureemail/SecureEmailEncryptionField._displayWeakEncryptionReasonMenuItem Lnet/rim/device/apps/api/ui/VerbMenuItem;
      // 1b3: aload 0
      // 1b4: invokevirtual net/rim/device/apps/internal/secureemail/StatusField.updateStatus ()V
      // 1b7: return
      // try (140 -> 147): 148 null
      // try (140 -> 147): 150 null
   }

   @Override
   public void makeDelegateContextMenu(ContextMenu contextMenu) {
      if (this._displayRecipientCertificateMenuItem != null) {
         contextMenu.addItem(this._displayRecipientCertificateMenuItem);
      }

      if (this._displayWeakEncryptionReasonMenuItem != null) {
         contextMenu.addItem(this._displayWeakEncryptionReasonMenuItem);
      }

      super.makeDelegateContextMenu(contextMenu);
   }

   @Override
   protected Image getImage() {
      int status = this.getStatus();
      switch (status) {
         case 0:
            return CryptoIcons.getImage(12);
         case 1:
         case 2:
         case 3:
         default:
            return CryptoIcons.getImage(11);
      }
   }

   @Override
   protected String getText() {
      if (this._contentCipherAlgorithm == null) {
         return SecureEmailResources.getString(50);
      }

      String[] messageFormatParameters = new Object[]{this._secureEmailFactory.getEncodingString(), this._contentCipherAlgorithm};
      int status = this.getStatus();
      int messageFormatResourceID;
      switch (status) {
         case 0:
            messageFormatResourceID = 53;
            break;
         case 1:
         case 4:
         default:
            messageFormatResourceID = 52;
            break;
         case 2:
            messageFormatResourceID = 107;
            messageFormatParameters = new Object[]{this._secureEmailFactory.getEncodingString()};
            break;
         case 3:
            messageFormatResourceID = 142;
      }

      return MessageFormat.format(SecureEmailResources.getString(messageFormatResourceID), messageFormatParameters);
   }

   @Override
   public String getShortText() {
      int status = this.getStatus();
      switch (status) {
         case 0:
            return SecureEmailResources.getString(54);
         case 1:
         case 2:
         case 4:
         default:
            return SecureEmailResources.getString(51);
         case 3:
            return SecureEmailResources.getString(143);
      }
   }

   @Override
   public int getPriority() {
      int status = this.getStatus();
      switch (status) {
         case 0:
            return 7000;
         case 1:
         case 2:
         case 3:
         default:
            return 2000;
      }
   }

   @Override
   public long getStatusType() {
      return 356383575838032505L;
   }

   public void setContentCipherAlgorithm(String contentCipherAlgorithm) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   protected SymmetricKey getSessionKey() {
      throw null;
   }

   protected String getRecipientPublicKeyAlgorithm() {
      throw null;
   }

   protected int getRecipientPublicKeyBitLength() {
      throw null;
   }

   protected Certificate getRecipientCertificate() {
      throw null;
   }

   protected int getContentCipher() {
      throw null;
   }
}
