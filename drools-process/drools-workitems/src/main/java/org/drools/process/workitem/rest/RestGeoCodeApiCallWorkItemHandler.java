package org.drools.process.workitem.rest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class RestGeoCodeApiCallWorkItemHandler implements WorkItemHandler {

    private List<ResultGeoCodeApi> results;
    private int httpResponseCode;
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        try {
            //APPID from yahoo TIpNDenV34Fwcw_x32k1eX6AlQzq4wajFEFvG501Pwc6w9jKEfy2vGnkIn.r5qSQqVvyhPPaTFo-
            String URL = (String) workItem.getParameter("URL");
            workItem.getParameters().remove("URL");
            URL = URL + (String) workItem.getParameter("Service");
            workItem.getParameters().remove("Service");
            URL = URL + (String) workItem.getParameter("Method");
            workItem.getParameters().remove("Method");

            Set<String> keys = workItem.getParameters().keySet();
            for (String parameter : keys) {
                URL = URL + parameter + "=" + workItem.getParameter(parameter) + "&";
            }


            HttpURLConnection connection;
            System.out.println("*** GET Created Customer **");
            URL getUrl = new URL(URL);
            connection = (HttpURLConnection) getUrl.openConnection();
            connection.setRequestMethod("GET");
            System.out.println("Content-Type: " + connection.getContentType());

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
            setHttpResponseCode(connection.getResponseCode());

            //
            this.results = parseResults(line);
            
            System.out.println("" + line);
            connection.disconnect();

        } catch (MalformedURLException ex) {
            Logger.getLogger(RestGeoCodeApiCallWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RestGeoCodeApiCallWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
        }





    }

    private List<ResultGeoCodeApi> parseResults(String xml){
        List<ResultGeoCodeApi> results = new ArrayList<ResultGeoCodeApi>();
        try {
            
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
              // normalize text representation
            doc.getDocumentElement ().normalize ();
            NodeList listOfResults = doc.getElementsByTagName("Result");
            for(int i= 0; i< listOfResults.getLength(); i++){
                   ResultGeoCodeApi result = new ResultGeoCodeApi();
                   Node nodeResult = listOfResults.item(i);
                   if(nodeResult.getNodeType() == Node.ELEMENT_NODE){
                    Element elementResult = (Element)nodeResult;
                    result.setPrecision(elementResult.getAttribute("precision"));


                    NodeList latitudes = elementResult.getElementsByTagName("Latitude");
                    Element latitudeElement = (Element)latitudes.item(0);
                    result.setLatitude(latitudeElement.getNodeValue().trim());

                    NodeList longitudes = elementResult.getElementsByTagName("Longitude");
                    Element longitudeElement = (Element)longitudes.item(0);
                    result.setLongitude(longitudeElement.getNodeValue().trim());

                    NodeList addresses = elementResult.getElementsByTagName("Address");
                    Element addressElement = (Element)addresses.item(0);
                    result.setAddress(addressElement.getNodeValue().trim());
                    
                    NodeList cities = elementResult.getElementsByTagName("City");
                    Element cityElement = (Element)cities.item(0);
                    result.setCity(cityElement.getNodeValue().trim());

                    NodeList states = elementResult.getElementsByTagName("State");
                    Element stateElement = (Element)states.item(0);
                    result.setState(stateElement.getNodeValue().trim());

                    NodeList zips = elementResult.getElementsByTagName("Zip");
                    Element zipElement = (Element)zips.item(0);
                    result.setZip(zipElement.getNodeValue().trim());

                    NodeList countries = elementResult.getElementsByTagName("Country");
                    Element countryElement = (Element)countries.item(0);
                    result.setCountry(countryElement.getNodeValue().trim());

                    results.add(result);
                   }

            }

        } catch (SAXException ex) {
            Logger.getLogger(RestGeoCodeApiCallWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RestGeoCodeApiCallWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(RestGeoCodeApiCallWorkItemHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return results;

    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // Do nothing, this work item cannot be aborted
    }

    /**
     * @return the results
     */
    public List<ResultGeoCodeApi> getResults() {
        return results;
    }

    /**
     * @return the httpResponseCode
     */
    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    /**
     * @param httpResponseCode the httpResponseCode to set
     */
    public void setHttpResponseCode(int httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
    }
}
