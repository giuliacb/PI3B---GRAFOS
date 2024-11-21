import java.io.*;
import java.util.*;

public class FILMES {
    public static void main(String[] args) {
        String filePath = "PI_GRAFOS/IMDB_CSV.csv"; // Substitua pelo caminho do seu arquivo

        // Estruturas de grafos
        Map<String, Set<String>> grafo = new HashMap<>();  // Grafo bipartido (Filmes e Atores)
        Map<String, Set<String>> filmesGeneros = new HashMap<>(); // Filme -> Gêneros
        Map<String, Set<String>> generoAtores = new HashMap<>();  // Gênero -> Atores

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Ignorar cabeçalho

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                String titulo = values[0].trim();
                String generosBrutos = values[1].replaceAll("[\\[\\]'\"]", "").trim();
                String elencoBruto = values[12].replaceAll("[\\[\\]'\"]", "").trim();

                Set<String> generos = new HashSet<>(Arrays.asList(generosBrutos.split(",\\s*")));
                Set<String> elenco = new HashSet<>(Arrays.asList(elencoBruto.split(",\\s*")));

                // Mapear filme -> gêneros
                filmesGeneros.put(titulo, generos);

                // Construção do grafo bipartido
                grafo.putIfAbsent(titulo, new HashSet<>());
                for (String ator : elenco) {
                    grafo.putIfAbsent(ator, new HashSet<>());
                    grafo.get(titulo).add(ator);  // Filme conecta-se aos atores
                    grafo.get(ator).add(titulo); // Ator conecta-se aos filmes

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

        // Menu interativo
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
                    System.out.println("\n### Filmes e Seus Gêneros ###");
                    filmesGeneros.forEach((filme, generos) -> 
                        System.out.println(filme + " -> " + String.join(", ", generos)));
                    break;

                case 2:
                    System.out.println("\n### Atores e Seus Filmes ###");
                    grafo.forEach((no, conexoes) -> {
                        if (!filmesGeneros.containsKey(no)) { // Se não é um filme, é um ator
                            System.out.println(no + " -> " + String.join(", ", conexoes));
                        }
                    });
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

                    // Busca atores e usa BFS para calcular participações
                    Map<String, Integer> contadorAtores = new HashMap<>();
                    for (String ator : generoAtores.get(generoEscolhido)) {
                        contadorAtores.put(ator, bfsContarParticipacoes(grafo, ator, generoEscolhido, filmesGeneros));
                    }

                    // Ordenar atores por participações
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

    // Busca em Largura (BFS) para contar participações de um ator em filmes de um gênero
    private static int bfsContarParticipacoes(Map<String, Set<String>> grafo, String ator, String genero, Map<String, Set<String>> filmesGeneros) {
        Set<String> visitados = new HashSet<>();
        Queue<String> fila = new LinkedList<>();
        fila.add(ator);
        visitados.add(ator);

        int participacoes = 0;

        while (!fila.isEmpty()) {
            String atual = fila.poll();

            for (String vizinho : grafo.get(atual)) {
                if (!visitados.contains(vizinho)) {
                    visitados.add(vizinho);
                    fila.add(vizinho);
                    
                    // Se o vizinho é um filme e pertence ao gênero, conta
                    if (filmesGeneros.containsKey(vizinho) && filmesGeneros.get(vizinho).contains(genero)) {
                        participacoes++;
                    }
                }
            }
        }
        return participacoes;
    }
}
