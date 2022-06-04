package org.kylin.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalTime;

@UtilityClass
public class MyDateUtil {

    public LocalDate getLatestDrawDate(){
        LocalTime drawTime = LocalTime.of(21, 15);
        if(LocalTime.now().isBefore(drawTime)){
            return LocalDate.now().minusDays(1);
        }else{
            return LocalDate.now();
        }
    }

}
