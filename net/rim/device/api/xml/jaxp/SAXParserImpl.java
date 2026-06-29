package net.rim.device.api.xml.jaxp;

import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.xml.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParserImpl extends SAXParser {
   private boolean _allowUndefinedNamespaces;
   private boolean _isNamespaceAware = true;
   private XMLReader _reader;
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

   @Override
   public XMLReader getXMLReader() {
      if (this._reader == null) {
         this.newXMLParser();
      }

      return this._reader;
   }

   @Override
   public boolean getAllowUndefinedNamespaces() {
      return this._allowUndefinedNamespaces;
   }

   @Override
   public void setAllowUndefinedNamespaces(boolean allowUndefinedNamespaces) {
      this._allowUndefinedNamespaces = allowUndefinedNamespaces;
   }

   @Override
   public boolean isNamespaceAware() {
      return this._isNamespaceAware;
   }

   @Override
   public void setNamespaceAware(boolean isNamespaceAware) {
      this._isNamespaceAware = isNamespaceAware;
   }

   @Override
   public boolean isValidating() {
      return false;
   }

   @Override
   public void parse(InputStream is, DefaultHandler dh) {
      if (is == null) {
         throw new Object("InputStream cannot be null");
      }

      int ch = is.read();
      switch (ch) {
         case 0:
            XMLParser xml = this.newXMLParser();
            xml.pushBackFirstChar(ch);
            xml.parse(is, dh);
            return;
         case 1:
         case 2:
         case 3:
         default:
            WBXMLParser wbxml = new WBXMLParser(is, ch);
            wbxml.parse(dh);
      }
   }

   @Override
   public void parse(InputSource is, DefaultHandler dh) {
      if (is == null) {
         throw new Object("InputStream cannot be null");
      }

      InputStream byteStream = is.getByteStream();
      if (byteStream != null) {
         this.parse(byteStream, dh);
      } else {
         this.newXMLParser().parse(is, dh);
      }
   }

   public void parse(InputStream is, DefaultHandler dh, String[] tagTable, String[] attrStartTable, String[] attrTable) {
      if (is == null) {
         throw new Object("InputStream cannot be null");
      }

      WBXMLParser wbxml = new WBXMLParser(is);
      wbxml.setTagTable(0, tagTable);
      wbxml.setAttrStartTable(0, attrStartTable);
      wbxml.setAttrValueTable(0, attrTable);
      wbxml.parse(dh);
   }

   public void parse(InputStream is, DefaultHandler dh, IntHashtable tagTables, IntHashtable attrStartTables, IntHashtable attrTables) {
      if (is == null) {
         throw new Object("InputStream cannot be null");
      }

      WBXMLParser wbxml = new WBXMLParser(is);
      wbxml.setTagTable(tagTables);
      wbxml.setAttrStartTable(attrStartTables);
      wbxml.setAttrValueTable(attrTables);
      wbxml.parse(dh);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void parse(String url, DefaultHandler dh, boolean autoCodeBookManagement) {
      StreamConnection s = (StreamConnection)Connector.open(url);
      HttpConnection httpConn = (HttpConnection)s;
      httpConn.setRequestMethod("GET");
      httpConn.setRequestProperty("Accept", WBXML_MIME_TYPE);
      httpConn.setRequestProperty("x-rim-transcode-content", "text/xml");
      httpConn.setRequestProperty("X-Rim-UseCodeBook", autoCodeBookManagement ? TRUE : FALSE);
      int status = httpConn.getResponseCode();
      if (status != 200) {
         throw new Object(((StringBuffer)(new Object("HTTP Error "))).append(status).toString());
      }

      WBXMLParser wbxml = null;
      if (autoCodeBookManagement) {
         String codeBookURL = httpConn.getHeaderField("X-RIM-CodeBookURL");
         String codeBookHash = httpConn.getHeaderField("X-RIM-CodeBookHash");
         if (codeBookHash != null) {
            FileConnection rootDir = null;
            boolean var128 = false /* VF: Semaphore variable */;

            label844:
            try {
               var128 = true;
               rootDir = (FileConnection)Connector.open(CB_CACHE_DIR);
               if (!rootDir.exists()) {
                  rootDir.mkdir();
                  var128 = false;
               } else {
                  var128 = false;
               }
            } finally {
               if (var128) {
                  if (rootDir != null) {
                     label839:
                     try {
                        rootDir.close();
                     } finally {
                        break label839;
                     }
                  }
                  break label844;
               }
            }

            StringBuffer file = (StringBuffer)(new Object(CB_CACHE_DIR));
            file.append(StringUtilities.toLowerCase(codeBookHash, 1701707776)).append(".wbxml");
            String fileName = file.toString();
            FileConnection cb = (FileConnection)Connector.open(fileName);
            if (!cb.exists() && codeBookURL != null) {
               StreamConnection s2 = (StreamConnection)Connector.open(codeBookURL);
               HttpConnection httpConn2 = (HttpConnection)s2;
               httpConn2.setRequestMethod("GET");
               httpConn2.setRequestProperty("Accept", CB_MIME_TYPE);
               httpConn2.setRequestProperty("x-rim-transcode-content", "text/xml, application/xml-dtd");
               httpConn2.setRequestProperty("X-Rim-ConvertXSD", TRUE);
               httpConn2.setRequestProperty("X-Rim-UseCodeBook", TRUE);
               int status2;
               if ((status2 = httpConn2.getResponseCode()) != 200) {
                  throw new Object(((StringBuffer)(new Object("HTTP Error getting codebook: "))).append(status2).toString());
               }

               InputStream cbInputStream = httpConn2.openInputStream();
               OutputStream fileOut = cb.openOutputStream();
               boolean var107 = false /* VF: Semaphore variable */;

               try {
                  var107 = true;
                  byte[] chunk = new byte[4096];

                  while (true) {
                     int numRead = cbInputStream.read(chunk);
                     if (numRead == -1) {
                        var107 = false;
                        break;
                     }

                     fileOut.write(chunk, 0, numRead);
                  }
               } finally {
                  if (var107) {
                     if (fileOut != null) {
                        label805:
                        try {
                           fileOut.close();
                        } finally {
                           break label805;
                        }
                     }
                  }
               }

               if (fileOut != null) {
                  label824:
                  try {
                     fileOut.close();
                  } finally {
                     break label824;
                  }
               }
            }

            WBXMLCodeBookCreationHandler ch = new WBXMLCodeBookCreationHandler();
            InputStream in = cb.openInputStream();
            boolean var55 = false /* VF: Semaphore variable */;
            boolean var76 = false /* VF: Semaphore variable */;

            try {
               try {
                  var76 = true;
                  var55 = true;
                  WBXMLParser var141 = new WBXMLParser(in);
                  var141.parse(ch);
                  wbxml = new WBXMLParser(s.openInputStream());
                  wbxml.setTagTable(ch.getTagTable());
                  wbxml.setAttrStartTable(ch.getAttrStartTable());
                  wbxml.setAttrValueTable(ch.getAttrValueTable());
                  var55 = false;
                  var76 = false;
               } finally {
                  if (var76) {
                     label798:
                     try {
                        cb.delete();
                     } finally {
                        break label798;
                     }
                  }
               }
            } finally {
               if (var55) {
                  label794:
                  try {
                     in.close();
                  } finally {
                     break label794;
                  }
               }
            }

            label811:
            try {
               in.close();
            } finally {
               break label811;
            }
         }
      }

      if (wbxml == null) {
         wbxml = new WBXMLParser(s.openInputStream());
      }

      wbxml.parse(dh);
   }
}
