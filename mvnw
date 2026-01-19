#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# Maven Wrapper startup script for Unix/Linux/Mac

MAVEN_VERSION=3.9.5
DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz"
MAVEN_HOME="$HOME/.m2/wrapper/dists/apache-maven-${MAVEN_VERSION}"

# Check if Maven is already downloaded
if [ ! -f "$MAVEN_HOME/bin/mvn" ]; then
    echo "Downloading Apache Maven $MAVEN_VERSION..."
    echo "This will only happen once."
    
    # Create directories
    mkdir -p "$HOME/.m2/wrapper/dists"
    
    # Download and extract Maven
    curl -L "$DOWNLOAD_URL" -o /tmp/maven.tar.gz
    tar -xzf /tmp/maven.tar.gz -C "$HOME/.m2/wrapper/dists"
    rm /tmp/maven.tar.gz
    
    echo "Maven downloaded and extracted successfully."
fi

# Execute Maven
exec "$MAVEN_HOME/bin/mvn" "$@"
