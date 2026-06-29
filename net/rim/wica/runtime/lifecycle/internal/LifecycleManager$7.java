package net.rim.wica.runtime.lifecycle.internal;

class LifecycleManager$7 implements LifecycleManager$ApplicationBooleanExpression {
   private final LifecycleManager this$0;

   LifecycleManager$7(LifecycleManager this$0) {
      this.this$0 = this$0;
   }

   @Override
   public boolean evaluate(WicletImpl application) {
      return !application.isSystemApplication();
   }
}
