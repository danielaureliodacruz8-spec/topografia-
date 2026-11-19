# TOPO IA — Escopo funcional

## Princípios
- Aplicativo Android nativo.
- Cálculos matemáticos locais/offline sempre que possível.
- A IA interpreta solicitações e documentos; o motor determinístico calcula os resultados.
- Registrar entradas, unidades, método, resultado e alertas.
- Nunca aprovar locação apenas por OCR/foto: validar coordenadas, datum, fuso, unidade, revisão do projeto e tolerância.

## Módulos
1. Topografia básica: cálculo direto/inverso, azimute, rumo, distância, área, interseções e irradiação.
2. Poligonais: fechada por ângulos internos e aberta enquadrada por deflexões assinadas; fechamento angular/linear e Bowditch. Poligonais abertas sem coordenada final conhecida e mínimos quadrados permanecem em desenvolvimento.
3. Nivelamento: simples, composto, caderneta, ré, vante, intermediárias, HI, cotas, conferência aritmética e compensação.
4. Estação total: irradiação por distância/azimute, orientação por leitura horizontal e azimute da ré, redução de distância inclinada por ângulo zenital, alturas do instrumento/prisma, locação e interseção de visadas para ponto inacessível. Resseção por distâncias com três ou mais pontos de apoio e ajustamento iterativo por mínimos quadrados.
5. Geometria rodoviária: estaqueamento, curvas horizontais/verticais, greide, rampas, superelevação e seções.
6. Terraplenagem: seções por offsets, áreas de corte/aterro, volumes por áreas médias, prismoidal e volumes acumulados.
7. Seções rodoviárias: bordas de plataforma por largura/caimento e encontro de taludes de corte/aterro com terreno por interpolação linear e relação H:V.
8. Drenagem: cotas, declividades, dimensões e conferências de projeto.
9. GNSS/RTK e geodésia: sistemas de referência, transformação e qualidade posicional.
10. Arquivos: CSV/TXT/KML/KMZ/DXF e PDF; formatos avançados exigem bibliotecas e testes.
11. Modo Estrada: leitura de nota de serviço, associação por estaca/seção, orientação de off-set e acompanhamento.
12. IA: assistente técnico com conhecimento validado e conectores configuráveis a provedores externos.

## Estado de desenvolvimento
Versão 0.1.0 em evolução. Estrutura Android nativa e interface inicial em Jetpack Compose. Já existem módulos matemáticos para cálculo inverso/direto, poligonal fechada, poligonal aberta enquadrada por deflexões, nivelamento e caderneta, curvas rodoviárias, seções/terraplenagem e geometria de plataforma/taludes.

Nesta etapa foi adicionado o núcleo de estação total: irradiação, orientação por leitura horizontal, redução de distância inclinada com ângulo zenital e alturas, locação e interseção de duas visadas. Também foi adicionado o cálculo de resseção por distâncias com ajustamento iterativo e resíduos. A tela Android inicial permite irradiação por distância horizontal e azimute.

## Próximas etapas
- Validar todos os testes e a compilação Android.
- Integrar calculadoras de poligonal, nivelamento, estação total e seções na interface.
- Implementar resseção por observações redundantes e ajustamento por mínimos quadrados.
- Desenvolver superelevação variável por estaca, drenagem, importação/exportação de arquivos e Modo Estrada.
- Conectar provedores de IA com consentimento e chaves configuradas pelo usuário.

## Critérios antes de uso em obra
- Testes unitários com casos conhecidos e validação independente.
- Conferência de convenções de azimute, coordenadas, unidades e sinais.
- Tolerâncias conforme norma e projeto aplicáveis.
- Testes de importação com arquivos reais de coletores diferentes.
- Revisão por profissional habilitado antes de liberar serviços.
