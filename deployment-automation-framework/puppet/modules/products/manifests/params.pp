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

class products::params {
	
	$owner  = 'root'
  	$group  = 'root'
  	$target = '/opt'

	$deployment_code = 'as'
	$service_code   = 'as'
	$version = '5.2.1'
	$offset = 0
  	$carbon_version = "wso2${service_code}-${version}"
  	$carbon_home    = "${target}/${carbon_version}"

	$elb_dep_code = 'elb'
	$elb_service_code = 'elb'
	$elb_version = '2.1.0'
	$elb_carbon_version = "wso2${elb_service_code}-${elb_version}"
	$elb_carbon_home = "${target}/${elb_carbon_version}"

	$hazelcast_port = 4100
        $elb_common_port = 4500
        $elb_mgt_cluster_port = 4500
        $elb_wkr_cluster_port = 4600

	$as_cluster_dep_pattern = 2

	# Available JDK versions
	# jdk1.6.0_24
	# jdk1.7.0_51
	$jdk_version = 'jdk1.6.0_24'
	$jdk_home = "${target}/${jdk_version}"	

	$jdk_file	= "${jdk_home}.tar.gz"
	$pack_file	= "${carbon_home}.zip"
	$elb_pack_file  = "${elb_carbon_home}.zip"

	$servers = {
                appserver-mgr2 => { axis2   => {subDomain => 'mgt', members => [], elbs => ['10.0.2.140']},
                                    carbon  => {subDomain => 'mgt',},
				    serverOptions => '-Dsetup', 
				    server_home => $carbon_home, },
                appserver-wkr2 => { axis2   => {subDomain => 'worker', members => ['10.0.2.141'], elbs => ['10.0.2.140']},
                                    carbon  => {subDomain => 'worker',},
				    serverOptions => '-DworkerNode=true',
				    server_home => $carbon_home, },
		elb	       => { axis2   => { domain => 'wso2.carbon.lb.domain',},
				    carbon  => { domain => 'wso2.carbon.lb.domain',},
				    serverOptions => '',
				    server_home => $elb_carbon_home, },
        }

        $serversDefaults = {
                clustering => 'true',
        }	
}
