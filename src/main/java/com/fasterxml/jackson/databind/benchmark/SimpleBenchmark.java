package com.fasterxml.jackson.databind.benchmark;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SimpleBenchmark {

    private static final String TOP_COUNTRIES_FILE = "files/countries.json";
    private static final String EMPLOYEE_FILE = "files/employee.json";
    private static final String USER_FILE = "files/user.json";
    private static final int TOP_COUNT = 20;
    private static final String FILE_PATH = "random.json";
    private static int NUMBER_OF_OBJECTS = 800000;

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        generateJsonFile();
        parseJsonFileAndPrintResults();
        String jsonString = generateRandomJsonString();
        parseJsonStringAndPrintResults(jsonString);
        employeeAnalyzer();
        countriesAnalyzer();
        employeeSalariesSum();
        employeeSalariesSumWithStr();
        long end = System.currentTimeMillis();
        System.out.println("time: " + (end - start));
    }

    private static void employeeAnalyzer() {
        //parse 70mb json file and use counter and sum
        for (int i = 0; i < 5; i++) {
            String fileName = EMPLOYEE_FILE; // Change to your JSON file name
            int count = countSalariesOver100000(fileName);
            System.out.println("----------------------------------------");
            System.out.println("Number of salaries over $100,000: " + count);
        }
    }

    private static int countSalariesOver100000(String fileName) {
        int count = 0;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Path filePath = Paths.get(fileName);
            JsonNode root = objectMapper.readTree(filePath.toFile());

            for (JsonNode jsonNode : root) {
                int salary = jsonNode.get("salary").asInt();
                if (salary > 100000) {
                    count++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }

        return count;
    }

    private static void countriesAnalyzer() {
        String fileName = TOP_COUNTRIES_FILE; // Change to your JSON file name
        int topCount = TOP_COUNT;
        for (int i = 0; i < 5; i++) {
            List<Country> topCountries = getTopPopulationCountries(fileName, topCount);

            System.out.println("Top " + topCount + " Countries by Population:");
            for (Country country : topCountries) {
                System.out.println(country.getCountryName() + ": " + country.getPopulation());
            }
        }
    }

    public static void employeeSalariesSum() {
        String fileName = EMPLOYEE_FILE; // Change to your JSON file name
        for (int i = 0; i < 5; i++) {
            long totalSalary = calculateTotalSalary(fileName);
            System.out.println("Total sum of all salaries: $" + totalSalary);
        }
    }

    public static void employeeSalariesSumWithStr() {
        String fileName = EMPLOYEE_FILE; // Change to your JSON file name
        for (int i = 0; i < 10; i++) {
            long totalSalary = calculateTotalSalaryWithStr(fileName);
            System.out.println("Str Total sum of all salaries: $" + totalSalary);
        }
    }

    private static long calculateTotalSalary(String fileName) {
        long totalSalary = 0;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Path filePath = Paths.get(fileName);
            JsonNode root = objectMapper.readTree(filePath.toFile());

            for (JsonNode jsonNode : root) {
                int salary = jsonNode.get("salary").asInt();
                totalSalary += salary;
            }
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }

        return totalSalary;
    }

    private static long calculateTotalSalaryWithStr(String fileName) {
        long totalSalary = 0;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Path filePath = Paths.get(fileName);
            StringBuilder jsonString = new StringBuilder(new String(Files.readAllBytes(filePath)));

            JsonNode root = objectMapper.readTree(jsonString.toString());

            for (JsonNode jsonNode : root) {
                int salary = jsonNode.get("salary").asInt();
                totalSalary += salary;
            }
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }

        return totalSalary;
    }

    private static List<Country> getTopPopulationCountries(String fileName, int topCount) {
        List<Country> countryList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Path filePath = Paths.get(fileName);
            JsonNode root = objectMapper.readTree(filePath.toFile());

            for (JsonNode jsonNode : root) {
                String countryName = jsonNode.get("countryName").asText();
                int population = jsonNode.get("population").asInt();
                countryList.add(new Country(countryName, population));
            }
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }

        Collections.sort(countryList, new Comparator<Country>() {
            @Override
            public int compare(Country c1, Country c2) {
                return Integer.compare(c2.getPopulation(), c1.getPopulation());
            }
        });

        return countryList.subList(0, Math.min(topCount, countryList.size()));
    }


    public static void generateJsonFile() {

        List<Map<String, Object>> jsonArray = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_OBJECTS; i++) {
            Map<String, Object> jsonData = new HashMap<>();
            jsonData.put("name", "Person " + (i + 1));
            jsonData.put("age", 40); // Random age between 18 and 67
            jsonData.put("isStudent", false);
            jsonData.put("city", getRandomCity());
            jsonData.put("country", getRandomCountry());
            jsonData.put("email", "person" + (i + 1) + "@example.com");
            jsonData.put("phone", "1234567890");
            jsonData.put("height", 180); // Random height between 150 and 250 cm
            jsonData.put("weight", 80); // Random weight between 50 and 120 kg
            jsonData.put("isEmployed", true);
            jsonData.put("salary", 30000); // Random salary between 30000 and 49999

            jsonArray.add(jsonData);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            File outputFile = new File(FILE_PATH);
            objectMapper.writeValue(outputFile, jsonArray);
            System.out.println("Random JSON file with JsonArray generated successfully.");
        } catch (IOException e) {
            System.err.println("Error while generating the JSON file: " + e.getMessage());
        }
    }

    public static String generateRandomJsonString() {
        Random random = new Random();

        List<Person> personList = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_OBJECTS; i++) {
            Person person = new Person();
            person.setName("Person " + (i + 1));
            person.setAge(40);
            person.setStudent(false);
            person.setCity(getRandomCity());
            person.setCountry(getRandomCountry());
            person.setEmail("abc_new@example.com");
            person.setPhone("0120340560");
            person.setHeight(150);
            person.setWeight(50);
            person.setEmployed(true);
            person.setSalary(30000);

            personList.add(person);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            return objectMapper.writeValueAsString(personList);
        } catch (IOException e) {
            System.err.println("Error while generating the JSON string: " + e.getMessage());
            return null;
        }
    }

    public static void parseJsonFileAndPrintResults() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            File inputFile = new File(FILE_PATH);
            List<Map<String, Object>> jsonArray = objectMapper.readValue(inputFile, List.class);

            System.out.println("Parsed JSON Data:");
            int count = 0;
            for (Map<String, Object> jsonData : jsonArray) {
                Person person = objectMapper.convertValue(jsonData, Person.class);
                if (person.getCity().equalsIgnoreCase("New York") && person.getCountry().equalsIgnoreCase("USA")) {
                    count++;
                }
            }
            System.out.println("Number of people living in New York, USA: " + count);
        } catch (IOException e) {
            System.err.println("Error while parsing the JSON file: " + e.getMessage());
        }
    }

    public static void parseJsonStringAndPrintResults(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<Person> personList = objectMapper.readValue(jsonString, objectMapper.getTypeFactory().constructCollectionType(List.class, Person.class));

            System.out.println("Parsed JSON Data:");
            int count = 0;
            int unknownPropertiesCount = 0;
            for (Person person : personList) {
                if (person.getCity().equalsIgnoreCase("London") && person.getCountry().equalsIgnoreCase("UK")) {
                    count++;
                }
                if (!person.getAdditionalProperties().isEmpty()) {
                    System.out.println("Unknown properties for " + person.getName() + ": " + person.getAdditionalProperties());
                    unknownPropertiesCount++;
                }
            }
            System.out.println("Number of people living in London, UK: " + count);
            System.out.println("Number of objects with unknown properties: " + unknownPropertiesCount);

//            String jsonWithFullView = objectMapper.writerWithView(Person.FullView.class).writeValueAsString(personList);
            System.out.println("\nSerialized JSON Data with FullView:");
//            System.out.println(jsonWithFullView.substring(0, 1000) + "...");
        } catch (IOException e) {
            System.err.println("Error while parsing the JSON string: " + e.getMessage());
        }
    }

    // Helper methods for generating random data

    private static String getRandomCity() {
        String[] cities = {"New York", "London", "Tokyo", "Paris", "Sydney", "Berlin", "Beijing", "Rome", "Moscow", "Singapore"};
        Random random = new Random();
        return cities[random.nextInt(cities.length)];
    }

    private static String getRandomCountry() {
        String[] countries = {"USA", "UK", "Japan", "France", "Australia", "Germany", "China", "Italy", "Russia", "Singapore"};
        Random random = new Random();
        return countries[random.nextInt(countries.length)];
    }

}
