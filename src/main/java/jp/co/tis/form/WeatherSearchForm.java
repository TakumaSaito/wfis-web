package jp.co.tis.form;

import java.io.Serializable;

/**
 * 天気予報用Form。<br/> 画面の入力項目や画面から送られてくる項目をプロパティとして持つクラス。
 *
 * @author Saito Takuma
 * @since 1.0
 */
public class WeatherSearchForm implements Serializable {

    /** 日付 */
    private String weatherDate;

    /** 場所 */
    private String place;

    /** 天気 */
    private String weather;

    /** 最高気温 */
    private String maxTemperature;

    /** 最低気温 */
    private String minTemperature;

    /**
     * デフォルトコンストラクタ。
     */
    public WeatherSearchForm() {
    }

    /**
     * コンストラクタ。
     *
     * @param weatherDate 日付
     * @param place 場所
     * @param weather 天気
     * @param maxTemperature 最高気温
     * @param minTemperature 最低気温
     */
    public WeatherSearchForm(String weatherDate, String place, String weather, String maxTemperature, String minTemperature) {
        this.weatherDate = weatherDate;
        this.place = place;
        this.weather = weather;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
    }

    /**
     * 日付を取得する。
     *
     * @return 日付
     */
    public String getWeatherDate() {
        return weatherDate;
    }

    /**
     * 日付を設定する。
     *
     * @param weatherDate 日付
     */
    public void setWeatherDate(String weatherDate) {
        this.weatherDate = weatherDate;
    }

    /**
     * 場所を取得する。
     *
     * @return 場所
     */
    public String getPlace() {
        return place;
    }

    /**
     * 場所を設定する。
     *
     * @param place 場所
     */
    public void setPlace(String place) {
        this.place = place;
    }

    /**
     * 天気を取得する。
     *
     * @return 天気
     */
    public String getWeather() {
        return weather;
    }

    /**
     * 天気を設定する。
     *
     * @param weather 天気
     */
    public void setWeather(String weather) {
        this.weather = weather;
    }

    /**
     * 最高気温を取得する。
     *
     * @return 最高気温
     */
    public String getMaxTemperature() {
        return maxTemperature;
    }

    /**
     * 最高気温を設定する。
     *
     * @param maxTemperature 最高気温
     */
    public void setMaxTemperature(String maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    /**
     * 最低気温を取得する。
     *
     * @return 最低気温
     */
    public String getMinTemperature() {
        return minTemperature;
    }

    /**
     * 最低気温を設定する。
     *
     * @param minTemperature 最低気温
     */
    public void setMinTemperature(String minTemperature) {
        this.minTemperature = minTemperature;
    }
}
