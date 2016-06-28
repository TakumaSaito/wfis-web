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
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.co.tis.form.WeatherSearchForm;
import jp.co.tis.model.Person;
import jp.co.tis.model.Weather;
import jp.co.tis.model.WeatherDao;

/**
 * 天気検索Logicクラス。<br/>
 * コントローラーに直接メソッド切り出しを行うと行数が膨れるため、<br/>
 * 業務ロジック部分はロジッククラスに切り出す。<br/>
 * JUnitテストをしやすくするための目的もある。
 *
 * @author Saito Takuma
 * @since 1.0
 */
@Component
public class WeatherSearchLogic {

    /** DB操作DAO */
    @Autowired
    private WeatherDao weatherDao;

    /**
     * 自己紹介を作成する。
     *
     * @return Person
     */
    public Person createPersonInfo() {
        Person person = new Person();
        person.setMyName("TIS 太郎");
        person.setAge("25");
        person.setHobby("読書・映画・ショッピング");
        person.setSkill("空手5段");

        return person;
    }

    /**
     * SQLから天気一覧を検索する。
     *
     * @return 検索結果
     */
    public List<Weather> findBySqlWeatherList() {
        String selectSql = "SELECT * FROM WEATHER";
        return weatherDao.findBySql(selectSql);
    }

    /**
     * SQLと条件から天気情報を検索する（天気簡易検索)。
     *
     * @param form フォーム
     * @return 検索結果
     */
    public List<Weather> findBySqlSimple(WeatherSearchForm form) {
        String selectSql = createSqlSimple(form);
        Map<String, String> condition = createConditionSimple(form);
        return weatherDao.findBySql(selectSql, condition);
    }

    /**
     * 検索に使用するSQLを作成する（天気簡易検索）。
     *
     * @param form フォーム
     * @return SQL
     */
    public String createSqlSimple(WeatherSearchForm form) {
        StringBuilder selectSql = new StringBuilder("SELECT * FROM WEATHER");
        if (!StringUtils.isEmpty(form.getPlace())) {
            selectSql.append(" WHERE PLACE = :place");
        }
        return selectSql.toString();
    }

    /**
     * 検索に使用する条件を作成する(天気簡易検索)。
     *
     * @param form フォーム
     * @return 検索結果
     */
    public Map<String, String> createConditionSimple(WeatherSearchForm form) {
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("place", form.getPlace());

        return condition;
    }

    /**
     * 入力項目をバリデーションする（天気検索）
     *
     * @param form フォーム
     * @return エラーリスト
     */
    public List<String> validateForm(WeatherSearchForm form) {
        List<String> errorList = new ArrayList<String>();

        if (!StringUtils.isEmpty(form.getWeatherDate()) && !isValidDate(form.getWeatherDate())) {
            errorList.add("日付は日付形式で入力してください。");
        }
        if (!StringUtils.isEmpty(form.getPlace()) && form.getPlace().length() > 10) {
            errorList.add("場所は10文字以内で入力してください。");
        }
        if (!StringUtils.isEmpty(form.getWeather()) && form.getWeather().length() > 10) {
            errorList.add("天気は10文字以内で入力してください。");
        }
        if (!StringUtils.isEmpty(form.getMaxTemperature()) && !NumberUtils.isNumber(form.getMaxTemperature())) {
            errorList.add("最高気温は数値で入力してください。");
        } else if (!StringUtils.isEmpty(form.getMaxTemperature())) {
            Integer maxTemperature = Integer.parseInt(form.getMaxTemperature());
            if (maxTemperature < -999 || maxTemperature > 999) {
                errorList.add("最高気温は3桁以内で入力してください。");
            }
        }
        if (!StringUtils.isEmpty(form.getMinTemperature()) && !NumberUtils.isNumber(form.getMinTemperature())) {
            errorList.add("最低気温は数値で入力してください。");
        } else if (!StringUtils.isEmpty(form.getMinTemperature())) {
            Integer mimTemperature = Integer.parseInt(form.getMinTemperature());
            if (mimTemperature < -999 || mimTemperature > 999) {
                errorList.add("最低気温は3桁以内で入力してください。");
            }
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
            format.parse(date);
        } catch (ParseException e) {
            return false;
        }
        // DateFormat#parse の場合、前方一致で正常な日付を判定する。
        // このため、次のようなケースも正常に変換できてしまう。→ 2016/01/01A
        // これを不正な入力値として判定するために追加チェックを行う。
        // 方法①：正規表現を用いた日付形式チェックを追加。
        if (!Pattern.compile("^([0-9]{4})/(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])$").matcher(date).find()) {
            return false;
        }
        // 方法②：Date型へパースした後に日付文字列に再変換した値と、元の日付文字列を比較することで、
        // 入力文字列が有効な日付形式であったかを確認する。
        // if (!date.equals((new
        // SimpleDateFormat("yyyy/MM/dd")).format(パースしたDate型オブジェクト))) {
        // return false;
        // }
        return true;
    }

