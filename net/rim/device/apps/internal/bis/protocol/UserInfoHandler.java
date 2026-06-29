package net.rim.device.apps.internal.bis.protocol;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.Common;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.data.UserInfo;
import net.rim.device.apps.internal.bis.utils.ArgUtils;
import net.rim.device.apps.internal.bis.utils.xml.XMLToObjectHandler;

public final class UserInfoHandler extends XMLToObjectHandler implements BISServiceConstants {
   private SAXParser _parser;
   private static final String[] REQUIRED_ELEMENTS = new String[]{"pin", "site", "username", "emailAccounts", "features", "features2", "maxintegrations"};

   public UserInfoHandler() {
      super("user", REQUIRED_ELEMENTS, true);
      this.setElementHandler("emailAccounts", new MailboxesHandler());
   }

   public final UserInfo loadFromXML(InputStream istream) {
      if (this._parser == null) {
         SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         this._parser = parserFactory.newSAXParser();
         this._parser.setAllowUndefinedNamespaces(true);
      }

      this._parser.parse(istream, this);
      return (UserInfo)this.getResult();
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      UserInfo userInfo = new UserInfo();
      userInfo.setPIN(ArgUtils.getStringValue(elementToValueMap, "pin"));
      userInfo.setUsername(ArgUtils.getStringValue(elementToValueMap, "username"));
      userInfo.setFeatures(Integer.parseInt((String)elementToValueMap.get("features")));
      String features2 = (String)elementToValueMap.get("features2");
      if (features2 != null) {
         userInfo.setFeatures2(Integer.parseInt(features2));
      }

      userInfo.setNumSupportedMailboxes(Integer.parseInt((String)elementToValueMap.get("maxintegrations")));
      String tctimestamp = (String)elementToValueMap.get("tctimestamp");
      if (tctimestamp != null) {
         userInfo.setTimeStamp(Long.parseLong(tctimestamp));
      }

      String language = (String)elementToValueMap.get("language");
      if (language != null) {
         if (language.equals("iw")) {
            language = "he";
         }

         Locale locale = Common.getLocale(language);
         if (locale != null) {
            userInfo.setLocale(locale);
         } else {
            BISEventLogger.logEvent("Unable to find a valid Locale from code: " + language, 0);
         }
      }

      Vector emailAccounts = (Vector)elementToValueMap.get("emailAccounts");
      if (emailAccounts != null) {
         int numAccounts = emailAccounts.size();

         for (int i = 0; i < numAccounts; i++) {
            userInfo.addMailbox((Mailbox)emailAccounts.elementAt(i));
         }
      }

      String strAutoAuth = (String)elementToValueMap.get("autoAuth");
      if (strAutoAuth != null) {
         userInfo.setAutoAuth("true".equals(strAutoAuth.toLowerCase()));
      }

      return userInfo;
   }
}
