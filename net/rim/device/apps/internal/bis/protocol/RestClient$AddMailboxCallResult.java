package net.rim.device.apps.internal.bis.protocol;

import net.rim.device.apps.internal.bis.data.Mailbox;

public final class RestClient$AddMailboxCallResult extends RestClient$RESTCallResult {
   private Mailbox _mailbox;

   RestClient$AddMailboxCallResult(long restStatusCode, Mailbox mailbox) {
      super(restStatusCode);
      this._mailbox = mailbox;
   }

   public final Mailbox getMailbox() {
      return this._mailbox;
   }
}
