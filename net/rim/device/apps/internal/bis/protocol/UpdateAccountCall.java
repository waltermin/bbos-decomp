package net.rim.device.apps.internal.bis.protocol;

import java.io.OutputStream;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.utils.xml.XMLUtils;

final class UpdateAccountCall implements XMLCall, BISServiceConstants {
   private Mailbox _mailbox;
   private String _oldPassword;

   UpdateAccountCall(Mailbox mailbox, String oldPassword) {
      this._mailbox = mailbox;
      this._oldPassword = oldPassword;
   }

   @Override
   public final void serialize(OutputStream ostream) {
      XMLUtils.startElement(ostream, "emailAccount");
      if (this._mailbox.getHosted() != null) {
         XMLUtils.writeSimpleElement(ostream, "hosted", this._mailbox.getHosted());
      }

      if (this._mailbox.getEmail() != null) {
         XMLUtils.writeSimpleElement(ostream, "email", this._mailbox.getEmail());
      }

      if (this._mailbox.getProtocol() != null) {
         XMLUtils.writeSimpleElement(ostream, "protocol", this._mailbox.getProtocol());
      }

      if (this._mailbox.getServer() != null) {
         XMLUtils.writeSimpleElement(ostream, "server", this._mailbox.getServer());
      }

      if (this._mailbox.getDescription() != null) {
         XMLUtils.writeSimpleElement(ostream, "mailbox", this._mailbox.getDescription());
      }

      if (this._mailbox.getUserName() != null) {
         XMLUtils.writeSimpleElement(ostream, "userName", this._mailbox.getUserName());
      }

      if (this._mailbox.getPassword() != null) {
         XMLUtils.writeSimpleElement(ostream, "password", this._mailbox.getPassword());
      }

      if (this._mailbox.getUseSSL() != null) {
         XMLUtils.writeSimpleElement(ostream, "ssl", this._mailbox.getUseSSL());
      }

      if (this._mailbox.getFriendlyName() != null) {
         XMLUtils.writeSimpleElement(ostream, "friendlyName", this._mailbox.getFriendlyName());
      }

      if (this._mailbox.getReplyTo() != null) {
         XMLUtils.writeSimpleElement(ostream, "replyTo", this._mailbox.getReplyTo());
      }

      if (this._mailbox.getAutoBCC() != null) {
         XMLUtils.writeSimpleElement(ostream, "autoBCC", this._mailbox.getAutoBCC());
      }

      if (this._mailbox.getAutoForward() != null) {
         XMLUtils.writeSimpleElement(ostream, "autoForward", this._mailbox.getAutoForward());
      }

      if (this._mailbox.getAutoForwardAll() != null) {
         XMLUtils.writeSimpleElement(ostream, "autoForwardAll", this._mailbox.getAutoForwardAll());
      }

      if (this._mailbox.getDeleteSync() != null) {
         XMLUtils.writeSimpleElement(ostream, "deleteSync", this._mailbox.getDeleteSync());
      }

      if (this._mailbox.getSignature() != null) {
         XMLUtils.writeSimpleElement(ostream, "signature", this._mailbox.getSignature());
      }

      if (this._mailbox.getForwardMessages() != null) {
         XMLUtils.writeSimpleElement(ostream, "defaultRuleSend", this._mailbox.getForwardMessages());
      }

      if (this._mailbox.getSrcMboxID() != null) {
         XMLUtils.writeSimpleElement(ostream, "id", this._mailbox.getSrcMboxID());
      }

      if (this._mailbox.getSecretQuestion() != null) {
         XMLUtils.writeSimpleElement(ostream, "secretQuestion", this._mailbox.getSecretQuestion());
      }

      if (this._mailbox.getSecretQuestionId() != null) {
         XMLUtils.writeSimpleElement(ostream, "secretQuestionId", this._mailbox.getSecretQuestionId());
      }

      if (this._mailbox.getSecretAnswer() != null) {
         XMLUtils.writeSimpleElement(ostream, "secretQuestionAnswer", this._mailbox.getSecretAnswer());
      }

      if (!this._mailbox.isValid()) {
         XMLUtils.writeSimpleElement(ostream, "valid", new Object(false));
      }

      if (this._oldPassword != null) {
         XMLUtils.writeSimpleElement(ostream, "oldPassword", this._oldPassword);
      }

      XMLUtils.endElement(ostream, "emailAccount");
   }
}
