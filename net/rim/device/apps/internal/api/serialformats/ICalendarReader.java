package net.rim.device.apps.internal.api.serialformats;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;
import java.util.TimeZone;
import net.rim.device.api.util.Arrays;

public final class ICalendarReader implements ICalendarDefine {
   private ICalendarProvider _iCalendarCurrent;
   private Stack _componentStack;
   private String _version;
   private TokenParser _tokenParser;
   private PropertyParser _propertyParser;
   private static final char VCAL_COUNT_START = '#';
   private static final String VCAL_LAST_DAY = "LD";

   public ICalendarReader(ICalendarProvider iCalendarProvider, InputStream in, String encoding) {
      this._tokenParser = new TokenParser(in, encoding);
      this._propertyParser = new PropertyParser();
      this._componentStack = (Stack)(new Object());
      this._iCalendarCurrent = iCalendarProvider;
   }

   private final boolean isVersion_1_0() {
      return "VCALENDAR/1.0".equals(this._version) || "1.0".equals(this._version);
   }

   private final boolean isVersion_2_0() {
      return "VCALENDAR/2.0".equals(this._version) || "2.0".equals(this._version);
   }

   private final boolean isVersionSpecified() {
      return this._version != null && !this._version.equals("");
   }

   private static final int getWeekDay(String day) {
      if (day.equals("SU")) {
         return 1;
      } else if (day.equals("MO")) {
         return 2;
      } else if (day.equals("TU")) {
         return 3;
      } else if (day.equals("WE")) {
         return 4;
      } else if (day.equals("TH")) {
         return 5;
      } else if (day.equals("FR")) {
         return 6;
      } else if (day.equals("SA")) {
         return 7;
      } else if (day.equals("SU")) {
         return 1;
      } else {
         throw new InvalidFormatException();
      }
   }

   public final ICalendarProvider parseIt() {
      int currentProperty = 0;
      currentProperty = this.nextProperty();
      if (currentProperty == 63078537 && this._tokenParser.getCommonOneLine(0).hashCode() == -217959020) {
         this._componentStack.addElement(new ICalendarReader$CalendarComponent(-217959020));
         this._iCalendarCurrent.setVCalendarBeginTag(-217959020);

         while (true) {
            currentProperty = this.nextProperty();
            ICalendarReader$CalendarComponent currentComponent = (ICalendarReader$CalendarComponent)this._componentStack.peek();
            if (currentProperty == 63078537) {
               int tag = this._tokenParser.getCommonOneLine(0).hashCode();
               if (currentComponent.contains(tag) && tag == -1766506524) {
                  while (!this._tokenParser.isLeadingString("END:VEVENT")) {
                     this._tokenParser.getCommonOneLine(0);
                  }

                  this._tokenParser.getCommonOneLine(0);
               } else {
                  if (!this.isValidSubcomponent(tag, currentComponent)) {
                     throw new InvalidFormatException();
                  }

                  this._componentStack.addElement(new ICalendarReader$CalendarComponent(tag));
                  currentComponent.add(tag);
                  if (tag == -1770502245) {
                     this._iCalendarCurrent.setAlarmNestedBeginTag(-1770502245);
                  } else {
                     this._iCalendarCurrent.setCalendarComponent(tag);
                  }
               }
            } else if (currentProperty == 68795) {
               this._componentStack.pop();
               if (this._tokenParser.readIfLeadingString("VCALENDAR")) {
                  if (this._componentStack.isEmpty() && this.hasRequiredProperties(currentComponent)) {
                     return this._iCalendarCurrent;
                  }

                  throw new InvalidFormatException();
               }

               int tag = this._tokenParser.getCommonOneLine(0).hashCode();
               if (tag != currentComponent.getTag() || !this.hasRequiredProperties(currentComponent)) {
                  throw new InvalidFormatException();
               }
            } else {
               if (currentComponent.contains(currentProperty) && !this.mayOccurMoreThanOnce(currentProperty, currentComponent.getTag())) {
                  throw new InvalidFormatException();
               }

               switch (currentComponent.getTag()) {
                  case -2115861937:
                     this.parseVTIMEZONEProperty(currentProperty);
                     break;
                  case -1770502245:
                     this.parseVALARMProperty(currentProperty);
                     break;
                  case -1766506524:
                     this.parseVEVENTProperty(currentProperty);
                     break;
                  case -1577546565:
                     this.parseVFREEBUSYProperty(currentProperty);
                     break;
                  case -1569357062:
                  case 2095255229:
                     this.parseDAYLIGHTorSTANDARDProperty(currentProperty);
                     break;
                  case -1143648767:
                     this.parseVJOURNALProperty(currentProperty);
                     break;
                  case -217959020:
                     this.parseVCALENDARProperty(currentProperty);
                     break;
                  case 82003356:
                     this.parseVTODOProperty(currentProperty);
               }

               currentComponent.add(currentProperty);
            }
         }
      } else {
         throw new InvalidFormatException();
      }
   }

   private final void parseVCALENDARProperty(int property) {
      if (property == -1926485326) {
         this._iCalendarCurrent.setProductId(this.buildStringProperty());
      } else if (property == 1069590712) {
         this._version = this.buildVersionProperty();
         this._iCalendarCurrent.setVersion(this._version);
      } else if (property != 616901180 || !this.isVersion_2_0() && this.isVersionSpecified()) {
         if ((!this.isVersion_1_0() || Arrays.getIndex(ICalendarDefine.VALID_PROPERTIES_IN_VCAL_VCALENDAR, property) < 0)
            && (!this.isVersion_2_0() || Arrays.getIndex(ICalendarDefine.VALID_PROPERTIES_IN_ICAL_VCALENDAR, property) < 0)
            && (
               this.isVersionSpecified()
                  || Arrays.getIndex(ICalendarDefine.VALID_PROPERTIES_IN_VCAL_VCALENDAR, property) < 0
                     && Arrays.getIndex(ICalendarDefine.VALID_PROPERTIES_IN_ICAL_VCALENDAR, property) < 0
            )) {
            throw new InvalidFormatException();
         }

         this.handleUnsupportedTags();
      } else {
         this._iCalendarCurrent.setCalendarScale(this.buildTextProperty());
      }
   }

