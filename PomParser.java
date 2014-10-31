
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by manshu on 10/27/14.
 */
public class PomParser {

    private Document doc;
    private String ekstazi_version;
    private String xml_file;
    private XPathExpression expr;
    private XPathFactory xFactory;
    private XPath xpath;

    public static void main(String args[]) {

        String path = "/home/manshu/Templates/EXEs/CS527SE/Homework/hw7/temp_ekstazi/continuum";

        String ek_version = "3.4.2";

        if (args.length > 0)
            path = args[0];
        if (args.length > 1)
            ek_version = args[1];

        ListDir ld = new ListDir();
        PomParser pp = new PomParser();
        ArrayList<String> poms = ld.ListDir(path);

        try {

            for (String pom_path : poms) {
                System.out.print("File : " + pom_path + ", ");
                pp.queryPom(pom_path, ek_version);
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertExcludesFile(Node configuration_node)
    {
        Element excElement = doc.createElement("excludesFile");
        excElement.appendChild(doc.createTextNode("myExcludes"));
        configuration_node.appendChild(excElement);
    }

    private void insertDependency(Node node){

        Element dInsert0 = doc.createElement("dependency");
        Element gInsert0 = doc.createElement("groupId");
        Element aInsert0 = doc.createElement("artifactId");
        Element vInsert0 = doc.createElement("version");

        dInsert0.appendChild(gInsert0);
        dInsert0.appendChild(aInsert0);
        dInsert0.appendChild(vInsert0);
        gInsert0.appendChild(doc.createTextNode("org.ekstazi"));
        aInsert0.appendChild(doc.createTextNode("ekstazi-maven-plugin"));
        vInsert0.appendChild(doc.createTextNode(ekstazi_version));

        node.insertBefore(dInsert0, node.getFirstChild());

    }

    private void insertPlugin(Node surefire_node)
    {
        Element toInsert = doc.createElement("plugin");
        Element dsInsert = doc.createElement("dependencies");
        Element dInsert = doc.createElement("dependency");
        Element gInsert0 = doc.createElement("groupId");
        Element gInsert = doc.createElement("groupId");
        Element aInsert0 = doc.createElement("artifactId");
        Element aInsert = doc.createElement("artifactId");
        Element vInsert0 = doc.createElement("version");
        Element vInsert = doc.createElement("version");
        Element exsInsert = doc.createElement("executions");
        Element exInsert = doc.createElement("execution");
        Element idInsert = doc.createElement("id");
        Element goalsInsert = doc.createElement("goals");
        Element goalInsert = doc.createElement("goal");
        Element goalInsert2 = doc.createElement("goal");

        Text txt1 = doc.createTextNode("org.ekstazi");
        Text txt2 = doc.createTextNode("org.ekstazi.core");
        Text txt3 = doc.createTextNode(ekstazi_version);
        toInsert.appendChild(dsInsert);
        dsInsert.appendChild(dInsert);
        dInsert.appendChild(gInsert0);
        dInsert.appendChild(aInsert0);
        dInsert.appendChild(vInsert0);
        gInsert0.appendChild(txt1);
        aInsert0.appendChild(txt2);
        vInsert0.appendChild(txt3);


        toInsert.appendChild(gInsert);
        toInsert.appendChild(aInsert);
        toInsert.appendChild(vInsert);
        gInsert.appendChild(doc.createTextNode("org.ekstazi"));
        aInsert.appendChild(doc.createTextNode("ekstazi-maven-plugin"));
        vInsert.appendChild(doc.createTextNode(ekstazi_version));
        toInsert.appendChild(exsInsert);
        exsInsert.appendChild(exInsert);
        exInsert.appendChild(idInsert);
        idInsert.appendChild(doc.createTextNode("selection"));
        exInsert.appendChild(goalsInsert);
        goalsInsert.appendChild(goalInsert);
        goalsInsert.appendChild(goalInsert2);
        goalInsert.appendChild(doc.createTextNode("selection"));
        goalInsert2.appendChild(doc.createTextNode("restore"));

        surefire_node.getParentNode().insertBefore(toInsert, surefire_node);
    }

    private Node getNode(String search_expression) throws XPathException {
        Node node = (Node) xpath.evaluate(search_expression, doc, XPathConstants.NODE);
        return node;
    }

    private NodeList getNodeList(String search_expression) throws XPathException {
        NodeList nodelist = (NodeList) xpath.evaluate(search_expression, doc, XPathConstants.NODESET);
        return nodelist;
    }

    private void writeXml(){
        //////////////////////////////////////////////////////////////////
        //Write out the final xml file again into the same pom.xml file//
        /////////////////////////////////////////////////////////////////
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.ENCODING, "US-ASCII");
            //transformer.setErrorListener(OutputKeys.);
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            doc.setXmlStandalone(true);
            DOMSource source = new DOMSource(doc);
            StreamResult _result = new StreamResult(xml_file);
            transformer.transform(source, _result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }


    public boolean queryPom(String xml_file, String ekstazi_version) throws ParserConfigurationException, IOException, XPathException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.ekstazi_version = "3.4.2";
        this.xml_file = xml_file;
        this.expr = null;
        this.xFactory = XPathFactory.newInstance();
        this.doc = builder.parse(xml_file);
        this.doc.getDocumentElement().normalize();
        this.xpath = xFactory.newXPath();

        NodeList nodes = getNodeList("/project/build//plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/artifactId/text()");
        NodeList surefire_plugin = nodes;
        System.out.print("Surefire : " + (nodes.getLength() != 0) + " ");

        NodeList ekstazi_plugin = getNodeList("/project/build//plugin[artifactId[contains(text(), 'ekstazi-maven-plugin')]]/artifactId/text()");
        System.out.print(" Ekstazi Plugin Present : " + (ekstazi_plugin.getLength() != 0) + ", ");

        //Adding Ekstazi Plugin
        if (nodes.getLength() != 0 && ekstazi_plugin.getLength() == 0) {
            //expr = xpath.compile("count(/project/build//plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/artifactId/parent::*/preceding-sibling::*) + 1");
            //result = expr.evaluate(doc, XPathConstants.NUMBER);
            //System.out.print("at plugin number : " + result + ", ");

            Node surefire_node = getNode("/project/build//plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]");
            insertPlugin(surefire_node);
        }

        //Adding dependencies tag and ekstazi dependency
        NodeList dependencies = getNodeList("/project/dependencies|/project/dependencyManagement/dependencies");
        System.out.print("Dependencies : " + (dependencies.getLength() != 0) + " ");

        NodeList ekstazi_dependency = getNodeList("/project/dependencies/dependency[artifactId[contains(text(), 'ekstazi-maven-plugin')]]|/project/dependencyManagement/dependencies/dependency[artifactId[contains(text(), 'ekstazi-maven-plugin')]]");
        System.out.print("Ekstazi Dependency : " + (ekstazi_dependency.getLength() != 0) + " ");

        if (dependencies.getLength() == 0){
            Node project_node = getNode("/project");
            Node project_artifact_node = getNode("/project/artifactId");
            Element dependencies_node = doc.createElement("dependencies");
            if (project_artifact_node.getNextSibling() != null){
                project_node.insertBefore(dependencies_node, project_artifact_node.getNextSibling());
                insertDependency(dependencies_node);
            }
        }

        else if (ekstazi_dependency.getLength() == 0) {
            Node dependencies_node = getNode("/project/dependencies|/project/dependencyManagement/dependencies");
            insertDependency(dependencies_node);
        }

        //Adding Excludes Configuration
        NodeList excludes_configuration = getNodeList("/project/build//plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/configuration");
        System.out.print("Excludes : " + (excludes_configuration.getLength() != 0) + " ");

        NodeList excludesFile = getNodeList("/project/build//plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/configuration/excludesFile/text()");
        System.out.print("ExcludesFile Present : " + (excludesFile.getLength() > 0 && excludesFile.item(0).getNodeValue().equalsIgnoreCase("myExcludes")) + ", ");

        if(surefire_plugin.getLength() != 0 && excludes_configuration.getLength() == 0)
        {
            Node surefire_node = getNode("/project/build//plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]");
            Node artifactId = getNode("/project/build//plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/artifactId");
            Element configuration = doc.createElement("configuration");
            surefire_node.insertBefore(configuration,artifactId.getNextSibling());
            insertExcludesFile(configuration);
        }
        else if(excludesFile.getLength() == 0 && excludes_configuration.getLength() != 0)
        {
            Node configuration_node = getNode("/project/build//plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/configuration");
            insertExcludesFile(configuration_node);
        }

        //Check ArgsLine
        NodeList argsLine = getNodeList("/project/build//plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/configuration/argLine");
        System.out.print("ArgLine : " + (argsLine.getLength() != 0) + "");

        //Write to file
        writeXml();

        return true;
    }

}