package net.rim.device.api.system;

import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

class ApplicationManagerImpl$ApplicationProcessContainer {
   private ApplicationProcess[] _processes = new ApplicationProcess[0];

   int numberOfProcesses() {
      return this._processes.length;
   }

   ApplicationProcess getProcessAtIndex(int index) {
      return this._processes[index];
   }

   int getProcessIndex(ApplicationProcess process) {
      for (int i = this._processes.length - 1; i >= 0; i--) {
         if (this._processes[i] == process) {
            return i;
         }
      }

      return -1;
   }

   void addProcess(ApplicationProcess process) {
      Arrays.add(this._processes, process);
   }

   void removeProcess(int index) {
      this.moveProcessToRear(index);
      Array.resize(this._processes, this._processes.length - 1);
   }

   void moveProcess(ApplicationProcess process, int destIndex) {
      int srcIndex = this.getProcessIndex(process);
      if (srcIndex == -1) {
         throw new IllegalArgumentException();
      }

      this.moveProcess(srcIndex, destIndex);
   }

   void moveProcessToRear(ApplicationProcess process) {
      int srcIndex = this.getProcessIndex(process);
      if (srcIndex == -1) {
         throw new IllegalArgumentException();
      }

      this.moveProcessToRear(srcIndex);
   }

   void moveProcessToRear(int srcIndex) {
      this.moveProcess(srcIndex, this._processes.length - 1);
   }

   void moveProcess(int srcIndex, int destIndex) {
      ApplicationProcess targetProcess = this._processes[srcIndex];
      if (destIndex < srcIndex) {
         System.arraycopy(this._processes, destIndex, this._processes, destIndex + 1, srcIndex - destIndex);
      } else if (destIndex > srcIndex) {
         System.arraycopy(this._processes, srcIndex + 1, this._processes, srcIndex, destIndex - srcIndex);
      }

      this._processes[destIndex] = targetProcess;
   }

   ApplicationProcess[] getCopyOfProcesses() {
      int numProcesses = this._processes.length;
      ApplicationProcess[] copy = new ApplicationProcess[numProcesses];
      System.arraycopy(this._processes, 0, copy, 0, numProcesses);
      return copy;
   }
}
