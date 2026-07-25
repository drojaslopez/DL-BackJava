@echo off
setlocal

if exist "%HOME%\mavenrc_pre.cmd" call "%HOME%\mavenrc_pre.cmd"

set MAVEN_CMD_LINE_ARGS=%*

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    if not exist "%MAVEN_HOME%\bin\mvn" (
        echo Maven not found. Please set MAVEN_HOME environment variable.
        exit /b 1
    )
)

if exist "%MAVEN_HOME%\bin\mvn.cmd" (
    call "%MAVEN_HOME%\bin\mvn.cmd" %MAVEN_CMD_LINE_ARGS%
) else (
    call "%MAVEN_HOME%\bin\mvn" %MAVEN_CMD_LINE_ARGS%
)
