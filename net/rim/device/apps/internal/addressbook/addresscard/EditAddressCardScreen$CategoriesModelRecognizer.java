package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;

final class EditAddressCardScreen$CategoriesModelRecognizer implements Recognizer {
   private EditAddressCardScreen$CategoriesModelRecognizer() {
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof CategoriesModel;
   }

   EditAddressCardScreen$CategoriesModelRecognizer(EditAddressCardScreen$1 x0) {
      this();
   }
}
