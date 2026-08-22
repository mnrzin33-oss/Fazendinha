# Fazendinha

Jogo de fazenda feito com LibGDX + Kotlin.

## Controles

### Teclado (PC/Emulador/ChromeOS)
| Tecla | Ação |
|-------|------|
| WASD / Setas | Mover |
| Espaço | Usar ferramenta |
| E | Falar com NPC |
| I | Inventário |

### Touch (Celular/Tablet)
- **Joystick** (inferior esquerdo) - Mover
- **F** - Falar com NPC
- **U** - Usar ferramenta
- **I** - Inventário

## Como buildar

```bash
./setup.sh
./gradlew android:assembleDebug
```

APK universal em: `android/build/outputs/apk/debug/`

## Tecnologias

- Kotlin
- LibGDX 1.12.1
- Gradle 8.5
- Android API 21+
