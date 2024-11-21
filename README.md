Construção do Grafo:

O grafo é representado corretamente como um mapa grafo, onde nós são conectados por arestas:

Filmes se conectam aos atores que participaram deles.

Atores se conectam aos filmes em que apareceram.

Contagem de Participações:

O método bfsContarParticipacoes realiza uma busca em largura a partir de um ator inicial.

A lógica verifica cada conexão do grafo, contabilizando apenas filmes que pertencem ao gênero solicitado.

A BFS evita ciclos usando um conjunto visitados.

Saída Ordenada:

Atores são ordenados pelo número de participações usando Comparator de forma decrescente, o que é esperado.

Geração de Arquivo:

O arquivo gerado reflete o gênero solicitado, listando atores em ordem decrescente de participações.
