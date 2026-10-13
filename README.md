# Profile Management System for Resume/CV Generation
*Desktop & Web Application Powered by Pure Java & Apache PDFBox*

---

## 🌐 Web Application Access URL
The web server is running locally on port **8080**.

### 👉 Access Link: **[http://localhost:8080](http://localhost:8080)**

Open this URL in any browser (Google Chrome, Microsoft Edge, Firefox, Brave).
- **Dashboard**: Quick actions, stats, and template showcase.
- **Resume Builder**: Form with field validation and `✨ Fill Sample Data` button.
- **Live Resume Preview**: Real-time switching between all 6 templates, zoom controls, and **Download PDF (via Apache PDFBox)** button.
- **Manage Profiles**: Real-time search, table view, editing, and deletion.

To start or restart the web server at any time:
```cmd
run_web.bat
```
or in PowerShell:
```powershell
.\run_web.ps1
```

---

## 📌 Project Overview
The **Profile Management System (PMS)** is an academic software project engineered in **Java** using **Java SE**, **Java Object Serialization** for local persistence, and **Apache PDFBox** for high-resolution vector PDF export. It supports both a **modern Web UI** (accessible via browser at `http://localhost:8080`) and a **Java Swing Desktop GUI**.

Designed for a **2nd-Year B.Tech Computer Science / Information Technology** academic demonstration, this project showcases core and advanced Object-Oriented Programming (OOP) concepts, rigorous input validation, dynamic 2D canvas graphics rendering, and defensive exception handling.

---

## 🚀 Key Features

### 1. Modern Interactive Home Dashboard (`HomeFrame`)
- **Navy Blue & Slate Design System**: Clean, corporate styling with high contrast and intuitive navigation.
- **Quick Action Cards**: One-click access to *Create Resume*, *View Profiles*, *Manage Profiles*, and *Instant Demo*.
- **Live Statistics**: Displays the count of saved profiles and system readiness in real time.
- **Template Showcase**: Highlights all 6 supported resume formats directly on the main screen.

### 2. Comprehensive Candidate Profile Form (`ProfileFormFrame`)
- **Structured Sections**:
  - Personal Details (Full Name, Email, Phone, Location/Address)
  - Career Objective (Multi-line text area)
  - Educational Qualifications (College/University, Degree & Branch, Graduation Year, CGPA/Percentage)
  - Technical Skills (Languages, Frameworks, Tools, Core Subjects)
  - Software Projects (Detailed descriptions, tech stack)
  - Work Experience / Internships
  - Certifications & Online Courses
  - Extracurricular Achievements & Awards
  - Languages Known
- **Field Validation**:
  - Required field enforcement
  - Email format validation using Regular Expressions (`Pattern` & `Matcher`)
  - Phone number length and format checks
  - 4-digit graduation year bounds checking (1980–2040)
- **Convenience Actions**:
  - `✨ Fill Sample Data`: Pre-populates a rich 2nd-year engineering student profile with one click (great for viva demos!).
  - `🔄 Clear Form`: Resets all fields after confirmation.
  - `👁 Generate & Preview`: Validates, saves, and immediately launches the visual resume preview.

### 3. Six Visually Distinct Resume Templates (`ResumeTemplate` Hierarchy)
Demonstrates **Inheritance and Polymorphism** through 6 concrete subclasses:
1. **Professional**: Classic corporate layout with a navy blue header banner, structured section dividers, and formal serif/sans typography.
2. **Modern**: Sleek **two-column sidebar layout** featuring a dark slate sidebar (initials avatar, contact info, skills, education) and a white main body (objective, experience, projects).
3. **Simple**: Minimalist, single-column **ATS-optimized** format with centered header, clean dividers, and high readability.
4. **Student / Fresher**: Emphasizes **Education and Academic Projects** right after the objective, featuring an emerald-teal highlighted academic card and CGPA badge.
5. **Technical**: Engineer-focused theme with terminal prompt styling (`<b.tech_cse />`), monospace code tags, and prominent technical skill chips.
6. **Creative**: Vibrant Indigo/Violet gradient header with rounded pill section badges and modern card aesthetics.

### 4. Interactive Resume Canvas & Preview (`ResumePreviewFrame` & `ResumeCanvasPanel`)
- **Live Template Switcher**: Switch between any of the 6 templates on the fly via a dropdown menu.
- **Custom Java 2D Graphics Canvas**: Renders standard A4 paper dimensions with text anti-aliasing and a realistic desk drop-shadow.
- **Interactive Zoom Controls**: Zoom In (+), Zoom Out (-), and Fit to Width options.
- **Integrated PDF Export**: One-click export to PDF with desktop auto-open support.

