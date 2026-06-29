package net.rim.device.apps.internal.implus;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.SyncFieldIDProvider;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.implus.InteractiveHHAddressModel;

final class InteractiveHHAddressModelImpl
   implements InteractiveHHAddressModel,
   VerbProvider,
   FieldProvider,
   PaintProvider,
   KeyProvider,
   ConversionProvider,
   MatchProvider,
   VerbDescriptionProvider,
   SyncFieldIDProvider {
   private Object _dataEncoding;

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context) {
      return IMPlusUtilities.paint(this, this.getData(), g, x, y, width, height, context);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      IMPlusCmimeListener$ReceiptCapableService receiptCapableService = IMPlusCmimeListener.getInstance().getIMPlusReceiptCapableService();
      return receiptCapableService != null && receiptCapableService._twoWayPageEnabled
         ? IMPlusUtilities.getVerbs(this, 4439968724864684903L, 327957, context, verbs)
         : null;
   }

   @Override
   public final int match(Object criteria) {
      return IMPlusUtilities.match(this, this.getData(), criteria);
   }

   @Override
   public final String getFriendlyName() {
      return IMPlusUtilities.getFriendlyName(this.getData());
   }

   @Override
   public final String getVerbDescription(Object context) {
      return IMPlusUtilities.getVerbDescription(this, this.getData(), context);
   }

   @Override
   public final boolean convert(Object context, Object target) {
      return IMPlusUtilities.convert(this, this.getData(), this.getSyncFieldId(null), "\rInteractive Handheld:", context, target);
   }

   @Override
   public final void setAddressAndFriendlyName(String address, String fn) {
      this.setData(IMPlusUtilities.initializeData(address, fn));
   }

   @Override
   public final int getSyncFieldId(Object context) {
      return 5;
   }

   @Override
   public final Field getField(Object context) {
      return IMPlusUtilities.getField(this.getSyncFieldId(null), 0, this, this.getData(), context);
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      if (!(field instanceof EditField)) {
         return false;
      }

      EditField editField = (EditField)field;
      String address = editField.getText().trim();
      this.setData(IMPlusUtilities.initializeData(address, null));
      return address.length() > 0;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final void setFreeForm(boolean isFreeForm) {
      throw new IllegalStateException("Called setFreeForm on an InteractiveHHAddressModelImpl");
   }

   @Override
   public final boolean isFreeForm() {
      return false;
   }

   @Override
   public final int getOrder(Object context) {
      if (ContextObject.getFlag(context, 11)) {
         return ContextObject.getFlag(context, 0) ? 2550 : 3300;
      } else {
         return 0;
      }
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return IMPlusUtilities.getKeys(this.getData(), context, keyArray, index, keyRequested);
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return IMPlusUtilities.getKeys(this.getData(), context, keyArray, index, keyRequested);
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final String getAddress() {
      return IMPlusUtilities.getAddress(this.getData());
   }

   @Override
   public final void setFriendlyName(String fn) {
      this.setData(IMPlusUtilities.initializeData(this.getAddress(), fn));
   }

   @Override
   public final void setAddress(String address) {
      this.setData(IMPlusUtilities.initializeData(address, this.getFriendlyName()));
   }

   private final void setData(String data) {
      if (data != null && data.startsWith("IAP:")) {
         if (data.length() > 4) {
            data = data.substring(4);
         } else {
            data = null;
         }
      }

      this._dataEncoding = PersistentContent.encode(data, false, true);
   }

   public InteractiveHHAddressModelImpl(Object initialData) {
      String data = null;
      if (!(initialData instanceof String)) {
         if (initialData != null) {
            ContextObject contextObject = ContextObject.verifyNonNull(initialData);
            Object test = contextObject.get(254);
            if (test instanceof InteractiveHHAddressModel) {
               InteractiveHHAddressModelImpl model = (InteractiveHHAddressModelImpl)test;
               data = model.getData();
            } else {
               data = IMPlusUtilities.initializeData(contextObject);
            }
         }
      } else {
         data = IMPlusUtilities.initializeData((String)initialData);
      }

      this.setData(data);
   }

   @Override
   public final String toString() {
      return IMPlusUtilities.toString(this.getData());
   }

   private final String getData() {
      return PersistentContent.decodeString(this._dataEncoding);
   }

   @Override
   public final boolean equals(Object object) {
      return this == object;
   }
}
