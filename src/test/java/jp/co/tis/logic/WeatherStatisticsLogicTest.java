package jp.co.tis.logic;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import jp.co.tis.App;
import jp.co.tis.form.WeatherStatisticsForm;
import jp.co.tis.model.Weather;
import jp.co.tis.model.WeatherDao;
import jp.co.tis.model.WeatherStatisticsDto;

/**
 * 天気統計Logicのテスト。
 *
 * @author Yoshiwara Masashi
 * @since 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@TransactionConfiguration
@Transactional
public class WeatherStatisticsLogicTest extends AbstractTransactionalJUnit4SpringContextTests {

    /** テスト対象クラス */
    @Autowired
    private WeatherStatisticsLogic target;

    /** DB操作DAO */
    @Autowired
    private WeatherDao weatherDao;

    /** INSERT文の雛形 */
    private String insertSql = "INSERT INTO WEATHER (WEATHER_DATE, PLACE, WEATHER, MAX_TEMPERATURE, MIN_TEMPERATURE) "
            + "VALUES ('%s','%s','%s','%s','%s')";

    /**
     * 各テストメソッドの前に呼ばれるセットアップメソッド。<br/>
     * WEATHERテーブルの中身を空にする。<br/>
     * DB関連のテストを行う場合、各テストメソッドで事前データをDBに登録する必要がある。<br/>
     * テスト終了後にはロールバックが行われるため、テスト実施前後でDBの中身は変わらない。
     */
    @Before
    public void setUp() {
        super.deleteFromTables("WEATHER");
    }

    /**
     * {@link WeatherStatisticsLogic#validateForm(WeatherStatisticsForm)}のテスト。
     *
     * <pre>
     * 全ての項目に正常な値が入力された場合
     * </pre>
     */
    @Test
    public void testValidationNormal() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("01/01");
        form.setPlace("東京");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherStatisticsLogic#validateForm(WeatherStatisticsForm)}のテスト。
     *
     * <pre>
     * 全ての項目がブランクの場合
     * </pre>
     */
    @Test
    public void testValidationAllBlank() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("");
        form.setPlace("");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("日付と場所は、必ず両方入力してください。"));
    }

    /**
     * {@link WeatherStatisticsLogic#validateForm(WeatherStatisticsForm)}のテスト。
     *
     * <pre>
     * 全ての項目がnullの場合
     * </pre>
     */
    @Test
    public void testValidationAllNull() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("日付と場所は、必ず両方入力してください。"));
    }

    /**
     * {@link WeatherStatisticsLogic#validateForm(WeatherStatisticsForm)}のテスト。
     *
     * <pre>
     * 全ての項目に異常な値が入力された場合
     * </pre>
     */
    @Test
    public void testValidationAbnormlAll() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("20150101");
        form.setPlace("12345678901");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("日付はMM/dd形式で入力してください。"));
        assertThat(errorList.get(1), is("場所は10文字以内で入力してください。"));
    }

    /**
     * {@link WeatherStatisticsLogic#validateForm(WeatherStatisticsForm)}のテスト。
     *
     * <pre>
     * 日付項目の形式が不正な場合(MMdd)
     * </pre>
     */
    @Test
    public void testValidationAbnormalDateFormat() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("0101");
        form.setPlace("東京");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("日付はMM/dd形式で入力してください。"));
    }

    /**
     * {@link WeatherStatisticsLogic#validateForm(WeatherStatisticsForm)}のテスト。
     *
     * <pre>
     * 日付項目の形式が不正な場合(01/01A)
     * </pre>
     */
    @Test
    public void testValidationAbnormalDateFormatBackWord() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("01/01A");
        form.setPlace("東京");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("日付はMM/dd形式で入力してください。"));
    }

    /**
     * {@link WeatherStatisticsLogic#validateForm(WeatherStatisticsForm)}のテスト。
     *
     * <pre>
     * 日付項目の形式が不正な場合(全角数字を使用)
     * </pre>
     */
    @Test
    public void testValidationAbnormalDateFormatZenkaku() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("０１/０１");
        form.setPlace("東京");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("日付はMM/dd形式で入力してください。"));
    }

    /**
     * {@link WeatherStatisticsLogic#validateForm(WeatherStatisticsForm)}のテスト。
     * <pre>
     * 日付項目がうるう年の日付の場合(02/29)
     * ※うるう年特有の日付であってもOKとなること。
     * </pre>
     */
    @Test
    public void testValidationAbnormalDateFormatLeapYear() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("02/29");
        form.setPlace("東京");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherStatisticsLogic#validateForm(WeatherStatisticsForm)}のテスト。
     *
     * <pre>
     * 場所項目の文字数が最大の場合（10文字）
     * </pre>
     */
    @Test
    public void testValidationPlaceNormal() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("01/01");
        form.setPlace("場所れれれれれれれれ");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherStatisticsLogic#validateForm(WeatherStatisticsForm)}のテスト。
     *
     * <pre>
     * 場所項目の文字数が最大数を超える場合（11文字）
     * </pre>
     */
    @Test
    public void testValidationPlaceAbnormal() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("01/01");
        form.setPlace("場所れれれれれれれれれ");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("場所は10文字以内で入力してください。"));
    }

    /**
     * {@link WeatherStatisticsLogic#validateForm(WeatherStatisticsForm)}のテスト。
     *
     * <pre>
     * 日付のみ入力されなかった場合
     * </pre>
     */
    @Test
    public void testValidationEmptyDate() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setPlace("東京");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("日付と場所は、必ず両方入力してください。"));
    }

    /**
     * {@link WeatherStatisticsLogic#validateForm(WeatherStatisticsForm)}のテスト。
     *
     * <pre>
     * 場所のみ入力されなかった場合
     * </pre>
     */
    @Test
    public void testValidationEmptyPlace() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("01/01");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("日付と場所は、必ず両方入力してください。"));
    }

    /**
     * {@link WeatherStatisticsLogic#validateForm(WeatherStatisticsForm)}のテスト。
     *
     * <pre>
     * 日付が未入力かつ場所に異常な値が入力された場合
     * </pre>
     */
    @Test
    public void testValidationEmptyDateAndAbnormalPlace() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setPlace("場所れれれれれれれれれ");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("場所は10文字以内で入力してください。"));
        assertThat(errorList.get(1), is("日付と場所は、必ず両方入力してください。"));
    }

    /**
     * {@link WeatherStatisticsLogic#validateForm(WeatherStatisticsForm)}のテスト。
     *
     * <pre>
     * 場所が未入力かつ日付に異常な値が入力された場合
     * </pre>
     */
    @Test
    public void testValidationEmptyPlaceAndAbnormalDate() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("20150101");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("日付はMM/dd形式で入力してください。"));
        assertThat(errorList.get(1), is("日付と場所は、必ず両方入力してください。"));
    }

    /**
     * {@link WeatherStatisticsLogic#createPastWeatherList(WeatherStatisticsForm)}
     * のテスト。
     *
     * <pre>
     * 全ての項目に正常な値が入力された場合
     * </pre>
     */
    @Test
    public void testSearchPastWeather() {
        // 事前データ
        weatherDao.insert(String.format(insertSql, "2010/01/01", "東京", "晴れ", "10", "5"));
        weatherDao.insert(String.format(insertSql, "2011/01/01", "東京", "曇り", "9", "4"));
        weatherDao.insert(String.format(insertSql, "2012/01/01", "東京", "雨", "8", "3"));
        weatherDao.insert(String.format(insertSql, "2013/01/01", "東京", "雪", "7", "2"));
        weatherDao.insert(String.format(insertSql, "2014/01/01", "東京", "晴れ", "6", "1"));

        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("01/01");
        form.setPlace("東京");
        List<Weather> resultWeatherList = target.createPastWeatherList(form);

        List<Weather> expectedList = new ArrayList<Weather>();
        Weather weather = new Weather();
        weather.setWeatherDate("2010/01/01");
        weather.setPlace("東京");
        weather.setWeather("晴れ");
        weather.setMaxTemperature("10");
        weather.setMinTemperature("5");
        expectedList.add(weather);

        weather = new Weather();
        weather.setWeatherDate("2011/01/01");
        weather.setPlace("東京");
        weather.setWeather("曇り");
        weather.setMaxTemperature("9");
        weather.setMinTemperature("4");
        expectedList.add(weather);

        weather = new Weather();
        weather.setWeatherDate("2012/01/01");
        weather.setPlace("東京");
        weather.setWeather("雨");
        weather.setMaxTemperature("8");
        weather.setMinTemperature("3");
        expectedList.add(weather);

        weather = new Weather();
        weather.setWeatherDate("2013/01/01");
        weather.setPlace("東京");
        weather.setWeather("雪");
        weather.setMaxTemperature("7");
        weather.setMinTemperature("2");
        expectedList.add(weather);

        weather = new Weather();
        weather.setWeatherDate("2014/01/01");
        weather.setPlace("東京");
        weather.setWeather("晴れ");
        weather.setMaxTemperature("6");
        weather.setMinTemperature("1");
        expectedList.add(weather);

        assertThat(resultWeatherList.size(), is(expectedList.size()));
        for (int index = 0; index < 5; index++) {
            assertThat(resultWeatherList.get(index).getWeatherDate(), is(expectedList.get(index).getWeatherDate()));
            assertThat(resultWeatherList.get(index).getPlace(), is(expectedList.get(index).getPlace()));
            assertThat(resultWeatherList.get(index).getWeather(), is(expectedList.get(index).getWeather()));
            assertThat(resultWeatherList.get(index).getMaxTemperature(),
                    is(expectedList.get(index).getMaxTemperature()));
            assertThat(resultWeatherList.get(index).getMinTemperature(),
                    is(expectedList.get(index).getMinTemperature()));
        }

    }

    /**
     * {@link WeatherStatisticsLogic#createWeatherStatisticsDto(WeatherStatisticsForm,List
     * <Weather>)}のテスト。
     *
     * <pre>
     * 過去の天気のリストに全種類の天気が含まれていた場合
     * </pre>
     */
    @Test
    public void testCreateWeatherStatisticsDtoAllWeather() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("01/01");
        form.setPlace("東京");

        Weather firstWeather = new Weather();
        firstWeather.setWeatherDate("2010/01/01");
        firstWeather.setPlace("東京");
        firstWeather.setWeather("晴れ");
        firstWeather.setMaxTemperature("10");
        firstWeather.setMinTemperature("0");

        Weather secondWeather = new Weather();
        secondWeather.setWeatherDate("2011/01/01");
        secondWeather.setPlace("東京");
        secondWeather.setWeather("曇り");
        secondWeather.setMaxTemperature("10");
        secondWeather.setMinTemperature("0");

        Weather thirdWeather = new Weather();
        thirdWeather.setWeatherDate("2012/01/01");
        thirdWeather.setPlace("東京");
        thirdWeather.setWeather("雨");
        thirdWeather.setMaxTemperature("10");
        thirdWeather.setMinTemperature("0");

        Weather fourthWeather = new Weather();
        fourthWeather.setWeatherDate("2013/01/01");
        fourthWeather.setPlace("東京");
        fourthWeather.setWeather("雪");
        fourthWeather.setMaxTemperature("10");
        fourthWeather.setMinTemperature("0");

        Weather fifthWeather = new Weather();
        fifthWeather.setWeatherDate("2014/01/01");
        fifthWeather.setPlace("東京");
        fifthWeather.setWeather("晴れ");
        fifthWeather.setMaxTemperature("10");
        fifthWeather.setMinTemperature("0");

        List<Weather> pastWeatherList = new ArrayList<Weather>();
        pastWeatherList.add(firstWeather);
        pastWeatherList.add(secondWeather);
        pastWeatherList.add(thirdWeather);
        pastWeatherList.add(fourthWeather);
        pastWeatherList.add(fifthWeather);

        WeatherStatisticsDto resultDto = target.createWeatherStatisticsDto(form, pastWeatherList);

        assertThat(resultDto.getWeatherDate(), is("01/01"));
        assertThat(resultDto.getPlace(), is("東京"));
        assertThat(resultDto.getSunnyPercent(), is(40));
        assertThat(resultDto.getCloudyPercent(), is(20));
        assertThat(resultDto.getRainyPercent(), is(20));
        assertThat(resultDto.getSnowPercent(), is(20));
        assertThat(resultDto.getMaxTemperatureAve(), is(10));
        assertThat(resultDto.getMinTemperatureAve(), is(0));

    }

    /**
     * {@link WeatherStatisticsLogic#createWeatherStatisticsDto(WeatherStatisticsForm,List
     * <Weather>)}のテスト。
     *
     * <pre>
     * 過去の天気のリストに含まれている天気がすべて晴れの場合
     * </pre>
     */
    @Test
    public void testCreateWeatherStatisticsDtoAllSunny() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("01/01");
        form.setPlace("東京");

        Weather firstWeather = new Weather();
        firstWeather.setWeatherDate("2010/01/01");
        firstWeather.setPlace("東京");
        firstWeather.setWeather("晴れ");
        firstWeather.setMaxTemperature("10");
        firstWeather.setMinTemperature("0");

        Weather secondWeather = new Weather();
        secondWeather.setWeatherDate("2011/01/01");
        secondWeather.setPlace("東京");
        secondWeather.setWeather("晴れ");
        secondWeather.setMaxTemperature("10");
        secondWeather.setMinTemperature("0");

        Weather thirdWeather = new Weather();
        thirdWeather.setWeatherDate("2012/01/01");
        thirdWeather.setPlace("東京");
        thirdWeather.setWeather("晴れ");
        thirdWeather.setMaxTemperature("10");
        thirdWeather.setMinTemperature("0");

        Weather fourthWeather = new Weather();
        fourthWeather.setWeatherDate("2013/01/01");
        fourthWeather.setPlace("東京");
        fourthWeather.setWeather("晴れ");
        fourthWeather.setMaxTemperature("10");
        fourthWeather.setMinTemperature("0");

        Weather fifthWeather = new Weather();
        fifthWeather.setWeatherDate("2014/01/01");
        fifthWeather.setPlace("東京");
        fifthWeather.setWeather("晴れ");
        fifthWeather.setMaxTemperature("10");
        fifthWeather.setMinTemperature("0");

        List<Weather> pastWeatherList = new ArrayList<Weather>();
        pastWeatherList.add(firstWeather);
        pastWeatherList.add(secondWeather);
        pastWeatherList.add(thirdWeather);
        pastWeatherList.add(fourthWeather);
        pastWeatherList.add(fifthWeather);

        WeatherStatisticsDto resultDto = target.createWeatherStatisticsDto(form, pastWeatherList);

        assertThat(resultDto.getWeatherDate(), is("01/01"));
        assertThat(resultDto.getPlace(), is("東京"));
        assertThat(resultDto.getSunnyPercent(), is(100));
        assertThat(resultDto.getCloudyPercent(), is(0));
        assertThat(resultDto.getRainyPercent(), is(0));
        assertThat(resultDto.getSnowPercent(), is(0));
        assertThat(resultDto.getMaxTemperatureAve(), is(10));
        assertThat(resultDto.getMinTemperatureAve(), is(0));

    }

    /**
     * {@link WeatherStatisticsLogic#createWeatherStatisticsDto(WeatherStatisticsForm,List
     * <Weather>)}のテスト。
     *
     * <pre>
     * 過去の天気のリストに含まれている天気がすべて曇りの場合
     * </pre>
     */
    @Test
    public void testCreateWeatherStatisticsDtoAllCloudy() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("01/01");
        form.setPlace("東京");

        Weather firstWeather = new Weather();
        firstWeather.setWeatherDate("2010/01/01");
        firstWeather.setPlace("東京");
        firstWeather.setWeather("曇り");
        firstWeather.setMaxTemperature("10");
        firstWeather.setMinTemperature("0");

        Weather secondWeather = new Weather();
        secondWeather.setWeatherDate("2011/01/01");
        secondWeather.setPlace("東京");
        secondWeather.setWeather("曇り");
        secondWeather.setMaxTemperature("10");
        secondWeather.setMinTemperature("0");

        Weather thirdWeather = new Weather();
        thirdWeather.setWeatherDate("2012/01/01");
        thirdWeather.setPlace("東京");
        thirdWeather.setWeather("曇り");
        thirdWeather.setMaxTemperature("10");
        thirdWeather.setMinTemperature("0");

        Weather fourthWeather = new Weather();
        fourthWeather.setWeatherDate("2013/01/01");
        fourthWeather.setPlace("東京");
        fourthWeather.setWeather("曇り");
        fourthWeather.setMaxTemperature("10");
        fourthWeather.setMinTemperature("0");

        Weather fifthWeather = new Weather();
        fifthWeather.setWeatherDate("2014/01/01");
        fifthWeather.setPlace("東京");
        fifthWeather.setWeather("曇り");
        fifthWeather.setMaxTemperature("10");
        fifthWeather.setMinTemperature("0");

        List<Weather> pastWeatherList = new ArrayList<Weather>();
        pastWeatherList.add(firstWeather);
        pastWeatherList.add(secondWeather);
        pastWeatherList.add(thirdWeather);
        pastWeatherList.add(fourthWeather);
        pastWeatherList.add(fifthWeather);

        WeatherStatisticsDto resultDto = target.createWeatherStatisticsDto(form, pastWeatherList);

        assertThat(resultDto.getWeatherDate(), is("01/01"));
        assertThat(resultDto.getPlace(), is("東京"));
        assertThat(resultDto.getSunnyPercent(), is(0));
        assertThat(resultDto.getCloudyPercent(), is(100));
        assertThat(resultDto.getRainyPercent(), is(0));
        assertThat(resultDto.getSnowPercent(), is(0));
        assertThat(resultDto.getMaxTemperatureAve(), is(10));
        assertThat(resultDto.getMinTemperatureAve(), is(0));

    }

    /**
     * {@link WeatherStatisticsLogic#createWeatherStatisticsDto(WeatherStatisticsForm,List
     * <Weather>)}のテスト。
     *
     * <pre>
     * 過去の天気のリストに含まれている天気がすべて雨の場合
     * </pre>
     */
    @Test
    public void testCreateWeatherStatisticsDtoAllRainy() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("01/01");
        form.setPlace("東京");

        Weather firstWeather = new Weather();
        firstWeather.setWeatherDate("2015/01/01");
        firstWeather.setPlace("東京");
        firstWeather.setWeather("雨");
        firstWeather.setMaxTemperature("10");
        firstWeather.setMinTemperature("0");

        Weather secondWeather = new Weather();
        secondWeather.setWeatherDate("2015/01/01");
        secondWeather.setPlace("東京");
        secondWeather.setWeather("雨");
        secondWeather.setMaxTemperature("10");
        secondWeather.setMinTemperature("0");

        Weather thirdWeather = new Weather();
        thirdWeather.setWeatherDate("2015/01/01");
        thirdWeather.setPlace("東京");
        thirdWeather.setWeather("雨");
        thirdWeather.setMaxTemperature("10");
        thirdWeather.setMinTemperature("0");

        Weather fourthWeather = new Weather();
        fourthWeather.setWeatherDate("2015/01/01");
        fourthWeather.setPlace("東京");
        fourthWeather.setWeather("雨");
        fourthWeather.setMaxTemperature("10");
        fourthWeather.setMinTemperature("0");

        Weather fifthWeather = new Weather();
        fifthWeather.setWeatherDate("2015/01/01");
        fifthWeather.setPlace("東京");
        fifthWeather.setWeather("雨");
        fifthWeather.setMaxTemperature("10");
        fifthWeather.setMinTemperature("0");

        List<Weather> pastWeatherList = new ArrayList<Weather>();
        pastWeatherList.add(firstWeather);
        pastWeatherList.add(secondWeather);
        pastWeatherList.add(thirdWeather);
        pastWeatherList.add(fourthWeather);
        pastWeatherList.add(fifthWeather);

        WeatherStatisticsDto resultDto = target.createWeatherStatisticsDto(form, pastWeatherList);

        assertThat(resultDto.getWeatherDate(), is("01/01"));
        assertThat(resultDto.getPlace(), is("東京"));
        assertThat(resultDto.getSunnyPercent(), is(0));
        assertThat(resultDto.getCloudyPercent(), is(0));
        assertThat(resultDto.getRainyPercent(), is(100));
        assertThat(resultDto.getSnowPercent(), is(0));
        assertThat(resultDto.getMaxTemperatureAve(), is(10));
        assertThat(resultDto.getMinTemperatureAve(), is(0));

    }

    /**
     * {@link WeatherStatisticsLogic#createWeatherStatisticsDto(WeatherStatisticsForm,List
     * <Weather>)}のテスト。
     *
     * <pre>
     * 過去の天気のリストに含まれている天気がすべて雨の場合
     * </pre>
     */
    @Test
    public void testCreateWeatherStatisticsDtoAllSnow() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("01/01");
        form.setPlace("東京");

        Weather firstWeather = new Weather();
        firstWeather.setWeatherDate("2015/01/01");
        firstWeather.setPlace("東京");
        firstWeather.setWeather("雪");
        firstWeather.setMaxTemperature("10");
        firstWeather.setMinTemperature("0");

        Weather secondWeather = new Weather();
        secondWeather.setWeatherDate("2015/01/01");
        secondWeather.setPlace("東京");
        secondWeather.setWeather("雪");
        secondWeather.setMaxTemperature("10");
        secondWeather.setMinTemperature("0");

        Weather thirdWeather = new Weather();
        thirdWeather.setWeatherDate("2015/01/01");
        thirdWeather.setPlace("東京");
        thirdWeather.setWeather("雪");
        thirdWeather.setMaxTemperature("10");
        thirdWeather.setMinTemperature("0");

        Weather fourthWeather = new Weather();
        fourthWeather.setWeatherDate("2015/01/01");
        fourthWeather.setPlace("東京");
        fourthWeather.setWeather("雪");
        fourthWeather.setMaxTemperature("10");
        fourthWeather.setMinTemperature("0");

        Weather fifthWeather = new Weather();
        fifthWeather.setWeatherDate("2015/01/01");
        fifthWeather.setPlace("東京");
        fifthWeather.setWeather("雪");
        fifthWeather.setMaxTemperature("10");
        fifthWeather.setMinTemperature("0");

        List<Weather> pastWeatherList = new ArrayList<Weather>();
        pastWeatherList.add(firstWeather);
        pastWeatherList.add(secondWeather);
        pastWeatherList.add(thirdWeather);
        pastWeatherList.add(fourthWeather);
        pastWeatherList.add(fifthWeather);

        WeatherStatisticsDto resultDto = target.createWeatherStatisticsDto(form, pastWeatherList);

        assertThat(resultDto.getWeatherDate(), is("01/01"));
        assertThat(resultDto.getPlace(), is("東京"));
        assertThat(resultDto.getSunnyPercent(), is(0));
        assertThat(resultDto.getCloudyPercent(), is(0));
        assertThat(resultDto.getRainyPercent(), is(0));
        assertThat(resultDto.getSnowPercent(), is(100));
        assertThat(resultDto.getMaxTemperatureAve(), is(10));
        assertThat(resultDto.getMinTemperatureAve(), is(0));

    }

    /**
     * {@link WeatherStatisticsLogic#createWeatherStatisticsDto(WeatherStatisticsForm,List
     * <Weather>)}のテスト。
     *
     * <pre>
     * 過去の天気のリストに含まれている天気がすべて空の場合
     * ※実際には起こりえないが、createWeatherStatisticsDto()の条件分岐を網羅するために記述
     * </pre>
     */
    @Test
    public void testCreateWeatherStatisticsDtoWetherAllEmpty() {
        WeatherStatisticsForm form = new WeatherStatisticsForm();
        form.setWeatherDate("01/01");
        form.setPlace("東京");

        Weather firstWeather = new Weather();
        firstWeather.setWeatherDate("2015/01/01");
        firstWeather.setPlace("東京");
        firstWeather.setWeather("");
        firstWeather.setMaxTemperature("10");
        firstWeather.setMinTemperature("0");

        Weather secondWeather = new Weather();
        secondWeather.setWeatherDate("2015/01/01");
        secondWeather.setPlace("東京");
        secondWeather.setWeather("");
        secondWeather.setMaxTemperature("10");
        secondWeather.setMinTemperature("0");

        Weather thirdWeather = new Weather();
        thirdWeather.setWeatherDate("2015/01/01");
        thirdWeather.setPlace("東京");
        thirdWeather.setWeather("");
        thirdWeather.setMaxTemperature("10");
        thirdWeather.setMinTemperature("0");

        Weather fourthWeather = new Weather();
        fourthWeather.setWeatherDate("2015/01/01");
        fourthWeather.setPlace("東京");
        fourthWeather.setWeather("");
        fourthWeather.setMaxTemperature("10");
        fourthWeather.setMinTemperature("0");

        Weather fifthWeather = new Weather();
        fifthWeather.setWeatherDate("2015/01/01");
        fifthWeather.setPlace("東京");
        fifthWeather.setWeather("");
        fifthWeather.setMaxTemperature("10");
        fifthWeather.setMinTemperature("0");

        List<Weather> pastWeatherList = new ArrayList<Weather>();
        pastWeatherList.add(firstWeather);
        pastWeatherList.add(secondWeather);
        pastWeatherList.add(thirdWeather);
        pastWeatherList.add(fourthWeather);
        pastWeatherList.add(fifthWeather);

        WeatherStatisticsDto resultDto = target.createWeatherStatisticsDto(form, pastWeatherList);

        assertThat(resultDto.getWeatherDate(), is("01/01"));
        assertThat(resultDto.getPlace(), is("東京"));
        assertThat(resultDto.getSunnyPercent(), is(0));
        assertThat(resultDto.getCloudyPercent(), is(0));
        assertThat(resultDto.getRainyPercent(), is(0));
        assertThat(resultDto.getSnowPercent(), is(0));
        assertThat(resultDto.getMaxTemperatureAve(), is(10));
        assertThat(resultDto.getMinTemperatureAve(), is(0));

    }

}
