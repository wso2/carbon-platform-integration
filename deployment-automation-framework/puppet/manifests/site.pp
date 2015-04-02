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
