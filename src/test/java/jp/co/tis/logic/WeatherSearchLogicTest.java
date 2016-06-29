package jp.co.tis.logic;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
import jp.co.tis.form.WeatherSearchForm;
import jp.co.tis.model.Person;
import jp.co.tis.model.Weather;
import jp.co.tis.model.WeatherDao;

/**
 * 天気検索Logicのテスト。
 *
 * @author Yoshiwara Masashi
 * @since 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@TransactionConfiguration
@Transactional
public class WeatherSearchLogicTest extends AbstractTransactionalJUnit4SpringContextTests {

    /** テスト対象クラス */
    @Autowired
    private WeatherSearchLogic target;

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
     * {@link WeatherSearchLogic#createPersonInfo()}のテスト。
     *
     * <pre>
     * ウォーミングアップのテスト
     * </pre>
     */
    @Test
    public void testCreatePersonInfo() {
        Person person = target.createPersonInfo();

        assertThat(person.getMyName(), is("TIS 太郎"));
        assertThat(person.getAge(), is("25"));
        assertThat(person.getHobby(), is("読書・映画・ショッピング"));
        assertThat(person.getSkill(), is("空手5段"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlWeatherList()}のテスト。
     *
     * <pre>
     * 天気一覧検索のテスト
     * </pre>
     */
    @Test
    public void testFindBySqlWeatherList() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        List<Weather> resultWeatherList = target.findBySqlWeatherList();
        assertThat(resultWeatherList.size(), is(2));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("7"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-3"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(1).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(1).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("11"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("6"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlSimple(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 検索条件を設定せずに検索を行った場合
     * </pre>
     */
    @Test
    public void testFindBySqlSimpleNoCondition() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        WeatherSearchForm form = new WeatherSearchForm();
        List<Weather> resultWeatherList = target.findBySqlSimple(form);
        assertThat(resultWeatherList.size(), is(2));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("7"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-3"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(1).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(1).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("11"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("6"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlSimple(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 場所を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlSimplePlace() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setPlace("東京");

        List<Weather> resultWeatherList = target.findBySqlSimple(form);
        assertThat(resultWeatherList.size(), is(1));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(0).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(0).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("11"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("6"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlSimple(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 検索結果が0件となる条件の場合
     * </pre>
     */
    @Test
    public void testFindBySqlSimpleNoResult() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setPlace("北海道");

        List<Weather> resultWeatherList = target.findBySqlSimple(form);
        assertThat(resultWeatherList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 全ての項目に正常な値が入力された場合
     * </pre>
     */
    @Test
    public void testValidateFormNormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDate("2016/02/29");
        form.setPlace("東京");
        form.setWeather("晴れ");
        form.setMaxTemperature("20");
        form.setMinTemperature("10");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 全ての項目がブランクの場合
     * </pre>
     */
    @Test
    public void testValidateFormNormalAllBlank() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDate("");
        form.setPlace("");
        form.setWeather("");
        form.setMaxTemperature("");
        form.setMinTemperature("");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 全ての項目がnullの場合
     * </pre>
     */
    @Test
    public void testValidateFormNormalAllNull() {
        WeatherSearchForm form = new WeatherSearchForm();
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 全ての項目に異常な値が入力された場合
     * </pre>
     */
    @Test
    public void testValidateFormAbnormalAll() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDate("20150101");
        form.setPlace("12345678901");
        form.setWeather("12345678901");
        form.setMaxTemperature("あ");
        form.setMinTemperature("あ");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("日付は日付形式で入力してください。"));
        assertThat(errorList.get(1), is("場所は10文字以内で入力してください。"));
        assertThat(errorList.get(2), is("天気は10文字以内で入力してください。"));
        assertThat(errorList.get(3), is("最高気温は数値で入力してください。"));
        assertThat(errorList.get(4), is("最低気温は数値で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付項目の形式が不正な場合(yyyyMMdd)
     * </pre>
     */
    @Test
    public void testValidateFormWeatherDateAbnormalFormat() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDate("20150101");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("日付は日付形式で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付項目の形式が不正な場合(2015/01/01A)
     * ※SimpleDateFormatを使用したパースでは前方一致で日付を取り扱うため、文字列の後ろに不正な値があっても無視されてしまう。
     * </pre>
     */
    @Test
    public void testValidateFormWeatherDateAbnormalFormatBackward() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDate("2015/01/01A");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("日付は日付形式で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付項目の形式が不正な場合(全角数字を使用)
     * </pre>
     */
    @Test
    public void testValidateFormWeatherDateAbnormalFormatZenkaku() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDate("２０１５/０１/０１");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("日付は日付形式で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付項目がうるう年の正常な日付の場合(2016 / 02 / 29)
     * </pre>
     */
    @Test
    public void testValidateFormWeatherDateNormalDateLeapYear() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDate("2016/02/29");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付項目がうるう年の不正な日付の場合(2015 / 02 / 29)
     * </pre>
     */
    @Test
    public void testValidateFormWeatherDateAbnormalDateLeapYear() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDate("2015/02/29");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("日付は日付形式で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 場所項目の文字数が最大の場合（10文字）
     * </pre>
     */
    @Test
    public void testValidateFormPlaceNormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setPlace("場所れれれれれれれれ");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 場所項目の文字数が最大数を超える場合（11文字）
     * </pre>
     */
    @Test
    public void testValidateFormPlaceAbnormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setPlace("場所れれれれれれれれれ");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("場所は10文字以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 天気項目の文字数が最大の場合（10文字）
     * </pre>
     */
    @Test
    public void testValidateFormWeatherNormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeather("天気れれれれれれれれ");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 天気項目の文字数が最大数を超える場合（11文字）
     * </pre>
     */
    @Test
    public void testValidateFormWeatherAbnormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeather("天気れれれれれれれれれ");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("天気は10文字以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温に数値以外のものが入力された場合
     * </pre>
     */
    @Test
    public void testValidateFormMaxTemperatureAbnormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperature("あ");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("最高気温は数値で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温に正の整数3桁の上限が入力された場合(999)
     * </pre>
     */
    @Test
    public void testValidateFormMaxTemperatureNormalUpperLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperature("999");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温に負の整数3桁の下限が入力された場合(-999)
     * </pre>
     */
    @Test
    public void testValidateFormMaxTemperatureNormalLowerLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperature("-999");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温に正の整数3桁を超える入力がされた場合(1000)
     * </pre>
     */
    @Test
    public void testValidateFormMaxTemperatureAbnormalUpperLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperature("1000");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("最高気温は3桁以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温に負の整数3桁を下回る入力がされた場合(-1000)
     * </pre>
     */
    @Test
    public void testValidateFormMaxTemperatureAbnormalLowerLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperature("-1000");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("最高気温は3桁以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温に数値以外のものが入力された場合
     * </pre>
     */
    @Test
    public void testValidateFormMinTemperatureAbnormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperature("あ");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("最低気温は数値で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温に正の整数3桁の上限が入力された場合(999)
     * </pre>
     */
    @Test
    public void testValidateFormMinTemperatureNormalUpperLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperature("999");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温に負の整数3桁の下限が入力された場合(-999)
     * </pre>
     */
    @Test
    public void testValidateFormMinTemperatureNormalLowerLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperature("-999");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温に正の整数3桁を超える入力がされた場合(1000)
     * </pre>
     */
    @Test
    public void testValidateFormMinTemperatureAbnormalUpperLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperature("1000");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("最低気温は3桁以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateForm(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温に負の整数3桁を下回る入力がされた場合(-1000)
     * </pre>
     */
    @Test
    public void testValidateFormMinTemperatureAbnormalLowerLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperature("-1000");
        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("最低気温は3桁以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 全ての項目に正常な値が入力された場合
     * </pre>
     */
    @Test
    public void testValidateFormHardNormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("2015/01/01");
        form.setWeatherDateTo("2015/02/01");
        form.setPlace("東京");
        form.setWeather("晴れ");
        form.setMaxTemperatureFrom("10");
        form.setMaxTemperatureTo("20");
        form.setMinTemperatureFrom("0");
        form.setMinTemperatureTo("10");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 全ての項目がブランクの場合
     * </pre>
     */
    @Test
    public void testValidateFormHardNormalAllBlank() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("");
        form.setWeatherDateTo("");
        form.setPlace("");
        form.setWeather("");
        form.setMaxTemperatureFrom("");
        form.setMaxTemperatureTo("");
        form.setMinTemperatureFrom("");
        form.setMinTemperatureTo("");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 全ての項目がnullの場合
     * </pre>
     */
    @Test
    public void testValidateFormHardNormalAllNull() {
        WeatherSearchForm form = new WeatherSearchForm();
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 全ての項目に異常な値が入力された場合
     * </pre>
     */
    @Test
    public void testValidateFormHardAbnormlAll() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("20150101");
        form.setWeatherDateTo("20150101");
        form.setPlace("12345678901");
        form.setWeather("12345678901");
        form.setMaxTemperatureFrom("あ");
        form.setMaxTemperatureTo("1000");
        form.setMinTemperatureFrom("あ");
        form.setMinTemperatureTo("1000");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("日付は日付形式で入力してください。"));
        assertThat(errorList.get(1), is("場所は10文字以内で入力してください。"));
        assertThat(errorList.get(2), is("天気は10文字以内で入力してください。"));
        assertThat(errorList.get(3), is("最高気温は数値で入力してください。"));
        assertThat(errorList.get(4), is("最高気温は3桁以内で入力してください。"));
        assertThat(errorList.get(5), is("最低気温は数値で入力してください。"));
        assertThat(errorList.get(6), is("最低気温は3桁以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付項目(Form)の形式が不正な場合(yyyyMMdd)
     * </pre>
     */
    @Test
    public void testValidateFormHardWeatherDateFromAbnormalFormat() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("20150101");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("日付は日付形式で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付項目(Form)の形式が不正な場合(2015/01/01A)
     * ※SimpleDateFormatを使用したパースでは前方一致で日付を取り扱うため、文字列の後ろに不正な値があっても無視されてしまう。
     * </pre>
     */
    @Test
    public void testValidateFormHardWeatherDateFromAbnormalFormatBackward() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("2015/01/01A");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("日付は日付形式で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付項目(Form)の形式が不正な場合(全角数字を使用)
     * </pre>
     */
    @Test
    public void testValidateFormHardWeatherDateFromAbnormalFormatZenkaku() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("２０１５/０１/０１");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("日付は日付形式で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付項目(Form)がうるう年の正常な日付の場合(2016/02/29)
     * </pre>
     */
    @Test
    public void testValidateFormHardWeatherDateFromNormalDateLeapYear() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("2016/02/29");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付項目(Form)がうるう年の不正な日付の場合(2015/02/29)
     * </pre>
     */
    @Test
    public void testValidateFormHardWeatherDateFromAbnormalDateLeapYear() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("2015/02/29");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("日付は日付形式で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付項目(To)の形式が不正な場合(yyyyMMdd)
     * </pre>
     */
    @Test
    public void testValidateFormHardWeatherDateToAbnormalFormat() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateTo("20150101");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("日付は日付形式で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付項目(To)の形式が不正な場合(2015/01/01A)
     * ※SimpleDateFormatを使用したパースでは前方一致で日付を取り扱うため、文字列の後ろに不正な値があっても無視されてしまう。
     * </pre>
     */
    @Test
    public void testValidateFormHardWeatherDateToAbnormalFormatBackward() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateTo("2015/01/01A");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("日付は日付形式で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付項目(To)の形式が不正な場合(全角数字を使用)
     * </pre>
     */
    @Test
    public void testValidateFormHardWeatherDateToAbnormalFormatZenkaku() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateTo("２０１５/０１/０１");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("日付は日付形式で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付項目(To)がうるう年の正常な日付の場合(2016/02/29)
     * </pre>
     */
    @Test
    public void testValidateFormHardWeatherDateToNormalDateLeapYear() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateTo("2016/02/29");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付項目(To)がうるう年の不正な日付の場合(2015/02/29)
     * </pre>
     */
    @Test
    public void testValidateFormHardWeatherDateToAbnormalDateLeapYear() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateTo("2015/02/29");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("日付は日付形式で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 場所項目の文字数が最大の場合（10文字）
     * </pre>
     */
    @Test
    public void testValidateFormHardPlaceNormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setPlace("場所れれれれれれれれ");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 場所項目の文字数が最大数を超える場合（11文字）
     * </pre>
     */
    @Test
    public void testValidateFormHardPlaceAbnormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setPlace("場所れれれれれれれれれ");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("場所は10文字以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 天気項目の文字数が最大の場合（10文字）
     * </pre>
     */
    @Test
    public void testValidateFormHardWeatherNormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeather("天気れれれれれれれれ");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 天気項目の文字数が最大数を超える場合（11文字）
     * </pre>
     */
    @Test
    public void testValidateFormHardWeatherAbnormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeather("天気れれれれれれれれれ");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("天気は10文字以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温(From)に数値以外のものが入力された場合
     * </pre>
     */
    @Test
    public void testValidateFormHardMaxTemperatureFromAbnormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperatureFrom("あ");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("最高気温は数値で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温(From)に正の整数3桁の上限が入力された場合(999)
     * </pre>
     */
    @Test
    public void testValidateFormHardMaxTemperatureFromNormalUpperLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperatureFrom("999");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温(From)に負の整数3桁の下限が入力された場合(-999)
     * </pre>
     */
    @Test
    public void testValidateFormHardMaxTemperatureFromNormalLowerLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperatureFrom("-999");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温(From)に正の整数3桁を超える入力がされた場合(1000)
     * </pre>
     */
    @Test
    public void testValidateFormHardMaxTemperatureFromAbnormalUpperLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperatureFrom("1000");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("最高気温は3桁以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温(From)に負の整数3桁を下回る入力がされた場合(-1000)
     * </pre>
     */
    @Test
    public void testValidateFormHardMaxTemperatureFromAbnormalLowerLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperatureFrom("-1000");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("最高気温は3桁以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温(To)に数値以外のものが入力された場合
     * </pre>
     */
    @Test
    public void testValidateFormHardMaxTemperatureToAbnormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperatureTo("あ");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("最高気温は数値で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温(To)に正の整数3桁の上限が入力された場合(999)
     * </pre>
     */
    @Test
    public void testValidateFormHardMaxTemperatureToNormalUpperLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperatureTo("999");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温(To)に負の整数3桁の下限が入力された場合(-999)
     * </pre>
     */
    @Test
    public void testValidateFormHardMaxTemperatureToNormalLowerLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperatureTo("-999");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温(To)に正の整数3桁を超える入力がされた場合(1000)
     * </pre>
     */
    @Test
    public void testValidateFormHardMaxTemperatureToAbnormalUpperLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperatureTo("1000");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("最高気温は3桁以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温(To)に負の整数3桁を下回る入力がされた場合(-1000)
     * </pre>
     */
    @Test
    public void testValidateFormHardMaxTemperatureToAbnormalLowerLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperatureTo("-1000");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("最高気温は3桁以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温(From)に数値以外のものが入力された場合
     * </pre>
     */
    @Test
    public void testValidateFormHardMinTemperatureFromAbnormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperatureFrom("あ");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("最低気温は数値で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温(From)に正の整数3桁の上限が入力された場合(999)
     * </pre>
     */
    @Test
    public void testValidateFormHardMinTemperatureFromNormalUpperLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperatureFrom("999");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温(From)に負の整数3桁の下限が入力された場合(-999)
     * </pre>
     */
    @Test
    public void testValidateFormHardMinTemperatureFromNormalLowerLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperatureFrom("-999");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温(From)に正の整数3桁を超える入力がされた場合(1000)
     * </pre>
     */
    @Test
    public void testValidateFormHardMinTemperatureFromAbnormalUpperLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperatureFrom("1000");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("最低気温は3桁以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温(From)に負の整数3桁を下回る入力がされた場合(-1000)
     * </pre>
     */
    @Test
    public void testValidateFormHardMinTemperatureFromAbnormalLowerLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperatureFrom("-1000");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("最低気温は3桁以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温(To)に数値以外のものが入力された場合
     * </pre>
     */
    @Test
    public void testValidateFormHardMinTemperatureToAbnormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperatureTo("あ");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("最低気温は数値で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温(To)に正の整数3桁の上限が入力された場合(999)
     * </pre>
     */
    @Test
    public void testValidateFormHardMinTemperatureToNormalUpperLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperatureTo("999");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温(To)に負の整数3桁の下限が入力された場合(-999)
     * </pre>
     */
    @Test
    public void testValidateFormHardMinTemperatureToNormalLowerLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperatureTo("-999");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温(To)に正の整数3桁を超える入力がされた場合(1000)
     * </pre>
     */
    @Test
    public void testValidateFormHardMinTemperatureToAbnormalUpperLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperatureTo("1000");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("最低気温は3桁以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateFormHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温(To)に負の整数3桁を下回る入力がされた場合(-1000)
     * </pre>
     */
    @Test
    public void testValidateFormHardMinTemperatureToAbnormalLowerLimit() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperatureTo("-1000");
        List<String> errorList = target.validateFormHard(form);

        assertThat(errorList.get(0), is("最低気温は3桁以内で入力してください。"));
    }

    /**
     * {@link WeatherSearchLogic#validateBetweenItem(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 全ての項目に正常な値が入力された場合
     * </pre>
     */
    @Test
    public void testValidateBetweenItemNormal() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("2015/01/01");
        form.setWeatherDateTo("2015/02/01");
        form.setMaxTemperatureFrom("20");
        form.setMaxTemperatureTo("30");
        form.setMinTemperatureFrom("10");
        form.setMinTemperatureTo("20");
        List<String> errorList = target.validateBetweenItem(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateBetweenItem(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 全ての項目がブランクの場合
     * </pre>
     */
    @Test
    public void testValidateBetweenItemNormalAllBlank() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("");
        form.setWeatherDateTo("");
        form.setMaxTemperatureFrom("");
        form.setMaxTemperatureTo("");
        form.setMinTemperatureFrom("");
        form.setMinTemperatureTo("");
        List<String> errorList = target.validateBetweenItem(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateBetweenItem(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 全ての項目がnullの場合
     * </pre>
     */
    @Test
    public void testValidateBetweenItemNormalAllNull() {
        WeatherSearchForm form = new WeatherSearchForm();
        List<String> errorList = target.validateBetweenItem(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateBetweenItem(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付、最高気温、最低気温のFromのみが入力された場合
     * </pre>
     */
    @Test
    public void testValidateBetweenItemNormalFromOnly() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("2015/01/01");
        form.setMaxTemperatureFrom("20");
        form.setMinTemperatureFrom("10");
        List<String> errorList = target.validateBetweenItem(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateBetweenItem(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付、最高気温、最低気温のToのみが入力された場合
     * </pre>
     */
    @Test
    public void testValidateBetweenItemNormalToOnly() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateTo("2015/01/01");
        form.setMaxTemperatureTo("20");
        form.setMinTemperatureTo("10");
        List<String> errorList = target.validateBetweenItem(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateBetweenItem(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付の範囲指定(From,To)が同じ場合
     * </pre>
     */
    @Test
    public void testValidateBetweenItemWeatherDateNormalFromToEqual() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("2015/02/01");
        form.setWeatherDateTo("2015/02/01");
        List<String> errorList = target.validateBetweenItem(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateBetweenItem(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付の範囲指定(From,To)が不正な場合(From>To)
     * </pre>
     */
    @Test
    public void testValidateBetweenItemWeatherDateAbnormalFromTo() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("2015/02/02");
        form.setWeatherDateTo("2015/02/01");
        List<String> errorList = target.validateBetweenItem(form);

        assertThat(errorList.get(0), is("日付の範囲指定が不正です。"));
    }

    /**
     * {@link WeatherSearchLogic#validateBetweenItem(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温の範囲指定(From,To)が不正な場合(From=To)
     * </pre>
     */
    @Test
    public void testValidateBetweenItemMaxTemperatureNormalFromToEqual() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperatureFrom("20");
        form.setMaxTemperatureTo("20");
        List<String> errorList = target.validateBetweenItem(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateBetweenItem(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温の範囲指定(From,To)が不正な場合(From>To)
     * </pre>
     */
    @Test
    public void testValidateBetweenItemMaxTemperatureAbnormalFromTo() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperatureFrom("20");
        form.setMaxTemperatureTo("19");
        List<String> errorList = target.validateBetweenItem(form);

        assertThat(errorList.get(0), is("最高気温の範囲指定が不正です。"));
    }

    /**
     * {@link WeatherSearchLogic#validateBetweenItem(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温の範囲指定(From,To)が不正な場合(From=To)
     * </pre>
     */
    @Test
    public void testValidateBetweenItemMinTemperatureNormalFromToEqual() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperatureFrom("-10");
        form.setMinTemperatureTo("-10");
        List<String> errorList = target.validateBetweenItem(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#validateBetweenItem(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温の範囲指定(From,To)が不正な場合(From>To)
     * </pre>
     */
    @Test
    public void testValidateBetweenItemMinTemperatureAbnormalFromTo() {
        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperatureFrom("-10");
        form.setMinTemperatureTo("-11");
        List<String> errorList = target.validateBetweenItem(form);

        assertThat(errorList.get(0), is("最低気温の範囲指定が不正です。"));
    }

    /**
     * {@link WeatherSearchLogic#findBySql(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 検索条件を設定せずに検索を行った場合
     * </pre>
     */
    @Test
    public void testFindBySqlNoCondition() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        WeatherSearchForm form = new WeatherSearchForm();
        List<Weather> resultWeatherList = target.findBySql(form);
        assertThat(resultWeatherList.size(), is(2));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("7"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-3"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(1).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(1).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("11"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("6"));
    }

    /**
     * {@link WeatherSearchLogic#findBySql(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 全ての項目を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlAllCondition() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDate("2015/01/02");
        form.setPlace("東京");
        form.setWeather("曇り");
        form.setMaxTemperature("11");
        form.setMinTemperature("6");

        List<Weather> resultWeatherList = target.findBySql(form);
        assertThat(resultWeatherList.size(), is(1));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(0).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(0).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("11"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("6"));
    }

    /**
     * {@link WeatherSearchLogic#findBySql(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 検索結果が0件となる条件の場合
     * </pre>
     */
    @Test
    public void testFindBySqlNoResult() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDate("2015/01/01");
        form.setPlace("東京");

        List<Weather> resultWeatherList = target.findBySql(form);
        assertThat(resultWeatherList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#findBySql(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlWeatherDate() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDate("2015/01/01");

        List<Weather> resultWeatherList = target.findBySql(form);
        assertThat(resultWeatherList.size(), is(1));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("7"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-3"));
    }

    /**
     * {@link WeatherSearchLogic#findBySql(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 場所を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlPlace() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setPlace("群馬");

        List<Weather> resultWeatherList = target.findBySql(form);
        assertThat(resultWeatherList.size(), is(1));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("7"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-3"));
    }

    /**
     * {@link WeatherSearchLogic#findBySql(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 天気を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlWeather() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeather("晴れ");

        List<Weather> resultWeatherList = target.findBySql(form);
        assertThat(resultWeatherList.size(), is(1));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("7"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-3"));
    }

    /**
     * {@link WeatherSearchLogic#findBySql(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlMaxTemperature() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperature("7");

        List<Weather> resultWeatherList = target.findBySql(form);
        assertThat(resultWeatherList.size(), is(1));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("7"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-3"));
    }

    /**
     * {@link WeatherSearchLogic#findBySql(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlMinTemperature() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperature("-3");

        List<Weather> resultWeatherList = target.findBySql(form);
        assertThat(resultWeatherList.size(), is(1));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("7"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-3"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 検索条件を設定せずに検索を行った場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardNoCondition() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        WeatherSearchForm form = new WeatherSearchForm();
        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(2));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("7"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-3"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(1).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(1).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("11"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("6"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 全ての項目を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardAllCondition() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("2015/01/01");
        form.setWeatherDateTo("2015/01/02");
        form.setPlace("東京");
        form.setWeather("晴れ,曇り");
        form.setMaxTemperatureFrom("10");
        form.setMaxTemperatureTo("20");
        form.setMinTemperatureFrom("5");
        form.setMinTemperatureTo("10");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(1));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(0).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(0).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("11"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("6"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 検索結果が0件となる条件の場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardNoResult() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("2015/01/02");
        form.setPlace("群馬");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(0));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付の範囲指定(From)を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardWeatherDateFrom() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));
        weatherDao.insert(String.format(insertSql, "2015/01/03", "埼玉", "雨", "10", "3"));
        weatherDao.insert(String.format(insertSql, "2015/01/04", "神奈川", "雪", "3", "-1"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("2015/01/02");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(3));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(0).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(0).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("11"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("6"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/03"));
        assertThat(resultWeatherList.get(1).getPlace(), is("埼玉"));
        assertThat(resultWeatherList.get(1).getWeather(), is("雨"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("10"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("3"));
        assertThat(resultWeatherList.get(2).getWeatherDate(), is("2015/01/04"));
        assertThat(resultWeatherList.get(2).getPlace(), is("神奈川"));
        assertThat(resultWeatherList.get(2).getWeather(), is("雪"));
        assertThat(resultWeatherList.get(2).getMaxTemperature(), is("3"));
        assertThat(resultWeatherList.get(2).getMinTemperature(), is("-1"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付の範囲指定(To)を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardWeatherDateTo() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));
        weatherDao.insert(String.format(insertSql, "2015/01/03", "埼玉", "雨", "10", "3"));
        weatherDao.insert(String.format(insertSql, "2015/01/04", "神奈川", "雪", "3", "-1"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateTo("2015/01/03");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(3));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("7"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-3"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(1).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(1).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("11"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("6"));
        assertThat(resultWeatherList.get(2).getWeatherDate(), is("2015/01/03"));
        assertThat(resultWeatherList.get(2).getPlace(), is("埼玉"));
        assertThat(resultWeatherList.get(2).getWeather(), is("雨"));
        assertThat(resultWeatherList.get(2).getMaxTemperature(), is("10"));
        assertThat(resultWeatherList.get(2).getMinTemperature(), is("3"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 日付の範囲指定(From,To)を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardWeatherDateFromTo() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));
        weatherDao.insert(String.format(insertSql, "2015/01/03", "埼玉", "雨", "10", "3"));
        weatherDao.insert(String.format(insertSql, "2015/01/04", "神奈川", "雪", "3", "-1"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeatherDateFrom("2015/01/02");
        form.setWeatherDateTo("2015/01/03");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(2));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(0).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(0).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("11"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("6"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/03"));
        assertThat(resultWeatherList.get(1).getPlace(), is("埼玉"));
        assertThat(resultWeatherList.get(1).getWeather(), is("雨"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("10"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("3"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 場所を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardPlace() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "7", "-3"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "11", "6"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setPlace("群馬");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(1));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("7"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-3"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 1つの天気を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardWeather1() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "-1", "-2"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "0", "-1"));
        weatherDao.insert(String.format(insertSql, "2015/01/03", "埼玉", "雨", "1", "0"));
        weatherDao.insert(String.format(insertSql, "2015/01/04", "神奈川", "雪", "2", "1"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeather("晴れ");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(1));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("-1"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-2"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 2つの天気を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardWeather2() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "-1", "-2"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "0", "-1"));
        weatherDao.insert(String.format(insertSql, "2015/01/03", "埼玉", "雨", "1", "0"));
        weatherDao.insert(String.format(insertSql, "2015/01/04", "神奈川", "雪", "2", "1"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeather("晴れ,曇り");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(2));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("-1"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-2"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(1).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(1).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("0"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("-1"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 3つの天気を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardWeatherSelect3() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "-1", "-2"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "0", "-1"));
        weatherDao.insert(String.format(insertSql, "2015/01/03", "埼玉", "雨", "1", "0"));
        weatherDao.insert(String.format(insertSql, "2015/01/04", "神奈川", "雪", "2", "1"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeather("晴れ,曇り,雨");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(3));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("-1"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-2"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(1).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(1).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("0"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("-1"));
        assertThat(resultWeatherList.get(2).getWeatherDate(), is("2015/01/03"));
        assertThat(resultWeatherList.get(2).getPlace(), is("埼玉"));
        assertThat(resultWeatherList.get(2).getWeather(), is("雨"));
        assertThat(resultWeatherList.get(2).getMaxTemperature(), is("1"));
        assertThat(resultWeatherList.get(2).getMinTemperature(), is("0"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 4つの天気を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardWeatherSelect4() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "-1", "-2"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "0", "-1"));
        weatherDao.insert(String.format(insertSql, "2015/01/03", "埼玉", "雨", "1", "0"));
        weatherDao.insert(String.format(insertSql, "2015/01/04", "神奈川", "雪", "2", "1"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setWeather("晴れ,曇り,雨,雪");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(4));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("-1"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-2"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(1).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(1).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("0"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("-1"));
        assertThat(resultWeatherList.get(2).getWeatherDate(), is("2015/01/03"));
        assertThat(resultWeatherList.get(2).getPlace(), is("埼玉"));
        assertThat(resultWeatherList.get(2).getWeather(), is("雨"));
        assertThat(resultWeatherList.get(2).getMaxTemperature(), is("1"));
        assertThat(resultWeatherList.get(2).getMinTemperature(), is("0"));
        assertThat(resultWeatherList.get(3).getWeatherDate(), is("2015/01/04"));
        assertThat(resultWeatherList.get(3).getPlace(), is("神奈川"));
        assertThat(resultWeatherList.get(3).getWeather(), is("雪"));
        assertThat(resultWeatherList.get(3).getMaxTemperature(), is("2"));
        assertThat(resultWeatherList.get(3).getMinTemperature(), is("1"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温の範囲指定(From)を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardMaxTemperatureFrom() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "-1", "-2"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "0", "-1"));
        weatherDao.insert(String.format(insertSql, "2015/01/03", "埼玉", "雨", "1", "0"));
        weatherDao.insert(String.format(insertSql, "2015/01/04", "神奈川", "雪", "2", "1"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperatureFrom("0");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(3));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(0).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(0).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("0"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-1"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/03"));
        assertThat(resultWeatherList.get(1).getPlace(), is("埼玉"));
        assertThat(resultWeatherList.get(1).getWeather(), is("雨"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("1"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("0"));
        assertThat(resultWeatherList.get(2).getWeatherDate(), is("2015/01/04"));
        assertThat(resultWeatherList.get(2).getPlace(), is("神奈川"));
        assertThat(resultWeatherList.get(2).getWeather(), is("雪"));
        assertThat(resultWeatherList.get(2).getMaxTemperature(), is("2"));
        assertThat(resultWeatherList.get(2).getMinTemperature(), is("1"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温の範囲指定(To)を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardMaxTemperatureTo() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "-1", "-2"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "0", "-1"));
        weatherDao.insert(String.format(insertSql, "2015/01/03", "埼玉", "雨", "1", "0"));
        weatherDao.insert(String.format(insertSql, "2015/01/04", "神奈川", "雪", "2", "1"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperatureTo("1");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(3));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("-1"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-2"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(1).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(1).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("0"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("-1"));
        assertThat(resultWeatherList.get(2).getWeatherDate(), is("2015/01/03"));
        assertThat(resultWeatherList.get(2).getPlace(), is("埼玉"));
        assertThat(resultWeatherList.get(2).getWeather(), is("雨"));
        assertThat(resultWeatherList.get(2).getMaxTemperature(), is("1"));
        assertThat(resultWeatherList.get(2).getMinTemperature(), is("0"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最高気温の範囲指定(From,To)を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardMaxTemperatureFromTo() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "-1", "-2"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "0", "-1"));
        weatherDao.insert(String.format(insertSql, "2015/01/03", "埼玉", "雨", "1", "0"));
        weatherDao.insert(String.format(insertSql, "2015/01/04", "神奈川", "雪", "2", "1"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setMaxTemperatureFrom("0");
        form.setMaxTemperatureTo("1");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(2));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(0).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(0).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("0"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-1"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/03"));
        assertThat(resultWeatherList.get(1).getPlace(), is("埼玉"));
        assertThat(resultWeatherList.get(1).getWeather(), is("雨"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("1"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("0"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温の範囲指定(From)を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardMinTemperatureFrom() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "-1", "-2"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "0", "-1"));
        weatherDao.insert(String.format(insertSql, "2015/01/03", "埼玉", "雨", "1", "0"));
        weatherDao.insert(String.format(insertSql, "2015/01/04", "神奈川", "雪", "2", "1"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperatureFrom("-1");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(3));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(0).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(0).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("0"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-1"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/03"));
        assertThat(resultWeatherList.get(1).getPlace(), is("埼玉"));
        assertThat(resultWeatherList.get(1).getWeather(), is("雨"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("1"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("0"));
        assertThat(resultWeatherList.get(2).getWeatherDate(), is("2015/01/04"));
        assertThat(resultWeatherList.get(2).getPlace(), is("神奈川"));
        assertThat(resultWeatherList.get(2).getWeather(), is("雪"));
        assertThat(resultWeatherList.get(2).getMaxTemperature(), is("2"));
        assertThat(resultWeatherList.get(2).getMinTemperature(), is("1"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温の範囲指定(To)を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardMinTemperatureTo() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "-1", "-2"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "0", "-1"));
        weatherDao.insert(String.format(insertSql, "2015/01/03", "埼玉", "雨", "1", "0"));
        weatherDao.insert(String.format(insertSql, "2015/01/04", "神奈川", "雪", "2", "1"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperatureTo("0");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(3));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/01"));
        assertThat(resultWeatherList.get(0).getPlace(), is("群馬"));
        assertThat(resultWeatherList.get(0).getWeather(), is("晴れ"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("-1"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-2"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(1).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(1).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("0"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("-1"));
        assertThat(resultWeatherList.get(2).getWeatherDate(), is("2015/01/03"));
        assertThat(resultWeatherList.get(2).getPlace(), is("埼玉"));
        assertThat(resultWeatherList.get(2).getWeather(), is("雨"));
        assertThat(resultWeatherList.get(2).getMaxTemperature(), is("1"));
        assertThat(resultWeatherList.get(2).getMinTemperature(), is("0"));
    }

    /**
     * {@link WeatherSearchLogic#findBySqlHard(WeatherSearchForm)}のテスト。
     *
     * <pre>
     * 最低気温の範囲指定(From,To)を条件に天気情報を検索する場合
     * </pre>
     */
    @Test
    public void testFindBySqlHardMinTemperatureFromTo() {
        // 事前データ準備
        weatherDao.insert(String.format(insertSql, "2015/01/01", "群馬", "晴れ", "-1", "-2"));
        weatherDao.insert(String.format(insertSql, "2015/01/02", "東京", "曇り", "0", "-1"));
        weatherDao.insert(String.format(insertSql, "2015/01/03", "埼玉", "雨", "1", "0"));
        weatherDao.insert(String.format(insertSql, "2015/01/04", "神奈川", "雪", "2", "1"));

        WeatherSearchForm form = new WeatherSearchForm();
        form.setMinTemperatureFrom("-1");
        form.setMinTemperatureTo("0");

        List<Weather> resultWeatherList = target.findBySqlHard(form);
        assertThat(resultWeatherList.size(), is(2));
        assertThat(resultWeatherList.get(0).getWeatherDate(), is("2015/01/02"));
        assertThat(resultWeatherList.get(0).getPlace(), is("東京"));
        assertThat(resultWeatherList.get(0).getWeather(), is("曇り"));
        assertThat(resultWeatherList.get(0).getMaxTemperature(), is("0"));
        assertThat(resultWeatherList.get(0).getMinTemperature(), is("-1"));
        assertThat(resultWeatherList.get(1).getWeatherDate(), is("2015/01/03"));
        assertThat(resultWeatherList.get(1).getPlace(), is("埼玉"));
        assertThat(resultWeatherList.get(1).getWeather(), is("雨"));
        assertThat(resultWeatherList.get(1).getMaxTemperature(), is("1"));
        assertThat(resultWeatherList.get(1).getMinTemperature(), is("0"));
    }
}
