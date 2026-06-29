package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.protocol.HttpCommException;
import net.rim.device.apps.internal.bis.protocol.RESTException;

public class NetCommand implements DomainCommand {
   DomainCommandResult runInternal(Hashtable _1) {
      throw null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public DomainCommandResult run(Hashtable params) {
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         return this.runInternal(params);
      } catch (RESTException var7) {
         var6 = false;
         return new DomainCommandResult("error", null, null);
      } catch (HttpCommException var8) {
         var6 = false;
      } finally {
         if (var6) {
            return new DomainCommandResult(null, this.getIOErrorMessage(), null);
         }
      }

      return new DomainCommandResult(null, this.getHttpErrorMessage(), null);
   }

   String getIOErrorMessage() {
      return ApplicationResources.getString(122);
   }

   String getHttpErrorMessage() {
      return this.getIOErrorMessage();
   }
}
