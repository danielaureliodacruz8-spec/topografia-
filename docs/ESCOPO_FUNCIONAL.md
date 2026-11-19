# TOPO IA — Escopo funcional

## Princípios
- Aplicativo Android nativo.
- Operações matemáticas locais/offline sempre que possível.
- A IA interpreta solicitações e documentos; o motor determinístico calcula resultados.
- Registrar entradas, unidades, método, resultado e alertas.
- Nunca aprovar locação apenas por OCR/foto: validar coordenadas, datum, fuso, unidade, revisão do projeto e tolerância.

## Módulos planejados
1. Topografia básica: inversa, direta, azimute, rumo, distância, área, interseções e irradiação.
2. Poligonais: aberta, fechada e enquadrada; fechamento angular e linear; Bowditch e métodos alternativos.
3. Nivelamento: simples, composto, intermediário, contranivelamento, fechamento e compensação.
4. Estação total: irradiação, locação, redução de distância inclinada, alturas de instrumento/prisma e resseção.
5. Geometria rodoviária: estaqueamento, curvas horizontais/verticais, greide, superelevação e seções.
6. Terraplenagem: corte/aterro, taludes, áreas médias, prismoidal e superfícies.
7. Drenagem: cotas, declividades, dimensões e conferências de projeto.
8. GNSS/RTK e geodésia: sistemas de referência, transformação e qualidade posicional.
9. Arquivos: CSV/TXT/KML/KMZ/DXF e PDF; formatos avançados exigem bibliotecas e testes.
10. Modo Estrada: leitura de nota de serviço, associação por estaca/seção, orientação de off-set e acompanhamento.
11. IA: assistente técnico com conhecimento validado e conectores configuráveis a provedores externos.

## Estado
Versão 0.1.0: estrutura Android inicial, cálculo inverso, irradiação direta, nivelamento simples e conversão de estacas.
Etapa seguinte: núcleo de poligonal com Bowditch e nivelamento composto com compensação proporcional à distância.

## Critérios antes de uso em obra
- Testes unitários com casos conhecidos e validação independente.
- Conferência de convenções de azimute, coordenadas, unidades e sinais.
- Tolerâncias conforme norma e projeto aplicáveis.
- Testes de importação com arquivos reais de coletores diferentes.
- Revisão por profissional habilitado antes de liberar serviços.
