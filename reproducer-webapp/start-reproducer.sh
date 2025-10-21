podman run -it \
  -p 8080:8080 \
  -v ./modules:/usr/local/tomcat/lightmodules:Z \
  -e CATALINA_OPTS="-Dmagnolia.resources.dir=/usr/local/tomcat/lightmodules" \
  magnolia-reproducer 