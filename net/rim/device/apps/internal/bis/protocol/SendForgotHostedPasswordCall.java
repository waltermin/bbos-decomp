package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;

final class SendForgotHostedPasswordCall implements XMLCall {
   private String _secretAnswer;

   SendForgotHostedPasswordCall(String secretAnswer) {
      this._secretAnswer = secretAnswer;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.startElement(ostream, "sfhp");
      XMLUtils.writeSimpleElement(ostream, "secretQuestionAnswer", this._secretAnswer);
      XMLUtils.endElement(ostream, "sfhp");
   }
}
