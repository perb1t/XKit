package com.shijiwei.xkit.utility.file;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by shijiwei on 2017/11/23.
 *
 * @VERSION 1.0
 */

public class Xml {


    public static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
    public static final String ELEMENT_ROOT = "answers";
    public static final String ELEMENT_ANSWER = "answer";
    public static final String NODE_QUESTION = "question";
    public static final String NODE_REPLY = "reply";

    /**
     * @param qls
     * @return
     */
    public static String generateXml(List<Qustion> qls) {
        String xmlWriter = null;
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element eleRoot = doc.createElement(ELEMENT_ROOT);
            doc.appendChild(eleRoot);

            for (int i = 0; i < qls.size(); i++) {
                Element eleAnswer = doc.createElement(ELEMENT_ANSWER);
                for (int j = 0; j < qls.get(i).getQustions().size(); j++) {
                    Element eleQustion = doc.createElement(NODE_QUESTION);
                    Node qustion = doc.createTextNode(qls.get(i).getQustions().get(i));
                    eleQustion.appendChild(qustion);
                    eleAnswer.appendChild(eleQustion);
                }
                Element eleReply = doc.createElement(NODE_REPLY);
                Node replay = doc.createTextNode(qls.get(i).getAnswer());
                eleReply.appendChild(replay);
                eleAnswer.appendChild(eleReply);
                eleRoot.appendChild(eleAnswer);
            }

            Properties properties = new Properties();
            properties.setProperty(OutputKeys.INDENT, "yes");
            properties.setProperty(OutputKeys.MEDIA_TYPE, "xml");
            properties.setProperty(OutputKeys.VERSION, "1.0");
            properties.setProperty(OutputKeys.ENCODING, "utf-8");
            properties.setProperty(OutputKeys.METHOD, "xml");
            properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperties(properties);

            DOMSource domSource = new DOMSource(doc.getDocumentElement());
            OutputStream output = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(output);
            transformer.transform(domSource, result);

            xmlWriter = HEADER + output.toString();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return xmlWriter;
    }

    /**
     * @param is
     * @return
     */
    public static List<Qustion> parseXml(InputStream is) {

        List<Qustion> dataSet = new ArrayList<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            doc.getDocumentElement().normalize();
            NodeList nlsRoot = doc.getElementsByTagName(ELEMENT_ROOT);
            Element eleRoot = (Element) nlsRoot.item(0);
            NodeList nlAnswer = eleRoot.getElementsByTagName(ELEMENT_ANSWER);
            for (int i = 0; i < nlAnswer.getLength(); i++) {
                Qustion q = new Qustion();
                List<String> qls = new ArrayList<>();
                Element elementAnswer = (Element) nlAnswer.item(i);
                NodeList nlsQustion = elementAnswer.getElementsByTagName(NODE_QUESTION);
                for (int j = 0; j < nlsQustion.getLength(); j++) {
                    String qustion = nlsQustion.item(j).getFirstChild().getNodeValue();
                    qls.add(qustion);
                }
                q.setQustions(qls);
                NodeList nlsReply = elementAnswer.getElementsByTagName(NODE_REPLY);
                String answer = nlsReply.item(0).getFirstChild().getNodeValue();
                q.setAnswer(answer);
                dataSet.add(q);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataSet;
    }

    public static class Qustion {

        private List<String> qustions;
        private String answer;

        public Qustion() {
        }

        public Qustion(List<String> qustions, String answer) {
            this.qustions = qustions;
            this.answer = answer;
        }

        public List<String> getQustions() {
            return qustions;
        }

        public void setQustions(List<String> qustions) {
            this.qustions = qustions;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        @Override
        public String toString() {

            String q = "";
            for (int i = 0; i < qustions.size(); i++) {
                q += "," + qustions.get(i);
            }
            return "qusion : " + q + "    answer :  " + answer;
        }
    }


}
