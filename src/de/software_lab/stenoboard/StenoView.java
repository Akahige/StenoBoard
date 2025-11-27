// 27nov25 Software Lab. Alexander Burger

package de.software_lab.stenoboard;

import java.io.*;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.ArrayDeque;

import android.util.*;
import android.view.*;
import android.graphics.*;
import android.content.res.*;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.ContentResolver;
import android.content.ClipboardManager;
import android.content.ClipDescription;
import android.content.ClipData;
import android.media.AudioManager;
import android.provider.Settings;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.os.Bundle;

public class StenoView extends View implements RecognitionListener {
   StenoIME Ime;
   SpeechRecognizer Mic;
   float Max, Width, Height, Size, OrgX, OrgY, PadX, PadY;
   int Pos, Dir, Rpt, RptN;
   boolean Off, Beg, Shift, Punct, Digit, Cntrl, AltGr, Funct, Upc;
   int Num, Repeat, Repeat2, Repeat3, Vis, Wipe;
   long ActTime, TapTime;
   float BegX, BegY, PX, PY, MX, MY, TapX, TapY;
   final Paint Text1 = new Paint();
   final Paint Text2 = new Paint();
   final Paint[] Lines = new Paint[5];
   final int[] Colors = new int[]{Color.WHITE, Color.BLACK, Color.RED, Color.GREEN, Color.BLUE};
   String Dict[];
   File DictFile;
   ArrayDeque<String> Paste = new ArrayDeque<String>();
   final static int CANDIDATES = 40;
   final String Candidates[] = new String[CANDIDATES];
   float CandX[] = new float[CANDIDATES];
   float CandY;
   int CandPos;
   Paint Help;

   final static int DOT = 12;
   final static int DLY = 80;
   final static int MOV = 300;
   final static int TAP = 800;

   final static int Dirs[] = {
      1, 2, 3, 0, 5, 4, 3, 0, 1, 8, 7, 0, 5, 6, 7, 0
   };

   final static int Strokes[] = {
          1,  2,  3,  4,  5,  6,  7,  8,
   /*1*/  0,  9,  9, 11, 11, 11, 10, 10,
   /*2*/ 13,  0, 12, 12, 14, 14, 14, 13,
   /*3*/ 16, 16,  0, 15, 15, 17, 17, 17,
   /*4*/ 20, 19, 19,  0, 18, 18, 20, 20,
   /*5*/ 23, 23, 22, 22,  0, 21, 21, 23,
   /*6*/ 26, 26, 26, 25, 25,  0, 24, 24,
   /*7*/ 27, 29, 29, 29, 28, 28,  0, 27,
   /*8*/ 30, 30, 32, 32, 32, 31, 31,  0
   };

   final static int Steno[] = new int[] {
      32, 'r', 't', 's', 'e', 'n', 'i', 'a',
      'b', 'h', 'z', 'x', 'v', 'w', 'j', 'l',
      'u', ',', 'k', '(', '?', 'c', 'f', 'g',
      '.', 'm', 'p', 'q', 'd', 'o', 'y', ')'
   };
   final static int StenoDigit[] = new int[] {
      32, 0x100001, 0x100002, 0x100003, 0x100004, 0x100005, 0x100006, 0x100007,
      '7', '-', '3', 0, 0x100008, 0x100009, '9', '4',
      '1', '/', '2', '5', '+', '6', '8', 0,
      '.', 0x10000A, 0x10000B, 0, 0x10000C, '0', '*', 0x10000D
   };
   final static int StenoPunct[] = new int[] {
      32, 0, '~', '$', '=', 0, 0, '&',
      '\\', '#', 0, 0, 0, 0, 0, '<',
      '_', ';', '@', '[', '!', '^', 0, '`',
      ':', '>', '%', '\'', '"', '|', 0, ']'
   };
   final static int StenoAltGr[] = new int[] {
      32, 9829, 128077, 'ß', '€', 'ñ', 0, 'ä',
      128560, '—', '³', 0, 0, 0, 0, '☺',
      'ü', 0, '²', 128073, '±', 128526, 0, 'º',
      0, 0, '§', 0, 'Δ', 'ö', 0, 128072
   };
   final static int StenoFunct[] = new int[] {
      -KeyEvent.KEYCODE_DPAD_RIGHT, -KeyEvent.KEYCODE_PAGE_DOWN, -KeyEvent.KEYCODE_DPAD_DOWN, -KeyEvent.KEYCODE_MOVE_END, -KeyEvent.KEYCODE_DPAD_LEFT, -KeyEvent.KEYCODE_MOVE_HOME, -KeyEvent.KEYCODE_DPAD_UP, -KeyEvent.KEYCODE_PAGE_UP,
      -KeyEvent.KEYCODE_F7, 0, -KeyEvent.KEYCODE_F3, 0, -KeyEvent.KEYCODE_F11, -KeyEvent.KEYCODE_F12, -KeyEvent.KEYCODE_F9, -KeyEvent.KEYCODE_F4,
      -KeyEvent.KEYCODE_F1, -KeyEvent.KEYCODE_INSERT, -KeyEvent.KEYCODE_F2, -KeyEvent.KEYCODE_F5, 0, -KeyEvent.KEYCODE_F6, -KeyEvent.KEYCODE_F8, 0,
      0, 0, 0, -KeyEvent.KEYCODE_BREAK, -KeyEvent.KEYCODE_FORWARD_DEL, -KeyEvent.KEYCODE_F10, 0, 0
   };

