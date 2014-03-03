#!usr/bin/perl -w
use strict;
use XML::LibXML;
use File::Slurp ();
use XML::XPath;
require xmlResolver;
require envResolver;
require mountConfig;

package clusterResolver;

sub activateCluster {
	my ( $dirPath, $configFile, $copyDir, $product ) = @_;
	editNodes( $dirPath, $configFile, $copyDir, $product );
}

sub editNodes {
	my ( $dirPath, $configFile, $copyDir, $product ) = @_;
	my $file   = "$dirPath$configFile";
	my $parser = XML::LibXML->new();
	my $tree   = $parser->parse_file($file);
	my $root   = $tree->getDocumentElement;
	foreach my $product ( $root->findnodes($product) ) {
		foreach my $cluster ( $product->findnodes('Cluster') ) {
			my @name_node   = $cluster->getElementsByTagName('currentDBConfig');
			my $common_name = $name_node[0]->getFirstChild->getData;
			my @domain_node = $cluster->getElementsByTagName('Domain');
			my $domain_name = $domain_node[0]->getFirstChild->getData;
			my @offset      = $cluster->getElementsByTagName('Offset');
			my $offset_name = $offset[0]->getFirstChild->getData;
			my @readOnly    = $cluster->getElementsByTagName('readOnly');
			my $readOnly_name   = $readOnly[0]->getFirstChild->getData;
			my @targetPath      = $cluster->getElementsByTagName('targetPath');
			my $targetPath_name = $targetPath[0]->getFirstChild->getData;
			my $clusterName     = $cluster->findvalue('@name');
			configureClusterxmls( $dirPath, $copyDir, $clusterName,
				$domain_name, $common_name, $offset_name, $readOnly_name,
				$targetPath_name );
			my $replacedoc  = XML::LibXML::Document->new();
			my $replaceroot = XML::LibXML::Element->new('dbConfig');
			$replaceroot->setAttribute( 'name', $common_name );
			$replacedoc->setDocumentElement($replaceroot);
			foreach my $clusterdb ( $cluster->findnodes('Database') ) {
				my $node = XML::LibXML::;
				$node = $clusterdb;
				my @childnodes = $node->childNodes;
				foreach my $db (@childnodes) {
					$node = $db;
					my $name = $node->nodeName;
					if (   $name ne '#text'
						&& $name ne 'currentDBConfig'
						&& $name ne 'dbConfigname' )
					{
						my $link = XML::LibXML::Element->new($name);
						my $text =
						  XML::LibXML::Text->new(
							$clusterdb->findvalue($name) );
						$link->appendChild($text);
						$replaceroot->appendChild($link);
					}
				}
			}
			replaceMaindb( $dirPath, $clusterName, $replaceroot );
		}
	}
}

sub replaceMaindb {
	my ( $dirPath, $dirName, $newNode ) = @_;
	my $file        = "$dirPath/SNAPSHOT/$dirName/repository/conf/registry.xml";
	my $parser      = XML::LibXML->new();
	my $tree        = $parser->parse_file($file);
	my $root        = $tree->getDocumentElement;
	my @name_node   = $root->getElementsByTagName('currentDBConfig');
	my $common_name = $name_node[0]->getFirstChild->getData;
	my @nodes       = $root->findnodes('dbConfig');
	my $nodeee      = XML::LibXML::;
	$nodeee = $nodes[0];
	$root->replaceChild( $newNode, $nodeee );
	my $doc    = XML::LibXML::Document->new();
	my @dbNode = $doc->getElementsByTagName('dbConfig');

	foreach my $dbSlot (@dbNode) {
		my @namenode = $dbSlot->getElementsByTagName('url');
	}
	File::Slurp::write_file( $file, $root->toString(1) );
}

