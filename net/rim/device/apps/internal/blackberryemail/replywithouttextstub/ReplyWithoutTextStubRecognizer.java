package net.rim.device.apps.internal.blackberryemail.replywithouttextstub;

import net.rim.device.apps.api.framework.model.Recognizer;

public final class ReplyWithoutTextStubRecognizer implements Recognizer {
   private static ReplyWithoutTextStubRecognizer _singletonInstance;

   ReplyWithoutTextStubRecognizer() {
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof ReplyWithoutTextStubModel;
   }

   public static final synchronized ReplyWithoutTextStubRecognizer getInstance() {
      if (_singletonInstance == null) {
         _singletonInstance = new ReplyWithoutTextStubRecognizer();
      }

      return _singletonInstance;
   }
}
