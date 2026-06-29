package net.rim.device.internal.ui;

import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.DrawTextParam$AdvancedDrawTextParam;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.AttributedString$Iterator;
import net.rim.tid.text.AttributedString$Picture;

public class Formatter {
   private static ArticInterface$Line _linesPool;
   private static long _formattingTime;
   private static long _paintingTime;
   public static boolean _debug;
   private static int _errorReportCount;

   public static ArticInterface$Line incrementalFormat(
      FormatParams input, Field field, int width, AttributedString text, int cursor, boolean cursorLeadingEdge, int anchor, boolean doUpdateLayout
   ) {
      return incrementalFormatHelper(input, field, width, text, cursor, cursorLeadingEdge, anchor, doUpdateLayout, false);
   }

   private static ArticInterface$Line incrementalFormatHelper(
      FormatParams input,
      Field field,
      int width,
      AttributedString text,
      int cursor,
      boolean cursorLeadingEdge,
      int anchor,
      boolean doUpdateLayout,
      boolean afterFailure
   ) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   private static ArticInterface$Line adjustLengths(
      int aDelta, ArticInterface$Layout aLayout, XYRect aInvalid, ArticInterface$Line lineList, ArticInterface$Line startLine
   ) {
      int old_lines = aLayout._oldLines;
      if (old_lines < 0) {
         throw new IllegalArgumentException();
      }

      int new_lines = aLayout.lines();
      if (new_lines < 0) {
         throw new IllegalArgumentException();
      }

      int old_height = aLayout._oldBoundsBottom - aLayout._oldBoundsTop;
      int new_height = aLayout._boundsBottom - aLayout._boundsTop;
      ArticInterface$Line ret = readLines(aLayout, lineList, startLine);
      aInvalid.height = new_height == old_height ? new_height : Integer.MAX_VALUE;
      return ret;
   }

