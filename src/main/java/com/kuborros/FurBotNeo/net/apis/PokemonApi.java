
package com.kuborros.FurBotNeo.net.apis;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class PokemonApi {

    private final List<String> urls = new ArrayList<>();
    private final String url;
    private final Logger LOG = LoggerFactory.getLogger("ImageBoardApi");

public PokemonApi(String url){
    this.url = url;
}

    public List<String> getImageSetRandom() throws IllegalArgumentException, ParserConfigurationException, SAXException, IOException, NoImgException {
        URL u = new URL(url);
        return getPokeSet(u);
    }

    public List<String> getImageSetTags(String tags) throws IllegalArgumentException, ParserConfigurationException, SAXException, IOException, NoImgException {
        URL u = new URL(url + "&search=" + tags.replaceAll(" ", "+") + "+order:random");
        return getPokeSet(u);
    }

    private List<String> getPokeSet(URL u) throws IllegalArgumentException, NoImgException, ParserConfigurationException, SAXException, IOException {
        try {

            URLConnection UC = u.openConnection();
            UC.setRequestProperty ( "User-agent", "DiscordBot/1.0");

            InputSource source = new InputSource(UC.getInputStream());

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(source);

            doc.getDocumentElement().normalize();


            NodeList nList = doc.getElementsByTagName("post");


            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    urls.add(eElement.getElementsByTagName("file_url").item(0).getTextContent());
                }
            }

            urls.removeIf(s -> s.contains(".webm"));
            if (urls.isEmpty()) {
                throw new NoImgException();
            }
            return urls;
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            LOG.error("Error occured while retreiving images: ", ex);
            throw ex;
        }
}
}
