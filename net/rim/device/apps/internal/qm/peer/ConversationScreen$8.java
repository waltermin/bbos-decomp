package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.collection.Collection;

final class ConversationScreen$8 implements Runnable {
   private final Collection val$collection;
   private final Object val$oldElement;
   private final Object val$newElement;
   private final ConversationScreen this$0;

   ConversationScreen$8(ConversationScreen _1, Collection _2, Object _3, Object _4) {
      this.this$0 = _1;
      this.val$collection = _2;
      this.val$oldElement = _3;
      this.val$newElement = _4;
   }

   @Override
   public final void run() {
      ConversationScreen.access$500(this.this$0, this.val$collection, this.val$oldElement, this.val$newElement);
   }
}
