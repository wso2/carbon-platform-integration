Running Governance Registry Mount Test Case
===========================================

Configure Mounting in Governance Registry
-----------------------------------------

MySQL
=====
Use MySQL command line or other UI to create the following two databses.

1. Create database M1 - CREATE DATABASE M1;
2. Create database W1 - CREATE DATABASE W1; 


Governance Registry
===================

Configure Governance Registry
-----------------------------
Use configurations provided in 'G-Reg-Configs' folder

1. Replace $CARBON_HOME/repository/conf/datasources/master-datasources.xml with master-datasources.xml.
2. Replace $CARBON_HOME/repository/conf/axis2/axis2.xml with axis2.xml.
3. Replace $CARBON_HOME/repository/conf/axis2/carbon.xml with carbon.xml.
 
Start Governance Registry
-------------------------
Linux : sh $CARBON_HOME/lib/wso2server.sh -Dsetup
Windows : $CARBON_HOME/lib/wso2server.bat -Dsetup


Mounted Governance Registry
===========================

Configure Mounted Governance Registry
-------------------------------------
Use configurations provided in 'Mounted-G-Reg-Configs' folder

1. Replace $CARBON_HOME/repository/conf/datasources/master-datasources.xml with master-datasources.xml.
2. Replace $CARBON_HOME/repository/conf/axis2/axis2.xml with axis2.xml.
3. Replace $CARBON_HOME/repository/conf/axis2/registry.xml with registry.xml.

Start Governance Registry
-------------------------
Linux : sh $CARBON_HOME/lib/wso2server.sh -Dsetup
Windows : $CARBON_HOME/lib/wso2server.bat -Dsetup


Run The Test Case
-----------------
If both servers are up and running you can run the RegistryMountTestCase.


