package net.rim.device.apps.internal.commonmodels.body;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.StringMatch;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ColumnPaintProvider;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.messaging.OutgoingMessage;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.PopupStatus;

public class BodyModelImpl
   implements PersistableRIMModel,
   BodyModel,
   FieldProvider,
   ColumnPaintProvider,
   PaintProvider,
   ConversionProvider,
   MatchProvider,
   CloneProvider,
   EncryptableProvider {
   private Object _textEncoding;
   static final int MEMO_BODY_SIZE = 8191;
   static final int TASK_FIELD_TAG = 3;
   static final int MEMO_FIELD_TAG = 2;
   private static final int MAX_MESSAGE_SIZE = 16384;

   @Override
   public int paint(Graphics g, int x, int y, int width, int height, Object context) {
      return 0;
   }

   protected void appendText(String more) {
      this._textEncoding = PersistentContent.encodeAndAppend(more, true, true, this._textEncoding);
   }

   @Override
   public int match(Object criteria) {
      SearchCriterion crit = (SearchCriterion)criteria;
      switch (crit.getType()) {
         case 1:
         case 21:
            String body = this.getText();
            if (body != null && ((StringMatch)crit.getValue()).indexOf(body) >= 0) {
               return 1;
            }

            return 0;
         default:
            return -1;
      }
   }

   public String getTextPrefix() {
      return StringUtilities.removeLineBreaksInString(this.getText(100));
   }

   @Override
   public String getText() {
      return this.getText(Integer.MAX_VALUE);
   }

   @Override
   public boolean convert(Object context, Object target) {
      String text = this.getText();
      if (target instanceof Object && text != null && text.length() > 0) {
         OutgoingMessage outgoingTransmission = (OutgoingMessage)target;
         outgoingTransmission.setTextAndType(text, context);
         return true;
      }

      if (context instanceof Object) {
         ContextObject contextObject = (ContextObject)context;
         if (contextObject.getFlag(11) && contextObject.getFlag(43) && contextObject.getFlag(54)) {
            if (target instanceof Object) {
               StringBuffer stringBuffer = (StringBuffer)target;
               if (text != null) {
                  stringBuffer.append("\rNotes:");
                  int length = text.length();

                  for (int i = 0; i < length; i++) {
                     char c = text.charAt(i);
                     if (c == '\n') {
                        stringBuffer.append('\r').append('\t');
                     } else {
                        stringBuffer.append(c);
                     }
                  }
               }

               return true;
            }
         } else if (!contextObject.getFlag(19)) {
            if (contextObject.getFlag(24) && target instanceof Object && text != null && text.length() > 0) {
               StringBuffer buf = (StringBuffer)target;
               buf.setLength(0);
               buf.append(text);
               return true;
            }
         } else {
            SyncBuffer syncBuffer = (SyncBuffer)target;
            int fieldId = -1;
            if (text == null) {
               text = "";
            }

            if (contextObject.getFlag(28)) {
               fieldId = 3;
            } else if (contextObject.getFlag(11)) {
               fieldId = 64;
            } else if (contextObject.getFlag(35)) {
               fieldId = 2;
               if (text.length() > 8191) {
                  text = text.substring(0, 8191);
               }
            } else if (contextObject.getFlag(43) || contextObject.getFlag(94)) {
               fieldId = 12;
               if (text.length() > 16384) {
                  text = text.substring(0, 16384);
               }
            } else if (contextObject.getFlag(20)) {
               fieldId = 8;
            }

            if (fieldId != -1) {
               syncBuffer.addField(fieldId, text);
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public Object clone(Object context) {
      return new BodyModelImpl(this.getText());
   }

   @Override
   public boolean isTextOpaque() {
      return false;
   }

   @Override
   public void setText(String text) {
      if (text == null) {
         text = "";
      }

      if (text.length() > 2048) {
         this._textEncoding = PersistentContent.encode(text.substring(0, 2048), true, true);
         this._textEncoding = PersistentContent.encodeAndAppend(text.substring(2048), true, true, this._textEncoding);
      } else {
         this._textEncoding = PersistentContent.encode(text, true, true);
      }
   }

   @Override
   public void paint(ColumnPainter painter, Object context) {
      if (painter.isColumnEmpty(4)) {
         painter.drawText(4, this.getTextPrefix(), false);
      }
   }

   @Override
   public Field getField(Object context) {
      String bodyText = this.getText();
      boolean addressCard = ContextObject.getFlag(context, 11);
      boolean taskApp = ContextObject.getFlag(context, 28);
      boolean editable = ContextObject.getFlag(context, 0);
      boolean memoPadApp = ContextObject.getFlag(context, 8);
      boolean emailApp = ContextObject.getFlag(context, 43) || ContextObject.getFlag(context, 94);
      boolean phone = ContextObject.getFlag(context, 20);
      String label;
      if ((taskApp || addressCard || phone) && editable && !emailApp) {
         label = CommonResources.getString(2004);
      } else {
         label = "";
      }

      Field field;
      if (!editable) {
         field = (Field)(new Object(bodyText));
      } else {
         int length = 1000000;
         if (memoPadApp) {
            length = 8191;
         } else if (emailApp || addressCard) {
            length = 16384;
         }

         if (bodyText.length() > length) {
            bodyText = bodyText.substring(0, length);
            PopupStatus.show(CommonResources.getString(9127), 1500);
         }

         if (ContextObject.getFlag(context, 120)) {
            field = (Field)(new Object(label, bodyText, length, 4503599627374592L));
         } else {
            field = (Field)(new Object(label, bodyText, length, 4503599627374592L));
         }
      }

      if (emailApp) {
         field.setTag(Tag.create("email-message-body"));
      } else {
         field.setTag(Tag.create("message-body"));
      }

      if (!(field instanceof Object)) {
         if (field instanceof Object) {
            BasicEditField edit = (BasicEditField)field;
            edit.setAdjustAlignments(emailApp && !edit.isEditable());
         }
      } else {
         ((TextField)field).setAdjustAlignments(emailApp);
      }

      field.setEditable(editable);
      field.setCookie(this);
      Font gFont = (Font)ContextObject.get(context, 77);
      if (gFont != null) {
         Font cFont = field.getFont();
         int style = cFont.getStyle() & -3 & -7169;
         style |= gFont.getStyle() & 7168;
         cFont = cFont.derive(style);
         field.setFont(cFont);
      }

      return field;
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      String fieldText = null;
      boolean isEditable = field.isEditable();
      if (field instanceof Object && isEditable) {
         AutoTextEditField editField = (AutoTextEditField)field;
         fieldText = editField.getText();
         this.setText(fieldText);
         if (context instanceof Object) {
            int hanStyle = editField.getFont().getStyle() & 7168;
            if (hanStyle != 0) {
               ContextObject.put(context, 77, editField.getFont());
            }
         }
      } else if (field instanceof Object && !isEditable) {
         RichTextField textField = (RichTextField)field;
         fieldText = textField.getText();
         this.setText(fieldText);
      }

      return fieldText != null && fieldText.length() > 0;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public int getOrder(Object context) {
      if (context instanceof Object) {
         ContextObject contextObject = (ContextObject)context;
         if (contextObject.getFlag(11)) {
            if (contextObject.getFlag(0)) {
               return 6100;
            }

            return 7100;
         }

         if (contextObject.getFlag(24)) {
            return 17100;
         }

         if (contextObject.getFlag(28)) {
            return 13500;
         }
      }

      return 5500;
   }

   @Override
   public void setTextEncoding(Object textEncoding) {
      this._textEncoding = textEncoding;
   }

   @Override
   public Object getTextEncoding() {
      return this._textEncoding;
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._textEncoding, compress, encrypt);
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this._textEncoding = PersistentContent.reEncode(this._textEncoding, compress, encrypt);
      return null;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof BodyModelImpl)) {
         return false;
      }

      BodyModelImpl bm = (BodyModelImpl)object;
      String v1 = this.getText();
      String v2 = bm.getText();
      return v1.equals(v2);
   }

   private String getText(int maxChar) {
      try {
         boolean firstBlockOnly = maxChar != Integer.MAX_VALUE;
         String s = (String)PersistentContent.decode(this._textEncoding, firstBlockOnly);
         if (!firstBlockOnly) {
            return s;
         }

         int count = Math.min(s.length(), maxChar);
         StringBuffer buff = (StringBuffer)(new Object(s.substring(0, count)));
         return buff.toString();
      } finally {
         ;
      }
   }

   public BodyModelImpl(Object initialData) {
      String text = null;
      if (!(initialData instanceof Object)) {
         if (initialData != null) {
            ContextObject contextObject = ContextObject.verifyNonNull(initialData);
            text = (String)contextObject.get(-8478555129720928586L);
            if (text == null) {
               text = (String)contextObject.get(253);
            }
         }
      } else {
         text = (String)initialData;
      }

      this.setText(text);
   }
}
