# 📍 Velocity Tracker

Aplicativo Android que rastreia a localização do usuário em tempo real, exibe a velocidade atual (em km/h) e mostra sua posição no mapa com estilo personalizado.

## 🚀 Funcionalidades

- 🌍 Mapa com visual personalizado usando Google Maps
- 📡 Rastreamento em tempo real da localização
- 🏎️ Exibição da velocidade atual em km/h
- 🧭 Atualização rápida da posição e velocidade
- 📌 Marcação da localização atual no mapa
- 🔁 Botões para iniciar e parar o rastreamento
- ➡️ Navegação para uma segunda tela (SecondActivity)

## 🧑‍💻 Tecnologias utilizadas

- Java (Android SDK)
- Google Play Services - Location API
- Google Maps SDK
- FusedLocationProviderClient
- MapView
- Permissões de localização em tempo real

## 📱 Layout

- `activity_main.xml`: Tela principal com MapView, informações de localização e velocidade, e botões de controle.
- `map_style.json`: Arquivo de estilo para personalizar o visual do Google Maps.

## 🔐 Permissões necessárias

Para funcionamento adequado, o app requer:

<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />


✅ Como usar
Instale o app em um dispositivo Android com serviços do Google.

Conceda permissão de localização.

Toque em "Iniciar" para começar a ver sua localização e velocidade.

Toque em "Parar" para encerrar o rastreamento.

Acesse a próxima tela com o botão "Próximo".

🛠️ Melhorias futuras
Armazenamento do histórico de velocidade

Exibição de gráfico de velocidade

Modo escuro automático no mapa

Exportação de rota percorrida

⚠️ Observações
A velocidade exibida depende da precisão do GPS do dispositivo.

A frequência de atualização foi otimizada para rastrear movimentos pequenos em tempo real, o que pode impactar o consumo de bateria.

Desenvolvido por Pedro Henrique 🚗📱
