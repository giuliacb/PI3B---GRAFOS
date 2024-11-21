
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

    public class LerArquivo {

        public static void main(String[] args) {
            String filePath = "C:/Users/Giulia/Projeto Integrador Grafos/PI_GRAFOS/csv/IMDB_CSV.csv";

            List<String[]> data = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                // Pula a primeira linha (cabeçalho)
                br.readLine();

                while ((line = br.readLine()) != null) {
                    // Divide a linha usando a vírgula como separador
                    //String[] values = line.split(",");

                    // Dividir a linha pelo primeiro nível de vírgulas, ignorando vírgulas dentro de aspas
                    String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                    // Extrai título, gênero e elenco, removendo colchetes e aspas desnecessários
                    String titulo = values[0].trim();
                    String genero = values[1].replaceAll("[\\[\\]'\"]", "").trim(); // remove colchetes e aspas
                    String elenco = values[12].replaceAll("[\\[\\]'\"]", "").trim(); // remove colchetes e aspas


                    System.out.println("Título: " + titulo);
                    System.out.println("Gêneros: " + genero);
                    System.out.println("Elenco: " + elenco);
                    System.out.println("-----------------------------");


                    // Adiciona o array de valores ao array de arrays
                    //data.add(values);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Exibindo o conteúdo do dataset (opcional)
            //for (String[] row : data) {
             //   System.out.println(Arrays.toString(row));
            //}
        }
    }