    /**
     * 入力項目をバリデーションする（天気検索発展）。
     *
     * @param form フォーム
     * @return エラーリスト
     */
    public List<String> validateFormHard(WeatherSearchForm form) {
        List<String> errorList = new ArrayList<String>();

        if ((!StringUtils.isEmpty(form.getWeatherDateFrom()) && !isValidDate(form.getWeatherDateFrom()))
                || (!StringUtils.isEmpty(form.getWeatherDateTo()) && !isValidDate(form.getWeatherDateTo()))) {
            errorList.add("日付は日付形式で入力してください。");
        }
        if (!StringUtils.isEmpty(form.getPlace()) && form.getPlace().length() > 10) {
            errorList.add("場所は10文字以内で入力してください。");
        }
        if (!StringUtils.isEmpty(form.getWeather()) && form.getWeather().length() > 10) {
            errorList.add("天気は10文字以内で入力してください。");
        }
        if ((!StringUtils.isEmpty(form.getMaxTemperatureFrom()) && !NumberUtils.isNumber(form.getMaxTemperatureFrom()))
                || (!StringUtils.isEmpty(form.getMaxTemperatureTo()) && !NumberUtils.isNumber(form.getMaxTemperatureTo()))) {
            errorList.add("最高気温は数値で入力してください。");
        }
        if (!StringUtils.isEmpty(form.getMaxTemperatureFrom()) && NumberUtils.isNumber(form.getMaxTemperatureFrom())
                && !isValidTemperature(form.getMaxTemperatureFrom())) {
            errorList.add("最高気温は3桁以内で入力してください。");
        } else if (!StringUtils.isEmpty(form.getMaxTemperatureTo()) && NumberUtils.isNumber(form.getMaxTemperatureTo())
                && !isValidTemperature(form.getMaxTemperatureTo())) {
            errorList.add("最高気温は3桁以内で入力してください。");
        }
        if ((!StringUtils.isEmpty(form.getMinTemperatureFrom()) && !NumberUtils.isNumber(form.getMinTemperatureFrom()))
                || (!StringUtils.isEmpty(form.getMinTemperatureTo()) && !NumberUtils.isNumber(form.getMinTemperatureTo()))) {
            errorList.add("最低気温は数値で入力してください。");
        }
        if (!StringUtils.isEmpty(form.getMinTemperatureFrom()) && NumberUtils.isNumber(form.getMinTemperatureFrom())
                && !isValidTemperature(form.getMinTemperatureFrom())) {
            errorList.add("最低気温は3桁以内で入力してください。");
        } else if (!StringUtils.isEmpty(form.getMinTemperatureTo()) && NumberUtils.isNumber(form.getMinTemperatureTo())
                && !isValidTemperature(form.getMinTemperatureTo())) {
            errorList.add("最低気温は3桁以内で入力してください。");
        }

        return errorList;
    }

    /**
     * 有効な気温であるかを判定する。
     *
     * <pre>
     * 気温は -999 ～ 999 の間を有効(true)と判定する。
     * 本メソッドでは、未入力チェックおよび数値形式チェックは行わない。
     * </pre>
     *
     * @param temperature 気温
     * @return 有効な気温の場合 True / 無効な気温の場合 false
     */
    private boolean isValidTemperature(String temperature) {
        Integer result = Integer.parseInt(temperature);
        if (result < -999 || result > 999) {
            return false;
        }
        return true;
    }

