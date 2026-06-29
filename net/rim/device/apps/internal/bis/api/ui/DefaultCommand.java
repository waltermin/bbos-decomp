package net.rim.device.apps.internal.bis.api.ui;

import java.util.Hashtable;

public final class DefaultCommand implements DomainCommand {
   public static final String DEFAULT_COMMAND_RESULT;
   private static DefaultCommand _instance = new DefaultCommand();
   private static DomainCommandResult _commandResult = new DomainCommandResult("success", null, null);

   private DefaultCommand() {
   }

   @Override
   public final DomainCommandResult run(Hashtable params) {
      return _commandResult;
   }
}
