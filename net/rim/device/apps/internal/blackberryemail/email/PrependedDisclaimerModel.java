package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;

final class PrependedDisclaimerModel implements PersistableRIMModel, FieldProvider, RemoveWhenSendingModel {
   private boolean _prependDisclaimer = true;

   public final boolean prependDisclaimer() {
      return this._prependDisclaimer;
   }

   public static final Recognizer getRecognizer() {
      return new PrependedDisclaimerModel$PrependedDisclaimerModelRecognizer(null);
   }

   @Override
   public final Field getField(Object context) {
      return new PrependedDisclaimerModelField(this);
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      if (field instanceof PrependedDisclaimerModelField) {
         this._prependDisclaimer = ((PrependedDisclaimerModelField)field).prependDisclaimer();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final int getOrder(Object context) {
      return 5300;
   }

   @Override
   public final boolean removeBeforeSending() {
      return false;
   }

   @Override
   public final boolean removeAfterSending() {
      return true;
   }
}
