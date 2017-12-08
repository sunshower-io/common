FROM sunshower/sunshower-base:1.0.0 

# Environment Variables
ENV PROJECT_NAME=common
ENV PROJECT_LOCATION https://github.com/sunshower-io/$PROJECT_NAME.git
ENV GRADLE_VERSION 4.3.1
ENV MAVEN_VERSION 3.5.2

COPY ./ /home/sunshower/common
ENV VERSION_QUALIFIER ""
ENV MAVEN_REPOSITORY_URL "" 
ENV MVN_REPO_USERNAME "" 
ENV MVN_REPO_PASSWORD "" 

ENTRYPOINT mvn clean install deploy -f bom/pom.xml --settings=/root/.m2/settings.xml && \
gradle clean build publish --stacktrace \
-PmavenRepositoryUrl=$MAVEN_REPOSITORY_URL \
-PmavenRepositoryUsername=$MVN_REPO_USERNAME \
-PmavenRepositoryPassword=$MVN_REPO_PASSWORD \
-PversionQualifier=$VERSION_QUALIFIER
