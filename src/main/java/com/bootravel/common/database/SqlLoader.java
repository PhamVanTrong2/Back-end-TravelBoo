package com.bootravel.common.database;

import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SqlLoader {
    private final Map<String, String> queries;

    public SqlLoader(String filename) throws ParserConfigurationException, IOException, SAXException {
        queries = new HashMap<>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ClassPathResource(filename).getInputStream());
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("query");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element query = (Element) nodeList.item(i);
            String id = query.getAttribute("id");
            String sql = query.getTextContent().trim();
            queries.put(id.toUpperCase(), sql);
        }
    }

    public String getSql(String id) {
        return queries.get(id.toUpperCase());
    }
}