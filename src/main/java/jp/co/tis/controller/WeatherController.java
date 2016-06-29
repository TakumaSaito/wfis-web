package jp.co.tis.controller;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jp.co.tis.exception.FileFormatException;
import jp.co.tis.form.CsvRegisterForm;
import jp.co.tis.form.WeatherSearchForm;
import jp.co.tis.form.WeatherStatisticsForm;
import jp.co.tis.logic.CsvRegisterLogic;
import jp.co.tis.logic.WeatherSearchLogic;
import jp.co.tis.logic.WeatherStatisticsLogic;
import jp.co.tis.model.Person;
import jp.co.tis.model.Weather;
import jp.co.tis.model.WeatherStatisticsDto;

/**
 * 天気予報のコントローラークラス。
 *
 * @author Saito Takuma
 * @since 1.0
 */
@Controller
public class WeatherController {

    /** 天気検索ロジッククラス */
    @Autowired
    private WeatherSearchLogic weatherSearchLogic;

    /** 天気統計ロジッククラス */
    @Autowired
    private WeatherStatisticsLogic weatherStatisticsLogic;

    /** CSV登録ロジッククラス */
    @Autowired
    private CsvRegisterLogic csvRegisterLogic;

    /**
     * TOP画面へ遷移する。
     *
     * @return ModelAndView
     */
    @RequestMapping("/")
    public ModelAndView top() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("top");

