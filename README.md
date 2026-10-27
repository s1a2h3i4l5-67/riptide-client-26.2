# Riptide

Eaglercraft 26.2 client with a module system and ClickGUI.

## Status
- Core module framework (modules, categories, settings, event dispatch) — done
- ClickGUI + HUD — in progress
- Modules (combat / movement / render + finders) — in progress
- Consent-gated chat logging — in progress

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
