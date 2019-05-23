import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Deserializer implements IMapper {

    private String offBinary;
    private int year;

    public Deserializer(String offBinary, int year) {
        this.offBinary = offBinary;
        this.year = year;
    }

    @Override
    public Map<LocalDate, OffMode> getYearMap() {
        return generateYearMap(offBinary,year);
    }

    public Map<LocalDate, OffMode> generateYearMap(String offBinary, int year) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String,String> stringMap = objectMapper.readValue(offBinary, new TypeReference<Map<String, String>>(){});
            return stringMapToProperMap(stringMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<LocalDate, OffMode> stringMapToProperMap(Map<String,String> stringMap)
    {
        Map<LocalDate, OffMode> properMap = new TreeMap<>();
        Holidays holidays = new Holidays(String.valueOf(year));

        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            LocalDate date = LocalDate.parse(entry.getKey());
            OffMode mode = setOffMode(entry.getValue());

            if (isHoliday(date, holidays)){
                mode = OffMode.HOLIDAY;
            }

            properMap.put(date,mode);
        }
        return properMap;
    }

    private OffMode setOffMode (String stringMode){
        if (stringMode.equals("WORKING")){
            return OffMode.WORKING;
        }
        else if (stringMode.equals("OFF")){
            return OffMode.OFF;
        }
        else if (stringMode.equals("HOLIDAY")){
            return OffMode.HOLIDAY;
        }
        else return OffMode.WORKING;
    }

    private Boolean isHoliday(LocalDate date, Holidays holidays) {

        if (holidays.isOnList(date)){
            return Boolean.TRUE;
        }

        if (date.getDayOfWeek().toString() == "SATURDAY" || date.getDayOfWeek().toString() == "SUNDAY") {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
}
