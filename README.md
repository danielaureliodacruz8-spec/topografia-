# TOPO IA

Aplicativo Android para apoio técnico em topografia, fiscalização e engenharia rodoviária.

## Versão 0.1.0 — Fundação

Esta primeira versão inicia o projeto Android nativo em Kotlin + Jetpack Compose e inclui uma central inicial de cálculos verificáveis:

- Distância horizontal e azimute entre coordenadas;
- Irradiação de coordenadas a partir de um ponto conhecido;
- Nivelamento geométrico por ré, altura do instrumento e vante;
- Conversão de estaca/quilometragem em estaqueamento;
- Estrutura inicial para evoluir para poligonais, compensações, curvas, terraplenagem, drenagem, GNSS/RTK, importação de notas de serviço e modo estrada com assistência por IA.

## Importante

O aplicativo ainda é um protótipo técnico. Não deve ser usado para liberar ou executar locações de obra sem validação independente dos dados, unidades, sistema de referência, projeto e resultados por profissional habilitado.

A IA será uma camada de interpretação e orientação. Os resultados numéricos devem vir do motor matemático determinístico, com memória de cálculo e validações.

## Como abrir

Abra a pasta do repositório no Android Studio (versão compatível com Android Gradle Plugin 8.7.3), sincronize o Gradle e execute o módulo `app`.

## Próximas etapas

1. Poligonais abertas, fechadas e enquadradas, com fechamento angular/linear e métodos de compensação;
2. Nivelamento completo com múltiplas estações e compensação;
3. Curvas horizontais/verticais e geometria rodoviária;
4. Importação de CSV/TXT/KML/KMZ/DXF e leitura de notas de serviço;
5. Modo Estrada: interpretação de coletora, seções, off-set, rampas e superelevação;
6. Integração GNSS/RTK, mapas, relatórios e provedores de IA.

## Stack

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX
- Motor de cálculo local, sem dependência de internet para as operações básicas
