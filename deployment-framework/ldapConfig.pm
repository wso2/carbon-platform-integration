#!/usr/bin/perl
#MAin perl Class for Deployment Framework
use strict;
use XML::LibXML;
use XML::XPath;
use File::Slurp ();

package ldapConfig;

sub createldap {
	my ( $dirpath, $productdir, $isDir ) = @_;
	ldap( $dirpath, $productdir, $isDir );
}

sub ldap {
	my ( $dirpath, $productdir, $isDir ) = @_;
	my $configPath = "$dirpath/config/";
	print
"\n-----------------------LDAP for $productdir started-------------------------\n";
	my $file   = "$dirpath/SNAPSHOT/$productdir/repository/conf/user-mgt.xml";
	my $parser = XML::LibXML->new();
	my $tree   = $parser->parse_file($file);
	my $root   = $tree->getDocumentElement;

	my $element   = XML::LibXML::Document->new();
	my $element2  = XML::LibXML::Document->new();
	my $name_node = $root->getElementsByTagName('Realm');
	my $doc       = XML::LibXML::Document->new($name_node);
	foreach my $parent ( $root->findnodes('Realm') ) {
		$element  = $parent;
		$element2 = $parent;
		foreach my $child ( $parent->findnodes('UserStoreManager') ) {
			my $camelid2 = XML::LibXML::Comment->new($child);
			$element->replaceChild( $camelid2, $child );
		}
		$element->appendChild( readisNode( $dirpath, $isDir ) );

	}

	File::Slurp::write_file( $file, $root->toString(1) );
	my $uname = getNodes( $configPath, 'commonConfig.xml', 'LDAP', 'UserName' );
	my $port  = getNodes( $configPath, 'commonConfig.xml', 'LDAP', 'port' );
	my $domainName =
	  getNodes( $configPath, 'commonConfig.xml', 'LDAP', 'DomainName' );
	my $passwd =
	  getNodes( $configPath, 'commonConfig.xml', 'LDAP', 'Password' );
	my $adminPw =
	  getNodes( $configPath, 'commonConfig.xml', 'LDAP', 'AdminPassword' );
	setldapEnv( $file, $port, $domainName, $uname, $passwd, $adminPw );
	print "\n .....completed ..... \n";

}
1;

sub readisNode {
	my ( $dirPath, $isDir ) = @_;
	my $file   = "$dirPath/SNAPSHOT/$isDir/repository/conf/user-mgt.xml";
	my $parser = XML::LibXML->new();
	my $tree   = $parser->parse_file($file);
	my $root   = $tree->getDocumentElement;
	my $node;
	foreach my $camelid ( $root->findnodes('Realm/UserStoreManager') ) {
		$node = $camelid;
	}
	return $node;
}
1;

sub setldapEnv {
	my ( $file, $port, $url, $userName, $password, $adminPw ) = @_;
	my $path = "$file";
	my $xp = XML::XPath->new( filename => $path );
	$xp->setNodeText( q{//UserStoreManager/Property[@name="ConnectionURL"]},
		"$url:$port" );
	$xp->setNodeText(
		q{//UserStoreManager/Property[@name="ConnectionPassword"]}, $adminPw );
	$xp->setNodeText( q{//Configuration/AdminUser/UserName}, $userName );
	$xp->setNodeText( q{//Configuration/AdminUser/Password}, $password );
	File::Slurp::write_file( $path, $xp->findnodes_as_string('/') );
}
1;

sub getNodes {
	my ( $dirPath, $configFile, $topNode, $ele ) = @_;
	my $file   = "$dirPath$configFile";
	my $parser = XML::LibXML->new();
	my $tree   = $parser->parse_file($file);
	my $root   = $tree->getDocumentElement;
	my $common_name;
	foreach my $config ( $root->findnodes($topNode) )
	{
		my @name_node = $config->getElementsByTagName($ele);
		$common_name = $name_node[0]->getFirstChild->getData;
	}
	return $common_name;
}

1;
