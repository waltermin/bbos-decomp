package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.apps.internal.bis.data.SecretQuestion;

final class NewBBEmailAddressScreen$CustomSecretQuestionFieldListener implements FieldChangeListener {
   private final NewBBEmailAddressScreen this$0;

   private NewBBEmailAddressScreen$CustomSecretQuestionFieldListener(NewBBEmailAddressScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this.this$0._secretQuestions) {
         SecretQuestion question = (SecretQuestion)this.this$0._secretQuestions.getChoice(this.this$0._secretQuestions.getSelectedIndex());
         if (question.isCustom()) {
            this.this$0.showCustomQuestion();
            return;
         }

         this.this$0.hideCustomQuestion();
      }
   }

   NewBBEmailAddressScreen$CustomSecretQuestionFieldListener(NewBBEmailAddressScreen x0, NewBBEmailAddressScreen$1 x1) {
      this(x0);
   }
}
