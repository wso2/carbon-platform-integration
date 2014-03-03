Deployment Framework
====================

1. Introduction.
----------------

This README describes brief overview of configuration, deployment, execution and management of Deployment Framework. 

Deployment framework is a deployment configuration and setup automation for WSO2 products. which automates the Database configurations, creating mounts, Clustering and configuring ldap.

2. System Requirements.
-----------------------

1. Minimum memory - 256MB
2. Processor      - Pentium 800MHz or equivalent at minimum
4. Hard disk space 3GB minimum based on deployment. 
3. Perl support.
4. Following Perl modules will be needed.
	-strict
	-warnings
	-XML::DOM
	-XML::Simple
	-XML::Writer;
	-XML::Parser
	-Archive::Extract
	-XML::Twig
	-File::Path
	-XML::XPath
	-XML::LibXML
	-File::Slurp ()
	-XML::XPath::XMLParser

	Installing Perl modules through cpan

		1.Install cpan.
			sudo  apt-get install build-essential
		2.Install Modules.
			sudo cpan XML::DOM
			sudo cpan XML::Simple
			sudo cpan XML::Writer;
			sudo cpan XML::Parser
			sudo cpan Archive::Extract
			sudo cpan XML::Twig
			sudo cpan File::Path
			sudo cpan XML::XPath
			sudo cpan XML::LibXML
			sudo cpan File::Slurp ()
			sudo cpan XML::XPath::XMLParser
                        sudo cpan Archive::Zip
                        sudo cpan WWW::Mechanize
                        sudo cpan Net::SCP
                        sudo cpan Net::SCP::Expect
                        sudo cpan Net::SSH::Expect
                        sudo cpan Crypt::RSA
                        sudo cpan Math::Pari
                        sudo cpan Math::GMP - (apt-get instgall libmath-gmp-perl for ubuntu)
                        sudo cpan Net::SSH::Perl

3. Deployment Framework file and directory structure.
-----------------------------------------------------

	DeploymentFramework
		|- config <folder>
			|-product_config <folder>
				|--asConfig.xml  <file>
				|--bpsConfig.xml <file>
				|--brsConfig.xml <file>
				|--cepConfig.xml <file>
				|--dssConfig.xml <file>
				|--esbConfig.xml <file>
				|--gregConfig.xml<file>
				|--gsConfig.xml  <file>
				|--isConfig.xml  <file>
				|--mbConfig.xml  <file>
				|--msConfig.xml  <file>
			|--commonConfig.xml <file>
			|--clusterConfig.xml <file>
		|- resources <folder>
                |- SNAPSHOT <folder>
		|--deploy.pl <file>
		|--clusterresolver.pm <perl module>
		|--envResolver.pm <perl module>
		|--fileResolver.pm <perl module>
		|--ldapConfig.pm <perl module>
		|--mountConfig.pm
		|--xmlResolver.pm
		|--README.txt


	-config
		Contains all configuration XML files needed for Deployment  Framework.

		-product_config
			Contains product based configuration for all supported wso2 products.
		
		--commonConfig.xml 
			Contains all configurations on product selection ,mounts, an LDAPs.

		--clusterConfig.xml 
			Contains all configurations on Clusters to be created.

	-resources
		Contains all artifacts of all products and database drivers to be use.

	-SNAPSHOT
		Targer folder for output final deployed package.
	
	--deploy.pl 
		Main perl file to execute.

	-- envResolver
		Contans all lookups for the environment, including Prodict config files, Product names. Deployment directory names.

	-- README.txt
	  This document.


4. Installation & Running.
--------------------------

	1. Extract DeploymentFramework.tar.bz2 by
	tar-xzvf DeploymentFramework.tar.bz2

	2. Copy .zip distributions of all products and database drivers to resources directory.
	(Please keep original naming of the package ex:- wso2esb-x.x.x).

	3. Configure .xml files in config directory -** Please refer section 5.

	4. execure deploy.pl file from the terminal.
	perl deploy.pl

	5. Configured product directories will be available at SNAPSHOT directory.

	note :- Once you execute the script all previous files in SNAPSOT directory will be removed.
	

