package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;

final class AddHostedAccountCall implements XMLCall, BISServiceConstants {
   private String _email;
   private String _password;
   private String _secretQuestion;
   private Integer _secretQuestionId;
   private String _secretAnswer;

   AddHostedAccountCall(String email, String password, String secretQuestion, Integer secretQuestionId, String secretAnswer) {
      this._email = email;
      this._password = password;
      this._secretQuestion = secretQuestion;
      this._secretQuestionId = secretQuestionId;
      this._secretAnswer = secretAnswer;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.startElement(ostream, "emailAccount");
      XMLUtils.writeSimpleElement(ostream, "hosted", "true");
      XMLUtils.writeSimpleElement(ostream, "email", this._email);
      if (this._password != null) {
         XMLUtils.writeSimpleElement(ostream, "password", this._password);
      }

      if (this._secretQuestion != null) {
         XMLUtils.writeSimpleElement(ostream, "secretQuestion", this._secretQuestion);
      }

      if (this._secretQuestionId != null) {
         XMLUtils.writeSimpleElement(ostream, "secretQuestionId", this._secretQuestionId.toString());
      }

      if (this._secretAnswer != null) {
         XMLUtils.writeSimpleElement(ostream, "secretQuestionAnswer", this._secretAnswer);
      }

      XMLUtils.endElement(ostream, "emailAccount");
   }
}
