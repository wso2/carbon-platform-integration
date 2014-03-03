#!/usr/bin/perl
#
# search for a file in all subdirectories
#

use Archive::Zip;

package fileResolver;

sub nameResolver {

	#print ;
	my $data1    = $_[0];
	my $filename = $data1;

	# look in current directory
	chop($dir);
	my $fileName;
	local ($dir);
	local (@lines);
	local ($line);
	local ($file);
	local ($subdir);
	$dir = $_[1];

	# check for permission
	if ( -x $dir ) {

		# search this directory
		@lines = `cd $dir; ls -l | grep $filename`;
		foreach $line (@lines) {
			$line =~ /\s+(\S+)$/;
			$file     = $1;
			$fileName = $file;
		}

		# search any sub directories
		@lines = `cd $dir; ls -l`;

	}
	return $fileName;
}
1;

sub CleanPath {
	my ($filepath) = @_;
	my $result;
	opendir( DIR, $filepath ) or die $!;
	readdir DIR;    # reads .
	readdir DIR;    # reads ..
	if ( readdir DIR ) {
		print "Removing existing files....................\n";
		my $deletedir = "$filepath/*";
		print $deletedir;
		$result = system("rm -r $deletedir");
		print "Result: $result\n";
	}
	else {
		print "Destination is empty proceeding ...............\n";
		$result = 0;
	}
	close DIR;
	return $result;
}

sub extractApp {
	my ( $filepath, $destpath ) = @_;
	print "---------------------------------\n";
	print $filepath;
	print "\n---------------------------------\n";
	system "unzip -qu $filepath -d $destpath";
}

sub copyArtifacts {
	my ( $dirname, $copylocation, $sqlconnector ) = @_;
	use File::Copy;
	copy( "$dirname/resource/$sqlconnector",
"$dirname/SNAPSHOT/$copylocation/repository/components/lib/$sqlconnector"
	);
}

sub createArchive {
	my ( $inDirectory, $outFile ) = @_;

	print("Create a Zip file");
	my $zip = Archive::Zip->new();

	my $dir_member = $zip->addTree( $inDirectory . "/" );

	# Save the Zip file
	unless ( $zip->writeToFileNamed($outFile) == AZ_OK ) {
		die 'Could not zip file';
	}
}
