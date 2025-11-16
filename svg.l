# 29aug24 Software Lab. Alexander Burger

(symbols '(svg pico))

(local) (CHAR STROKE steno plain right left back fields)

(setq CHAR "#AAA"  STROKE "#000")

(de steno (Chr X Y S . Prg)
   (window X Y 1 1
      (scale S S
         (rect 0 0 22 22)
         (window 11 0 1 1
            (font 16 (ps 0 (cons 'C CHAR) Chr '(C))) )
         (run Prg) ) ) )

(de plain (X Y S A)
   (window X Y 1 1
      (rotate A
         (scale S S
            (circle 0 0 "1.5" STROKE)
            (polygon STROKE
               0 "-0.6"  10 "-0.6"
               "9.4" "-2.4"  "13.4" 0  "9.4" "2.4"
               10 "0.6"  0 "0.6" ) ) ) ) )

(de right (X Y S A)
   (window X Y 1 1
      (rotate A
         (scale S S
            (circle 0 0 "1.5" STROKE)
            (polygon STROKE
               0 "-0.6"  "11.2" "-0.6"
               "11.2" "4.8" "13.0" "4.2"
               "10.6" "9.2" "8.2" "4.2"
               10 "4.8"  10 "0.6"  0 "0.6" ) ) ) ) )

(de left (X Y S A)
   (window X Y 1 1
      (rotate A
         (scale S S
            (circle 0 0 "1.5" STROKE)
            (polygon STROKE
               0 "0.6"  "11.2" "0.6"
               "11.2" "-4.8" "13.0" "-4.2"
               "10.6" "-9.2" "8.2" "-4.2"
               10 "-4.8"  10 "-0.6"  0 "-0.6" ) ) ) ) )

(de back (X Y S A)
   (window X Y 1 1
      (rotate A
         (scale S S
            (circle 0 0 "1.5" STROKE)
            (polygon STROKE
               0 "-0.6"  "11.8" "-0.6"
               "11.8" "0.6"  0 "0.6" )
            (circle "11.8" 0 "0.6" STROKE)
            (polygon STROKE
               3 0  8 "-2.4"  "7.4" "-0.6"
               "7.4" "0.6"  8 "2.4" ) ) ) ) )

(de fields (X Y . @)
   (window X Y 1 1
      (polyline STROKE 0 30  90 30)
      (polyline STROKE 0 60  90 60)
      (polyline STROKE 30 0  30 90)
      (polyline STROKE 60 0  60 90)
      (window 0 7 30 30 (ps 0 (next)))
      (window 30 7 30 30 (ps 0 (next)))
      (window 60 7 30 30 (ps 0 (next)))
      (window 0 37 30 30 (ps 0 (next)))
      (window 30 37 30 30 (ps 0 (next)))
      (window 60 37 30 30 (ps 0 (next)))
      (window 0 67 30 30 (ps 0 (next)))
      (window 30 67 30 30 (ps 0 (next)))
      (window 60 67 30 30 (ps 0 (next))) ) )

(local) (stenoSP stenoA stenoB stenoC stenoD stenoE stenoF stenoG stenoH stenoI
stenoJ stenoK stenoL stenoM stenoN stenoO stenoP stenoQ stenoR stenoS stenoT
stenoU stenoV stenoW stenoX stenoY stenoZ stenoComma stenoDot stenoQuest
stenoOpen stenoClose steno0 steno1 steno2 steno3 steno4 steno5 steno6 steno7
steno8 steno9)

(de stenoSP (X Y S)
   (steno " " X Y S
      (plain 6 11 "1.0" 0) ) )

(de stenoA (X Y S)
   (steno "A" X Y S
      (plain 7 15 "1.1" -45) ) )

(de stenoB (X Y S)
   (steno "B" X Y S
      (right 7 7 "0.8" 0) ) )

(de stenoC (X Y S)
   (steno "C" X Y S
      (left 16 7 "0.8" 180) ) )

(de stenoD (X Y S)
   (steno "D" X Y S
      (back 10 16 "0.9" -90) ) )

(de stenoE (X Y S)
   (steno "E" X Y S
      (plain 15 11 "0.8" 180) ) )

(de stenoF (X Y S)
   (steno "F" X Y S
      (back 15 11 "0.8" 180) ) )

(de stenoG (X Y S)
   (steno "G" X Y S
      (right 13 17 "1.0" -135) ) )

(de stenoH (X Y S)
   (steno "H" X Y S
      (left 7 11 "0.8" 0) ) )

(de stenoI (X Y S)
   (steno "I" X Y S
      (plain 12 16 "0.9" -90) ) )

(de stenoJ (X Y S)
   (steno "J" X Y S
      (right 12 7 "0.9" 90) ) )

(de stenoK (X Y S)
   (steno "K" X Y S
      (left 16 6 "0.9" 135) ) )

(de stenoL (X Y S)
   (steno "L" X Y S
      (left 10 5 "0.9" 90) ) )

(de stenoM (X Y S)
   (steno "M" X Y S
      (back 14 15 "1.0" -135) ) )

(de stenoN (X Y S)
   (steno "N" X Y S
      (plain 14 15 "1.0" -135) ) )

(de stenoO (X Y S)
   (steno "O" X Y S
      (right 6 10 "0.8" -45) ) )

(de stenoP (X Y S)
   (steno "P" X Y S
      (right 10 15 "0.9" -90) ) )

(de stenoQ (X Y S)
   (steno "Q" X Y S
      (left 13 15 "0.9" -90) ) )

(de stenoR (X Y S)
   (steno "R" X Y S
      (plain 9 8 "0.8" 45) ) )

(de stenoS (X Y S)
   (steno "S" X Y S
      (plain 15 7 "1.0" 135) ) )

(de stenoT (X Y S)
   (steno "T" X Y S
      (plain 12 5 "0.9" 90) ) )

(de stenoU (X Y S)
   (steno "U" X Y S
      (back 12 5 "0.9" 90) ) )

(de stenoV (X Y S)
   (steno "V" X Y S
      (left 6 8 "0.9" 45) ) )

(de stenoW (X Y S)
   (steno "W" X Y S
      (back 7 7 "0.9" 45) ) )

(de stenoX (X Y S)
   (steno "X" X Y S
      (right 9 5 "0.8" 45) ) )

(de stenoY (X Y S)
   (steno "Y" X Y S
      (left 8 15 "0.8" -45) ) )

(de stenoZ (X Y S)
   (steno "Z" X Y S
      (back 7 11 "0.8" 0) ) )

(de stenoComma (X Y S)
   (steno "," X Y S
      (right 15 8 "0.8" 135) ) )

(de stenoDot (X Y S)
   (steno "." X Y S
      (left 15 14 "0.8" -135) ) )

(de stenoQuest (X Y S)
   (steno "?" X Y S
      (right 16 14 "0.8" 180) ) )

(de stenoOpen (X Y S)
   (steno "(" X Y S
      (back 14 7 "0.8" 135) ) )

(de stenoClose (X Y S)
   (steno ")" X Y S
      (back 8 15 "0.8" -45) ) )

(de steno0 (X Y S)
   (steno "0" X Y S
      (right 6 10 "0.8" -45) ) )

(de steno1 (X Y S)
   (steno "1" X Y S
      (back 12 5 "0.9" 90) ) )

(de steno2 (X Y S)
   (steno "2" X Y S
      (left 16 6 "0.9" 135) ) )

(de steno3 (X Y S)
   (steno "3" X Y S
      (back 7 11 "0.8" 0) ) )

(de steno4 (X Y S)
   (steno "4" X Y S
      (left 10 5 "0.9" 90) ) )

(de steno5 (X Y S)
   (steno "5" X Y S
      (back 14 7 "0.8" 135) ) )

(de steno6 (X Y S)
   (steno "6" X Y S
      (left 16 7 "0.8" 180) ) )

(de steno7 (X Y S)
   (steno "7" X Y S
      (right 7 7 "0.8" 0) ) )

(de steno8 (X Y S)
   (steno "8" X Y S
      (back 15 11 "0.8" 180) ) )

(de steno9 (X Y S)
   (steno "9" X Y S
      (right 12 7 "0.9" 90) ) )
