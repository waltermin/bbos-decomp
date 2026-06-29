package net.rim.device.internal.system;

import net.rim.device.api.system.ApplicationDescriptor;

public final class RIMProcessLauncher$Data {
   private ApplicationDescriptor _descriptor;
   private int counter;
   private int _flags;
   private Runnable _runnable;
   private RIMProcessLauncher$ApplicationCallback _callback;

   public final void setDescriptor(ApplicationDescriptor applicationDescriptor) {
      this._descriptor = applicationDescriptor;
   }

   public final ApplicationDescriptor getDescriptor() {
      return this._descriptor;
   }

   public final int getFlags() {
      return this._flags;
   }

   public final void setFlags(int flags) {
      this._flags = flags;
   }

   public final Runnable getRunnable() {
      return this._runnable;
   }

   public final void setRunnable(Runnable runnable) {
      this._runnable = runnable;
   }

   public final void setCallBack(RIMProcessLauncher$ApplicationCallback callback) {
      this._callback = callback;
   }

   public final RIMProcessLauncher$ApplicationCallback getApplicationCallback() {
      return this._callback;
   }

   static final int access$008(RIMProcessLauncher$Data x0) {
      return x0.counter++;
   }
}
