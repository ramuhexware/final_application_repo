<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Legacy Processing Results</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f4f7f6;
            margin: 0;
            padding: 40px;
            display: flex;
            justify-content: center;
        }
        .container {
            background-color: #ffffff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            width: 500px;
        }
        h2 {
            color: #2c3e50;
            margin-top: 0;
            border-bottom: 2px solid #2ecc71;
            padding-bottom: 10px;
        }
        .data-item {
            margin-bottom: 15px;
            font-size: 14px;
        }
        .data-label {
            font-weight: bold;
            color: #34495e;
        }
        .data-value {
            color: #2c3e50;
            background-color: #ecf0f1;
            padding: 8px;
            border-radius: 4px;
            margin-top: 5px;
            word-wrap: break-word;
        }
        .logs-section {
            margin-top: 20px;
            background-color: #2c3e50;
            color: #ecf0f1;
            padding: 15px;
            border-radius: 6px;
            font-family: 'Courier New', Courier, monospace;
            font-size: 13px;
        }
        .logs-title {
            font-weight: bold;
            margin-bottom: 10px;
            border-bottom: 1px solid #7f8c8d;
            padding-bottom: 5px;
            color: #2ecc71;
        }
        .log-line {
            margin-bottom: 5px;
        }
        .back-btn {
            display: inline-block;
            margin-top: 20px;
            background-color: #95a5a6;
            color: white;
            text-decoration: none;
            padding: 10px 20px;
            border-radius: 4px;
            font-weight: bold;
            text-align: center;
            width: calc(100% - 40px);
        }
        .back-btn:hover {
            background-color: #7f8c8d;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Processing Success</h2>

    <div class="data-item">
        <div class="data-label">Processed Timestamp:</div>
        <div class="data-value"><c:out value="${timestamp}"/></div>
    </div>

    <div class="data-item">
        <div class="data-label">Client Name:</div>
        <div class="data-value"><c:out value="${clientName}"/></div>
    </div>

    <c:if test="${not empty message}">
        <div class="data-item">
            <div class="data-label">Instructions / Message:</div>
            <div class="data-value"><c:out value="${message}"/></div>
        </div>
    </c:if>

    <div class="logs-section">
        <div class="logs-title">System Execution Logs:</div>
        <c:forEach var="log" items="${logs}">
            <div class="log-line">> <c:out value="${log}"/></div>
        </c:forEach>
    </div>

    <a href="process" class="back-btn">Go Back</a>
</div>
</body>
</html>
