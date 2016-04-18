package jp.co.tis.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jp.co.tis.exception.FileFormatException;
import jp.co.tis.exception.SystemException;


/**
 * CsvReader実装クラス。
 *
 * @author Nomura Tomoka, Murakami Hiroyuki
 * @since 2.0
 */
public class CsvReaderImpl implements CsvReader {

    /** 読み込みのためのBufferedReaderクラス。 */
    private BufferedReader bufferedReader;

    /** ヘッダー部の項目を格納する配列。 */
    private String[] itemArray;

    /** CSVファイルのパスを格納する文字列。 */
    private String csvPath;

    /**
     * コンストラクタ。
     *
     * @param csvPath CSVファイルパス。
     */
    public CsvReaderImpl(String csvPath) {
        this.csvPath = csvPath;
    }

    @Override
    public void open() throws FileNotFoundException, FileFormatException {
        if (bufferedReader != null) {
            throw new IllegalStateException("File already open.");
        }
        bufferedReader = new BufferedReader(new FileReader(csvPath));
        // ※実行環境依存を避ける実装方法
        // 読みこみのエンコードを指定する。
        // bufferedReader = new BufferedReader(new InputStreamReader(
        //         new FileInputStream(csvPath), Charset.forName("UTF-8")));

        String headerSection = null;
        try {
            headerSection = bufferedReader.readLine();
        } catch (IOException e) {
            // テストで到達不可能
            throw new SystemException("A system exception occured.", e);
        }
        if (headerSection == null || headerSection.isEmpty()) {
            throw new FileFormatException("Input file has no header.");
        }
        itemArray = headerSection.split(",", -1);
        // 重複チェック開始
        Set<String> checkRepetition = new HashSet<String>();
        for (String item : itemArray) {
            if (!checkRepetition.add(item)) {
                throw new FileFormatException("Input file has duplicate headers.");
            }
        }
        //空項目チェック開始
        for (String item : itemArray) {
            if ("".equals(item)) {
                throw new FileFormatException("Input file has blank header.");
            }
        }

    }

    @Override
    public void close() {
        if (bufferedReader == null) {
            return;
        }
        try {
            bufferedReader.close();
        } catch (IOException e) {
            // テストで到達不可能
            throw new SystemException("A system exception occured.", e);
        } finally {
            bufferedReader = null;
        }
    }

    @Override
    public Map<String, String> readLine() throws IOException, FileFormatException {
        if (bufferedReader == null) {
            throw new IOException("File is not open.");
        }

        String dataSection = null;
        try {
            dataSection = bufferedReader.readLine();
        } catch (IOException e) {
            // テストで到達不可能
            throw new SystemException("A system exception occured.", e);
        }

        if (dataSection == null) {
            return null;
        }
        String[] dataArray = dataSection.split(",", -1);
        if (itemArray.length != dataArray.length) {
            throw new FileFormatException("Invalid data.");
        }

        Map<String, String> keyValue = new LinkedHashMap<String, String>();
        for (int i = 0; i < dataArray.length; i++) {
            if (dataArray[i].isEmpty()) {
                keyValue.put(itemArray[i], null);
            } else {
                keyValue.put(itemArray[i], dataArray[i]);
            }
        }
        return keyValue;
    }
}