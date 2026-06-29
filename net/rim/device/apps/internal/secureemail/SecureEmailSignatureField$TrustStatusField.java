package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.DecodeException;
import net.rim.device.api.crypto.InvalidSignatureEncodingException;
import net.rim.device.api.crypto.NoSuchAlgorithmException;
import net.rim.device.api.crypto.certificate.CertificateInvalidException;
import net.rim.device.api.crypto.certificate.CertificateRevokedException;
import net.rim.device.api.crypto.certificate.CertificateVerificationException;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.internal.resource.crypto.CryptoIcons;
import net.rim.device.internal.ui.Image;

public class SecureEmailSignatureField$TrustStatusField extends StatusField {
   protected String _details;
   private final SecureEmailSignatureField this$0;
   private static final long STATUS_TYPE_TRUST = -7654458812076809197L;
   public static final int VERIFYING = 0;
   public static final int TRUSTED = 1;
   public static final int NOT_TRUSTED = 2;
   public static final int EXPIRED = 3;
   public static final int REVOKED = 4;
   public static final int ERROR = 5;
   public static final int NO_SENDER_CERT = 6;
   public static final int NO_SENDER_CERT_REQUESTING = 7;
   public static final int EMAIL_ADDRESS_NOMATCH = 8;
   public static final int NOT_AVAILABLE = 9;
   public static final int STALE = 10;
   public static final int STALE_REQUESTING = 11;
   public static final int WEAK = 12;
   public static final int UNVERIFIABLE = 13;
   public static final int LONG_DELAY = 14;

   public SecureEmailSignatureField$TrustStatusField(SecureEmailSignatureField _1, Application app, int initialStatus, String details) {
      super(app, initialStatus);
      this.this$0 = _1;
      this.setDetails(details);
   }

   @Override
   protected Image getImage() {
      int status = this.getStatus();
      switch (status) {
         case -1:
         case 5:
            return CryptoIcons.getImage(2);
         case 0:
         case 7:
         case 11:
         default:
            return CryptoIcons.getImage(13);
         case 1:
            return CryptoIcons.getImage(6);
         case 2:
         case 4:
         case 6:
         case 8:
         case 13:
            return CryptoIcons.getImage(4);
         case 3:
         case 14:
            return CryptoIcons.getImage(3);
         case 9:
         case 10:
         case 12:
            return CryptoIcons.getImage(5);
      }
   }

   @Override
   protected String getText() {
      String containerChainStringLower = this.this$0._secureEmailFactory.getPublicKeyContainerChainString(false);
      String containerStringLowerSingular = this.this$0._secureEmailFactory.getPublicKeyContainerString(false, false);
      int status = this.getStatus();
      switch (status) {
         case -1:
         case 5:
            return MessageFormat.format(SecureEmailResources.getString(79), new String[]{this._details});
         case 0:
         default:
            return SecureEmailResources.getString(71);
         case 1:
            return MessageFormat.format(SecureEmailResources.getString(72), new String[]{containerChainStringLower});
         case 2:
            return MessageFormat.format(SecureEmailResources.getString(73), new String[]{containerChainStringLower});
         case 3:
            return MessageFormat.format(SecureEmailResources.getString(74), new String[]{containerChainStringLower});
         case 4:
            return MessageFormat.format(SecureEmailResources.getString(76), new String[]{containerChainStringLower});
         case 6:
            return MessageFormat.format(SecureEmailResources.getString(77), new String[]{containerStringLowerSingular});
         case 7:
            return MessageFormat.format(SecureEmailResources.getString(156), new String[]{containerStringLowerSingular});
         case 8:
            return MessageFormat.format(SecureEmailResources.getString(75), new String[]{containerStringLowerSingular});
         case 9:
            return SecureEmailResources.getString(78);
         case 10:
            return MessageFormat.format(SecureEmailResources.getString(131), new String[]{containerStringLowerSingular});
         case 11:
            return MessageFormat.format(SecureEmailResources.getString(133), new String[]{containerStringLowerSingular});
         case 12:
            return MessageFormat.format(SecureEmailResources.getString(144), new String[]{containerStringLowerSingular});
         case 13:
            return MessageFormat.format(SecureEmailResources.getString(154), new String[]{containerChainStringLower});
         case 14:
            return SecureEmailResources.getString(177);
      }
   }

