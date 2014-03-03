#!/usr/bin/perl 
use Getopt::Std;
use LWP::UserAgent;
use File::Basename;
use XML::Parser;
use Archive::Extract;
use XML::Simple;
use Data::Dumper;
use XML::Writer;
use XML::Simple;
use XML::Twig;
use File::Path;
use XML::DOM;
use File::Slurp ();
use XML::XPath;
use XML::XPath::XMLParser;
my $product_zip;
my $sqlconnector;
my $config_file;
my $base_dir;
my $snapshot_home;
my $dirname;
my $zipname;
my $registryXml = 'registry.xml';
my $useMgtXml   = 'user-mgt.xml';
$sqlconnector = 'mysql-connector-java-5.1.12-bin.jar';
$dirname      = dirname(__FILE__);
my $parser = new XML::DOM::Parser;
my $xml    = new XML::Simple;

package xmlResolver;
my $value;

sub setTargetXml
{
	my ( $main_file, $search, $valuexml ) = @_;
	$value = $valuexml;

	# Process the main file
	my $orig = new XML::Twig(    TwigHandlers => { $search => \&search, },
							  PrettyPrint  => 'indented', );
	$orig->parsefile($main_file);
	File::Slurp::write_file( $main_file, $orig->toString(1) );
	return 0;
}

sub SetUseMgtxml
{
	my ( $main_file, $searchit, $valuexml ) = @_;
	$value = $valuexml;
	my $search = envResolver::getXmlAttribute($searchit);
	my $orig = new XML::Twig(    TwigHandlers => { $search => \&search, },
							  PrettyPrint  => 'indented', );
	$orig->parsefile($main_file);
	File::Slurp::write_file( $main_file, $orig->toString(1) );
	return 0;
}

sub search
{
	my ( $orig, $search ) = @_;
	my $search_tag = $search->tag;
	$search->set_text($value);
	return 0;
}

sub readElementlist
{
	my ( $Xmlpath, $filename ) = @_;
	my $doc        = $parser->parsefile("$dirname/config/$filename");
	my $iswrappers = $doc->getElementsByTagName("Database");
	if ($iswrappers)
	{
		for ( my $i = 0 ; $i < $iswrappers->getLength ; $i++ )
		{
			my $wrapper = $iswrappers->item($i);
			my $parent  = $wrapper->getParentNode;
			foreach my $child ( $wrapper->getChildNodes )
			{
				my $nodeText = $child->getNodeName;
				if ( $nodeText ne '#text' && $nodeText ne 'dbConfigname')
				{
					readXmlConfig( $nodeText, $filename );
					if (
						 (
						   elemntExists( "$Xmlpath$registryXml", $nodeText ) eq
						   '1'
						 )
					  )
					{
						setTargetXml( "$Xmlpath$registryXml", $nodeText,
									  readXmlConfig( $nodeText, $filename ) );
					} else
					{
						insertElement( "$Xmlpath$registryXml", $nodeText,
									   readXmlConfig( $nodeText, $filename ) );
					}
					SetUseMgtxml( "$Xmlpath$useMgtXml", $nodeText,
								  readXmlConfig( $nodeText, $filename ) );
					if ( $nodeText eq 'currentDBConfig' )
					{
						replaceElement( "$Xmlpath$registryXml",
										readXmlConfig( $nodeText, $filename ) );
					}
				}
			}
		}
	}
	return 0;
}
1;

sub readXmlConfig
{
	my ( $param, $file ) = @_;
	my $data    = $xml->XMLin("$dirname/config/$file");
	my $dataSet = $data->{'Database'}->{$param};
	return $dataSet;
}
1;

sub replaceElement
{
	my ( $file, $value ) = @_;
	my $path = "$file";
	my $xp = XML::XPath->new( filename => $path );
	$xp->setNodeText( q{//dbConfig/@name}, $value );
	File::Slurp::write_file( $path, $xp->findnodes_as_string('/') );
}

sub elemntExists
{
	my ( $file, $value ) = @_;
	my $parser     = new XML::DOM::Parser;
	my $doc        = $parser->parsefile($file);
	my $iswrappers = $doc->getElementsByTagName($value);
	if ( $iswrappers->getLength gt '0' ) { return 1 }
	else { return 0 }
}

sub insertElement
{
	my ( $main_file, $tagName, $valuexml ) = @_;
	my $doc         = XML::LibXML::Document->new($main_file);
	my $file        = $main_file;
	my $parser      = XML::LibXML->new();
	my $tree        = $parser->parse_file($file);
	my $root        = $tree->getDocumentElement;
	my @name_node   = $root->getElementsByTagName('currentDBConfig');
	my $common_name = $name_node[0]->getFirstChild->getData;
	my @nodes       = $root->findnodes('dbConfig');
	my $nodeee      = XML::LibXML::;
	my $nodedb      = XML::LibXML::;
	$nodedb = $nodes[0];
	$nodeee = $nodes[0];
	my $subElement = $doc->createElement($tagName);
	my $subvalue   = XML::LibXML::Text->new($valuexml);
	$subElement->appendChild($subvalue);
	$nodedb->appendChild($subElement);
	File::Slurp::write_file( $file, $root->toString(1) );
}

sub readxml
{
	my ( $main_file, $tagName) = @_;
	my $doc         = XML::LibXML::Document->new($main_file);
	my $file        = $main_file;
	my $parser      = XML::LibXML->new();
	my $tree        = $parser->parse_file($file);
	my $root        = $tree->getDocumentElement;
	my @name_node   = $root->getElementsByTagName($tagName);
	my $common_name = $name_node[0]->getFirstChild->getData;
	return $common_name;

}