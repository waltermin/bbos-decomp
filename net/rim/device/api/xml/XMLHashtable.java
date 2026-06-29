package net.rim.device.api.xml;

import java.io.InputStream;
import java.util.Hashtable;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.vm.Memory;
import org.w3c.dom.Node;

public class XMLHashtable extends Hashtable implements Persistable {
   private boolean _includesNamespaces;
   private boolean _includesExplicitPaths;
   private static String[] _emptyStrings = new Object[0];
   private static String _emptyString = "";
   private static String _true = "true";
   private static final boolean DEBUG = false;

   public boolean includesNamespaces() {
      return this._includesNamespaces;
   }

   public boolean includesExplicitPaths() {
      return this._includesExplicitPaths;
   }

   public int getNumKeys(String key) {
      Object object = this.get(key);
      if (!(object instanceof Object)) {
         if (object instanceof Object) {
            return 1;
         } else {
            return object instanceof Object[] ? 1 : 0;
         }
      } else {
         return object;
      }
   }

   public int getNumValues(String key) {
      Object object = this.get(key);
      if (object instanceof Object) {
         return 1;
      } else {
         return object instanceof Object[] ? ((Object[])object).length : 0;
      }
   }

   public boolean isPresent(String key) {
      return this.get(key) != null;
   }

   public String getStringAt(String key, int index, String defaultValue) {
      Object object = this.get(key);
      if (!(object instanceof Object)) {
         return (String)(object instanceof Object[] ? ((Object[])object)[index] : defaultValue);
      } else {
         return (String)object;
      }
   }

   public String getString(String key, String defaultValue) {
      return this.getStringAt(key, 0, defaultValue);
   }

   public String getStringAt(String key, int index) {
      return this.getStringAt(key, index, null);
   }

   public String getString(String key) {
      return this.getStringAt(key, 0, null);
   }

   public boolean getBooleanAt(String key, int index, boolean defaultValue) {
      String value = this.getStringAt(key, index, null);
      return value == null ? defaultValue : StringUtilities.compareToIgnoreCase(value.trim(), _true, 1701707776) == 0;
   }

   public boolean getBoolean(String key, boolean defaultValue) {
      return this.getBooleanAt(key, 0, defaultValue);
   }

   public boolean getBooleanAt(String key, int index) {
      return this.getBooleanAt(key, index, false);
   }

   public boolean getBoolean(String key) {
      return this.getBooleanAt(key, 0, false);
   }

   public int getIntegerAt(String key, int index, int defaultValue, int radix) {
      Object object = this.get(key);
      String value;
      if (!(object instanceof Object)) {
         if (!(object instanceof Object[])) {
            return defaultValue;
         }

         value = (String)((Object[])object)[index];
      } else {
         value = (String)object;
      }

      value = value.trim();
      int start = 0;
      if (radix == 16 && value.length() > 2 && value.charAt(0) == '0' && value.charAt(1) == 'x') {
         start = 2;
      }

      return NumberUtilities.parseInt(value, start, Integer.MAX_VALUE, radix);
   }

   public int getIntegerAt(String key, int index, int defaultValue) {
      return this.getIntegerAt(key, index, defaultValue, 10);
   }

   public int getInteger(String key, int defaultValue, int radix) {
      return this.getIntegerAt(key, 0, defaultValue, radix);
   }

   public int getInteger(String key, int defaultValue) {
      return this.getIntegerAt(key, 0, defaultValue, 10);
   }

   public int getIntegerAt(String key, int index) {
      return this.getIntegerAt(key, index, -1, 10);
   }

   public int getInteger(String key) {
      return this.getIntegerAt(key, 0, -1, 10);
   }

   public XMLHashtable(SAXParser parser, InputStream input, boolean includeNamespaces, boolean includeExplicitPath) {
      this(parser, input, includeNamespaces, includeExplicitPath, _emptyString);
   }

   public XMLHashtable(SAXParser parser, InputStream input, boolean includeNamespaces, boolean includeExplicitPath, String path) {
      XMLHashtable$XMLToHashtableHandler handler = new XMLHashtable$XMLToHashtableHandler(this, includeNamespaces, includeExplicitPath, path, null);
      parser.parse(input, handler);
      this._includesNamespaces = includeNamespaces;
      this._includesExplicitPaths = includeExplicitPath;
   }

   public XMLHashtable(Node node) {
      if (node != null) {
         recurseDOM(node, this, _emptyString, (StringBuffer)(new Object()));
      }
   }

   private static void recurseDOM(Node node, XMLHashtable hashtable, String key, StringBuffer value) {
      switch (node.getNodeType()) {
         case 1:
            String name = node.getNodeName();
            int index = name.indexOf(58);
            if (index >= 0) {
               name = name.substring(index + 1);
            }

            key = ((StringBuffer)(new Object())).append(key).append('/').append(name).toString();
            value = (StringBuffer)(new Object());
            break;
         case 3:
            value.append(node.getNodeValue());
      }

      for (Node var6 = node.getFirstChild(); var6 != null; var6 = var6.getNextSibling()) {
         recurseDOM(var6, hashtable, key, value);
      }

      if (value.length() > 0) {
         value.setLength(0);
      }
   }

   static {
      Memory.createGroup(_emptyStrings);
      Memory.createGroup(_emptyString);
   }
}
