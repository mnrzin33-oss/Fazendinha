# Fazendinha

Jogo estilo Stardew Valley feito com LibGDX + Kotlin.

## Controles

| Tecla | Ação |
|-------|------|
| WASD / Setas | Mover |
| Espaço | Usar ferramenta / Avançar diálogo |
| E | Falar com NPC |
| I | Abrir inventário |

## Como buildar

### Local (precisa de JDK 17)

```bash
# Setup inicial
./setup.sh

# Build debug
./gradlew android:assembleDebug

# APK gerado em:
# android/build/outputs/apk/debug/android-debug.apk
```

### GitHub Actions (automático)

1. Faça push para o repositório GitHub
2. O workflow `build.yml` roda automaticamente
3. Baixe o APK em **Actions > Build > Artifacts**

## Estrutura do projeto

```
stardew-clone/
├── core/                    # Código do jogo (plataforma-independente)
│   └── src/main/kotlin/
│       └── com/stardew/game/
│           ├── StardewGame.kt       # Classe principal
│           ├── screens/
│           │   └── PlayScreen.kt    # Tela de jogo
│           ├── entities/
│           │   ├── Player.kt        # Jogador
│           │   └── Npc.kt           # NPCs
│           └── world/
│               └── GameWorld.kt     # Mundo/mapa
├── android/                 # Launcher Android
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/.../AndroidLauncher.kt
├── .github/workflows/
│   └── build.yml            # CI/CD para build APK
├── build.gradle.kts         # Config raiz
├── settings.gradle.kts      # Módulos
└── gradlew                  # Wrapper do Gradle
```

## Tecnologias

- **Linguagem:** Kotlin
- **Engine:** LibGDX 1.12.1
- **Build:** Gradle 8.5
- **Android:** API 21+ (Android 5.0+)
- **CI/CD:** GitHub Actions
- **Nome do App:** Fazendinha

## Próximos passos

- [ ] Adicionar sprites (substituir shapes por imagens)
- [ ] Sistema de save/load
- [ ] Mais itens e ferramentas
- [ ] Sistema de combate
- [ ] Mineração
- [ ] Peixaria
- [ ] Relacionamento com NPCs
- [ ] Músicas e sons
