package net.rim.device.apps.internal.bis.protocol;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import net.rim.device.apps.internal.bis.utils.xml.XMLToObjectHandler;

public final class FiltersHandler extends XMLToObjectHandler implements BISServiceConstants {
   private SAXParser _parser;
   private static final String[] REQUIRED_ELEMENTS = new Object[0];

   public FiltersHandler() {
      super("filters", REQUIRED_ELEMENTS, true);
      this.setElementHandler("filter", new FilterHandler());
   }

   public final Vector loadFromXML(InputStream istream) {
      if (this._parser == null) {
         SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         this._parser = parserFactory.newSAXParser();
         this._parser.setAllowUndefinedNamespaces(true);
      }

      this._parser.parse(istream, this);
      return (Vector)this.getResult();
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      Object filters = elementToValueMap.get("filter");
      if (filters != null) {
         if (!(filters instanceof Object)) {
            Vector filtersVector = (Vector)(new Object());
            filtersVector.addElement(filters);
            return filtersVector;
         } else {
            return filters;
         }
      } else {
         return null;
      }
   }
}
