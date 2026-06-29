package net.rim.wica.runtime.lifecycle.internal;

class LifecycleManager$1 implements LifecycleManager$ApplicationBooleanExpression {
   private final String val$uri;
   private final LifecycleManager this$0;

   LifecycleManager$1(LifecycleManager this$0, String val$uri) {
      this.this$0 = this$0;
      this.val$uri = val$uri;
   }

   @Override
   public boolean evaluate(WicletImpl application) {
      return application.getUri().equalsIgnoreCase(this.val$uri);
   }
}
