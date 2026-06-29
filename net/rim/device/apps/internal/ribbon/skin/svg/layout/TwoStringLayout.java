package net.rim.device.apps.internal.ribbon.skin.svg.layout;

import java.util.Hashtable;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.TextGraphics;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.TextNodeImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.VisualNodeImpl;
import net.rim.plazmic.internal.mediaengine.ui.PME12GraphicsImpl;

class TwoStringLayout extends HorizontalLayout {
   private char _ellipsis = 8230;
   private int _spacing = 5;
   private int _textHandle1;
   private int _textHandle2;
   private int _maxInitialWidth = 100;
   private char[] _origString1;
   private char[] _origString2;
   private char[] _layedOutString1;
   private char[] _layedOutString2;
   private int _fontSize1;
   private int _fontWeight1;
   private int _fontStyle1;
   private String _fontFamily1;
   private int _fontSize2;
   private int _fontWeight2;
   private int _fontStyle2;
   private String _fontFamily2;
   private TextGraphics _textGraphics = (TextGraphics)(new Object("BBMillbank", 10));
   private DrawTextParam _textParams = (DrawTextParam)(new Object());
   private StringBuffer _textBuffer = (StringBuffer)(new Object());
   private static final int ELLIPSIS_LENGTH = 1;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public TwoStringLayout(ModelInteractorImpl model, Hashtable params) {
      super(model, params);
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         String e = params.get("ids");
         if (e != null) {
            int delim = ((String)e).indexOf(59);
            this._textHandle1 = model.getHandle(((String)e).substring(0, delim));
            this._textHandle2 = model.getHandle(((String)e).substring(delim + 1));
            this._origString1 = TextNodeImpl.getString(this._textHandle1, model);
            this._origString2 = TextNodeImpl.getString(this._textHandle2, model);
         }

         e = params.get("spacing");
         if (e != null) {
            this._spacing = Integer.parseInt((String)e);
         }

         e = params.get("ellipsis");
         if (e != null) {
            this._ellipsis = ((String)e).charAt(0);
         }

         e = params.get("firstsectionwidth");
         if (e != null) {
            this._maxInitialWidth = Integer.parseInt((String)e);
            return;
         }

         var6 = false;
      } finally {
         if (var6) {
            System.out.println("Invalid number format in TwoStringLayout");
            return;
         }
      }
   }

   @Override
   public void layout() {
      throw new RuntimeException("cod2jar: invokevirtual: receiver not in world");
   }

   private void doLayout() {
      int widthLeft = super._width;
      int maxInitialWidth = this._origString2.length > 0 ? Math.min(this._maxInitialWidth, widthLeft) : widthLeft;
      int style = PME12GraphicsImpl.getFontStyle(this._fontStyle1);
      style |= PME12GraphicsImpl.getFontWeight(this._fontWeight1);
      this._textGraphics.setTypefaceName(this._fontFamily1);
      this._textGraphics.setHeightWithLeading(Fixed32.toInt(this._fontSize1));
      this._textGraphics.setStyle(style);
      this._textGraphics.setAntialiasingMode(1);
      int len1 = this._origString1.length;
      int advance;
      if (len1 > 0) {
         this._textBuffer.setLength(0);
         this._textBuffer.append(this._origString1);
         advance = this._textGraphics.measureText(this._textBuffer, 0, len1, this._textParams, null);
      } else {
         advance = 0;
      }

      if (advance > maxInitialWidth) {
         while (true) {
            len1--;
            this._textBuffer.setLength(0);
            this._textBuffer.append(this._origString1, 0, len1);
            this._textBuffer.append(this._ellipsis);
            advance = this._textGraphics.measureText(this._textBuffer, 0, len1 + 1, this._textParams, null);
            if (advance <= maxInitialWidth) {
               char[] s1 = new char[len1 + 1];
               System.arraycopy(this._origString1, 0, s1, 0, len1);
               s1[len1] = this._ellipsis;
               this._layedOutString1 = s1;
               if (maxInitialWidth >= widthLeft) {
                  this._layedOutString2 = new char[0];
                  this.setLayedOutStrings();
                  return;
               }
               break;
            }
         }
      } else {
         this._layedOutString1 = this._origString1;
      }

      widthLeft -= advance + this._spacing;
      int pos2 = VisualNodeImpl.getX(this._textHandle1, super._model);
      if (advance > 0) {
         pos2 += Fixed32.toFP(advance + this._spacing);
      }

      VisualNodeImpl.setX(pos2, this._textHandle2, super._model);
      style = PME12GraphicsImpl.getFontStyle(this._fontStyle2);
      style |= PME12GraphicsImpl.getFontWeight(this._fontWeight2);
      this._textGraphics.setTypefaceName(this._fontFamily2);
      this._textGraphics.setHeightWithLeading(Fixed32.toInt(this._fontSize2));
      this._textGraphics.setStyle(style);
      this._textGraphics.setAntialiasingMode(1);
      this._textBuffer.setLength(0);
      this._textBuffer.append(this._ellipsis);
      advance = this._textGraphics.measureText(this._textBuffer, 0, 1, this._textParams, null);
      if (widthLeft < advance) {
         this._layedOutString2 = new char[0];
         this.setLayedOutStrings();
      } else {
         int len2 = this._origString2.length;
         if (len2 > 0) {
            this._textBuffer.setLength(0);
            this._textBuffer.append(this._origString2);
            advance = this._textGraphics.measureText(this._textBuffer, 0, len2, this._textParams, null);
         } else {
            advance = 0;
         }

         if (advance <= widthLeft) {
            this._layedOutString2 = this._origString2;
            this.setLayedOutStrings();
         } else {
            do {
               len2--;
               this._textBuffer.setLength(0);
               this._textBuffer.append(this._origString2, 0, len2);
               this._textBuffer.append(this._ellipsis);
               advance = this._textGraphics.measureText(this._textBuffer, 0, len2 + 1, this._textParams, null);
            } while (advance > widthLeft);

            char[] s2 = new char[len2 + 1];
            System.arraycopy(this._origString2, 0, s2, 0, len2);
            s2[len2] = this._ellipsis;
            this._layedOutString2 = s2;
            this.setLayedOutStrings();
         }
      }
   }

   private void setLayedOutStrings() {
      TextNodeImpl.setString(this._layedOutString1, this._textHandle1, super._model);
      TextNodeImpl.setString(this._layedOutString2, this._textHandle2, super._model);
   }

   @Override
   public int getWidth() {
      return super._width;
   }

   @Override
   public int getHeight() {
      return 20;
   }
}
