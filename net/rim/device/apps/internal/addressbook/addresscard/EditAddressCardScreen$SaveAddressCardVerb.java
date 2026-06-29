package net.rim.device.apps.internal.addressbook.addresscard;

import java.util.Enumeration;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.apps.internal.profiles.Overrides;
import net.rim.device.internal.i18n.CommonResource;

final class EditAddressCardScreen$SaveAddressCardVerb extends Verb {
   private final EditAddressCardScreen this$0;

   public EditAddressCardScreen$SaveAddressCardVerb(EditAddressCardScreen _1) {
      super(610560, CommonResource.getBundle(), 18);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object context) {
      if (!this.this$0.proceedWithSave()) {
         UiApplication.getUiApplication().popScreen(this.this$0);
         return null;
      }

      if (EditAddressCardScreen.access$300(this.this$0)) {
         boolean validAddressCard = false;
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         Recognizer personNameModelRecognizer = (Recognizer)ar.waitFor(5149066071290992769L);
         Recognizer companyInfoModelRecognizer = (Recognizer)ar.waitFor(-2467076596918202204L);
         Field firstFieldFailure = null;
         Field firstRequiredFieldFailure = null;
         Field personNameModelField = null;
         Field companyInfoModelField = null;
         ContextObject emptyCheckContext = (ContextObject)(new Object(95, 11));
         Enumeration fieldEnum = this.this$0.getFieldsFromEdit();

         while (fieldEnum.hasMoreElements()) {
            Field field = (Field)fieldEnum.nextElement();
            boolean requiredField = false;
            RIMModel model = (RIMModel)field.getCookie();
            if (personNameModelRecognizer.recognize(model)) {
               personNameModelField = field;
               requiredField = true;
            } else if (companyInfoModelRecognizer.recognize(model)) {
               companyInfoModelField = field;
               requiredField = true;
            }

            FieldProvider fieldProvider = (FieldProvider)model;
            if (fieldProvider.validate(field, emptyCheckContext)) {
               if (requiredField) {
                  validAddressCard = true;
               }
            } else if (requiredField && firstRequiredFieldFailure == null) {
               firstRequiredFieldFailure = field;
            } else if (firstFieldFailure == null) {
               firstFieldFailure = field;
            }
         }

         if (validAddressCard && firstFieldFailure == null) {
            if (!AddressBookServices.getAddressBookOptions().getDuplicateNamesAllowed() && personNameModelField != null) {
               AddressCardModel duplicateCardModel = (AddressCardModel)FactoryUtil.createInstance(-3124646573404667739L, null);
               RIMModel model = (RIMModel)((Factory)personNameModelRecognizer).createInstance(null);
               if (((FieldProvider)model).grabDataFromField(personNameModelField, null)) {
                  duplicateCardModel.add(model);
               }

               if (companyInfoModelField != null) {
                  model = (RIMModel)((Factory)companyInfoModelRecognizer).createInstance(null);
                  if (((FieldProvider)model).grabDataFromField(companyInfoModelField, null)) {
                     duplicateCardModel.add(model);
                  }
               }

               duplicateCardModel = AddressCardModelFactory.compressCard(duplicateCardModel);
               if (this.this$0.isDuplicateName(duplicateCardModel)) {
                  personNameModelField.setFocus();
                  Status.show(AddressBookResources.getString(1100));
                  return null;
               }
            }

            try {
               AddressCardModel addressCard = (AddressCardModel)this.this$0.getModel();
               addressCard = AddressCardModelFactory.compressCard(addressCard);
               AddressCardUtilities.createGroup(addressCard);
               this.this$0._storeAction.invoke(addressCard);
               if (this.this$0._customTuneField != null) {
                  String tuneName = this.this$0._customTuneField.getSelectedTuneName();
                  Overrides.getInstance().setCustomTune(addressCard, tuneName);
               } else if (this.this$0._hasTune) {
                  Overrides.getInstance().deleteCustomTune(addressCard.getUID());
                  this.this$0._hasTune = false;
               }

               UiApplication.getUiApplication().popScreen(this.this$0);
               return null;
            } finally {
               throw new Object();
            }
         }

         if (firstRequiredFieldFailure != null) {
            firstRequiredFieldFailure.setFocus();
         } else if (firstFieldFailure != null) {
            firstFieldFailure.setFocus();
         }

         Status.show(AddressBookResources.getString(1101));
      }

      return null;
   }
}
