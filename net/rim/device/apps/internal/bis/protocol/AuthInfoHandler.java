package net.rim.device.apps.internal.bis.protocol;

import java.io.InputStream;
import java.util.Hashtable;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import net.rim.device.apps.internal.bis.data.AuthInfo;
import net.rim.device.apps.internal.bis.utils.ArgUtils;
import net.rim.device.apps.internal.bis.utils.xml.XMLToObjectHandler;

public final class AuthInfoHandler extends XMLToObjectHandler implements BISServiceConstants {
   private SAXParser _parser;
   private static final String[] REQUIRED_TAGS = new String[]{"response"};

   public AuthInfoHandler() {
      super("simpleresponse", REQUIRED_TAGS, true);
   }

   public final AuthInfo loadFromXML(InputStream istream) {
      if (this._parser == null) {
         SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         this._parser = parserFactory.newSAXParser();
         this._parser.setAllowUndefinedNamespaces(true);
      }

      this._parser.parse(istream, this);
      return (AuthInfo)this.getResult();
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      AuthInfo authInfo = new AuthInfo();
      String username = ArgUtils.getStringValue(elementToValueMap, "response");
      if (username.indexOf(58) > -1) {
         username = username.substring(0, username.indexOf(58));
      }

      authInfo.setUsername(username);
      return authInfo;
   }
}
