package converter_csvToJason;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        String csvFileName = "data.csv";
        String jsonFileName = "data.json";
        String xmlFileName = "data.xml";
        String jsonFileName2 = "data2.json";
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        List<Employee> employees = parseCSV(columnMapping, csvFileName);
        String jsonList = listToJson(employees);
        writeString(jsonList, jsonFileName);

        List<Employee> employees2 = parseXML(columnMapping, xmlFileName);
        String jsonList2 = listToJson(employees2);
        writeString(jsonList2, jsonFileName2);

    }

    private static List<Employee> parseXML(String[] columnMapping, String xmlFileName) {
        List<Employee> employees = new ArrayList<>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File(xmlFileName));
            Node root = document.getDocumentElement();
            NodeList childNodes = root.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;
                    String[] strings = new String[columnMapping.length];
                    for (int j = 0; j < columnMapping.length; j++) {
                        strings[j] = element.getElementsByTagName(columnMapping[j]).item(0).getTextContent();
                    }
                    Employee employee = new Employee(Long.parseLong(strings[0]), strings[1], strings[2], strings[3], Integer.parseInt(strings[4]));
                    employees.add(employee);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employees;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String csvFileName) throws IOException {
        List<Employee> employees = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(csvFileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader).withMappingStrategy(strategy).build();
            employees = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employees;
    }

    private static String listToJson(List<Employee> employees) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        Gson gson = new GsonBuilder().create();
        return gson.toJson(employees, listType);
    }

    private static void writeString(String jsonList, String jsonFileName) throws ParseException {
        try (FileWriter file = new FileWriter(jsonFileName)) {
            file.write(jsonList);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
