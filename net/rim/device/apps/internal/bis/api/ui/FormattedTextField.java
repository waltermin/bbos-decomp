package net.rim.device.apps.internal.bis.api.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.RichTextField;

public final class FormattedTextField extends RichTextField {
   private int[] offsets;
   private int[] boldStartingPos;
   private static final String HTML_TAG_PARAGRAPH_START = "<p>";
   private static final String HTML_TAG_PARAGRAPH_END = "</p>";
   private static final String HTML_TAG_BREAK = "<br>";
   private static final String HTML_TAG_BOLD_START = "<b>";
   private static final String HTML_TAG_BOLD_END = "</b>";
   private static final int NORMAL_FONT_INDEX = 0;
   private static final int BOLD_FONT_INDEX = 1;
   private static final int MINIMUM_OFFSETS_LIST_LENGTH = 2;
   private static final Font DEFAULT_FONT = null;

   public FormattedTextField() {
      this(null, 36028797018963968L);
   }

   public FormattedTextField(String text) {
      this(text, 36028797018963968L);
   }

   public FormattedTextField(String text, long style) {
      super(text, style);
      text = this.convertLineBreaks(text);
      text = this.convertParagraphs(text);
      text = this.getBoldTextOffsets(text);
      this.setText(text);
   }

   @Override
   public final void setText(String text) {
      Font currentFont = this.getFont();
      Font boldFont = currentFont.derive(currentFont.getStyle() | 1);
      if (this.offsets != null && this.boldStartingPos != null && text != null && text.length() > 0) {
         byte[] attributes = this.generateAttributes();
         super.setText(text, this.offsets, attributes, new Object[]{DEFAULT_FONT, boldFont});
      } else {
         super.setText(text);
      }
   }

   private final String convertLineBreaks(String original) {
      if (original == null) {
         return null;
      }

      StringBuffer breakSearchAndReplaceBuffer = (StringBuffer)(new Object(original));
      int breakIndex = 0;

      while ((breakIndex = breakSearchAndReplaceBuffer.toString().indexOf("<br>", breakIndex)) != -1) {
         breakSearchAndReplaceBuffer.delete(breakIndex, breakIndex + 4);
         breakSearchAndReplaceBuffer.insert(breakIndex, "\n");
      }

      return breakSearchAndReplaceBuffer.toString();
   }

   private final String convertParagraphs(String original) {
      if (original == null) {
         return null;
      }

      StringBuffer startParagraphSearchAndReplaceBuffer = (StringBuffer)(new Object(original));
      int startParagraphIndex = 0;

      while ((startParagraphIndex = startParagraphSearchAndReplaceBuffer.toString().indexOf("<p>", startParagraphIndex)) != -1) {
         startParagraphSearchAndReplaceBuffer.delete(startParagraphIndex, startParagraphIndex + 3);
         startParagraphSearchAndReplaceBuffer.insert(startParagraphIndex, "\n\n");
      }

      String formattedText = startParagraphSearchAndReplaceBuffer.toString();
      StringBuffer endParagraphSearchAndReplaceBuffer = (StringBuffer)(new Object(formattedText));
      int endParagraphIndex = 0;

      while ((endParagraphIndex = endParagraphSearchAndReplaceBuffer.toString().indexOf("</p>", endParagraphIndex)) != -1) {
         endParagraphSearchAndReplaceBuffer.delete(endParagraphIndex, endParagraphIndex + 4);
         endParagraphSearchAndReplaceBuffer.insert(endParagraphIndex, "\n\n");
      }

      return endParagraphSearchAndReplaceBuffer.toString();
   }

   private final String getBoldTextOffsets(String original) {
      StringBuffer boldTagsReplaceBuffer = (StringBuffer)(new Object(original));
      int offsetsListLength = original.length() < 2 ? 2 : original.length();
      int[] offsetsList = new int[offsetsListLength];
      int[] boldStartingPositionsList = new int[offsetsListLength / 2];
      int offsetsCounter = 0;
      int boldStartingPosCounter = 0;
      offsetsList[offsetsCounter++] = 0;
      int trackingIndex = 0;

      while ((trackingIndex = boldTagsReplaceBuffer.toString().indexOf("<b>")) != -1) {
         if (offsetsList[offsetsCounter - 1] != trackingIndex) {
            offsetsList[offsetsCounter++] = trackingIndex;
         }

         if (boldStartingPosCounter == 0 || boldStartingPositionsList[boldStartingPosCounter - 1] != trackingIndex) {
            boldStartingPositionsList[boldStartingPosCounter++] = trackingIndex;
         }

         boldTagsReplaceBuffer.delete(trackingIndex, trackingIndex + 3);
         if ((trackingIndex = boldTagsReplaceBuffer.toString().indexOf("</b>")) == -1) {
            break;
         }

         if (offsetsList[offsetsCounter - 1] != trackingIndex) {
            offsetsList[offsetsCounter++] = trackingIndex;
         } else {
            if (offsetsCounter > 1) {
               offsetsCounter--;
            }

            boldStartingPosCounter--;
         }

         boldTagsReplaceBuffer.delete(trackingIndex, trackingIndex + 4);
      }

      if (offsetsList[offsetsCounter - 1] != boldTagsReplaceBuffer.length()) {
         offsetsList[offsetsCounter++] = boldTagsReplaceBuffer.length();
      }

      this.offsets = new int[offsetsCounter];

      for (int i = 0; i < offsetsCounter; i++) {
         this.offsets[i] = offsetsList[i];
      }

      this.boldStartingPos = new int[boldStartingPosCounter];

      for (int i = 0; i < boldStartingPosCounter; i++) {
         this.boldStartingPos[i] = boldStartingPositionsList[i];
      }

      return boldTagsReplaceBuffer.toString();
   }

   private final byte[] generateAttributes() {
      byte[] attributes = new byte[this.offsets.length - 1];
      int i = 0;
      int boldStartingPosCounter = 0;

      while (i < attributes.length) {
         if (this.boldStartingPos.length > 0 && this.offsets[i] == this.boldStartingPos[boldStartingPosCounter]) {
            attributes[i] = 1;
            if (boldStartingPosCounter < this.boldStartingPos.length - 1) {
               boldStartingPosCounter++;
            }
         } else {
            attributes[i] = 0;
         }

         i++;
      }

      return attributes;
   }
}
