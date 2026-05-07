# Greenwood High School Chatbot - JavaFX UI
=============================================

## Project Structure
```
school-chatbot-fx/
├── src/
│   ├── Main.java         ← JavaFX UI + entry point
│   ├── Chatbot.java      ← OpenRouter API
│   ├── SchoolData.java   ← loads school_info.json
│   └── Config.java       ← reads config.properties
├── data/
│   └── school_info.json  ← school data
├── config.properties     ← your API key
└── README.md
```

## Setup Steps

### Step 1 — Install JavaFX
1. Go to: https://gluonhq.com/products/javafx/
2. Download JavaFX SDK for your OS (Windows/Mac/Linux)
3. Extract it somewhere easy, e.g.: C:\javafx-sdk\

### Step 2 — Set your API key
Open config.properties:
```
API_KEY=your-openrouter-key-here
MODEL=nvidia/nemotron-3-super-120b-a12b:free
MAX_TOKENS=1000
SCHOOL_DATA_PATH=data/school_info.json
```

### Step 3 — Compile (in terminal, inside project folder)
```bash
javac --module-path "C:\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml -cp src src\Main.java src\Chatbot.java src\Config.java src\SchoolData.java -d out
```

### Step 4 — Run
```bash
java --module-path "C:\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml -cp out Main


### Tell VS Code where JavaFX is
Create a file .vscode\settings.json inside your project folder with this:
```bash
json{
    "java.project.referencedLibraries": [
        "C:\\javafx-sdk\\lib\\*.jar"
    ]
}
### Then create .vscode\launch.json:
json{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Run School Chatbot",
            "request": "launch",
            "mainClass": "Main",
            "vmArgs": "--module-path \"C:\\javafx-sdk\\lib\" --add-modules javafx.controls"
        }
    ]
}
```


## Features
- Modern chat bubble UI
- Quick chip buttons for common questions
- Typing indicator while waiting
- Reset button to clear conversation
- Runs as a desktop app
