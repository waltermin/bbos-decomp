package net.rim.device.apps.internal.bis.api.ui;

public final class CommandDescriptor {
   private int _commandID;
   private String[] _paramNames;

   public CommandDescriptor(int commandID, String[] paramNames) {
      this._commandID = commandID;
      this._paramNames = paramNames;
   }

   public final int getCommandID() {
      return this._commandID;
   }

   public final String[] getParamNames() {
      return this._paramNames;
   }
}
