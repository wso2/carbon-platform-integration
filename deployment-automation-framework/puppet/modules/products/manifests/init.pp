class products inherits products::params {
	
        $serverName = $::hostname
	$setServerOption = $servers[$serverName][serverOptions]

        package { 'unzip':
                  ensure => present,
        }

}
