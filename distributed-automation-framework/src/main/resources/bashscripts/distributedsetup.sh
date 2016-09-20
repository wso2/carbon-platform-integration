#!/bin/bash

echo Test Automation Script Initialization .... $@

TEST_RESOURCE_LOCATION=$5
DISTRIBUTION_NAME="wso2am-1.10.0.zip"
PUPPET_MODULE_LOCATION=$5"/Docker-Puppet/puppet-module"
DOCKER_MODULE_LOCATION=$5"/Docker-Puppet/dockerfiles"
PRIVATE_DOCKERHUB=$1
CONNECTOR=mysql-connector-java-5.1.36-bin.jar
K8S_JACKSON_CORE_JAR=jackson-core-2.5.4.jar
K8S_JACKSON_DATABIND_JAR=jackson-databind-2.5.4.jar
K8S_JACKSON_ANNOTATIONS_JAR=jackson-annotations-2.5.4.jar
JDK=jdk-7u80-linux-x64.tar.gz
FILE=$TEST_RESOURCE_LOCATION"/artifacts/AM/scripts/bashscripts/images.txt"

password_encoded="Uml6bGEhMjMK"
password_decoded=$(echo "$password_encoded" | openssl enc -d -base64)

dockerRegistryLogin(){
  docker login --username=iqbal --email=iqbal@wso2.com --password=$password_decoded dockerhub.private.wso2.com
}

# step 01
echo "Copying product distribution from ... "$TEST_RESOURCE_LOCATION"/distribution/wso2am"
cp $TEST_RESOURCE_LOCATION"/distribution/wso2am"/wso2am-1.10.0.zip $PUPPET_MODULE_LOCATION/modules/wso2am/files

# step 02
echo "Copying K8S MEMBERSHIP JARS ...." "" $PUPPET_MODULE_LOCATION "" "/modules/wso2am/files/configs/repository/components/lib"
cp $TEST_RESOURCE_LOCATION"/artifacts/AM/jar"/$K8S_JACKSON_CORE_JAR $PUPPET_MODULE_LOCATION/modules/wso2am/files/configs/repository/components/lib
cp $TEST_RESOURCE_LOCATION"/artifacts/AM/jar"/$K8S_JACKSON_DATABIND_JAR $PUPPET_MODULE_LOCATION/modules/wso2am/files/configs/repository/components/lib
cp $TEST_RESOURCE_LOCATION"/artifacts/AM/jar"/$K8S_JACKSON_ANNOTATIONS_JAR $PUPPET_MODULE_LOCATION/modules/wso2am/files/configs/repository/components/lib

# step 03
echo "Copying JDK ...."
cp $TEST_RESOURCE_LOCATION"/artifacts/AM/jdk/$JDK" $PUPPET_MODULE_LOCATION"/modules/wso2base/files"

if [ "$2" = "pattern01" ]
then echo "Executing pattern 01"
elif [ "$2" = "pattern02" ]
then echo "Executing pattern 02"
elif [ "$2" = "default" ]
then echo "Executing default"

     # step 04
     if [ "$3" = "MySql" ]
     then CONNECTOR=mysql-connector-java-5.2.2-bin.jar
     echo "Copying DB Connector to ...." "" $PUPPET_MODULE_LOCATION "" "/modules/wso2am/files/configs/repository/components/lib"
     cp $TEST_RESOURCE_LOCATION"/artifacts/AM/jar"/$CONNECTOR $PUPPET_MODULE_LOCATION/modules/wso2am/files/configs/repository/components/lib
     fi

     # step 05
     echo "Setting hiera data yaml files for pattern default ....."

     #cp $TEST_RESOURCE_LOCATION"/artifacts/AM/patterns/default"/api-key-manager.yaml $PUPPET_MODULE_LOCATION/hieradata/dev/wso2/wso2am/1.10.0/kubernetes
     #cp $TEST_RESOURCE_LOCATION"/artifacts/AM/patterns/default"/api-publisher.yaml $PUPPET_MODULE_LOCATION/hieradata/dev/wso2/wso2am/1.10.0/kubernetes
     #cp $TEST_RESOURCE_LOCATION"/artifacts/AM/patterns/default"/api-store.yaml $PUPPET_MODULE_LOCATION/hieradata/dev/wso2/wso2am/1.10.0/kubernetes
     #cp $TEST_RESOURCE_LOCATION"/artifacts/AM/patterns/default"/gateway-manager.yaml $PUPPET_MODULE_LOCATION/hieradata/dev/wso2/wso2am/1.10.0/kubernetes

     #step 06
     echo running bash.sh script

     #$DOCKER_MODULE_LOCATION/wso2am/build.sh -v 1.10.0 -l api-publisher -r puppet -s kubernetes
     #$DOCKER_MODULE_LOCATION/wso2am/build.sh -v 1.10.0 -l api-store -r puppet -s kubernetes
     #$DOCKER_MODULE_LOCATION/wso2am/build.sh -v 1.10.0 -l gateway-manager -r puppet -s kubernetes
     #$DOCKER_MODULE_LOCATION/wso2am/build.sh -v 1.10.0 -l api-key-manager -r puppet -s kubernetes

     #docker tag wso2am-api-publisher:1.10.0 dockerhub.private.wso2.com/dimuthud-wso2-am-api-publisher:1.10.0
     #docker tag wso2am-api-store:1.10.0 dockerhub.private.wso2.com/dimuthud-wso2-am-api-store:1.10.0
     #docker tag wso2am-gateway-manager:1.10.0 dockerhub.private.wso2.com/dimuthud-wso2-am-api-gateway:1.10.0
     #docker tag wso2am-api-key-manager:1.10.0 dockerhub.private.wso2.com/dimuthud-wso2-am-api-keymanager:1.10.0

     echo logging to private wso2 docker repo

     dockerRegistryLogin

     echo pushing images private wso2 docker repo ...  $PRIVATE_DOCKERHUB

     #docker push  $PRIVATE_DOCKERHUB/dimuthud-wso2-am-api-publisher:1.10.0
     #docker push  $PRIVATE_DOCKERHUB/dimuthud-wso2-am-api-store:1.10.0
     #docker push  $PRIVATE_DOCKERHUB/dimuthud-wso2-am-api-gateway:1.10.0
     #docker push  $PRIVATE_DOCKERHUB/dimuthud-wso2-am-api-keymanager:1.10.0

     echo writing to text file docker image details


     #mkdir $TEST_RESOURCE_LOCATION"/artifacts/AM/scripts/bashscripts" wso2am-api-key-manager
     touch FILE
     /bin/cat <<EOM >$FILE
     wso2am-api-publisher: image: $PRIVATE_DOCKERHUB/dimuthud-wso2-am-api-publisher tag:1.10.0
     wso2am-api-store: image:$PRIVATE_DOCKERHUB/dimuthud-wso2-am-api-store tag:1.10.0
     wso2am-gateway-manager: image: $PRIVATE_DOCKERHUB/dimuthud-wso2-am-api-gateway tag:1.10.0
     wso2am-api-key-manager: image: $PRIVATE_DOCKERHUB/dimuthud-wso2-am-api-keymanager tag:1.10.0


EOM

fi


