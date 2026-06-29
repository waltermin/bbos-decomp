package net.rim.device.apps.internal.implus;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

final class IMPlusRequestOptionModelImpl implements RIMModel, VerbProvider, FieldProvider {
   private byte _type;
   private boolean _default;
   private int _msgComposeOptionSetting;
   private IMPlusCmimeListener$ReceiptCapableService _receiptCapableService;
   private static final byte CONFIRM_DELIVERY_TYPE;
   private static final byte CONFIRM_READ_TYPE;
   private static final byte ALLOW_READ_CONFIRM_TYPE;

   final void initialize(Object context) {
      this._msgComposeOptionSetting = 0;
      if (context instanceof Object) {
         this._default = true;
         if (((ContextObject)context).getFlag(75)) {
            this._type = 0;
         } else if (((ContextObject)context).getFlag(73)) {
            this._type = 2;
         } else {
            this._type = 1;
         }

         int serviceRecId = ContextObject.getIntegerData(context, -1);
         if (serviceRecId != -1) {
            this._receiptCapableService = IMPlusCmimeListener.getInstance().findReceiptCapableService(serviceRecId);
            if (this._receiptCapableService != null) {
               this._msgComposeOptionSetting = this.getPreference();
            }
         }

         Object message = ContextObject.get(context, 254);
         if (message instanceof Object) {
            EmailMessageModel emailMsgModel = (EmailMessageModel)message;
            this._msgComposeOptionSetting = 1;
            ServiceRecord serviceRec = emailMsgModel.getServiceRecordForMessage();
            if (serviceRec == null) {
               serviceRec = (ServiceRecord)ContextObject.get(context, -6095803566992128485L);
            }

            if (serviceRec != null) {
               this._receiptCapableService = IMPlusCmimeListener.getInstance().findReceiptCapableService(serviceRec.getId());
            }

            int flags = emailMsgModel.getFlags();
            if ((flags & 4194304) != 0) {
               if (this._type == 0) {
                  this._msgComposeOptionSetting = (flags & 2097152) != 0 ? 1 : 0;
               } else {
                  this._msgComposeOptionSetting = (flags & 64) != 0 ? 1 : 0;
               }
            } else if (this._receiptCapableService != null) {
               this._msgComposeOptionSetting = this.getPreference();
            }

            this._default = false;
         }
      }
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      return null;
   }

   public final int getPreference() {
      int returnVal = 0;
      if (!this._default) {
         returnVal = this._msgComposeOptionSetting;
      } else {
         if (this._receiptCapableService == null) {
            return returnVal;
         }

         ServiceRecord serviceRecord = ServiceBook.getSB().getRecordById(this._receiptCapableService._serviceRecId);
         if (serviceRecord == null) {
            return returnVal;
         }

         MessageListOptions msgListOptions = MessageListOptions.getOptions();
         byte dsnSettings = msgListOptions.getDSNSettings(serviceRecord.getName(), serviceRecord.getUid());
         if (dsnSettings != 0) {
            switch (this._type) {
               case -1:
                  if ((byte)(8 & dsnSettings) != 0) {
                     return 1;
                  }

                  if ((byte)(16 & dsnSettings) != 0) {
                     return 2;
                  }
                  break;
               case 0:
               default:
                  return (byte)(2 & dsnSettings) != 0 ? 1 : 0;
               case 1:
                  return (byte)(4 & dsnSettings) != 0 ? 1 : 0;
            }
         } else {
            if (this._type == 0 && this._receiptCapableService._enableConfirmDeliveryUI) {
               return this._receiptCapableService._defaultConfirmDelivery ? 1 : 0;
            }

            if (this._type == 1 && this._receiptCapableService._enableConfirmReadUI) {
               return this._receiptCapableService._defaultConfirmRead ? 1 : 0;
            }

            if (this._type == 2) {
               if (this._receiptCapableService._allowReadConfirmUI) {
                  return this._receiptCapableService._defaultAllowReadConfirm;
               }

               return 2;
            }
         }
      }

      return returnVal;
   }