   @Override
   public String getShortText() {
      int status = this.getStatus();
      switch (status) {
         case -1:
         case 5:
            return SecureEmailResources.getString(86);
         case 0:
         default:
            return SecureEmailResources.getString(65);
         case 1:
            return SecureEmailResources.getString(80);
         case 2:
            return SecureEmailResources.getString(81);
         case 3:
            return SecureEmailResources.getString(82);
         case 4:
            return SecureEmailResources.getString(83);
         case 6:
            return SecureEmailResources.getString(85);
         case 7:
            return SecureEmailResources.getString(157);
         case 8:
            return SecureEmailResources.getString(84);
         case 9:
            return SecureEmailResources.getString(179);
         case 10:
            return SecureEmailResources.getString(132);
         case 11:
            return SecureEmailResources.getString(134);
         case 12:
            return SecureEmailResources.getString(145);
         case 13:
            return SecureEmailResources.getString(155);
         case 14:
            return SecureEmailResources.getString(178);
      }
   }

   @Override
   public int getPriority() {
      int status = this.getStatus();
      switch (status) {
         case -1:
         case 5:
            return 18000;
         case 0:
         default:
            return 9000;
         case 1:
            return 4000;
         case 2:
            return 11000;
         case 3:
         case 14:
            return 12000;
         case 4:
            return 13000;
         case 6:
         case 7:
            return 14000;
         case 8:
            return 15000;
         case 9:
            return 19000;
         case 10:
         case 11:
            return 6000;
         case 12:
            return 8000;
         case 13:
            return 10500;
      }
   }

   @Override
   public long getStatusType() {
      return -7654458812076809197L;
   }

   @Override
   public void makeDelegateContextMenu(ContextMenu contextMenu) {
      this.this$0.makeDelegateContextMenu(contextMenu);
      super.makeDelegateContextMenu(contextMenu);
   }

   public void setThrowable(Throwable t) {
      SecureEmailSignatureField$ThrowableHandlerData throwableHandlerData = new SecureEmailSignatureField$ThrowableHandlerData();
      this.handleThrowable(t, throwableHandlerData);
      if (!throwableHandlerData._unrecoverableThrowable && this.this$0._moreAvailable) {
         this.setStatus(9);
      } else {
         this.setStatus(throwableHandlerData._status);
         if (throwableHandlerData._details != null) {
            this.setDetails(throwableHandlerData._details);
         }
      }
   }

   public void handleThrowable(Throwable t, SecureEmailSignatureField$ThrowableHandlerData throwableHandlerData) {
      if (t instanceof CertificateVerificationException) {
         throwableHandlerData._status = 2;
         throwableHandlerData._unrecoverableThrowable = true;
      } else if (t instanceof CertificateRevokedException) {
         throwableHandlerData._status = 4;
         throwableHandlerData._unrecoverableThrowable = true;
      } else if (t instanceof CertificateInvalidException) {
         throwableHandlerData._status = 3;
         throwableHandlerData._unrecoverableThrowable = true;
      } else if (t instanceof DecodeException || t instanceof InvalidSignatureEncodingException) {
         throwableHandlerData._status = 5;
         throwableHandlerData._details = SecureEmailResources.getString(87);
         throwableHandlerData._unrecoverableThrowable = true;
      } else if (t instanceof NoSuchAlgorithmException) {
         throwableHandlerData._status = 5;
         throwableHandlerData._details = SecureEmailResources.getString(88);
         throwableHandlerData._unrecoverableThrowable = true;
      } else if (t instanceof CryptoUnsupportedOperationException) {
         throwableHandlerData._status = 5;
         throwableHandlerData._details = SecureEmailResources.getString(69);
         throwableHandlerData._unrecoverableThrowable = true;
      } else {
         throwableHandlerData._status = 5;
         throwableHandlerData._details = SecureEmailResources.getString(70);
      }
   }

   public void setDetails(String details) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public String getDetails() {
      return this._details;
   }

   protected int getOverallSignatureStatus() {
      int status = this.getStatus();
      switch (status) {
         case 0:
         case 7:
         case 9:
         case 10:
         case 11:
         case 12:
            return 1;
         case 1:
            return 2;
         default:
            return 0;
      }
   }

   @Override
   public void updateStatus() {
      super.updateStatus();
      this.this$0.updateManagerExtentIndicator();
   }
}
