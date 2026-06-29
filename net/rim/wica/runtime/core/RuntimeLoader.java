package net.rim.wica.runtime.core;

final class RuntimeLoader {
   public static final void main(String[] args) {
      if (args != null && args.length > 0) {
         RuntimeFramework framework = RuntimeFramework.getFramework();
         if (args[0].equals("activation")) {
            framework.displayActivationScreen();
         } else {
            framework.loadApplication(args[0]);
         }
      } else {
         new RuntimeFramework().dispatchEvents();
      }
   }
}