   final static String StenoHelp1[] = new String[] {
      "", "", "S", "P", "D", "A", "F"
   };
   final static String StenoHelp[][] = new String[][] {
      {"E", "SP", "SP", "SP", "SP", "SP", "RIGHT"},
      {"NE", "a", "A", "&", "TOP-R", "ä", "PGUP"},
      {"E-R", "b", "B", "\\", "7", "😰", "F7"},
      {"W-L", "c", "C", "^", "6", "😎", "F6"},
      {"N-B", "d", "D", "\"", "DOC", "Δ", "DEL"},
      {"W", "e", "E", "=", "NUM", "€", "LEFT"},
      {"W-B", "f", "F", null, "8", null, "F8"},
      {"NW-R", "g", "G", "`", null, "º", null},
      {"E-L", "h", "H", "#", "-", "—", null},
      {"N", "i", "I", null, "PASTE", null, "UP"},
      {"S-R", "j", "J", null, "9", null, "F7"},
      {"SW-L", "k", "K", "@", "2", "²", "F2"},
      {"S-L", "l", "L", "<", "4", "☺", "F4"},
      {"NW-B", "m", "M", ">", "MIC", null, null},
      {"NW", "n", "N", null, "TOP-L", "ñ", "HOME"},
      {"NE-R", "o", "O", "|", "0", "ö", "F10"},
      {"N-R", "p", "P", "%", "PUSH", "§", null},
      {"N-L", "q", "Q", "'", null, null, "BREAK"},
      {"SE", "r", "R", null, "BOT-R", "♥", "PGDOWN"},
      {"SW", "s", "S", "$", "BOT-L", "ß", "END"},
      {"S", "t", "T", "~", "QUIT", "👍", "DOWN"},
      {"S-B", "u", "U", "_", "1", "ü", "F1"},
      {"SE-L", "v", "V", null, "VERS", null, "F11"},
      {"SE-B", "w", "W", null, "WIPE", null, "F12"},
      {"SE-R", "x", "X", null, null, null, null},
      {"NE-L", "y", "Y", null, "*", null, null},
      {"E-B", "z", "Z", null, "3", "³", "F3"},
      {"SW-R", ",", ",", ";", "/", null, "INS"},
      {"NW-L", ".", ".", ":", ".", null, null},
      {"W-R", "?", "?", "!", "+", "±", null},
      {"SW-B", "(", "{", "[", "5", "👉", "F5"},
      {"NE-B", ")", "}", "]", "UPC", "👈", null}
   };

   public StenoView(Context context, AttributeSet attrs) {
      super(context, attrs);
      Text1.setColor(Color.BLACK);
      Text1.setStyle(Paint.Style.STROKE);
      Text1.setTextAlign(Paint.Align.CENTER);
      Text2.setColor(Color.WHITE);
      Text2.setTextAlign(Paint.Align.CENTER);
      for (int i = 0;  i < Lines.length;  ++i) {
         Lines[i] = new Paint();
         Lines[i].setColor(Colors[i]);
         Lines[i].setStyle(Paint.Style.STROKE);
         Lines[i].setPathEffect(new DashPathEffect(new float[]{0, i * DOT*2, DOT*2, (Lines.length-1 - i) * DOT*2}, 0));
         Lines[i].setStrokeWidth(DOT);
      }
      Max = getResources().getDisplayMetrics().densityDpi * 0.5f;  // 0.5 inch
      DictFile = new File(context.getFilesDir().getPath() + "Dict");
   }

