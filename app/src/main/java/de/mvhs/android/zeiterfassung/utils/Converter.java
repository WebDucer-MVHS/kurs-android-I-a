package de.mvhs.android.zeiterfassung.utils;

import java.text.DateFormat;
import java.util.Date;

public class Converter {
   private final static DateFormat _DATE_TO_DATE_TIME_STRING = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

   private final static DateFormat _DATE_TO_DATE_STRING = DateFormat.getDateInstance(DateFormat.SHORT);

   private final static DateFormat _DATE_TO_TIME_STRING = DateFormat.getTimeInstance(DateFormat.SHORT);

   /**
    * Konvertierung des DateTime Objektes zu einen Text
    */
   public static String toDateTimeString(Date date){
      if(date == null){
         date = new Date();
      }
      return _DATE_TO_DATE_TIME_STRING.format(date);
   }

   /**
    * Konvertierung des DateTime Objektes zu einen Datum-Text
    */
   public static String toTimeString(Date date){
      if(date == null){
         date = new Date();
      }
      return _DATE_TO_TIME_STRING.format(date);
   }

   /**
    * Konvertierung des DateTime Objektes zu einen Zeit-Text
    */
   public static String toDateString(Date date){
      if(date == null){
         date = new Date();
      }
      return _DATE_TO_DATE_STRING.format(date);
   }
}
