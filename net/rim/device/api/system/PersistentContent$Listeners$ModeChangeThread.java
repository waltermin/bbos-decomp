package net.rim.device.api.system;

final class PersistentContent$Listeners$ModeChangeThread extends Thread {
   private PersistentContentListener _listener;
   private int _modeGeneration;
   private Object _ticket;

   PersistentContent$Listeners$ModeChangeThread(PersistentContentListener listener, int modeGeneration, Object ticket) {
      this._listener = listener;
      this._modeGeneration = modeGeneration;
      this._ticket = ticket;
      if (this._ticket != null) {
         this._ticket.hashCode();
      }
   }

   @Override
   public final void run() {
      try {
         if (PersistentContent.getModeGeneration() == this._modeGeneration) {
            this._listener.persistentContentModeChanged(this._modeGeneration);
         }
      } catch (Throwable var2) {
      }

      this._ticket = null;
   }
}
