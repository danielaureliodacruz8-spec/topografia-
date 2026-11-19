# TOPO IA — Plano de testes e validação

## Testes automatizados incluídos
- Cálculo inverso: distância e azimute a partir de coordenadas E/N.
- Cálculo direto: coordenada de destino por distância e azimute.
- Nivelamento simples: cota conhecida + ré - vante.
- Conversão de distância acumulada em estaca + complemento.
- Área e perímetro pelo método das coordenadas.
- Declividade percentual.
- Redução de distância inclinada.
- Fechamento de poligonal sem erro e estrutura Bowditch.

## Convenções
- Coordenadas planas em metros: E = Este, N = Norte.
- Azimute contado do Norte, sentido horário, de 0° a 360°.
- Ângulo vertical da redução de distância contado a partir do horizonte.
- Desnível positivo indica subida do ponto final em relação ao inicial.
- Leituras de nivelamento são em metros.

## Antes de liberar para produção
1. Executar `./gradlew test` e `./gradlew assembleDebug`.
2. Revisar cada fórmula e convenção com profissional de topografia.
3. Adicionar testes de casos-limite, tolerâncias e precisão numérica.
4. Validar poligonais abertas, fechadas e enquadradas com dados de campo conhecidos.
5. Validar compensações segundo método e norma especificados no projeto.
6. Testar arquivos reais de coletores e notas de serviço.
7. Realizar testes de campo independentes antes de usar os resultados para locação/liberação.

Este plano não constitui certificação do aplicativo nem substitui a conferência profissional.