   private final void parseVEVENTProperty(int property) {
      ICalendarReader$CalendarComponent currentComponent = (ICalendarReader$CalendarComponent)this._componentStack.peek();
      if (property == 84016) {
         this._iCalendarCurrent.setUID(this.buildStringProperty());
      } else if (property == 428414940) {
         this.buildDESCRIPTION();
      } else if (property == -1139657850) {
         this.buildSUMMARY();
      } else if (property == -1611296843) {
         this.buildLOCATION();
      } else if (property == -382834268) {
         this._iCalendarCurrent.setPriority(this.buildPriorityProperty());
      } else if (property == -1590190670 || property == 65370667) {
         this.buildDateTimeProperty(property);
      } else if (property == 2058772193) {
         if (!currentComponent.contains(2058772193)) {
            this.buildDateTimeProperty(2058772193);
         } else {
            this.handleUnsupportedTags();
         }
      } else if (property == 78255694) {
         if (!currentComponent.contains(78255694)) {
            this.buildRecurrenceProperty();
         } else {
            this.handleUnsupportedTags();
         }
      } else if (property == 2009140333 && this.isVersion_1_0()) {
         this.buildDALARM();
      } else {
         if ((!this.isVersion_1_0() || Arrays.getIndex(ICalendarDefine.VALID_PROPERTIES_IN_VCAL_VEVENT, property) < 0)
            && (!this.isVersion_2_0() || Arrays.getIndex(ICalendarDefine.VALID_PROPERTIES_IN_ICAL_VEVENT, property) < 0)) {
            throw new InvalidFormatException();
         }

         this.handleUnsupportedTags();
      }
   }

   private final void parseVTODOProperty(int property) {
      ICalendarReader$CalendarComponent currentComponent = (ICalendarReader$CalendarComponent)this._componentStack.peek();
      if (property == 84016) {
         this._iCalendarCurrent.setUID(this.buildStringProperty());
      } else if (property == 428414940) {
         this.buildDESCRIPTION();
      } else if (property == -1139657850) {
         this.buildSUMMARY();
      } else if (property == -1611296843) {
         this.buildLOCATION();
      } else if (property == -382834268) {
         this._iCalendarCurrent.setPriority(this.buildPriorityProperty());
      } else if (property == -1590190670 || property == 68052) {
         this.buildDateTimeProperty(property);
      } else if (property == 1383663147) {
         this.buildCOMPLETED();
      } else if (property == 2058772193) {
         if (!currentComponent.contains(2058772193)) {
            this.buildDateTimeProperty(2058772193);
         } else {
            this.handleUnsupportedTags();
         }
      } else if (property == 78255694) {
         if (!currentComponent.contains(78255694)) {
            this.buildRecurrenceProperty();
         } else {
            this.handleUnsupportedTags();
         }
      } else if (property == 2009140333 && this.isVersion_1_0()) {
         this.buildDALARM();
      } else {
         if ((!this.isVersion_1_0() || Arrays.getIndex(ICalendarDefine.VALID_PROPERTIES_IN_VCAL_VTODO, property) < 0)
            && (!this.isVersion_2_0() || Arrays.getIndex(ICalendarDefine.VALID_PROPERTIES_IN_ICAL_VTODO, property) < 0)) {
            throw new InvalidFormatException();
         }

         this.handleUnsupportedTags();
      }
   }

   private final void parseVJOURNALProperty(int property) {
      if (Arrays.getIndex(ICalendarDefine.VALID_PROPERTIES_IN_VJOURNAL, property) >= 0) {
         this.handleUnsupportedTags();
      } else {
         throw new InvalidFormatException();
      }
   }

   private final void parseVFREEBUSYProperty(int property) {
      if (Arrays.getIndex(ICalendarDefine.VALID_PROPERTIES_IN_VFREEBUSY, property) >= 0) {
         this.handleUnsupportedTags();
      } else {
         throw new InvalidFormatException();
      }
   }

   private final void parseVTIMEZONEProperty(int property) {
      if (Arrays.getIndex(ICalendarDefine.VALID_PROPERTIES_IN_VTIMEZONE, property) >= 0) {
         this.handleUnsupportedTags();
      } else {
         throw new InvalidFormatException();
      }
   }

   private final void parseDAYLIGHTorSTANDARDProperty(int property) {
      if (Arrays.getIndex(ICalendarDefine.VALID_PROPERTIES_IN_DAYLIGHT_OR_STANDARD, property) >= 0) {
         this.handleUnsupportedTags();
      } else {
         throw new InvalidFormatException();
      }
   }

   private final void parseVALARMProperty(int property) {
      if (property == 1925345846) {
         this._iCalendarCurrent.setAction(this.buildActionProperty());
      } else if (property == -341909096) {
         this.buildTriggerProperty();
      } else if (Arrays.getIndex(ICalendarDefine.VALID_PROPERTIES_IN_VALARM, property) >= 0) {
         this.handleUnsupportedTags();
      } else {
         throw new InvalidFormatException();
      }
   }

