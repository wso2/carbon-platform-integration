# Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
#
# WSO2 Inc. licenses this file to you under the Apache License,
# Version 2.0 (the "License"); you may not use this file except
# in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

class products::appserver inherits products::params {
	
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
                        	content => template('products/appserver/axis2.xml.erb'),
                	}
                	->
                	file {  "${server_home}/repository/conf/carbon.xml":
                        	ensure  => file,
                        	content => template('products/appserver/carbon.xml.erb'),
                	}
                	->
                	file {  "${server_home}/repository/conf/tomcat/catalina-server.xml":
                        	ensure  => file,
                        	content => template('products/appserver/catalina-server.xml.erb'),
                	}
       	 	}
	}

        package { 'unzip':
                  ensure => present,
        }

        exec {  "Stop_process_and_remove_CARBON_HOME":
                path        => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin',
                command => "kill -9 `cat ${carbon_home}/wso2carbon.pid` ; rm -rf ${carbon_home}";
        }
        ->
        exec {  "remove_java_dirs":
                path        => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin',
                command => "rm -rf ${target}/${jdk_version} ; rm -rf /opt/java";
        }
        ->
        file { "${pack_file}":
                replace => "no",
                ensure => present,
                source => "puppet:///modules/products/${carbon_version}.zip",
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

                "extracting appserver":
                user => 'root',
                path        => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/java/bin/',
                command => "unzip ${carbon_home}.zip -d ${target}/",
                require   => Exec["installing_java"];
        }

	create_resources(appserver::fill_templates, $servers, $serversDefaults)

	Exec<| title == "extracting appserver" |> -> Appserver::Fill_Templates<| |> -> Exec<| title == "starting_appserver" |>

	exec {  "starting_appserver":
                user    => 'root',
                environment => "JAVA_HOME=/opt/java",
                path        => '/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/java/bin/',
                unless  => "test -f ${carbon_home}/wso2carbon.lck",
                command => "touch ${carbon_home}/wso2carbon.lck; ${carbon_home}/bin/wso2server.sh  $setServerOption > /dev/null 2>&1 &",
                creates => "${carbon_home}/repository/wso2carbon.log",
		require => Exec["extracting appserver"];
        }
}
