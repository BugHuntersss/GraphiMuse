package com.example.covbackend.util;

import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DateUtil {
    private ArrayList<Date> dates;
    DateUtil() {
        dates = new ArrayList<>();
        long oneDayTime = 24 * 3600 * 1000;
        long baseDayTime = 1579449600000L; // 2020.01.20
        long endDayTime = 1627747200000L; // 2021.08.01
        for (long i = baseDayTime; i < endDayTime; i+=oneDayTime) {
            dates.add(new Date(i));
        }
    }

    public ArrayList<Date> getDates() {
        return dates;
    }

    public void addDatetime(List<Map<String, Object>> maps){
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
//        if (maps.size() == 0) return;
//        LocalDate localDate = LocalDate.parse(maps.get(0).get("y").toString()+"."+maps.get(0).get("date").toString(),formatter);
//        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
//        long baseDayTime = Date.from(zonedDateTime.toInstant()).getTime();
//        int baseIndex = 0;
//        for (int i = 0; i < dates.size(); i++) {
//            if(dates.get(i).getTime()==baseDayTime) {
//                baseIndex = i;
//                break;
//            }
//        }
        for (int i = 0; i< maps.size(); i++){
            maps.get(i).put("datetime", maps.get(i).get("y").toString()+"."+maps.get(i).get("date").toString());
        }
    }

}
