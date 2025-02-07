#!/bin/sh

MAX_FILES_INSTALL_MINIMUM=15000
MAX_PROCESS_FILES_INSTALL_MINIMUM=5000

#--- this was copied from the tomcat scripts ...
# resolve links - $0 may be a softlink
PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
PRGDIR=`dirname "$PRG"`
#---

if [ "$1" = "start" ] ; then
    # check the number of allowed file descriptors and issue warning it's too low
    max_files=`sysctl -n fs.file-max 2> /dev/null || sysctl -n kern.maxfiles 2> /dev/null`; echo $num
    max_process_files=`ulimit -n`
    ignore_open_files_limit=false
    for var in "$@"
    do
        if [ "$var" = "--ignore-open-files-limit" ]; then
            ignore_open_files_limit=true
        fi
    done

    if [ $ignore_open_files_limit = false ] && ([ "$max_files" -lt $MAX_FILES_INSTALL_MINIMUM ] || [ "$max_process_files" -lt $MAX_PROCESS_FILES_INSTALL_MINIMUM ]); then
        echo "[ERROR]: #######################################################################################################################"
        echo "[ERROR]: The max open files limit allowed by your system may be too low."
        echo "[ERROR]: See https://documentation.magnolia-cms.com/display/DOCS/Known+issues#Knownissues-Toomanyopenfiles for more information."
        echo "[ERROR]: If you want to suppress this check, use --ignore-open-files-limit flag."
        echo "[ERROR]: #######################################################################################################################"
        exit 1
    fi

    # create public webapp(from author webapp) when "installed" file and "magnoiaPublic/WEB-INF" directory doesn't exist
    if [ ! -e "$PRGDIR/.installed" ] && [ ! -d "$PRGDIR/../webapps/magnoliaPublic/WEB-INF" ] && [ -d "$PRGDIR/../webapps/magnoliaAuthor/WEB-INF" ] ; then
      echo "First run -> create magnoliaPublic webapp from magnoliaAuthor webapp."
      if [ -d "$PRGDIR/../webapps/magnoliaPublic" ] ; then
         rm -rf $PRGDIR/../webapps/magnoliaPublic
      fi
      mkdir $PRGDIR/../webapps/magnoliaPublic
      cp -r $PRGDIR/../webapps/magnoliaAuthor/docroot $PRGDIR/../webapps/magnoliaPublic/docroot
      cp -r $PRGDIR/../webapps/magnoliaAuthor/modules $PRGDIR/../webapps/magnoliaPublic/modules
      cp -r $PRGDIR/../webapps/magnoliaAuthor/META-INF $PRGDIR/../webapps/magnoliaPublic/META-INF
      cp -r $PRGDIR/../webapps/magnoliaAuthor/WEB-INF $PRGDIR/../webapps/magnoliaPublic/WEB-INF
      cp $PRGDIR/../webapps/magnoliaAuthor/LICENSE.txt $PRGDIR/../webapps/magnoliaPublic/LICENSE.txt
      cp $PRGDIR/../webapps/magnoliaAuthor/NOTICE.txt $PRGDIR/../webapps/magnoliaPublic/NOTICE.txt
      cp $PRGDIR/../webapps/magnoliaAuthor/README.txt $PRGDIR/../webapps/magnoliaPublic/README.txt
      echo "This file indicates that the public webapp was created. The file is created during first run." > $PRGDIR/.installed
    fi
    # to start with jpda (debugging):
    # exec "$PRGDIR"/catalina.sh jpda start
    exec "$PRGDIR"/startup.sh
elif [ "$1" = "stop" ] ; then
    exec "$PRGDIR"/shutdown.sh
else
    echo "Please provide \"start\" or \"stop\" as argument."
    exit 1
fi
