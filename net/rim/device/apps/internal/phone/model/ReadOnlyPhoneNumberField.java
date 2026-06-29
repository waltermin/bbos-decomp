package net.rim.device.apps.internal.phone.model;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.addressbook.AddressModelField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.vm.WeakReference;

class ReadOnlyPhoneNumberField extends AddressModelField {
   private static WeakReference _strBufferWR = (WeakReference)(new Object(null));

   public ReadOnlyPhoneNumberField(RIMModel phoneNumberModel, Object context) {
   }

   @Override
   protected Field getFriendlyField(RIMModel model, Object addressCardModel, Object context) {
      Field baseFriendlyField = super.getFriendlyField(model, addressCardModel, context);
      PhoneNumberModel numberModel = (PhoneNumberModel)model;
      if (baseFriendlyField == null) {
         return null;
      }

      baseFriendlyField.setCookie(model);
      String typeString = null;
      FlowFieldManager ffm = (FlowFieldManager)(new Object());
      ffm.setCookie(model);
      ffm.add(baseFriendlyField);
      if (addressCardModel != null) {
         if (numberModel._type == 0) {
            PhoneNumberModel matchedNumber = (PhoneNumberModel)numberModel.matchPhoneNumber(addressCardModel, null);
            if (matchedNumber != null) {
               typeString = AbstractPhoneNumberModel.getTypeString(matchedNumber._type);
            }
         } else {
            typeString = AbstractPhoneNumberModel.getTypeString(numberModel._type);
         }
      }

      StringBuffer _strBuffer = WeakReferenceUtilities.getStringBuffer(_strBufferWR);
      synchronized (_strBuffer) {
         _strBuffer.setLength(0);
         _strBuffer.append(' ');
         AbstractPhoneNumberModel.formatTypeString(_strBuffer, typeString, true, false);
         ffm.add((Field)(new Object(_strBuffer.toString())));
      }

      boolean includeLabel = !ContextObject.getFlag(context, 1);
      if (includeLabel) {
         String label = getLabel(model, context);
         FlowFieldManager containerField = (FlowFieldManager)(new Object());
         containerField.add((Field)(new Object(label)));
         containerField.add(ffm);
         containerField.setCookie(model);
         ffm = containerField;
      }

      return ffm;
   }

   @Override
   protected Field getQualifiedField(RIMModel model, Object context) {
      ContextObject contextObject = ContextObject.castOrCreate(context);
      contextObject.setFlag(84);
      Field qualifiedField = null;
      if (model instanceof Object) {
         FieldProvider fieldProvider = (FieldProvider)model;
         qualifiedField = fieldProvider.getField(contextObject);
      }

      if (qualifiedField == null) {
         return null;
      }

      qualifiedField.setCookie(model);
      boolean includeLabel = !ContextObject.getFlag(context, 1);
      if (includeLabel) {
         String label = getLabel(model, context);
         HorizontalFieldManager containerField = (HorizontalFieldManager)(new Object());
         containerField.add((Field)(new Object(label)));
         containerField.add(qualifiedField);
         containerField.setCookie(model);
         qualifiedField = containerField;
      }

      return qualifiedField;
   }

   private static String getLabel(RIMModel model, Object context) {
      Object contextFieldLabel = ContextObject.get(context, 3986845832244503196L);
      if (!(contextFieldLabel instanceof Object)) {
         PhoneNumberModel numberModel = (PhoneNumberModel)model;
         return ((StringBuffer)(new Object())).append(AbstractPhoneNumberModel.getTypeString(numberModel._type)).append(": ").toString();
      } else {
         return (String)contextFieldLabel;
      }
   }
}
