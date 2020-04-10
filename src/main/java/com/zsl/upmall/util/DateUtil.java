package com.zsl.upmall.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static  void main(String args[]) {
    }

    public static String DateToString(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(date);

    }

}