    /**
     * 入力項目間のバリデーションする（天気検索発展）。
     *
     * @param form フォーム
     * @return エラーリスト
     */
    public List<String> validateBetweenItem(WeatherSearchForm form) {
        List<String> errorList = new ArrayList<String>();

        if (!StringUtils.isEmpty(form.getWeatherDateFrom()) && !StringUtils.isEmpty(form.getWeatherDateTo())) {
            if (form.getWeatherDateFrom().compareTo(form.getWeatherDateTo()) > 0) {
                errorList.add("日付の範囲指定が不正です。");
            }
        }
        if (!StringUtils.isEmpty(form.getMaxTemperatureFrom()) && !StringUtils.isEmpty(form.getMaxTemperatureTo())) {
            Integer valueFrom = Integer.parseInt(form.getMaxTemperatureFrom());
            Integer valueTo = Integer.parseInt(form.getMaxTemperatureTo());
            if (valueFrom > valueTo) {
                errorList.add("最高気温の範囲指定が不正です。");
            }
        }
        if (!StringUtils.isEmpty(form.getMinTemperatureFrom()) && !StringUtils.isEmpty(form.getMinTemperatureTo())) {
            Integer valueFrom = Integer.parseInt(form.getMinTemperatureFrom());
            Integer valueTo = Integer.parseInt(form.getMinTemperatureTo());
            if (valueFrom > valueTo) {
                errorList.add("最低気温の範囲指定が不正です。");
            }
        }

        return errorList;
    }

    /**
     * SQLと条件から天気情報を検索する。
     *
     * @param form フォーム
     * @return 検索結果
     */
    public List<Weather> findBySql(WeatherSearchForm form) {
        String selectSql = createSql(form);
        Map<String, String> condition = createCondition(form);
        return weatherDao.findBySql(selectSql, condition);
    }

    /**
     * 検索に使用するSQLを作成する。
     *
     * @param form フォーム
     * @return SQL
     */
    private String createSql(WeatherSearchForm form) {
        boolean isFirstCondition = true;
        StringBuilder selectSql = new StringBuilder("SELECT * FROM WEATHER");
        if (!StringUtils.isEmpty(form.getWeatherDate())) {
            selectSql.append(" WHERE WEATHER_DATE = :weatherDate");
            isFirstCondition = false;
        }
        if (!StringUtils.isEmpty(form.getPlace())) {
            if (isFirstCondition) {
                selectSql.append(" WHERE PLACE = :place");
                isFirstCondition = false;
            } else {
                selectSql.append(" AND PLACE = :place");
            }
        }
        if (!StringUtils.isEmpty(form.getWeather())) {
            if (isFirstCondition) {
                selectSql.append(" WHERE WEATHER = :weather");
                isFirstCondition = false;
            } else {
                selectSql.append(" AND WEATHER = :weather");
            }
        }
        if (!StringUtils.isEmpty(form.getMaxTemperature())) {
            if (isFirstCondition) {
                selectSql.append(" WHERE MAX_TEMPERATURE = :maxTemperature");
                isFirstCondition = false;
            } else {
                selectSql.append(" AND MAX_TEMPERATURE = :maxTemperature");
            }
        }
        if (!StringUtils.isEmpty(form.getMinTemperature())) {
            if (isFirstCondition) {
                selectSql.append(" WHERE MIN_TEMPERATURE = :minTemperature");
                isFirstCondition = false;
            } else {
                selectSql.append(" AND MIN_TEMPERATURE = :minTemperature");
            }
        }
        return selectSql.toString();
    }

    /**
     * 検索に使用する条件を作成する。
     *
     * @param form フォーム
     * @return 検索条件
     */
    private Map<String, String> createCondition(WeatherSearchForm form) {
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("weatherDate", form.getWeatherDate());
        condition.put("place", form.getPlace());
        condition.put("weather", form.getWeather());
        condition.put("maxTemperature", form.getMaxTemperature());
        condition.put("minTemperature", form.getMinTemperature());

        return condition;
    }

    /**
     * SQLと条件から天気情報を検索する（天気検索発展）。
     *
     * @param form フォーム
     * @return 検索結果
     */
    public List<Weather> findBySqlHard(WeatherSearchForm form) {
        String selectSql = createSqlHard(form);
        Map<String, String> condition = createConditionHard(form);
        return weatherDao.findBySql(selectSql, condition);
    }

