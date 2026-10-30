# Riptide

Eaglercraft 26.2 client with a module system and ClickGUI.

## Status
- Core module framework (modules, categories, settings, event dispatch) — done
- Game hooks (tick, keys, HUD, ESP glow, lightmap, knockback) — done
- ClickGUI (Right Shift) + HUD — done
- Modules — first set done (see below)

## Controls
- **Right Shift** — open/close the ClickGUI
- In the ClickGUI: **left click** a module to toggle, **right click** for settings,
  **middle click** then press a key to bind it (Esc/Delete clears the bind).
  Drag panel headers to move them, scroll to move all panels.

## Modules
| Category | Module | What it does |
| --- | --- | --- |
| Combat | KillAura | Hits nearby players/mobs/animals; cooldown or CPS timing, silent rotations |
| Combat | AutoTotem | Keeps a totem in your offhand (optional health threshold) |
| Combat | Velocity | Scales knockback from hits and explosions |
| Movement | Sprint | Always sprint (Legit / Omni) |
| Movement | Flight | Motion or Vanilla fly, anti-kick |
| Movement | Speed | Strafe or Hop |
| Movement | NoFall | No fall damage |
| Movement | Step | Step up 1–2.5 blocks |
| Player | FastPlace | Removes right-click delay |
| Render | Fullbright | Gamma boost or night vision |
| Render | ESP | Colored glow outlines through walls |
| Render | HUD | Watermark, module list, coords, FPS |

## Building (Windows)
Requires **JDK 25** and **Node.js 24** on PATH.

Compile the Java only (fast sanity check):
```
gradlew.bat :game:compileJava --console=plain --no-daemon
```

Full single-file HTML build (~30–40 min, needs ~16 GB RAM free):
```
gradlew.bat build
node wasm-toolchain/build-single-html.js --output riptide.html
```
Test locally:
```
py -3 -m http.server 8000 --directory target_teavm_wasm_gc/build/web
```
then open http://localhost:8000/

## Riptide code
Lives in `game/src/main/java/net/riptide/`. The rest is the Eaglercraft 26.2 workspace.
