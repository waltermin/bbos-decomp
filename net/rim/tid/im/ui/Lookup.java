package net.rim.tid.im.ui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.tid.im.SLInputMethod;
import net.rim.tid.im.conv.SLVariants;

public class Lookup extends PopupScreen implements LookupIf {
   private Object _parent;
   public static final byte LOOKUP_DEFAULT = 0;
   public static final byte LOOKUP_WITH_AMOUNT = 1;
   public static final byte LOOKUP_WITH_NUMBERS = 8;
   public static final byte INSIDE_SPACE = 2;
   public static final byte INSIDE_SPACE_DOUBLE = 4;
   public static final byte INSIDE_SPACE_WITH_LINE = 3;
   public static final byte SPACE = 5;
   private static Lookup _instance;

   public void setParent(Object parent) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public Object getParent() {
      return this._parent;
   }

   public void resetSizeWithAnchor(int yAnchor) {
   }

   public void reset() {
      throw null;
   }

   public void setBounds(int _1, int _2, int _3, int _4, int _5) {
      throw null;
   }

   public int getSelectedIndex() {
      throw null;
   }

   @Override
   public void setVariants(SLVariants _1) {
      throw null;
   }

   @Override
   public int actionPerformed(Object _1, int _2, Object _3) {
      throw null;
   }

   @Override
   public void composedTextChanged(XYRect composedBounds) {
   }

   @Override
   public void setVisible(boolean _1) {
      throw null;
   }

   @Override
   public void init(SLInputMethod _1, Locale _2, int _3) {
      throw null;
   }

   @Override
   public void setLabels(String _1, String _2) {
      throw null;
   }

   @Override
   public void setStyle(byte style) {
   }

   Lookup(Manager manager) {
   }

   public static Lookup getInstance(SLInputMethod aInputMethod, Locale locale, int aType) {
      try {
         if (_instance == null) {
            _instance = (Lookup)Class.forName("net.rim.tid.im.ui.LookupImpl2").newInstance();
         }
      } finally {
         ;
      }

      _instance.init(aInputMethod, locale, aType);
      return _instance;
   }
}