        return modelAndView;
    }

    /**
     * ウォーミングアップ画面へ遷移する。
     *
     * @return ModelAndView
     */
    @RequestMapping("/warmUp")
    public ModelAndView warmUp() {
        ModelAndView modelAndView = new ModelAndView();
        Person person = weatherSearchLogic.createPersonInfo();

        // personごとJSPに渡して、JSP側でperson.myNameのように取り出してもよい
        modelAndView.addObject("myName", person.getMyName());
        modelAndView.addObject("age", person.getAge());
        modelAndView.addObject("hobby", person.getHobby());
        modelAndView.addObject("skill", person.getSkill());
        modelAndView.setViewName("warmUp");

        return modelAndView;
    }

    /**
     * 天気一覧画面へ遷移する。
     *
     * @return ModelAndView
     */
    @RequestMapping("/weatherList")
    public ModelAndView weatherList() {
        ModelAndView modelAndView = new ModelAndView();
        List<Weather> weatherList = weatherSearchLogic.findBySqlWeatherList();

        modelAndView.addObject("weatherList", weatherList);
        modelAndView.setViewName("weatherList");
        return modelAndView;
    }

    /**
     * 天気簡易検索TOP画面へ遷移する。
     *
     * @return ModelAndView
     */
    @RequestMapping("/weatherSimpleSearch/top")
    public ModelAndView weatherSimpleSearchTop() {
        ModelAndView modelAndView = new ModelAndView();
        List<Weather> weatherList = weatherSearchLogic.findBySqlWeatherList();

        modelAndView.addObject("weatherList", weatherList);
        modelAndView.setViewName("weatherSimpleSearch");
        return modelAndView;
    }

    /**
     * 天気検索TOP画面へ遷移する。
     *
     * @return ModelAndView
     */
    @RequestMapping("/weatherSearch/top")
    public ModelAndView weatherSearchTop() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("weatherSearch");
        return modelAndView;
    }

    /**
     * 天気検索発展TOP画面へ遷移する。
     *
     * @return ModelAndView
     */
    @RequestMapping("/weatherSearchHard/top")
    public ModelAndView weatherSearchHardTop() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("weatherSearchHard");
        return modelAndView;
    }

    /**
     * 天気統計TOP画面へ遷移する。
     *
     * @return ModelAndView
     */
    @RequestMapping("/weatherStatistics/top")
    public ModelAndView weatherStatisticsTop() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("weatherStatistics");
        return modelAndView;
    }

    /**
     * CSV登録TOP画面へ遷移する。
     *
     * @return ModelAndView
     */
    @RequestMapping("/csvRegister/top")
    public ModelAndView csvRegisterTop() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("csvRegister");
        return modelAndView;
    }

    /**
     * 天気の検索を行う（天気簡易検索）。
     *
     * @param form フォーム
     * @return ModelAndView
     */
    @RequestMapping("/weatherSimpleSearch/search")
    public ModelAndView simpleSearch(WeatherSearchForm form) {
        ModelAndView modelAndView = new ModelAndView();
        List<Weather> weatherList = weatherSearchLogic.findBySqlSimple(form);

        modelAndView.addObject("form", form);
        modelAndView.addObject("weatherList", weatherList);
        modelAndView.setViewName("weatherSimpleSearch");
        return modelAndView;
    }

    /**
     * 天気の検索を行う（天気検索）。
     *
     * @param form フォーム
     * @return ModelAndView
     */
    @RequestMapping("/weatherSearch/search")
    public ModelAndView search(WeatherSearchForm form) {
        ModelAndView modelAndView = new ModelAndView();

        // 項目精査を行う
        List<String> errorList = weatherSearchLogic.validateForm(form);
        if (!errorList.isEmpty()) {
            modelAndView.addObject("errorList", errorList);
            modelAndView.addObject("form", form);
            modelAndView.setViewName("weatherSearch");
            return modelAndView;
        }

        List<Weather> weatherList = weatherSearchLogic.findBySql(form);
        if (weatherList.isEmpty()) {
            errorList.add("検索結果がありません。");
            modelAndView.addObject("errorList", errorList);
            modelAndView.addObject("form", form);
            modelAndView.setViewName("weatherSearch");
        }

        modelAndView.addObject("form", form);
        modelAndView.addObject("weatherList", weatherList);
        modelAndView.setViewName("weatherSearch");
        return modelAndView;
    }

    /**
     * 天気の検索を行う（天気検索発展）。
     *
     * @param form フォーム
     * @return ModelAndView
     */
    @RequestMapping("/weatherSearchHard/search")
    public ModelAndView searchHard(WeatherSearchForm form) {
        ModelAndView modelAndView = new ModelAndView();

        // 項目精査を行う
        List<String> errorList = weatherSearchLogic.validateFormHard(form);
        if (!errorList.isEmpty()) {
            modelAndView.addObject("errorList", errorList);
            modelAndView.addObject("form", form);
            modelAndView.setViewName("weatherSearchHard");
            return modelAndView;
        }

        errorList = weatherSearchLogic.validateBetweenItem(form);
        if (!errorList.isEmpty()) {
            modelAndView.addObject("errorList", errorList);
            modelAndView.addObject("form", form);
            modelAndView.setViewName("weatherSearchHard");
            return modelAndView;
        }

        List<Weather> weatherList = weatherSearchLogic.findBySqlHard(form);
        if (weatherList.isEmpty()) {
            modelAndView.addObject("noResult", Boolean.TRUE);
        }

        modelAndView.addObject("form", form);
        modelAndView.addObject("weatherList", weatherList);
        modelAndView.addObject("searchCount", weatherList.size());
        modelAndView.setViewName("weatherSearchHard");
        return modelAndView;
    }

    /**
     * 天気の統計処理を行う。
     *
     * @param form フォーム
     * @return ModelAndView
     */
    @RequestMapping("/weatherStatistics/analysis")
    public ModelAndView statistics(WeatherStatisticsForm form) {
        ModelAndView modelAndView = new ModelAndView();

        // 項目精査を行う
        List<String> errorList = weatherStatisticsLogic.validateForm(form);
        if (!errorList.isEmpty()) {
            modelAndView.addObject("errorList", errorList);
            modelAndView.addObject("form", form);
            modelAndView.setViewName("weatherStatistics");
            return modelAndView;
        }

        // 過去の天気の統計を検索する
        List<Weather> pastWeatherList = weatherStatisticsLogic.createPastWeatherList(form);
        if (pastWeatherList.isEmpty()) {
            errorList.add("データが存在しませんでした。");
            modelAndView.addObject("errorList", errorList);
            modelAndView.addObject("form", form);
            modelAndView.setViewName("weatherStatistics");
            return modelAndView;
        }

        WeatherStatisticsDto weatherStatisticsDto = weatherStatisticsLogic.createWeatherStatisticsDto(form, pastWeatherList);
        modelAndView.addObject("form", form);
        modelAndView.addObject("weatherStatisticsDto", weatherStatisticsDto);
        modelAndView.setViewName("weatherStatistics");
        return modelAndView;
    }

    /**
     * CSVファイルを読み込んでデータをテーブルに登録する。
     *
     * @param form フォーム
     * @return ModelAndView
     */
    @Transactional
    @RequestMapping("/csvRegister/insert")
    public ModelAndView csvRegister(CsvRegisterForm form) {
        ModelAndView modelAndView = new ModelAndView();

        // 項目精査
        List<String> errorList = csvRegisterLogic.validateForm(form);
        if (!errorList.isEmpty()) {
            modelAndView.addObject("filePath", form.getFilePath());
            modelAndView.addObject("errorList", errorList);
            modelAndView.setViewName("csvRegister");
            return modelAndView;
        }

        // CSV読み込み処理
        List<String> csvDataList = new ArrayList<String>();
        try {
            csvDataList = csvRegisterLogic.createCsvDataList(form);
            if (csvDataList.isEmpty()) {
                errorList.add("登録するデータが存在しません。");
                modelAndView.addObject("filePath", form.getFilePath());
                modelAndView.addObject("errorList", errorList);
                modelAndView.setViewName("csvRegister");
                return modelAndView;
            }
        } catch (FileNotFoundException | FileFormatException e) {
            errorList.add(e.getMessage());
            modelAndView.addObject("filePath", form.getFilePath());
            modelAndView.addObject("errorList", errorList);
            modelAndView.setViewName("csvRegister");
            return modelAndView;
        }
        // CSV登録処理
        csvRegisterLogic.insert(csvDataList);

        modelAndView.setViewName("complete");
        return modelAndView;
    }
}
