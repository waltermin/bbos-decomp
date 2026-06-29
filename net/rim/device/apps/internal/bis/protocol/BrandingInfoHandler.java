package net.rim.device.apps.internal.bis.protocol;

import java.io.InputStream;
import java.util.Hashtable;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import net.rim.device.apps.internal.bis.data.BrandingInfo;
import net.rim.device.apps.internal.bis.utils.ArgUtils;
import net.rim.device.apps.internal.bis.utils.xml.XMLToObjectHandler;

public final class BrandingInfoHandler extends XMLToObjectHandler implements BISServiceConstants {
   private SAXParser _parser;
   private static final String[] REQUIRED_TAGS = new String[]{
      "hostedMailDomain", "mboxProviderName", "settings", "languages", "defaultLanguage", "endUserAgreement", "help"
   };

   public BrandingInfoHandler() {
      super("brand", REQUIRED_TAGS, true);
   }

   public final BrandingInfo loadFromXML(InputStream istream) {
      if (this._parser == null) {
         SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         this._parser = parserFactory.newSAXParser();
         this._parser.setAllowUndefinedNamespaces(true);
      }

      this._parser.parse(istream, this);
      return (BrandingInfo)this.getResult();
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      BrandingInfo brandingInfo = new BrandingInfo();
      brandingInfo.setAvailableLanguages(ArgUtils.getStringValue(elementToValueMap, "languages"));
      brandingInfo.setDefaultLanguage(ArgUtils.getStringValue(elementToValueMap, "defaultLanguage"));
      brandingInfo.setHostedMailDomain(ArgUtils.getStringValue(elementToValueMap, "hostedMailDomain"));
      String selfCreateEnabled = ArgUtils.getStringValue(elementToValueMap, "selfCreationEnabled");
      if ("true".equalsIgnoreCase(selfCreateEnabled)) {
         brandingInfo.setSelfCreateEnabled(true);
      } else {
         brandingInfo.setSelfCreateEnabled(false);
      }

      brandingInfo.setSettings(Integer.parseInt((String)elementToValueMap.get("settings")));
      brandingInfo.setEndUserAgreementURL(ArgUtils.getStringValue(elementToValueMap, "endUserAgreement"));
      brandingInfo.setHelpRootURL(ArgUtils.getStringValue(elementToValueMap, "help"));
      String numAttempts = ArgUtils.getStringValue(elementToValueMap, "numValidationAttempts");
      if (numAttempts != null) {
         brandingInfo.setNumValidationAttempts(Integer.parseInt(numAttempts));
         return brandingInfo;
      } else {
         brandingInfo.setNumValidationAttempts(3);
         return brandingInfo;
      }
   }
}
