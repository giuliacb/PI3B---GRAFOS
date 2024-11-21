import java.io.*;
import java.util.*;

public class FILMES {
    public static void main(String[] args) {
        String filePath = "PI_GRAFOS/IMDB_CSV.csv"; // Substitua pelo caminho do seu arquivo

        // Estruturas de grafos
        Map<String, Set<String>> grafo = new HashMap<>(); // Grafo bipartido (Filmes e Atores)
        Map<String, Set<String>> filmesGeneros = new HashMap<>(); // Filme -> Gêneros
        Map<String, Set<String>> generoAtores = new HashMap<>(); // Gênero -> Atores

        // Construção do grafo
        construirGrafo(filePath, grafo, filmesGeneros, generoAtores);

        // Menu interativo
        menuInterativo(grafo, filmesGeneros, generoAtores);
    }

    private static void construirGrafo(String filePath, Map<String, Set<String>> grafo,
            Map<String, Set<String>> filmesGeneros,
            Map<String, Set<String>> generoAtores) {
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

                    // Conectar filme aos atores e vice-versa
                    grafo.get(titulo).add(ator);
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
    }

    private static void menuInterativo(Map<String, Set<String>> grafo, Map<String, Set<String>> filmesGeneros,
            Map<String, Set<String>> generoAtores) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n### Menu de Opções ###");
            System.out.println("1. Listar filmes e seus gêneros");
            System.out.println("2. Listar atores e seus filmes");
            System.out.println("3. Buscar melhor ator por gênero e salvar em arquivo");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");

            String opcaoInput = scanner.nextLine();
            int opcao;
            try {
                opcao = Integer.parseInt(opcaoInput);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número entre 1 e 4.");
                continue;
            }

            switch (opcao) {
                case 1:
                    listarFilmesEGeneros(filmesGeneros);
                    break;

                case 2:
                    listarAtoresEFilmes(grafo, filmesGeneros);
                    break;

                case 3:
                    buscarMelhorAtorPorGenero(scanner, grafo, filmesGeneros, generoAtores);
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

    private static void listarFilmesEGeneros(Map<String, Set<String>> filmesGeneros) {
        System.out.println("\n### Filmes e Seus Gêneros ###");
        filmesGeneros.forEach((filme, generos) -> System.out.println(filme + " -> " + String.join(", ", generos)));
    }

    private static void listarAtoresEFilmes(Map<String, Set<String>> grafo, Map<String, Set<String>> filmesGeneros) {
        System.out.println("\n### Atores e Seus Filmes ###");
        grafo.forEach((no, conexoes) -> {
            if (!filmesGeneros.containsKey(no)) { // Se não é um filme, é um ator
                System.out.println(no + " -> " + String.join(", ", conexoes));
            }
        });
    }

    private static void buscarMelhorAtorPorGenero(Scanner scanner, Map<String, Set<String>> grafo,
            Map<String, Set<String>> filmesGeneros,
            Map<String, Set<String>> generoAtores) {
        System.out.println("\n### Gêneros Disponíveis ###");
        for (String genero : generoAtores.keySet()) {
            System.out.println("- " + genero);
        }
        System.out.print("\nDigite o gênero para buscar o melhor ator: ");
        String generoEscolhido = scanner.nextLine();

        if (!generoAtores.containsKey(generoEscolhido)) {
            System.out.println("Gênero não encontrado.");
            return;
        }

        Map<String, Integer> contadorAtores = new HashMap<>();
        for (String ator : generoAtores.get(generoEscolhido)) {
            contadorAtores.put(ator, bfsContarParticipacoes(grafo, ator, generoEscolhido, filmesGeneros));
        }

        List<Map.Entry<String, Integer>> listaOrdenada = new ArrayList<>(contadorAtores.entrySet());
        listaOrdenada.sort((a, b) -> b.getValue().compareTo(a.getValue()));

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
    }

    private static int bfsContarParticipacoes(Map<String, Set<String>> grafo, String ator, String genero,
                                          Map<String, Set<String>> filmesGeneros) {
    Set<String> visitados = new HashSet<>();
    Queue<String> fila = new LinkedList<>();
    fila.add(ator);

    int participacoes = 0;

    while (!fila.isEmpty()) {
        String atual = fila.poll();

        // Evita processar o mesmo nó mais de uma vez
        if (visitados.contains(atual)) {
            continue;
        }
        visitados.add(atual);

        for (String vizinho : grafo.get(atual)) {
            // Processar apenas filmes conectados ao ator
            if (filmesGeneros.containsKey(vizinho) && filmesGeneros.get(vizinho).contains(genero)) {
                participacoes++;
            }

            // Adiciona apenas atores ao BFS, para evitar processar filmes mais de uma vez
            if (!filmesGeneros.containsKey(vizinho)) {
                fila.add(vizinho);
            }
        }
    }
    return participacoes;
}
}
