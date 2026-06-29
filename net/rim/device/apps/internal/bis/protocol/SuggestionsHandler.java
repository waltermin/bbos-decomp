package net.rim.device.apps.internal.bis.protocol;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import net.rim.device.apps.internal.bis.utils.xml.XMLToObjectHandler;

public final class SuggestionsHandler extends XMLToObjectHandler implements BISServiceConstants {
   private SAXParser _parser;
   private String[] _suggestions = new String[3];
   private static final String[] REQUIRED_ELEMENTS = new String[]{"suggestion"};

   public SuggestionsHandler() {
      super("suggestions", REQUIRED_ELEMENTS, true);
   }

   public final String[] loadFromXML(InputStream istream) {
      if (this._parser == null) {
         SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         this._parser = parserFactory.newSAXParser();
         this._parser.setAllowUndefinedNamespaces(true);
      }

      this._parser.parse(istream, this);
      return (String[])this.getResult();
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      Vector suggestions = (Vector)elementToValueMap.get("suggestion");
      int numSuggestions = suggestions.size();

      for (int i = 0; i < numSuggestions; i++) {
         this._suggestions[i] = (String)suggestions.elementAt(i);
      }

      return this._suggestions;
   }
}
