package net.rim.device.apps.internal.blackberryemail.replywithouttextstub;

import net.rim.device.apps.api.framework.registration.RIMModelFactory;

final class ReplyWithoutTextStubModelFactory extends RIMModelFactory {
   @Override
   public final Object createInstance(Object context) {
      return new ReplyWithoutTextStubModel(context);
   }

   @Override
   public final int getMinimumCount(Object context) {
      return 0;
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof ReplyWithoutTextStubModel;
   }
}
