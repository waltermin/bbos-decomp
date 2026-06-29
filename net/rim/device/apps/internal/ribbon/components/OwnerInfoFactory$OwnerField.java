package net.rim.device.apps.internal.ribbon.components;

import java.util.Hashtable;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.api.ribbon.StringRibbonComponent;
import net.rim.device.internal.deviceoptions.Owner;

final class OwnerInfoFactory$OwnerField extends StringRibbonComponent implements GlobalEventListener {
   private int _type;
   private String _text;
   DrawTextParam _drawParam;

   @Override
   public final void applyTheme() {
   }

   @Override
   public final String getText() {
      return this._text;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -3297167379286550693L) {
         this.updateText();
      }

      super.eventOccurred(guid, data0, data1, object0, object1);
   }

   private final void updateText() {
      switch (this._type) {
         case 0:
         default:
            this._text = Owner.getOwnerName();
            return;
         case 1:
            this._text = Owner.getOwnerInfo();
         case -1:
      }
   }

   @Override
   public final void initialize(Hashtable params, Object context) {
      this._drawParam = new DrawTextParam();
      String id = (String)params.get("id");
      if (id != null) {
         this._type = id.equals("name") ? 0 : 1;
      }

      super.initialize(params, context);
      this.updateText();
   }

   private final int getFirstUndrawnCharacterIndex(Font font, int usedAdvance, int totalAdvance, String text) {
      if (text.length() != 0 && totalAdvance != 0) {
         int fracFP = Fixed32.div(Fixed32.toFP(usedAdvance), Fixed32.toFP(totalAdvance));
         int guess = Fixed32.toInt(Fixed32.mul(fracFP, Fixed32.toFP(text.length())));
         int measureGuess = font.measureText(text, 0, guess + 1, null, null);
         if (measureGuess == usedAdvance) {
            guess++;
            return guess > text.length() ? -1 : guess;
         }

         if (measureGuess > usedAdvance) {
            guess--;

            while (guess >= 0) {
               if (font.measureText(text, 0, guess + 1, null, null) <= usedAdvance) {
                  return guess + 1;
               }

               guess--;
            }

            return 0;
         } else {
            guess++;

            while (guess < text.length()) {
               measureGuess = font.measureText(text, 0, guess + 1, null, null);
               if (measureGuess > usedAdvance) {
                  return guess - 1;
               }

               if (measureGuess == usedAdvance) {
                  if (++guess > text.length()) {
                     return -1;
                  }

                  return guess;
               }

               guess++;
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   @Override
   public final void drawText(Graphics g, String text, int x, int y, int flags, int width) {
      if (this._type == 0) {
         super.drawText(g, text, x, y, flags, width);
      } else {
         StringTokenizer st = new StringTokenizer(text, " \n\t\r\f", true);
         Font font = g.getFont();
         int maxColumn = x + width;
         int currentLine = y;
         int currentColumn = x;

         while (st.hasMoreTokens()) {
            String currentWord = st.nextToken();
            boolean isAReturn = currentWord.charAt(0) == '\n';
            if (isAReturn) {
               currentColumn = x;
               currentLine += font.getHeight();
            } else {
               int wordWidth = font.getAdvance(currentWord);
               if (wordWidth + currentColumn > maxColumn && currentColumn != x) {
                  currentColumn = x;
                  currentLine += font.getHeight();
               }

               if (wordWidth + currentColumn > maxColumn) {
                  this._drawParam.iMaxAdvance = width;
                  int usedAdvance = g.drawText(currentWord, 0, currentWord.length(), currentColumn, currentLine, this._drawParam, null);

                  while (usedAdvance < wordWidth) {
                     int start = this.getFirstUndrawnCharacterIndex(font, usedAdvance, wordWidth, currentWord);
                     if (start < 0) {
                        break;
                     }

                     if (start == 0) {
                        return;
                     }

                     currentColumn = x;
                     currentLine += font.getHeight();
                     currentWord = currentWord.substring(start);
                     wordWidth = font.getAdvance(currentWord);
                     usedAdvance = g.drawText(currentWord, 0, currentWord.length(), currentColumn, currentLine, this._drawParam, null);
                     currentColumn += usedAdvance;
                  }
               } else {
                  g.drawText(currentWord, currentColumn, currentLine, 0, width);
                  currentColumn += wordWidth;
               }
            }
         }
      }
   }
}
