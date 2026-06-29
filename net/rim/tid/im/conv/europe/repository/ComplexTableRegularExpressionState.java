package net.rim.tid.im.conv.europe.repository;

import net.rim.device.api.i18n.Locale;
import net.rim.tid.im.conv.europe.CaseCorrector;

public class ComplexTableRegularExpressionState {
   private RegularExpression expr;
   private RegularExpressionState state;
   private Locale locale;
   private boolean ignoreCapitalization;
   private boolean ignoreCase;
   private boolean[] upperCasePrefix = new boolean[2];
   private char[] charPrefix = new char[2];
   private Object[] marks = new Object[2];
   private int upperCasePrefixIndex;

   public ComplexTableRegularExpressionState(
      boolean ignoreCapitalization, boolean ignoreCase, RegularExpression expr, RegularExpressionState state, Locale locale
   ) {
      this.ignoreCapitalization = ignoreCapitalization;
      this.ignoreCase = ignoreCase;
      this.expr = expr;
      this.state = state;
      this.locale = locale;
   }

   public boolean isIgnoreCase() {
      return this.ignoreCase;
   }

   public int ucMark() {
      this.marks[this.upperCasePrefixIndex] = this.state.newMark();
      return this.upperCasePrefixIndex;
   }

   public void ucRollback(int mark) {
      this.upperCasePrefixIndex = mark;
   }

   public void setUpperCaseUsed(boolean used, char ch) {
      this.charPrefix[this.upperCasePrefixIndex] = ch;
      this.upperCasePrefix[this.upperCasePrefixIndex++] = used;
   }

   public boolean checkCorrectCasePrefix(boolean first, boolean second) {
      if (this.ignoreCase) {
         return true;
      }

      if (this.upperCasePrefixIndex > 1 && this.upperCasePrefix[1] == second) {
         if ((!this.ignoreCapitalization || first) && this.upperCasePrefix[0] != first) {
            this.state.rollback(this.marks[0]);
            return first
               ? this.expr.accept(this.state, CaseCorrector.toUpperCase(this.charPrefix[0], this.locale))
               : this.expr.accept(this.state, CaseCorrector.toLowerCase(this.charPrefix[0], this.locale));
         } else {
            return true;
         }
      } else {
         if ((!this.ignoreCapitalization || first) && this.upperCasePrefix[0] != first) {
            this.state.rollback(this.marks[0]);
            char ch1 = first ? CaseCorrector.toUpperCase(this.charPrefix[0], this.locale) : CaseCorrector.toLowerCase(this.charPrefix[0], this.locale);
            if (!this.expr.accept(this.state, ch1)) {
               return false;
            }
         }

         if (this.upperCasePrefixIndex > 1) {
            char ch2 = second ? CaseCorrector.toUpperCase(this.charPrefix[1], this.locale) : CaseCorrector.toLowerCase(this.charPrefix[1], this.locale);
            this.state.rollback(this.marks[1]);
            return this.expr.accept(this.state, ch2);
         } else {
            return true;
         }
      }
   }
}
