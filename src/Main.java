import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.lang.StringBuilder;
import java.util.stream.Collectors;

public class Main {
    /**
     * Iterate through each line of input.
     */

    private static String givenText = "Mary had a little lamb its fleece was white as snow; And everywhere that Mary went, the lamb was sure to go. It followed her to school one day, which was against the rule; It made the children laugh and play, to see a lamb at school. And so the teacher turned it out, but still it lingered near, And waited patiently about till Mary did appear. Why does the lamb love Mary so?\" the eager children cry;\"Why, Mary loves the lamb, you know\" the teacher did reply.\"";

    public static void main(String[] args) throws IOException {

        InputStreamReader reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        BufferedReader in = new BufferedReader(reader);
        String line;
        while ((line = in.readLine()) != null) {
            String[] currLine = line.split(",");
            int n = Integer.parseInt(currLine[0]);
            String givenWord = currLine[1];
            List<String> ngrams = generateNgrams(givenText.replaceAll("[^A-Za-z0-9 ]", ""), n);
            List<Tuple> sortedTuples = calculatePredictions(givenWord,  ngrams);
            displayTuples(sortedTuples);
        }
    }

    private static void displayTuples(List<Tuple> tuples) {
        for (int i = 0; i< tuples.size(); i++) {
            System.out.print(tuples.get(i));
            if (i < tuples.size() - 1) {
                System.out.print(";");
            }
        }
    }

    private static List<Tuple> calculatePredictions(String word, List<String> ngrams) {
        HashMap<String, Integer> wordTotalCount = new HashMap<>();
        ngrams.stream().filter(x -> {
            String[] ngram = x.split(" ", 2);
            return Arrays.asList(ngram[0]).contains(word);
        }).map(x -> {
            String[] ngram = x.split(" ", 2);
            return Arrays.asList(ngram[1]);
        }).forEach(x -> {
            if (wordTotalCount.get(x.get(0)) == null) {
                wordTotalCount.put(x.get(0), 1);
            } else {
                wordTotalCount.put(x.get(0), wordTotalCount.get(x.get(0)) +1);
            }
        });
        double totalOccurrences = wordTotalCount.values().stream().mapToInt(Integer::intValue).sum();
        HashMap<String, Double> wordProbabilities = new HashMap<>();
        wordTotalCount.forEach((k,v) -> {
            wordProbabilities.put(k, v/totalOccurrences);
        });
        List<Tuple> tuples = new ArrayList<>();

        wordProbabilities.forEach((k,v) -> tuples.add(new Tuple(k, v)));
        return tuples.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    private static List<String> generateNgrams(String str, int n) {
        List<String> ngrams = new ArrayList<>();
        String[] words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++) {
            StringBuilder sb= new StringBuilder();
            for (int j = 0; j < n; j++) {
                sb.append(words[i+j]);
                if (j != n-1) {
                    sb.append(" ");
                }
            }
            ngrams.add(sb.toString());
        }
        return ngrams;
    }

    private static class Tuple implements Comparable<Tuple> {
        String prediction;
        String likelihood;
        double probability;

        public Tuple(String word, double probability) {
            this.prediction = word;
            this.probability = probability;
            this.likelihood = formatDouble(probability);
        }

        private String formatDouble(double probability) {
            DecimalFormat df = new DecimalFormat("0.000");
            return df.format(probability);
        }

        @Override
        public String toString() {
            return prediction +","+likelihood;
        }

        @Override
        public int compareTo(Tuple t) {
            if (probability == t.probability) {
                return t.prediction.compareTo(prediction);
            } else if (probability > t.probability) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}