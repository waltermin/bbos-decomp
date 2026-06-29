package net.rim.device.apps.internal.bis.protocol;

import java.io.InputStream;
import java.util.Hashtable;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.utils.ArgUtils;
import net.rim.device.apps.internal.bis.utils.xml.XMLToObjectHandler;

public final class MailboxHandler extends XMLToObjectHandler implements BISServiceConstants {
   private SAXParser _parser;
   private static final String[] REQUIRED_ELEMENTS = new String[]{"hosted", "id", "email", "settings", "fieldPermissions"};

   public MailboxHandler() {
      super("emailAccount", REQUIRED_ELEMENTS, true);
   }

   public final Mailbox loadFromXML(InputStream istream) {
      if (this._parser == null) {
         SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         this._parser = parserFactory.newSAXParser();
         this._parser.setAllowUndefinedNamespaces(true);
      }

      this._parser.parse(istream, this);
      return (Mailbox)this.getResult();
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      Mailbox mailbox = new Mailbox();
      boolean hosted = "true".equalsIgnoreCase(ArgUtils.getStringValue(elementToValueMap, "hosted"));
      mailbox.setHosted(hosted);
      mailbox.setEmail(ArgUtils.getStringValue(elementToValueMap, "email"));
      mailbox.setSrcMboxID(ArgUtils.getStringValue(elementToValueMap, "id"));
      mailbox.setSettings(Integer.parseInt(ArgUtils.getStringValue(elementToValueMap, "settings")));
      mailbox.setProtocol(ArgUtils.getStringValue(elementToValueMap, "protocol"));
      mailbox.setServer(ArgUtils.getStringValue(elementToValueMap, "server"));
      mailbox.setDescription(ArgUtils.getStringValue(elementToValueMap, "description"));
      mailbox.setUserName(ArgUtils.getStringValue(elementToValueMap, "userName"));
      mailbox.setPassword(ArgUtils.getStringValue(elementToValueMap, "password"));
      if (ArgUtils.getStringValue(elementToValueMap, "ssl") != null) {
         boolean useSSL = "true".equalsIgnoreCase(ArgUtils.getStringValue(elementToValueMap, "ssl"));
         mailbox.setUseSSL(useSSL);
      }

      mailbox.setFriendlyName(ArgUtils.getStringValue(elementToValueMap, "friendlyName"));
      mailbox.setAutoBCC(ArgUtils.getStringValue(elementToValueMap, "autoBCC"));
      mailbox.setAutoForward(ArgUtils.getStringValue(elementToValueMap, "autoForward"));
      boolean autoForwardAll = "true".equalsIgnoreCase(ArgUtils.getStringValue(elementToValueMap, "autoForwardAll"));
      mailbox.setAutoForwardAll(autoForwardAll);
      String portString = ArgUtils.getStringValue(elementToValueMap, "port");
      if (portString != null) {
         mailbox.setPort(Integer.parseInt(portString));
      }

      String timeoutString = ArgUtils.getStringValue(elementToValueMap, "timeout");
      if (timeoutString != null) {
         mailbox.setTimeout(Integer.parseInt(timeoutString));
      }

      boolean deleteSync = "true".equalsIgnoreCase(ArgUtils.getStringValue(elementToValueMap, "deleteSync"));
      mailbox.setDeleteSync(deleteSync);
      mailbox.setReplyTo(ArgUtils.getStringValue(elementToValueMap, "replyTo"));
      mailbox.setSignature(ArgUtils.getStringValue(elementToValueMap, "signature"));
      mailbox.setFieldPermissions(ArgUtils.getStringValue(elementToValueMap, "fieldPermissions"));
      mailbox.setSecretQuestion(ArgUtils.getStringValue(elementToValueMap, "secretQuestion"));
      String secretQuestionIdString = ArgUtils.getStringValue(elementToValueMap, "secretQuestionId");
      if (secretQuestionIdString != null) {
         mailbox.setSecretQuestionId((Integer)(new Object(Integer.parseInt(secretQuestionIdString))));
      }

      mailbox.setSecretAnswer(ArgUtils.getStringValue(elementToValueMap, "secretQuestionAnswer"));
      return mailbox;
   }
}
