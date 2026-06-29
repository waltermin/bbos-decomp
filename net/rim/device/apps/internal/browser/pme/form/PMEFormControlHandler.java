package net.rim.device.apps.internal.browser.pme.form;

import java.io.ByteArrayInputStream;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldForeignObject;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import net.rim.device.apps.internal.browser.markup.HTMLBinaryConstants;
import net.rim.device.apps.internal.browser.util.ObjectVector;
import net.rim.plazmic.internal.mediaengine.MediaModel;
import net.rim.plazmic.internal.mediaengine.ResourceContext;
import net.rim.plazmic.internal.mediaengine.ResourceProvider;
import net.rim.plazmic.mediaengine.MediaException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public final class PMEFormControlHandler extends DefaultHandler implements ResourceProvider {
   private String _type;
   private String _ref;
   private String _label;
   private String _value;
   private String _modelId;
   private ObjectVector _options;
   private ObjectVector _cookies;
   private int _itemCount;
   private boolean _isLabel;
   private boolean _isCookie;
   private boolean _isText;
   private boolean _isEditable = true;
   private String _fontStyle;
   private String _fontWeight;
   private String _fontFamily;
   private String _fontSize;
   private String _fontFill;
   private String _style;
   private String _bkColor;
   private int _rows = -1;
   private int _columns = -1;
   private SAXParser _parser;
   public static final String[] EXTENSION_IDS = new String[]{"pme://xforms-controls", "pme://ui-controls"};

   public final void clear() {
      this._type = this._ref = this._modelId = this._label = this._value = this._fontStyle = this._fontWeight = this._fontFamily = this._fontSize = this._fontFill = this._style = this._bkColor = null;
      this._options = this._cookies = null;
      this._itemCount = 0;
      this._isLabel = this._isCookie = false;
      this._isEditable = true;
      this._rows = this._columns = -1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object createResource(String type, Object data, ResourceContext context, Object referrer) {
      if (data instanceof String && this.isSupported(type)) {
         boolean var12 = false /* VF: Semaphore variable */;

         FieldForeignObject var19;
         label110: {
            try {
               try {
                  var12 = true;
                  if (this._parser == null) {
                     SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                     this._parser = parserFactory.newSAXParser();
                     this._parser.setAllowUndefinedNamespaces(true);
                  }

                  ByteArrayInputStream in = new ByteArrayInputStream(((String)data).getBytes());
                  this._parser.parse(in, this);
                  this.parseStyle();
                  Object resource = null;
                  if (this._modelId != null && context != null) {
                     Object model = context.get("Media");
                     if (model instanceof MediaModel) {
                        resource = ((MediaModel)model).getResource(this._modelId);
                     }
                  }

                  Field field = null;
                  if (!(resource instanceof PMEForm)) {
                     field = this.createField();
                  } else {
                     PMEForm pmeForm = (PMEForm)resource;
                     if (!(pmeForm.getInitialValue(this._ref) instanceof FormField)) {
                        field = this.createFormControl(pmeForm);
                        if (field instanceof FormField) {
                           pmeForm.addControl(this._ref, (FormField)field);
                        }
                     }
                  }

                  if (field != null) {
                     this.applyStyle(field);
                     var19 = new FieldForeignObject(field);
                     var12 = false;
                     break label110;
                  }

                  var12 = false;
               } catch (Throwable var15) {
                  throw new MediaException(-1, e.getMessage(), e);
               }
            } finally {
               if (var12) {
                  this.clear();
               }
            }

            this.clear();
            return null;
         }

         this.clear();
         return var19;
      } else {
         return null;
      }
   }

   @Override
   public final Object createResourceFromURI(String uri, String suggestedType, ResourceContext context, Object referrer) {
      return null;
   }

   private final boolean isSupported(String type) {
      for (int i = EXTENSION_IDS.length - 1; i >= 0; i--) {
         if (EXTENSION_IDS[i].equals(type)) {
            return true;
         }
      }

      return "foreignObject".equals(type);
   }

   private final Field createFormControl(PMEForm pmeForm) {
      Field field = null;
      if ("input".equals(this._type)) {
         String value = (String)pmeForm.getInitialValue(this._ref);
         return new PMETextInput(this._ref, this._label, value, 1000000, 1407374883553280L, 2147483648L, this._rows, this._columns);
      }

      if ("textarea".equals(this._type)) {
         String value = (String)pmeForm.getInitialValue(this._ref);
         return new PMETextInput(this._ref, this._label, value, 1000000, 281474976710656L, 0, this._rows, this._columns);
      }

      if ("select1".equals(this._type)) {
         this._options.trimToSize();
         this._cookies.trimToSize();
         String value = (String)pmeForm.getInitialValue(this._ref);
         int selIndex = 0;
         if (value != null) {
            for (int i = this._cookies.size() - 1; i >= 0; i--) {
               if (value.equals(this._cookies.elementAt(i))) {
                  selIndex = i;
                  break;
               }
            }
         }

         field = new PMEChoiceField(this._ref, this._label, this._options.getArray(), this._cookies.getArray(), selIndex, 4831838208L);
      }

      return field;
   }

   private final Field createField() {
      Field field = null;
      if ("textarea".equals(this._type)) {
         long style;
         if (!this._isEditable) {
            style = 27021597764222976L;
         } else {
            style = 22517998136852480L;
         }

         field = new PMETextInput("", this._label, this._value, Integer.MAX_VALUE, 281474976710656L, style, this._rows, this._columns);
      }

      return field;
   }

   private final void parseStyle() {
      if (this._style != null) {
         StringTokenizer t = new StringTokenizer(this._style, " :;");

         while (t.hasMoreTokens()) {
            String token = t.nextToken();
            if ("background-color".equals(token)) {
               if (t.hasMoreTokens()) {
                  this._bkColor = t.nextToken();
               }
            } else if ("rows".equals(token)) {
               if (t.hasMoreTokens()) {
                  try {
                     this._rows = Integer.parseInt(t.nextToken());
                  } finally {
                     continue;
                  }
               }
            } else if ("columns".equals(token) && t.hasMoreTokens()) {
               try {
                  this._columns = Integer.parseInt(t.nextToken());
               } finally {
                  continue;
               }
            }
         }
      }
   }

   private final void applyStyle(Field field) {
      Font font = this.getFont();
      if (font != null) {
         field.setFont(font);
      }

      if (this._fontFill != null || this._bkColor != null) {
         Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
         ThemeAttributeSet$Writer writer = themeWriter.createThemeAttributeSetWriter(null);
         if (this._bkColor != null) {
            writer.setColor(0, HTMLBinaryConstants.resolveColor(this._bkColor));
         }

         if (this._fontFill != null) {
            writer.setColor(1, HTMLBinaryConstants.resolveColor(this._fontFill));
         }

         Theme t = ThemeManager.getActiveTheme();
         if (t != null) {
            ThemeAttributeSet attr = writer.getThemeAttributeSet();
            field.setThemeAttributesSpecial(attr, null);
         }
      }
   }

   private final Font getFont() {
      Font font = null;
      if (this._fontFamily != null || this._fontStyle != null || this._fontSize != null || this._fontWeight != null) {
         try {
            FontFamily fontFamily = null;
            if (this._fontFamily != null) {
               label129:
               try {
                  fontFamily = FontFamily.forName(this._fontFamily);
               } finally {
                  break label129;
               }
            }

            int style = 0;
            if ("italic".equals(this._fontStyle)) {
               style = 2;
            }

            if ("bold".equals(this._fontWeight)) {
               style |= 1;
            }

            int units = 0;
            int height = Font.getDefault().getHeight();
            if (this._fontSize != null) {
               label121:
               try {
                  height = Integer.parseInt(this._fontSize);
               } finally {
                  break label121;
               }
            }

            if (fontFamily == null) {
               return Font.getDefault().derive(style, height, units);
            }

            font = fontFamily.getFont(style, height, units);
         } finally {
            return font;
         }
      }

      return font;
   }

   @Override
   public final void characters(char[] ch, int start, int length) {
      if (ch[0] != '\n') {
         if (this._itemCount == 0) {
            if (this._isCookie || this._isText) {
               this._value = new String(ch, start, length);
               return;
            }

            if (this._isLabel) {
               this._label = new String(ch, start, length);
               return;
            }
         } else if (this._isLabel || this._isCookie) {
            ObjectVector v = this._isCookie ? this._cookies : this._options;
            v.addElement(new String(ch, start, length));
         }
      }
   }

   @Override
   public final void startElement(String uri, String localName, String qName, Attributes attributes) {
      this._isText = this._isLabel = this._isCookie = false;
      if (!"input".equals(localName) && !"textarea".equals(localName) && !"select1".equals(localName)) {
         if ("label".equals(localName)) {
            this._isLabel = true;
            return;
         }

         if ("value".equals(localName)) {
            this._isCookie = true;
            return;
         }

         if ("item".equals(localName)) {
            this._itemCount++;
            return;
         }

         if ("text".equals(localName)) {
            this._isText = true;

            for (int i = 0; i < attributes.getLength(); i++) {
               String attrValue = attributes.getLocalName(i);
               if ("font-family".equals(attrValue)) {
                  this._fontFamily = attributes.getValue(i);
               } else if ("font-size".equals(attrValue)) {
                  this._fontSize = attributes.getValue(i);
               } else if ("font-style".equals(attrValue)) {
                  this._fontStyle = attributes.getValue(i);
               } else if ("fill".equals(attrValue)) {
                  this._fontFill = attributes.getValue(i);
               } else if ("font-weight".equals(attrValue)) {
                  this._fontWeight = attributes.getValue(i);
               }
            }
         }
      } else {
         this._type = localName;

         for (int i = 0; i < attributes.getLength(); i++) {
            String attrValue = attributes.getLocalName(i);
            if ("ref".equals(attrValue)) {
               this._ref = attributes.getValue(i);
            } else if ("editable".equals(attrValue)) {
               this._isEditable = "true".equals(attributes.getValue(i));
            } else if ("style".equals(attrValue)) {
               this._style = attributes.getValue(i);
            } else if ("model".equals(attrValue)) {
               this._modelId = attributes.getValue(i);
            }
         }

         if ("select1".equals(localName)) {
            this._options = new ObjectVector();
            this._cookies = new ObjectVector();
            return;
         }
      }
   }
}
