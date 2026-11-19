# TOPO IA — Escopo funcional

## Princípios
- Aplicativo Android nativo.
- Operações matemáticas locais/offline sempre que possível.
- A IA interpreta solicitações e documentos; o motor determinístico calcula resultados.
- Registrar entradas, unidades, método, resultado e alertas.
- Nunca aprovar locação apenas por OCR/foto: validar coordenadas, datum, fuso, unidade, revisão do projeto e tolerância.

## Módulos planejados
1. Topografia básica: inversa, direta, azimute, rumo, distância, área, interseções e irradiação.
2. Poligonais: fechada por ângulos internos e aberta enquadrada por deflexões assinadas; fechamento angular e linear; Bowditch. Abertas sem coordenada final conhecida e ajustamentos por mínimos quadrados permanecem em desenvolvimento.
3. Nivelamento: simples, composto, intermediário, contranivelamento, fechamento e compensação.
4. Estação total: irradiação, locação, redução de distância inclinada, alturas de instrumento/prisma e resseção.
5. Geometria rodoviária: estaqueamento, curvas horizontais/verticais, greide, superelevação e seções.
6. Terraplenagem: seções transversais por offsets e diferenças projeto-terreno, separação de áreas de corte/aterro com interpolação do cruzamento, volumes por áreas médias, prismoidal e volumes acumulados separados.\n7. Seções rodoviárias: bordas de plataforma por largura e caimento transversal; encontro de taludes de corte/aterro com terreno por interpolação linear e relação H:V configurável.
8. Drenagem: cotas, declividades, dimensões e conferências de projeto.
9. GNSS/RTK e geodésia: sistemas de referência, transformação e qualidade posicional.
10. Arquivos: CSV/TXT/KML/KMZ/DXF e PDF; formatos avançados exigem bibliotecas e testes.
11. Modo Estrada: leitura de nota de serviço, associação por estaca/seção, orientação de off-set e acompanhamento.
12. IA: assistente técnico com conhecimento validado e conectores configuráveis a provedores externos.

## Estado
Versão 0.1.0: estrutura Android inicial, cálculos básicos, poligonal fechada com compensação angular e Bowditch, nivelamento composto/caderneta, geometria de curvas rodoviárias e núcleo de seções/terraplenagem, bordas de plataforma e cálculo de off-set de encontro de taludes.
Implementado nesta etapa: curva circular simples (T, desenvolvimento, corda longa, externa, flecha, PC/PT), curva vertical parabólica simétrica (PVC/PVI/PVT, cotas, K) e conversão de distância acumulada em estaca com intervalo configurável.
Próximas etapas: estação total, poligonais abertas sem apoio final conhecido, mínimos quadrados, superelevação variável por estaca, drenagem, integração de arquivos e interface de campo.

## Critérios antes de uso em obra
- Testes unitários com casos conhecidos e validação independente.
- Conferência de convenções de azimute, coordenadas, unidades e sinais.
- Tolerâncias conforme norma e projeto aplicáveis.
- Testes de importação com arquivos reais de coletores diferentes.
- Revisão por profissional habilitado antes de liberar serviços.
