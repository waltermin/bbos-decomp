package net.rim.device.apps.internal.ribbon.skin.svg.layout;

import java.util.Hashtable;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.TextGraphics;
import net.rim.device.api.ui.TextMetrics;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.TextNodeImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.VisualNodeImpl;
import net.rim.plazmic.internal.mediaengine.ui.PME12GraphicsImpl;

class ImageStringLayout extends HorizontalLayout {
   private int _imageHandle;
   private int _textHandle;
   private int _anchor;
   private int _spacing = 3;
   private char _ellipsis = 8230;
   private int _layedOutImageWidth = -1;
   private char[] _origString;
   private char[] _layedOutString;
   private int _fontSize;
   private int _fontWeight;
   private int _fontStyle;
   private String _fontFamily;
   private static final int ELLIPSIS_LENGTH;
   private static TextGraphics _textGraphics = (TextGraphics)(new Object("BBMillbank", 10));
   private static DrawTextParam _textParams = (DrawTextParam)(new Object());
   private static TextMetrics _textMetrics = (TextMetrics)(new Object());
   private static StringBuffer _textBuffer = (StringBuffer)(new Object());

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public ImageStringLayout(ModelInteractorImpl model, Hashtable params) {
      super(model, params);
      String value = (String)params.get("ids");
      if (value != null) {
         int delim = value.indexOf(59);
         this._imageHandle = model.getHandle(value.substring(0, delim));
         this._textHandle = model.getHandle(value.substring(delim + 1));
         this._origString = TextNodeImpl.getString(this._textHandle, model);
      }

      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         value = (String)params.get("spacing");
         if (value != null) {
            this._spacing = Integer.parseInt(value);
         }

         value = (String)params.get("anchor");
         if (value != null) {
            this._anchor = Integer.parseInt(value);
            return;
         }

         var6 = false;
      } finally {
         if (var6) {
            System.out.println("Invalid number format in ImageStringLayout");
            return;
         }
      }
   }

   @Override
   public void layout() {
      throw new RuntimeException("cod2jar: invokevirtual: receiver not in world");
   }

   private void doLayout() {
      int allowedTextWidth = this._layedOutImageWidth > 0 ? super._width - this._layedOutImageWidth - this._spacing : super._width;
      int style = PME12GraphicsImpl.getFontStyle(this._fontStyle);
      style |= PME12GraphicsImpl.getFontWeight(this._fontWeight);
      _textGraphics.setTypefaceName(this._fontFamily);
      _textGraphics.setHeightWithLeading(Fixed32.toInt(this._fontSize));
      _textGraphics.setStyle(style);
      _textGraphics.setAntialiasingMode(1);
      int stringLength = this._origString.length;
      int textWidth;
      if (stringLength > 0) {
         _textBuffer.setLength(0);
         _textBuffer.append(this._origString);
         _textGraphics.measureText(_textBuffer, 0, stringLength, _textParams, _textMetrics);
         textWidth = _textMetrics.iBoundsBrX - _textMetrics.iBoundsTlX;
      } else {
         textWidth = 0;
      }

      if (textWidth > allowedTextWidth) {
         while (true) {
            stringLength--;
            _textBuffer.setLength(0);
            _textBuffer.append(this._origString, 0, stringLength);
            _textBuffer.append(this._ellipsis);
            _textGraphics.measureText(_textBuffer, 0, stringLength + 1, _textParams, _textMetrics);
            textWidth = _textMetrics.iBoundsBrX - _textMetrics.iBoundsTlX;
            if (textWidth <= allowedTextWidth) {
               char[] newString = new char[stringLength + 1];
               System.arraycopy(this._origString, 0, newString, 0, stringLength);
               newString[stringLength] = this._ellipsis;
               this._layedOutString = newString;
               break;
            }
         }
      } else {
         this._layedOutString = this._origString;
      }

      int imgLeft;
      int textLeft;
      switch (super._align) {
         case 0:
            imgLeft = this._anchor;
            textLeft = this._layedOutImageWidth > 0 ? this._anchor + this._layedOutImageWidth + this._spacing : this._anchor;
            break;
         case 2:
            textLeft = this._anchor - textWidth;
            imgLeft = textLeft - this._layedOutImageWidth - this._spacing;
            break;
         default:
            if (this._layedOutImageWidth != 0 && textWidth + 2 * (this._layedOutImageWidth + this._spacing) >= super._width) {
               textLeft = this._anchor - super._width / 2 + this._layedOutImageWidth + this._spacing;
            } else {
               textLeft = this._anchor - textWidth / 2;
            }

            imgLeft = textLeft - this._layedOutImageWidth - this._spacing;
      }

      TextNodeImpl.setString(this._layedOutString, this._textHandle, super._model);
      VisualNodeImpl.setX(Fixed32.toFP(textLeft), this._textHandle, super._model);
      VisualNodeImpl.setX(Fixed32.toFP(imgLeft), this._imageHandle, super._model);
   }
}
