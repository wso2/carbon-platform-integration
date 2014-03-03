#! /usr/bin/perl

use Net::SCP qw/ scp /;
use Net::SCP::Expect;
use Net::SSH::Expect;
use Net::SSH::Perl;
use XML::LibXML;
use File::Slurp ();
use XML::XPath;
require fileResolver;
require envResolver;

package sshTaskManager;

sub setRemoteEnvironment {
	my ( $dirPath, $configFile, $productName ) = @_;
	my $file = "$dirPath$configFile";
	my $url;
	my $type;
	my $sourceDir;
	my $userName;
	my $passWord;
	my $desination;
	my $localDir;

	my $parser = XML::LibXML->new();
	my $tree   = $parser->parse_file($file);
	my $root   = $tree->getDocumentElement;

	foreach my $product ( $root->findnodes('Upload') ) {
		foreach my $cluster ( $product->findnodes('Node') ) {
			$destinationDir = $cluster->getFirstChild->getData;
			$url            = $cluster->findvalue('@url');
			$type           = $cluster->findvalue('@cluster');
			$sourceDir      = $cluster->findvalue('@source');
			$userName       = $cluster->findvalue('@user');
			$passWord       = $cluster->findvalue('@password');
			$desination     = $cluster->findvalue('@destination');
			print($dirPath);

			if ( $type eq 'true' ) {
				$localDir = $sourceDir;
			}
			if ( $type eq 'false' ) {
				my @values = split(
					".zip",
					fileResolver::nameResolver(
						envResolver::resolveProduceName($sourceDir),
						"$dirPath/resource/"
					)
				);
				$localDir = $values[0];
			}
			fileResolver::createArchive( "$dirPath/SNAPSHOT/$localDir/",
				"temp/$destinationDir.zip" );
			scp( "temp/$destinationDir.zip",
				$desination, $url, $userName, $passWord );
			sshunzipProduct( $url, $userName, $passWord, $desination,
				$destinationDir );
		}
	}
	die();
}

sub scp {
	my ( $file, $remoteFilename, $host, $username, $password ) = @_;

	my $scpe = Net::SCP::Expect->new(
		host     => "$host",
		user     => "$username",
		password => "$password",
		auto_yes => 1
	);
	print(
"\n**......................Starting deploying $file to $username@$host.........................**\n"
	);
	$scpe->scp( $file, $remoteFilename );
	print(
"\n**......................Compleated deploying to $username@$host.........................**\n"
	);
	my $localFile = $file;
}

sub sshunzipProduct {
	my ( $host, $userName, $passWord, $filePath, $file ) = @_;
	my $ssh = Net::SSH::Expect->new(
		host     => "$host",
		password => "$userName",
		user     => "$passWord",
		raw_pty  => 1
	);

	my $login_output = $ssh->login();

	print('..................waiting...............');
	print("\n$filePath/$file.zip\n");
	$ssh->exec("unzip -qu $filePath/$file.zip -d $filePath");
	$ssh->waitfor( '>\s*\z', 1 );
	$ssh->close();
	print('\n................$file.zip extracted to $filePath...........\n');
}

sub sshStartServer {
	my ( $host, $userName, $passWord, $filePath, $file ) = @_;

	# 1) construct the object
	my $ssh = Net::SSH::Expect->new(
		host     => "$host",
		password => "$userName",
		user     => "$passWord",
		raw_pty  => 1
	);

	#$ssh->close();
	print("\nwaiting...............\n");
	my $login_output = $ssh->login();

	print('..................waiting...............');
	print("\n...............$filePath/bin/wso2server.sh....................\n");
	my $log = $ssh->exec("nohup sh $filePath/bin/wso2server.sh -Dsetup &");
	$ssh->waitfor( '>\s*\z', 1 );
	while ( defined( $line = $ssh->read_line() ) ) {
		print $line . "\n";
	}
	$ssh->close();
	print('\n..................server Started...........\n');
}

sub upProductList {
	my ( $dirPath, $configFile ) = @_;
	my $file = "$dirPath$configFile";
	my $url;
	my $type;
	my $sourceDir;
	my $userName;
	my $passWord;
	my $desination;
	my $localDir;
	my $currentProduct;
	my $parser = XML::LibXML->new();
	my $tree   = $parser->parse_file($file);
	my $root   = $tree->getDocumentElement;

	foreach my $pro (
		envResolver::readProductList(
			xmlResolver::readxml( "$file", 'ProductList' )
		)
	  )
	{
		if ( $pro eq 'GREG' ) {
			foreach my $product ( $root->findnodes('Upload') ) {
				foreach my $cluster ( $product->findnodes('Node') ) {
					$destinationDir = $cluster->getFirstChild->getData;
					$url            = $cluster->findvalue('@url');
					$type           = $cluster->findvalue('@cluster');
					$sourceDir      = $cluster->findvalue('@source');
					$userName       = $cluster->findvalue('@user');
					$passWord       = $cluster->findvalue('@password');
					$desination     = $cluster->findvalue('@destination');
					if ( $sourceDir eq $pro ) {
					sshStartServer( $url, $userName, $passWord, $desination,
						$destinationDir );
					}
				}
			}
		}
	}
	foreach my $pro (
		envResolver::readProductList(
			xmlResolver::readxml( "$file", 'ProductList' )
		)
	  )
	{
		if ( $pro ne 'GREG' ) {
			foreach my $product ( $root->findnodes('Upload') ) {
				foreach my $cluster ( $product->findnodes('Node') ) {
					$destinationDir = $cluster->getFirstChild->getData;
					$url            = $cluster->findvalue('@url');
					$type           = $cluster->findvalue('@cluster');
					$sourceDir      = $cluster->findvalue('@source');
					$userName       = $cluster->findvalue('@user');
					$passWord       = $cluster->findvalue('@password');
					$desination     = $cluster->findvalue('@destination');
					if($type eq 'true')
					{
						sshStartServer( $url, $userName, $passWord, $desination,
							$destinationDir );
					}
					if ( $sourceDir eq $pro ) {
						sshStartServer( $url, $userName, $passWord, $desination,
							$destinationDir );
					}
				}
			}
		}
	}
}
+1