    /**
     * 検索に使用するSQLを作成する（天気検索発展）。
     *
     * @param form フォーム
     * @return SQL
     */
    private String createSqlHard(WeatherSearchForm form) {
        boolean isFirstCondition = true;
        StringBuilder selectSql = new StringBuilder("SELECT * FROM WEATHER");
        if (!StringUtils.isEmpty(form.getWeatherDateFrom())) {
            selectSql.append(" WHERE WEATHER_DATE >= :weatherDateFrom");
            isFirstCondition = false;
        }
        if (!StringUtils.isEmpty(form.getWeatherDateTo())) {
            if (isFirstCondition) {
                selectSql.append(" WHERE WEATHER_DATE <= :weatherDateTo");
                isFirstCondition = false;
            } else {
                selectSql.append(" AND WEATHER_DATE <= :weatherDateTo");
            }
        }
        if (!StringUtils.isEmpty(form.getPlace())) {
            if (isFirstCondition) {
                selectSql.append(" WHERE PLACE = :place");
                isFirstCondition = false;
            } else {
                selectSql.append(" AND PLACE = :place");
            }
        }
        if (!StringUtils.isEmpty(form.getWeather()) && StringUtils.split(form.getWeather(), ",").length < 4) {
            String[] weatherArray = StringUtils.split(form.getWeather(), ",");

            if (isFirstCondition) {
                selectSql.append(" WHERE (WEATHER = :weather");
                isFirstCondition = false;
            } else {
                selectSql.append(" AND (WEATHER = :weather");
            }
            if (weatherArray.length == 1) {
                selectSql.append(")");
            } else if (weatherArray.length == 2) {
                selectSql.append(" OR WEATHER = :weather2)");
            } else {
                selectSql.append(" OR WEATHER = :weather2 OR WEATHER = :weather3)");
            }
        }
        if (!StringUtils.isEmpty(form.getMaxTemperatureFrom())) {
            if (isFirstCondition) {
                selectSql.append(" WHERE MAX_TEMPERATURE >= :maxTemperatureFrom");
                isFirstCondition = false;
            } else {
                selectSql.append(" AND MAX_TEMPERATURE >= :maxTemperatureFrom");
            }
        }
        if (!StringUtils.isEmpty(form.getMaxTemperatureTo())) {
            if (isFirstCondition) {
                selectSql.append(" WHERE MAX_TEMPERATURE <= :maxTemperatureTo");
                isFirstCondition = false;
            } else {
                selectSql.append(" AND MAX_TEMPERATURE <= :maxTemperatureTo");
            }
        }
        if (!StringUtils.isEmpty(form.getMinTemperatureFrom())) {
            if (isFirstCondition) {
                selectSql.append(" WHERE MIN_TEMPERATURE >= :minTemperatureFrom");
                isFirstCondition = false;
            } else {
                selectSql.append(" AND MIN_TEMPERATURE >= :minTemperatureFrom");
            }
        }
        if (!StringUtils.isEmpty(form.getMinTemperatureTo())) {
            if (isFirstCondition) {
                selectSql.append(" WHERE MIN_TEMPERATURE <= :minTemperatureTo");
                isFirstCondition = false;
            } else {
                selectSql.append(" AND MIN_TEMPERATURE <= :minTemperatureTo");
            }
        }

        return selectSql.toString();
    }

    /**
     * 検索に使用する条件を作成する（天気検索発展）。
     *
     * @param form フォーム
     * @return 検索条件
     */
    private Map<String, String> createConditionHard(WeatherSearchForm form) {
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("weatherDateFrom", form.getWeatherDateFrom());
        condition.put("weatherDateTo", form.getWeatherDateTo());
        condition.put("place", form.getPlace());
        condition.put("maxTemperatureFrom", form.getMaxTemperatureFrom());
        condition.put("maxTemperatureTo", form.getMaxTemperatureTo());
        condition.put("minTemperatureFrom", form.getMinTemperatureFrom());
        condition.put("minTemperatureTo", form.getMinTemperatureTo());

        // 天気項目の検索条件をチェックボックスの数に応じて設定する
        if (!StringUtils.isEmpty(form.getWeather())) {
            String[] weatherArray = StringUtils.split(form.getWeather(), ",");
            if (weatherArray.length == 1) {
                condition.put("weather", weatherArray[0]);
            } else if (weatherArray.length == 2) {
                condition.put("weather", weatherArray[0]);
                condition.put("weather2", weatherArray[1]);
            } else if (weatherArray.length == 3) {
                condition.put("weather", weatherArray[0]);
                condition.put("weather2", weatherArray[1]);
                condition.put("weather3", weatherArray[2]);
            } else {
                // 全てにチェックが入っている場合は条件を指定しない。
            }
        }

        return condition;
    }
}