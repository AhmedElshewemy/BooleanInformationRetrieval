import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;


public class BooleanRetrieval {

    public static HashSet<String> distinctTerms = new HashSet<String>();//Store All Distinct Terms
    public static HashMap<Integer, ArrayList<String>> documentAndTerms = new HashMap<>();
    public static HashMap<Integer, String> fileNameHolder = new HashMap<>();
    public static double documentTotalSize = 0;
    public static HashMap<String, ArrayList<Integer>> matrix = new HashMap<String, ArrayList<Integer>>();

    public static void main(String[] args) throws IOException {
        String folderPath = (new File("").getAbsolutePath()) + "/docs";

        readFile(folderPath);
        System.out.println("Documents Collection => "+documentAndTerms.toString());
        System.out.println("Terms => "+distinctTerms.toString());
        makeMatrix(distinctTerms, documentAndTerms, true);
        Scanner in = new Scanner(System.in);
        String s;
        do {
            System.out.println("Enter Query:(computer AND/OR information)");
            s = in.nextLine();
            query(s);
            System.out.println("to end search: stop");
            System.out.println();
            System.out.println();
        } while (!s.equals("stop"));
        in.close();
    }

    public static void readFile(String folderPath) throws IOException {
        File folder = new File(folderPath);//Current Folder
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            System.out.println("No files found in the specified folder.");
            return;
        }
        documentTotalSize = listOfFiles.length;
        int fileCounter = 0;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                BufferedReader br = new BufferedReader(new FileReader(folderPath + "/" + file.getName()));
                try {
                    String line = br.readLine();
                    ArrayList<String> DocumentAllTerms = new ArrayList<>();
                    while (line != null) {
                        String[] Terms = line.split(" ");
                        for (String Term : Terms) {
                            String tempTerm = Term.trim();
                            distinctTerms.add(tempTerm);
                            DocumentAllTerms.add(tempTerm);
                        }
                        line = br.readLine();
                    }
                    documentAndTerms.put(fileCounter, DocumentAllTerms);
                } finally {
                    fileNameHolder.put(fileCounter, file.getName());
                    br.close();
                }
            }
            fileCounter++;
        }
    }

    public static void removeKeyWords(ArrayList<String> Terms) {
        ArrayList<String> Temp;
        Temp = (ArrayList<String>) Terms.clone();
        for (String foo : Temp) {
            if (foo.equals("AND") || foo.equals("OR"))
                Terms.remove(foo);
        }

    }
    private static void makeMatrix(HashSet<String> distinctTerms, HashMap<Integer, ArrayList<String>> documentAndTerms, Boolean showMatrix) {
        ArrayList<Integer> vector;
        for (String Term : distinctTerms) {
            vector = new ArrayList<>();
            for (Entry<Integer, ArrayList<String>> value : documentAndTerms.entrySet()) {
                if (value.getValue().contains(Term))
                    vector.add(1);
                else
                    vector.add(0);
            }
            matrix.put(Term, vector);
            if (showMatrix) {
				System.out.print(Term + "\t");
                for (Integer B : vector) {
                    System.out.print(B + "\t");
                }
                System.out.println();
            }
        }

    }

    private static void query(String QueryString) {
        String[] queryStringArray = QueryString.split(" ");
        ArrayList<String> terms = new ArrayList<>();
		Collections.addAll(terms, queryStringArray);
        if (terms.contains("AND")) {
            removeKeyWords(terms);
            ArrayList<ArrayList<Integer>> Temp = getQueryResult(terms);
			showResult(Temp, "AND");
        } else if (terms.contains("OR")) {
            removeKeyWords(terms);
            ArrayList<ArrayList<Integer>> Temp = getQueryResult(terms);
			showResult(Temp, "OR");
        } else {
            ArrayList<ArrayList<Integer>> Temp = getQueryResult(terms);
			showResult(Temp, "NO");
        }
    }


    private static ArrayList<ArrayList<Integer>> getQueryResult(ArrayList<String> Terms) {
        ArrayList<ArrayList<Integer>> ResultMatrix = new ArrayList<ArrayList<Integer>>();
        for (String term : Terms) {
            ArrayList<Integer> termVector = matrix.get(term);
            if (termVector != null) {
                if (!termVector.isEmpty()) {
                    ResultMatrix.add(termVector);
                } else {
                    ArrayList<Integer> emptyArray = new ArrayList<Integer>(fileNameHolder.entrySet().size());
                    ResultMatrix.add(emptyArray);
                }
            } else {
                ArrayList<Integer> emptyArray = new ArrayList<Integer>(Collections.nCopies(fileNameHolder.entrySet().size(), 0));
                ResultMatrix.add(emptyArray);
            }
        }
        return ResultMatrix;
    }

    private static void showResult(ArrayList<ArrayList<Integer>> ResultMatrix, String KeyWord) {
        int[] resultArray = new int[fileNameHolder.entrySet().size()];
        if (KeyWord.equals("AND")) {
            int Temp = 1;
            for (int i = 0; i < fileNameHolder.entrySet().size(); i++) {

                Temp = 1;
                for (ArrayList<Integer> j : ResultMatrix) {
                    Temp = Temp * j.get(i);
                }
                resultArray[i] = Temp;
            }
        } else {
            int Temp = 0;
            for (int i = 0; i < fileNameHolder.entrySet().size(); i++) {
                Temp = 0;
                for (ArrayList<Integer> j : ResultMatrix) {
                    Temp = Temp + j.get(i);
                }
                resultArray[i] = Temp;
            }
        }
        int Counter = 0;
        for (Entry<Integer, String> value : fileNameHolder.entrySet()) {
            if (resultArray[value.getKey()] > 0) {
                Counter++;
                System.out.println(value.getValue());
            }
        }
        if (Counter == 0)
            System.out.println("No Result");
        else
            System.out.println("Number Of Files : " + Counter);


    }


}




