5. Configuration and deployment.
--------------------------------

	5.1. Adding product list to deploy
	
	    	  i. Make sure all .zip files are copied in to resources directory.
	   	 ii. Add product code seperated by the "," for the tag <ProductList> </ProductList> in commonConfig.xml.
	   	iii. Product code reference.
		 
			AS 	= 'wso2as'
			ESB 	= 'wso2esb'
			GREG	= 'wso2greg'
			DSS	='wso2dataservices'
			BPS	= 'wso2bps'
			IS	= 'wso2is'
			MS	= 'wso2ms'
			MB	= 'wso2mb'
			GS	= 'wso2gs'
			CEP	= 'wso2brs'

		ex :- <ProductList>AS,IS,GREG,ESB,MB</ProductList>

	5.2.Configure Database for a product

	 	i. Open the config XML at DeploymentFramework/config/product_config/<product_XML>.xml.
			ex:- For ESB  
				vim .../DeploymentFramework/config/product_config/esbConfig.xml
	       ii. con fig file reference.

			AS=   'asConfig.xml'
			ESB = 'esbConfig.xml'
			GREG= 'gregConfig.xml'
			DSS=  'dssConfig.xml'
			BPS=  'bpsConfig.xml'
			IS=   'isConfig.xml'
			MS=   'msConfig.xml'
			MB=   'mbConfig.xml'
			GS=   'gsConfig.xml'
			CEP=  'cepConfig.xml'
			BRS = 'bpsConfig.xml'
	

			<esb>	
			    <Database>
		    		<currentDBConfig>mysql-db</currentDBConfig>
	    			<dbConfigname>mysql-db</dbConfigname>
				<url>jdbc:mysql://localhost:3306/regdb</url>
				<userName>regadmin</userName>
				<password>regadmin</password>
				<driverName>com.mysql.jdbc.Driver</driverName>
				<maxActive>80</maxActive>
				<maxWait>60000</maxWait>
				<minIdle>5</minIdle> 
				   |          |
				   |          |
				   |          |
			    </Database>   
			</esb>

		Explanation of the database configuration options.

			currentDBConfig : Assigns the database config to use for the product.
			dbConfigname : Name for the particular database configure setting.
			url: The URL of the database
			userName: The name of the database user
			password: The password of the database user
			driverName: The class name of the database driver
		**Note :- You can append any number of configurations inside <Database></Database> tag.

	5.3. Configuring Mount and clusters

		  i. Configure mount database for wso2greg as step 4.
		 ii. Add the mount database to commonConfig.xml
			<MountDatabase>
				<dbConfig name="oracle-db-mount"></dbConfig>
				<url>jdbc:oracle:thin:dharshana/dharshana2@10.100.3.104:1521:orcl</url>
				<userName>dharshana</userName>
				<password>dharshana</password>
				<driverName>oracle.jdbc.driver.OracleDriver</driverName>
					   |          |
					   |          |
			</MountDatabase>
		 **Note :- You have to remove this element from commonConfig.xml if you dont want to configure mounts for your setup.

		iii. Add Mount Instance 

			<MountInstance>
				<remoteInstance url="https://localhost:9443/registry"></remoteInstance>
				<id>conf</id>
				<dbConfig>oracle-db-mount</dbConfig>
				<readOnly>false</readOnly>
				<enableCache>true</enableCache>
				<registryRoot>/</registryRoot>
			</MountInstance>
		**Note :- You have to remove this element from commonConfig.xml if you dont want to configure mounts for your setup.

		iv. Add Governance mount

		<GovernenceMount>
			<mount path="/_system/governance" overwrite="true"></mount>
			<instanceId>conf</instanceId>
			<targetPath>/_system/governance</targetPath>
		</GovernenceMount>

		**Note :- You have to remove this element from commonConfig.xml if you dont want to configure Governance mount for your setup.

		iv. Add Governamce mount

		<ConfigMount>
			<mount path="/_system/config" overwrite="true"></mount>
			<instanceId>conf</instanceId>
			<targetPath>/_system/nodes</targetPath>
		</ConfigMount>

		**Note :- You have to remove this element from commonConfig.xml if you dont want to configure Config mount for your setup.
		**Note :- The target path of the ConfigMount the part "/node" is replaced by the product name.

		v. Enable Clusters.

		On commonConfig.xml add following element.

		<Clusters>
			<Enablecluster>true</Enablecluster>
		</Clusters>	
	  
	    	On clusterConfig.xml following should be available.
	      Ex - for ESB cluster 
			Add your new cluster inside <esb></esb> tag.

			<esb>
			   <Cluster name="wso2esb-1">
				<Domain>wso2.carbon.domain.as2</Domain>
				<Offset>27</Offset>
				<readOnly>false</readOnly>
				<targetPath>/_system/esb1</targetPath>
				<Database>
					<currentDBConfig>oracle-esb1</currentDBConfig>
					<dbConfigname>oracle-esb1</dbConfigname>
					<url>jdbc:oracle:thin:dharshana/dharshana2@10.100.3.104:1521:orcl</url>
					<userName>dharshana</userName>
					<password>dharshana</password>
					<driverName>oracle.jdbc.driver.OracleDriver</driverName>
					<maxActive>5</maxActive>
					<maxWait>60000</maxWait>
					<minIdle>50</minIdle>
					<validationQuery>SELECT 1 FROM DUAL</validationQuery>
				</Database>
			   </Cluster>
			</esb>

		Cluster @Name   : attribute assigns a name for new cluster. The directory is created on particular name.
	 	Domain		: Domain Name for the cluster.
		Offset  	: Offset number for the new instance.
		readOnly 	: Determines whether the mount is primery or secondery.       
		targetPath	: Target share instance for the ConfigMount.
		Database	: Default data base for the particular cluster.


	5.4 Configuring LDAP.

	  	i. Create LDAP on wso2is server.
		ii. Configure IS as described above.
		iii. On commonConfig.xml add following element. 

		<LDAP>
			<UserName>admin</UserName>
			<Password>admin</Password>
			<DomainName>ldap://localhost</DomainName>
			<AdminPassword>admin</AdminPassword>
			<port>10394</port>
		</LDAP>

	UserName :  User Name for the LDAP server.
	Password : Passord for LDAP login.
	DomainName: Domain name for the LDAP server.
	AdminPassword: Admin password.
	port : LDAP Port.

	**Note :- You have to remove this element from commonConfig.xml if you don’t want to configure LDAP for your setup.


6 .Running Samples.
--------------------

 All sample configuration file set is available on the sample directory.
