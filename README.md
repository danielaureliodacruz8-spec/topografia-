# TOPO IA

Aplicativo Android para apoio técnico em topografia, fiscalização e engenharia rodoviária.

## Estado atual

Protótipo técnico em desenvolvimento, versão 0.1.0. A estrutura Android nativa usa Kotlin, Jetpack Compose e Material 3.

### Núcleos matemáticos disponíveis no código
- Cálculo direto e inverso de coordenadas;
- Poligonal fechada por ângulos internos, compensação angular uniforme e Bowditch;
- Poligonal aberta enquadrada por deflexões assinadas e Bowditch;
- Nivelamento geométrico e caderneta com ré, intermediárias, vante, HI, cotas e compensação;
- Curvas circulares simples e curvas verticais parabólicas;
- Seções transversais, áreas de corte/aterro, volumes por áreas médias e prismoidal;
- Plataforma rodoviária e encontro de taludes por interpolação;
- Estação total: irradiação, orientação por ré, redução zenital, alturas, locação e interseção de visadas;
- Resseção planimétrica por distâncias com ajustamento iterativo por mínimos quadrados;
- Importação/exportação textual de pontos CSV, incluindo cabeçalhos comuns em português e arquivos com separador decimal brasileiro.

### Telas Android disponíveis
- Cálculo inverso;
- Nivelamento geométrico simples;
- Conversor de estacas;
- Irradiação por estação total;
- Resseção por distâncias com três pontos de apoio.

## Limitações importantes

Ainda não é uma versão 100% concluída nem liberada para uso operacional em obra. Os módulos matemáticos não estão todos integrados às telas. A importação CSV ainda precisa de uma interface de seleção de arquivo e não substitui a validação dos formatos reais de cada coletora. KML/KMZ/DXF/PDF, GNSS/RTK, mapas, relatórios, banco de projetos, backup, integração com provedores de IA e Modo Estrada ainda estão pendentes.

Não liberar locações ou medições com base apenas no aplicativo sem conferência independente por profissional habilitado, incluindo datum, fuso, unidades, revisão do projeto, orientação, alturas, tolerâncias e memória de cálculo.

## Arquitetura e princípios
- Cálculos determinísticos e locais; a IA será auxiliar para interpretar comandos e documentos, nunca para inventar resultados numéricos.
- Unidades, convenções e hipóteses devem ser explícitas.
- Operações de campo essenciais devem funcionar offline.
- Registrar entradas, método, resultados, resíduos e alertas.

## Como abrir
Abra o repositório no Android Studio, sincronize o Gradle e execute o módulo `app`. O projeto usa JDK 17 e Gradle 8.9.

## Próximas etapas
1. Validar os novos módulos no CI e corrigir falhas;
2. Integrar calculadoras matemáticas à interface;
3. Importar/exportar arquivos pelo seletor nativo Android;
4. Implementar nivelamento completo, poligonais por observações, resseção angular e ajustamento robusto;
5. Implementar curvas, superelevação variável, greide, drenagem e notas de serviço;
6. Criar banco de projetos, histórico, backup, relatórios e modo offline completo;
7. Implementar GNSS/RTK, mapas, formatos KML/KMZ/DXF e Modo Estrada;
8. Integrar provedores de IA com configuração explícita e proteção de chaves;
9. Testes de campo, validação independente e publicação de APK assinado.
