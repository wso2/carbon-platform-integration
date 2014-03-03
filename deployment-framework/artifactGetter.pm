#!/usr/bin/perl
use strict;

use warnings;
use File::Basename;
use Archive::Extract;
use XML::Writer;
use XML::Simple;
use XML::Writer;
use XML::Simple;
use XML::Twig;
use File::Path;
use XML::DOM;

use WWW::Mechanize;
require xmlResolver;
require ldapConfig;
require fileResolver;
require envResolver;
my $dirname;
$dirname = dirname(__FILE__);

package artifactGetter;

sub downloadProduct {
	my ($prodCode) = @_;
	my $path       = "$dirname/resource/";
	my $zipFile    =  envResolver::resolveProduceName($prodCode);
		print ($zipFile);
	my $filen = "$path$zipFile*.zip";
	if ( -e $filen ) {
		print ".........File Exists! Removing existing files.........";
		system "rm $path$zipFile";
	}
	print(
"\n....................Downloading $prodCode  strated......................\n"
	);
	my $url = getUrlList($prodCode);
	my $m = WWW::Mechanize->new( autocheck => 1 );
	$m->get($url);
	$m->form_number(2);
	my $response = $m->res();
	my $filename = $response->filename;

	if ( !open( FOUT, ">$path$filename" ) ) {
		die("Could not create file: $!");
	}
	print( FOUT $m->response->content() );
	close(FOUT);
	print(
"\n.............................Download Compleated ............................\n"
	);
}

sub getUrlList {
	my $prodCode = $_[0];
	my $url;
	my $configPath = "$dirname/config/";
	my $www        = xmlResolver::readxml( "$dirname/config/commonConfig.xml",
		'DownloadProduct' );
	my $download =
	  ldapConfig::getNodes( $configPath, 'commonConfig.xml', 'DownloadProduct',
		'Download' );
	if ( $download eq 'true' ) {
		$url =
		  ldapConfig::getNodes( $configPath, 'commonConfig.xml',
			'DownloadProduct', $prodCode );
	}
	print("\n Read From Download Location : $url \n");
	return $url;
}
+1
