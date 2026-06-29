package net.rim.device.apps.internal.browser.pme.form;

import java.io.ByteArrayInputStream;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import net.rim.plazmic.internal.mediaengine.ResourceContext;
import net.rim.plazmic.internal.mediaengine.ResourceProvider;
import net.rim.plazmic.mediaengine.MediaException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public final class PMEFormHandler extends DefaultHandler implements ResourceProvider {
   private PMEForm _form;
   private String _currentControl;
   private boolean _isInInstance;
   private SAXParser _parser;
   public static final String EXTENSION_ID = "pme://xforms";

   public final void clear() {
      this._form = null;
      this._isInInstance = false;
      this._currentControl = null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object createResource(String type, Object data, ResourceContext context, Object referrer) {
      if (data instanceof String && "pme://xforms".equals(type)) {
         boolean var10 = false /* VF: Semaphore variable */;

         SAXParserFactory parserFactory;
         label74: {
            PMEForm var6;
            try {
               try {
                  var10 = true;
                  if (this._parser == null) {
                     parserFactory = SAXParserFactory.newInstance();
                     this._parser = parserFactory.newSAXParser();
                     this._parser.setAllowUndefinedNamespaces(true);
                  }

                  if (context == null) {
                     parserFactory = null;
                     var10 = false;
                     break label74;
                  }

                  this._form = new PMEForm((BrowserContent)context.get("BrowserContent"));
                  ByteArrayInputStream in = new ByteArrayInputStream(((String)data).getBytes());
                  this._parser.parse(in, this);
                  var6 = this._form;
                  var10 = false;
               } catch (Throwable var13) {
                  throw new MediaException(-1, e.getMessage(), e);
               }
            } finally {
               if (var10) {
                  this.clear();
               }
            }

            this.clear();
            return var6;
         }

         this.clear();
         return parserFactory;
      } else {
         return null;
      }
   }

   @Override
   public final Object createResourceFromURI(String uri, String suggestedType, ResourceContext context, Object referrer) {
      return null;
   }

   @Override
   public final void characters(char[] ch, int start, int length) {
      if (this._isInInstance && ch[0] != '\n' && this._currentControl != null) {
         this._form.addControl(this._currentControl, new String(ch, start, length));
      }
   }

   @Override
   public final void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (this._isInInstance) {
         this._currentControl = localName;
      } else if ("instance".equals(localName)) {
         this._isInInstance = true;
      } else {
         if (!"submission".equals(localName)) {
            if ("trigger".equals(localName)) {
               for (int i = attributes.getLength() - 1; i >= 0; i--) {
                  String attrName = attributes.getLocalName(i);
                  if ("id".equals(attrName)) {
                     this._form.setResetTrigger(attributes.getValue(i));
                     return;
                  }
               }
            } else if ("submit".equals(localName)) {
               String triggerId = null;
               String submissionId = null;

               for (int i = attributes.getLength() - 1; i >= 0; i--) {
                  String attrName = attributes.getLocalName(i);
                  if ("id".equals(attrName)) {
                     triggerId = attributes.getValue(i);
                  } else if ("submission".equals(attrName)) {
                     submissionId = attributes.getValue(i);
                  }
               }

               this._form.addSubmitTrigger(triggerId, submissionId);
            }
         } else {
            PMEForm$PMEFormSubmission submit = new PMEForm$PMEFormSubmission();

            for (int i = attributes.getLength() - 1; i >= 0; i--) {
               String attrName = attributes.getLocalName(i);
               if ("action".equals(attrName)) {
                  submit._action = attributes.getValue(i);
               } else if ("method".equals(attrName)) {
                  String method = attributes.getValue(i);
                  if (method == null || "get".equals(method)) {
                     submit._post = false;
                     submit._multipart = false;
                  } else if ("form-data-post".equals(method)) {
                     submit._post = true;
                     submit._multipart = true;
                  }
               } else if ("encoding".equals(attrName)) {
                  submit._encoding = attributes.getValue(i);
               } else if ("id".equals(attrName)) {
                  this._form.addSubmission(attributes.getValue(i), submit);
               }
            }
         }
      }
   }

   @Override
   public final void endElement(String uri, String localName, String qName) {
      if (this._isInInstance && "instance".equals(localName)) {
         this._isInInstance = false;
         this._currentControl = null;
      }
   }
}
