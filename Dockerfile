FROM tomcat
# Add all files and directorie to webapp sae

ADD . /usr/local/tomcat/webapps/sae

# COMPILE all java files in all packeges and sub packages in the project

RUN javac -cp /usr/local/tomcat/lib/*:/usr/local/tomcat/webapps/sae/WEB-INF/lib/* $(find /usr/local/tomcat/webapps/sae/WEB-INF/src/ -name "*.java") -d /usr/local/tomcat/webapps/sae/WEB-INF/classes/ && rm -rf /usr/local/tomcat/webapps/sae/WEB-INF/src/

# RUN the tomcat server

CMD ["catalina.sh", "run"]