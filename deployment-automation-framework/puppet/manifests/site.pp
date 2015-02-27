node 'default' {
	file {'server-node-mismatch':
      		path    => '/tmp/server-node-mismatch',
      		ensure  => present,
      		mode    => 0640,
      		content => "Server node definition not found in puppet master.",
    	}
}

node /^appserver-/ {
	# params.pp -> appserver.pp
	class {'products::appserver':}
}

node /^elb/ {
	class {'products::elb':}
}

node 'file-test' {
	file {  "/tmp/dbscripts/mysql/hellopuppet.txt":
                ensure  => file,
                content => 'master says hello to puppet agent',
        }
}

node 'directory-test' {

	#this is a test
	$say_hello_to = 'guys and gals'
        $myname = 'welcome file.xml'
        $servers           = {'10.0.1.196' => '4100', '10.0.1.198'  => '4100', '10.0.1.200'  => '4000', }

        # create a directory      
	$path = '/tmp/dbscripts/mysql'
  	exec { "installing_java":
		path        => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin',
    		command => "mkdir -p ${path}",
  	}
	
	file {  "/tmp/dbscripts/mysql/$myname":
                ensure  => file,
                content => template('products/polite-file.erb'),
        }

}