### 5. Profile Management Dashboard (`ProfileManagerFrame`)
- **Interactive Table View**: Displays all saved candidates in a formatted `JTable` (Columns: #, Name, Email, Degree/Branch, College, Year, CGPA, Last Modified).
- **Real-Time Search**: Substring filtering across names, degrees, colleges, and skills as the user types (`DocumentListener`).
- **Full CRUD Operations**:
  - **Create**: Add new candidate profiles.
  - **Read**: View and browse profiles.
  - **Update**: Edit existing profiles with fields pre-filled.
  - **Delete**: Remove profiles with safety confirmation dialogs.
- **Defensive File Handling**: Automatically backs up corrupted storage files (`*.bak`) and recovers cleanly without crashing.

### 6. Apache PDFBox Vector PDF Export (`PDFExporter`)
- Vector-rendered A4 PDF documents matching the exact colors, headers, and rules of the chosen template.
- Built-in multi-page pagination with continuation headers.
- File selection dialog (`JFileChooser`) with `.pdf` extension validation.
- Prompt to immediately launch the generated PDF in the system's default viewer (`java.awt.Desktop`).

---

## 🏗️ Architecture & OOP Concepts Demonstrated

| Concept | Implementation Details |
| :--- | :--- |
| **Classes & Objects** | `Profile` encapsulates candidate state; `ResumeTemplate` encapsulates styling algorithms. |
| **Encapsulation** | Private attributes with standard getters/setters, data sanitization, and `serialVersionUID`. |
| **Inheritance** | Base abstract class `ResumeTemplate` extended by `ProfessionalTemplate`, `ModernTemplate`, `SimpleTemplate`, `StudentFresherTemplate`, `TechnicalTemplate`, and `CreativeTemplate`. |
| **Polymorphism** | Dynamic dispatch in `template.render(g2, profile, width, height)` allows uniform preview rendering across all 6 themes without `if/else` ladders. |
| **Collections** | `ArrayList<Profile>` used in `ProfileManager` for in-memory profile manipulation and searching. |
| **File I/O & Serialization** | `ObjectOutputStream` and `ObjectInputStream` persist profiles to `data/profiles.dat`. |
| **Exception Handling** | Custom checked exceptions (`ValidationException`, `DataStorageException`), `try-catch-finally`, and defensive try-with-resources. |
| **Swing Event Handling** | `ActionListener`, `DocumentListener`, `MouseAdapter`, and execution on the Event Dispatch Thread via `SwingUtilities.invokeLater`. |

---

## 📂 Project Directory Structure

```
ProfileManagementSystem/
├── .vscode/
│   ├── launch.json               # VS Code Run/Debug configuration
│   └── settings.json             # VS Code Java classpath configuration
├── bin/                          # Compiled Java bytecode (.class files)
├── data/
│   ├── profiles.dat              # Serialized local database of profiles
│   └── test_exports/             # Verification sample exported PDFs
├── lib/
│   └── pdfbox-app-2.0.31.jar     # Apache PDFBox self-contained standalone library
├── src/
│   └── com/
│       └── pms/
│           ├── Main.java         # Application Entry Point
│           ├── export/
│           │   └── PDFExporter.java         # Apache PDFBox PDF rendering engine
│           ├── model/
│           │   └── Profile.java             # Candidate Data Model (Serializable)
│           ├── service/
│           │   ├── DataStorageException.java # Custom storage exception
│           │   ├── ProfileManager.java       # CRUD & Serialization service
│           │   └── ValidationException.java  # Custom validation exception
│           ├── template/
│           │   ├── CreativeTemplate.java     # Template 6: Creative Indigo/Violet
│           │   ├── ModernTemplate.java       # Template 2: 2-Column Sidebar
│           │   ├── ProfessionalTemplate.java # Template 1: Classic Navy
│           │   ├── ResumeTemplate.java       # Abstract Base Template
│           │   ├── SimpleTemplate.java       # Template 3: ATS Minimalist
│           │   ├── StudentFresherTemplate.java # Template 4: Academic Teal
│           │   ├── TechnicalTemplate.java    # Template 5: Developer Monospace
│           │   └── TemplateType.java         # Template Enum
│           ├── test/
│           │   └── SystemTestRunner.java     # Automated 34-point Test Suite
│           └── ui/
│               ├── HomeFrame.java            # Main Dashboard Window
│               ├── ProfileFormFrame.java     # Candidate Input Form
│               ├── ProfileManagerFrame.java  # Management Table & Search
│               ├── ResumeCanvasPanel.java    # Custom 2D Canvas Panel
│               ├── ResumePreviewFrame.java   # Preview & PDF Export Window
│               └── UITheme.java              # Color Palette & Swing Styling Helpers
├── compile.bat                   # Windows CMD build script
├── run.bat                       # Windows CMD launcher script
├── compile.ps1                   # Windows PowerShell build script
├── run.ps1                       # Windows PowerShell launcher script
└── README.md                     # Comprehensive Project Documentation
```

---

## 🛠️ Prerequisites
- **Java Development Kit (JDK)**: JDK 8, 11, 17, 21, or 26.
- **Operating System**: Windows 10 / 11, macOS, or Linux.
- **IDE**: Visual Studio Code (with "Extension Pack for Java") or any standard text editor / terminal.

---

## 💻 How to Run the Project

### Option A: Running from Visual Studio Code (Recommended)
1. In Visual Studio Code, select **File > Open Folder...**
2. Choose the directory:  
   `ProfileManagementSystem`
3. Ensure the **Extension Pack for Java** is installed in VS Code.
4. Open [Main.java](file:///C:/Users/nandu/.gemini/antigravity-ide/scratch/ProfileManagementSystem/src/com/pms/Main.java).
5. Click the **Run** button at the top-right corner, or press **F5** to start with debugging.
6. Alternatively, open the **Run & Debug** panel (Ctrl+Shift+D) and select **"Run Profile Management System"**.

---

### Option B: Using Windows Command Prompt (CMD)
1. Open Command Prompt (`cmd.exe`).
2. Navigate to the project directory:
   ```cmd
   cd C:\Users\nandu\.gemini\antigravity-ide\scratch\ProfileManagementSystem
   ```
3. To compile the project:
   ```cmd
   compile.bat
   ```
4. To run the application:
   ```cmd
   run.bat
   ```

---

### Option C: Using Windows PowerShell
1. Open PowerShell.
2. Navigate to the project directory:
   ```powershell
   cd C:\Users\nandu\.gemini\antigravity-ide\scratch\ProfileManagementSystem
   ```
3. Compile:
   ```powershell
   .\compile.ps1
   ```
4. Run:
   ```powershell
   .\run.ps1
   ```

---

### Option D: Manual Command Line Execution
```bash
# Compile all source files
javac -encoding UTF-8 -cp "lib/*" -d bin src/com/pms/*.java src/com/pms/*/*.java

# Run Main
java -cp "bin;lib/*" com.pms.Main
```

---

## 🧪 Automated Verification Suite
To execute the automated 34-point unit and system test suite:
```cmd
java -cp "bin;lib/*" com.pms.test.SystemTestRunner
```
This tests:
- Model getters, setters, and UUID generation
- Validation constraints (email regex, year ranges, required fields)
- Persistence and CRUD cycles (Add, Search, Update, Delete)
- Corrupted data file backup and recovery
- High-DPI Graphics2D rendering across **all 6 templates**
- Apache PDFBox PDF generation and document integrity across **all 6 templates**

---

## 🎓 College Viva / Project Presentation Guide

### Frequently Asked Viva Questions:
1. **Why is Java Swing used instead of web technologies?**  
   Swing is Java's native desktop GUI framework. It runs locally without needing a web server, internet connection, or external runtime, making it fast, self-contained, and secure.

2. **How is Inheritance and Polymorphism applied in the Resume Templates?**  
   The abstract base class `ResumeTemplate` specifies the blueprint for resume rendering. Six distinct subclasses (`ProfessionalTemplate`, `ModernTemplate`, etc.) override the `render(...)` method and color hooks. At runtime, the application treats any template polymorphically, eliminating rigid conditional code.

3. **How does Profile persistence work?**  
   Profiles implement `java.io.Serializable`. When saving, `ProfileManager` uses `ObjectOutputStream` to write the object graph directly to disk in `data/profiles.dat`. On startup, `ObjectInputStream` restores the objects into an `ArrayList<Profile>`.

4. **How are corrupted files handled?**  
   If an unexpected or corrupted data stream is detected, `ProfileManager` catches the exception, makes a timestamped backup copy (`data/profiles_corrupted_<timestamp>.bak`), and initializes a fresh profile store so the application never crashes.

5. **How is the PDF generated?**  
   Using the Apache PDFBox vector rendering library. The `PDFExporter` maps the template styling, calculates string bounds, wraps paragraphs, handles pagination across multiple pages, and writes the vector PDF stream to disk.
