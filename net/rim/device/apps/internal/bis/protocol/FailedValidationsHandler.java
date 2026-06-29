package net.rim.device.apps.internal.bis.protocol;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import net.rim.device.apps.internal.bis.utils.xml.XMLToObjectHandler;

public final class FailedValidationsHandler extends XMLToObjectHandler implements BISServiceConstants {
   private SAXParser _parser;
   private int[] _failedMboxes;
   private static final String[] REQUIRED_ELEMENTS = new String[]{"srcMboxId"};

   public FailedValidationsHandler() {
      super("failedValidations", REQUIRED_ELEMENTS, true);
   }

   public final int[] loadFromXML(InputStream istream) {
      if (this._parser == null) {
         SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         this._parser = parserFactory.newSAXParser();
         this._parser.setAllowUndefinedNamespaces(true);
      }

      this._parser.parse(istream, this);
      return (int[])this.getResult();
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      Object failed = elementToValueMap.get("srcMboxId");
      if (failed == null) {
         return new int[0];
      }

      Vector list = new Vector();
      if (failed instanceof String) {
         list.addElement(failed);
      } else {
         list = (Vector)failed;
      }

      int numFailed = list.size();
      this._failedMboxes = new int[numFailed];

      for (int i = 0; i < numFailed; i++) {
         this._failedMboxes[i] = Integer.parseInt((String)list.elementAt(i));
      }

      return this._failedMboxes;
   }
}