   @Override
   public final Field getField(Object context) {
      if (this._receiptCapableService == null) {
         return null;
      }

      if (this._type == 0 && this._receiptCapableService._enableConfirmDeliveryUI
         || this._type == 1 && this._receiptCapableService._enableConfirmReadUI
         || this._type == 2 && this._receiptCapableService._allowReadConfirmUI) {
         String[] yesOrNo = CommonResources.getYesNoArray(0);
         int optionChoice = this.getPreference();
         int choiceIndex;
         if (this._type == 2) {
            Arrays.add(yesOrNo, IMPlusResources.getString(24));
            switch (optionChoice) {
               case 0:
                  choiceIndex = 1;
                  break;
               case 1:
                  choiceIndex = 0;
                  break;
               case 2:
               default:
                  choiceIndex = 2;
            }
         } else {
            choiceIndex = optionChoice == 1 ? 0 : 1;
         }

         int stringID;
         switch (this._type) {
            case -1:
               stringID = 6;
               break;
            case 0:
            default:
               stringID = 4;
               break;
            case 1:
               stringID = 5;
         }

         ObjectChoiceField field = (ObjectChoiceField)(new Object(IMPlusResources.getString(stringID), yesOrNo, choiceIndex));
         field.setCookie(this);
         return field;
      } else {
         return null;
      }
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      boolean returnVal = false;
      if (field instanceof Object) {
         EmailMessageModel message = null;
         if (context != null) {
            message = (EmailMessageModel)ContextObject.get(context, 254);
         }

         int choiceIndex = ((ChoiceField)field).getSelectedIndex();
         if (this._default) {
            if (this._receiptCapableService == null) {
               return false;
            }

            ServiceRecord serviceRecord = ServiceBook.getSB().getRecordById(this._receiptCapableService._serviceRecId);
            if (serviceRecord == null) {
               return false;
            }

            MessageListOptions msgListOptions;
            byte dsnSettings;
            msgListOptions = MessageListOptions.getOptions();
            dsnSettings = msgListOptions.getDSNSettings(serviceRecord.getName(), serviceRecord.getUid());
            label53:
            switch (this._type) {
               case -1:
                  switch (choiceIndex) {
                     case 0:
                        dsnSettings = (byte)(dsnSettings | 8);
                        dsnSettings = (byte)(dsnSettings & -17);
                        break label53;
                     case 1:
                        dsnSettings = -25;
                        break label53;
                     case 2:
                     default:
                        dsnSettings = (byte)(dsnSettings | 16);
                        dsnSettings = (byte)(dsnSettings & -9);
                        break label53;
                  }
               case 0:
               default:
                  dsnSettings = choiceIndex == 0 ? (byte)(2 | dsnSettings) : (byte)(-3 & dsnSettings);
                  break;
               case 1:
                  dsnSettings = choiceIndex == 0 ? (byte)(4 | dsnSettings) : (byte)(-5 & dsnSettings);
            }

            msgListOptions.setDSNSettings(serviceRecord.getName(), serviceRecord.getUid(), dsnSettings);
            msgListOptions.commit();
            return true;
         }

         if (message != null) {
            this._msgComposeOptionSetting = choiceIndex == 0 ? 1 : 0;
            if (this._msgComposeOptionSetting == 1) {
               if (this._type == 0) {
                  message.setFlags(2097152);
               } else {
                  message.setFlags(64);
               }
            } else if (this._type == 0) {
               message.clearFlags(2097152);
            } else {
               message.clearFlags(64);
            }

            message.setFlags(4194304);
            returnVal = true;
         }
      }

      return returnVal;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final int getOrder(Object context) {
      return 1;
   }

   IMPlusRequestOptionModelImpl(Object context) {
      this.initialize(context);
   }
}
