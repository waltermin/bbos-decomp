package net.rim.device.apps.internal.bis.api.ui;

import java.util.Hashtable;

public interface DomainCommand {
   DomainCommandResult SESSION_TIMEOUT_RESULT;

   DomainCommandResult run(Hashtable var1);
}
