#!/usr/bin/perl 
use strict;
use XML::DOM;
use XML::Simple;
use XML::XPath;
use XML::LibXML;
use File::Slurp ();
my $parser2 = new XML::DOM::Parser;
my $value;

package mountConfig;

sub mount
{
	my (
		 $parentele, $childele, $attribname1, $attrib1, $attribname2,
		 $attrib2,   $path,     $yelFile,     $product
	  )
	  = @_;
	my $doc2   = $parser2->parsefile($path);
	my $xp     = XML::XPath->new( filename => $path );
	my $parser = XML::LibXML->new();
	my $docw   = $parser->parse_file($yelFile);
	my $root   = $docw->getDocumentElement;
	my $doc    = XML::LibXML::Document->new();
	my $root   = $docw->getDocumentElement;
	$doc->setDocumentElement($root);
	my $dbElement = $doc->createElement($childele);
	my $dbConfig;

	foreach my $nodeList ( $xp->find("//$parentele/$childele")->get_nodelist )
	{
		$dbConfig = $nodeList->find($attribname1);
	}
	$dbElement->setAttribute( $attrib1, $dbConfig );
	if ( $childele eq 'mount' )
	{
		foreach
		  my $nodeList ( $xp->find("//$parentele/$childele")->get_nodelist )
		{
			$dbConfig = $nodeList->find($attribname2);
		}
		$dbElement->setAttribute( $attrib2, $dbConfig );
	}
	my $iswrappers = $doc2->getElementsByTagName($parentele);
	if ($iswrappers)
	{
		for ( my $i = 0 ; $i < $iswrappers->getLength ; $i++ )
		{
			my $wrapper = $iswrappers->item($i);
			my $parent  = $wrapper->getParentNode;
			foreach my $child ( $wrapper->getChildNodes )
			{
				my $nodeText = $child->getNodeName;
				if ( $parentele eq 'MountInstance' )
				{
					if ( $nodeText ne '#text'
						&& $nodeText ne 'remoteInstance')
						{
							 my $val =
							 readXmlConfig( $nodeText, $path, $parentele );
							 my $subElement = $doc->createElement($nodeText);
								 my $subvalue   = XML::LibXML::Text->new($val);
							 if ( $nodeText eq 'targetPath' )
						   {
							   $subvalue =
								 XML::LibXML::Text->new("_system/$product");
						   }
						   $subElement->appendChild($subvalue);
							 $dbElement->appendChild($subElement);
						};
					}
					else{ 
						if (    $nodeText ne '#text'
							 && $nodeText ne 'dbConfig'
							 && $nodeText ne 'remoteInstance'
							 && $nodeText ne 'mount' )
						{
							my $val =
							  readXmlConfig( $nodeText, $path, $parentele );
							my $subElement = $doc->createElement($nodeText);
							my $subvalue   = XML::LibXML::Text->new($val);
							if ($parentele eq 'ConfigMount' && $nodeText eq 'targetPath' )
							{
								$subvalue =
								  XML::LibXML::Text->new("/_system/$product");
							}
							$subElement->appendChild($subvalue);
							$dbElement->appendChild($subElement);
						}
					}
				}
			}
		}
		$root->appendChild($dbElement);
		File::Slurp::write_file( $yelFile, $root->toString(1) );
	}
	1;

	sub readXmlConfig
	{
		my $xml = new XML::Simple;
		my ( $param, $file, $datatag ) = @_;
		my $data    = $xml->XMLin($file);
		my $dataSet = $data->{$datatag}->{$param};
		return $dataSet;
	}

	sub setoffset
	{
		my ( $file, $value ) = @_;
		my $path = "$file";
		print "------------------------------------------";
		my $xp = XML::XPath->new( filename => $path );
		$xp->setNodeText( q{//Ports/Offset}, $value );
		File::Slurp::write_file( $path, $xp->findnodes_as_string('/') );
	}
