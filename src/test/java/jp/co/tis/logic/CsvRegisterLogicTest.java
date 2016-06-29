package jp.co.tis.logic;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
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
import jp.co.tis.exception.FileFormatException;
import jp.co.tis.form.CsvRegisterForm;
import jp.co.tis.model.Weather;
import jp.co.tis.model.WeatherDao;

/**
 * CSV登録Logicのテスト。
 *
 * @author Yoshiwara Masashi
 * @since 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@TransactionConfiguration
@Transactional
public class CsvRegisterLogicTest extends AbstractTransactionalJUnit4SpringContextTests {

    /** テスト対象クラス */
    @Autowired
    private CsvRegisterLogic target;

    /** DB操作DAO */
    @Autowired
    private WeatherDao weatherDao;

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
     * {@link CsvRegisterLogic#validateForm(CsvRegisterForm)}のテスト。
     * <pre>
     * 正常な値が入力された場合
     * </pre>
     */
    @Test
    public void testValidationNormal() {
        CsvRegisterForm form = new CsvRegisterForm();
        form.setFilePath("src/test/java/jp/co/tis/util/testData/normal.csv");

        List<String> errorList = target.validateForm(form);

        assertThat(errorList.size(), is(0));
    }

    /**
     * {@link CsvRegisterLogic#validateForm(CsvRegisterForm)}のテスト。
     * <pre>
     * 異常な値が入力された場合（ファイルパスが未入力の場合）
     * </pre>
     */
    @Test
    public void testValidationEmptyFilePath() {
        CsvRegisterForm form = new CsvRegisterForm();

        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("ファイルパスは必ず入力してください。"));
    }

    /**
     * {@link CsvRegisterLogic#validateForm(CsvRegisterForm)}のテスト。
     * <pre>
     * 異常な値が入力された場合（ファイルパスで指定したファイルの拡張子が.csvでない場合）
     * </pre>
     */
    @Test
    public void testValidationNotCsv() {
        CsvRegisterForm form = new CsvRegisterForm();
        form.setFilePath("あいうえお");

        List<String> errorList = target.validateForm(form);

        assertThat(errorList.get(0), is("ファイルの拡張子はcsv形式にしてください。"));
    }

    /**
     * {@link CsvRegisterLogic#createCsvDataList(CsvRegisterForm)}のテスト。
     * <pre>
     * 正常な値が入力された場合
     * </pre>
     */
    @Test
    public void testCsvReadNormal() {
        CsvRegisterForm form = new CsvRegisterForm();
        form.setFilePath("src/test/java/jp/co/tis/util/testData/normal.csv");

        try {
            List<String> csvDataList = target.createCsvDataList(form);
            assertThat(csvDataList.get(0), is("'1','ikeda','25'"));
            assertThat(csvDataList.get(1), is("'2','noda','22'"));
            assertThat(csvDataList.get(2), is("'3','nagamine','24'"));
        } catch (FileNotFoundException | FileFormatException e) {
            fail();
        }
    }

    /**
     * {@link CsvRegisterLogic#createCsvDataList(CsvRegisterForm)}のテスト。
     * <pre>
     * 異常な値が入力された場合（存在しないCSVファイルに読み込み処理を実行した場合）
     * </pre>
     */
    @Test
    public void testCsvReadNoFile() {
        CsvRegisterForm form = new CsvRegisterForm();
        form.setFilePath("noFile.csv");

        try {
            target.createCsvDataList(form);
            fail();
        } catch (FileNotFoundException | FileFormatException e) {
            assertThat(e.getMessage(), is("noFile.csv (指定されたファイルが見つかりません。)"));
        }
    }

    /**
     * {@link CsvRegisterLogic#createCsvDataList(CsvRegisterForm)}のテスト。
     * <pre>
     * 異常な値が入力された場合（ヘッダー部が存在しないCSVファイルに読み込み処理を実行した場合）
     * </pre>
     */
    @Test
    public void testCsvReadNoHeader() {
        CsvRegisterForm form = new CsvRegisterForm();
        form.setFilePath("src/test/java/jp/co/tis/util/testData/testOpenInCaseOfFileFormatException1.csv");

        try {
            target.createCsvDataList(form);
            fail();
        } catch (FileNotFoundException | FileFormatException e) {
            assertThat(e.getMessage(), is("ヘッダー部が存在しません。"));
        }
    }

    /**
     * {@link CsvRegisterLogic#createCsvDataList(CsvRegisterForm)}のテスト。
     * <pre>
     * 異常な値が入力された場合（ヘッダー部の項目名数とデータ部のデータ数が一致しないCSVファイルに読み込み処理を実行した場合）
     * </pre>
     */
    @Test
    public void testCsvReadDeferentNumberOfItemBetweenHeaderAndData() {
        CsvRegisterForm form = new CsvRegisterForm();
        form.setFilePath("src/test/java/jp/co/tis/util/testData/testReadLineInCaseOfFileFormatException.csv");

        try {
            target.createCsvDataList(form);
            fail();
        } catch (FileNotFoundException | FileFormatException e) {
            assertThat(e.getMessage(), is("2行目 ：ヘッダー部と項目数が異なっています。"));
        }
    }

    /**
     * {@link CsvRegisterLogic#insert(List<String>)}のテスト。
     * <pre>
     * 登録処理
     * </pre>
     */
    @Test
    public void testInsertNormal() {
        List<String> csvDataList = new ArrayList<String>();
        csvDataList.add("'2016/01/01','東京','晴れ','10','5'");
        target.insert(csvDataList);

        List<Weather> result = weatherDao.findBySql("SELECT * FROM WEATHER");
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getWeatherDate(), is("2016/01/01"));
        assertThat(result.get(0).getPlace(), is("東京"));
        assertThat(result.get(0).getWeather(), is("晴れ"));
        assertThat(result.get(0).getMaxTemperature(), is("10"));
        assertThat(result.get(0).getMinTemperature(), is("5"));
    }

}
