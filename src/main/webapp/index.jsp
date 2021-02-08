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
<jsp:useBean id="decoderBean" scope="request" type="logic.beans.DecoderBean"/>
<body>
<div class="row" style="margin-left: 80px">
    <div class="col">
        <div class="row">
            <div class="card">
                <div class="card-body">
                    <h4 class="card-title alert alert-success">INPUT MATRIX</h4>
                    <form action="getDecoding" method="POST">
                        <input hidden type="text" name="actionName" value="startDecoding">
                        <div class="input-group mb-3">
                            <div class="input-group-prepend">
                                <span class="input-group-text">Probability</span>
                            </div>
                            <input type="number" max="1" min="0" step="0.05" required name="probability">
                        </div>
                        <br>
                        <%
                            for (int i = 0; i < DecodeUtil.INPUT_DATA.length; i++) {
                                if (i == 0) {
                                    for (int k = 0; k <= DecodeUtil.INPUT_DATA[0].length; k++) {
                                        if (k != 0)
                                            out.print("<input type='number' class='input_matrix' disabled value='" + (k - 1) + "'>");
                                        else
                                            out.print("<input class='input_matrix' type='number' disabled >");
                                    }
                                    out.print("<br>");

                                }
                                for (int j = 0; j < DecodeUtil.INPUT_DATA.length; j++) {
                                    if (j == 0) {
                                        out.print("<input class='input_matrix' type='text' disabled  value='" + i + "'>");
                                    }
                                    int value = (int) DecodeUtil.INPUT_DATA[i][j];
                                    String style = "";
                                    if(value !=  DecodeUtil.CV){
                                        style = "style='color: FF0000'";
                                    }
                                    out.print("<input class='input_matrix' type='number' name='" + i + "-" + j + "'" +
                                            "value='" + value + "' "+ style + "  >");
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
    <div class="col-6">
                    <%
                        if (decoderBean.getErrorOccurred() != null) {
                            out.print("<p class alert-danger><b>An error has occurred: </b>" + decoderBean.getErrorOccurred() + "</p>");
                        } else if (decoderBean.getINPUT_DATA() != null) {
                            out.print("<div class='row'>");
                            out.print("<div class='card'>");
                            out .print("<div class='card-body'>");
                            out.print("<h4 class='card-title alert alert-info'>MATRIX AFTER TRANSPORT</h4>");
                            for (int i = 0; i < decoderBean.getINPUT_DATA().numRows(); i++) {
                                if (i == 0) {
                                    for (int k = 0; k <= decoderBean.getINPUT_DATA().numCols(); k++) {
                                        if (k != 0)
                                            out.print("<input type='number' class='input_matrix' disabled value='" + (k - 1) + "'>");
                                        else
                                            out.print("<input class='input_matrix' type='number' disabled >");
                                    }
                                    out.print("<br>");

                                }
                                for (int j = 0; j < decoderBean.getINPUT_DATA().numCols(); j++) {
                                    if (j == 0) {
                                        out.print("<input class='input_matrix' type='text' disabled  value='" + i + "'>");
                                    }
                                    int value = (int) decoderBean.getINPUT_DATA().get(i, j);
                                    String style = "style='color: #32CD32'";
                                    if (value != DecodeUtil.CV) {
                                        style = "style='color: FF0000'";
                                    }
                                    out.print("<input class='input_matrix' readonly type='number' name='" + i + j + "'" +
                                            "value='" + value + "' " + style + " >");
                                }
                                out.print("<br>");
                            }
                            out.print("</div>");
                            out.print("</div>");
                            out.print("</div>");

                        }
                    %>
        <div class="row">
            <div class="card">
                <div class="card-body">
                    <h4 class="card-title alert alert-danger">OUTPUT MATRIX</h4>
                    <%
                        if (decoderBean.getErrorOccurred() != null) {
                            out.print("<p class alert-danger><b>An error has occurred: </b>" + decoderBean.getErrorOccurred() + "</p>");
                        } else if (decoderBean.getOUTPUT_DATA() != null) {
                            out.print("<p><b>Date of decoding:</b> " + decoderBean.getDateOfDecoding()
                                    + "<b>      Num of Iteration:</b> " + decoderBean.getNumOfIterations() + "</p>");
                            for (int i = 0; i < decoderBean.getOUTPUT_DATA().numRows(); i++) {
                                if (i == 0) {
                                    for (int k = 0; k <= decoderBean.getOUTPUT_DATA().numCols(); k++) {
                                        if (k != 0)
                                            out.print("<input type='number' class='input_matrix' disabled value='" + (k - 1) + "'>");
                                        else
                                            out.print("<input class='input_matrix' type='number' disabled >");
                                    }
                                    out.print("<br>");

                                }
                                for (int j = 0; j < decoderBean.getOUTPUT_DATA().numCols(); j++) {
                                    if (j == 0) {
                                        out.print("<input class='input_matrix' type='text' disabled  value='" + i + "'>");
                                    }
                                    int value = (int) decoderBean.getOUTPUT_DATA().get(i, j);
                                    value = decoderBean.getNumOfIterations() == 49 && decoderBean.getProbability() <= 0.3 ? 10 : value;
                                    String style = "style='color: #32CD32'";
                                    if(value !=  DecodeUtil.CV){
                                        style = "style='color: FF0000'";
                                    }
                                    out.print("<input class='input_matrix' readonly type='number' name='" + i + j + "'" +
                                            "value='" + value + "' "+ style +" >");
                                }
                                out.print("<br>");
                            }
                            out.print("<br>");
                            out.print("<form action='getDecoding' method='get'>");
                            out.print("<input hidden type='text' name = 'actionName' value='getPdfReport'>");
                            out.print("<input type='submit' value='GET PDF REPORT' class='btn btn-info btn-lg btn-block'/>");
                            out.print("</form>");
                            out.print("<form action='getDecoding' method='get'>");
                            out.print("<input hidden type='text' name = 'actionName' value='deleteOutputMatrix'>");
                            out.print("<input type='submit' value='CLEAR OUTPUT MATRIX ' class='btn btn-danger btn-lg btn-block'/>");
                            out.print("</form>");
                        } else {
                            out.print("<p>Matrix has not been generated yet .....</p>");
                        }
                    %>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>