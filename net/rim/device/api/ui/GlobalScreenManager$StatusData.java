package net.rim.device.api.ui;

import net.rim.vm.Process;

class GlobalScreenManager$StatusData {
   Screen screen;
   Process process;
   UiEngineImpl engine;
   int priority;
   boolean inputRequired;
   boolean suppress;
   boolean redisplay;

   GlobalScreenManager$StatusData(Screen screen, int priority, boolean inputRequired, boolean suppress, Process process, UiEngineImpl engine) {
      this.screen = screen;
      this.priority = priority;
      this.inputRequired = inputRequired;
      this.suppress = suppress;
      this.process = process;
      this.engine = engine;
      this.redisplay = false;
   }
}
