package net.rim.device.apps.internal.bis.commands;

import java.io.InputStream;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

final class EndUserAgreementCommand$TermsAndConditionsResponseHandler extends DefaultHandler {
   private SAXParser _parser;
   private String _retry;
   private String _termsAndConditions;
   private String _version;
   private boolean _foundRetryTag;
   private boolean _foundTermsAndConditionsTag;
   private boolean _foundVersionTag;
   private final EndUserAgreementCommand this$0;
   private static final String TAG_RETRY = "retry";
   private static final String TAG_TERMS_AND_CONDITIONS = "termsandconditions";
   private static final String TAG_VERSION = "tcversion";

   public EndUserAgreementCommand$TermsAndConditionsResponseHandler(EndUserAgreementCommand _1) {
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void parse(InputStream istream) {
      this._foundRetryTag = false;
      this._foundTermsAndConditionsTag = false;
      this._foundVersionTag = false;

      try {
         if (this._parser == null) {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            this._parser = parserFactory.newSAXParser();
            this._parser.setAllowUndefinedNamespaces(true);
         }

         this._parser.parse(istream, this);
      } catch (Throwable var4) {
         e.printStackTrace();
         return;
      }
   }

   public final String getRetry() {
      return this._retry;
   }

   public final boolean isRetry() {
      return this._foundRetryTag;
   }

   public final String getTermsAndConditions() {
      return this._termsAndConditions;
   }

   public final String getVersion() {
      return this._version;
   }

   @Override
   public final void characters(char[] ch, int start, int length) {
      String result = ch != null && ch.length > 0 ? new String(ch, start, length) : null;
      if (this._foundRetryTag) {
         this._retry = result;
      } else if (this._foundTermsAndConditionsTag) {
         this._termsAndConditions = result;
      } else {
         if (this._foundVersionTag) {
            this._version = result;
         }
      }
   }

   @Override
   public final void startElement(String uri, String localName, String qName, Attributes attributes) {
      if ("retry".equals(qName)) {
         this._foundRetryTag = true;
      } else if ("termsandconditions".equals(qName)) {
         this._foundTermsAndConditionsTag = true;
      } else {
         if ("tcversion".equals(qName)) {
            this._foundVersionTag = true;
         }
      }
   }

   @Override
   public final void endElement(String uri, String localName, String qName) {
      if ("termsandconditions".equals(qName)) {
         this._foundTermsAndConditionsTag = false;
      } else {
         if ("tcversion".equals(qName)) {
            this._foundVersionTag = false;
         }
      }
   }
}
