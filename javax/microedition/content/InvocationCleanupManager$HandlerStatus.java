package javax.microedition.content;

final class InvocationCleanupManager$HandlerStatus {
   public boolean canExit;
   public boolean invocationsAdded;
   public ContentHandlerServerImpl handler;
   public RegistryImpl registry;

   private InvocationCleanupManager$HandlerStatus() {
      this.canExit = false;
      this.invocationsAdded = false;
   }

   public InvocationCleanupManager$HandlerStatus(ContentHandlerServerImpl handler) {
      this.handler = handler;
   }

   public InvocationCleanupManager$HandlerStatus(RegistryImpl registry) {
      this.registry = registry;
   }
}