   private final boolean isValidSubcomponent(int tag, ICalendarReader$CalendarComponent component) {
      switch (component.getTag()) {
         case -2115861937:
            if (tag != -1569357062 && tag != 2095255229) {
               return false;
            }

            return true;
         case -1766506524:
         case 82003356:
            if (this.isVersion_2_0() && tag == -1770502245) {
               return true;
            }

            return false;
         case -217959020:
            if (this.isVersion_1_0()) {
               if (tag != -1766506524 && tag != 82003356) {
                  return false;
               }

               return true;
            } else if (this.isVersion_2_0()) {
               if (tag != -1766506524 && tag != 82003356 && tag != -1143648767 && tag != -1577546565 && tag != -2115861937) {
                  return false;
               }

               return true;
            }
         default:
            return false;
      }
   }

   private final boolean hasRequiredProperties(ICalendarReader$CalendarComponent component) {
      switch (component.getTag()) {
         case -2115861937:
            if (!component.contains(2591265) || !component.contains(-1569357062) && !component.contains(2095255229)) {
               return false;
            }

            return true;
         case -1770502245:
            switch (this._iCalendarCurrent.getAction()) {
               case -1905220446:
                  if (!component.contains(1925345846)
                     || !component.contains(-341909096)
                     || (!component.contains(-1209385580) || !component.contains(-1881202277))
                        && (component.contains(-1209385580) || component.contains(-1881202277))) {
                     return false;
                  }

                  return true;
               case 62628790:
                  if (!component.contains(1925345846)
                     || !component.contains(-341909096)
                     || (!component.contains(-1209385580) || !component.contains(-1881202277))
                        && (component.contains(-1209385580) || component.contains(-1881202277))) {
                     return false;
                  }

                  return true;
               case 66081660:
                  if (!component.contains(1925345846)
                     || !component.contains(428414940)
                     || !component.contains(-1139657850)
                     || !component.contains(-341909096)
                     || (!component.contains(-1209385580) || !component.contains(-1881202277))
                        && (component.contains(-1209385580) || component.contains(-1881202277))) {
                     return false;
                  }

                  return true;
               case 1691390643:
                  if (!component.contains(1925345846)
                     || !component.contains(1941037637)
                     || !component.contains(-341909096)
                     || (!component.contains(-1209385580) || !component.contains(-1881202277))
                        && (component.contains(-1209385580) || component.contains(-1881202277))) {
                     return false;
                  }

                  return true;
               default:
                  return false;
            }
         case -1766506524:
            if (this.isVersion_1_0()) {
               return true;
            }

            if (this.isVersion_2_0()) {
               if (component.contains(65370667) && component.contains(-1209385580)) {
                  return false;
               }

               return true;
            }
            break;
         case -1577546565:
         case -1143648767:
            return true;
         case -1569357062:
         case 2095255229:
            if (component.contains(-1590190670) && component.contains(1237233364) && component.contains(-725089853)) {
               return true;
            }

            return false;
         case -217959020:
            if (this.isVersion_1_0()) {
               return component.contains(1069590712);
            }

            if (this.isVersion_2_0()) {
               if (component.contains(1069590712) && component.contains(-1926485326)) {
                  return true;
               }

               return false;
            }
            break;
         case 82003356:
            if (this.isVersion_1_0()) {
               return true;
            }

            if (this.isVersion_2_0()) {
               if (component.contains(68052) && component.contains(-1209385580)) {
                  return false;
               }

               return true;
            }
      }

      return false;
   }

   private final boolean mayOccurMoreThanOnce(int property, int componentTag) {
      switch (componentTag) {
         case -2115861937:
            if (Arrays.getIndex(ICalendarDefine.MAY_OCCUR_MORE_THAN_ONCE_IN_VTIMEZONE, property) >= 0) {
               return true;
            }

            return false;
         case -1770502245:
            if (Arrays.getIndex(ICalendarDefine.MAY_OCCUR_MORE_THAN_ONCE_IN_VALARM, property) >= 0) {
               return true;
            }

            return false;
         case -1577546565:
            if (Arrays.getIndex(ICalendarDefine.MAY_OCCUR_MORE_THAN_ONCE_IN_VFREEBUSY, property) >= 0) {
               return true;
            }

            return false;
         case -1569357062:
         case 2095255229:
            if (Arrays.getIndex(ICalendarDefine.MAY_OCCUR_MORE_THAN_ONCE_IN_DAYLIGHT_OR_STANDARD, property) >= 0) {
               return true;
            }

            return false;
         case -217959020:
            if (this.isVersion_1_0()) {
               if (Arrays.getIndex(ICalendarDefine.MAY_OCCUR_MORE_THAN_ONCE_IN_VCAL_VCALENDAR, property) >= 0) {
                  return true;
               }

               return false;
            } else if (this.isVersion_2_0()) {
               if (Arrays.getIndex(ICalendarDefine.MAY_OCCUR_MORE_THAN_ONCE_IN_ICAL_VCALENDAR, property) >= 0) {
                  return true;
               }

               return false;
            }
         case -1766506524:
            if (this.isVersion_1_0()) {
               if (Arrays.getIndex(ICalendarDefine.MAY_OCCUR_MORE_THAN_ONCE_IN_VCAL_VEVENT, property) >= 0) {
                  return true;
               }

               return false;
            } else if (this.isVersion_2_0()) {
               if (Arrays.getIndex(ICalendarDefine.MAY_OCCUR_MORE_THAN_ONCE_IN_ICAL_VEVENT, property) >= 0) {
                  return true;
               }

               return false;
            }
         default:
            return false;
         case 82003356:
            if (this.isVersion_1_0()) {
               if (Arrays.getIndex(ICalendarDefine.MAY_OCCUR_MORE_THAN_ONCE_IN_VCAL_VTODO, property) >= 0) {
                  return true;
               }

               return false;
            } else if (this.isVersion_2_0()) {
               if (Arrays.getIndex(ICalendarDefine.MAY_OCCUR_MORE_THAN_ONCE_IN_ICAL_VTODO, property) >= 0) {
                  return true;
               }

               return false;
            }
         case -1143648767:
            return Arrays.getIndex(ICalendarDefine.MAY_OCCUR_MORE_THAN_ONCE_IN_VJOURNAL, property) >= 0;
      }
   }

