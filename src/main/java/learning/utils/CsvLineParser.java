package learning.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvLineParser {
    private Map<String, Integer> columnNamesMap = new HashMap<String, Integer>();
    private String[] tokens;

    static Pattern pattern = Pattern.compile("([^\",]*),|\"([^\"]*)\",");

    public CsvLineParser(String[] columnNames, int[] index) {
        int count = 0;
        for (String columnName : columnNames) {
            columnNamesMap.put(columnName, index[count]);
            count++;
        }
    }

    public CsvLineParser(String[] columnNames) {
        int count = 0;
        for (String columnName : columnNames) {
            columnNamesMap.put(columnName, new Integer(count));
            count++;
        }
    }

    public void parse(String line) {
        List<String> tokensList = new ArrayList<String>();
        line = line + ",";
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String token;
            if (matcher.group(1) == null || matcher.group(1).equals("")) {
                token = matcher.group(2);
            } else {
                token = matcher.group(1);
            }
            tokensList.add(token);
        }
        tokens = tokensList.toArray(new String[0]);
    }

    public String getValue(String key) {
        Integer index = columnNamesMap.get(key);
        if (index != null) {
            return tokens[index];
        } else {
            return null;
        }
    }

    public static enum States {
        TokenStart,
        CWait,
        QWait,
        TokenEnd
    }

    ;

    public static List<String> tokenizeCSV(String line) {
        List<String> tokens = new ArrayList<String>();
        StringBuffer buf = new StringBuffer();
        States s = States.TokenStart;
        for (char c : line.toCharArray()) {
            if (s == States.TokenStart) {
                if (c == '"') {
                    s = States.QWait;
                } else if (c == ',') {
                    tokens.add(buf.toString());
                    buf = new StringBuffer();
                } else {
                    s = States.CWait;
                    buf.append(c);
                }
            } else if (s == States.CWait) {
                if (c == ',') {
                    s = States.TokenStart;
                    tokens.add(buf.toString());
                    buf = new StringBuffer();
                } else {
                    buf.append(c);
                }
            } else if (s == States.QWait) {
                if (c == '"') {
                    s = States.TokenEnd;
                } else {
                    buf.append(c);
                }
            } else {
                if (c == ',') {
                    s = States.TokenStart;
                    tokens.add(buf.toString());
                    buf = new StringBuffer();
                } else {
                    throw new RuntimeException("Found the character " + c + " after \", and it should have been ,");
                }
            }
        }
        tokens.add(buf.toString());
        return tokens;
    }


    public static String printList(String[] list) {
        StringBuffer buf = new StringBuffer();
        for (String str : list) {
            buf.append(str).append("|");
        }
        return buf.toString();
    }

    public static void main(String[] args) throws Exception {
        String line = "Sri Lanka Telecom,Sri Lanka,,,\"Mar 24, 2012 1:59AM\",1,1,3,3,26,3,3, ,glahiru@gmail.com,\"Mar 24, 2012 1:59AM\",13+,10151,987143,"
                + "http://wso2.com/casestudies/wso2-mobile-service-provider-orchestrates-its-success-with-wso2-middleware,http://connect.wso2.com/wso2/dnres/reg/2degrees-case-study.pdf,2427178,null,null";
        line = line + ",";
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            String token;
            if (matcher.group(1) == null || matcher.group(1).equals("")) {
                token = matcher.group(2);
            } else {
                token = matcher.group(1);
            }
            System.out.println(token);
        }
    }
}
