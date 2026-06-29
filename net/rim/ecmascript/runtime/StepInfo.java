package net.rim.ecmascript.runtime;

class StepInfo extends LineAndOffsets {
   int numCalls;
   int stepMode;
   boolean allExceptions;

   void reset() {
      this.numCalls = 0;
      this.stepMode = 0;
   }

   StepInfo() {
      this.resetLineInfo();
      this.reset();
   }

   void newContext() {
      this.resetLineInfo();
   }

   void enterCall() {
      this.numCalls++;
      this.resetLineInfo();
   }

   void exitCall() {
      this.numCalls--;
      this.resetLineInfo();
   }
}