   private final int nextProperty() {
      String propertyName = this._tokenParser.nextProperty();
      int id;
      if (propertyName.startsWith("X-")) {
         id = 2773;
      } else {
         id = propertyName.hashCode();
      }

      switch (id) {
         case 68795:
         case 63078537:
            if (this._tokenParser.getBreakingCharacter() != 58) {
               throw new InvalidFormatException();
            }
         default:
            return id;
      }
   }

   private final String buildVersionProperty() {
      String version = this.buildStringProperty();
      if (!version.equals("1.0") && !version.equals("2.0")) {
         throw new InvalidFormatException();
      } else {
         return version;
      }
   }

   private final String buildStringProperty() {
      if (this._tokenParser.getBreakingCharacter() != 58) {
         throw new InvalidFormatException();
      } else {
         return this._tokenParser.getCommonOneLine(0);
      }
   }

   private final void buildDESCRIPTION() {
      if (this._tokenParser.getBreakingCharacter() == 58) {
         if (this._iCalendarCurrent.getAlarmNestedBeginTag() == -1770502245) {
            this._iCalendarCurrent.setAlarmDescription(0, null, this.buildStringProperty());
         } else {
            this._iCalendarCurrent.setDescription(0, null, this.buildStringProperty());
         }
      } else {
         this._tokenParser.setEndOfTokenCharacters(":,;=");
         this._tokenParser.ignoreWhitespaceTokens(true);
         String name = null;
         String value = null;
         int encodingType = 0;

         while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            String paraName = this._propertyParser.getParameterName();
            String paraValue = this._propertyParser.getParameterValue();
            if (paraName == null) {
               throw new InvalidFormatException();
            }

            if (paraName.equals("ENCODING")) {
               encodingType = paraValue.hashCode();
            }
         }

         this._tokenParser.clearEndOfTokenCharacters(";:,=");
         int intTempName = 0;
         if (name != null) {
            intTempName = name.hashCode();
         }

         String tempString = null;
         if (encodingType == -151191742) {
            tempString = (String)(new Object(this._tokenParser.getQuotedPrintableContent(0)));
         } else if (encodingType != 1952093519 && encodingType != 66) {
            tempString = this._tokenParser.getCommonOneLine(0);
         } else {
            tempString = (String)(new Object(this._tokenParser.getBase64Content(0)));
         }

         if (this._iCalendarCurrent.getAlarmNestedBeginTag() == -1770502245) {
            this._iCalendarCurrent.setAlarmDescription(intTempName, value, tempString);
         } else {
            this._iCalendarCurrent.setDescription(intTempName, value, tempString);
         }
      }
   }

   private final void buildSUMMARY() {
      if (this._tokenParser.getBreakingCharacter() == 58) {
         String tempString = this.buildStringProperty();
         this._iCalendarCurrent.setSummary(0, null, tempString);
      } else {
         this._tokenParser.setEndOfTokenCharacters(":,;=");
         this._tokenParser.ignoreWhitespaceTokens(true);
         String name = null;
         String value = null;
         int encodingType = 0;

         while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            String paraName = this._propertyParser.getParameterName();
            String paraValue = this._propertyParser.getParameterValue();
            if (paraName == null) {
               throw new InvalidFormatException();
            }

            if (paraName.equals("ENCODING")) {
               encodingType = paraValue.hashCode();
            }
         }

         this._tokenParser.clearEndOfTokenCharacters(";:,=");
         int intTempName = 0;
         if (name != null) {
            intTempName = name.hashCode();
         }

         String tempString = null;
         if (encodingType == -151191742) {
            tempString = (String)(new Object(this._tokenParser.getQuotedPrintableContent(0)));
         } else if (encodingType != 1952093519 && encodingType != 66) {
            tempString = this._tokenParser.getCommonOneLine(0);
         } else {
            tempString = (String)(new Object(this._tokenParser.getBase64Content(0)));
         }

         this._iCalendarCurrent.setSummary(intTempName, value, tempString);
      }
   }

   private final void buildLOCATION() {
      if (this._tokenParser.getBreakingCharacter() == 58) {
         String tempString = this.buildStringProperty();
         this._iCalendarCurrent.setLocation(0, null, tempString);
      } else {
         this._tokenParser.setEndOfTokenCharacters(":,;=");
         this._tokenParser.ignoreWhitespaceTokens(true);
         String name = null;
         String value = null;
         int encodingType = 0;

         while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            String paraName = this._propertyParser.getParameterName();
            String paraValue = this._propertyParser.getParameterValue();
            if (paraName == null) {
               throw new InvalidFormatException();
            }

            if (paraName.equals("ENCODING")) {
               encodingType = paraValue.hashCode();
            }
         }

         this._tokenParser.clearEndOfTokenCharacters(";:,=");
         int intTempName = 0;
         if (name != null) {
            intTempName = name.hashCode();
         }

         String tempString = null;
         if (encodingType == -151191742) {
            tempString = (String)(new Object(this._tokenParser.getQuotedPrintableContent(0)));
         } else if (encodingType != 1952093519 && encodingType != 66) {
            tempString = this._tokenParser.getCommonOneLine(0);
         } else {
            tempString = (String)(new Object(this._tokenParser.getBase64Content(0)));
         }

         this._iCalendarCurrent.setLocation(intTempName, value, tempString);
      }
   }

   private final int buildPriorityProperty() {
      String str = this.buildStringProperty();
      return str != null && str.length() != 0 ? Integer.parseInt(str) : 0;
   }

   private final void buildCOMPLETED() {
      if (this._tokenParser.getBreakingCharacter() != 58) {
         throw new InvalidFormatException();
      }

      Date dateString = this.getDateTimeFromParser();
      this._iCalendarCurrent.setDateTimeCompleted(dateString);
   }

   private final int buildActionProperty() {
      String str = this.buildStringProperty();
      if (str != null && str.length() != 0) {
         return str.hashCode();
      } else {
         throw new InvalidFormatException();
      }
   }

   private final void buildDALARM() {
      String str = this._tokenParser.getCommonOneLine(0);
      int index = str.indexOf(59);
      if (index < 0) {
         this._iCalendarCurrent.setTrigger(0, 0, this.getDateTime(str));
      } else {
         Date triggerDate = this.getDateTime(str.substring(0, index));

         for (int semicoloncount = 1; index > 0; semicoloncount++) {
            if (semicoloncount > 3) {
               throw new InvalidFormatException();
            }

            index = str.indexOf(59, index + 1);
         }

         this._iCalendarCurrent.setTrigger(0, 0, triggerDate);
      }
   }

   private final void buildTriggerProperty() {
      String paraName = null;
      String paraValue = null;
      int intParaName = 0;
      int intParaValue = 0;
      int negate = -1;
      if (this._tokenParser.getBreakingCharacter() != 58) {
         this._tokenParser.setEndOfTokenCharacters(";:,=");
         this._tokenParser.ignoreWhitespaceTokens(true);
         if (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            if (!this.isVersion_2_0()) {
               throw new InvalidFormatException();
            }

            paraName = this._propertyParser.getParameterName();
            paraValue = this._propertyParser.getParameterValue();
            if (paraName == null || paraValue == null) {
               throw new InvalidFormatException();
            }

            if (paraName.equals("VALUE")) {
               if (!paraValue.equals("DATE-TIME") && !paraValue.equals("DATE") && !paraValue.equals("DURATION")) {
                  throw new InvalidFormatException();
               }
            } else {
               if (!paraName.equals("RELATED")) {
                  throw new InvalidFormatException();
               }

               if (!paraValue.equals("END")) {
                  throw new InvalidFormatException();
               }

               negate = 1;
            }

            intParaName = paraName.hashCode();
            intParaValue = paraValue.hashCode();
         }
      }

      this._tokenParser.clearEndOfTokenCharacters(";:,=");
      if (paraValue != null && !paraValue.equals("DURATION") && !paraValue.equals("END")) {
         if (paraValue.equals("DATE-TIME") || paraValue.equals("DATE")) {
            Date dateTimeValue = this.getDateTimeFromParser();
            this._iCalendarCurrent.setTrigger(intParaName, intParaValue, dateTimeValue);
         }
      } else {
         int offset = negate * this.getDuration();
         this._iCalendarCurrent.setRelativeTrigger(intParaName, intParaValue, offset);
      }
   }

   private final void buildRecurrenceProperty() {
      if (this.isVersion_1_0()) {
         this.buildvCalRecurrenceProperty();
      } else if (this.isVersion_2_0()) {
         this.buildiCalRecurrenceProperty();
      } else {
         throw new InvalidFormatException();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void buildvCalRecurrenceProperty() {
      String str = this._tokenParser.getCommonOneLine(0);
      int index = 0;
      int type = 0;
      byte var91;
      if (str.startsWith("D")) {
         var91 = 60;
         this._iCalendarCurrent.setFreq(64808441);
      } else if (str.startsWith("W")) {
         var91 = 61;
         this._iCalendarCurrent.setFreq(-1738378111);
      } else if (str.startsWith("MP")) {
         var91 = 62;
         this._iCalendarCurrent.setFreq(1954618349);
         index++;
      } else if (str.startsWith("MD")) {
         var91 = 63;
         this._iCalendarCurrent.setFreq(1954618349);
         index++;
      } else if (str.startsWith("YM")) {
         var91 = 64;
         this._iCalendarCurrent.setFreq(-1681232246);
         index++;
      } else {
         if (!str.startsWith("YD")) {
            throw new InvalidFormatException();
         }

         var91 = 65;
         this._iCalendarCurrent.setFreq(-1681232246);
         index++;
      }

      index++;
      boolean var76 = false /* VF: Semaphore variable */;

      try {
         var76 = true;
         String tok = this.getNextToken(str, index);
         this._iCalendarCurrent.setInterval(Integer.parseInt(tok));
         index += tok.length() + 1;
         var76 = false;
      } finally {
         if (var76) {
            throw new InvalidFormatException();
         }
      }

      String tok = null;
      switch (var91) {
         case 60:
            break;
         case 61:
         default:
            for (String var97 = this.getNextToken(str, index); var97 != null; var97 = this.getNextToken(str, index)) {
               try {
                  int day = getWeekDay(var97);
                  this._iCalendarCurrent.setByDay(0, day);
                  index += var97.length() + 1;
               } finally {
                  break;
               }
            }
            break;
         case 62:
            tok = this.getNextToken(str, index);
            int[] pos = new int[10];
            int posIndex = 0;
            boolean dayFollowsPos = true;

            while (tok != null) {
               if (this.isPos(tok)) {
                  if (dayFollowsPos) {
                     posIndex = 0;
                  }

                  pos[posIndex++] = this.getPos(tok);
                  dayFollowsPos = false;
               } else {
                  if (posIndex == 0) {
                     break;
                  }

                  try {
                     int day = getWeekDay(tok);

                     for (int i = 0; i < posIndex; i++) {
                        this._iCalendarCurrent.setByDay(pos[i], day);
                     }

                     dayFollowsPos = true;
                  } finally {
                     break;
                  }
               }

               index += tok.length() + 1;
               tok = this.getNextToken(str, index);
            }

            if (!dayFollowsPos) {
               throw new InvalidFormatException();
            }
            break;
         case 63:
            for (String var95 = this.getNextToken(str, index); var95 != null; var95 = this.getNextToken(str, index)) {
               try {
                  int day = this.getDayInMonth(var95);
                  this._iCalendarCurrent.setByMonthDay(day);
                  index += var95.length() + 1;
               } finally {
                  break;
               }
            }
            break;
         case 64:
            for (String var94 = this.getNextToken(str, index); var94 != null; var94 = this.getNextToken(str, index)) {
               try {
                  int month = Integer.parseInt(var94);
                  this._iCalendarCurrent.setByMonth(month);
                  index += var94.length() + 1;
               } finally {
                  break;
               }
            }
            break;
         case 65:
            for (String var93 = this.getNextToken(str, index); var93 != null; var93 = this.getNextToken(str, index)) {
               try {
                  int yearDay = Integer.parseInt(var93);
                  this._iCalendarCurrent.setByYearDay(yearDay);
                  index += var93.length() + 1;
               } finally {
                  break;
               }
            }
      }

      tok = this.getNextToken(str, index);
      if (tok != null) {
         if (tok.charAt(0) == '#') {
            boolean var27 = false /* VF: Semaphore variable */;

            try {
               var27 = true;
               int var105 = Integer.parseInt(tok.substring(1));
               this._iCalendarCurrent.setCount(var105);
               if (var105 == 0) {
                  this._iCalendarCurrent.setUntil(0, null);
                  var27 = false;
               } else {
                  var27 = false;
               }
            } finally {
               if (var27) {
                  throw new InvalidFormatException();
               }
            }

            index += tok.length() + 1;
            tok = this.getNextToken(str, index);
            if (tok != null) {
               Date endDate = this.getDateTime(tok);
               this._iCalendarCurrent.setUntil(-1773854324, endDate);
               index += tok.length() + 1;
               if (this.getNextToken(str, index) != null) {
                  throw new InvalidFormatException();
               }
            }
         } else {
            Date endDate = this.getDateTime(tok);
            this._iCalendarCurrent.setUntil(-1773854324, endDate);
            index += tok.length() + 1;
            tok = this.getNextToken(str, index);
            if (tok != null) {
               if (tok.charAt(0) != '#') {
                  throw new InvalidFormatException();
               }

               boolean var18 = false /* VF: Semaphore variable */;

               try {
                  var18 = true;
                  int var108 = Integer.parseInt(tok.substring(1));
                  this._iCalendarCurrent.setCount(var108);
                  var18 = false;
               } finally {
                  if (var18) {
                     throw new InvalidFormatException();
                  }
               }

               index += tok.length() + 1;
               if (this.getNextToken(str, index) != null) {
                  throw new InvalidFormatException();
               }
            }
         }
      }
   }

   private final boolean isPos(String str) {
      char lastChar = str.charAt(str.length() - 1);
      return lastChar == '+' || lastChar == '-';
   }

   private final int getPos(String str) {
      int lastIndex = str.length() - 1;
      int pos = Integer.parseInt(str.substring(0, lastIndex));
      if (str.charAt(lastIndex) == '-') {
         pos = -pos;
      }

      return pos;
   }

   private final int getDayInMonth(String str) {
      if (str.equals("LD")) {
         return -1;
      } else {
         int strlen = str.length();
         char lastChar = str.charAt(strlen - 1);
         if (lastChar == '-') {
            return -Integer.parseInt(str.substring(0, strlen - 1));
         } else {
            return lastChar == 43 ? Integer.parseInt(str.substring(0, strlen - 1)) : Integer.parseInt(str);
         }
      }
   }

   private final String getNextToken(String str, int startIndex) {
      if (startIndex >= str.length()) {
         return null;
      }

      int nextIndex = str.indexOf(32, startIndex);
      return nextIndex < 0 ? str.substring(startIndex) : str.substring(startIndex, nextIndex);
   }

   private final void buildiCalRecurrenceProperty() {
      this._tokenParser.setEndOfTokenCharacters(";:,=\"");
      this._tokenParser.ignoreWhitespaceTokens(true);
      String name = null;
      String value = null;
      boolean isFreqSpecified = false;

      while (this._propertyParser.getNextParameterAndValue(this._tokenParser, true)) {
         name = this._propertyParser.getParameterName();
         value = this._propertyParser.getParameterValue();
         int recurPropertyTagName = name.hashCode();
         switch (recurPropertyTagName) {
            case -1571028365:
               int byMonthDayValue = Integer.parseInt(value);
               this._iCalendarCurrent.setByMonthDay(byMonthDayValue);
               break;
            case -1280916181:
               int byMinuteValue = Integer.parseInt(value);
               this._iCalendarCurrent.setByMinute(byMinuteValue);
               break;
            case -1113169013:
               int bySecondValue = Integer.parseInt(value);
               this._iCalendarCurrent.setBySecond(bySecondValue);
               break;
            case -1112661559:
               int bySetPosValue = Integer.parseInt(value);
               this._iCalendarCurrent.setBySetPos(bySetPosValue);
               break;
            case -998596660:
               int byWeekNoValue = Integer.parseInt(value);
               this._iCalendarCurrent.setByWeekNo(byWeekNoValue);
               break;
            case 2166392:
               int freqValue = value.hashCode();
               this._iCalendarCurrent.setFreq(freqValue);
               isFreqSpecified = true;
               break;
            case 2666549:
               int weekStartValue = getWeekDay(value);
               this._iCalendarCurrent.setWeekStart(weekStartValue);
               break;
            case 63671237:
               if (value.length() != 2) {
                  String weekDay = value.substring(value.length() - 2, value.length());
                  int weekDayValue = getWeekDay(weekDay);
                  boolean isPlusPresent = value.startsWith("+");
                  if (isPlusPresent) {
                     String orderWeek = value.substring(1, value.length() - 2);
                     int orderWeekValue = Integer.parseInt(orderWeek);
                     this._iCalendarCurrent.setByDay(orderWeekValue, weekDayValue);
                  } else {
                     String orderWeek = value.substring(0, value.length() - 2);
                     int orderWeekValue = Integer.parseInt(orderWeek);
                     this._iCalendarCurrent.setByDay(orderWeekValue, weekDayValue);
                  }
               } else {
                  int weekDayValue = getWeekDay(value);
                  this._iCalendarCurrent.setByDay(0, weekDayValue);
               }
               break;
            case 64313583:
               int countValue = Integer.parseInt(value);
               this._iCalendarCurrent.setCount(countValue);
               break;
            case 80906046:
               boolean isDate = value.indexOf("T") < 0;
               Date untilDate = isDate ? this.getDate(value) : this.getDateTime(value);
               this._iCalendarCurrent.setUntil(2090926, untilDate);
               break;
            case 879786472:
               int byYearDayValue = Integer.parseInt(value);
               this._iCalendarCurrent.setByYearDay(byYearDayValue);
               break;
            case 1067237481:
               int byMonthValue = Integer.parseInt(value);
               this._iCalendarCurrent.setByMonth(byMonthValue);
               break;
            case 1353045189:
               int intervalValue = Integer.parseInt(value);
               this._iCalendarCurrent.setInterval(intervalValue);
               break;
            case 1973940923:
               int byHourValue = Integer.parseInt(value);
               this._iCalendarCurrent.setByHour(byHourValue);
               break;
            default:
               throw new InvalidFormatException();
         }
      }

      this._tokenParser.clearEndOfTokenCharacters(";:,=");
      if (!isFreqSpecified) {
         throw new InvalidFormatException();
      }
   }

   private final void buildDateTimeProperty(int currentProperty) {
      int intParaName = 0;
      int intParaValue = 0;
      Date dateTimeValue = null;
      if (this._tokenParser.getBreakingCharacter() != 58) {
         this._tokenParser.setEndOfTokenCharacters(";:,=");
         this._tokenParser.ignoreWhitespaceTokens(true);
         String paraName = null;
         String paraValue = null;

         while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            if (!this.isVersion_2_0()) {
               throw new InvalidFormatException();
            }

            paraName = this._propertyParser.getParameterName();
            paraValue = this._propertyParser.getParameterValue();
            if (paraValue == null || paraValue == null) {
               throw new InvalidFormatException();
            }

            if (paraName.equals("VALUE")) {
               if (!paraValue.equals("DATE-TIME") && !paraValue.equals("DATE")) {
                  throw new InvalidFormatException();
               }

               intParaName = paraName.hashCode();
               intParaValue = paraValue.hashCode();
            } else if (!paraName.equals("TZID")) {
               throw new InvalidFormatException();
            }
         }
      }

      this._tokenParser.clearEndOfTokenCharacters(";:,=");
      if (currentProperty != 2058772193) {
         dateTimeValue = this.getDateTimeFromParser();
         switch (currentProperty) {
            case -1590190670:
               this._iCalendarCurrent.setDateTimeStart(intParaName, intParaValue, dateTimeValue);
               return;
            case -341909096:
               this._iCalendarCurrent.setTrigger(intParaName, intParaValue, dateTimeValue);
               return;
            case 68052:
               this._iCalendarCurrent.setDateTimeDue(intParaName, intParaValue, dateTimeValue);
               return;
            case 65370667:
               this._iCalendarCurrent.setDateTimeEnd(intParaName, intParaValue, dateTimeValue);
               return;
            case 2058772193:
               break;
            default:
               throw new InvalidFormatException();
         }
      } else {
         if (this._tokenParser.getBreakingCharacter() != 58) {
            throw new InvalidFormatException();
         }

         while (true) {
            dateTimeValue = this.getDateTimeFromParser();
            if (dateTimeValue != null) {
               this._iCalendarCurrent.setExceptionDateTime(intParaName, intParaValue, dateTimeValue);
               if (this._tokenParser.isLineBreak()) {
                  break;
               }
            }
         }
      }
   }

   private final String buildTextProperty() {
      this.getPropertyBasicTextParameter();
      return this._tokenParser.getCommonOneLine(0);
   }

   private final boolean getPropertyBasicTextParameter() {
      if (this._tokenParser.getBreakingCharacter() != 58) {
         this._tokenParser.setEndOfTokenCharacters(";:,=");
         this._tokenParser.ignoreWhitespaceTokens(true);

         while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            if (!this.verifyTextParameter()) {
               throw new InvalidFormatException();
            }
         }

         this._tokenParser.clearEndOfTokenCharacters(";:,=");
         return true;
      } else {
         return false;
      }
   }

   private final boolean verifyTextParameter() {
      String paraName = this._propertyParser.getParameterName();
      String paraValue = this._propertyParser.getParameterValue();
      if (paraValue == null) {
         throw new InvalidFormatException();
      } else {
         return paraName != null && (paraName.equals("NOT_SPECIFIED") || paraName.equals("LANGUAGE") || this.isVersion_2_0() && paraName.equals("ALTREP"));
      }
   }

   private final void handleUnsupportedTags() {
      if (this._tokenParser.getBreakingCharacter() != 58) {
         this._tokenParser.setEndOfTokenCharacters(":,;=");
         this._tokenParser.ignoreWhitespaceTokens(true);

         while (this._propertyParser.getNextParameterAndValue(this._tokenParser)) {
            if (this.verifyTextParameter()) {
            }
         }

         this._tokenParser.clearEndOfTokenCharacters(";:,=");
      }

      this._tokenParser.getCommonOneLine(0);
   }

   private final Date getDate(String date) {
      try {
         int length = date.length();
         int index = 0;
         int year = Integer.parseInt(date.substring(index, index + 4));
         index += 4;
         int month = Integer.parseInt(date.substring(index, index + 2));
         index += 2;
         int day = Integer.parseInt(date.substring(index, index + 2));
         index += 2;
         if (index != length) {
            throw new InvalidFormatException();
         }

         Calendar cal = Calendar.getInstance();
         cal.setTime((Date)(new Object(0)));
         cal.set(1, year);
         cal.set(2, month - 1);
         cal.set(5, day);
         return cal.getTime();
      } finally {
         throw new InvalidFormatException();
      }
   }

   private final Date getDateTime(String date) {
      try {
         int indexOfT = date.indexOf(84);
         if (indexOfT < 0) {
            throw new InvalidFormatException();
         }

         boolean utcTime = false;
         Date dateOnly = this.getDate(date.substring(0, indexOfT));
         int length = date.length();
         int index = indexOfT + 1;
         int hour = Integer.parseInt(date.substring(index, index + 2));
         index += 2;
         int minute = Integer.parseInt(date.substring(index, index + 2));
         index += 2;
         int second = Integer.parseInt(date.substring(index, index + 2));
         index += 2;
         if (index == length - 1) {
            if (date.charAt(index) != 'Z') {
               throw new InvalidFormatException();
            }

            utcTime = true;
         } else if (index != length) {
            throw new InvalidFormatException();
         }

         Calendar cal = Calendar.getInstance();
         cal.setTime(dateOnly);
         cal.set(11, hour);
         cal.set(12, minute);
         cal.set(13, second);
         return utcTime ? this.fromUTCTime(cal.getTime()) : cal.getTime();
      } finally {
         throw new InvalidFormatException();
      }
   }

   private final int getDuration() {
      String durationString = this._tokenParser.getCommonOneLine(0);
      boolean negativeDuration = false;
      int duration = 0;
      int index = 0;
      if (durationString == null) {
         throw new InvalidFormatException();
      }

      byte var8;
      switch (durationString.charAt(0)) {
         case '-':
            negativeDuration = true;
         case '+':
            if (durationString.charAt(1) != 'P') {
               throw new InvalidFormatException();
            }

            var8 = 2;
            break;
         case 'P':
            var8 = 1;
            break;
         default:
            throw new InvalidFormatException();
      }

      int indexOfT = durationString.indexOf(84);
      if (indexOfT < 0) {
         duration += this.getDayDuration(durationString.substring(var8));
      } else if (indexOfT == var8) {
         if (durationString.length() <= indexOfT + 1) {
            throw new InvalidFormatException();
         }

         duration += this.getTimeDuration(durationString.substring(indexOfT + 1));
      } else {
         duration += this.getDayDuration(durationString.substring(var8, durationString.indexOf(84)));
         if (durationString.length() <= indexOfT + 1) {
            throw new InvalidFormatException();
         }

         duration += this.getTimeDuration(durationString.substring(indexOfT + 1));
      }

      return negativeDuration ? -duration : duration;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int getDayDuration(String duration) {
      char finalChar = duration.charAt(duration.length() - 1);
      boolean var6 = false /* VF: Semaphore variable */;

      int value;
      try {
         var6 = true;
         value = Integer.parseInt(duration.substring(0, duration.length() - 1));
         var6 = false;
      } finally {
         if (var6) {
            throw new InvalidFormatException();
         }
      }

      switch (finalChar) {
         case 'D':
            return value * 24 * 3600;
         case 'W':
            return value * 7 * 24 * 3600;
         default:
            throw new InvalidFormatException();
      }
   }

   private final int getTimeDuration(String duration) {
      int totalDuration = 0;
      int indexOfH = duration.indexOf(72);
      int indexOfM = duration.indexOf(77);
      int indexOfS = duration.indexOf(83);
      if (indexOfM >= 0 && indexOfM < indexOfH) {
         throw new InvalidFormatException();
      }

      if (indexOfS >= 0 && indexOfS < indexOfM) {
         throw new InvalidFormatException();
      }

      if (indexOfH >= 0 && indexOfM < 0 && indexOfS >= 0) {
         throw new InvalidFormatException();
      }

      try {
         if (indexOfH >= 0) {
            totalDuration += Integer.parseInt(duration.substring(0, indexOfH)) * 3600;
         }

         if (indexOfM >= 0) {
            totalDuration += Integer.parseInt(duration.substring(indexOfH + 1, indexOfM)) * 60;
         }

         return indexOfS >= 0 ? totalDuration + Integer.parseInt(duration.substring(indexOfM + 1, duration.length() - 1)) : totalDuration;
      } finally {
         throw new InvalidFormatException();
      }
   }

   private final Date getDateTimeFromParser() {
      Date date = this._tokenParser.getDateTime();
      if (this._tokenParser.isUTCTime()) {
         date = this.fromUTCTime(date);
      }

      return date;
   }

   private final Date fromUTCTime(Date utcDate) {
      TimeZone tz = TimeZone.getDefault();
      Calendar cal = Calendar.getInstance();
      cal.setTime(utcDate);
      int year = cal.get(1);
      int month = cal.get(2);
      int day = cal.get(5);
      int dayOfWeek = cal.get(7);
      long time = utcDate.getTime();
      int millis = (int)(time % 86400000);
      if (millis < 0) {
         millis += 86400000;
      }

      int offset = tz.getOffset(1, year, month, day, dayOfWeek, millis);
      time += offset;
      return (Date)(new Object(time));
   }
}
