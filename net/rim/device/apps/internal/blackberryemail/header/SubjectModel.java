package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.ActiveAutoTextEditField;
import net.rim.device.api.ui.component.AutoTextEditField;
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
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.messaging.messagelist.MessagePartsProvider;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;
import net.rim.device.apps.internal.blackberryemail.email.ThemeUtilities;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public final class SubjectModel
   implements PersistableRIMModel,
   FieldProvider,
   ColumnPaintProvider,
   ConversionProvider,
   MatchProvider,
   CloneProvider,
   EncryptableProvider {
   private Object _subjectEncoding;
   private static final char WIDE_COLON;
   private static int SUBJECT_MAX_LENGTH = 255;

   public final void setSubject(String subject) {
      this._subjectEncoding = PersistentContent.encode(subject, false, true);
   }

   public final String getSubject() {
      try {
         return PersistentContent.decodeString(this._subjectEncoding);
      } finally {
         ;
      }
   }

   @Override
   public final int match(Object criteria) {
      SearchCriterion crit = (SearchCriterion)criteria;
      switch (crit.getType()) {
         case 2:
         case 21:
            StringMatch matcher = (StringMatch)crit.getValue();
            String subject = this.getSubject();
            if (subject != null && subject.length() > 0) {
               if (matcher.indexOf(subject) >= 0) {
                  return 1;
               }
            } else if (matcher.numStringsInPattern() == 0) {
               return 1;
            }

            return 0;
         default:
            return -1;
      }
   }

   @Override
   public final void paint(ColumnPainter painter, Object context) {
      String subject = this.getSubject();
      if (subject.length() != 0) {
         painter.clear(4);
         painter.drawText(4, subject, false);
      }
   }

   @Override
   public final Field getField(Object context) {
      String subject = this.getSubject();
      if (subject.length() > SUBJECT_MAX_LENGTH) {
         subject = subject.substring(0, SUBJECT_MAX_LENGTH);
      }

      boolean editable = ContextObject.getFlag(context, 0);
      long attributes = 2147483648L | (editable ? 4503599627370496L : 9007199254740992L);
      String label = null;
      if (editable) {
         label = EmailResources.getString(58);
      }

      ActiveAutoTextEditField field = (ActiveAutoTextEditField)(new Object(label, subject, SUBJECT_MAX_LENGTH, attributes));
      field.setAdjustAlignments(true);
      field.setTag(ThemeUtilities.EMAIL_COMPOSE_SUBJECT_TAG);
      field.setCookie(this);
      Font gFont = (Font)ContextObject.get(context, 77);
      if (gFont != null) {
         Font cFont = field.getFont();
         int style = cFont.getStyle() & -3;
         style |= gFont.getStyle() & 7168;
         cFont = cFont.derive(style);
         field.setFont(cFont);
      }

      return field;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      String subject = this.getSubject();
      if (!(target instanceof Object)) {
         if (ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 19)) {
            ((SyncBuffer)target).addField(11, subject);
            return true;
         } else if (target instanceof Object && ContextObject.getFlag(context, 70)) {
            StringBuffer stringBuffer = (StringBuffer)target;
            stringBuffer.append(EmailResources.getString(58));
            stringBuffer.append(subject);
            stringBuffer.append('\n');
            return true;
         } else {
            return false;
         }
      } else {
         RIMMessagingOutgoingMessage outgoingTransmission = (RIMMessagingOutgoingMessage)target;

         try {
            outgoingTransmission.setSubject(subject);
            return true;
         } finally {
            ;
         }
      }
   }

   @Override
   public final Object clone(Object context) {
      ContextObject creationContext = ContextObject.clone(context);
      creationContext.put(-1188330299125235844L, this.getSubject());
      return new SubjectModel(creationContext);
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      if (!(field instanceof Object)) {
         return false;
      }

      AutoTextEditField editField = (AutoTextEditField)field;
      String subject = editField.getText().trim();
      if (subject.length() > SUBJECT_MAX_LENGTH) {
         this.setSubject(subject.substring(0, SUBJECT_MAX_LENGTH));
      } else {
         this.setSubject(subject);
      }

      if (editField.isEditable() && context instanceof Object) {
         int hanStyle = editField.getFont().getStyle() & 7168;
         if (hanStyle != 0) {
            ContextObject.put(context, 77, editField.getFont());
         }
      }

      return true;
   }

   @Override
   public final int getOrder(Object context) {
      return 2600;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._subjectEncoding, false, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._subjectEncoding = PersistentContent.reEncode(this._subjectEncoding, false, encrypt);
      return null;
   }

   public SubjectModel(Object initialData) {
      String subject = null;
      ContextObject contextObject = ContextObject.verifyNonNull(initialData);
      Object originalObject = contextObject.get(245);
      if (originalObject == null) {
         subject = (String)contextObject.get(-1188330299125235844L);
         if (subject == null) {
            subject = (String)contextObject.get(253);
         }
      } else {
         if (originalObject instanceof Object) {
            ReadableList submembers = (ReadableList)originalObject;

            for (int i = submembers.size() - 1; i >= 0; i--) {
               Object o = submembers.getAt(i);
               if (o instanceof SubjectModel) {
                  SubjectModel subjectModel = (SubjectModel)o;
                  subject = subjectModel.getSubject();
                  break;
               }
            }
         }

         boolean addSubjectFWPrefix = contextObject.getFlag(13);
         if (subject == null && originalObject instanceof Object) {
            MessagePartsProvider mpp = (MessagePartsProvider)originalObject;
            subject = mpp.getSubject();
            addSubjectFWPrefix = mpp.allowDescriptiveForwardHeader();
         }

         if (subject == null) {
            subject = "";
         }

         String fwPrefix = EmailResources.getString(59);
         String rePrefix = EmailResources.getString(60);
         int offset = 0;
         if (prefixMatches(subject, fwPrefix)) {
            offset = fwPrefix.length();
         } else if (prefixMatches(subject, rePrefix)) {
            offset = rePrefix.length();
         }

         if (addSubjectFWPrefix) {
            subject = ((StringBuffer)(new Object())).append(fwPrefix).append(' ').append(subject.substring(offset).trim()).toString();
         } else if (contextObject.getFlag(12) || contextObject.getFlag(53) || contextObject.getFlag(29) || contextObject.getFlag(30)) {
            subject = ((StringBuffer)(new Object())).append(rePrefix).append(' ').append(subject.substring(offset).trim()).toString();
         }
      }

      if (subject == null) {
         subject = "";
      }

      this.setSubject(subject);
   }

   @Override
   public final String toString() {
      return this.getSubject();
   }

   @Override
   public final boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof SubjectModel) ? false : ((SubjectModel)object).getSubject().equals(this.getSubject());
      }
   }

   private static final String getAlternatePrefix(String prefix) {
      int prefixLength = prefix.length();
      if (prefixLength == 0) {
         return null;
      }

      char lastChar = prefix.charAt(prefixLength - 1);
      switch (lastChar) {
         case ':':
            return ((StringBuffer)(new Object())).append(prefix.substring(0, prefixLength - 1)).append('：').toString();
         case '：':
            return ((StringBuffer)(new Object())).append(prefix.substring(0, prefixLength - 1)).append(':').toString();
         default:
            return null;
      }
   }

   private static final boolean prefixMatches(String subject, String prefix) {
      if (StringUtilities.startsWithIgnoreCase(subject, prefix)) {
         return true;
      }

      if (StringUtilities.getCharacterSize(prefix) > 1 || StringUtilities.getCharacterSize(subject) > 1) {
         String alternate = getAlternatePrefix(prefix);
         if (alternate != null && StringUtilities.startsWithIgnoreCase(subject, alternate)) {
            return true;
         }
      }

      return false;
   }
}
