package net.rim.device.apps.internal.memo;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;

final class MemoEditScreen$BodyModelRecognizer implements Recognizer {
   private MemoEditScreen$BodyModelRecognizer() {
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof BodyModel;
   }

   MemoEditScreen$BodyModelRecognizer(MemoEditScreen$1 x0) {
      this();
   }
}
