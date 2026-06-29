package net.rim.device.apps.internal.bis.protocol;

import java.io.InputStream;
import java.util.Hashtable;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import net.rim.device.apps.internal.bis.data.Filter;
import net.rim.device.apps.internal.bis.utils.ArgUtils;
import net.rim.device.apps.internal.bis.utils.xml.XMLToObjectHandler;

final class FilterHandler extends XMLToObjectHandler implements BISServiceConstants {
   private SAXParser _parser;
   private static final String[] REQUIRED_ELEMENTS = new String[]{
      "filterId", "name", "sendAlert", "sequence", "levelOne", "headersOnly", "filterOperator", "filterValue"
   };

   public FilterHandler() {
      super("filter", REQUIRED_ELEMENTS, true);
   }

   public final Filter loadFromXML(InputStream istream) {
      if (this._parser == null) {
         SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         this._parser = parserFactory.newSAXParser();
         this._parser.setAllowUndefinedNamespaces(true);
      }

      this._parser.parse(istream, this);
      return (Filter)this.getResult();
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      Filter filter = new Filter();
      filter.setId(ArgUtils.getStringValue(elementToValueMap, "filterId"));
      filter.setName(ArgUtils.getStringValue(elementToValueMap, "name"));
      boolean sendAlert = "true".equalsIgnoreCase(ArgUtils.getStringValue(elementToValueMap, "sendAlert"));
      filter.setSendAlert(sendAlert);
      filter.setSequenceId(Integer.parseInt(ArgUtils.getStringValue(elementToValueMap, "sequence")));
      boolean levelOne = "true".equalsIgnoreCase(ArgUtils.getStringValue(elementToValueMap, "levelOne"));
      filter.setLevelOne(levelOne);
      boolean headersOnly = "true".equalsIgnoreCase(ArgUtils.getStringValue(elementToValueMap, "headersOnly"));
      filter.setHeadersOnly(headersOnly);
      filter.setOperator(ArgUtils.getStringValue(elementToValueMap, "filterOperator"));
      filter.setValue(ArgUtils.getStringValue(elementToValueMap, "filterValue"));
      return filter;
   }
}
