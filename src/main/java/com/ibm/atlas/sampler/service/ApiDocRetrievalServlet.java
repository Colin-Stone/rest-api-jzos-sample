package com.ibm.atlas.sampler.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Objects;
import java.util.ResourceBundle;

@WebServlet(urlPatterns = {"/api-doc"}, loadOnStartup = 1)
public class ApiDocRetrievalServlet extends HttpServlet {
    private static final String LOCATION_KEY = "eureka.metadata.mfaas.api-info.swagger.location";
    private static ResourceBundle eurekaProperties = ResourceBundle.getBundle("eureka-client");

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Retrieving /api-doc");
        response.getWriter().append(getApiDocFromResourceFile());
    }

    /**
     * Load the swagger from a file specified in the configuration
     *
     * @return API Doc as a string
     */
    private static String getApiDocFromResourceFile() throws IOException {
        String apiDoc = "";
        String swaggerLocation = eurekaProperties.getString(LOCATION_KEY);
        if (!swaggerLocation.trim().isEmpty()) {
            ClassLoader classLoader = ApiDocRetrievalServlet.class.getClassLoader();
            File file = new File(Objects.requireNonNull(classLoader.getResource(swaggerLocation)).getFile());
            apiDoc = FileUtils.readFileToString(file, "UTF-8");
            // BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),  "UTF-8"));
            // String line;
            // while ((line = in.readLine()) != null) {
            //     apiDoc+=line;
            // }
            // in.close();            
        }
        return apiDoc;
    }
}
