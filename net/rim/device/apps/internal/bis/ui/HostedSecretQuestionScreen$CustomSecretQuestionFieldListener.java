package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.apps.internal.bis.data.SecretQuestion;

final class HostedSecretQuestionScreen$CustomSecretQuestionFieldListener implements FieldChangeListener {
   private final HostedSecretQuestionScreen this$0;

   private HostedSecretQuestionScreen$CustomSecretQuestionFieldListener(HostedSecretQuestionScreen _1) {
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

   HostedSecretQuestionScreen$CustomSecretQuestionFieldListener(HostedSecretQuestionScreen x0, HostedSecretQuestionScreen$1 x1) {
      this(x0);
   }
}
