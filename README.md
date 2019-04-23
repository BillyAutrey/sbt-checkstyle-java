# SBT Checkstyle Java

A lightweight program to enable support for the Java "Checkstyle" plugin in compiled Java code.

## Usage

```sbtshell
sbt checkstyle
```

## Commands

| Command | Description |
| --- | --- |
| `checkstyle` | Executes Java Checkstyle to analyze only the Java code in a project |
| `checkstyleFetchGoogle` | Fetch checkstyle's Google style format from their git repository. |

## Settings

| Option | Description |
| --- | --- |
| `checkstyleConfig` | Location of a style configuration.  Defaults to `resources/google_checks.xml` |
| `checkstyleTarget` | Destination of the XML file containing the checkstyle report.  Defaults to `target/checkstyle-result.xml` |