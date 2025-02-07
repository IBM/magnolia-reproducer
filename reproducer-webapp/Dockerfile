ARG DOCKERREGISTRY=docker.io
ARG VERSION=9-jdk11-temurin
ARG IMAGEVERSION=${DOCKERREGISTRY}/tomcat:${VERSION}
FROM "${IMAGEVERSION}"

COPY docker/tomcat-barebone/ /usr/local/tomcat/

RUN set -eux; \
    chmod uog+x /usr/local/tomcat/bin/setenv.sh; \
    chmod uog+x /usr/local/tomcat/bin/magnolia_control.sh;

RUN useradd --uid 1001 --gid 0 --home-dir ${CATALINA_HOME} --shell /usr/sbin/nologin tomcat
RUN mkdir -p /usr/local/magnolia
RUN chown -R 1001:0 .
RUN chown -R 1001:0 /usr/local/magnolia
RUN chown -R 1001:0 /usr/local/tomcat

ARG SOURCE_WAR_EXPLODED

COPY --chown=1001:0 ${SOURCE_WAR_EXPLODED} ${CATALINA_HOME}/webapps/ROOT

# allow group to write logs folder, assuming securityContext.runAsGroup is not set and so defaults to group root(0)
RUN chmod -R g=u /usr/local/tomcat/webapps

USER 1001
ENV MAGNOLIA_PROFILE=develop
#ENV MAGNOLIA_INSTANCE_TYPE=author
ENTRYPOINT ["/bin/sh", "-c", "magnolia_control.sh start && tail -f logs/catalina.out"]
