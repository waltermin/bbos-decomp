package net.rim.device.api.xml.jaxp;

import net.rim.device.api.util.IntHashtable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WBXMLCodeBookCreationHandler extends DefaultHandler implements RIMWBXMLHandler {
   private IntHashtable _tagTable = new IntHashtable(3);
   private IntHashtable _attrStartTable = new IntHashtable(3);
   private IntHashtable _attrValueTable = new IntHashtable(3);
   private String _codeBookID;
   private long _codeBookHash;
   private int _currentPage = -1;
   private static final int _pageTableSize = 3;
   private static final int _hashTableSize = 31;

   public WBXMLCodeBookCreationHandler() {
      this._codeBookID = null;
   }

   public IntHashtable getTagTable() {
      return this._tagTable;
   }

   public IntHashtable getAttrStartTable() {
      return this._attrStartTable;
   }

   public IntHashtable getAttrValueTable() {
      return this._attrValueTable;
   }

   public String getCodeBookID() {
      return this._codeBookID;
   }

   public long getCodeBookHash() {
      return this._codeBookHash;
   }

   @Override
   public void startElement(int ElementCode, String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (localName.equals("mdscodebook")) {
         String nameAttr = attributes.getValue("name");
         this._codeBookID = nameAttr;
         String hashAttr = attributes.getValue("hash");
         if (hashAttr != null) {
            if (hashAttr.startsWith("0x")) {
               if (hashAttr.length() < 18) {
                  this._codeBookHash = Long.parseLong(hashAttr.substring(2), 16);
                  return;
               }

               this._codeBookHash = Long.parseLong(hashAttr.substring(2, 10), 16) << 32 | Long.parseLong(hashAttr.substring(10), 16);
               return;
            }

            this._codeBookHash = Long.parseLong(hashAttr);
            return;
         }
      } else {
         String value = attributes.getValue("value");
         int intValue = -1;
         if (value.startsWith("0x")) {
            intValue = Integer.parseInt(value.substring(2), 16);
         } else {
            intValue = Integer.parseInt(value, 10);
         }

         if (localName.equals("page")) {
            this._currentPage = intValue;
            return;
         }

         String nameAttr = attributes.getValue("name");
         IntHashtable table = null;
         if (localName.equals("tag")) {
            table = this._tagTable;
         } else if (localName.equals("attrStart")) {
            table = this._attrStartTable;
         } else {
            if (!localName.equals("attrValue")) {
               throw new SAXException("Error parsing codebook");
            }

            table = this._attrValueTable;
         }

         IntHashtable page = null;
         if ((page = (IntHashtable)table.get(this._currentPage)) == null) {
            page = new IntHashtable(31);
            table.put(this._currentPage, page);
         }

         page.put(intValue, nameAttr);
      }
   }

   @Override
   public void endElement(int ElementCode, String uri, String localName, String qName) {
   }
}
