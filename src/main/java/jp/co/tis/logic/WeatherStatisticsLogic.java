package jp.co.tis.logic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.tis.form.WeatherStatisticsForm;
import jp.co.tis.model.Weather;
import jp.co.tis.model.WeatherDao;
import jp.co.tis.model.WeatherStatisticsDto;

/**
 * 天気統計Logicクラス。<br/>
 * コントローラーに直接メソッド切り出しを行うと行数が膨れるため、<br/>
 * 業務ロジック部分はロジッククラスに切り出す。<br/>
 * JUnitテストをしやすくするための目的もある。
 *
 * @author Saito Takuma
 * @since 1.0
 */
@Component
public class WeatherStatisticsLogic {

    /** DB操作DAO */
    @Autowired
    private WeatherDao weatherDao;

    /**
     * 入力項目をバリデーションする。
     *
     * @param form フォーム
     * @return エラーリスト
     */
    public List<String> validateForm(WeatherStatisticsForm form) {
        List<String> errorList = new ArrayList<String>();

        if (!StringUtils.isEmpty(form.getWeatherDate()) && !isValidDate(form.getWeatherDate())) {
            errorList.add("日付はMM/dd形式で入力してください。");
        }
        if (!StringUtils.isEmpty(form.getPlace()) && form.getPlace().length() > 10) {
            errorList.add("場所は10文字以内で入力してください。");
        }
        if (StringUtils.isEmpty(form.getWeatherDate()) || StringUtils.isEmpty(form.getPlace())) {
            errorList.add("日付と場所は、必ず両方入力してください。");
        }

        return errorList;
    }

    /**
     * 有効な日付かどうかを判定する。
     *
     * @param date 日付
     * @return 有効な日付の場合 True / 無効な日付の場合 false
     */
    private boolean isValidDate(String date) {
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        format.setLenient(false);
        try {
            // 2/29に対応するため、うるう年を基準にパースを行う。
            format.parse("2016/" + date);
        } catch (ParseException e) {
            return false;
        }
        if (!Pattern.compile("^(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])$").matcher(date).find()) {
            return false;
        }
        return true;
    }

    /**
     * 過去天気のリストを作成する。
     *
     * @param form フォーム
     * @return 過去天気のリスト
     */
    public List<Weather> createPastWeatherList(WeatherStatisticsForm form) {
        String selectSql = "SELECT * FROM WEATHER WHERE WEATHER_DATE LIKE :percentMonthAndDay AND PLACE = :place";
        Map<String, String> condition = new HashMap<String, String>();
        String percentMonthAndDay = "%" + form.getWeatherDate();
        condition.put("percentMonthAndDay", percentMonthAndDay);
        condition.put("place", form.getPlace());

        return weatherDao.findBySql(selectSql, condition);
    }

    /**
     * 天気統計のDtoを作成する。
     *
     * @param form フォーム
     * @param pastWeatherList 過去の天気のリスト
     * @return 天気統計のDto
     */
    public WeatherStatisticsDto createWeatherStatisticsDto(WeatherStatisticsForm form, List<Weather> pastWeatherList) {
        WeatherStatisticsDto statisticsWeather = new WeatherStatisticsDto();
        double sunnyCount = 0;
        double cloudyCount = 0;
        double rainyCount = 0;
        double snowCount = 0;
        int maxTemperatureSum = 0;
        int minTemperatureSum = 0;
        for (Weather pastWeather : pastWeatherList) {
            if ("晴れ".equals(pastWeather.getWeather())) {
                sunnyCount++;
            } else if ("曇り".equals(pastWeather.getWeather())) {
                cloudyCount++;
            } else if ("雨".equals(pastWeather.getWeather())) {
                rainyCount++;
            } else if ("雪".equals(pastWeather.getWeather())) {
                snowCount++;
            }
            maxTemperatureSum += Integer.parseInt(pastWeather.getMaxTemperature());
            minTemperatureSum += Integer.parseInt(pastWeather.getMinTemperature());
        }
        if (sunnyCount != 0) {
            Double percent = (sunnyCount / pastWeatherList.size()) * 100;
            statisticsWeather.setSunnyPercent(percent.intValue());
        }
        if (cloudyCount != 0) {
            Double percent = (cloudyCount / pastWeatherList.size()) * 100;
            statisticsWeather.setCloudyPercent(percent.intValue());
        }
        if (rainyCount != 0) {
            Double percent = (rainyCount / pastWeatherList.size()) * 100;
            statisticsWeather.setRainyPercent(percent.intValue());
        }
        if (snowCount != 0) {
            Double percent = (snowCount / pastWeatherList.size()) * 100;
            statisticsWeather.setSnowPercent(percent.intValue());
        }
        statisticsWeather.setMaxTemperatureAve(maxTemperatureSum / pastWeatherList.size());
        statisticsWeather.setMinTemperatureAve(minTemperatureSum / pastWeatherList.size());
        statisticsWeather.setWeatherDate(form.getWeatherDate());
        statisticsWeather.setPlace(form.getPlace());

        return statisticsWeather;
    }
}
