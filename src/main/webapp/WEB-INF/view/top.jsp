<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>開発演習TOP画面</title>
<link href="/css/bootstrap.min.css" rel="stylesheet">
<link href="/css/font-awesome.min.css" rel="stylesheet">
<link href="/css/weather.css" rel="stylesheet">
<script src="/js/jquery-2.2.2.min.js"></script>
<script src="/js/bootstrap.min.js"></script>
</head>

<body>
<jsp:include page="header.jsp" />

<div class="container">
  <div class="row">
    <div class="col-lg-2"></div>
    <div class="col-lg-8">
      <h2>Lv.0</h2>
      <a class="btn btn-default btn-block btn-info section" href="/warmUp">
        <i class="fa fa-magic"></i>　ウォーミングアップ
      </a>

      <h2>Lv.1</h2>
      <a class="btn btn-default btn-block btn-success section" href="/weatherList">
      <i class="fa fa-list" ></i>　天気一覧
      </a>

      <h2>Lv.2</h2>
      <a class="btn btn-default btn-block btn-success section" href="/weatherSimpleSearch/top">
      <i class="fa fa-search" ></i>　天気簡易検索
      </a>

      <h2>Lv.3</h2>
      <a class="btn btn-default btn-block btn-warning section" href="/weatherSearch/top">
      <i class="fa fa-search-plus"></i>　天気検索
      </a>

      <h2>Lv.4</h2>
      <a class="btn btn-default btn-block btn-warning section" href="/weatherStatistics/top">
      <i class="fa fa-bar-chart" ></i>　天気統計
      </a>

      <h2>Lv.5</h2>
      <a class="btn btn-default btn-block btn-danger section" href="/csvRegister/top">
      <i class="fa fa-database"></i>　CSVデータ登録
      </a>

      <h2>Lv.6</h2>
      <a class="btn btn-default btn-block btn-danger section" href="/weatherSearchHard/top">
      <i class="fa fa-search-plus" ></i>　天気検索発展
      </a>

    </div>
    <div class="col-lg-2"></div>
  </div>
</div>
</body>
</html>