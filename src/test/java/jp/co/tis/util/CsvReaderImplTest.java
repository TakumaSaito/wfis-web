package jp.co.tis.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import jp.co.tis.exception.FileFormatException;

/**
 * CsvReaderImplをテストするクラス。
 *
 * @author Yoshiwara Masashi
 * @since 1.0
 */
public class CsvReaderImplTest {

    /**
     * ルール設定
     */
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * {@link CsvReaderImpl#open()}のテスト。
     * 
     * <pre>
     * 正常なCSVファイルを読み込む場合
     * </pre>
     *
     * @throws Exception 例外。
     */
    @Test
    public void testOpenNormally() throws Exception {
        CsvReader csvReader = new CsvReaderImpl("src/test/java/jp/co/tis/util/testData/normal.csv");
        csvReader.open();
    }

    /**
     * {@link CsvReaderImpl#open()}のテスト。
     * 
     * <pre>
     * すでに開かれているファイルに対してopenメソッドを実行する場合
     * </pre>
     *
     * @throws Exception 例外。
     */
    @Test
    public void testOpenInCaseOfFileAlreadyOpen() throws Exception {
        CsvReader csvReader = new CsvReaderImpl("src/test/java/jp/co/tis/util/testData/normal.csv");
        exception.expect(IllegalStateException.class);
        exception.expectMessage("すでにファイルが開かれています。");
        csvReader.open();
        csvReader.open();
    }

    /**
     * {@link CsvReaderImpl#open()}のテスト。
     * 
     * <pre>
     * フィルパスに存在しないファイルパスを指定した場合
     * </pre>
     *
     * @throws Exception 例外。
     */
    @Test
    public void testOpenInCaseOfFileNotFoundException() throws Exception {
        CsvReader csvReader = new CsvReaderImpl("noFile.csv");
        exception.expect(FileNotFoundException.class);
        // JavaAPIからthrowされる例外なので、例外発生のみチェックしメッセージ内容のチェックはしない。
        csvReader.open();
    }

    /**
     * {@link CsvReaderImpl#open()}のテスト。
     * 
     * <pre>
     * フィルパスにヘッダー部が存在しないCSVファイルのファイルパスを指定した場合
     * </pre>
     *
     * @throws Exception 例外。
     */
    @Test
    public void testOpenInCaseOfFileFormatException() throws Exception {
        CsvReader csvReader = new CsvReaderImpl("src/test/java/jp/co/tis/util/testData/testOpenInCaseOfFileFormatException1.csv");
        exception.expect(FileFormatException.class);
        exception.expectMessage("ヘッダー部が存在しません。");
        csvReader.open();
    }

    /**
     * {@link CsvReaderImpl#open()}のテスト。
     * 
     * <pre>
     * フィルパスにヘッダー部に重複が存在するCSVファイルのファイルパスを指定した場合
     * </pre>
     *
     * @throws Exception 例外。
     */
    @Test
    public void testOpenInCaseOfFileFormatException2() throws Exception {
        CsvReader csvReader = new CsvReaderImpl("src/test/java/jp/co/tis/util/testData/testOpenInCaseOfFileFormatException2.csv");
        exception.expect(FileFormatException.class);
        exception.expectMessage("ヘッダー部の項目が重複しています。");
        csvReader.open();
    }

    /**
     * {@link CsvReaderImpl#open()}のテスト。
     * 
     * <pre>
     * フィルパスにヘッダー部に空項目が存在するCSVファイルのファイルパスを指定した場合
     * </pre>
     *
     * @throws Exception 例外。
     */
    @Test
    public void testOpenInCaseOfFileFormatException3() throws Exception {
        CsvReader csvReader = new CsvReaderImpl("src/test/java/jp/co/tis/util/testData/testOpenInCaseOfFileFormatException3.csv");
        exception.expect(FileFormatException.class);
        exception.expectMessage("ヘッダー部に空項目が含まれています。");
        csvReader.open();
    }

    /**
     * {@link CsvReaderImpl#close()}のテスト。
     * 
     * <pre>
     * ファイルが開かれた状態でcloseメソッドを実行した場合
     * </pre>
     *
     * @throws Exception 例外。
     */
    @Test
    public void testCloseAfterOpen() throws Exception {
        CsvReader csvReader = new CsvReaderImpl("src/test/java/jp/co/tis/util/testData/normal.csv");
        csvReader.open();
        csvReader.close();
        exception.expect(IOException.class);
        exception.expectMessage("ファイルが開かれていません。");
        csvReader.readLine();
    }

    /**
     * {@link CsvReaderImpl#readLine()}のテスト。
     * 
     * <pre>
     * 次の読み込み行がある状態でreadLineメソッドを実行した場合
     * </pre>
     *
     * @throws Exception 例外。
     */
    @Test
    public void testReadLineOneTime() throws Exception {
        CsvReader csvReader = new CsvReaderImpl("src/test/java/jp/co/tis/util/testData/normal.csv");
        csvReader.open();
        Map<String, String> map = csvReader.readLine();
        assertThat(map.get("id"), is("1"));
        assertThat(map.get("name"), is("ikeda"));
        assertThat(map.get("age"), is("25"));

    }

    /**
     * {@link CsvReaderImpl#readLine()}のテスト。
     * 
     * <pre>
     * 次の読み込み行がない状態でreadLineメソッドを実行した場合
     * </pre>
     *
     * @throws Exception 例外。
     */
    @Test
    public void testReadLineAtFileEnd() throws Exception {
        CsvReader csvReader = new CsvReaderImpl("src/test/java/jp/co/tis/util/testData/normal.csv");
        csvReader.open();
        csvReader.readLine();
        csvReader.readLine();
        csvReader.readLine();
        assertThat(csvReader.readLine(), is(nullValue()));
    }

    /**
     * {@link CsvReaderImpl#readLine()}のテスト。
     * 
     * <pre>
     * 次の読み込み行がヘッダー部と項目数が異なる状態でreadLineメソッドを実行した場合
     * </pre>
     *
     * @throws Exception 例外。
     */
    @Test
    public void testReadLineInCaseOfFileFormatException() throws Exception {
        CsvReader csvReader = new CsvReaderImpl("src/test/java/jp/co/tis/util/testData/testReadLineInCaseOfFileFormatException.csv");
        csvReader.open();
        exception.expect(FileFormatException.class);
        exception.expectMessage("ヘッダー部と項目数が異なっています。");
        csvReader.readLine();
    }

    /**
     * {@link CsvReaderImpl#readLine()}のテスト。
     * 
     * <pre>
     * データ部がないCSVファイルに対してreadLineメソッドを実行した場合
     * </pre>
     *
     * @throws Exception 例外。
     */
    @Test
    public void testReadLineWhenMapIsNull() throws Exception {
        CsvReader csvReader = new CsvReaderImpl("src/test/java/jp/co/tis/util/testData/testReadLineWhenMapIsNull.csv");
        csvReader.open();
        assertThat(csvReader.readLine(), is(nullValue()));
    }

    /**
     * {@link CsvReaderImpl#readLine()}のテスト。
     * 
     * <pre>
     * 次の読み込み行に空項目が含まれる状態でreadLineメソッドを実行した場合
     * </pre>
     *
     * @throws Exception 例外。
     */
    @Test
    public void testReadLineWhenDataHaveBlank() throws Exception {
        CsvReader csvReader = new CsvReaderImpl("src/test/java/jp/co/tis/util/testData/testReadLineWhenDataHaveBlank.csv");
        csvReader.open();
        Map<String, String> map = csvReader.readLine();
        assertThat(map.get("A"), is(nullValue()));
        assertThat(map.get("B"), is(" "));
        assertThat(map.get("C"), is("  "));
    }

}
