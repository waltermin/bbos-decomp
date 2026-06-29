package net.rim.device.internal.EScreens;

import net.rim.device.api.system.ApplicationProcess;
import net.rim.vm.Process;

class EScreenVMProcesses$EScreenProcess {
   private ApplicationProcess _process;
   private long _time;
   private final EScreenVMProcesses this$0;

   public EScreenVMProcesses$EScreenProcess(EScreenVMProcesses _1, ApplicationProcess process) {
      this.this$0 = _1;
      this._process = process;
      switch (EScreenVMProcesses._displayType) {
         case 4:
         default:
            this._time = Process.getRecentCPUTime(process);
            return;
         case 5:
            this._time = Process.getTotalCPUTime(process);
         case 3:
      }
   }

   public long getTime() {
      return this._time;
   }

   public void setTime(long time) {
      this._time = time;
   }

   public int getProcessId() {
      return this._process == null ? -1 : this._process.getProcessId();
   }

   public ApplicationProcess getApplicationProcess() {
      return this._process;
   }

   public int percent(long a, long b) {
      return (int)(a * 100 / b);
   }

   @Override
   public String toString() {
      String name;
      if (this._process == null) {
         switch (EScreenVMProcesses._displayType) {
            case 3:
               name = "Processes";
               break;
            case 4:
            default:
               name = "Utilization";
               break;
            case 5:
               name = "Total (Non-idle " + (int)(this.this$0._totalUtilizedTime / 1000) + "s)";
         }
      } else {
         name = this._process.toString();
      }

      switch (EScreenVMProcesses._displayType) {
         case 3:
            return name;
         case 4:
         default:
            return Integer.toString(this.percent(this._time, this.this$0._recentTimeWindow)) + "% " + name;
         case 5:
            String s = Integer.toString((int)(this._time / 1000)) + "s ";
            if (this._process != null) {
               s = s + "(" + this.percent(this._time, this.this$0._totalUtilizedTime) + "%) ";
            }

            return s + name;
      }
   }

   public int compareTo(EScreenVMProcesses$EScreenProcess other) {
      if (other._process == null) {
         return 1;
      } else if (this._process == null) {
         return -1;
      } else {
         long diff = other._time - this._time;
         if (diff < 0) {
            return -1;
         } else {
            return diff > 0 ? 1 : 0;
         }
      }
   }
}
