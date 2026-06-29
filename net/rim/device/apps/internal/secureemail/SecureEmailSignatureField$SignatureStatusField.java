package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerEnrollmentException;
import net.rim.device.internal.resource.crypto.CryptoIcons;
import net.rim.device.internal.ui.Image;

public class SecureEmailSignatureField$SignatureStatusField extends StatusField {
   protected String _signerName;
   protected String _details;
   private final SecureEmailSignatureField this$0;
   private static final long STATUS_TYPE_SIGNATURE;
   public static final int VERIFYING;
   public static final int VERIFIED;
   public static final int FAILED;
   public static final int UNKNOWN_ON_DEVICE;
   public static final int ERROR;
   public static final int WEAK_DIGEST;

   public SecureEmailSignatureField$SignatureStatusField(SecureEmailSignatureField _1, Application app, int initialStatus, String signerName, String details) {
      super(app, initialStatus);
      this.this$0 = _1;
      this.setSignerName(signerName);
      this.setDetails(details);
   }

   @Override
   protected Image getImage() {
      int status = this.getStatus();
      switch (status) {
         case -1:
         case 4:
            return CryptoIcons.getImage(9);
         case 0:
         default:
            return CryptoIcons.getImage(13);
         case 1:
            return CryptoIcons.getImage(8);
         case 2:
            return CryptoIcons.getImage(9);
         case 3:
            switch (this.this$0._besVerificationState) {
               case -1:
                  return CryptoIcons.getImage(10);
               case 0:
               default:
                  return CryptoIcons.getImage(20);
               case 1:
                  return CryptoIcons.getImage(21);
            }
         case 5:
            return CryptoIcons.getImage(10);
      }
   }

   @Override
   protected String getText() {
      String encodingString = this.this$0._secureEmailFactory.getEncodingString();
      int status = this.getStatus();
      switch (status) {
         case -1:
         case 4:
            return MessageFormat.format(SecureEmailResources.getString(67), new Object[]{encodingString});
         case 0:
         default:
            return SecureEmailResources.getString(59);
         case 1:
            if (this._signerName == null) {
               return MessageFormat.format(SecureEmailResources.getString(60), new Object[]{encodingString});
            }

            return MessageFormat.format(SecureEmailResources.getString(61), new Object[]{encodingString, this._signerName});
         case 2:
            return MessageFormat.format(SecureEmailResources.getString(63), new Object[]{this._details});
         case 3:
            if (this.this$0._besNoVerifyReason != null) {
               String patternString = null;
               int besNoVerifyReasonInt = Arrays.getIndex(SecureEmailConstants.CANNOT_VERIFY_ON_DEVICE_REASONS, this.this$0._besNoVerifyReason);
               switch (besNoVerifyReasonInt) {
                  case -1:
                     patternString = SecureEmailResources.getString(124);
                     break;
                  case 0:
                  default:
                     patternString = SecureEmailResources.getString(128);
                     break;
                  case 1:
                     patternString = SecureEmailResources.getString(126);
               }

               String text = MessageFormat.format(patternString, new Object[]{this.this$0._secureEmailFactory.getEncodingString()});
               switch (this.this$0._besVerificationState) {
                  case 0:
                  case 1:
                  default:
                     text = ((StringBuffer)(new Object())).append(text).append(this.getBESVerificationSuffix()).toString();
                  case -1:
                     return text;
               }
            } else {
               switch (this.this$0._besVerificationState) {
                  case -1:
                     if (this.this$0._moreAvailable) {
                        return MessageFormat.format(SecureEmailResources.getString(66), new Object[]{encodingString});
                     }

                     return MessageFormat.format(SecureEmailResources.getString(124), new Object[]{encodingString});
                  case 0:
                  case 1:
                  default:
                     return ((StringBuffer)(new Object()))
                        .append(MessageFormat.format(SecureEmailResources.getString(60), new Object[]{encodingString}))
                        .append(this.getBESVerificationSuffix())
                        .toString();
               }
            }
         case 5:
            return this._signerName == null
               ? MessageFormat.format(SecureEmailResources.getString(187), new Object[]{encodingString})
               : MessageFormat.format(SecureEmailResources.getString(188), new Object[]{encodingString, this._signerName});
      }
   }

   private String getBESVerificationSuffix() {
      switch (this.this$0._besVerificationState) {
         case -1:
            return null;
         case 0:
         default:
            return SecureEmailResources.getString(116);
         case 1:
            return SecureEmailResources.getString(119);
      }
   }

