<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>天気検索画面</title>
<link href="/css/bootstrap.min.css" rel="stylesheet">
<link href="/css/font-awesome.min.css" rel="stylesheet">
<link href="/css/jquery-ui.min.css" rel="stylesheet">
<link href="/css/weather.css" rel="stylesheet">
<script src="/js/jquery-2.2.2.min.js"></script>
<script src="/js/bootstrap.min.js"></script>
<script src="/js/jquery-ui.min.js"></script>
<script src="/js/datepicker-ja.js"></script>
</head>

<body>
<jsp:include page="header.jsp" />

<div class="container">
  <div class="row">
    <div class="col-lg-2"></div>
    <div class="col-lg-8">
     <c:if test="${!empty errorList}">
       <ul class="error">
         <c:forEach var="error" items="${errorList}">
           <li><c:out value="${error}" /></li>
         </c:forEach>
       </ul>
     </c:if>

     <h3>
      天気検索
      <img src="/img/weather2.jpg" class="width-7"">
     </h3>
     <form action="/weatherSearch/search" method="POST">
       <table class="table table-striped table-bordered table-hover">
        <thead>
          <tr class="info">
            <th>検索項目</th>
            <th>検索内容</th>
          </tr>
        </thead>
        <tbody>
            <tr>
              <td style="vertical-align: middle;">日付</td>
              <td class="text-align-center">
                <input type="text" name="weatherDate" class="form-control calendar" value="${form.weatherDate}">
              </td>
            </tr>
            <tr>
              <td style="vertical-align: middle;">場所</td>
              <td><input type="text" name="place" class="form-control" value="${form.place}"></td>
            </tr>
            <tr>
              <td style="vertical-align: middle;">天気</td>
              <td><input type="text" name="weather" class="form-control" value="${form.weather}"></td>
            </tr>
            <tr>
              <td style="vertical-align: middle;">最高気温</td>
              <td class="text-align-center">
                <input type="text" name="maxTemperature" class="form-control"  value="${form.maxTemperature}">
              </td>
            </tr>
            <tr>
              <td style="vertical-align: middle;">最低気温</td>
              <td class="text-align-center">
                <input type="text" name="minTemperature" class="form-control pull-right" value="${form.minTemperature}">
              </td>
            </tr>
        </tbody>
       </table>
       <div class="text-align-center">
         <button type="submit" class="btn btn-info" ><i class="fa fa-search"></i>  検索</button>
       </div>
     </form>

     <c:choose>
       <c:when test="${!empty weatherList}">
         <h3>
          検索結果
         </h3>
         <table class="table table-striped table-bordered table-hover">
          <thead>
            <tr class="info">
              <th width="20%">日付</th>
              <th width="20%">場所</th>
              <th width="20%">天気</th>
              <th width="20%">最高気温</th>
              <th width="20%">最低気温</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="weather" items="${weatherList}">
              <tr>
                <td style="vertical-align: middle;"><c:out value="${weather.weatherDate}" /></td>
                <td style="vertical-align: middle;"><c:out value="${weather.place}" /></td>
                <td style="vertical-align: middle;"><c:out value="${weather.weather}" /></td>
                <td style="vertical-align: middle;"><c:out value="${weather.maxTemperature}" /> ℃</td>
                <td style="vertical-align: middle;"><c:out value="${weather.minTemperature}" /> ℃</td>
              </tr>
            </c:forEach>
          </tbody>
         </table>
       </c:when>
     </c:choose>
    </div>
    <div class="col-lg-2"></div>
  </div>

 </div>
</body>
<script>
$(function() {
    $('.calendar').datepicker();
});
</script>
</html>