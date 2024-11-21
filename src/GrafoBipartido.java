import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GrafoBipartido {

    public static void main(String[] args) {
        String filePath = "C:/Users/Giulia/Projeto Integrador Grafos/PI_GRAFOS/csv/IMDB_CSV.csv";

        // Mapa para representar o grafo bipartido, com atores como chaves e sets de filmes como valores
        Map<String, Set<String>> grafo = new TreeMap<>(); // treemap para ordenação alfabética

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Pula a primeira linha (cabeçalho)
            br.readLine();

            while ((line = br.readLine()) != null) {
                // Dividir a linha ignorando vírgulas entre aspas
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // Extrair e limpar título e elenco
                String titulo = values[0].trim();
                String elenco = values[12].replaceAll("[\\[\\]'\"]", "").trim();
                String[] atores = elenco.split(",\\s*"); // Divide o elenco em uma lista de atores

                // Adiciona cada ator e o filme em que participou ao grafo
                for (String ator : atores) {
                    grafo.putIfAbsent(ator, new HashSet<>());
                    grafo.get(ator).add(titulo);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Exibindo as participações de cada ator
        for (Map.Entry<String, Set<String>> entry : grafo.entrySet()) {
            String ator = entry.getKey();
            Set<String> filmes = entry.getValue();
            System.out.print("Ator: " + ator + " -> Participações: ");
            System.out.println(String.join(" / ", filmes));
        }
    }
}
