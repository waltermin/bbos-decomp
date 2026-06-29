package net.rim.device.apps.internal.bis.protocol;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.apps.internal.bis.utils.xml.XMLToObjectHandler;

final class MailboxesHandler extends XMLToObjectHandler implements BISServiceConstants {
   private static final String[] REQUIRED_ELEMENTS = new String[]{"emailAccount"};

   public MailboxesHandler() {
      super("emailAccounts", REQUIRED_ELEMENTS, true);
      this.setElementHandler("emailAccount", new MailboxHandler());
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      Object emailAccount = elementToValueMap.get("emailAccount");
      if (emailAccount != null) {
         if (!(emailAccount instanceof Object)) {
            Vector emailAccountsVector = (Vector)(new Object());
            emailAccountsVector.addElement(emailAccount);
            return emailAccountsVector;
         } else {
            return emailAccount;
         }
      } else {
         return null;
      }
   }
}
