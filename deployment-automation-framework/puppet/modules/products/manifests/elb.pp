class products::elb inherits products::params {
	
        $serverName = $::hostname
	$setServerOption = $servers[$serverName][serverOptions]

	define fill_templates($axis2, $carbon, $clustering, $serverOptions, $server_home) {
        	$ipAdd = $::ipaddress
        	$hostName = $::hostname
		notify{"hostName -> $hostName, arrayName -> ${name}, serverOptions -> $serverOptions":}
        	if $hostName == "${name}" {
                	notify {"host name match found for $hostName for $ipAdd":}
                	file {  "${server_home}/repository/conf/axis2/axis2.xml":
                        	ensure  => file,
                        	content => template('products/elb/axis2.xml.erb'),
                	}
                	->
                	file {  "${server_home}/repository/conf/loadbalancer.conf":
                        	ensure  => file,
                        	content => template('products/elb/loadbalancer.conf.erb'),
                	}
       	 	}
	}

        package { 'unzip':
                  ensure => present,
        }

        exec {  "Stop_process_and_remove_CARBON_HOME":
                path        => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin',
                command => "kill -9 `cat ${elb_carbon_home}/wso2carbon.pid` ; rm -rf ${elb_carbon_home}";
        }
        ->
        exec {  "remove_java_dirs":
                path        => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin',
                command => "rm -rf ${target}/${jdk_version} ; rm -rf /opt/java";
        }
        ->
        file { "${elb_pack_file}":
                replace => "no",
                ensure => present,
                source => "puppet:///modules/products/${elb_carbon_version}.zip",
        }
	->
        file { "${jdk_file}":
                replace => "no",
                ensure => present,
                source => "puppet:///modules/packs/${jdk_version}.tar.gz",
        }
	->
        exec {  "installing_java":
                user => 'root',
                path        => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin',
                command => "tar -xvzf ${target}/${jdk_version}.tar.gz  -C ${target}/; ln -s ${target}/${jdk_version} /opt/java",
                creates => "/tmp/installjava.log",
                require   => File["${jdk_file}"];

                "extracting elb":
                user => 'root',
                path        => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/java/bin/',
                command => "unzip ${elb_carbon_home}.zip -d ${target}/",
                require   => Exec["installing_java"];
        }

	create_resources(elb::fill_templates, $servers, $serversDefaults)

	Exec<| title == "extracting elb" |> -> Elb::Fill_Templates<| |> -> Exec<| title == "elb_appserver" |>

	exec {  "starting_elb":
                user    => 'root',
                environment => "JAVA_HOME=/opt/java",
                path        => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/java/bin/',
                unless  => "test -f ${elb_carbon_home}/wso2carbon.lck",
                command => "touch ${elb_carbon_home}/wso2carbon.lck; ${elb_carbon_home}/bin/wso2server.sh  $setServerOption > /dev/null 2>&1 &",
                creates => "${elb_carbon_home}/repository/wso2carbon.log",
		require => Exec["extracting elb"];
        }
}
