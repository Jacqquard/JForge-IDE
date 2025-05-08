# Security Policy for JFORGE

JFORGE is a desktop Java IDE. The security of our users is important. This document outlines our security policy, including how to report vulnerabilities.

## Supported Versions

Currently, JFORGE is a single-developer project, and support is provided for the latest version available in the main branch of its repository. As it's a learning/demonstration project, formal versioning and long-term support for older versions are not in place.

| Version | Supported          |
| ------- | ------------------ |
| Latest  | :white_check_mark: |
| < 0.x   | :x:                |

## Reporting a Vulnerability

We take all security bug reports seriously. We appreciate your efforts to responsibly disclose your findings.

**How to Report:**

If you discover a security vulnerability, please report it to us by emailing `maslennukuvilya@gmail.com`.
*Please do not create a public GitHub issue for security vulnerabilities.*

**What to Include:**

Please include the following details with your report:
*   A clear description of the vulnerability.
*   Steps to reproduce the vulnerability.
*   The version of JFORGE you are using (if applicable, or commit hash).
*   Any relevant environment details (e.g., Operating System, Java version).
*   Potential impact of the vulnerability.
*   Any suggested mitigations, if you have them.

**Our Commitment:**
*   We will acknowledge receipt of your vulnerability report within 48 hours.
*   We will investigate the report and determine its validity and severity.
*   We will aim to keep you informed of our progress.
*   We will publicly credit you for your discovery if you wish, once the vulnerability is addressed.

## Security Considerations & Scope

JFORGE is a local Integrated Development Environment (IDE). Its primary function is to edit, compile, and run Java code provided by the user.

1.  **Code Execution (`Compile and Run` Feature):**
    *   **This is the most significant security consideration.** JFORGE allows users to compile and execute arbitrary Java code.
    *   **User Responsibility:** Users are responsible for the code they write, open, or execute using JFORGE. Running code from untrusted sources carries inherent risks, as the code will execute with the permissions of the user running JFORGE.
    *   **No Sandboxing:** Currently, JFORGE does **not** implement any sandboxing mechanism for the code it compiles and runs. The executed Java code has the same access rights to the system (file system, network, etc.) as the JFORGE application itself, which typically means full user privileges.
    *   **Warning:** Exercise extreme caution when running Java code obtained from untrusted third parties.

2.  **File System Access:**
    *   JFORGE interacts with the file system to open, save, and manage `.java` files. These operations are initiated by the user through standard file dialogs.
    *   When compiling unsaved code, JFORGE creates a temporary file (e.g., `TempClass.java`) in the application's current working directory (`user.dir`). Users should be aware of this if their working directory is a sensitive location.

3.  **Input Handling (Syntax Highlighting):**
    *   The syntax highlighting feature uses regular expressions to parse editor content. While the current patterns are relatively simple, complex regex patterns applied to malicious input could theoretically lead to Regex Denial of Service (ReDoS). This is considered a low risk with the current implementation.

4.  **Dependencies:**
    *   JFORGE primarily relies on the standard Java Development Kit (JDK) and its included Swing libraries. Users are responsible for ensuring their JDK is obtained from a trusted source and kept up-to-date with security patches.
    *   The application uses `ToolProvider.getSystemJavaCompiler()`, which requires a full JDK installation.

5.  **No Built-in Network Features (Beyond User Code):**
    *   JFORGE itself does not initiate network connections for updates, telemetry, or other purposes. Any network activity would originate from the Java code being executed by the user.

## Best Practices for Users

*   Only open and run Java files from trusted sources.
*   Keep your Java Development Kit (JDK) updated.
*   Be mindful of the code you write and its potential impact on your system.

---

This `SECURITY.md` file should be placed in the root directory of your project. Remember to replace `maslennukuvilya@gmail.com` with an actual email address where users can send vulnerability reports. If this is a personal project, your personal GitHub-associated email might be fine, or you could create a free alias.