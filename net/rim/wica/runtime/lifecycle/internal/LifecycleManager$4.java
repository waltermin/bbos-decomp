package net.rim.wica.runtime.lifecycle.internal;

class LifecycleManager$4 implements LifecycleManager$ApplicationBooleanExpression {
   private final LifecycleManager this$0;

   LifecycleManager$4(LifecycleManager this$0) {
      this.this$0 = this$0;
   }

   @Override
   public boolean evaluate(WicletImpl application) {
      return application.getState() == 4;
   }
}
