@ECHO OFF
SET DIRNAME=%~dp0
IF "%JAVA_HOME%"=="" GOTO useJava
SET JAVACMD=%JAVA_HOME%\bin\java.exe
GOTO execute
:useJava
SET JAVACMD=java.exe
:execute
"%JAVACMD%" -classpath "%DIRNAME%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