sub configureClusterxmls {
	my ( $dirPath, $copyDir, $clusterName, $domain_name, $common_name,
		$offset_name, $readOnly_name, $targetPath_name )
	  = @_;
	enableCluster( "$dirPath/SNAPSHOT/$copyDir/repository/conf/axis2.xml",
		'true' );
	ReplaceElement( "$dirPath/SNAPSHOT/$copyDir/repository/conf/axis2.xml",
		'parameter[@name="domain"]', $domain_name );
	createDir( $dirPath, $clusterName, $copyDir );
	enableCluster( "$dirPath/SNAPSHOT/$clusterName/repository/conf/axis2.xml",
		'true' );
	ReplaceElement( "$dirPath/SNAPSHOT/$clusterName/repository/conf/axis2.xml",
		'parameter[@name="domain"]', $domain_name );
	ReplaceElement(
		"$dirPath/SNAPSHOT/$clusterName/repository/conf/registry.xml",
		'currentDBConfig', $common_name );
	ReplaceElement(
		"$dirPath/SNAPSHOT/$clusterName/repository/conf/registry.xml",
		'readOnly', $readOnly_name );
	mountConfig::setoffset(
		"$dirPath/SNAPSHOT/$clusterName/repository/conf/carbon.xml",
		$offset_name );
	editConfigReg(
		"$dirPath/SNAPSHOT/$clusterName/repository/conf/registry.xml",
		$targetPath_name );
}

sub createDir {
	my ( $dirPath, $dirName, $copyDir ) = @_;
	my $result = system("mkdir $dirPath/SNAPSHOT/$dirName");
	$result = system(
		"cp -r $dirPath/SNAPSHOT/$copyDir/* $dirPath/SNAPSHOT/$dirName
 	"
	);
}
1;

sub setConfigPathName {
	my ( $file, $value ) = @_;
	my $path = "$file";
	my $xp = XML::XPath->new( filename => $path );
	$xp->setNodeText( q{//mount[@path="/_system/governance"]/targetPath},
		$value );
	File::Slurp::write_file( $path, $xp->findnodes_as_string('/') );
}

sub setoffset {
	my ( $file, $value ) = @_;
	my $path = "$file";
	my $xp = XML::XPath->new( filename => $path );
	$xp->setNodeText( q{//Ports/Offset}, $value );
	File::Slurp::write_file( $path, $xp->findnodes_as_string('/') );
}
1;

sub enableCluster {
	my ( $file, $value ) = @_;
	my $path = "$file";
	my $com  = "commonConfig.xml";
	my $xp   = XML::XPath->new( filename => $path );
	$xp->setNodeText( q{//clustering/@enable}, $value );
	File::Slurp::write_file( $path, $xp->findnodes_as_string('/') );
}
1;
my $value;

sub ReplaceElement {
	my ( $main_file, $searchit, $valuexml ) = @_;
	$value = $valuexml;
	my $search = $searchit;
	my $orig   = new XML::Twig(
		TwigHandlers => { $search => \&search, },
		PrettyPrint  => 'indented',
	);
	$orig->parsefile($main_file);
	File::Slurp::write_file( $main_file, $orig->toString(1) );
	return 0;
}
1;

sub search {
	my ( $orig, $search ) = @_;
	my $search_tag = $search->tag;
	$search->set_text($value);
	return 0;
}
1;

sub getNodes {
	my ( $dirPath, $configFile, $ele ) = @_;
	my $parser      = XML::LibXML->new();
	my $tree        = $parser->parse_file("$dirPath$configFile");
	my $root        = $tree->getDocumentElement;
	my @name_node   = $root->getElementsByTagName($ele);
	my $common_name = $name_node[0]->getFirstChild->getData;
	return $common_name;
}

sub editConfigReg {
	my ( $regFile, $pathValue ) = @_;
	my $doc    = XML::LibXML::Document->new();
	my $parser = XML::LibXML->new();
	my $tree   = $parser->parse_file($regFile);
	my $root   = $tree->getDocumentElement;
	$doc->setDocumentElement($root);
	foreach my $product ( $root->findnodes('mount') ) {
		my @currentnode = $product->getElementsByTagName('targetPath');
		my $nodename    = $currentnode[0]->getFirstChild->data;
		my $mountName   = $product->findvalue('@path');
		if ( $mountName eq '/_system/config' ) {
			print $mountName;
			my @nodes      = $product->findnodes('targetPath');
			my $targetPath = $doc->createElement('targetPath');
			my $subvalue   = XML::LibXML::Text->new($pathValue);
			$targetPath->appendChild($subvalue);
			$product->replaceChild( $targetPath, $nodes[0] );
		}
	}
	File::Slurp::write_file( $regFile, $root->toString(1) );
}
1;
