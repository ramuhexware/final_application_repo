<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Legacy JSP Portal</title>
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
            width: 450px;
        }
        h2 {
            color: #2c3e50;
            margin-top: 0;
            border-bottom: 2px solid #3498db;
            padding-bottom: 10px;
        }
        .error {
            color: #e74c3c;
            background-color: #fadbd8;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #34495e;
        }
        input[type="text"], textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #bdc3c7;
            border-radius: 4px;
            box-sizing: border-box;
        }
        textarea {
            height: 100px;
            resize: none;
        }
        input[type="submit"] {
            background-color: #3498db;
            color: white;
            border: none;
            padding: 12px 20px;
            border-radius: 4px;
            cursor: pointer;
            width: 100%;
            font-size: 16px;
            font-weight: bold;
        }
        input[type="submit"]:hover {
            background-color: #2980b9;
        }
        .footer {
            margin-top: 20px;
            font-size: 12px;
            color: #7f8c8d;
            text-align: center;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Legacy Request Center</h2>

    <c:if test="${not empty error}">
        <div class="error">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <form action="process" method="post">
        <div class="form-group">
            <label for="clientName">Client/Institution Name:</label>
            <input type="text" id="clientName" name="clientName" placeholder="e.g. Freddie Mac" />
        </div>
        <div class="form-group">
            <label for="message">Process Instructions / Message:</label>
            <textarea id="message" name="message" placeholder="Enter instructions here..."></textarea>
        </div>
        <input type="submit" value="Submit Legacy Request" />
    </form>

    <div class="footer">
        Powered by Servlet 4.0 & JSP 2.3 inside Apache Tomcat 9.0 (Java 8)
    </div>
</div>
</body>
</html>
