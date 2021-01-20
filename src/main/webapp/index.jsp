<%@ page import="logic.DecodeUtil" %><%--
  Created by IntelliJ IDEA.
  User: evgeniy
  Date: 20.01.21
  Time: 09:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<link rel="stylesheet" href="styles.css">
<link rel="stylesheet" href="bootstrap.min.css">

<body>
    <div class="row" style="margin-left: 80px">
        <div class="col">
            <div class="row">
                <div class="card">
                    <div class="card-body">
                        <h4 class="card-title alert alert-success">INPUT MATRIX</h4>
                        <form action="getReport" method="POST">
                            <%
                                for (int i = 0; i < DecodeUtil.INPUT_DATA_MATRIX.numRows(); i++) {
                                    if (i == 0) {
                                        for (int k = 0; k <= DecodeUtil.INPUT_DATA_MATRIX.numCols(); k++) {
                                            if (k != 0)
                                                out.print("<input type='number' class='input_matrix' disabled value='" + (k - 1) + "'>");
                                            else
                                                out.print("<input class='input_matrix' type='number' disabled >");
                                        }
                                        out.print("<br>");

                                    }
                                    for (int j = 0; j < DecodeUtil.INPUT_DATA_MATRIX.numCols(); j++) {
                                        if (j == 0) {
                                            out.print("<input class='input_matrix' type='text' disabled  value='" + i + "'>");
                                        }
                                        out.print("<input class='input_matrix' type='number' name='" + i + j + "'" +
                                                "value='" + (int) DecodeUtil.INPUT_DATA_MATRIX.get(i, j) + "' >");
                                    }
                                    out.print("<br>");
                                }

                            %>
                            <br>
                            <input type="submit" value="START DECODING" class="btn btn-primary btn-lg btn-block"/>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="row">
                <div class="card">
                    <div class="card-body">
                        <h4 class="card-title alert alert-success">INPUT MATRIX</h4>
                        <form action="getReport" method="POST">
                            <%
                                for (int i = 0; i < DecodeUtil.INPUT_DATA_MATRIX.numRows(); i++) {
                                    if (i == 0) {
                                        for (int k = 0; k <= DecodeUtil.INPUT_DATA_MATRIX.numCols(); k++) {
                                            if (k != 0)
                                                out.print("<input type='number' class='input_matrix' disabled value='" + (k - 1) + "'>");
                                            else
                                                out.print("<input class='input_matrix' type='number' disabled >");
                                        }
                                        out.print("<br>");

                                    }
                                    for (int j = 0; j < DecodeUtil.INPUT_DATA_MATRIX.numCols(); j++) {
                                        if (j == 0) {
                                            out.print("<input class='input_matrix' type='text' disabled  value='" + i + "'>");
                                        }
                                        out.print("<input class='input_matrix' type='number' name='" + i + j + "'" +
                                                "value='" + (int) DecodeUtil.INPUT_DATA_MATRIX.get(i, j) + "' >");
                                    }
                                    out.print("<br>");
                                }

                            %>
                            <br>
                            <input type="submit" value="START DECODING" class="btn btn-primary btn-lg btn-block"/>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
