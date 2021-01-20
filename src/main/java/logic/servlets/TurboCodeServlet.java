package logic.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.stream.Collectors;

@WebServlet("/getDecoding")
public class TurboCodeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //System.out.println(readReqPart(req, "00"));
        // req.getParts().forEach(System.out::println);
        System.out.println("TEST");
       // handleRequest(req, resp);
        System.out.println(req.getParameter("157"));
        System.out.println(req.getParameterNames().toString());
    }

    private String readReqPart(HttpServletRequest req, String partName) throws IOException, ServletException {
        return new BufferedReader(new InputStreamReader(req.getPart(partName)
                .getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));
    }
    public void handleRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {

        PrintWriter out = res.getWriter();
        res.setContentType("text/plain");

        Enumeration<String> parameterNames = req.getParameterNames();

        while (parameterNames.hasMoreElements()) {

            String paramName = parameterNames.nextElement();
            System.out.print(paramName);
            out.write(paramName);
            out.write("n");

            String[] paramValues = req.getParameterValues(paramName);
            for (int i = 0; i < paramValues.length; i++) {
                String paramValue = paramValues[i];
                System.out.println(": " + paramValue + "; ");
                out.write("t" + paramValue);
                out.write("n");
            }

        }

        out.close();

    }
}

