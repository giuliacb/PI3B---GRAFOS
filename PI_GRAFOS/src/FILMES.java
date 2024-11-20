import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class FILMES {
    public static void main(String[] args) {
        String filePath = "PI_GRAFOS/IMDB_CSV.csv"; // Substitua pelo caminho do seu arquivo

        // Estruturas para armazenar dados
        Map<String, Set<String>> filmesGeneros = new HashMap<>(); // Filme -> Gêneros
        Map<String, Set<String>> grafo = new TreeMap<>();         // Ator -> Filmes
        Map<String, Set<String>> generoAtores = new HashMap<>();  // Gênero -> Atores

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Ignorar a linha de cabeçalho

            while ((line = br.readLine()) != null) {
                // Dividir a linha ignorando vírgulas entre aspas
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // Extrair campos relevantes e tratar os dados
                String titulo = values[0].trim();
                String generosBrutos = values[1].replaceAll("[\\[\\]'\"]", "").trim();
                String elencoBruto = values[12].replaceAll("[\\[\\]'\"]", "").trim();

                // Processar gêneros e elenco
                Set<String> generos = new HashSet<>(Arrays.asList(generosBrutos.split(",\\s*")));
                Set<String> elenco = new HashSet<>(Arrays.asList(elencoBruto.split(",\\s*")));

                // Mapear filme -> gêneros
                filmesGeneros.put(titulo, generos);

                // Mapear atores -> filmes e gêneros -> atores
                for (String ator : elenco) {
                    // Grafo: Ator -> Filmes
                    grafo.putIfAbsent(ator, new HashSet<>());
                    grafo.get(ator).add(titulo);

                    // Gêneros -> Atores
                    for (String genero : generos) {
                        generoAtores.putIfAbsent(genero, new HashSet<>());
                        generoAtores.get(genero).add(ator);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao processar o arquivo: " + e.getMessage());
        }

        // Menu interativo para verificar relações
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n### Menu de Opções ###");
            System.out.println("1. Listar filmes e seus gêneros");
            System.out.println("2. Listar atores e seus filmes");
            System.out.println("3. Buscar melhor ator por gênero e salvar em arquivo");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir a quebra de linha

            switch (opcao) {
                case 1:
                    System.out.println("\n### Filmes e seus Gêneros ###");
                    filmesGeneros.forEach((filme, generos) -> 
                        System.out.println(filme + " -> " + String.join(", ", generos)));
                    break;

                case 2:
                    System.out.println("\n### Atores e seus Filmes ###");
                    grafo.forEach((ator, filmes) -> 
                        System.out.println(ator + " -> " + String.join(", ", filmes)));
                    break;

                case 3:
                    System.out.println("\n### Gêneros Disponíveis ###");
                    for (String genero : generoAtores.keySet()) {
                        System.out.println("- " + genero);
                    }
                    System.out.print("\nDigite o gênero para buscar o melhor ator: ");
                    String generoEscolhido = scanner.nextLine();
                    
                    if (!generoAtores.containsKey(generoEscolhido)) {
                        System.out.println("Gênero não encontrado.");
                        break;
                    }

                    // Contar participações dos atores no gênero escolhido
                    Map<String, Integer> contadorAtores = new HashMap<>();
                    for (String ator : generoAtores.get(generoEscolhido)) {
                        int participacoes = 0;
                        for (String filme : grafo.get(ator)) {
                            if (filmesGeneros.get(filme).contains(generoEscolhido)) {
                                participacoes++;
                            }
                        }
                        contadorAtores.put(ator, participacoes);
                    }

                    // Ordenar atores por número de participações
                    List<Map.Entry<String, Integer>> listaOrdenada = new ArrayList<>(contadorAtores.entrySet());
                    listaOrdenada.sort((a, b) -> b.getValue().compareTo(a.getValue()));

                    // Salvar resultados em um arquivo
                    String arquivoSaida = "resultado_" + generoEscolhido + ".txt";
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(arquivoSaida), "UTF-8"))) {
                        writer.write("Atores que mais participaram no gênero \"" + generoEscolhido + "\":\n");
                        for (Map.Entry<String, Integer> entry : listaOrdenada) {
                            writer.write("Ator: " + entry.getKey() + " -> Participações: " + entry.getValue() + "\n");
                        }
                        System.out.println("Arquivo gerado com sucesso: " + arquivoSaida);
                    } catch (IOException e) {
                        System.err.println("Erro ao salvar o arquivo: " + e.getMessage());
                    }
                    break;

                case 4:
                    System.out.println("Encerrando o programa. Até mais!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }
}
