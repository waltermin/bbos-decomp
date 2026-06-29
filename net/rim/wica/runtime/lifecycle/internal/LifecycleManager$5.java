package net.rim.wica.runtime.lifecycle.internal;

class LifecycleManager$5 implements LifecycleManager$ApplicationBooleanExpression {
   private final LifecycleManager this$0;

   LifecycleManager$5(LifecycleManager this$0) {
      this.this$0 = this$0;
   }

   @Override
   public boolean evaluate(WicletImpl application) {
      return application.getExecutionState() == 2;
   }
}