   @Override public boolean onTouchEvent(MotionEvent ev) {
      int i;
      float x, y;

      switch (ev.getActionMasked()) {
      case MotionEvent.ACTION_DOWN:
         x = ev.getX();
         y = ev.getY();
         ActTime = ev.getEventTime();

         Vis = 0;
         Shift = Punct = Digit = Cntrl = AltGr = Funct = false;
         if (Candidates[0] != null  &&  y < CandY) {
            for (i = 0;  i < CANDIDATES;  ++i)
               if (x < CandX[i]) {
                  String s = Candidates[i];
                  int c = s.codePointAt(0);

                  hapt();
                  Repeat3 = Repeat2;
                  Repeat2 = Repeat;
                  Vis = Repeat = c;
                  text(s);
                  break;
               }
            for (i = 0;  i < CANDIDATES;  ++i)
               Candidates[i] = null;
         }
         else {
            Beg = true;
            BegX = x;
            BegY = y;

            if (x < OrgX - Size/2  ||  x >= OrgX + Size * 7/2  ||  y < OrgY - Size/2  ||  y >= OrgY + Size * 7/2) {
               Off = true;
               if (Help == null)
                  Ime.setCandidatesViewShown(false);
            }
            else if (Rpt == 0  &&  x >= OrgX  &&  x < OrgX + Size * 3  &&  y >= OrgY  &&  y < OrgY + Size * 3) {
               (new Thread() {
                  public void run() {
                     int r = Rpt = ++RptN;
                     float bx = BegX - OrgX;
                     float by = BegY - OrgY;

                     try {
                        sleep(4 * DLY);
                        if (Rpt == r) {
                           Rpt = r = -r;
                           do {
                              tap(bx, by);
                              sleep(DLY);
                           } while (Rpt == r);
                        }
                     }
                     catch (InterruptedException e) {}
                  }
               } ).start();
            }
         }
         break;
      case MotionEvent.ACTION_POINTER_DOWN:
         reset();
         break;
      case MotionEvent.ACTION_MOVE:
         if (Beg) {
            x = (MX = ev.getX()) - BegX;
            y = (MY = ev.getY()) - BegY;
            if (Off) {
               if (ActTime < 0  ||  ev.getEventTime() - ActTime < MOV) {
                  if (ActTime == -1  ||  BegX >= PadX  &&  BegX < PadX + Size * 3  &&  BegY >= PadY  &&  BegY < PadY + Size * 3) {
                     if (Math.abs(x) >= Math.abs(y)) {
                        if (x <= -Size/8  ||  x >= Size/8) {
                           send(x > 0? -KeyEvent.KEYCODE_DPAD_RIGHT : -KeyEvent.KEYCODE_DPAD_LEFT);
                           ActTime = -1;
                           BegX += x;
                        }
                        BegY += y;
                     }
                     else {
                        if (y <= -Size/8  ||  y >= Size/8) {
                           send(y > 0? -KeyEvent.KEYCODE_DPAD_DOWN : -KeyEvent.KEYCODE_DPAD_UP);
                           ActTime = -1;
                           BegY += y;
                        }
                        BegX += x;
                     }
                  }
                  else if (Settings.System.canWrite(Ime)) {
                     if (Math.abs(x) >= Math.abs(y)) {
                        if (x <= -Size/2  ||  x >= Size/2) {
                           hapt();
                           ((AudioManager)Ime.getSystemService(Context.AUDIO_SERVICE)).adjustVolume(x > 0? 1 : -1, 0);
                           ActTime = -2;
                           BegX += x;
                        }
                        BegY += y;
                     }
                     else {
                        if (y <= -Size/2  ||  y >= Size/2) {
                           hapt();
                           ContentResolver cr = Ime.getContentResolver();
                           if (ActTime > 0) {
                              ActTime = -1;
                              Settings.System.putInt(cr,
                                 Settings.System.SCREEN_BRIGHTNESS_MODE,
                                 Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL );
                           }
                           Settings.System.putInt(cr,
                              Settings.System.SCREEN_BRIGHTNESS,
                              Math.max(1,
                                 Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS, 127) +
                                 (int)(y > 0? ActTime : -ActTime) ) );
                           if (ActTime > -64)
                              ActTime *= 2;
                           BegY += y;
                        }
                        BegX += x;
                     }
                  }
               }
            }
            else {
               float px = x;
               float py = y;
               float sx = 1;
               float sy = 1;
               double mv;
               int d;

               if (x < 0) {
                  px = -px;
                  sx = -1;
               }
               if (y < 0) {
                  py = -py;
                  sy = -1;
               }
               if (px >= py) {
                  if (py < px/2)
                     py = 0;
                  else
                     px = py = (px + py) / 2;
               }
               else {
                  if (px < py/2)
                     px = 0;
                  else
                     px = py = (px + py) / 2;
               }
               px *= sx;
               py *= sy;
               if (Dir == 0) {
                 if ((mv = dist(x, y)) > Size/8) {
                     PX = px;
                     PY = py;
                     if (Rpt > 0)
                        Rpt = 0;
                     if (Rpt == 0  &&  mv >= Size / 2) {
                        hapt();
                        Dir = dir(px, py);
                     }
                  }
               }
               else if (dist(x-PX, y-PY) >= Math.max(Size, dist(x, y)) / 3  &&  (d = dir(x-PX, y-PY)) != Dir) {
                  hapt();
                  stroke(Strokes[Dir << 3 | d - 1] - 1);
                  Beg = false;
                  Dir = 0;
               }
               else if (dir(px, py) == Dir  &&  dist(px, py) > dist(PX, PY)) {
                  PX = px;
                  PY = py;
               }
               x = BegX + PX / 2 - OrgX;
               y = BegY + PY / 2 - OrgY;
               Shift = Punct = Digit = Cntrl = AltGr = Funct = false;
               if (x < Size) {
                  if (y < Size)
                     Punct = true;
                  else if (y >= Size * 2)
                     Cntrl = true;
                  else
                     Punct = Cntrl = true;
               }
               else if (x >= Size * 2) {
                  if (y < Size)
                     Shift = true;
                  else if (y >= Size * 2)
                     AltGr = true;
                  else
                     Shift = AltGr = true;
               }
               else if (y < Size)
                  Digit = true;
               else if (y >= Size * 2)
                  Funct = true;
            }
         }
         break;
      case MotionEvent.ACTION_UP:
         if (Off)
            reset();
         else if (Beg) {
            Rpt = 0;
            if (Dir != 0)
               stroke(Strokes[Dir-1] - 1);
            else if (dist(PX, PY) <= Size/20  &&  (BegX -= OrgX) >= 0  &&  BegX < Size * 3  &&  (BegY -= OrgY) >= 0  &&  BegY < Size * 3) {
               hapt();
               if ((ev.getEventTime() - TapTime) > TAP  ||  dist(TapX-BegX, TapY-BegY) > Size/3)
                  tap(BegX, BegY);
               else
                  tap(Size * 3/2, Size * 3/2);
               TapTime = ev.getEventTime();
            }
         }
         else if (Mic != null) {
            Mic.destroy();
            Mic = null;
         }
         Beg = false;
         PX = PY = 0;
         Dir = 0;
         break;
      case MotionEvent.ACTION_CANCEL:
         reset();
         break;
      }
      postInvalidate();
      return true;
   }

   double dist(float dx, float dy) {
      return Math.sqrt(dx * dx + dy * dy);
   }

   int dir(float dx, float dy) {
      int d, q = 0;

      if (dx < 0) {
         dx = -dx;
         q = 4;
      }
      if (dy < 0) {
         dy = -dy;
         q |= 8;
      }
      if (dx >= dy) {
         if (dy < dx/2)
            d = 0;
         else
            d = 1;
      }
      else {
         if (dx < dy/2)
            d = 2;
         else
            d = 1;
      }
      return Dirs[d | q];
   }

   void stroke(int c) {
      if (Num >= 0)
         c = StenoDigit[c];
      else if (Digit)
         c = StenoDigit[c];
      else if (Shift) {
         switch (c = AltGr? StenoAltGr[c] : Steno[c]) {
         case '(': c = '{'; break;
         case ')': c = '}'; break;
         default:
            c = Character.toUpperCase(c);
         }
      }
      else if (Punct) {
         c = StenoPunct[c];
         if (Cntrl) {
            if (c >= '['  &&  c <= '_')
               c &= 0x1F;
            else
               c = 0;
         }
      }
      else if (Funct)
         c = StenoFunct[c];
      else if (AltGr)
         c = StenoAltGr[c];
      else if (Cntrl) {
         if ((c = Steno[c]) == 32)
            c = 0x100000;
         else if (c >= 'a'  &&  c <= 'z')
            c &= 0x1F;
         else if (c == ',')
            c = 0x100011;  // ^B DOWN
         else if (c == '.')
            c = 0x100012;  // ^B UP
         else if (c == '?')
            c = 0x100013;  // ^B [ 0 PGUP
         else if (c == '(')
            c = 0x100014;  // ^B p
         else if (c == ')')
            c = 0x100015;  // ^B n
         else
            c = 0;
      }
      else
         c = Steno[c];
      send(c);
   }

   void tap(float x, float y) {
      TapX = TapY = 0;
      if (x < Size) {
         if (y < Size)
            send1(Repeat3);
         else if (y >= Size * 2)
            send1(Repeat2);
         else
            send(-KeyEvent.KEYCODE_DEL);
      }
      else if (x >= Size * 2) {
         if (y < Size) {
            send1(Repeat3);
            dly();
            send1(Repeat2);
            dly();
            send1(Repeat);
         }
         else if (y >= Size * 2) {
            send1(Repeat2);
            dly();
            send1(Repeat);
         }
         else
            send(-KeyEvent.KEYCODE_TAB);
      }
      else if (y < Size)
         send(-KeyEvent.KEYCODE_ESCAPE);
      else if (y >= Size * 2)
         send(-KeyEvent.KEYCODE_ENTER);
      else {
         send1(Repeat);
         TapX = BegX;
         TapY = BegY;
      }
   }

   void send(int c) {
      if (c != 0  &&  c < 0x100000) {
         Repeat3 = Repeat2;
         Repeat2 = Repeat;
         Repeat = c;
      }
      send1(c);
   }

   void send1(int c) {
      if (Candidates[0] != null) {
         int i, j = 1;

         switch (c) {
         case 0x100000:  // CNTRL-SPACE
            Candidates[0] = null;
            break;
         case 0x100006:  // PASTE
            String s = clipboard();

            if (s.length() != 0) {
               if (Candidates[0].length() != 0)
                  s = Candidates[0] + '\t' + s;
               try {
                  PrintWriter out = new PrintWriter(DictFile);

                  out.println(Dict.length + 1);
                  for (i = 0;  i < Dict.length  &&  Dict[i].compareTo(s) < 0;  ++i)
                     out.println(Dict[i]);
                  out.println(s);
                  while (i < Dict.length)
                     out.println(Dict[i++]);
                  out.close();
                  readDict(new FileReader(DictFile));
               }
               catch (IOException e) {}
            }
            Candidates[0] = null;
            break;
         case -KeyEvent.KEYCODE_FORWARD_DEL:
            String s0, s1;

            if ((s0 = Candidates[0]).length() != 0  &&  (s1 = Candidates[CandPos]).length() != 0) {
               try {
                  PrintWriter out = new PrintWriter(DictFile);

                  out.println(Dict.length - 1);
                  for (i = 0;  i < Dict.length;  ++i) {
                     if (Dict[i].startsWith(s0)) {
                        int ix = Dict[i].indexOf('\t');

                        if (s1.equals(ix < 0? Dict[i] : Dict[i].substring(ix + 1)))
                           break;
                     }
                     out.println(Dict[i]);
                  }
                  while (++i < Dict.length)
                     out.println(Dict[i]);
                  out.close();
                  readDict(new FileReader(DictFile));
               }
               catch (IOException e) {}
            }
            Candidates[0] = null;
            break;
         case -KeyEvent.KEYCODE_ESCAPE:
            if (Candidates[0].length() == 0) {
               Ime.sendDownUpKeyEvents(KeyEvent.KEYCODE_ESCAPE);
               return;
            }
            text(Candidates[0]);
            Candidates[0] = null;
            break;
         case -KeyEvent.KEYCODE_ENTER:
            if (Candidates[0].length() == 0) {
               Ime.sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER);
               return;
            }
            if (Candidates[CandPos] != null)
               text(Candidates[CandPos]);
            Candidates[0] = null;
            break;
         default:
            if (c == -KeyEvent.KEYCODE_TAB) {
               if (Candidates[0].length() == 0) {
                  Ime.sendDownUpKeyEvents(KeyEvent.KEYCODE_TAB);
                  return;
               }
               if (CandPos < CANDIDATES - 1  &&  Candidates[CandPos + 1] != null)
                  ++CandPos;
            }
            else if (c == -KeyEvent.KEYCODE_DEL) {
               if ((i = Candidates[0].length()) == 0) {
                  Ime.sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
                  return;
               }
               if (CandPos > 1)
                  --CandPos;
               else
                  Candidates[0] = Candidates[0].substring(0, i-1);
            }
            else if (Candidates[0].length() == 0  &&  c == 32) {
               Ime.sendDownUpKeyEvents(KeyEvent.KEYCODE_SPACE);
               return;
            }
            else if (c >= 32  &&  c < 0x100000)
               Candidates[0] = Candidates[0] + (char)c;
            if (Candidates[0].length() > 0) {
               int a = 0;
               int z = Dict.length - 1;

               while (a <= z) {
                  i = (a + z) / 2;
                  if (Dict[i].startsWith(Candidates[0])) {
                     while (i > 0  &&  Dict[i-1].startsWith(Candidates[0]))
                        --i;
                     do {
                        int ix = Dict[i].indexOf('\t');

                        Candidates[j++] = ix < 0? Dict[i] : Dict[i].substring(ix + 1);
                     } while (j < CANDIDATES  &&  ++i < Dict.length  &&  Dict[i].startsWith(Candidates[0]));
                     break;
                  }
                  if (Dict[i].compareTo(Candidates[0]) > 0)
                     z = i - 1;
                  else
                     a = i + 1;
               }
            }
         }
         while (j < CANDIDATES)
            Candidates[j++] = null;
         return;
      }
      if (Num >= 0) {
         if (c == -KeyEvent.KEYCODE_DEL) {
            Num /= 10;
            return;
         }
         if (c >= '0'  &&  c <= '9') {
            Num = Num * 10 + c - '0';
            return;
         }
         if (c == -KeyEvent.KEYCODE_ESCAPE) {
            Num = -1;
            return;
         }
         else if (c != -KeyEvent.KEYCODE_TAB  &&  c != -KeyEvent.KEYCODE_ENTER)
            return;
         Repeat = c = Num;
         Num = -1;
      }
      if ((Vis = c) < 0)
         Ime.sendDownUpKeyEvents(-c);
      else if (c > 0  &&  c < 0x100000)
         text((new StringBuffer()).appendCodePoint(c).toString());
      else {
         switch (c) {
         case 0x100000:  // CNTRL-SPACE
            if (Dict == null) {
               try {
                  readDict(
                     DictFile.exists()?
                        new FileReader(DictFile) :
                        new InputStreamReader(
                           getResources().openRawResource(R.raw.dict),
                           StandardCharsets.UTF_8 ) );
               }
               catch (IOException e) {Dict = null;}
            }
            Candidates[0] = "";
            CandPos = 1;
            break;
         case 0x100001:  // BOT-R
            Pos = 0;
            return;
         case 0x100002:  // QUIT
            Ime.requestHideSelf(0);
            break;
         case 0x100003:  // BOT-L
            Pos = 1;
            return;
         case 0x100004:  // NUM
            Num = 0;
            break;
         case 0x100005:  // TOP-L
            Pos = 2;
            return;
         case 0x100006:  // PASTE
            text(clipboard());
            break;
         case 0x100007:  // TOP-R
            Pos = 3;
            return;
         case 0x100009:  // WIPE
            while (Wipe > 0) {
               Ime.sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
               --Wipe;
            }
            break;
         case 0x10000A:  // MIC
            if (SpeechRecognizer.isRecognitionAvailable(Ime)) {
               (Mic = SpeechRecognizer.createSpeechRecognizer(Ime)).setRecognitionListener(this);
               Intent ri = new Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE);
               ri.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
               ri.putExtra(RecognizerIntent.EXTRA_ENABLE_LANGUAGE_SWITCH, RecognizerIntent.LANGUAGE_SWITCH_HIGH_PRECISION);
               Mic.startListening(ri);
            }
            break;
         case 0x10000B:  // PUSH
            ClipboardManager cm = (ClipboardManager)Ime.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null  &&  cm.hasPrimaryClip())
               Paste.push(cm.getPrimaryClip().getItemAt(0).coerceToText(getContext()).toString());
            break;
         case 0x10000C:  // DOC
            if (Help == null) {
               Help = new Paint();
               setBackgroundResource(R.drawable.help);
               Help.setTextAlign(Paint.Align.CENTER);
               return;
            }
            break;
         case 0x10000D:  // UPC
            Upc = true;
            break;
         case 0x100011:  // ^B DOWN
            send('\002');
            dly();
            send(-KeyEvent.KEYCODE_DPAD_DOWN);
            break;
         case 0x100012:  // ^B [ 0 UP
            send('\002');
            dly();
            send('[');
            dly();
            send('0');
            dly();
            send(-KeyEvent.KEYCODE_DPAD_UP);
            break;
         case 0x100013:  // ^B [ 0 PGUP
            send('\002');
            dly();
            send('[');
            dly();
            send('0');
            dly();
            send(-KeyEvent.KEYCODE_PAGE_UP);
            break;
         case 0x100014:  // ^B p
            send('\002');
            dly();
            send('p');
            break;
         case 0x100015:  // ^B n
            send('\002');
            dly();
            send('n');
            break;
         }
      }
      if (Help != null) {
         setBackgroundResource(0);
         Help = null;
      }
   }

   String clipboard() {
      ClipboardManager cm = (ClipboardManager)Ime.getSystemService(Context.CLIPBOARD_SERVICE);

      if (!Paste.isEmpty())
         return Paste.removeLast();
      if (cm == null  ||  !cm.hasPrimaryClip())
         return "";
      return cm.getPrimaryClip().getItemAt(0).coerceToText(getContext()).toString();
   }

   void text(String s) {
      if (Upc) {
         s = s.substring(0,1).toUpperCase() + s.substring(1);
         Upc = false;
      }
      Ime.getCurrentInputConnection().commitText(s,1);
      Wipe = s.codePointCount(0, s.length());
   }

   void dotLine(Canvas canvas, float x1, float y1, float x2, float y2) {
      for (int i = 0;  i < Lines.length;  ++i)
         canvas.drawLine(x1, y1, x2, y2, Lines[i]);
   }

   @Override protected void onDraw(Canvas canvas) {
      String s;

      super.onDraw(canvas);
      Width = canvas.getWidth();
      Height = canvas.getHeight();
      if (Help != null) {
         int c, pos[] = {12, 24, 36, 48, 60, 72, 84};
         float n = Height / (StenoHelp.length + 2);

         Help.setTextSize(n * 3 / 4);
         Help.setColor(Color.BLUE);
         Help.setTypeface(Typeface.SERIF);
         for (int col = 0; col < StenoHelp1.length; ++col)
            canvas.drawText(StenoHelp1[col], Width/100 * pos[col], n, Help);
         Help.setTypeface(Typeface.MONOSPACE);
         Help.setColor(Color.BLACK);
         for (int row = 0; row < StenoHelp.length; ++row)
            for (int col = 0; col < StenoHelp[row].length; ++col)
               if (StenoHelp[row][col] != null)
                  canvas.drawText(StenoHelp[row][col], Width/100 * pos[col], n + (row+1) * n, Help);
      }
      else if ((s = Candidates[0]) != null) {
         Paint p = new Paint();
         float x, y, n;
         int i;

         p.setColor(Color.rgb(0xFF, 0xD5, 0x88));
         for (i = 0;  i < CANDIDATES;  ++i)
            CandX[i] = Width;
         y = (Width + Height) / 2;
         canvas.drawRect(0, 0, Width, CandY = y/16, p);
         p.setTypeface(Typeface.SERIF);
         p.setColor(Color.BLACK);
         p.setTextSize(y/20);
         canvas.drawText(s, x = 6, CandY*4/5, p);
         p.setStrokeWidth(4);
         n = p.measureText(s);
         for (i = 1; i < CandPos; ++i)
            CandX[i-1] = 6 + n + 12;
         for (i = CandPos;  i < CANDIDATES;  ++i) {
            if ((s = Candidates[i]) == null  ||  (x += n + 12) >= Width)
               break;
            CandX[i-1] = x;
            canvas.drawLine(x, 0, x, CandY, p);
            canvas.drawText(s, x += 12, CandY*4/5, p);
            if (x + (n = p.measureText(s)) >= Width)
               break;
         }
      }
      Size = Math.min(Max, Math.min(Width, Height) / 5);
      switch (Pos) {
      case 0:  // SE
         OrgX = Width - Size * 7/2;
         OrgY = Height - Size * 4;
         break;
      case 1:  // SW
         OrgX = Size / 2;
         OrgY = Height - Size * 4;
         break;
      case 2:  // NW
         OrgX = Size / 2;
         OrgY = Size / 2;
         break;
      case 3:  // NE
         OrgX = Width - Size * 7/2;
         OrgY = Size / 2;
         break;
      }
      if (Height >= Size * 15/2) {
         PadX = OrgX;
         PadY = OrgY + Size * 7 / (Pos >= 2? 2 : -2);
      }
      else {
         PadX = OrgX + Size * 7 / (Pos == 1 || Pos == 2? 2 : -2);
         PadY = OrgY;
      }
      if (!Off) {
         float x = OrgX + Size;
         float y = OrgY + Size;

         dotLine(canvas, OrgX, y, x + Size * 2, y);
         dotLine(canvas, OrgX, y + Size, x + Size * 2, y + Size);
         dotLine(canvas, x, OrgY, x, y + Size * 2);
         dotLine(canvas, x + Size, OrgY, x + Size, y + Size * 2);
         if (Dir != 0) {
            dotLine(canvas, BegX, BegY, BegX + PX, BegY + PY);
            dotLine(canvas, BegX + PX, BegY + PY, MX, MY);
         }
         if (Mic != null)
            s = "MIC";
         else if (Num >= 0)
            s = "<" + Num + ">";
         else if (Vis < 0) {
            s = KeyEvent.keyCodeToString(-Vis).substring(8);
            if (s.startsWith("DPAD_")  ||  s.startsWith("MOVE_"))
               s = s.substring(5);
            else if (s.startsWith("PAGE_"))
               s = "PG" + s.substring(5);
            else if (s.equals("FORWARD_DEL"))
               s = "DEL";
            else if (s.equals("ESCAPE"))
               s = "ESC";
            else if (s.equals("INSERT"))
               s = "INS";
            else if (s.equals("ENTER"))
               s = "RET";
            else if (s.equals("DEL"))
               s = "BS";
         }
         else if (Vis > 0  &&  Vis < 32)
            s = "^" + (char)(Vis + 64);
         else if (Vis == 32)
            s = "˻˼";
         else if (Vis > 32  &&  Vis < 0x100000)
            s = Character.toString(Vis);
         else if (Vis == 0x100006)
            s = "PASTE";
         else if (Vis == 0x100008)
            s = R.version;
         else if (Vis == 0x100009)
            s = "WIPE";
         else if (Vis == 0x10000B)
            s = "PUSH";
         else if (Vis == 0x10000D)
            s = "UPC";
         else
            s = null;
         if (s != null) {
            Text1.setTextSize(Size / 3);
            Text2.setTextSize(Size / 3);
            Text1.setStrokeWidth(Size / 4 / 8);
            canvas.drawText(s, OrgX + Size * 3/2, OrgY + Size / 3, Text1);
            canvas.drawText(s, OrgX + Size * 3/2, OrgY + Size / 3, Text2);
         }
      }
   }

   void hapt() {
      performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
   }

   static void dly() {
      try {Thread.currentThread().sleep(DLY);}
      catch (InterruptedException e) {}
   }

   void reset() {
      Dir = Rpt = 0;
      Num = -1;
      Beg = false;
      if (Off) {
         Off = false;
         Ime.setCandidatesViewShown(true);
      }
   }

   void readDict(InputStreamReader in) throws IOException {
      BufferedReader rd = new BufferedReader(in);

      Dict = new String[Integer.parseInt(rd.readLine())];
      for (int i = 0;  i < Dict.length;  ++i)
         Dict[i] = rd.readLine();
      rd.close();
   }

   public void onBeginningOfSpeech() {}

   public void onBufferReceived(byte[] buf) {}

   public void onEndOfSpeech() {}

   public void onError(int err) {}

   public void onEvent(int type, Bundle par) {}

   public void onPartialResults(Bundle res) {}

   public void onReadyForSpeech(Bundle par) {}

   public void onResults(Bundle res) {
      ArrayList<String> txt = res.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

      for (int i = 0; i < txt.size(); ++i)
         text(txt.get(i));
   }

   public void onRmsChanged(float rms) {}
}
