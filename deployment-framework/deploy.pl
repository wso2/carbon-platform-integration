#!/usr/bin/perl 
use strict;
use warnings;
use File::Basename;
use Archive::Extract;
use XML::Writer;
use XML::Simple;
use XML::Twig;
use File::Path;
use XML::DOM;

my $product_zip;
my $sqlconnector;
my $config_file;
my $base_dir;
my $snapshot_home;
my $dirname;
my $zipname;
my $registryXml = 'registry.xml';
my $useMgtXml   = 'user-mgt.xml';
my $carbonxml   = 'carbon.xml';
$sqlconnector = 'mysql-connector-java-5.1.12-bin.jar';
$dirname      = dirname(__FILE__);
require fileResolver;
require envResolver;
require xmlResolver;
require ldapConfig;
require mountConfig;
require clusterResolver;
require artifactGetter;
require sshTaskManager;
package as_extractor;

# Here is the main program using above classes.
package main;
fileResolver::CleanPath("$dirname/temp");
fileResolver::CleanPath("$dirname/SNAPSHOT");

my $deployName;
if ( xmlResolver::elemntExists( "$dirname/config/commonConfig.xml", 'LDAP' ) eq
	'1' )
{
	my $zipFile =
	  fileResolver::nameResolver( envResolver::resolveProduceName('IS'),
		"$dirname/resource/" );
	fileResolver::extractApp( "$dirname/resource/$zipFile",
		"$dirname/SNAPSHOT" );
}
foreach my $pro (
	envResolver::readProductList(
		xmlResolver::readxml(
			"$dirname/config/commonConfig.xml",
			'ProductList'
		)
	)
  )
{
	artifactGetter::downloadProduct($pro);
	my $productName = envResolver::resolveProduceName($pro);
	print
"\n **----------------------Deployment of $productName Started-----------------------------** \n ";
	my $zipFile =
	  fileResolver::nameResolver( envResolver::resolveProduceName($pro),
		"$dirname/resource/" );
	fileResolver::extractApp( "$dirname/resource/$zipFile",
		"$dirname/SNAPSHOT" );
	print "Extracting completed $zipFile\n";
	$zipFile =~ s/.zip//;
	$deployName = $zipFile;

	if ( $pro eq "DSS" ) {
		fileResolver::copyArtifacts(
			$dirname, $zipFile,
			fileResolver::nameResolver(
				'mysql-connector', "$dirname/resource/"
			)
		);
	}
	if (
		xmlResolver::readXmlConfig( 'driverName',
			envResolver::resolveXmlName($pro) ) =~ /mysql/
	  )
	{
		fileResolver::copyArtifacts(
			$dirname, $zipFile,
			fileResolver::nameResolver(
				'mysql-connector', "$dirname/resource/"
			)
		);
	}
	if (
		xmlResolver::readXmlConfig( 'driverName',
			envResolver::resolveXmlName($pro) ) =~ /oracle/
	  )
	{
		fileResolver::copyArtifacts( $dirname, $zipFile,
			fileResolver::nameResolver( 'ojdbc', "$dirname/resource/" ) );
	}
	my $xmlpath = "$dirname/SNAPSHOT/$zipFile/repository/conf/";
	my $xmlfile = envResolver::resolveXmlName($pro);
	xmlResolver::readElementlist( "$xmlpath",
		envResolver::resolveXmlName($pro) );
	if ( $pro ne 'GREG' ) {
		mountConfig::setoffset( "$xmlpath$carbonxml",
			envResolver::resolveOffset($pro) );
		if (
			xmlResolver::elemntExists( "$dirname/config/commonConfig.xml",
				'MountDatabase' ) eq '1'
		  )
		{
			mountConfig::mount(
				'MountDatabase',                    'dbConfig',
				'@name',                            'name',
				'',                                 "",
				"$dirname/config/commonConfig.xml", "$xmlpath$registryXml",
				envResolver::resolveProduceName($pro)
			);
			mountConfig::setoffset( "$xmlpath$carbonxml",
				envResolver::resolveOffset($pro) );

		}
		if (
			xmlResolver::elemntExists( "$dirname/config/commonConfig.xml",
				'MountInstance' ) eq '1'
		  )
		{
			mountConfig::mount(
				'MountInstance',                    'remoteInstance',
				'@url',                             'url',
				'',                                 "",
				"$dirname/config/commonConfig.xml", "$xmlpath$registryXml",
				envResolver::resolveProduceName($pro)
			);
		}
		if (
			xmlResolver::elemntExists(
				"$dirname/config/commonConfig.xml",
				'GovernenceMount'
			)
		  )
		{
			mountConfig::mount(
				'GovernenceMount',                  'mount',
				'@path',                            'path',
				'@overwrite',                       'overwrite',
				"$dirname/config/commonConfig.xml", "$xmlpath$registryXml",
				envResolver::resolveProduceName($pro)
			);
		}
		if (
			xmlResolver::elemntExists( "$dirname/config/commonConfig.xml",
				'ConfigMount' ) eq '1'
		  )
		{
			mountConfig::mount(
				'ConfigMount',                      'mount',
				'@path',                            'path',
				'@overwrite',                       'overwrite',
				"$dirname/config/commonConfig.xml", "$xmlpath$registryXml",
				envResolver::resolveProduceName($pro)
			);
		}
	}
	my $isfile =
	  fileResolver::nameResolver( envResolver::resolveProduceName($pro),
		"$dirname/resource/" );
	$zipFile =~ s/.zip//;
	$deployName = $zipFile;
	print $deployName;
	$isfile =~ s/.zip//;

	if (
		(
			xmlResolver::elemntExists( "$dirname/config/commonConfig.xml",
				'LDAP' ) eq '1'
		)
		&& $pro ne 'GREG'
	  )
	{
		ldapConfig::createldap( $dirname, $deployName, $isfile );
	}
	if (
		clusterResolver::getNodes( "$dirname/config", '/commonConfig.xml',
			'Enablecluster' ) eq 'true'
	  )
	{
		clusterResolver::activateCluster(
			$dirname,    '/config/clusterConfig.xml',
			$deployName, envResolver::resolveCluster($pro)
		);
	}
	
	
	
	print
"\n**----------------------Deployment of $productName Compleated-----------------------------** \n";
}
sshTaskManager::setRemoteEnvironment( "$dirname", '/config/commonConfig.xml');
sshTaskManager::upProductList("$dirname", '/config/commonConfig.xml');
print
"\n **----------------------Deployment Compleated-----------------------------** \n";
