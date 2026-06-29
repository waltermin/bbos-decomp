package net.rim.wica.runtime.lifecycle.internal;

class LifecycleManager$3 implements LifecycleManager$ApplicationBooleanExpression {
   private final long val$serverId;
   private final LifecycleManager this$0;

   LifecycleManager$3(LifecycleManager this$0, long val$serverId) {
      this.this$0 = this$0;
      this.val$serverId = val$serverId;
   }

   @Override
   public boolean evaluate(WicletImpl application) {
      return application.getAgId() == this.val$serverId;
   }
}