   @Override
   public String getShortText() {
      int status = this.getStatus();
      switch (status) {
         case -1:
         case 4:
            return SecureEmailResources.getString(68);
         case 0:
         default:
            return SecureEmailResources.getString(65);
         case 1:
            return SecureEmailResources.getString(62);
         case 2:
            return SecureEmailResources.getString(64);
         case 3:
            if (this.this$0._besNoVerifyReason != null) {
               int besNoVerifyReasonInt = Arrays.getIndex(SecureEmailConstants.CANNOT_VERIFY_ON_DEVICE_REASONS, this.this$0._besNoVerifyReason);
               switch (besNoVerifyReasonInt) {
                  case -1:
                     return SecureEmailResources.getString(125);
                  case 0:
                  default:
                     return SecureEmailResources.getString(129);
                  case 1:
                     return SecureEmailResources.getString(127);
               }
            } else {
               switch (this.this$0._besVerificationState) {
                  case -1:
                     if (this.this$0._moreAvailable) {
                        return SecureEmailResources.getString(179);
                     }

                     return SecureEmailResources.getString(125);
                  case 0:
                  default:
                     return SecureEmailResources.getString(122);
                  case 1:
                     return SecureEmailResources.getString(123);
               }
            }
         case 5:
            return SecureEmailResources.getString(189);
      }
   }

   @Override
   public int getPriority() {
      int status = this.getStatus();
      switch (status) {
         case -1:
         case 4:
            return 10000;
         case 0:
         default:
            return 9000;
         case 1:
            return 3000;
         case 2:
            return 17000;
         case 3:
            if (this.this$0._besNoVerifyReason != null) {
               return 10000;
            } else {
               switch (this.this$0._besVerificationState) {
                  case -1:
                     if (this.this$0._moreAvailable) {
                        return 19000;
                     }

                     return 10000;
                  case 0:
                  default:
                     return 5000;
                  case 1:
                     return 16000;
               }
            }
         case 5:
            return 8000;
      }
   }

   @Override
   public long getStatusType() {
      return -6760830185273366870L;
   }

   public void setSignerName(String signerName) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public String getSignerName() {
      return this._signerName;
   }

   @Override
   public void makeDelegateContextMenu(ContextMenu contextMenu) {
      this.this$0.makeDelegateContextMenu(contextMenu);
      super.makeDelegateContextMenu(contextMenu);
   }

   public void setThrowable(Throwable t) {
      SecureEmailSignatureField$ThrowableHandlerData throwableHandlerData = new SecureEmailSignatureField$ThrowableHandlerData();
      this.handleThrowable(t, throwableHandlerData);
      if (throwableHandlerData._unrecoverableThrowable || this.this$0._besVerificationState == -1 && !this.this$0._moreAvailable) {
         this.setStatus(throwableHandlerData._status);
         if (throwableHandlerData._details != null) {
            this.setDetails(throwableHandlerData._details);
         }
      } else {
         this.setStatus(3);
      }
   }

   public void handleThrowable(Throwable t, SecureEmailSignatureField$ThrowableHandlerData throwableHandlerData) {
      if (t instanceof Object) {
         throwableHandlerData._status = 2;
         throwableHandlerData._details = SecureEmailResources.getString(69);
         throwableHandlerData._unrecoverableThrowable = true;
      } else if (t instanceof SecureEmailServerEnrollmentException) {
         throwableHandlerData._status = 2;
         throwableHandlerData._details = SecureEmailResources.getString(171);
         throwableHandlerData._unrecoverableThrowable = true;
      } else if (t instanceof SecureEmailWeakHashDigestException) {
         throwableHandlerData._status = 5;
         throwableHandlerData._details = SecureEmailResources.getString(190);
         throwableHandlerData._unrecoverableThrowable = true;
      } else {
         throwableHandlerData._status = 2;
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
         case -1:
         case 2:
         case 4:
            return 0;
         case 0:
         case 5:
         default:
            return 1;
         case 1:
            return 2;
         case 3:
            switch (this.this$0._besVerificationState) {
               case 1:
                  return 0;
               default:
                  return 1;
            }
      }
   }

   @Override
   public void updateStatus() {
      super.updateStatus();
      this.this$0.updateManagerExtentIndicator();
   }
}
