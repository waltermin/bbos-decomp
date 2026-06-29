package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.vm.Array;

public class EMSEmailHeaderModel extends EmailHeaderModel implements PersistableRIMModel, EncryptableProvider {
   private EmailHeaderModel[] _innerEmailHeaderModels = new Object[0];
   static final byte EMS_ADDRESS = 1;
   static final byte TO_ADDRESS = 2;
   static final byte CC_ADDRESS = 3;
   static final byte BCC_ADDRESS = 4;
   static final byte STRING_PAIR_0 = 5;
   static final byte STRING_PAIR_1 = 6;

   public EMSEmailHeaderModel(String[] stringPair, ContextObject context) {
      super(stringPair, context);
   }

   public EMSEmailHeaderModel(Object initialData) {
      super(initialData);
   }

   @Override
   public int getHeaderType() {
      return 0;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof EMSEmailHeaderModel)) {
         return false;
      }

      EMSEmailHeaderModel emsEmailHeaderModel = (EMSEmailHeaderModel)object;
      if (!super.equals(emsEmailHeaderModel)) {
         return false;
      }

      int numInnerHeaderModels = this._innerEmailHeaderModels.length;
      if (numInnerHeaderModels != emsEmailHeaderModel._innerEmailHeaderModels.length) {
         return false;
      }

      EmailHeaderModel[] innerEmailHeaderModelsCopy = new Object[numInnerHeaderModels];
      System.arraycopy(this._innerEmailHeaderModels, 0, innerEmailHeaderModelsCopy, 0, numInnerHeaderModels);

      for (int i = 0; i < numInnerHeaderModels; i++) {
         int index = Arrays.getIndex(innerEmailHeaderModelsCopy, emsEmailHeaderModel._innerEmailHeaderModels[i]);
         if (index < 0) {
            return false;
         }

         innerEmailHeaderModelsCopy[index] = null;
      }

      return true;
   }

   public void addDisplayModel(EmailHeaderModel newModel) {
      String newAddressLowerCase = this.getEmailAddressLowerCase(newModel);
      if (newAddressLowerCase != null) {
         int numExistingModels = this._innerEmailHeaderModels.length;

         for (int i = 0; i < numExistingModels; i++) {
            String currentAddressLowerCase = this.getEmailAddressLowerCase(this._innerEmailHeaderModels[i]);
            if (newAddressLowerCase.equals(currentAddressLowerCase)) {
               return;
            }
         }

         Arrays.add(this._innerEmailHeaderModels, newModel);
      }
   }

   private String getEmailAddressLowerCase(EmailHeaderModel emailHeaderModel) {
      String newEmailAddress = null;
      RIMModel insideModel = emailHeaderModel.getInsideModel();
      if (insideModel instanceof Object) {
         newEmailAddress = ((FriendlyNameAddressModel)insideModel).getAddress();
         if (newEmailAddress != null) {
            newEmailAddress = StringUtilities.toLowerCase(newEmailAddress, 1701707776);
         }
      }

      return newEmailAddress;
   }

   @Override
   protected EmailHeaderModel newInstance(Object initialData) {
      return new EMSEmailHeaderModel(initialData);
   }

   @Override
   public Object clone(Object context) {
      EMSEmailHeaderModel newModel = (EMSEmailHeaderModel)super.clone(context);
      int numInnerModels = this._innerEmailHeaderModels.length;

      for (int i = 0; i < numInnerModels; i++) {
         EmailHeaderModel newInnerModel = (EmailHeaderModel)this._innerEmailHeaderModels[i].clone(context);
         newModel.addDisplayModel(newInnerModel);
      }

      return newModel;
   }

   @Override
   public Field getField(Object context) {
      int numInnerModels = this._innerEmailHeaderModels.length;
      Field[] innerFields = new Object[numInnerModels];
      int[] innerFieldOrders = new int[numInnerModels];
      int innerFieldCount = 0;

      for (int i = 0; i < numInnerModels; i++) {
         Field innerField = this._innerEmailHeaderModels[i].getField(context);
         if (innerField != null) {
            innerFields[innerFieldCount] = innerField;
            innerFieldOrders[innerFieldCount] = this._innerEmailHeaderModels[i].getOrder(context);
            innerFieldCount++;
         }
      }

      Array.resize(innerFields, innerFieldCount);
      Array.resize(innerFieldOrders, innerFieldCount);
      Arrays.sort(innerFieldOrders, 0, innerFieldCount, innerFields);
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object(1152921504606846976L));

      for (int i = 0; i < innerFieldCount; i++) {
         vfm.add(innerFields[i]);
      }

      return vfm;
   }

   @Override
   public void paint(ColumnPainter columnPainter, Object context) {
      int numInnerModels = this._innerEmailHeaderModels.length;

      for (int i = 0; i < numInnerModels; i++) {
         this._innerEmailHeaderModels[i].paint(columnPainter, context);
      }
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 19)) {
         DataBuffer allData = (DataBuffer)(new Object());
         DataBuffer currentAddressData = (DataBuffer)(new Object());
         String[] stringPair = new Object[2];
         this.convert(null, stringPair);
         this.writeStringPair(allData, currentAddressData, (byte)1, stringPair);
         int numInnerModels = this._innerEmailHeaderModels.length;

         for (int i = 0; i < numInnerModels; i++) {
            EmailHeaderModel currentModel = this._innerEmailHeaderModels[i];
            byte headerTypeByte;
            switch (currentModel.getHeaderType()) {
               case 0:
               default:
                  headerTypeByte = 2;
                  break;
               case 1:
                  headerTypeByte = 3;
                  break;
               case 2:
                  headerTypeByte = 4;
            }

            currentModel.convert(null, stringPair);
            this.writeStringPair(allData, currentAddressData, headerTypeByte, stringPair);
         }

         ((SyncBuffer)target).addBytes(71, allData.toArray());
         return true;
      } else if (target instanceof Object && ContextObject.getFlag(context, 70)) {
         int numInnerModels = this._innerEmailHeaderModels.length;

         for (int i = 0; i < numInnerModels; i++) {
            this._innerEmailHeaderModels[i].convert(context, target);
         }

         return true;
      } else if (target instanceof Object[] && ContextObject.getFlag(context, 124)) {
         String[] conversionResult = (Object[])target;
         Array.resize(conversionResult, 0);
         int numInnerModels = this._innerEmailHeaderModels.length;

         for (int i = 0; i < numInnerModels; i++) {
            String[] currentAddressAndName = new Object[2];
            this._innerEmailHeaderModels[i].convert(context, currentAddressAndName);
            int oldConversionResultLength = conversionResult.length;
            Array.resize(target, oldConversionResultLength + currentAddressAndName.length);
            System.arraycopy(currentAddressAndName, 0, conversionResult, oldConversionResultLength, currentAddressAndName.length);
         }

         return true;
      } else {
         return super.convert(context, target);
      }
   }

   private void writeStringPair(DataBuffer allData, DataBuffer currentAddressData, byte currentAddressType, String[] stringPair) {
      currentAddressData.reset();
      if (stringPair[0] != null) {
         ConverterUtilities.writeString(currentAddressData, 5, stringPair[0]);
      }

      if (stringPair[1] != null) {
         ConverterUtilities.writeString(currentAddressData, 6, stringPair[1]);
      }

      ConverterUtilities.writeByteArray(allData, currentAddressType, currentAddressData.toArray());
   }

   @Override
   public int match(Object criteria) {
      int matchResult = -1;
      int numInnerModels = this._innerEmailHeaderModels.length;

      for (int i = 0; i < numInnerModels; i++) {
         int innerMatchResult = this._innerEmailHeaderModels[i].match(criteria);
         switch (innerMatchResult) {
            case 0:
               matchResult = 0;
               break;
            case -1:
            case 1:
            default:
               return 1;
         }
      }

      return matchResult;
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      int numInnerModels = this._innerEmailHeaderModels.length;

      for (int i = 0; i < numInnerModels; i++) {
         if (!this._innerEmailHeaderModels[i].checkCrypt(compress, encrypt)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      int numInnerModels = this._innerEmailHeaderModels.length;

      for (int i = 0; i < numInnerModels; i++) {
         Object reCryptedModel = this._innerEmailHeaderModels[i].reCrypt(compress, encrypt);
         if (reCryptedModel != null) {
            this._innerEmailHeaderModels[i] = (EmailHeaderModel)reCryptedModel;
         }
      }

      return null;
   }
}
