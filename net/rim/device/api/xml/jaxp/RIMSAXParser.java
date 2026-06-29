package net.rim.device.api.xml.jaxp;

import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import net.rim.device.api.util.IntHashtable;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class RIMSAXParser extends SAXParser {
   private boolean _allowUndefinedNamespaces;
   private boolean _isNamespaceAware;
   private XMLReader _reader;
   private SAXParserImpl _saxParserImpl = new SAXParserImpl();
   static String CB_MIME_TYPE = "application/vnd.rim.wbxml-cb";
   static String WBXML_MIME_TYPE = "application/vnd.wap.wbxml, application/vnd.wap.wmlc";
   static String CB_CACHE_DIR = "file:///store/appdata/rim/.xmlcb/";
   static String TRUE = "true";
   static String FALSE = "false";

   private XMLParser newXMLParser() {
      XMLParser parser = null;
      parser = new XMLParser();
      parser.setAllowUndefinedNamespaces(this._allowUndefinedNamespaces);
      parser.setNamespaceAware(this._isNamespaceAware);
      this._reader = parser;
      return parser;
   }

   public XMLReader getXMLReader() {
      if (this._reader == null) {
         this.newXMLParser();
      }

      return this._reader;
   }

   public boolean getAllowUndefinedNamespaces() {
      return this._allowUndefinedNamespaces;
   }

   public void setAllowUndefinedNamespaces(boolean allowUndefinedNamespaces) {
      this._allowUndefinedNamespaces = allowUndefinedNamespaces;
      this._saxParserImpl.setAllowUndefinedNamespaces(allowUndefinedNamespaces);
   }

   @Override
   public boolean isNamespaceAware() {
      return this._isNamespaceAware;
   }

   public void setNamespaceAware(boolean isNamespaceAware) {
      this._isNamespaceAware = isNamespaceAware;
      this._saxParserImpl.setNamespaceAware(isNamespaceAware);
   }

   @Override
   public boolean isValidating() {
      return false;
   }

   @Override
   public void parse(InputStream is, DefaultHandler dh) {
      this._saxParserImpl.parse(is, dh);
   }

   @Override
   public void parse(InputSource is, DefaultHandler dh) {
      this._saxParserImpl.parse(is, dh);
   }

   public void parse(InputStream is, DefaultHandler dh, String[] tagTable, String[] attrStartTable, String[] attrTable) {
      this._saxParserImpl.parse(is, dh, tagTable, attrStartTable, attrTable);
   }

   public void parse(InputStream is, DefaultHandler dh, IntHashtable tagTables, IntHashtable attrStartTables, IntHashtable attrTables) {
      this._saxParserImpl.parse(is, dh, tagTables, attrStartTables, attrTables);
   }

   public void parse(String url, DefaultHandler dh, boolean autoCodeBookManagement) {
      this._saxParserImpl.parse(url, dh, autoCodeBookManagement);
   }
}
