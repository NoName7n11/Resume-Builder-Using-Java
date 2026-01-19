@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM Maven Wrapper startup script for Windows

@echo off
@setlocal

set ERROR_CODE=0

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

set MAVEN_CMD_LINE_ARGS=%*

@REM Find the project base dir
set MAVEN_PROJECTBASEDIR=%~dp0
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

set MAVEN_VERSION=3.9.5
set DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip

@REM Maven home directory
set MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%

@REM Check if Maven is already downloaded
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Downloading Apache Maven %MAVEN_VERSION%...
    echo This will only happen once.
    
    @REM Create directories
    if not exist "%USERPROFILE%\.m2\wrapper\dists" mkdir "%USERPROFILE%\.m2\wrapper\dists"
    
    @REM Download Maven
    powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%TEMP%\maven.zip'}"
    
    @REM Extract Maven
    powershell -Command "& {Expand-Archive -Path '%TEMP%\maven.zip' -DestinationPath '%USERPROFILE%\.m2\wrapper\dists' -Force}"
    
    @REM Clean up
    del "%TEMP%\maven.zip"
    
    echo Maven downloaded and extracted successfully.
)

@REM Execute Maven
"%MAVEN_HOME%\bin\mvn.cmd" %MAVEN_CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

exit /B %ERROR_CODE%
