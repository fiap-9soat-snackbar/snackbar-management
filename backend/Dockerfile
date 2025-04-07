# Using 1/Tested version for stability, 2/JRE to reduce image size and attack surface (e.g. exposure to known vulnerabilities)
# 3/OpenJDK 21 (21.0.4_7) chosen as latest LTS Java version available, runs on Ubuntu 24.04
FROM eclipse-temurin:21-jre

# Set Java web server default port to 8080 for build and environment variables
ARG APP_PORT=8080
ENV APP_PORT=$APP_PORT
EXPOSE $APP_PORT

# Create application directory, create group and user specific for app, and change ownership for app specific user, to avoid run as root 
#RUN mkdir /app && groupadd --system javauser
#RUN useradd --system --shell /bin/false -g javauser javauser
#RUN chown -R javauser:javauser /app

# Define working directory and define app specific user
#WORKDIR /app
#USER javauser

# Define JAR file and copy source code last, as it changes the most
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Run command for application in exec form
CMD ["java", "-jar", "app.jar"]