   private static ArticInterface$Line readLines(ArticInterface$Layout aLayout, ArticInterface$Line lineList, ArticInterface$Line startLine) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   private static synchronized ArticInterface$Line getLineFromPool() {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   private static synchronized void returnLineToPool(ArticInterface$Line line) {
      line._prev = null;
      line._next = _linesPool;
      _linesPool = line;
   }

   public static ArticInterface$Line getLineInfoForDocPos(
      int docPos, boolean leadingEdge, ArticInterface$Line lineList, ArticInterface$LineInfo info, boolean updateFormatParams
   ) {
      if (docPos <= 0) {
         if (updateFormatParams) {
            info._line = lineList;
            info._start = 0;
            info._top = 0;
         }

         return lineList;
      } else {
         ArticInterface$Line line = info._line;
         int start = info._start;

         int top;
         for (top = info._top; docPos < start || docPos == start && docPos > 0 && !leadingEdge; top -= line._boundsBottom - line._boundsTop) {
            line = line._prev;
            start -= line._textLength + line._skippedCharacters;
         }

         while (true) {
            int end = start + line._textLength + line._skippedCharacters;
            if (docPos < end || line._next == null || docPos == end && !leadingEdge) {
               if (updateFormatParams) {
                  info._line = line;
                  info._start = start;
                  info._top = top;
               }

               return line;
            }

            start = end;
            top += line._boundsBottom - line._boundsTop;
            line = line._next;
         }
      }
   }

   public static void getLineInfoForYPos(int aY, ArticInterface$LineInfo info) {
      while (aY < info._top && info._line._prev != null) {
         info._line = info._line._prev;
         info._start = info._start - (info._line._textLength + info._line._skippedCharacters);
         info._top = info._top - (info._line._boundsBottom - info._line._boundsTop);
      }

      while (true) {
         int bottom = info._top + (info._line._boundsBottom - info._line._boundsTop);
         if (aY < bottom) {
            return;
         }

         if (info._line._next == null) {
            return;
         }

         info._start = info._start + info._line._textLength + info._line._skippedCharacters;
         info._top = bottom;
         info._line = info._line._next;
      }
   }

   public static void paint(
      Graphics aGraphics,
      DrawTextParam drawTextParam,
      ArticInterface$LineInfo info,
      AttributedString$Iterator iterator,
      Field field,
      Formatter$TextRenderer renderer
   ) {
      long start = 0;
      if (_debug) {
         start = System.currentTimeMillis();
      }

      XYRect clip = aGraphics.getClippingRect();
      boolean setColors = !(aGraphics.isDrawingStyleSet(8) | aGraphics.isDrawingStyleSet(16));
      int offset = info._start;
      ArticInterface$Line newLine = info._line;
      int y = info._top;
      int foreground = aGraphics.getColor();
      int background = aGraphics.getBackgroundColor();
      int maxAdvance = drawTextParam.iMaxAdvance;
      DrawTextParam$AdvancedDrawTextParam advancedDrawTextParam = new DrawTextParam$AdvancedDrawTextParam();
      drawTextParam.iAdvancedParam = advancedDrawTextParam;

      for (ArticInterface$Line currentLine = newLine; currentLine != null; currentLine = currentLine._next) {
         drawTextParam.iMaxAdvance = maxAdvance;
         if (currentLine._originX < 0) {
            drawTextParam.iMaxAdvance = drawTextParam.iMaxAdvance - currentLine._originX;
         }

         int currentLineHeight = currentLine._boundsBottom - currentLine._boundsTop;
         int lineLength = currentLine._textLength + currentLine._skippedCharacters;
         int runs = currentLine._layoutRun == null ? 0 : currentLine._layoutRun.length;
         int run_x = currentLine._originX;
         int run_y = y + currentLine._originY;
         aGraphics.setColor(foreground);
         aGraphics.setBackgroundColor(background);
         boolean lastRunUnderlined = false;

         for (int j = 0; j < runs; j++) {
            ArticInterface$LayoutRun run = currentLine._layoutRun[j];
            iterator.set(offset + run._textStart, offset + run._textStart + run._textLength);
            long attrib = iterator.runAttrib();
            if (setColors) {
               int bgColor = (int)((attrib & -281474976710656L) >> 48);
               bgColor = Ui.convertColorFrom16bit(bgColor, background);
               if (background != bgColor) {
                  aGraphics.setColor(bgColor);
                  aGraphics.fillRect(run_x, y, run._advance, currentLineHeight);
               }
            }

            int advance = 0;
            AttributedString$Picture picture = iterator.runPicture();
            if ((run._flags & 4) != 0) {
               if (picture != null) {
                  picture.draw(aGraphics, run_x, run_y + picture.getInfo()._y);
               }
            } else if ((run._flags & 8) == 0) {
               if ((run._flags & 16) != 0) {
                  aGraphics.drawText('‐', run_x, run_y, 8, -1);
               } else {
                  Font font = Ui.getFontFromAttributes(attrib, renderer.getFont());
                  aGraphics.setFont(font);
                  if (setColors) {
                     int fgColor = (int)((attrib & 281470681743360L) >> 32);
                     aGraphics.setColor(Ui.convertColorFrom16bit(fgColor, foreground));
                  }

                  drawTextParam.iReverse = (run._flags & 2) != 0 ? 1 : 0;
                  drawTextParam.iPasswordMode = (attrib & 268435456) != 0;
                  drawTextParam.iStartOffset = run._textStart;
                  drawTextParam.iEndOffset = run._textStart + run._textLength;
                  advancedDrawTextParam.iAllowStartOverlap = true;
                  long underline = attrib & 786432;
                  if (underline != 0) {
                     if (lastRunUnderlined) {
                        drawTextParam.iUnderlineToBounds = false;
                     } else if (j == runs - 1) {
                        drawTextParam.iUnderlineToBounds = true;
                     } else {
                        int nextStart = offset + currentLine._layoutRun[j + 1]._textStart;
                        iterator.set(nextStart, nextStart + currentLine._layoutRun[j + 1]._textLength);
                        drawTextParam.iUnderlineToBounds = underline != (iterator.runAttrib() & 786432);
                     }
                  }

                  lastRunUnderlined = underline != 0;
                  advance = renderer.drawText(aGraphics, offset, lineLength, run_x, run_y, drawTextParam);
               }
            }

            boolean selected = (run._flags & 1) != 0;
            boolean highlit = (attrib & 67108864) != 0;
            if (selected != highlit && !aGraphics.isDrawingStyleSet(8)) {
               drawHighlightRegion(
                  aGraphics, 2, true, run_x, y, run._advance, currentLineHeight, run_y, offset, lineLength, drawTextParam, picture, run._flags, field, renderer
               );
               aGraphics.setColor(foreground);
               aGraphics.setBackgroundColor(background);
            }

            int runWidth = Math.max(run._advance, advance);
            if (drawTextParam.iMaxAdvance > 0) {
               drawTextParam.iMaxAdvance -= runWidth;
            }

            run_x += runWidth;
         }

         offset += lineLength;
         y += currentLineHeight;
         if (y >= clip.y + clip.height) {
            break;
         }
      }

      if (_debug) {
         _paintingTime = _paintingTime + (System.currentTimeMillis() - start);
      }

      drawTextParam.iAdvancedParam = null;
   }

   private static final void drawHighlightRegion(
      Graphics graphics,
      int style,
      boolean on,
      int x,
      int y,
      int width,
      int height,
      int baseline_y,
      int offset,
      int length,
      DrawTextParam drawTextParam,
      AttributedString$Picture picture,
      int run_flags,
      Field field,
      Formatter$TextRenderer renderer
   ) {
      switch (ThemeAttributeSet.getFocusStyle(field)) {
         case -1:
            break;
         case 0:
         default:
            switch (style) {
               case -1:
                  return;
               case 0:
                  throw new IllegalArgumentException();
               case 1:
               case 2:
               case 3:
               default:
                  graphics.invert(x, y, width, height);
                  return;
            }
         case 1:
            switch (style) {
               case -1:
                  return;
               case 0:
                  throw new IllegalArgumentException();
               case 2:
                  graphics.invert(x, y, width, height);
                  return;
               case 3:
               default:
                  graphics.invert(x, y, width, height);
               case 1:
                  int mid = y + (height >> 1);
                  graphics.invert(x, mid, width, 1);
                  return;
            }
         case 2:
            if (!on) {
               graphics.pushContext(x, y, width, height, 0, 0);
               if (on) {
                  graphics.setDrawingStyle(8, (style & 1) != 0);
                  graphics.setDrawingStyle(16, (style & 2) != 0);
               }

               graphics.clear();
               drawPictureOrText(graphics, x, baseline_y, offset, length, drawTextParam, picture, run_flags, renderer);
               graphics.popContext();
               return;
            }

            switch (style) {
               case -1:
                  return;
               case 0:
                  throw new IllegalArgumentException();
               case 1:
                  graphics.drawRect(x, y, width, height);
                  return;
               case 3:
               default:
                  graphics.drawRect(x, y, width, height);
               case 2:
                  graphics.invert(x, y, width, height);
                  return;
            }
         case 3:
            graphics.pushContext(x, y, width, height, 0, 0);
            if (on) {
               graphics.setDrawingStyle(8, (style & 1) != 0);
               graphics.setDrawingStyle(16, (style & 2) != 0);
               switch (style) {
                  case -1:
                     break;
                  case 0:
                     throw new IllegalArgumentException();
                  case 1:
                  case 3:
                  default:
                     graphics.setColor(ThemeAttributeSet.getColor(field, 3));
                     graphics.setBackgroundColor(ThemeAttributeSet.getColor(field, 2));
                     break;
                  case 2:
                     graphics.setColor(ThemeAttributeSet.getColor(field, 5));
                     graphics.setBackgroundColor(ThemeAttributeSet.getColor(field, 4));
               }

               graphics.setBackgroundImage(null, 0, 0);
            }

            graphics.clear();
            drawPictureOrText(graphics, x, baseline_y, offset, length, drawTextParam, picture, run_flags, renderer);
            graphics.popContext();
            return;
         case 4:
            graphics.pushContext(x, y, width, height, 0, 0);
            if (on) {
               graphics.setDrawingStyle(8, (style & 1) != 0);
               graphics.setDrawingStyle(16, (style & 2) != 0);
            }

            graphics.clear();
            drawPictureOrText(graphics, x, baseline_y, offset, length, drawTextParam, picture, run_flags, renderer);
            graphics.popContext();
      }
   }

   private static void drawPictureOrText(
      Graphics graphics,
      int x,
      int y,
      int offset,
      int length,
      DrawTextParam drawTextParam,
      AttributedString$Picture picture,
      int run_flags,
      Formatter$TextRenderer renderer
   ) {
      if (picture != null) {
         picture.draw(graphics, x, y + picture.getInfo()._y);
      } else if ((run_flags & 16) != 0) {
         graphics.drawText('‐', x, y, 8, -1);
      } else if ((run_flags & 8) == 0) {
         renderer.drawText(graphics, offset, length, x, y, drawTextParam);
      }
   }

   public static ArticInterface$Line setSelection(
      ArticInterface$Line lineList,
      int oldAnchor,
      int oldCursor,
      int newAnchor,
      int newCursor,
      boolean newCursorLeadingEdge,
      int width,
      AttributedString text,
      FormatParams formatParams
   ) {
      ArticInterface$LineInfo cursorLineInfo = formatParams._cursorLineInfo;
      int old_top = 0;
      int old_bottom = 0;
      if (oldCursor != oldAnchor) {
         int sel_start = oldCursor;
         int sel_end = oldAnchor;
         if (oldAnchor < oldCursor) {
            sel_start = oldAnchor;
            sel_end = oldCursor;
         }

         getLineInfoForDocPos(sel_start, true, lineList, cursorLineInfo, true);
         int line_start = cursorLineInfo._start;
         int line_top = cursorLineInfo._top;
         ArticInterface$Line cur_line = cursorLineInfo._line;

         do {
            int line_end = line_start + cur_line._textLength + cur_line._skippedCharacters;
            int line_bottom = line_top + cur_line._boundsBottom - cur_line._boundsTop;
            if (line_end > sel_start) {
               if (line_start <= sel_start) {
                  old_top = line_top;
               }

               if (line_end >= sel_end) {
                  old_bottom = line_bottom;
               }

               if (cur_line._layoutRun != null) {
                  int runs = cur_line._layoutRun.length;

                  for (int j = 0; j < runs; j++) {
                     cur_line._layoutRun[j]._flags &= -2;
                  }
               }
            }

            line_start = line_end;
            line_top = line_bottom;
            cur_line = cur_line._next;
         } while (cur_line != null);
      }

      int new_top = 0;
      int new_bottom = 0;
      if (newCursor != newAnchor) {
         int sel_start = newCursor;
         int sel_end = newAnchor;
         if (newAnchor < newCursor) {
            sel_start = newAnchor;
            sel_end = newCursor;
         }

         getLineInfoForDocPos(sel_start, true, lineList, cursorLineInfo, true);
         int line_start = cursorLineInfo._start;
         int line_top = cursorLineInfo._top;
         ArticInterface$Line cur_line = cursorLineInfo._line;
         int sel_start_line_start = line_start;
         int sel_start_line_end = line_start + cur_line._textLength + cur_line._skippedCharacters;
         new_top = line_top;
         int sel_end_line_start = 0;
         int sel_end_line_end = 0;
         boolean reformat_start_line = true;
         boolean reformat_end_line = true;

         do {
            int line_end = line_start + cur_line._textLength + cur_line._skippedCharacters;
            int line_bottom = line_top + cur_line._boundsBottom - cur_line._boundsTop;
            int adjusted_line_end = cur_line._next == null ? line_end + 1 : line_end;
            if (cur_line._layoutRun == null) {
               if (line_start == sel_start) {
                  reformat_start_line = false;
               }

               if (line_start == sel_end) {
                  reformat_end_line = false;
               }
            } else {
               int runs = cur_line._layoutRun.length;

               for (int j = 0; j < runs; j++) {
                  int run_start = line_start + cur_line._layoutRun[j]._textStart;
                  int run_end = run_start + cur_line._layoutRun[j]._textLength;
                  if (run_start == sel_start || run_end == sel_start) {
                     reformat_start_line = false;
                  }

                  if (run_start == sel_end || run_end == sel_end) {
                     reformat_end_line = false;
                  }

                  if (run_start >= sel_start && run_end <= sel_end) {
                     cur_line._layoutRun[j]._flags |= 1;
                  }
               }
            }

            if (sel_end >= line_start && sel_end < adjusted_line_end) {
               sel_end_line_start = line_start;
               sel_end_line_end = line_end;
               new_bottom = line_bottom;
               break;
            }

            line_start = line_end;
            line_top = line_bottom;
            cur_line = cur_line._next;
         } while (cur_line != null);

         getLineInfoForDocPos(newCursor, newCursorLeadingEdge, lineList, cursorLineInfo, true);
         if (reformat_start_line) {
            lineList = reformatAfterSelectionChange(
               lineList,
               sel_start_line_start,
               sel_start_line_end,
               width,
               text,
               newCursor,
               newCursorLeadingEdge,
               newAnchor,
               cursorLineInfo,
               formatParams._formatFlags
            );
         }

         if (reformat_end_line) {
            lineList = reformatAfterSelectionChange(
               lineList,
               sel_end_line_start,
               sel_end_line_end,
               width,
               text,
               newCursor,
               newCursorLeadingEdge,
               newAnchor,
               cursorLineInfo,
               formatParams._formatFlags
            );
         }
      }

      getLineInfoForDocPos(newCursor, newCursorLeadingEdge, lineList, cursorLineInfo, true);
      if (new_top == new_bottom) {
         new_top = old_top;
         new_bottom = old_bottom;
      } else if (old_top != old_bottom) {
         if (new_top > old_top) {
            new_top = old_top;
         }

         if (new_bottom < old_bottom) {
            new_bottom = old_bottom;
         }
      }

      if (new_top != new_bottom) {
         formatParams._invalidRect.set(0, new_top, width, new_bottom - new_top);
      }

      return lineList;
   }

   private static ArticInterface$Line reformatAfterSelectionChange(
      ArticInterface$Line lineList,
      int lineStart,
      int lineEnd,
      int width,
      AttributedString text,
      int cursor,
      boolean cursorLeadingEdge,
      int anchor,
      ArticInterface$LineInfo cursorLineInfo,
      int formatFlags
   ) {
      synchronized (ArticInterface._layout) {
         ArticInterface$Layout layout = ArticInterface.Format(
            width,
            lineStart,
            lineEnd - lineStart,
            lineEnd - lineStart,
            cursor,
            cursorLeadingEdge,
            anchor,
            text,
            cursorLineInfo._line,
            cursorLineInfo._start,
            cursorLineInfo._top,
            Integer.MAX_VALUE,
            true,
            formatFlags
         );
         getLineInfoForDocPos(lineStart, true, lineList, cursorLineInfo, true);
         ArticInterface$Line lastUnchagedLine = cursorLineInfo._line._prev;
         lineList = readLines(layout, lineList, cursorLineInfo._line);
         cursorLineInfo._line = lastUnchagedLine == null ? lineList : lastUnchagedLine._next;
         return lineList;
      }
   }

   public static void getTextBounds(int offset1, int offset2, XYRect rect, ArticInterface$Line line, int lineStart, int lineTop) {
      rect.set(0, 0, 0, 0);
      int y = lineTop;
      int height = line._boundsBottom - line._boundsTop;
      offset1 -= lineStart;
      offset2 -= lineStart;
      int runs = line._layoutRun == null ? 0 : line._layoutRun.length;
      int run_x = line._originX;

      for (int i = 0; i < runs; i++) {
         ArticInterface$LayoutRun run = line._layoutRun[i];
         if (offset1 <= run._textStart && run._textStart + run._textLength <= offset2) {
            rect.union(run_x, y, run._advance, height);
         }

         run_x += run._advance;
      }
   }

   public static void printPerformanceStats(long aTotalTime) {
      long one_type = _formattingTime * 1000 / aTotalTime;
      System.out.println("*In formatting: " + one_type / 10 + "." + one_type % 10 + "%");
      one_type = _paintingTime * 1000 / aTotalTime;
      System.out.println("*In painting: " + one_type / 10 + "." + one_type % 10 + "%");
   }

   private static ArticInterface$Line recoverArticFailure(
      RuntimeException e,
      FormatParams input,
      Field field,
      int width,
      AttributedString text,
      int cursor,
      boolean cursorLeadingEdge,
      int anchor,
      boolean doUpdateLayout,
      boolean afterFailure
   ) {
      if (afterFailure) {
         throw e;
      }

      e.printStackTrace();
      reportArticException(
         text,
         width,
         input,
         cursor,
         cursorLeadingEdge,
         0,
         0,
         anchor,
         input._cursorLineInfo._start,
         input._cursorLineInfo._top,
         input._cursorLineInfo._line,
         input._lineList
      );
      ArticInterface$Line lineList = new ArticInterface$Line();
      lineList._flags = 3;
      input.init(0, 0, text.length(), cursor, false, lineList);
      return incrementalFormatHelper(input, field, width, text, cursor, cursorLeadingEdge, anchor, doUpdateLayout, true);
   }

   public static void reportArticException(
      AttributedString text,
      int width,
      FormatParams input,
      int cursor,
      boolean cursorLeadingEdge,
      int x,
      int y,
      int anchor,
      int cursorLineStart,
      int cursorLineTop,
      ArticInterface$Line cursorLine,
      ArticInterface$Line lineList
   ) {
      if (_errorReportCount < 2) {
         String strText = text.toString();
         String debugText = "Text: [" + strText + "]\n" + "Text length: " + strText.length() + "\n";
         if (input == null) {
            if (cursor == -1) {
               debugText = debugText + "doc position to find: " + x + "," + y + "\n";
            } else {
               debugText = debugText + "position to find: " + cursor + "\n" + "position leading edge: " + cursorLeadingEdge + "\n";
            }
         } else {
            debugText = debugText
               + "nextStartPosToFormat: "
               + input._nextStartPosToFormat
               + "\n"
               + "formatOldLength: "
               + input._formatOldLength
               + "\n"
               + "formatNewLength: "
               + input._formatNewLength
               + "\n"
               + "cursor: "
               + cursor
               + "\n"
               + "cursorLeadingEdge: "
               + cursorLeadingEdge
               + "\n"
               + "anchor: "
               + anchor
               + "\n"
               + "linesToFormatCount: "
               + input._linesToFormatCount
               + "\n"
               + "formatTextUnchanged: "
               + input._formatTextUnchanged
               + "\n"
               + "formatFlags: "
               + input._formatFlags
               + "\n";
         }

         System.err
            .println(
               debugText
                  + "cursorLineLength: "
                  + cursorLine._textLength
                  + "\n"
                  + "cursorLineStart: "
                  + cursorLineStart
                  + "\n"
                  + "cursorLineTop: "
                  + cursorLineTop
                  + "\n"
                  + "LineList:"
            );
         int count = 0;

         for (ArticInterface$Line line = lineList; line != null; line = line._next) {
            if (count > 0) {
               System.err.print("->");
            }

            System.err.print("(" + line._textLength + "," + line._skippedCharacters + "," + line._flags + ")");
            count++;
         }

         System.err.println("\nAttributes:");
         AttributedString$Iterator iter = text.getIterator();
         count = 0;

         do {
            if (count > 0) {
               System.err.print("->");
            }

            long attrib = iter.runAttrib();
            Font font = Ui.getFontFromAttributes(attrib, Font.getDefault());
            System.err
               .print(
                  "("
                     + iter.runLength()
                     + ";"
                     + Integer.toHexString((int)(attrib >> 32))
                     + Integer.toHexString((int)attrib)
                     + ";Font:"
                     + font.getFontFamily().getName()
                     + ","
                     + font.getHeight()
                     + ","
                     + font.getStyle()
                     + ")"
               );
            count++;
         } while (iter.next());

         System.err.print("\nText in hexa: ");

         for (int i = 0; i < strText.length(); i++) {
            System.err.print(Integer.toHexString(strText.charAt(i)) + " ");
         }

         System.err.println("");
         long LOGWORTHY_REPORT_REQUEST = 2888237357036234703L;
         RIMGlobalMessagePoster.postGlobalEvent(LOGWORTHY_REPORT_REQUEST, 0, 0, "", null);
         _errorReportCount++;
      }
   }
}
